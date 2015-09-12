package com.badprinter.sysu_course.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.badprinter.sysu_course.constant.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by root on 15-9-12.
 */
public class DownloadCode extends AsyncTask<ImageView, Void, Bitmap> {
    private final String TAG = "DownloadCode";
    private final String loginUrl = "http://uems.sysu.edu.cn/elect/";
    private final String codeUrl = "http://uems.sysu.edu.cn/elect/login/code";
    private ImageView imageView;

    @Override
    protected Bitmap doInBackground(ImageView... urls) {
        imageView = urls[0];
        return getCodeImg();
    }
    @Override
    protected void onPostExecute(Bitmap result) {
        imageView.setImageBitmap(result);
    }

    private Bitmap getCodeImg() {
        try {
            URL myUrl = new URL(loginUrl);
            HttpURLConnection urlConn = (HttpURLConnection)myUrl.openConnection();
            setHeader(urlConn);
            urlConn.setRequestMethod("GET");
            // Start the query
            urlConn.connect();
            String headerName=null;
            for (int i=1; (headerName = urlConn.getHeaderFieldKey(i))!=null; i++) {
                if (headerName.equals("Set-Cookie")) {
                    String cookie = urlConn.getHeaderField(i);
                    Log.e(TAG, "cookie : " + cookie);
                    cookie = cookie.substring(0, cookie.indexOf(";"));
                    // Save JSESSIONID
                    Constants.JSESSIONID = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
                }
            }
            if (!Constants.JSESSIONID.equals("")) {
                URL mycodeUrl = new URL(codeUrl);
                HttpURLConnection codeUrlConn = (HttpURLConnection)mycodeUrl.openConnection();
                setHeader(codeUrlConn);
                codeUrlConn.setRequestProperty("Cookie", "JSESSIONID=" + Constants.JSESSIONID);
                codeUrlConn.setRequestMethod("GET");
                // Start the query
                codeUrlConn.connect();

                // 获取验证码
                InputStream is = codeUrlConn.getInputStream();
               // Log.e(TAG, readIt(is, 10000));
                Bitmap codeImg = getCodeImgFromStream(is);
                is.close();
                Log.e(TAG, "codeImg == null : " + (codeImg == null));
                return  codeImg;
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "excption 1");
            return null;
        }
    }

    private void setHeader(HttpURLConnection urlConn) {
        urlConn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        urlConn.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
        urlConn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        urlConn.setRequestProperty("Cache-Control", "max-age=0");
        urlConn.setRequestProperty("Connection", "keep-alive");
        urlConn.setRequestProperty("Host", "uems.sysu.edu.cn");
        urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
        urlConn.setReadTimeout(5000 /* milliseconds */);
        urlConn.setConnectTimeout(5000 /* milliseconds */);
        urlConn.setDoInput(true);
    }

    private Bitmap getCodeImgFromStream(InputStream is) {
        return BitmapFactory.decodeStream(is);
    }
    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
