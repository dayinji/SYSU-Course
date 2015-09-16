package com.badprinter.sysu_course.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.badprinter.sysu_course.Common.AppContext;
import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.customview.MyViewPager;
import com.badprinter.sysu_course.fragment.CourseList;
import com.badprinter.sysu_course.fragment.GetCourseFragment;
import com.badprinter.sysu_course.fragment.GiveCourseFragment;
import com.badprinter.sysu_course.http.CourseInfoByBids;
import com.badprinter.sysu_course.http.GivenCourse;
import com.badprinter.sysu_course.model.*;
import com.badprinter.sysu_course.model.Course;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class SafeExchange extends SwipeBackActivity implements View.OnClickListener{

    private final String TAG = "SafeExchange";
    private Button geikeBt;
    private Button qukeBt;
    private MyViewPager pager;
    private GiveCourseFragment giveCourseFragment;
    private GetCourseFragment getCourseFragment;
    private GivenCourse givenCourse;
    private SweetAlertDialog dialog;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_exchange);

        findViewsById();
        setListener();

        getCourseFragment = new GetCourseFragment();

        givenCourse = new GivenCourse();
        givenCourse.onGetGivenCourse = new GivenCourse.OnGetGivenCourse() {
            @Override
            public void onSucceed(List<Course> myCourseList) {
                giveCourseFragment = GiveCourseFragment.newInstance(myCourseList);
                Log.e(TAG, "get give successful");
                initPager();
                if (dialog != null)
                    dialog.cancel();
            }

            @Override
            public void onFailed() {
                Log.e(TAG, "get give failed");
            }
        };
        dialog = new SweetAlertDialog(SafeExchange.this);
        dialog.setTitleText("正在加载可退课程")
                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        dialog.show();
        givenCourse.execute();

        ViewTreeObserver vto2 = logo.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                logo.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                // Reset Logo Size
                logo.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                ViewGroup.LayoutParams lp = logo.getLayoutParams();
                lp.width = logo.getMeasuredHeight() * 50 / 34;
                logo.setLayoutParams(lp);
            }
        });

    }

    private void findViewsById() {
        geikeBt = (Button)findViewById(R.id.geikeBt);
        qukeBt = (Button)findViewById(R.id.qukeBt);
    }
    private void setListener() {
        geikeBt.setOnClickListener(this);
        qukeBt.setOnClickListener(this);
        pager = (MyViewPager)findViewById(R.id.pager);
        logo = (ImageView)findViewById(R.id.logo);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.geikeBt :
                pager.setCurrentItem(0, true);
                geikeBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_bg_shenlan));
                qukeBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_bt));
                break;
            case R.id.qukeBt:
                pager.setCurrentItem(1, true);
                qukeBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_bg_shenlan));
                geikeBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_bt));
                break;
        }
    }
    private void initPager() {

        List<Fragment> list = new ArrayList<>();
        list.add(giveCourseFragment);
        list.add(getCourseFragment);
        Log.e(TAG, "get == null : " + (getCourseFragment == null));
        Log.e(TAG, "give == null : " + (giveCourseFragment == null));
        // Load All Pages And Never Destroy Pages
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), list));
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

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
    public void updateMyCourses() {
        givenCourse = new GivenCourse();
        givenCourse.onGetGivenCourse = new GivenCourse.OnGetGivenCourse() {
            @Override
            public void onSucceed(List<Course> myCourseList) {
                //giveCourseFragment = GiveCourseFragment.newInstance(myCourseList);
                giveCourseFragment.updateListView(myCourseList);
                Log.e(TAG, "get give successful");
                dialog.cancel();
            }

            @Override
            public void onFailed() {
                Log.e(TAG, "get give failed");
            }
        };
        dialog = new SweetAlertDialog(SafeExchange.this);
        dialog.setTitleText("正在更新数据")
                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        dialog.show();
        givenCourse.execute();
    }
}
