package com.badprinter.sysu_course.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.badprinter.sysu_course.Common.AppContext;
import com.badprinter.sysu_course.activity.Course;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by root on 15-8-27.
 */
public class DBManager {
    private final String TAG = "DBManager";

    private DBHelper helper;
    private SQLiteDatabase db;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.CHINA);

    public DBManager() {
        helper = new DBHelper(AppContext.getInstance());
        db = helper.getWritableDatabase();
    }

    /*****************************
     * Like Table
     ****************************/

    /**
     * Add a Course to Like Table
     */
    public void addToLike(com.badprinter.sysu_course.model.Course course, String cata) {
        if (isLike(course))
            return;
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO likecourses VALUES(null, ?, ?, ?, ?)",
                    new Object[]{course.getBid(), cata, course.getName(), course.getTeacher()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    /**
     * Judge Whether a Course Is in Like Table or Not
     */
    public boolean isLike(com.badprinter.sysu_course.model.Course course) {
        Cursor c;
        String[] args = {course.getBid()};
        db.beginTransaction();
        try {
            c =  db.rawQuery("SELECT * " +
                    "FROM likecourses " +
                    "WHERE bid = ?",args);
        } finally {
            db.endTransaction();
        }
        if (c.getCount() == 0) {
            c.close();
            return false;
        } else {
            c.close();
            return true;
        }
    }
    /**
     * Delete a Course from Like Table
     */
    public void deleteFromLike(com.badprinter.sysu_course.model.Course course) {
        if (!isLike(course))
            return;
        db.beginTransaction();
        try {
            db.delete("likecourses", "bid=?", new String[]{course.getBid()});
            db.setTransactionSuccessful();
        }  finally {
            db.endTransaction();
        }
    }
    /**
     * Query A Cursor from Like Table
     */
    public Cursor queryFromLike() {
        Cursor c;
        db.beginTransaction();
        try {
            c = db.rawQuery("SELECT * FROM likecourses", null);
        } finally {
            db.endTransaction();
        }
        return c;
    }

    /*****************************
     * Listened Table
     ****************************/

    /**
     * Add a Course to Listened Table
     */
    public void addToListened(com.badprinter.sysu_course.model.Course course, String cata) {
        if (isListened(course))
            return;
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO listenedcourses VALUES(null, ?, ?, ?, ?)",
                    new Object[]{course.getBid(), cata, course.getName(), course.getTeacher()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    /**
     * Judge Whether a Course Is in Listened Table or Not
     */
    public boolean isListened(com.badprinter.sysu_course.model.Course course) {
        String[] args = {course.getBid()};
        Cursor c;
        db.beginTransaction();
        try {
            c =  db.rawQuery("SELECT * " +
                    "FROM listenedcourses " +
                    "WHERE bid = ?",args);
        } finally {
            db.endTransaction();
        }
        if (c.getCount() == 0) {
            c.close();
            return false;
        } else {
            c.close();
            return true;
        }
    }
    /**
     * Delete a Course from Listened Table
     */
    public void deleteFromListened(com.badprinter.sysu_course.model.Course course) {
        if (!isListened(course))
            return;
        db.beginTransaction();
        try {
            db.delete("listenedcourses", "bid=?", new String[]{course.getBid()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    /**
     * Query A Cursor from Listened Table
     */
    public Cursor queryFromListened() {
        Cursor c;
        db.beginTransaction();
        try {
            c = db.rawQuery("SELECT * FROM listenedcourses", null);
        } finally {
            db.endTransaction();
        }
        return c;
    }


}
