package com.badprinter.sysu_course.activity;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.util.DownloadCode;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class Main extends ActionBarActivity {
    private final String TAG = "MAIN";

    private EditText username;
    private EditText password;
    private Button okBt;
    private TextView result;
    private ImageView codeImg;

    private String loginUrl = "http://uems.sysu.edu.cn/elect/";
    private String codeUrl = "http://uems.sysu.edu.cn/elect/login/code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewsById();
        setListener();
        //getCodeImg();
    }
    private void findViewsById() {
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        okBt = (Button)findViewById(R.id.okBt);
        result = (TextView)findViewById(R.id.result);
        codeImg = (ImageView)findViewById(R.id.codeImg);
    }
    private void setListener() {
        okBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String un = username.getText().toString();
                String pw = username.getText().toString();
                new DownloadCode().execute(codeImg);
            }
        });
    }


}
