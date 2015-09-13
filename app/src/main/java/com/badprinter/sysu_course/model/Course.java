package com.badprinter.sysu_course.model;

/**
 * Created by root on 15-9-12.
 */
public class Course {
    private String name;
    private String teacher;
    private String timePlace;
    private String credit;
    private String allNum;
    private String candidateNum;
    private String vacancyNum;
    private String rate;
    private CourseState state;

    public Course(String name, String teacher, String timePlace,
                  String credit, String allNum, String candidateNum,
                  String vacancyNum, String rate, CourseState state) {
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
    public Course() {

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

}
