package com.example.structify;

import java.util.ArrayList;
import java.util.Date;

//A class that allows the Calendar preview to see all study reminders and exam events for a given date at once
//rather than cycling through multiple UniversityCourse objects and re-visiting the calendar preview plot multiple
//times.

public class SemesterDays {

    //Store day of week position (important for the calendar UI layout)
    private int day_of_week;

    //Store all exam or assignment due dates and study reminders.
    private ArrayList<String> ExamEvents;
    private ArrayList<String> StudyReminders;
    private ArrayList<Integer> ExamCourseID;           //Correspond via indices to ExamEvents
    private ArrayList<Integer> StudyCourseID;          //correspond via indices to StudyReminders

    //Check if there are exams or if there is any studying to do.
    private boolean Exams;
    private boolean Studying;

    public SemesterDays(){
        ExamEvents = new ArrayList<String>();
        StudyReminders = new ArrayList<String>();
        ExamCourseID = new ArrayList<Integer>();
        StudyCourseID = new ArrayList<Integer>();
        Exams = false;
        Studying = false;
    }

    public int getDay_of_week() {
        return day_of_week;
    }

    public void setDay_of_week(int day_of_week) {
        this.day_of_week = day_of_week;
    }

    public ArrayList<String> getExamEvents() {
        return ExamEvents;
    }

    public void setExamEvents(ArrayList<String> examEvents) {
        ExamEvents = examEvents;
    }

    public ArrayList<String> getStudyReminders() {
        return StudyReminders;
    }

    public void setStudyReminders(ArrayList<String> studyReminders) {
        StudyReminders = studyReminders;
    }

    public ArrayList<Integer> getExamCourseID() {
        return ExamCourseID;
    }

    public void setExamCourseID(ArrayList<Integer> examCourseID) {
        ExamCourseID = examCourseID;
    }

    public ArrayList<Integer> getStudyCourseID() {
        return StudyCourseID;
    }

    public void setStudyCourseID(ArrayList<Integer> studyCourseID) {
        StudyCourseID = studyCourseID;
    }

    public boolean isExams() {
        return Exams;
    }

    public void setExams(boolean exams) {
        Exams = exams;
    }

    public boolean isStudying() {
        return Studying;
    }

    public void setStudying(boolean studying) {
        Studying = studying;
    }

    public void addStudyReminder(String reminder){
        if (Studying == false){
            StudyReminders = new ArrayList<String>();
            StudyReminders.add(reminder);
            Studying = true;
        } else {
            StudyReminders.add(reminder);
        }
    }

    public void addExamEvent(String exam){
        if (Exams == false){
            ExamEvents = new ArrayList<String>();
            ExamEvents.add(exam);
            Exams = true;
        } else {
            ExamEvents.add(exam);
        }
    }

    public void addExamCourse(int course){
        ExamCourseID.add(course);
    }

    public void addStudyCourse(int course){
        StudyCourseID.add(course);
    }
}
