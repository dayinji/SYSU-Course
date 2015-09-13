package com.badprinter.sysu_course.fragment;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.adapter.CourseAdapter;
import com.badprinter.sysu_course.model.Course;
import com.badprinter.sysu_course.model.CourseState;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

public class CourseList extends Fragment {
    private final String TAG = "CourseList";

    private View root;
    private ImageView bear;
    private ImageView cd;
    private TextView msg;
    private ListView courseListView;
    private PtrFrameLayout ptrFrame;
    private String url;
    private String cata;
    private List<Course> courseList;
    private CourseAdapter adapter;

    private ValueAnimator cdAnim;

    public static final CourseList newInstance(String url, String cata, List<Course> courseList) {
        CourseList fragment = new CourseList();
        fragment.setUrl(url);
        fragment.setCata(cata);
        fragment.setList(courseList);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root =  inflater.inflate(R.layout.fragment_course_list, container, false);
        findViewsById();

        initPtr();
        adapter = new CourseAdapter(courseList);
        adapter.onClickBt = new CourseAdapter.OnClickBt() {
            @Override
            public void onClickBt(int postion) {
                final Course c = courseList.get(postion);
                switch(c.getState()) {
                    case CANNOTUNSELECT:
                        break;
                    case CANNOTSELECT:
                        break;
                    case CANSELECT:
                        new SweetAlertDialog(getActivity())
                                .setTitleText("选课")
                                .setContentText("确定要选择" + c.getName() + "这门课吗")
                                .setConfirmText("是的")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog
                                                .setTitleText("选课成功!")
                                                .setContentText("你已经选择了" + c.getName() + "这门课")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    }
                                })
                                .setCancelText("取消")
                                .showCancelButton(true)
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.cancel();
                                    }
                                })
                                .show();
                        break;
                    case SELECTED:

                        break;
                    case SUCCEED:

                        break;
                }
            }
        };
        courseListView.setAdapter(adapter);

        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Course c = courseList.get(position);
                String state = "";
                switch(c.getState()) {
                    case CANNOTSELECT:
                        state = "不可选";
                        break;
                    case CANSELECT:
                        state = "可选";
                        break;
                    case SELECTED:
                        state = "带筛选";
                        break;
                    case SUCCEED:
                        state = "选课成功";
                        break;
                }
                String detail = "状态: " + state + "\n" +
                        "教师: " + c.getTeacher() + "\n" +
                        "学分: " + c.getCredit() + "\n" +
                        "课容量: " + c.getAllNum() + "\n" +
                        "待筛选: " + c.getCandidateNum() + "\n" +
                        "空位: " + c.getVacancyNum() + "\n" +
                        "选中率: " + c.getRate() + "\n" +
                        "时间地点: " + c.getTimePlace();
                new SweetAlertDialog(getActivity())
                        .setTitleText(c.getName())
                        .setContentText(detail)
                        .show();
            }
        });


        return root;
    }
    private void findViewsById() {
        bear = (ImageView)root.findViewById(R.id.bear);
        cd = (ImageView)root.findViewById(R.id.cd);
        msg = (TextView)root.findViewById(R.id.msg);
        courseListView = (ListView)root.findViewById(R.id.courseList);
        ptrFrame = (PtrFrameLayout)root.findViewById(R.id.ptrFrame);
    }

    private void initPtr() {
        ptrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //countAdapter.updateCount();
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrFrame.refreshComplete();
                    }
                }, 1000);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
        ptrFrame.addPtrUIHandler(new PtrUIHandler() {
            @Override
            public void onUIReset(PtrFrameLayout ptrFrameLayout) {
                msg.setText("下拉刷新");
                Log.e(TAG, "onUIReset");
            }

            @Override
            public void onUIRefreshPrepare(PtrFrameLayout ptrFrameLayout) {
                Log.e(TAG, "onUIRefreshPrepare");
            }

            @Override
            public void onUIRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                msg.setText("正在刷新...");
                cdAnim = ValueAnimator.ofFloat(0, 1);
                cdAnim.setDuration(700);
                cdAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        cd.setRotation((float) animation.getAnimatedValue() * 360);
                    }
                });
                cdAnim.setRepeatMode(ValueAnimator.RESTART);
                cdAnim.setRepeatCount(-1);
                cdAnim.setInterpolator(new LinearInterpolator());
                cdAnim.start();
                Log.e(TAG, "onUIRefreshBegin");
            }

            @Override
            public void onUIRefreshComplete(PtrFrameLayout ptrFrameLayout) {
                msg.setText("刷新成功");
                if (cdAnim != null && cdAnim.isRunning())
                    cdAnim.cancel();
                Log.e(TAG, "onUIRefreshComplete");
            }

            @Override
            public void onUIPositionChange(PtrFrameLayout ptrFrameLayout, boolean b, byte b1, PtrIndicator ptrIndicator) {

                int curY = ptrIndicator.getCurrentPosY();
                int offsetY = ptrIndicator.getOffsetToRefresh();
                int height = ptrIndicator.getHeaderHeight();

                cd.setRotation(curY * 10);
                cd.setScaleX(1 - (float) curY / height / 3);
                cd.setScaleY(1 - (float) curY / height / 3);
                bear.setScaleX((float) curY / height / 3 + 1);
                bear.setScaleY((float) curY / height / 3 + 1);
                float k = -(50+offsetY)/(float)offsetY;
                if (ptrIndicator.getCurrentPosY() <= ptrIndicator.getOffsetToRefresh()) {
                    cd.setY(curY * k + height + 50);
                    if (b)
                        msg.setText("下拉刷新");
                } else {
                    if (b)
                        msg.setText("松开刷新");
                }
            }
        });
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setCata(String cata) {
        this.cata = cata;
    }
    public void setList(List<Course> list) {
        this.courseList = list;
    }


}
