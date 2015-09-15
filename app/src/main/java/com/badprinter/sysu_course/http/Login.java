package com.badprinter.sysu_course.http;

import android.os.AsyncTask;
import android.util.Log;

import com.badprinter.sysu_course.Common.GlobalData;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 15-9-12.
 */
public class Login extends AsyncTask<String, Void, Boolean> {
    private final String TAG = "Login";
    private final String loginUrl = "http://uems.sysu.edu.cn/elect/login";
    public OnLoginResult onLoginResult;

    @Override
    protected Boolean doInBackground(String... urls) {
        String j_code = urls[0];
        Connection conn = Jsoup.connect(loginUrl);
        //conn.cookie("JSESSIONID", GlobalData.JSESSIONID);
        conn.data(getData(j_code));
        setHeader(conn);
        // No Redirection
        conn.request().followRedirects(false);
        try {
            conn.post();
            org.jsoup.Connection.Response rs = conn.response();
            if (rs.header("Location") != null) {
                GlobalData.SID = rs.header("Location").replace("http://uems.sysu.edu.cn/elect/s/types?sid=", "");

                return true;
            }
        } catch (IOException e) {
            Log.e(TAG, "login connect failed");
            e.printStackTrace();
            return false;
        }
        return false;

    }
    @Override
    protected void onPostExecute(Boolean b) {
        if (b)
            onLoginResult.onSucceed();
        else
            onLoginResult.onFailed();

    }

    private Map<String, String> getData(String j_code) {
        Map<String, String> data = new HashMap<>();
        data.put("username", GlobalData.STUDENT_ID);
        data.put("password", GlobalData.PASSWORD);
        data.put("j_code", j_code);
        data.put("lt", "");
        data.put("_eventId", "submit");
        data.put("gateway", "true");
        for (String key : data.keySet()) {
            Log.e(TAG, "key = " + key + ", value = " + data.get(key));
        }
        return data;
    }
    private void setHeader(Connection conn) {
        conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "zh-CN,zh;q=0.8")
                .header("Cache-Control", "max-age=0")
                .header("Connection", "keep-alive")
                .header("Content-Length", "104")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Host", "uems.sysu.edu.cn")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Cookie", "JSESSIONID=" + GlobalData.JSESSIONID)
                .header("Origin", "http://uems.sysu.edu.cn")
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36")
                .header("Referer", "http://uems.sysu.edu.cn/elect/index.html");
    }
    public interface OnLoginResult {
        void onSucceed();
        void onFailed();
    }
}
