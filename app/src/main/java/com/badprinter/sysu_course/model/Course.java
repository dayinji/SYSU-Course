package com.badprinter.sysu_course.model;

import android.util.Log;

/**
 * Created by root on 15-9-12.
 */
public class Course implements Comparable<Course> {
    private final String TAG = "COURSE";

    private String pinyin;
    private String bid;
    private String name;
    private String teacher;
    private String timePlace;
    private String credit;
    private String allNum;
    private String candidateNum;
    private String vacancyNum;
    private String rate;
    private CourseState state;

    public Course(String bid, String pinyin, String name, String teacher, String timePlace,
                  String credit, String allNum, String candidateNum,
                  String vacancyNum, String rate, CourseState state) {

        this.bid = bid;
        this.pinyin = pinyin;
        this.name = name;
        this.teacher = teacher;
        this.timePlace = timePlace;
        this.credit = credit;
        this.allNum = allNum;
        this.candidateNum = candidateNum;
        this.vacancyNum = vacancyNum;
        this.rate = rate;
        this.state = state;

    }

    public int compareTo(Course other) {
        char[] otherPinyin = other.getPinyin().toCharArray();
        char[] thisPinyin = pinyin.toCharArray();
        int length = otherPinyin.length > thisPinyin.length ? thisPinyin.length : otherPinyin.length;
        for (int i = 0 ; i < length ; i++) {
            if (isLetter(thisPinyin[i]) && isLetter(otherPinyin[i]) ||
                    !isLetter(thisPinyin[i]) && !isLetter(otherPinyin[i])) {
                if (thisPinyin[i] < otherPinyin[i])
                    return -1;
                else if (thisPinyin[i] > otherPinyin[i])
                    return 1;
            } else  {
                return isLetter(thisPinyin[i]) ? 1 : -1;
            }
        }
        if (thisPinyin.length < otherPinyin.length)
            return -1;
        else if (thisPinyin.length > otherPinyin.length)
            return 1;
        return 0;
    }

    public Course() {

    }

    public String getBid() {
        return bid;
    }
    public void setBid(String bid) {
        this.bid = bid;
    }


    public String getPinyin() {
        return pinyin;
    }
    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }
    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTimePlace() {
        return timePlace;
    }
    public void setTimePlace(String timePlace) {
        this.timePlace = timePlace;
    }

    public String getCredit() {
        return credit;
    }
    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getAllNum() {
        return allNum;
    }
    public void setAllNum(String allNum) {
        this.allNum = allNum;
    }

    public String getCandidateNum() {
        return candidateNum;
    }
    public void setCandidateNum(String candidateNum) {
        this.candidateNum = candidateNum;
    }

    public String getVacancyNum() {
        return vacancyNum;
    }
    public void setVacancyNum(String vacancyNum) {
        this.vacancyNum = vacancyNum;
    }

    public String getRate() {
        return rate;
    }
    public void setRate(String rate) {
        this.rate = rate;
    }

    public CourseState getState() {
        return state;
    }
    public void setState(CourseState state) {
        this.state = state;
    }

    private boolean isLetter(char c) {
        return (c >= 65 && c <= 90) || (c >= 97 && c <= 122);
    }

}
