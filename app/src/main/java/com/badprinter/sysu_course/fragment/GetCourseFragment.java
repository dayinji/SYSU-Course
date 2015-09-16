package com.badprinter.sysu_course.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.activity.SafeExchange;
import com.badprinter.sysu_course.http.ElectCourse;

import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class GetCourseFragment extends Fragment {
    private final String TAG="GetCourseFragment";

    private View root;
    private RadioGroup cataGroup;
    private Button okBt;
    private EditText bidEt;
    private SweetAlertDialog dialog;
    private String cata;
    private String bid;
    private ElectCourse electCourse;
    private Timer timer;
    private int count = 11;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG, "getMsg : count = " + count);
            if (msg.what == 1) {
                if (dialog.isShowing()) {
                    if (count > 0) {
                        count--;
                    } else {
                        dialog.cancel();
                        dialog = new SweetAlertDialog(getActivity());
                        dialog.setTitleText("取课失败")
                                .setContentText("请确认对方已退课或网络连接正常")
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        dialog.show();
                        timer.cancel();
                        if (!electCourse.isCancelled())
                            electCourse.cancel(true);
                    }
                }
            }
        }
    };

    public GetCourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDetach() {
        if (timer != null)
            timer.cancel();
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_get_course, container, false);
        findViewsById();
        setListener();

        return root;
    }
    private void findViewsById() {
        cataGroup = (RadioGroup)root.findViewById(R.id.cataGroup);
        okBt = (Button)root.findViewById(R.id.okBt);
        bidEt = (EditText)root.findViewById(R.id.bidEt);

    }
    private void setListener() {
        okBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bidEt.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "请输入课程号", Toast.LENGTH_SHORT).show();
                    return;
                }
                bid = bidEt.getText().toString();
                if (cataGroup.getCheckedRadioButtonId() == R.id.gongxuanBt)
                    cata = "公选";
                else if (cataGroup.getCheckedRadioButtonId() == R.id.zhuanxuanBt)
                    cata = "专选";
                else if (cataGroup.getCheckedRadioButtonId() == R.id.gongbiBt)
                    cata = "公必";
                dialog = new SweetAlertDialog(getActivity());
                dialog.setTitleText("取课")
                        .setContentText("课程号为" + bid + "\n" +
                                "类别为" + cata + "\n\n" +
                                "确认无误后请和对方同时按下[取课]&[选课]按钮,时差在3秒内为宜")
                        .showCancelButton(true)
                        .setCancelText("不取了")
                        .setConfirmText("取课")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                count = 11;
                                // Set CountDown
                                if (timer != null)
                                    timer.cancel();
                                timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        handler.sendEmptyMessage(1);
                                    }
                                }, 0, 1000);
                                // Set Dialog
                                dialog.cancel();
                                dialog = new SweetAlertDialog(getActivity());
                                dialog.setTitleText("正在取课中...")
                                        .setCancelText("反悔")
                                        .showCancelButton(true)
                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                timer.cancel();
                                                if (!electCourse.isCancelled())
                                                    electCourse.cancel(true);
                                                Log.e(TAG, "timer cancel");
                                                dialog.cancel();
                                            }
                                        });
                                dialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                                dialog.show();
                                electCourse();
                            }
                        });
                dialog.show();

            }
        });
    }
    private void electCourse() {
        // Elect course
        electCourse = new ElectCourse();
        electCourse.onSelected = new ElectCourse.OnSelected() {
            @Override
            public void onFinished(Integer code) {
                // Secceed
                if (code == 0) {
                    timer.cancel();
                    dialog.setTitleText("取课成功")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    dialog.cancel();
                                    ((SafeExchange)getActivity()).updateMyCourses();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                } else {
                    electCourse();
                }
            }
        };
        electCourse.execute(bid, cata);
    }


}
