
/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 10/28/17 11:40 AM
 */
package xyz.leezoom.grain.ui.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.leezoom.grain.R;
import xyz.leezoom.grain.module.QueryType;
import xyz.leezoom.grain.module.ServerIpOld;
import xyz.leezoom.grain.module.TodayCourse;
import xyz.leezoom.grain.module.User;
import xyz.leezoom.grain.ui.activity.MainActivity;
import xyz.leezoom.grain.ui.activity.SettingActivity;
import xyz.leezoom.grain.ui.adapter.CourseAdapter;
import xyz.leezoom.grain.util.FragmentUtil;
import xyz.leezoom.grain.util.MyBase64;
import xyz.leezoom.grain.util.NetWorkTask;
import xyz.leezoom.grain.util.PackMessage;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodayFragment extends Fragment {

    private String mDate;
    private String cDate;


    private List<TodayCourse> courseList  = new ArrayList<>();
    private CourseAdapter adapter;
    private SharedPreferences query;
    private SharedPreferences preferences;

    private NetWorkTask tTask;
    private NetWorkTask t2Task;
    private NetWorkTask.NetWorkListener tListener = new NetWorkTask.NetWorkListener() {
        @Override
        public void onSuccess() {
            //add data to list
            query = getActivity().getSharedPreferences("query", Context.MODE_PRIVATE);
            String[] allCourse1 = MyBase64.BASE64ToString(query.getString("500","")).split("\n");
            String[] singleCourse1 = null;
            for (String single : allCourse1) {
                TodayCourse course = new TodayCourse();
                singleCourse1 = single.split(PackMessage.SplitFields);
                int datePlaceFlag = 0;
                try {
                    course.setCourseName(singleCourse1[2].split("-")[0]);
                    course.setClassName(singleCourse1[8]);
                    course.setWeekDuration(singleCourse1[7]);
                if (isYourCourse(course.getClassName())) {
                    //date flag is the number of which class your go on today
                    datePlaceFlag = isTodayCourse(singleCourse1[5]);
                    course.setPlace((singleCourse1[4].split(";"))[datePlaceFlag]);
                    course.setDuration((singleCourse1[5].split(";"))[datePlaceFlag].replace(cDate, ""));
                    courseList.add(course);
                }
                } catch (ArrayIndexOutOfBoundsException t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            }
        }

        @Override
        public void onFailed() {
            FragmentUtil.showFailedPage(getActivity(), true, TodayFragment.this);
        }
    };
    private NetWorkTask.NetWorkListener t2Listener = new NetWorkTask.NetWorkListener() {
        @Override
        public void onSuccess() {
            //add data & show list in view
            //add data to list
            query = getActivity().getSharedPreferences("query", Context.MODE_PRIVATE);
            String[] allCourse2 = MyBase64.BASE64ToString(query.getString("501","")).split("\n");
            String[] singleCourse2 = null;
            for (String single : allCourse2) {
                TodayCourse course = new TodayCourse();
                singleCourse2 = single.split(PackMessage.SplitFields);
                int datePlaceFlag = 0;
                try {
                    course.setCourseName(singleCourse2[2].split("-")[0]);
                    course.setClassName(singleCourse2[8]);
                    course.setWeekDuration(singleCourse2[7]);
                    if (isYourCourse(course.getClassName())) {
                        //date flag is the number of which class your go on today
                        datePlaceFlag = isTodayCourse(singleCourse2[5]);
                        course.setPlace((singleCourse2[4].split(";"))[datePlaceFlag]);
                        course.setDuration((singleCourse2[5].split(";"))[datePlaceFlag].replace(cDate, ""));
                        courseList.add(course);
                    }
                } catch (ArrayIndexOutOfBoundsException t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            }
            refreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onFailed() {
            FragmentUtil.showFailedPage(getActivity(), true, TodayFragment.this);
        }
    };

    private User user;

    @BindView(R.id.td_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.td_refresh_view)
    SwipeRefreshLayout refreshLayout;

    public TodayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        ButterKnife.bind(this, view);
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("E");
        mDate = ft.format(date);
        cDate = convertDateToChinese(mDate);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        initView();
        initData();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (tTask != null && t2Task != null) {
            tTask.cancel(true);
            t2Task.cancel(true);
        }
        FragmentUtil.showFailedPage(getActivity(), false, TodayFragment.this);
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
    }

    private void initView() {
        adapter = new CourseAdapter(courseList, getContext());
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }

    private void initData() {
        refreshLayout.setRefreshing(true);
        courseList.clear();
        user = MainActivity.getUser();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String faculty = preferences.getString(SettingActivity.KEY_PREF_FACULTY, "");
        String classNameFromSetting = preferences.getString(SettingActivity.KEY_PREF_CLASS, "");
        if (faculty.isEmpty() || classNameFromSetting.isEmpty()) {
            Toast.makeText(getContext(), R.string.setting_hint, Toast.LENGTH_LONG).show();
            refreshLayout.setRefreshing(false);
            return;
        }
        tTask = new NetWorkTask(user, QueryType.ZFQueryCengkeToday, ServerIpOld.classServerPort, tListener, getContext());
        tTask.execute(faculty + ",50,0");
        t2Task = new NetWorkTask(user, QueryType.ZFQueryCengkeToday, ServerIpOld.classServerPort, t2Listener, getContext());
        t2Task.execute(faculty + ",50,1");
    }

    /**
     *
     * @param className
     * @return check your class in setting.
     */
    private boolean isYourCourse(String className) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String classNameFromSetting = preferences.getString(SettingActivity.KEY_PREF_CLASS, "");
        return className.contains(classNameFromSetting);
    }

    private int isTodayCourse(String duration){
        String[] singleDate = duration.split(";");
        for (int i = 0; i < singleDate.length; i++) {
            if (singleDate[i].contains(cDate)) {
                return i;
            }
        }
        return 0;
    }

    private String convertDateToChinese(String eDate) {
        String cDate = "";
        switch (eDate) {
            case "Mon":
                cDate = "周一";
                break;
            case "Tue":
                cDate = "周二";
                break;
            case "Wed":
                cDate = "周三";
                break;
            case "Thu":
                cDate = "周四";
                break;
            case "Fri":
                cDate = "周五";
                break;
            case "Sat":
                cDate = "周六";
                break;
            case "Sun":
                cDate = "周日";
                break;
            default:
                break;
        }
        return cDate;
    }

}
