package com.badprinter.sysu_course.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.badprinter.sysu_course.Common.GlobalData;
import com.badprinter.sysu_course.Constant.Constants;
import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.db.DBManager;
import com.badprinter.sysu_course.service.CourseService;
import com.badprinter.sysu_course.http.CourseSummary;
import com.badprinter.sysu_course.http.Login;

import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BasicInfo extends ActionBarActivity implements View.OnClickListener{
    private final String TAG = "BasicInfo";

    private TextView gongxuanText;
    private TextView gongbiText;
    private TextView zhuanxuanText;
    private TextView zhuanbiText;
    private TextView listenText;
    private Button gongxuanBt;
    private Button gongbiBt;
    private Button zhuanxuanBt;
    private Button zhuanbiBt;
    private Button listenBt;
    private ImageView logo;
    private SweetAlertDialog dialog;

    private CourseSummary courseSummary;
    private DBManager dbMgr;
    private Login login;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);

        sharedPreferences = getSharedPreferences(Constants.Preferences.PREFERENCES_KEY, MODE_PRIVATE);

        findViewsById();
        setListener();

        courseSummary = new CourseSummary();
        courseSummary.onGetCourseSummary = new CourseSummary.OnGetCourseSummary() {
            @Override
            public void onSucceed(Map<String, String> data) {
                gongxuanText.setText("公选 / 已选" + data.get("gongxuanmenshu") + " / 已选学分" + data.get("gongxuanxuefen"));
                zhuanxuanText.setText("专选 / 已选" + data.get("zhuanxuanmenshu") + " / 已选学分" + data.get("zhuanxuanxuefen"));
                gongbiText.setText("公必 / 已选" + data.get("gongbimenshu") + " / 已选学分" + data.get("gongbixuefen"));
                zhuanbiText.setText("专必 / 已选" + data.get("zhuanbimenshu") + " / 已选学分" + data.get("zhuanbixuefen"));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.Preferences.PREFERENCES_GONGXUAN_URL, GlobalData.gongxuanUrl);
                editor.putString(Constants.Preferences.PREFERENCES_ZHUANXUAN_URL, GlobalData.zhuanxuanUrl);
                editor.putString(Constants.Preferences.PREFERENCES_GONGBI_URL, GlobalData.gongbiUrl);
                editor.putString(Constants.Preferences.PREFERENCES_ZHUANBI_URL, GlobalData.zhuanbiUrl);
                editor.commit();
            }

            @Override
            public void onFailed() {
                Toast.makeText(BasicInfo.this, "请重新登陆", Toast.LENGTH_SHORT).show();
                finish();
            }
        };
        courseSummary.execute();

        //Init ListenText
        dbMgr = new DBManager();
        Cursor c = dbMgr.queryFromListened();
        listenText.setText("监听 / 正在监听" + c.getCount() + "门课程");
        c.close();

        final ViewTreeObserver observer = logo.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                logo.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                ViewGroup.LayoutParams lp = logo.getLayoutParams();
                lp.width = logo.getMeasuredHeight();
                logo.setLayoutParams(lp);
            }
        });

        //Init Service
        Intent intentInitService = new Intent();
        intentInitService.putExtra("msg", Constants.ListenerMsg.INIT_SERVICE);
        intentInitService.setAction("com.badprinter.sysucourse.service.CourseService");
        startService(intentInitService);
    }
    @Override
    public void onResume() {
        //Init ListenText
        dbMgr = new DBManager();
        Cursor c = dbMgr.queryFromListened();
        listenText.setText("监听 / 正在监听" + c.getCount() + "门课程");
        c.close();

        courseSummary = new CourseSummary();
        courseSummary.onGetCourseSummary = new CourseSummary.OnGetCourseSummary() {
            @Override
            public void onSucceed(Map<String, String> data) {
                gongxuanText.setText("公选 / 已选" + data.get("gongxuanmenshu") + " / 已选学分" + data.get("gongxuanxuefen"));
                zhuanxuanText.setText("专选 / 已选" + data.get("zhuanxuanmenshu") + " / 已选学分" + data.get("zhuanxuanxuefen"));
                gongbiText.setText("公必 / 已选" + data.get("gongbimenshu") + " / 已选学分" + data.get("gongbixuefen"));
                zhuanbiText.setText("专必 / 已选" + data.get("zhuanbimenshu") + " / 已选学分" + data.get("zhuanbixuefen"));
            }

            @Override
            public void onFailed() {
            }
        };
        courseSummary.execute();

        super.onResume();
    }
    @Override
    public void onDestroy() {
        Intent intent = new Intent(BasicInfo.this, CourseService.class);
        stopService(intent);
        super.onDestroy();
    }
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dialog = new SweetAlertDialog(BasicInfo.this);
            dialog.setTitleText("确定退出当前账号?")
                    .setCancelText("取消")
                    .showCancelButton(true)
                    .setConfirmText("确定")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            finish();
                            dialog.cancel();
                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dialog.cancel();
                        }
                    })
                    .show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void findViewsById() {
        gongxuanText = (TextView)findViewById(R.id.gongxuanText);
        gongbiText = (TextView)findViewById(R.id.gongbiText);
        zhuanxuanText = (TextView)findViewById(R.id.zhuanxuanText);
        zhuanbiText = (TextView)findViewById(R.id.zhuanbiText);
        listenText = (TextView)findViewById(R.id.listenText);
        zhuanbiBt = (Button)findViewById(R.id.zhuanbiBt);
        gongxuanBt = (Button)findViewById(R.id.gongxuanBt);
        gongbiBt = (Button)findViewById(R.id.gongbiBt);
        zhuanxuanBt = (Button)findViewById(R.id.zhuanxuanBt);
        listenBt = (Button)findViewById(R.id.listenBt);
        logo = (ImageView)findViewById(R.id.logo);
    }
    private void setListener() {
        zhuanbiBt.setOnClickListener(this);
        gongxuanBt.setOnClickListener(this);
        gongbiBt.setOnClickListener(this);
        zhuanxuanBt.setOnClickListener(this);
        listenBt.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        String url = "http://uems.sysu.edu.cn/elect/s/";
        switch (view.getId()) {
            case R.id.gongxuanBt:
                turnTo(url+ GlobalData.gongxuanUrl, "公选");
                break;
            case R.id.zhuanxuanBt:
                turnTo(url+ GlobalData.zhuanxuanUrl, "专选");
                break;
            case R.id.gongbiBt:
                turnTo(url+ GlobalData.gongbiUrl, "公必");
                break;
            case R.id.zhuanbiBt:
                turnTo(url+ GlobalData.zhuanbiUrl, "专必");
                break;
            case R.id.listenBt:
                Intent intent = new Intent(BasicInfo.this, Listen.class);
                startActivity(intent);
                break;
        }
    }

    private void turnTo(String url, String cata) {
        Intent intent = new Intent(BasicInfo.this, Course.class);
        intent.putExtra("url", url);
        intent.putExtra("cata", cata);
        startActivity(intent);
    }

}
