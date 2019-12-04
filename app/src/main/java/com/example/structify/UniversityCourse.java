package com.example.structify;

import java.util.ArrayList;

public class UniversityCourse {

    //Course's name and possibly number as well
    private String CourseName;

    //Timezone format: "America/Los_Angeles"
    private String TimeZone;

    //Dates of items provided in course syllabus in String format "2020-01-18T09:00:00-08:00"
    private String FinalDate;
    private ArrayList<String> MidtermDates;
    private ArrayList<String> AssignmentAndQuizDates;

    //Percentage weights of respective items as shown in the course syllabus.
    private int FinalWt;
    private int MidtermWt;
    private int AssignmentsAndQuizzesWt;

    public UniversityCourse() {
    }

    public UniversityCourse(String name, String timezone){

        CourseName = name;
        TimeZone = timezone;
    }

    public String getCourseName() {
        return CourseName;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
    }

    public String getTimeZone() {
        return TimeZone;
    }

    public void setTimeZone(String timeZone) {
        TimeZone = timeZone;
    }

    public String getFinalDate() {
        return FinalDate;
    }

    public void setFinalDate(String finalDate) {
        FinalDate = finalDate;
    }

    public ArrayList<String> getMidtermDates() {
        return MidtermDates;
    }

    public void setMidtermDates(ArrayList<String> midtermDates) {
        MidtermDates = midtermDates;
    }

    public ArrayList<String> getAssignmentAndQuizDates() {
        return AssignmentAndQuizDates;
    }

    public void setAssignmentAndQuizDates(ArrayList<String> assignmentAndQuizDates) {
        AssignmentAndQuizDates = assignmentAndQuizDates;
    }

    public int getFinalWt() {
        return FinalWt;
    }

    public void setFinalWt(int finalWt) {
        FinalWt = finalWt;
    }

    public int getMidtermWt() {
        return MidtermWt;
    }

    public void setMidtermWt(int midtermWt) {
        MidtermWt = midtermWt;
    }

    public int getAssignmentsAndQuizzesWt() {
        return AssignmentsAndQuizzesWt;
    }

    public void setAssignmentsAndQuizzesWt(int assignmentsAndQuizzesWt) {
        AssignmentsAndQuizzesWt = assignmentsAndQuizzesWt;
    }

    public int FinalAllocation(int CourseTime){

        return (CourseTime/100)*FinalWt;
    }

    public int MidtermAllocation(int CourseTime){

        return (CourseTime/100)*MidtermWt;
    }

    public int OtherAllocation(int CourseTime){

        return (CourseTime/100)*AssignmentsAndQuizzesWt;
    }
}
