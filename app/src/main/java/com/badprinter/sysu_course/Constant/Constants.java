package com.badprinter.sysu_course.Constant;

/**
 * Created by root on 15-9-13.
 */
public class Constants {
    public static final String[] ERROR_MSG = {
            "提交成功",
            "非法操作! 数据库没有对应的教学班号。",
            "当前不在此课程类别的选课时间范围内！",
            "您不在该教学班的修读对象范围内，不允许选此教学班！",
            "您所在的学生群体，在此阶段不允许对该课程类别的课进行选课、退课！",
            "系统中没有您这个学期的报到记录，不允许选课。请联系您所在院系的教务员申请补注册。",
            "您这个学期未完成评教任务，不允许选课。",
            "您不满足该教学班选课的性别要求，不能选此门课程！",
            "不允许跨校区选课！",
            "此课程已选，不能重复选择！",
            "您所选课程 的成绩为“已通过”，因此不允许再选该课，请重新选择！",
            "此类型课程已选学分总数超标",
            "此类型课程已选门数超标",
            "毕业班学生，公选学分已满，最后一个学期不允许选择公选课！",
            "您不是博雅班学生，不能选此门课程！",
            "您最多能选2门博雅班课程！",
            "您不是基础实验班学生，不能选此门课程！",
            "所选课程与已选课程上课时间冲突,请重新选择!",
            "已经超出限选人数，请选择别的课程！",
            "该教学班不参加选课，你不能选此教学班！",
            "选课等待超时",
            "您这个学期未完成缴费，不允许选课。请联系财务处帮助台（84036866 再按 3）",
            "您未满足选择该课程的先修课程条件!",
            "不在此课程类型的选课时间范围内",
            "您的核心通识课学分已满足培养方案的学分要求，无法再选择核心通识课",
            "请求失败,请稍后重试或登陆电脑选课"
    };
    public static class ListenerMsg {
        public final static String UPDATE_LISTENED_COURSES = "com.badprinter.sysu_course.UPDATE_LISTENED_COURSES";
    }
    public static class BoardAction {
        public final static String SUCCESSFUL_SELECTION = "com.badprinter.sysu_course.SUCCESSFUL_SELECTION";
    }
}
