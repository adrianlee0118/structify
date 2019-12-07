package com.example.structify;

import java.util.ArrayList;
import java.util.Date;

//A container class generated for each course that stores all of that course's information.

public class UniversityCourse {

    //Course's name and possibly number as well
    private String CourseName;

    //Timezone format: "America/Los_Angeles"
    private String TimeZone;

    //Dates of items provided in course syllabus in String format "2020-01-18T09:00:00-08:00"
    //If these are not provided, can be calculated from start and end dates of semester
    private Date FinalDate;
    private ArrayList<Date> MidtermDates;
    private ArrayList<Date> AssignmentAndQuizDates;

    //Percentage weights of respective items as shown in the course syllabus.
    private int FinalWt;
    private int MidtermWt;
    private int AssignmentsAndQuizzesWt;
    private int CourseWt; //the portion of total available study time you wish to allocate to this particular course.

    //Semester dates to help make default exam/assignment dates if they are not provided
    private Date startDate;
    private Date endDate;

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

    public Date getFinalDate() {
        return FinalDate;
    }

    public void setFinalDate(Date finalDate) {
        FinalDate = finalDate;
    }

    public ArrayList<Date> getMidtermDates() {
        return MidtermDates;
    }

    public void setMidtermDates(ArrayList<Date> midtermDates) {
        MidtermDates = midtermDates;
    }

    public ArrayList<Date> getAssignmentAndQuizDates() {
        return AssignmentAndQuizDates;
    }

    public void setAssignmentAndQuizDates(ArrayList<Date> assignmentAndQuizDates) {
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

    public int getCourseWt() {
        return CourseWt;
    }

    public void setCourseWt(int courseWt) {
        CourseWt = courseWt;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int FinalAllocation(int CourseTime){

        return (CourseTime/100)*FinalWt;
    }

    public int MidtermAllocation(int CourseTime){

        return (CourseTime/100)*MidtermWt;
    }

    public int AssignmentAllocation(int CourseTime){

        return (CourseTime/100)*AssignmentsAndQuizzesWt;
    }

    public Date calcMTDate(int howmany){
        //Use startDate and endDate to create a Midterm date if not provided.
    }

    public ArrayList<Date> calcAssignmentDates(int howmany){
        //Use startDate and endDate to create quiz and assignment dates.
    }
}
