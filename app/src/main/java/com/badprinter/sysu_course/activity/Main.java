package com.badprinter.sysu_course.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.badprinter.sysu_course.Common.AppContext;
import com.badprinter.sysu_course.Constant.Constants;
import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.Common.GlobalData;
import com.badprinter.sysu_course.http.DownloadCode;
import com.badprinter.sysu_course.http.Login;


public class Main extends ActionBarActivity{
    private final String TAG = "MAIN";

    private EditText username;
    private EditText password;
    private Button okBt;
    private Login login;
    // For changing password with javascript
    private WebView webView;
    private AlertDialog codeDialog;

    private String loginUrl = "http://uems.sysu.edu.cn/elect/";
    private String codeUrl = "http://uems.sysu.edu.cn/elect/login/code";
    private SharedPreferences sharedPreferences;
    private String j_code = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewsById();
        setListener();

        sharedPreferences = getSharedPreferences(Constants.Preferences.PREFERENCES_KEY, MODE_PRIVATE);

        username.setText(sharedPreferences.getString(Constants.Preferences.PREFERENCES_USERNAME, ""));
        password.setText(sharedPreferences.getString(Constants.Preferences.PREFERENCES_PASSWORD, ""));

        if (getIntent().getStringExtra("from") == null &&
                !sharedPreferences.getString(Constants.Preferences.PREFERENCES_JSESSIONID, "").equals("")) {

            GlobalData.PASSWORD = password.getText().toString();
            GlobalData.STUDENT_ID = username.getText().toString();
            GlobalData.SID = sharedPreferences.getString(Constants.Preferences.PREFERENCES_SID, "");
            GlobalData.JSESSIONID = sharedPreferences.getString(Constants.Preferences.PREFERENCES_JSESSIONID, "");
            GlobalData.gongxuanUrl = sharedPreferences.getString(Constants.Preferences.PREFERENCES_GONGXUAN_URL, "");
            GlobalData.zhuanxuanUrl = sharedPreferences.getString(Constants.Preferences.PREFERENCES_ZHUANXUAN_URL, "");
            GlobalData.gongbiUrl = sharedPreferences.getString(Constants.Preferences.PREFERENCES_GONGBI_URL, "");
            GlobalData.zhuanbiUrl = sharedPreferences.getString(Constants.Preferences.PREFERENCES_ZHUANBI_URL, "");
            Log.e(TAG, "pw : " + GlobalData.PASSWORD);
            Log.e(TAG, "studentId : " + GlobalData.STUDENT_ID);
            Log.e(TAG, "sid : " + GlobalData.SID);
            Log.e(TAG, "JSESSIONID : " + GlobalData.JSESSIONID);
            Log.e(TAG, "gongxuanUrl : " + GlobalData.gongxuanUrl);
            Log.e(TAG, "zhuanxuanUrl : " + GlobalData.zhuanxuanUrl);
            Log.e(TAG, "gongbiUrl : " + GlobalData.gongbiUrl);
            Log.e(TAG, "zhuanbiUrl : " + GlobalData.zhuanbiUrl);

            Intent intent = new Intent(Main.this, BasicInfo.class);
            startActivity(intent);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void findViewsById() {
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        okBt = (Button)findViewById(R.id.okBt);
    }

    private void setListener() {
        okBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String pw = password.getText().toString();
                getPw(pw);*/
                showCodeDialog();
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
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.Preferences.PREFERENCES_JSESSIONID, GlobalData.JSESSIONID);
                editor.putString(Constants.Preferences.PREFERENCES_SID, GlobalData.SID);
                editor.putString(Constants.Preferences.PREFERENCES_USERNAME, username.getText().toString());
                editor.putString(Constants.Preferences.PREFERENCES_PASSWORD, password.getText().toString());
                editor.commit();

                Toast.makeText(AppContext.getInstance(), "登录成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Main.this, BasicInfo.class);
                intent.putExtra("from", "loginActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void onFailed() {
                Toast.makeText(Main.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        };
        String un = username.getText().toString();
        GlobalData.PASSWORD = pw;
        GlobalData.STUDENT_ID = un;
        //String j_code = code.getText().toString();
        login.execute(j_code);
    }
    /*
         * Show CodeDialog
         */
    private void showCodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_code, null);

        builder.setView(view);

        final Button loginBt = (Button)view.findViewById(R.id.okBt);
        final ImageView codeImg = (ImageView)view.findViewById(R.id.codeImg);
        final EditText code = (EditText)view.findViewById(R.id.code);

        new DownloadCode().execute(codeImg);

        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                j_code = code.getText().toString();
                String pw = password.getText().toString();
                getPw(pw);
                codeDialog.cancel();
            }
        });
        codeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change code
                new DownloadCode().execute(codeImg);
            }
        });

        codeDialog = builder.create();
        codeDialog.show();
    }
}
