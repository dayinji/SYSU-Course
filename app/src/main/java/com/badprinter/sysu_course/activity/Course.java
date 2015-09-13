package com.badprinter.sysu_course.activity;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.customview.DragView;
import com.badprinter.sysu_course.customview.MyViewPager;
import com.badprinter.sysu_course.fragment.CourseList;
import com.badprinter.sysu_course.util.CourseInfo;

import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class Course extends SwipeBackActivity {
    private final String TAG = "Course";

    private RadioButton myCourses;
    private RadioButton otherCourses;
    private RadioGroup tabs;
    private DragView dragView;
    private MyViewPager pager;
    private CourseList myCourseFragment;
    private CourseList otherCourseFragment;
    private List<com.badprinter.sysu_course.model.Course> myCourseList;
    private List<com.badprinter.sysu_course.model.Course> otherCourseList;
    private CourseInfo courseInfo;
    private String cata;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        url = getIntent().getStringExtra("url");
        cata = getIntent().getStringExtra("cata");

        findViewsById();
        setListener();
        // Init DragView
        dragView.setAnimation(0);

        ViewTreeObserver vto2 = dragView.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                dragView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                courseInfo = new CourseInfo();
                courseInfo.onGetCourseInfo = new CourseInfo.OnGetCourseInfo() {
                    @Override
                    public void onSucceed(List<com.badprinter.sysu_course.model.Course> myCourseList, List<com.badprinter.sysu_course.model.Course> otherCourseList) {
                        Course.this.myCourseList = myCourseList;
                        Course.this.otherCourseList = otherCourseList;
                        // Init Pager
                        initPager();
                    }

                    @Override
                    public void onFailed() {
                        Toast.makeText(Course.this, "加载数据失败", Toast.LENGTH_SHORT);
                    }
                };
                courseInfo.execute(url);
            }
        });
    }
    private void findViewsById() {
        myCourses = (RadioButton)findViewById(R.id.myCourses);
        otherCourses = (RadioButton)findViewById(R.id.otherCourses);
        tabs = (RadioGroup)findViewById(R.id.tabs);
        dragView = (DragView)findViewById(R.id.drag);
        pager = (MyViewPager)findViewById(R.id.pager);
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
                }
            }
        });
    }
    private void setWhiteText(RadioButton tab, int position) {
        otherCourses.setTextColor(getResources().getColor(R.color.qianhui));
        myCourses.setTextColor(getResources().getColor(R.color.qianhui));

        tab.setTextColor(getResources().getColor(R.color.qianbai));
    }
    private void initPager() {
        myCourseFragment = CourseList.newInstance(url, cata, myCourseList);
        otherCourseFragment = CourseList.newInstance(url, cata, otherCourseList);

        List<Fragment> list = new ArrayList<>();
        list.add(myCourseFragment);
        list.add(otherCourseFragment);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), list));
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                dragView.setAnimation(position);
                tabs.check(position);
                RadioButton[] tabs = {myCourses, otherCourses};
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
        @Override
        public void destroyItem (ViewGroup container, int position, Object object) {
            // Never Destroy Fragment for Preventing from Getting stuck!
            return;
        }
    }
}
