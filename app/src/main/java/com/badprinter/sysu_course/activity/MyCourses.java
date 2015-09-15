package com.badprinter.sysu_course.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.fragment.CourseList;
import com.badprinter.sysu_course.model.Course;
import com.badprinter.sysu_course.http.MyCoursesResult;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class MyCourses extends SwipeBackActivity {
    private final String TAG = "MyCourses";

    private FrameLayout frameLayout;
    private ImageView logo;
    private MyCoursesResult myCoursesResult;
    private SweetAlertDialog dialog;
    private CourseList courseListFragment;
    private List<Course> courseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);

        courseList = new ArrayList<>();

        findViewsById();

        ViewTreeObserver vto2 = frameLayout.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                frameLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                // Reset Logo Size
                logo.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                ViewGroup.LayoutParams lp = logo.getLayoutParams();
                lp.width = logo.getMeasuredHeight();
                logo.setLayoutParams(lp);

                myCoursesResult = new MyCoursesResult();
                myCoursesResult.onGetMyCourses = new MyCoursesResult.OnGetMyCourses() {
                    @Override
                    public void onSucceed(List<Course> list) {
                        courseList = list;
                        courseListFragment = CourseList.newInstance("", "MyCourses", courseList);
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction t = fm.beginTransaction();
                        t.replace(R.id.frameLayout, courseListFragment);
                        t.commit();
                        dialog.cancel();
                    }

                    @Override
                    public void onFailed() {
                        dialog.setTitleText("加载数据失败")
                                .setContentText("网络问题或账户失效请重新登陆")
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    }
                };

                // Show Dialog
                dialog = new SweetAlertDialog(MyCourses.this);
                dialog.setTitleText("玩命加载数据")
                        .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                dialog.show();
                myCoursesResult.execute();
            }
        });
    }
    private void findViewsById() {
        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        logo = (ImageView)findViewById(R.id.logo);

    }
    public void updateCourseInfo() {
        // Show dialog
        dialog = new SweetAlertDialog(MyCourses.this);
        dialog.setTitleText("正在更新数据")
                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        dialog.show();

        if (myCoursesResult != null && !myCoursesResult.isCancelled())
            myCoursesResult.cancel(true);
        myCoursesResult = new MyCoursesResult();
        myCoursesResult.onGetMyCourses = new MyCoursesResult.OnGetMyCourses() {
            @Override
            public void onSucceed(List<Course> list) {
                courseListFragment = CourseList.newInstance("", "MyCourses", list);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction t = fm.beginTransaction();
                t.replace(R.id.frameLayout, courseListFragment);
                t.commit();
                dialog.cancel();
            }

            @Override
            public void onFailed() {
                dialog.setTitleText("加载数据失败")
                        .setContentText("当前不是选课阶段或账户失效请重新登陆")
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
        };
        //myCoursesResult.execute();
        myCoursesResult.execute();
    }
}
