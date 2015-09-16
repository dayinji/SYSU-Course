package com.badprinter.sysu_course.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.badprinter.sysu_course.Common.GlobalData;
import com.badprinter.sysu_course.Constant.Constants;
import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.activity.Main;
import com.badprinter.sysu_course.db.DBManager;
import com.badprinter.sysu_course.model.Course;
import com.badprinter.sysu_course.util.DateTimeUtil;
import com.badprinter.sysu_course.http.ListenCourse;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by root on 15-9-13.
 */
public class CourseService extends Service {
    private final String TAG = "CourseService";
    private Timer timer;
    //private Handler handler;
    // Every 60s
    private int duration = 15 * 1000;
    private ListenCourse listenCourse;
    private DBManager dbMgr;
    private NotificationManager nm;
    private SharedPreferences sharedPref;
    private NetworkConnectChangedReceiver networkConnectChangedReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        dbMgr = new DBManager();

        // Listen Wifi State Change
        networkConnectChangedReceiver = new NetworkConnectChangedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectChangedReceiver, filter);

        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        timer = new Timer();
        sharedPref = getSharedPreferences(Constants.Preferences.PREFERENCES_KEY, MODE_PRIVATE);
        if (sharedPref.getInt(Constants.Preferences.PREFERENCES_LISTEN, 1) == 1)
            startTimer();

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        switch (intent.getExtras().getString("msg")) {
            case Constants.ListenerMsg.INIT_SERVICE:
                break;
            case Constants.ListenerMsg.UPDATE_LISTENED_COURSES:
                break;
            case Constants.ListenerMsg.OPEN_LISTEN:
                GlobalData.logInfo.add(DateTimeUtil.getCurrentTimeString() + " : " + "开启监听");
                boardUpdateLog();
                startTimer();
                break;
            case Constants.ListenerMsg.CLOSE_LISTEN:
                if (timer != null)
                    timer.cancel();
                GlobalData.logInfo.add(DateTimeUtil.getCurrentTimeString() + " : " + "已关闭监听");
                boardUpdateLog();
                break;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (timer != null)
            timer.cancel();
        nm.cancel(950520);
        super.onDestroy();
    }

    private void boardUpdateList(String cata, String name, String bid) {
        Intent sendIntent = new Intent(Constants.BoardAction.SUCCESSFUL_SELECTION);
        sendIntent.putExtra("cata", cata);
        sendIntent.putExtra("bid", bid);
        sendIntent.putExtra("name", name);
        sendBroadcast(sendIntent);
    }

    private void boardUpdateLog() {
        Intent sendIntent = new Intent(Constants.BoardAction.UPDATE_LOG);
        sendBroadcast(sendIntent);
    }
    private void boardUpdateListenToggle(boolean isWifi) {
        if (!isWifi) {
            GlobalData.logInfo.clear();
            GlobalData.logInfo.add(DateTimeUtil.getCurrentTimeString() + " : " + "已关闭监听");
            boardUpdateLog();
        } else {
            GlobalData.logInfo.clear();
            GlobalData.logInfo.add(DateTimeUtil.getCurrentTimeString() + " : " + "开启监听");
            boardUpdateLog();
        }
        Intent sendIntent = new Intent(Constants.BoardAction.UPDATE_LISTEN_TOGGLE);
        sendIntent.putExtra("isWifi", isWifi);
        sendBroadcast(sendIntent);
    }

    private void showLyricNotify(String courseName, String cata) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        Intent activityIntent = new Intent(this, Main.class);
        // Make Sure That Returing App Instead of New A Activity
        activityIntent.setAction(Intent.ACTION_MAIN);
        activityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent intent_activity = PendingIntent.getActivity(this, 0, activityIntent, 0);

        mBuilder.setContentTitle("监听成功")
                .setContentText(cata + ": " + courseName + "选课成功")
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(intent_activity);

        nm.notify(950520, mBuilder.build());
    }

    private void startTimer() {
        if (timer != null)
            timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (listenCourse != null && !listenCourse.isCancelled())
                        listenCourse.cancel(true);
                    // New A ListenCourse
                    listenCourse = new ListenCourse();
                    // Set CallBack
                    listenCourse.onListenFinished = new ListenCourse.OnListenFinished() {
                        @Override
                        public void onListenFinished(List<Integer> codeList,
                                                     List<String> bidList,
                                                     List<String> nameList,
                                                     List<String> cataList) {
                            Log.e(TAG, "监听完成");
                            if (GlobalData.logInfo != null)
                                GlobalData.logInfo.clear();
                            else
                                GlobalData.logInfo = new ArrayList<String>();
                            for (int i = 0; i < codeList.size(); i++) {
                                if (codeList.get(i) == 0) {
                                    showLyricNotify(nameList.get(i), cataList.get(i));
                                    Course c = new Course();
                                    c.setBid(bidList.get(i));
                                    dbMgr.deleteFromListened(c);
                                    boardUpdateList(cataList.get(i), nameList.get(i), bidList.get(i));
                                }
                                Log.e(TAG, nameList.get(i) + " : " + Constants.ERROR_MSG[codeList.get(i)]);
                                String name = nameList.get(i);
                                if (name.length() > 8)
                                    name = name.substring(0, 8) + "...";
                                String logMsg;
                                if (codeList.get(i) == Constants.ERROR_MSG.length-1) {
                                   logMsg = DateTimeUtil.getCurrentTimeString() + " : " +
                                            name + " : " + "请检查网络或重新登陆";
                                } else {
                                    logMsg = DateTimeUtil.getCurrentTimeString() + " : " +
                                            name + " : " + Constants.ERROR_MSG[codeList.get(i)];
                                }
                                if (logMsg.length() > 35)
                                    logMsg = logMsg.substring(0, 35) + "...";
                                GlobalData.logInfo.add(logMsg);
                            }
                            if (codeList.size() != 0)
                                GlobalData.logInfo.add(DateTimeUtil.getCurrentTimeString() + " : " + "正在监听...");
                            else
                                GlobalData.logInfo.add(DateTimeUtil.getCurrentTimeString() + " : " + "当前监听队列为空,建议关闭监听功能");
                            boardUpdateLog();
                        }
                    };
                    listenCourse.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, duration);
    }

    private class NetworkConnectChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                // 这个监听wifi的打开与关闭，与wifi的连接无关
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                Log.e("H3c", "wifiState" + wifiState);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        if (timer != null)
                            timer.cancel();
                        boardUpdateListenToggle(false);
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        if (timer != null)
                            timer.cancel();
                        boardUpdateListenToggle(false);
                        break;
                }
            }
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
                    Log.e("H3c", "isConnected" + isConnected);
                    if (isConnected) {
                        startTimer();
                        boardUpdateListenToggle(true);
                    } else {
                        if (timer != null)
                            timer.cancel();
                        boardUpdateListenToggle(false);
                    }
                }
            }
        }
    }
}
