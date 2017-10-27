/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 10/27/17 12:04 PM
 */

package xyz.leezoom.grain.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.leezoom.grain.R;
import xyz.leezoom.grain.module.TodayCourse;
import xyz.leezoom.grain.ui.view.BigCharView;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private List<TodayCourse> courseList;
    private Context mContext;

    public CourseAdapter(List<TodayCourse> courseList, Context mContext) {
        this.courseList = courseList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.course_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TodayCourse course = courseList.get(position);
        holder.mCourseName.setText(course.getCourseName());
        holder.mPlace.setText(course.getPlace());
        holder.mDuration.setText(course.getDuration());
        holder.bigCharView.setFirstCHar(course.getCourseName().substring(0, 1));
        holder.bigCharView.setColor(getColorFromTime(course.getDuration()));
    }


    @Override
    public int getItemCount() {
        if (courseList != null) {
            return courseList.size();
        } else {
            return 0;
        }
    }

    private int getColorFromTime(String time) {
        int color = R.color.colorPrimary;
        switch (time) {
            case "第1,2节" :
            case "" :
                color = R.color.brown_700;
                break;
            default:
                break;
        }
        return color;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.td_bigchar_view)
        BigCharView bigCharView;
        @BindView(R.id.td_course_name)
        TextView mCourseName;
        @BindView(R.id.td_place)
        TextView mPlace;
        @BindView(R.id.td_duration)
        TextView mDuration;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
