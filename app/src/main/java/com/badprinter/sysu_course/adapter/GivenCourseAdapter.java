package com.badprinter.sysu_course.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badprinter.sysu_course.Common.AppContext;
import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.db.DBManager;
import com.badprinter.sysu_course.model.Course;

import java.util.List;

/**
 * Created by root on 15-9-16.
 */
public class GivenCourseAdapter extends BaseAdapter {
    private final String TAG = "GivenCourseAdapter";
    private List<Course> courseList;

    public GivenCourseAdapter(List<Course> list) {
        courseList = list;
    }
    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public Object getItem(int position) {
        return courseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(AppContext.getInstance()).inflate(R.layout.item_given_course, null);
            holder = new Holder();
            holder.name = (TextView)convertView.findViewById(R.id.name);
            holder.teacher = (TextView)convertView.findViewById(R.id.teacher);
            holder.credit = (TextView)convertView.findViewById(R.id.credit);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        Course c = courseList.get(position);
        if (c.getName().length() > 9)
            holder.name.setText(c.getName().substring(0, 9) + "...");
        else
            holder.name.setText(c.getName());
        if (c.getTeacher() == null || c.getTeacher().equals(""))
            c.setTeacher("(未知)");
        holder.teacher.setText(c.getTeacher());
        holder.credit.setText(c.getCredit());
        return convertView;
    }

    private class Holder {
        public TextView name;
        public TextView teacher;
        public TextView credit;
    }


    public char getLetterByPosition(int position) {
        char[] pinyin = courseList.get(position).getPinyin().toCharArray();
        if (pinyin[0] <= 'Z' &&  pinyin[0] >= 'A')
            return pinyin[0];
        else
            return '#';
    }
    public int getPositionByLetter(char letter) {

        int index = findByBinarySearch(0, courseList.size() - 1, letter);
        // When Find Nothing
        if (index == -1 && letter != 'A') {
            return getPositionByLetter((char)(letter-1));
        }
        else  if (index == -1 && letter == 'A') {
            return getPositionByLetter('#');
        }
        // When found
        for (int i = index ; i > 0 ; i --) {
            char[] pinyin = courseList.get(i-1).getPinyin().toCharArray();
            if (pinyin[0] != letter) {
                return i;
            }
            if (i == 1) {
                return 0;
            }
        }
        return 0;
    }
    private int findByBinarySearch(int start, int end, char letter) {
        if (letter == '#')
            return 0;
        if (end < start)
            return -1;
        int middle = (end + start)/2;
        char[] pinyin = courseList.get(middle).getPinyin().toCharArray();
        if (pinyin[0] > letter) {
            return findByBinarySearch(start, middle - 1, letter);
        } else if (pinyin[0] < letter) {
            return findByBinarySearch(middle + 1, end, letter);
        } else {
            return middle;
        }
    }


}
