package com.badprinter.sysu_course.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.constant.Constants;
import com.badprinter.sysu_course.util.DownloadCode;
import com.badprinter.sysu_course.util.Login;

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
    private ImageView codeImg;
    private EditText code;
    private Login login;
    // For changing password with javascript
    private WebView webView;

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
        codeImg = (ImageView)findViewById(R.id.codeImg);
        code = (EditText)findViewById(R.id.code);
    }
    private void setListener() {
        okBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pw = password.getText().toString();

                getPw(pw);
            }
        });
        codeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change code
                new DownloadCode().execute(codeImg);
            }
        });
    }
    private void getPw(final String pw) {
        webView = new WebView(Main.this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "android");
        webView.loadUrl("file:///android_res/raw/pw.html");
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:encryptPassword('" + pw + "')");
            }
        });
    }

    @JavascriptInterface
    public void onEncryptPassword(String pw) {

        login = new Login();
        login.onLoginResult = new Login.OnLoginResult() {
            @Override
            public void onSucceed() {
                Toast.makeText(Main.this, "登录成功" + Constants.SID, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed() {
                Toast.makeText(Main.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        };
        String un = username.getText().toString();
        Constants.PASSWORD = pw;
        Constants.STUDENT_ID = un;
        String j_code = code.getText().toString();
        login.execute(j_code);
    }

}
