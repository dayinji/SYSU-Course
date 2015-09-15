package com.badprinter.sysu_course.http;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.badprinter.sysu_course.Common.GlobalData;
import com.badprinter.sysu_course.Constant.Constants;
import com.badprinter.sysu_course.activity.Listen;
import com.badprinter.sysu_course.db.DBManager;
import com.badprinter.sysu_course.model.Course;
import com.badprinter.sysu_course.model.CourseState;
import com.badprinter.sysu_course.util.PinyinUtil;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 15-9-14.
 */
public class CourseInfoByBids extends AsyncTask<String, Void, Boolean> {
    private final String TAG = "CourseInfoByBids";

    private List<Course> courseList;
    private List<String> bidList;
    public OnGetCoursesInfo onGetCoursesInfo;
    private DBManager dbMgr;

    public CourseInfoByBids() {
        dbMgr = new DBManager();
        courseList = new ArrayList<>();
        bidList = new ArrayList<>();
        Cursor c = dbMgr.queryFromListened();
        while (c.moveToNext()) {
            bidList.add(c.getString(c.getColumnIndex("bid")));
            Course course = new Course();
            course.setBid(c.getString(c.getColumnIndex("bid")));
            course.setName(c.getString(c.getColumnIndex("name")));
            course.setState(CourseState.CANNOTSELECT);
            course.setTeacher(c.getString(c.getColumnIndex("teacher")));
            String pinyin = PinyinUtil.getPinYinFromHanYu(course.getName(), PinyinUtil.UPPER_CASE,
                    PinyinUtil.WITH_TONE_NUMBER, PinyinUtil.WITH_V);
            course.setPinyin(pinyin);
            courseList.add(course);
        }
        c.close();
        Log.e(TAG, "bid count = " + bidList.size());
    }

    @Override
    protected Boolean doInBackground(String... urls) {

        for (int i = 0 ; i < bidList.size() ; i++) {

            String bid = bidList.get(i);
            String url = "http://uems.sysu.edu.cn/elect/s/courseDet?id=" + bid +
                    "&xnd=2015-2016&xq=2&sid=" + GlobalData.SID;

            Connection conn = Jsoup.connect(url);
            setHeader(conn);
            conn.data(getData(bid));
            conn.cookie("JSESSIONID", GlobalData.JSESSIONID);
            try {
                Log.e(TAG, "start connect");
                conn.timeout(5000);
                Document doc = conn.post();
                Elements trs = doc.getElementsByTag("tr");
                Course course = courseList.get(i);
                course.setAllNum(trs.get(6).getElementsByTag("td").get(1).text());
                course.setCandidateNum(trs.get(8).getElementsByTag("td").get(3).text());
                course.setVacancyNum(trs.get(8).getElementsByTag("td").get(1).text());
                course.setTimePlace(trs.get(10).getElementsByTag("td").get(1).text());
                course.setCredit(trs.get(5).getElementsByTag("td").get(3).text());
                course.setState(CourseState.CANNOTSELECT);
                int v = Integer.parseInt(course.getVacancyNum());
                int c = Integer.parseInt(course.getCandidateNum());
                if (c != 0) {
                    float rate = v*100/c;
                    if (rate > 100)
                        rate = 100;
                    course.setRate(rate + "%");
                } else {
                    course.setRate("0%");
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "failed");
            }
        }
        return true;

    }
    @Override
    protected void onPostExecute(Boolean b) {
        if (b) {
            onGetCoursesInfo.onGetCoursesInfo(courseList);
        }
    }
    public interface OnSelected {
        void onFinished(Integer code);
    }

    private void setHeader(Connection conn) {
        conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Encoding", "gzip, deflate, sdch")
                .header("Accept-Language", "zh-CN,zh;q=0.8")
                .header("Connection", "keep-alive")
                .header("Content-Length", "77")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Host", "uems.sysu.edu.cn")
                .header("Origin", "http://uems.sysu.edu.cn")
                        //.header("Cookie", "JSESSIONID=" + GlobalData.JSESSIONID)
                .header("X-Requested-With", "XMLHttpRequest")
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
    }
    private Map<String, String> getData(String bid) {
        Map<String, String> data = new HashMap<>();
        data.put("xnd", "2015-2016");
        data.put("xq", "2");
        data.put("id", bid);
        data.put("sid", GlobalData.SID);
        return data;
    }
    public interface OnGetCoursesInfo {
        void onGetCoursesInfo(List<Course> courseList);
    }
}
