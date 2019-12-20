package com.example.structify;

import java.util.ArrayList;

//A class that allows the Calendar preview to see all study reminders and exam events for a given date at once
//rather than cycling through multiple UniversityCourse objects and re-visiting the calendar preview plot multiple
//times.

public class SemesterDays {

    //Store all exam or assignment due dates and study reminders.
    private ArrayList<String> ExamEvents;
    private ArrayList<String> StudyReminders;
    private ArrayList<Double> StudyTimes;     //correspond via indices to StudyReminders

    //Check if there are exams or if there is any studying to do.
    private boolean Exams;
    private boolean Studying;

    public SemesterDays(){
        ExamEvents = new ArrayList<String>();
        StudyReminders = new ArrayList<String>();
        Exams = false;
        Studying = true;
    }

    public SemesterDays(ArrayList<String> examEvents, ArrayList<String> studyReminders) {
        ExamEvents = examEvents;
        StudyReminders = studyReminders;
        Exams = true;
        Studying = true;
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

    public ArrayList<Double> getStudyTimes() {
        return StudyTimes;
    }

    public void setStudyTimes(ArrayList<Double> studyTimes) {
        StudyTimes = studyTimes;
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

}
