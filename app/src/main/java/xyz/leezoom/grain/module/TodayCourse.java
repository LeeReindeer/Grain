
/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 10/26/17 9:30 PM
 */

package xyz.leezoom.grain.module;

public class TodayCourse {
    private String courseName;
    private String teacher;
    private String duration;
    private String place;
    private String className; //班级
    private String weekDuration;

    public TodayCourse() {
    }

    public TodayCourse(String courseName, String teacher, String duration, String place, String weekDuration) {
        this.courseName = courseName;
        this.teacher = teacher;
        this.duration = duration;
        this.place = place;
        this.weekDuration = weekDuration;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getWeekDuration() {
        return weekDuration;
    }

    public void setWeekDuration(String weekDuration) {
        this.weekDuration = weekDuration;
    }
}
