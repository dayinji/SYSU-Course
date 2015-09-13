package com.badprinter.sysu_course.util;

import android.os.AsyncTask;
import android.util.Log;

import com.badprinter.sysu_course.Common.GlobalData;
import com.badprinter.sysu_course.db.DBManager;
import com.badprinter.sysu_course.model.Course;
import com.badprinter.sysu_course.model.CourseState;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by root on 15-9-12.
 */
public class CourseInfo extends AsyncTask<String, Void, Boolean> {
    private final String TAG = "CourseInfo";
    public OnGetCourseInfo onGetCourseInfo;
    private List<Course> myCourseList = new ArrayList<>();
    private List<Course> otherCourseList = new ArrayList<>();
    private List<Course> likeCourseList = new ArrayList<>();
    private DBManager dbMgr = new DBManager();
    @Override
    protected Boolean doInBackground(String... urls) {
        String url = urls[0];
        Connection conn2 = Jsoup.connect(url);
        setHeader(conn2);
        try {
           // conn2.maxBodySize(200000);
            Document doc = conn2.get();
            Log.e(TAG, "doc = " + doc.toString());
            // MyCourses
            Element electedTable = doc.getElementById("elected");
            Elements trs = electedTable.getElementsByTag("tr");
            for (Element tr : trs) {
                Elements tds = tr.getElementsByTag("td");
                analyseSeleted(tds);
            }
            //Other Courses
            Element otherTable = doc.getElementById("courses");
            Elements trs1 = otherTable.getElementsByTag("tr");
            for (Element tr : trs1) {
                Elements tds = tr.getElementsByTag("td");
                analyseOthers(tds);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    protected void onPostExecute(Boolean b) {
        if (b) {
            sortCourseList(myCourseList);
            sortCourseList(otherCourseList);
            sortCourseList(likeCourseList);
            onGetCourseInfo.onSucceed(myCourseList, otherCourseList, likeCourseList);
        }
        else
            onGetCourseInfo.onFailed();

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
    public interface OnGetCourseInfo {
        void onSucceed(List<Course> myCourseList, List<Course> otherCourseList, List<Course> likeCourseList);
        void onFailed();
    }
    private void analyseSeleted(Elements tds) {
        if (tds.size() <= 1)
            return;
        Course c = new Course();
        try {
            if (tds.get(0).getElementsByTag("a").size() == 0 &&
                    tds.get(1).text().equals("选课成功")) {
                c.setState(CourseState.CANNOTUNSELECT);
            } else if (tds.get(0).getElementsByTag("a").get(0).text().equals("退选") &&
                    tds.get(1).text().equals("选课成功")) {
                c.setState(CourseState.SUCCEED);
            } else if (tds.get(1).text().equals("待筛选")) {
                c.setState(CourseState.SELECTED);
            }
            String str = tds.get(2).getElementsByTag("a").get(0).attr("onclick");
            String bid = "";
            Pattern p=Pattern.compile("'\\d*'");
            Matcher m=p.matcher(str);
            while(m.find()) {
                bid = str.substring(m.start()+1, m.end()-1);
                break;
            }
            c.setBid(bid);
            c.setName(tds.get(2).getElementsByTag("a").get(0).text());
            String pinyin = PinyinUtil.getPinYinFromHanYu(c.getName(), PinyinUtil.UPPER_CASE,
                    PinyinUtil.WITH_TONE_NUMBER, PinyinUtil.WITH_V);
            c.setPinyin(pinyin);
            c.setTimePlace(tds.get(3).text());
            c.setTeacher(tds.get(4).text());
            c.setCredit(tds.get(5).text());
            c.setAllNum(tds.get(6).text());
            c.setCandidateNum(tds.get(7).text());
            c.setVacancyNum(tds.get(8).text());
            c.setRate(tds.get(9).text());
            myCourseList.add(c);
            if (dbMgr.isLike(c)) {
                likeCourseList.add(c);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
    private void analyseOthers(Elements tds) {
        if (tds.size() == 0)
            return;
        Course c = new Course();
        try {
            if (tds.get(0).getElementsByTag("a").size() == 2) {
                c.setState(CourseState.CANSELECT);
            } else {
                c.setState(CourseState.CANNOTSELECT);
            }
            String str = tds.get(1).getElementsByTag("a").get(0).attr("onclick");
            String bid = "";
            Pattern p=Pattern.compile("'\\d*'");
            Matcher m=p.matcher(str);
            while(m.find()) {
                bid = str.substring(m.start()+1, m.end()-1);
                break;
            }
            c.setBid(bid);
            c.setName(tds.get(1).getElementsByTag("a").get(0).text());
            String pinyin = PinyinUtil.getPinYinFromHanYu(c.getName(), PinyinUtil.UPPER_CASE,
                    PinyinUtil.WITH_TONE_NUMBER, PinyinUtil.WITH_V);
            c.setPinyin(pinyin);
            c.setTimePlace(tds.get(2).text());
            c.setTeacher(tds.get(3).text());
            c.setCredit(tds.get(4).text());
            c.setAllNum(tds.get(5).text());
            c.setCandidateNum(tds.get(6).text());
            c.setVacancyNum(tds.get(7).text());
            c.setRate(tds.get(8).text());
            otherCourseList.add(c);
            if (dbMgr.isLike(c)) {
                likeCourseList.add(c);
            }
            Log.e(TAG, "分析了一个");
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    /*
     * Sort a List By Pinyin
     */
    private void sortCourseList(List<Course> list) {
        Collections.sort(list);
    }
}
