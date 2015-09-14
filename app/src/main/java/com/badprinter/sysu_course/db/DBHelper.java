package com.badprinter.sysu_course.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by root on 15-8-27.
 */
public class DBHelper extends SQLiteOpenHelper {
    private final String TAG = "DBHelper";

    private static final String DATABASE_NAME = "sysy_course.db";
    private static final int DATABASE_VERSION = 2;

    /*
     * My Favorite Courses Table
     */
    final String SQL_CREATE_TABLE_LIKE_COURSES = "CREATE TABLE IF NOT EXISTS likecourses (" +
            "_id integer primary key autoincrement, " +
            "bid varchar(100), " +
            "cata varchar(100), " +
            "name varchar(100), " +
            "teacher varchar(100)) ";
    /*
     * My Listened Courses Table
     */
    final String SQL_CREATE_TABLE_LISTENED_COURSES = "CREATE TABLE IF NOT EXISTS listenedcourses (" +
            "_id integer primary key autoincrement, " +
            "bid varchar(100), " +
            "cata varchar(100), " +
            "name varchar(100), " +
            "teacher varchar(100)) ";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_LIKE_COURSES);
        db.execSQL(SQL_CREATE_TABLE_LISTENED_COURSES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "onUpgrade");
        //db.execSQL("DROP TABLE IF EXISTS "+"likecourses");
        onCreate(db);
    }
}
