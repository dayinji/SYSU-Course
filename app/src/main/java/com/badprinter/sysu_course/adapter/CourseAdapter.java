package com.badprinter.sysu_course.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.Common.AppContext;
import com.badprinter.sysu_course.model.Course;

import java.util.List;

/**
 * Created by root on 15-9-12.
 */
public class CourseAdapter extends BaseAdapter {
    private final String TAG = "CourseAdapter";
    private List<Course> courseList;
    public OnClickBt onClickBt;

    public CourseAdapter(List<Course> list) {
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
            convertView = LayoutInflater.from(AppContext.getInstance()).inflate(R.layout.item_course, null);
            holder = new Holder();
            holder.name = (TextView)convertView.findViewById(R.id.name);
            holder.teacher = (TextView)convertView.findViewById(R.id.teacher);
            holder.credit = (TextView)convertView.findViewById(R.id.credit);
            holder.bt = (Button)convertView.findViewById(R.id.bt);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        Course c = courseList.get(position);
        if (c.getName().length() > 6)
            holder.name.setText(c.getName().substring(0, 6) + "...");
        else
            holder.name.setText(c.getName());
        if (c.getTeacher() == null || c.getTeacher().equals(""))
            c.setTeacher("(未知)");
        holder.teacher.setText(c.getTeacher());
        holder.credit.setText(c.getCredit());
        switch(c.getState()) {
            case CANNOTUNSELECT:
                holder.bt.setText("无");
                holder.bt.setEnabled(false);
                holder.bt.setBackgroundResource(R.drawable.round_bt_default);
                break;
            case CANNOTSELECT:
                holder.bt.setText("无");
                holder.bt.setEnabled(false);
                holder.bt.setBackgroundResource(R.drawable.round_bt_default);
                break;
            case CANSELECT:
                holder.bt.setText("选课");
                holder.bt.setBackgroundResource(R.drawable.round_bt);
                break;
            case SELECTED:
                holder.bt.setText("退课");
                holder.bt.setBackgroundResource(R.drawable.round_bt_warn);
                break;
            case SUCCEED:
                holder.bt.setText("退课");
                holder.bt.setBackgroundResource(R.drawable.round_bt_warn);
                break;
        }
        holder.bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBt.onClickBt(position);
            }
        });
        return convertView;
    }

    private class Holder {
        public TextView name;
        public TextView teacher;
        public TextView credit;
        private Button bt;
    }
    public interface OnClickBt {
        void onClickBt(int postion);
    }

}
