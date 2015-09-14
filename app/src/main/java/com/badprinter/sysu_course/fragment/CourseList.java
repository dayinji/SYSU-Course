package com.badprinter.sysu_course.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

import com.badprinter.sysu_course.Constant.Constants;
import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.adapter.CourseAdapter;
import com.badprinter.sysu_course.customview.PinyinBar;
import com.badprinter.sysu_course.db.DBManager;
import com.badprinter.sysu_course.model.Course;
import com.badprinter.sysu_course.model.CourseState;
import com.badprinter.sysu_course.util.DisplayUtil;
import com.badprinter.sysu_course.util.ElectCourse;
import com.badprinter.sysu_course.util.UnelectCourse;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CourseList extends Fragment {
    private final String TAG = "CourseList";

    private View root;
    private SwipeMenuListView courseListView;
    private String url;
    private String cata;
    private List<Course> courseList;
    private CourseAdapter adapter;
    private SweetAlertDialog dialog;
    private ElectCourse electCourse;
    private UnelectCourse unelectCourse;
    private PinyinBar pinyinBar;
    private TextView selectorText;
    private ObjectAnimator fadeSelectorAnim;
    private DBManager dbMgr;

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

        initMenu();
        initPinyinBar();
        dbMgr = new DBManager();

        courseListView.setVerticalScrollBarEnabled(false);
        updateListView(courseList);


        return root;
    }

    /**
     * Find All Views
     */
    private void findViewsById() {
        courseListView = (SwipeMenuListView)root.findViewById(R.id.courseList);
        pinyinBar = (PinyinBar)root.findViewById(R.id.pinyinBar);
        selectorText = (TextView)root.findViewById(R.id.selectorText);
    }

    /**
     * Init ElectCourse
     * @param electCourse
     */
    private void initElectCourse(ElectCourse electCourse) {
        electCourse.onSelected = new ElectCourse.OnSelected() {
            @Override
            public void onFinished(Integer code) {
                Log.e(TAG, "finished, code = " + code);
                if (code == 0) {
                    dialog.setTitleText("选课成功!")
                            .setContentText("你已经成功选择这门课")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    // Update ListView
                                    ((com.badprinter.sysu_course.activity.Course)getActivity())
                                        .updateCourseInfo();
                                    dialog.cancel();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                } else {
                    dialog.setTitleText("选课失败!")
                            .setContentText(Constants.ERROR_MSG[code])
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    dialog.cancel();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                }
            }
        };
    }

    /**
     * Init UnelectCourse
     * @param unelectCourse
     */
    private void initUnelectCourse(UnelectCourse unelectCourse) {
        unelectCourse.onSelected = new UnelectCourse.OnSelected() {
            @Override
            public void onFinished(Integer code) {
                Log.e(TAG, "finished, code = " + code);
                if (code == 0) {
                    dialog.setTitleText("退课成功!")
                            .setContentText("你已经成功退了这门课")
                            .setConfirmText("OK")
                            .showCancelButton(false)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    // Update ListView
                                    ((com.badprinter.sysu_course.activity.Course)getActivity())
                                            .updateCourseInfo();
                                    dialog.cancel();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                } else {
                    dialog.setTitleText("退课失败!")
                            .setContentText(Constants.ERROR_MSG[code])
                            .setConfirmText("OK")
                            .showCancelButton(false)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    dialog.cancel();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                }
            }
        };
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

    /**
     * Update ListView
     * @param courseList
     */
    public void updateListView(final List<Course> courseList) {
        this.courseList = courseList;
        adapter = new CourseAdapter(courseList);
        courseListView.setAdapter(adapter);

        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Course c = courseList.get(position);
                String state = "";
                switch (c.getState()) {
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
                    case CANNOTUNSELECT:
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
    }

    /**
     * Init Pinyin Bar
     */
    private void initPinyinBar() {
        fadeSelectorAnim = ObjectAnimator.ofFloat(selectorText, "alpha", 1f, 0f).setDuration(300);
        fadeSelectorAnim.setStartDelay(300);

        pinyinBar.callback = new PinyinBar.PinyinBarCallBack() {
            @Override
            public void onBarChange(int current) {
                if (courseList.size() != 0) {

                    char[] letters = {'#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
                            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
                            'Y', 'Z'
                    };
                    int position = adapter.getPositionByLetter(letters[current]);
                    courseListView.setSelection(position);
                    if (fadeSelectorAnim != null && fadeSelectorAnim.isRunning())
                        fadeSelectorAnim.cancel();
                    selectorText.setAlpha(1);
                    String name = courseList.get(position).getName();
                    selectorText.setText(name.substring(0, 1));
                    fadeSelectorAnim.start();
                }
            }
        };
    }

    /**
     * Init Menu
     */
    private void initMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                switch (menu.getViewType() % 4) {
                    case 0:
                    case 3 :
                        SwipeMenuItem opItem_0 = new SwipeMenuItem(
                                getActivity());
                        opItem_0.setBackground(R.color.qianhui);
                        opItem_0.setWidth(DisplayUtil.dp2px(90));
                        opItem_0.setTitle("无");
                        opItem_0.setTitleSize(16);
                        opItem_0.setTitleColor(Color.WHITE);
                        menu.addMenuItem(opItem_0);
                        break;
                    case 1:
                        SwipeMenuItem opItem_1 = new SwipeMenuItem(
                                getActivity());
                        opItem_1.setBackground(R.color.qingse);
                        opItem_1.setWidth(DisplayUtil.dp2px(90));
                        opItem_1.setTitle("选课");
                        opItem_1.setTitleSize(16);
                        opItem_1.setTitleColor(Color.WHITE);
                        menu.addMenuItem(opItem_1);
                        break;
                    case 2:
                        SwipeMenuItem opItem_2 = new SwipeMenuItem(
                                getActivity());
                        opItem_2.setBackground(R.color.qianhong);
                        opItem_2.setWidth(DisplayUtil.dp2px(90));
                        opItem_2.setTitle("退课");
                        opItem_2.setTitleSize(16);
                        opItem_2.setTitleColor(Color.WHITE);
                        menu.addMenuItem(opItem_2);
                        break;
                }
                // Just for
                SwipeMenuItem lineItem = new SwipeMenuItem(
                        getActivity());
                lineItem.setBackground(R.color.qianbai);
                lineItem.setWidth(DisplayUtil.dp2px(1));
                menu.addMenuItem(lineItem);
                switch (menu.getViewType() / 4) {
                    case 0 :
                        // like Button
                        SwipeMenuItem unlikeItem = new SwipeMenuItem(
                                getActivity());
                        unlikeItem.setBackground(R.color.lanse);
                        unlikeItem.setWidth(DisplayUtil.dp2px(90));
                        unlikeItem.setTitle("关注");
                        unlikeItem.setTitleSize(16);
                        unlikeItem.setTitleColor(Color.WHITE);
                        menu.addMenuItem(unlikeItem);
                        break;
                    case 1 :
                    case 2 :
                        // Unlike Button
                        SwipeMenuItem likeItem = new SwipeMenuItem(
                                getActivity());
                        likeItem.setBackground(R.color.lanse);
                        likeItem.setWidth(DisplayUtil.dp2px(90));
                        likeItem.setTitle("取关");
                        likeItem.setTitleSize(16);
                        likeItem.setTitleColor(Color.WHITE);
                        menu.addMenuItem(likeItem);
                      //  if (menu.getViewType() % 4 == 3) {
                            // Just for
                            SwipeMenuItem lineItem1 = new SwipeMenuItem(
                                    getActivity());
                            lineItem1.setBackground(R.color.qianbai);
                            lineItem1.setWidth(DisplayUtil.dp2px(1));
                            menu.addMenuItem(lineItem1);
                            // Listener or Unlistened Button
                            SwipeMenuItem listenItem = new SwipeMenuItem(
                                    getActivity());
                            listenItem.setWidth(DisplayUtil.dp2px(90));
                            if (menu.getViewType() / 4 == 1) {
                                listenItem.setTitle("监听");
                                listenItem.setBackground(R.color.qianhong);
                            } else if (menu.getViewType() / 4 == 2){
                                listenItem.setTitle("取消监听");
                                listenItem.setBackground(R.color.qianhong);
                            }
                            listenItem.setTitleSize(16);
                            listenItem.setTitleColor(Color.WHITE);
                            menu.addMenuItem(listenItem);
                     //   }
                        break;
                }

            }
        };

        // set creator
        courseListView.setMenuCreator(creator);

        courseListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                final Course c = courseList.get(position);
                switch (index) {
                    case 0:
                        switch(c.getState()) {
                            case CANNOTUNSELECT:
                                break;
                            case CANNOTSELECT:
                                break;
                            case CANSELECT:
                                showSelectDialog(c);
                                break;
                            case SELECTED:
                                showUnselectDialog(c);
                                break;
                            case SUCCEED:
                                showUnselectDialog(c);
                                break;
                        }
                        break;
                    case 2:
                        if (!dbMgr.isLike(c))
                            showLikeDialog(c);
                        else
                            showUnlikeDialog(c);
                        break;
                    case 4:
                        switch(c.getState()) {
                            case CANNOTUNSELECT:
                                showCannotListenedDialog("本门课程已经选课成功,无需监听");
                                break;
                            case CANNOTSELECT:
                                if (!dbMgr.isListened(c))
                                    showListenedDialog(c);
                                else
                                    showUnlistenedDialog(c);
                                break;
                            case CANSELECT:
                              //  showCannotListenedDialog("本门课程可以选择,无需监听");
                                if (!dbMgr.isListened(c))
                                    showListenedDialog(c);
                                else
                                    showUnlistenedDialog(c);
                                break;
                            case SELECTED:
                                showCannotListenedDialog("本门课程已经选课成功,无需监听");
                                break;
                            case SUCCEED:
                                showCannotListenedDialog("本门课程已经选课成功,无需监听");
                                break;
                        }
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }


    /***************************************
     * The Codes Below Are All About Dialogs
     **************************************/


    /**
     * Show A CannotListen Dialog
     * @param msg
     */
    private void showCannotListenedDialog(String msg) {
        if (dialog != null && dialog.isShowing())
            dialog.cancel();
        dialog = new SweetAlertDialog(getActivity());
        dialog.setTitleText("无需监听")
                .setContentText(msg)
                .show();
    }

    /**
     * Show A Listen Dialog
     * @param course
     */
    private void showListenedDialog(final Course course) {
        if (dialog != null && dialog.isShowing())
            dialog.cancel();
        dialog = new SweetAlertDialog(getActivity());
        dialog.setTitleText("监听课程")
                .setContentText("确定要监听" + course.getName() + "这门课吗")
                .setConfirmText("是的")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        dbMgr.addToListened(course, cata);
                        sDialog
                                .setTitleText("已加入监听队列")
                                .showCancelButton(false)
                                .setContentText("")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        ((com.badprinter.sysu_course.activity.Course) getActivity())
                                                .updateCourseInfo();
                                        dialog.cancel();
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                })
                .setCancelText("取消")
                .showCancelButton(true)
                .show();
    }

    /**
     * Show An Unlistened Dialog
     * @param course
     */
    private void showUnlistenedDialog(final Course course) {
        if (dialog != null && dialog.isShowing())
            dialog.cancel();
        dialog = new SweetAlertDialog(getActivity());
        dialog.setTitleText("取消监听")
                .setContentText("确定要取消监听" + course.getName() + "这门课吗")
                .setConfirmText("是的")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        dbMgr.deleteFromListened(course);
                        sDialog
                                .setTitleText("取消监听成功")
                                .showCancelButton(false)
                                .setContentText("")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        ((com.badprinter.sysu_course.activity.Course) getActivity())
                                                .updateCourseInfo();
                                        dialog.cancel();
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                })
                .setCancelText("取消")
                .showCancelButton(true)
                .show();
    }

    /**
     * Show A Select Dialog
     * @param c
     */
    private void showSelectDialog(final Course c) {
        if (dialog != null && dialog.isShowing())
            dialog.cancel();
        dialog = new SweetAlertDialog(getActivity());
        dialog.setTitleText("选课")
                .setContentText("确定要选择" + c.getName() + "这门课吗")
                .setConfirmText("是的")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog
                                .setTitleText("选课ing...")
                                .setContentText("")
                                .showCancelButton(false)
                                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        if (electCourse != null && !electCourse.isCancelled())
                            electCourse.cancel(true);
                        electCourse = new ElectCourse();
                        initElectCourse(electCourse);
                        electCourse.execute(c.getBid(), cata);
                    }
                })
                .showCancelButton(true)
                .show();
    }

    /**
     * Show An Unselect Dialog
     * @param c
     */
    private void showUnselectDialog(final Course c) {
        if (dialog != null && dialog.isShowing())
            dialog.cancel();
        dialog = new SweetAlertDialog(getActivity());
        dialog.setTitleText("退课")
                .setContentText("确定要退掉" + c.getName() + "这门课吗")
                .setConfirmText("是的")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog
                                .setTitleText("退课ing...")
                                .showCancelButton(false)
                                .setContentText("")
                                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        if (unelectCourse != null && !unelectCourse.isCancelled())
                            unelectCourse.cancel(true);
                        unelectCourse = new UnelectCourse();
                        initUnelectCourse(unelectCourse);
                        unelectCourse.execute(c.getBid(), cata);
                    }
                })
                .showCancelButton(true)
                .show();
    }

    /**
     * Show A Like Dialog
     * @param c
     */
    private void showLikeDialog(final Course c) {
        if (dialog != null && dialog.isShowing())
            dialog.cancel();
        dialog = new SweetAlertDialog(getActivity());
        dialog.setTitleText("关注")
                .setContentText("确定要关注" + c.getName() + "这门课吗")
                .setConfirmText("是的")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        dbMgr.addToLike(c, cata);
                        sDialog
                                .setTitleText("关注成功")
                                .showCancelButton(false)
                                .setContentText("")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        ((com.badprinter.sysu_course.activity.Course) getActivity())
                                                .updateCourseInfo();
                                        dialog.cancel();
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                })
                .showCancelButton(true)
                .show();
    }

    /**
     * Show An Unlike Dialog
     * @param c
     */
    private void showUnlikeDialog(final Course c) {
        if (dialog != null && dialog.isShowing())
            dialog.cancel();
        dialog = new SweetAlertDialog(getActivity());
        dialog.setTitleText("取消关注")
                .setContentText("确定要取消关注" + c.getName() + "这门课吗")
                .setConfirmText("是的")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        if (dbMgr.isListened(c)) {
                            sDialog.setTitleText("警告")
                                    .setContentText("此本课程正在监听,如取消关注,将移出监听队列,是否取消关注?")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            dbMgr.deleteFromLike(c);
                                            dbMgr.deleteFromListened(c);
                                            sweetAlertDialog
                                                    .setTitleText("取消关注&取消监听成功")
                                                    .showCancelButton(false)
                                                    .setContentText("")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            ((com.badprinter.sysu_course.activity.Course) getActivity())
                                                                    .updateCourseInfo();
                                                            dialog.cancel();
                                                        }
                                                    })
                                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                        }
                                    })
                                    .showCancelButton(true)
                                    .setCancelText("取消")
                                    .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        } else {
                            dbMgr.deleteFromLike(c);
                            sDialog
                                    .setTitleText("取消关注成功")
                                    .showCancelButton(false)
                                    .setContentText("")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            ((com.badprinter.sysu_course.activity.Course) getActivity())
                                                    .updateCourseInfo();
                                            dialog.cancel();
                                        }
                                    })
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        }
                    }
                })
                .showCancelButton(true)
                .show();
    }

}
