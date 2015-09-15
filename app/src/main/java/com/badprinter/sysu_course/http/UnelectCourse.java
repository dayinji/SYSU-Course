package com.badprinter.sysu_course.http;

import android.os.AsyncTask;
import android.util.Log;

import com.badprinter.sysu_course.Common.GlobalData;
import com.badprinter.sysu_course.Constant.Constants;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 15-9-13.
 */
public class UnelectCourse extends AsyncTask<String, Void, Integer> {
    private final String TAG = "ElectCourse";
    private String url = "http://uems.sysu.edu.cn/elect/s/unelect";
    private String bid;
    private String cata;

    public OnSelected onSelected;
    @Override
    protected Integer doInBackground(String... urls) {
        bid = urls[0];
        cata = urls[1];

        Connection conn = Jsoup.connect(url);
        setHeader(conn);
        conn.data(getData());
        conn.cookie("JSESSIONID", GlobalData.JSESSIONID);
        try {
            Log.e(TAG, "start connect");
            conn.timeout(5000);
            conn.post();
            Connection.Response rs = conn.response();
            JSONObject jsonObject = new JSONObject(rs.body());
            JSONObject jsonObject1 = jsonObject.getJSONObject("err");
            int code = (Integer)jsonObject1.get("code");
            return code;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "failed");
            return Constants.ERROR_MSG.length-1;
        }
    }
    @Override
    protected void onPostExecute(Integer code) {
        onSelected.onFinished(code);

    }
    public interface OnSelected {
        void onFinished(Integer code);
    }

    private void setHeader(Connection conn) {
        conn.header("Accept", "*/*")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "zh-CN,zh;q=0.8")
                .header("Connection", "keep-alive")
                .header("Content-Length", "88")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Host", "uems.sysu.edu.cn")
                .header("Origin", "http://uems.sysu.edu.cn")
                //.header("Cookie", "JSESSIONID=" + GlobalData.JSESSIONID)
                .header("X-Requested-With", "XMLHttpRequest")
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
    }
    private Map<String, String> getData() {
        Map<String, String> data = new HashMap<>();
        data.put("xnd", "2015-2016");
        data.put("xq", "2");
        if (cata.equals("公选"))
            data.put("kclbm", "30");
        else if (cata.equals("专选"))
            data.put("kclbm", "21");
        else if (cata.equals("公必"))
            data.put("kclbm", "10");
        else if (cata.equals("专必"))
            data.put("kclbm", "11");

        data.put("jxbh", bid);
        data.put("sid", GlobalData.SID);
        return data;
    }
}
