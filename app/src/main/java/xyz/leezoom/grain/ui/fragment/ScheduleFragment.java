/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 9/24/17 1:49 PM
 */

package xyz.leezoom.grain.ui.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.leezoom.grain.R;
import xyz.leezoom.grain.module.Schedule;
import xyz.leezoom.grain.ui.view.ScheduleView;
import xyz.leezoom.grain.util.PackMessage;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {

    private FloatingActionButton fabB;
    private FloatingActionsMenu fabMenu;
    @BindView(R.id.schedule_view)
    ScheduleView scheduleView;
    @BindView(R.id.sd_container)
    RelativeLayout relativeLayout;
    private EditText editText;
    //course list in a day.
    private Map<Schedule, Integer> courseMap = new HashMap<>();
    private String allCourseInfo;

    public ScheduleFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        ButterKnife.bind(this, view);
        fabMenu = getActivity().findViewById(R.id.multiple_actions);
        fabB = getActivity().findViewById(R.id.fab_b);
        fabB.setVisibility(View.VISIBLE);
        fabB.setTitle("Add");
        fabB.setIcon(R.drawable.ic_exit_to_app_black_48dp);
        fabB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 9/23/17\
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                AlertDialog dialog= builder
                        .setTitle("Your course info")
                        .setIcon(R.mipmap.ic_reindeer)
                        .setCancelable(true)
                        .setView(scheduleView.setInputEdit(editText = new EditText(getContext())))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                allCourseInfo = editText.getText().toString();
                                Log.d("Schedule", allCourseInfo);
                                //scheduleView.invalidate();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                editText.setMaxLines(10);
                dialog.show();
                fabMenu.collapse();
            }
        });
        initData();
        addCourseView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
    }

    private void initData(){
        if (allCourseInfo != null && !allCourseInfo.isEmpty()) {
            String all = allCourseInfo.replaceAll("(星期一|星期二|星期三|星期四|星期五|星期六|星期日)", "")
                    .replaceAll("(第1节|第2节|第3节|第4节|第5节|第6节|第7节|第8节|第9节|第10节|第11节|第12节)", "")
                    .replaceAll("(时间|早晨|上午|下午|晚上)", "")
                    .replaceAll("\t", PackMessage.SplitGroups)
                    .replaceAll("\n", PackMessage.SplitFields).trim();
            System.out.println(all);
            String[] allSplit = all.split(PackMessage.SplitGroups);
            List<String> singleData = new ArrayList<>();
            for (String e : allSplit) {
                if (!e.isEmpty() && !e.equals(" ")) {
                    List<String> eSigle = new ArrayList(Arrays.asList(e.split(PackMessage.SplitFields)));
                    //remove empty or space item
                    if (eSigle.size() != 4) continue;
                    for (int i = 0; i < eSigle.size(); i++) {
                        if (eSigle.get(i).isEmpty() || eSigle.get(i).equals(" ")) {
                            eSigle.remove(eSigle.get(i));
                        }
                    }
                    singleData.addAll(eSigle);
                    Schedule schedule = new Schedule();
                    schedule.setClassName(singleData.get(0));
                    schedule.setTime(singleData.get(1));
                    schedule.setTeacher(singleData.get(2));
                    schedule.setPlace(singleData.get(3));
                    //a single class name
                    courseMap.put(schedule, getDataFromString(schedule.getTime()));
                    //List<Schedule> schedules = new ArrayList<>();
                    //schedules.add(schedule);
                    singleData.clear();
                }
            }
        }
    }

    private void addCourseView() {
        //relativeLayout.
    }

    private int getDataFromString(String time) {
        int day = 0;
        if (time.substring(0, 2).contains("周")){
            switch (time.substring(1, 2)) {
                case "一":
                    day = 1;
                    break;
                case "二":
                    day = 2;
                    break;
                case "三":
                    day = 3;
                    break;
                case "四":
                    day = 4;
                    break;
                case "五":
                    day = 5;
                    break;
                case "六":
                    day = 6;
                    break;
                case "日":
                    day = 7;
                    break;
                default:
                    break;
            }
        }
        return  day;
    }
}
