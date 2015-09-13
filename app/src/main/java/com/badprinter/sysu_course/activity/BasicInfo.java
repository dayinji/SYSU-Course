package com.badprinter.sysu_course.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.badprinter.sysu_course.Common.GlobalData;
import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.util.CourseSummary;

import java.util.Map;

public class BasicInfo extends ActionBarActivity implements View.OnClickListener{
    private final String TAG = "BasicInfo";

    private TextView gongxuanText;
    private TextView gongbiText;
    private TextView zhuanxuanText;
    private TextView zhuanbiText;
    private Button gongxuanBt;
    private Button gongbiBt;
    private Button zhuanxuanBt;
    private Button zhuanbiBt;

    private CourseSummary courseSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);
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
                Log.e(TAG, GlobalData.gongxuanUrl);
                Log.e(TAG, GlobalData.zhuanxuanUrl);
                Log.e(TAG, GlobalData.gongbiUrl);
                Log.e(TAG, GlobalData.zhuanbiUrl);
            }

            @Override
            public void onFailed() {
                Toast.makeText(BasicInfo.this, "获取课程信息失败", Toast.LENGTH_SHORT);
            }
        };
        courseSummary.execute();
    }
    private void findViewsById() {
        gongxuanText = (TextView)findViewById(R.id.gongxuanText);
        gongbiText = (TextView)findViewById(R.id.gongbiText);
        zhuanxuanText = (TextView)findViewById(R.id.zhuanxuanText);
        zhuanbiText = (TextView)findViewById(R.id.zhuanbiText);
        zhuanbiBt = (Button)findViewById(R.id.zhuanbiBt);
        gongxuanBt = (Button)findViewById(R.id.gongxuanBt);
        gongbiBt = (Button)findViewById(R.id.gongbiBt);
        zhuanxuanBt = (Button)findViewById(R.id.zhuanxuanBt);
    }
    private void setListener() {
        zhuanbiBt.setOnClickListener(this);
        gongxuanBt.setOnClickListener(this);
        gongbiBt.setOnClickListener(this);
        zhuanxuanBt.setOnClickListener(this);
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
        }
    }

    private void turnTo(String url, String cata) {
        Intent intent = new Intent(BasicInfo.this, Course.class);
        intent.putExtra("url", url);
        intent.putExtra("cata", cata);
        startActivity(intent);
    }
}
