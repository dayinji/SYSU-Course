package com.badprinter.sysu_course.util;

import android.os.AsyncTask;
import android.util.Log;

import com.badprinter.sysu_course.constant.Constants;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 15-9-12.
 */
public class CourseSummary extends AsyncTask<Void, Void, Boolean> {
    private final String TAG = "CourseSummary";
    public OnGetCourseSummary onGetCourseSummary;
    private Map<String, String> data = new HashMap<>();

    @Override
    protected Boolean doInBackground(Void... urls) {
        Connection conn2 = Jsoup.connect("http://uems.sysu.edu.cn/elect/s/types?sid=" + Constants.SID);
        setHeader(conn2);
        try {
            Document doc = conn2.get();
            Elements eles = doc.getElementsByTag("a");
            for (Element ele : eles) {
                if (ele.text().equals("公选")) {
                    Constants.gongxuanUrl = ele.attr("href");
                    Element numEle = ele.parent().nextElementSibling();
                    data.put("gongxuanmenshu", numEle.text());
                    Element gradeEle = numEle.nextElementSibling();
                    data.put("gongxuanxuefen", gradeEle.text());
                }
                else if (ele.text().equals("专选")) {
                    Constants.zhuanxuanUrl = ele.attr("href");
                    Element numEle = ele.parent().nextElementSibling();
                    data.put("zhuanxuanmenshu", numEle.text());
                    Element gradeEle = numEle.nextElementSibling();
                    data.put("zhuanxuanxuefen", gradeEle.text());

                }
                else if (ele.text().equals("公必")) {
                    Constants.gongbiUrl = ele.attr("href");
                    Element numEle = ele.parent().nextElementSibling();
                    data.put("gongbimenshu", numEle.text());
                    Element gradeEle = numEle.nextElementSibling();
                    data.put("gongbixuefen", gradeEle.text());

                }
                else if (ele.text().equals("专必")) {
                    Constants.zhuanbiUrl = ele.attr("href");

                    Element numEle = ele.parent().nextElementSibling();
                    data.put("zhuanbimenshu", numEle.text());
                    Element gradeEle = numEle.nextElementSibling();
                    data.put("zhuanbixuefen", gradeEle.text());
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    protected void onPostExecute(Boolean b) {
        if (b)
            onGetCourseSummary.onSucceed(data);
        else
            onGetCourseSummary.onFailed();

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
                .header("Cookie", "JSESSIONID=" + Constants.JSESSIONID)
                .header("Origin", "http://uems.sysu.edu.cn")
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36")
                .header("Referer", "http://uems.sysu.edu.cn/elect/index.html");
    }
    public interface OnGetCourseSummary {
        void onSucceed(Map<String, String> data);
        void onFailed();
    }
}
