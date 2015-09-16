package com.badprinter.sysu_course.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.badprinter.sysu_course.Constant.Constants;
import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.customview.LogView;
import com.badprinter.sysu_course.db.DBManager;
import com.badprinter.sysu_course.fragment.CourseList;
import com.badprinter.sysu_course.model.Course;
import com.badprinter.sysu_course.http.CourseInfoByBids;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class Listen extends SwipeBackActivity {
    private final String TAG = "Listen";

    private FrameLayout frameLayout;
    private CourseList courseListFragment;
    private CourseInfoByBids courseInfoByBids;
    private DBManager dbMgr;
    private SweetAlertDialog dialog;
    private LogView logView;
    private LogReceiver logReceiver;
    private ImageView logo;
    private ToggleButton listenToggle;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);

        dbMgr = new DBManager();

        findViewsById();

        sharedPref = getSharedPreferences(Constants.Preferences.PREFERENCES_KEY, MODE_PRIVATE);

        if (sharedPref.getInt(Constants.Preferences.PREFERENCES_LISTEN, 1) ==  0) {
            listenToggle.setChecked(false);
            listenToggle.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.round_bt_default));
        } else {
            listenToggle.setChecked(true);
            listenToggle.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.round_bt_warn));
        }

        logo.setImageDrawable(getResources().getDrawable(R.drawable.jianting));

        ViewTreeObserver vto2 = frameLayout.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                frameLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                // Reset Logo Size
                logo.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                ViewGroup.LayoutParams lp = logo.getLayoutParams();
                lp.width = logo.getMeasuredHeight()*50/34;
                logo.setLayoutParams(lp);

                List<String> bidList = new ArrayList<String>();
                courseInfoByBids = new CourseInfoByBids();
                courseInfoByBids.onGetCoursesInfo = new CourseInfoByBids.OnGetCoursesInfo() {
                    @Override
                    public void onGetCoursesInfo(List<Course> courseList) {
                        courseListFragment = CourseList.newInstance("", "Listen", courseList);
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction t = fm.beginTransaction();
                        t.replace(R.id.frameLayout, courseListFragment);
                        t.commit();
                        dialog.cancel();
                    }
                };// Show Dialog
                dialog = new SweetAlertDialog(Listen.this);
                dialog.setTitleText("玩命加载数据")
                        .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                dialog.show();
                courseInfoByBids.execute();
            }
        });

        listenToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "change");
                SharedPreferences.Editor editor = sharedPref.edit();
                if (listenToggle.isChecked()) {
                    editor.putInt(Constants.Preferences.PREFERENCES_LISTEN, 1);
                    editor.commit();
                    Intent intent = new Intent();
                    intent.setAction("com.badprinter.sysucourse.service.CourseService");
                    intent.putExtra("msg", Constants.ListenerMsg.OPEN_LISTEN);
                    startService(intent);
                    listenToggle.setBackgroundDrawable(ContextCompat.getDrawable(Listen.this, R.drawable.round_bt_warn));
                } else {
                    editor.putInt(Constants.Preferences.PREFERENCES_LISTEN, 0);
                    editor.commit();
                    Intent intent = new Intent();
                    intent.setAction("com.badprinter.sysucourse.service.CourseService");
                    intent.putExtra("msg", Constants.ListenerMsg.CLOSE_LISTEN);
                    startService(intent);
                    listenToggle.setBackgroundDrawable(ContextCompat.getDrawable(Listen.this, R.drawable.round_bt_default));
                }
            }
        });

        logView.updateLog();

        // Register LogReceiver
        logReceiver = new LogReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BoardAction.UPDATE_LOG);
        filter.addAction(Constants.BoardAction.UPDATE_LISTEN_TOGGLE);
        registerReceiver(logReceiver, filter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(logReceiver);
        super.onDestroy();
    }
    private void findViewsById() {
        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        logView = (LogView)findViewById(R.id.logView);
        logo = (ImageView)findViewById(R.id.logo);
        listenToggle = (ToggleButton)findViewById(R.id.listenToggle);
    }
    public void updateCourseInfo() {
        // Show dialog
        dialog = new SweetAlertDialog(Listen.this);
        dialog.setTitleText("正在更新数据")
                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        dialog.show();

        if (courseInfoByBids != null && !courseInfoByBids.isCancelled())
            courseInfoByBids.cancel(true);
        courseInfoByBids = new CourseInfoByBids();
        courseInfoByBids.onGetCoursesInfo = new CourseInfoByBids.OnGetCoursesInfo() {
            @Override
            public void onGetCoursesInfo(List<Course> courseList) {
                courseListFragment.updateListView(courseList);
                dialog.cancel();
            }
        };
        courseInfoByBids.execute();
    }
    private class LogReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.BoardAction.UPDATE_LOG:
                    logView.updateLog();
                    break;
                case Constants.BoardAction.UPDATE_LISTEN_TOGGLE:
                    boolean isWifi = intent.getBooleanExtra("isWifi", true);
                    listenToggle.setChecked(isWifi);
                    if (isWifi) {
                        listenToggle.setBackgroundDrawable(ContextCompat.getDrawable(Listen.this, R.drawable.round_bt_warn));
                    } else {
                        listenToggle.setBackgroundDrawable(ContextCompat.getDrawable(Listen.this, R.drawable.round_bt_default));
                    }
                    break;
            }
        }
    }

}
