package com.badprinter.sysu_course.http;

import android.os.AsyncTask;
import android.util.Log;

import com.badprinter.sysu_course.Common.GlobalData;
import com.badprinter.sysu_course.activity.Listen;
import com.badprinter.sysu_course.model.Course;
import com.badprinter.sysu_course.model.CourseState;
import com.badprinter.sysu_course.util.PinyinUtil;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by root on 15-9-15.
 */
public class MyCoursesResult extends AsyncTask<String, Void, Boolean> {
    private final String TAG = "MyCoursesResult";

    private String url = "http://uems.sysu.edu.cn/elect/s/courseAll?xnd=2015-2016&xq=2&sid=";
    private List<Course> courseList = new ArrayList<>();
    public OnGetMyCourses onGetMyCourses;

    @Override
    protected Boolean doInBackground(String... urls) {

        url += GlobalData.SID;
        Connection conn = Jsoup.connect(url);
        conn.timeout(5000);
        setHeader(conn);
        try {
            Log.e(TAG, url);
            conn.get();
            Document doc =conn.response().parse();
            Elements trs = doc.getElementById("elected").getElementsByTag("tbody").get(0).getElementsByTag("tr");
            for (Element tr : trs) {
                Elements tds = tr.getElementsByTag("td");
                Course course = new Course();
                course.setName(tds.get(3).text());
                course.setTeacher(tds.get(9).text());
                course.setCredit(tds.get(5).text());
                course.setTimePlace(tds.get(7).text());
                String str = tds.get(3).getElementsByTag("a").get(0).attr("onclick");
                String bid = "";
                Pattern p=Pattern.compile("'\\d*'");
                Matcher m=p.matcher(str);
                while(m.find()) {
                    bid = str.substring(m.start()+1, m.end()-1);
                    break;
                }
                course.setBid(bid);
                String pinyin = PinyinUtil.getPinYinFromHanYu(course.getName(), PinyinUtil.UPPER_CASE,
                        PinyinUtil.WITH_TONE_NUMBER, PinyinUtil.WITH_V);
                course.setPinyin(pinyin);
                course.setState(CourseState.CANNOTUNSELECT);
                courseList.add(course);
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "failed ");
            e.printStackTrace();
            return false;
        }

    }
    @Override
    protected void onPostExecute(Boolean b) {
        if (b)
            onGetMyCourses.onSucceed(courseList);
        else
            onGetMyCourses.onFailed();
    }
    private void setHeader(Connection conn) {
        conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Encoding", "gzip, deflate, sdch")
                .header("Accept-Language", "zh-CN,zh;q=0.8")
                .header("Connection", "keep-alive")
                .header("Host", "uems.sysu.edu.cn")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Referer", "http://uems.sysu.edu.cn/elect/s/types?sid=" + GlobalData.SID)
                .header("Cookie", "JSESSIONID=" + GlobalData.JSESSIONID)
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
    }
    public interface OnGetMyCourses {
        void onSucceed(List<Course> list);
        void onFailed();
    }
}
