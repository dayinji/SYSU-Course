package com.badprinter.sysu_course.http;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.badprinter.sysu_course.Common.GlobalData;
import com.badprinter.sysu_course.Constant.Constants;
import com.badprinter.sysu_course.db.DBManager;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 15-9-14.
 */
public class ListenCourse extends AsyncTask<String, Void, Boolean> {
    private final String TAG= "ListenCourse";

    private DBManager dbMgr = new DBManager();
    private List<String> bidList = new ArrayList<>();
    private List<String> courseNameList = new ArrayList<>();
    private List<String> cataList = new ArrayList<>();
    private String url = "http://uems.sysu.edu.cn/elect/s/elect";
    private String[] cataUrls = {
            GlobalData.gongxuanUrl,
            GlobalData.zhuanxuanUrl,
            GlobalData.gongbiUrl,
            GlobalData.zhuanbiUrl
    };
    private List<Integer> resultList = new ArrayList<>();
    public OnListenFinished onListenFinished;
    @Override
    protected Boolean doInBackground(String... urls) {
        Cursor c = dbMgr.queryFromListened();
        while(c.moveToNext()) {
            courseNameList.add(c.getString(c.getColumnIndex("name")));
            bidList.add(c.getString(c.getColumnIndex("bid")));
            String cata = c.getString(c.getColumnIndex("cata"));
            cataList.add(cata);
        }
        c.close();
        for (int i = 0 ; i < bidList.size() ; i++) {
            //String connectUrl = url + cataUrls[cataList.get(i)];
            Connection conn = Jsoup.connect(url);
            setHeader(conn);
            conn.data(getData(cataList.get(i), bidList.get(i)));
            conn.cookie("JSESSIONID", GlobalData.JSESSIONID);
            conn.timeout(5000);
            try {
                conn.post();
                Connection.Response rs = conn.response();
                JSONObject jsonObject = new JSONObject(rs.body());
                JSONObject jsonObject1 = jsonObject.getJSONObject("err");
                int code = (Integer)jsonObject1.get("code");
                resultList.add(code);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "failed");
                resultList.add(Constants.ERROR_MSG.length - 1);
            }
        }
        return true;
    }
    @Override
    protected void onPostExecute(Boolean b) {
        if (b) {
            onListenFinished.onListenFinished(resultList, bidList, courseNameList, cataList);
        }

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
                .header("X-Requested-With", "XMLHttpRequest")
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
    }
    private Map<String, String> getData(String cata, String bid) {
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
    public interface OnListenFinished {
        void onListenFinished(List<Integer> codeList,
                              List<String> bidList,
                              List<String> nameList,
                              List<String> cataList);
    }
}
