package com.badprinter.sysu_course.http;

import android.os.AsyncTask;

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
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by root on 15-9-16.
 */
public class GivenCourse extends AsyncTask<String, Void, Boolean> {
    private final String TAG = "GivenCourse";
    private final String gongxuanUrl = "http://uems.sysu.edu.cn/elect/s/" + GlobalData.gongxuanUrl;
    private final String zhuanxuanUrl = "http://uems.sysu.edu.cn/elect/s/" + GlobalData.zhuanxuanUrl;
    private final String gongbiUrl = "http://uems.sysu.edu.cn/elect/s/" + GlobalData.gongbiUrl;

    private List<Course> myCourseList = new ArrayList<>();
    public OnGetGivenCourse onGetGivenCourse;
    private String[] catas = {
            "公选", "专选", "公必"
    };

    @Override
    protected Boolean doInBackground(String... urls) {
        String[] url = {
                gongxuanUrl, zhuanxuanUrl, gongbiUrl
        };
        for (int i = 0 ; i < 3 ; i++) {
            Connection conn2 = Jsoup.connect(url[i]);
            setHeader(conn2);
            try {
                // conn2.maxBodySize(200000);
                Document doc = conn2.get();
                // MyCourses
                Element electedTable = doc.getElementById("elected");
                Elements trs = electedTable.getElementsByTag("tr");
                for (Element tr : trs) {
                    Elements tds = tr.getElementsByTag("td");
                    analyseSeleted(tds, i);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    @Override
    protected void onPostExecute(Boolean b) {
        if (b) {
            sortCourseList(myCourseList);
            onGetGivenCourse.onSucceed(myCourseList);
        }
        else
            onGetGivenCourse.onFailed();
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
    private void analyseSeleted(Elements tds, int cataId) {
        if (tds.size() <= 1)
            return;
        Course c = new Course();
        try {
            if (tds.get(0).getElementsByTag("a").size() == 0 &&
                    tds.get(1).text().equals("选课成功")) {
                //Do nothing
            } else if (tds.get(0).getElementsByTag("a").get(0).text().equals("退选") &&
                    tds.get(1).text().equals("选课成功")) {
                c.setState(CourseState.SUCCEED);

                String str = tds.get(2).getElementsByTag("a").get(0).attr("onclick");
                String bid = "";
                Pattern p = Pattern.compile("'\\d*'");
                Matcher m = p.matcher(str);
                while (m.find()) {
                    bid = str.substring(m.start() + 1, m.end() - 1);
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
                c.setCata(catas[cataId]);
               /* c.setAllNum(tds.get(6).text());
                c.setCandidateNum(tds.get(7).text());
                c.setVacancyNum(tds.get(8).text());
                c.setRate(tds.get(9).text());*/
                myCourseList.add(c);
            }
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

    public interface OnGetGivenCourse {
        void onSucceed(List<Course> myCourseList);
        void onFailed();
    }
}
