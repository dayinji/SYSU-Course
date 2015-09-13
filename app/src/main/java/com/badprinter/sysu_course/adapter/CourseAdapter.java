package com.badprinter.sysu_course.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.Common.AppContext;
import com.badprinter.sysu_course.db.DBManager;
import com.badprinter.sysu_course.model.Course;

import java.util.List;
import java.util.Locale;

/**
 * Created by root on 15-9-12.
 */
public class CourseAdapter extends BaseAdapter {
    private final String TAG = "CourseAdapter";
    private List<Course> courseList;
    private DBManager dbMgr;

    public CourseAdapter(List<Course> list) {
        courseList = list;
        dbMgr = new DBManager();
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
            convertView = LayoutInflater.from(AppContext.getInstance()).inflate(R.layout.item_course, null);
            holder = new Holder();
            holder.name = (TextView)convertView.findViewById(R.id.name);
            holder.teacher = (TextView)convertView.findViewById(R.id.teacher);
            holder.credit = (TextView)convertView.findViewById(R.id.credit);
            //holder.bt = (Button)convertView.findViewById(R.id.bt);
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
    @Override
    public int getViewTypeCount() {
        return 6;
    }

    @Override
    public int getItemViewType(int position) {
        Course c = courseList.get(position);
        int op;
        int islike = dbMgr.isLike(c) ? 1 : 0;
        switch(c.getState()) {
            case CANNOTUNSELECT:
                op = 0;
                break;
            case CANNOTSELECT:
                op = 0;
                break;
            case CANSELECT:
                op = 1;
                break;
            case SELECTED:
               op = 2;
                break;
            case SUCCEED:
                op = 2;
                break;
            default:
                op = 0;
                break;
        }
        return 3*islike+op;
    }

    private class Holder {
        public TextView name;
        public TextView teacher;
        public TextView credit;
        //private Button bt;
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
