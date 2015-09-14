package com.badprinter.sysu_course.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.badprinter.sysu_course.Constant.Constants;
import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.db.DBManager;
import com.badprinter.sysu_course.model.Course;
import com.badprinter.sysu_course.util.ListenCourse;

import java.sql.Time;
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
    private int duration = 30*1000;
    private ListenCourse listenCourse;
    private DBManager dbMgr;
    private NotificationManager nm;

    @Override
    public void onCreate() {
        super.onCreate();
        dbMgr = new DBManager();

        nm  = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

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
                            for (int i = 0 ; i < codeList.size() ; i++) {
                                if (codeList.get(i) == 0) {
                                    showLyricNotify(nameList.get(i), cataList.get(i));
                                    Course c = new Course();
                                    c.setBid(bidList.get(i));
                                    dbMgr.deleteFromListened(c);
                                    board(cataList.get(i), nameList.get(i), bidList.get(i));
                                }
                                Log.e(TAG, nameList.get(i) + " : " + Constants.ERROR_MSG[codeList.get(i)]);
                            }
                        }
                    };
                    listenCourse.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, duration);
    }
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags,  int startId) {

        /*switch (intent.getExtras().getString("controlMsg")) {
            case Constants.ListenerMsg.UPDATE_LISTENED_COURSES:
                break;
        }*/
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (timer != null)
            timer.cancel();
        nm.cancel(950520);
        super.onDestroy();
    }

    private void board(String cata, String name, String bid) {
        Intent sendIntent = new Intent(Constants.BoardAction.SUCCESSFUL_SELECTION);
        sendIntent.putExtra("cata", cata);
        sendIntent.putExtra("bid", bid);
        sendIntent.putExtra("name", name);
        sendBroadcast(sendIntent);
    }

    private void showLyricNotify(String courseName, String cata) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setContentTitle("监听成功")
                .setContentText(cata + ": " + courseName + "选课成功")
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher);
        nm.notify(950520, mBuilder.build());
    }
}
