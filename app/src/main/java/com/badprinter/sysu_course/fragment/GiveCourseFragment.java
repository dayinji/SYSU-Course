package com.badprinter.sysu_course.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.activity.SafeExchange;
import com.badprinter.sysu_course.adapter.CourseAdapter;
import com.badprinter.sysu_course.adapter.GivenCourseAdapter;
import com.badprinter.sysu_course.http.ElectCourse;
import com.badprinter.sysu_course.http.UnelectCourse;
import com.badprinter.sysu_course.model.Course;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class GiveCourseFragment extends Fragment {
    private final String TAG= "GiveCourseFragment";
    private List<Course> myCourseList;
    private ListView courseListView;
    private SweetAlertDialog dialog;
    private UnelectCourse unelectCourse;
    private int currentCourseId;
    private Timer timer;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG, "getMsg : count = " + count);
            if (msg.what == 1) {
                if (dialog.isShowing()) {
                    if (count > 0) {
                        dialog.setTitleText(count + "秒后将退课")
                                .setCancelText("反悔")
                                .showCancelButton(true)
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        timer.cancel();
                                        Log.e(TAG, "timer cancel");
                                        dialog.cancel();
                                    }
                                });
                        dialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        count--;
                    } else {
                        dialog.cancel();
                        timer.cancel();
                        unelectCourse();
                    }
                }
            }
        }
    };
    private int count = 6;

    private View root;

    public static final GiveCourseFragment newInstance(List<Course> courseList) {
        GiveCourseFragment fragment = new GiveCourseFragment();
        fragment.setList(courseList);
        return fragment;
    }

    public GiveCourseFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_give_course, container, false);
        findViewsById();

        courseListView.setAdapter(new GivenCourseAdapter(myCourseList));
        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (dialog != null)
                    dialog.cancel();
                dialog = new SweetAlertDialog(getActivity());
                dialog.setTitleText("让出" + myCourseList.get(position).getName() + "课程")
                        .setContentText("请把让对方输入" + myCourseList.get(position).getBid() + "课程号\n\n" +
                                "并同时按下[给课]&[取课]按钮,时差在3秒之内为宜")
                        .setConfirmText("给课")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                currentCourseId = position;
                                dialog.cancel();
                                showCountdownDialog();
                            }
                        })
                        .showCancelButton(true)
                        .setCancelText("老子不给了")
                        .show();
            }
        });
        return root;
    }

    @Override
    public void onDetach() {
        if (timer != null)
            timer.cancel();
        super.onDetach();
    }

    private void findViewsById() {
        courseListView = (ListView)root.findViewById(R.id.myCoursesList);
    }

    public void setList(List<Course> courseList) {
        myCourseList = courseList;
    }

    private void showCountdownDialog() {
        count = 6;
        if (dialog != null)
            dialog.cancel();
        dialog = new SweetAlertDialog(getActivity());
        dialog.setTitleText(count + "秒后将退课")
              .show();
        if (timer != null)
            timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        }, 0, 1000);
    }
    private void unelectCourse() {
        if (dialog != null)
            dialog.cancel();
        dialog = new SweetAlertDialog(getActivity());
        dialog.setTitleText("正在给课")
                .showCancelButton(false)
                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        dialog.show();

        unelectCourse = new UnelectCourse();
        unelectCourse.onSelected = new UnelectCourse.OnSelected() {
            @Override
            public void onFinished(Integer code) {
                dialog.setTitleText("给课成功")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                ((SafeExchange)getActivity()).updateMyCourses();
                                dialog.cancel();
                            }
                        })
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            }
        };
        unelectCourse.execute(myCourseList.get(currentCourseId).getBid(),
                myCourseList.get(currentCourseId).getCata());
    }

    public void updateListView(final List<Course> courseList) {
        this.myCourseList = courseList;
        courseListView.setAdapter(new GivenCourseAdapter(myCourseList));
    }

}
