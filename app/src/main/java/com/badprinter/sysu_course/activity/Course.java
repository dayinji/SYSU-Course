package com.badprinter.sysu_course.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.badprinter.sysu_course.Constant.Constants;
import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.customview.DragView;
import com.badprinter.sysu_course.customview.MyViewPager;
import com.badprinter.sysu_course.fragment.CourseList;
import com.badprinter.sysu_course.http.CourseInfo;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class Course extends SwipeBackActivity {
    private final String TAG = "Course";

    private RadioButton myCourses;
    private RadioButton otherCourses;
    private RadioButton likeCourses;
    private RadioGroup tabs;
    private DragView dragView;
    private MyViewPager pager;
    private CourseList myCourseFragment;
    private CourseList otherCourseFragment;
    private CourseList likeCourseFragment;
    private List<com.badprinter.sysu_course.model.Course> myCourseList;
    private List<com.badprinter.sysu_course.model.Course> otherCourseList;
    private List<com.badprinter.sysu_course.model.Course> likeCourseList;
    private CourseInfo courseInfo;
    private String cata;
    private String url;
    private SweetAlertDialog dialog;
    private ImageView logo;

    private CourseReceiver courseReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        // Register Recevier
        courseReceiver = new CourseReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BoardAction.SUCCESSFUL_SELECTION);
        registerReceiver(courseReceiver, filter);

        url = getIntent().getStringExtra("url");
        cata = getIntent().getStringExtra("cata");

        findViewsById();
        setListener();
        // Init DragView
        dragView.setAnimation(0);

        if (cata.equals("公选")) {
            logo.setImageDrawable(getResources().getDrawable(R.drawable.gongxuan));
        } else if (cata.equals("专选")) {
            logo.setImageDrawable(getResources().getDrawable(R.drawable.zhuanxuan));
        } else if (cata.equals("公必")) {
            logo.setImageDrawable(getResources().getDrawable(R.drawable.gongbi));
        } else if (cata.equals("专必")) {
            logo.setImageDrawable(getResources().getDrawable(R.drawable.zhuanbi));
        }

        ViewTreeObserver vto2 = dragView.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                dragView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                logo.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                ViewGroup.LayoutParams lp = logo.getLayoutParams();
                lp.width = logo.getMeasuredHeight()*50/34;
                logo.setLayoutParams(lp);

                courseInfo = new CourseInfo();
                courseInfo.onGetCourseInfo = new CourseInfo.OnGetCourseInfo() {
                    @Override
                    public void onSucceed(List<com.badprinter.sysu_course.model.Course> myCourseList,
                                          List<com.badprinter.sysu_course.model.Course> otherCourseList,
                                          List<com.badprinter.sysu_course.model.Course> likeCourseList) {
                        Course.this.myCourseList = myCourseList;
                        Course.this.otherCourseList = otherCourseList;
                        Course.this.likeCourseList = likeCourseList;
                        // Init Pager
                        initPager();
                        dialog.cancel();
                    }

                    @Override
                    public void onFailed() {
                        dialog.setTitleText("加载数据失败")
                                .setContentText("当前不是选课阶段或账户失效请重新登陆")
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        //Toast.makeText(Course.this, "加载数据失败", Toast.LENGTH_SHORT);
                    }
                };
                // Show Dialog
                dialog = new SweetAlertDialog(Course.this);
                dialog.setTitleText("玩命加载数据")
                        .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                dialog.show();

                courseInfo.execute(url, cata);
            }
        });
    }

    @Override
     public void onDestroy() {
        unregisterReceiver(courseReceiver);
        super.onDestroy();
    }

    private void findViewsById() {
        myCourses = (RadioButton)findViewById(R.id.myCourses);
        otherCourses = (RadioButton)findViewById(R.id.otherCourses);
        likeCourses = (RadioButton)findViewById(R.id.likeCourses);
        tabs = (RadioGroup)findViewById(R.id.tabs);
        dragView = (DragView)findViewById(R.id.drag);
        pager = (MyViewPager)findViewById(R.id.pager);
        logo = (ImageView)findViewById(R.id.logo);
    }
    private void setListener() {
        tabs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.myCourses:
                        dragView.setAnimation(0);
                        pager.setCurrentItem(0, true);
                        setWhiteText(myCourses, 0);
                        break;
                    case R.id.otherCourses:
                        dragView.setAnimation(1);
                        pager.setCurrentItem(1, true);
                        setWhiteText(otherCourses, 1);
                        break;
                    case R.id.likeCourses:
                        dragView.setAnimation(2);
                        pager.setCurrentItem(2, true);
                        setWhiteText(likeCourses, 2);
                }
            }
        });
    }
    private void setWhiteText(RadioButton tab, int position) {
        otherCourses.setTextColor(getResources().getColor(R.color.qianhui));
        myCourses.setTextColor(getResources().getColor(R.color.qianhui));
        likeCourses.setTextColor(getResources().getColor(R.color.qianhui));

        tab.setTextColor(getResources().getColor(R.color.qianbai));
    }
    private void initPager() {
        myCourseFragment = CourseList.newInstance(url, cata, myCourseList);
        otherCourseFragment = CourseList.newInstance(url, cata, otherCourseList);
        likeCourseFragment = CourseList.newInstance(url, cata, likeCourseList);

        List<Fragment> list = new ArrayList<>();
        list.add(myCourseFragment);
        list.add(otherCourseFragment);
        list.add(likeCourseFragment);
        // Load All Pages And Never Destroy Pages
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), list));
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                dragView.setAnimation(position);
                tabs.check(position);
                RadioButton[] tabs = {myCourses, otherCourses, likeCourses};
                setWhiteText(tabs[position], position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pager.setAlwaysDrawnWithCacheEnabled(true);
        pager.setHorizontalFadingEdgeEnabled(false);
    }
    private class MyPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;
        public MyPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.fragmentList = list;
        }
        @Override
        public int getCount() {
            return fragmentList.size();
        }
        @Override
        public Fragment getItem(int arg0) {
            return fragmentList.get(arg0);
        }
        /*@Override
        public void destroyItem (ViewGroup container, int position, Object object) {
            // Never Destroy Fragment for Preventing from Getting stuck!
            return;
        }*/
    }
    public void updateCourseInfo() {
        // Show dialog
        dialog = new SweetAlertDialog(Course.this);
        dialog.setTitleText("正在更新数据")
                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        dialog.show();

        if (courseInfo != null && !courseInfo.isCancelled())
            courseInfo.cancel(true);
        courseInfo = new CourseInfo();
        courseInfo.onGetCourseInfo = new CourseInfo.OnGetCourseInfo() {
            @Override
            public void onSucceed(List<com.badprinter.sysu_course.model.Course> myCourseList,
                                  List<com.badprinter.sysu_course.model.Course> otherCourseList,
                                  List<com.badprinter.sysu_course.model.Course> likeCourseList) {
                Course.this.myCourseList = myCourseList;
                Course.this.otherCourseList = otherCourseList;
                Course.this.likeCourseList = likeCourseList;

                myCourseFragment.updateListView(myCourseList);
                otherCourseFragment.updateListView(otherCourseList);
                likeCourseFragment.updateListView(likeCourseList);
                dialog.cancel();
            }

            @Override
            public void onFailed() {
                dialog.setTitleText("加载数据失败")
                        .setContentText("请检查网络连接或者重新登陆")
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                //Toast.makeText(Course.this, "加载数据失败", Toast.LENGTH_SHORT);
            }
        };
        courseInfo.execute(url);
    }
    private class CourseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.BoardAction.SUCCESSFUL_SELECTION:
                    if (intent.getStringExtra("cata").equals(cata))
                        updateCourseInfo();
                    break;
            }
        }
    }

}
