package com.example.structify;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

//A container class generated for each course that stores all of that course's information.
//Parcelable so objects can be passed between 2nd and 3rd activities.

public class UniversityCourse implements Parcelable {

    //Course's name and possibly number as well
    private String CourseName;

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

    public UniversityCourse(String name, Date finex, int finwt, int mtwt, int asgnwt, Date st,
                            Date end){

        CourseName = name;
        FinalDate = finex;
        FinalWt = finwt;
        MidtermWt = mtwt;
        AssignmentsAndQuizzesWt = asgnwt;
        startDate = st;
        endDate = end;
    }

    public String getCourseName() {
        return CourseName;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
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

    public void calcMTDate(int howmany){
        //Use startDate and endDate to create a Midterm date if not provided.
        ArrayList<Date> result = new ArrayList<Date>();
        long difference = (startDate.getTime()-endDate.getTime())/(howmany+1);
        Date curr = new Date();
        curr.setTime(startDate.getTime()+difference);
        result.add(curr);
        for (int i = 0; i < howmany; i++){
            curr.setTime(curr.getTime()+difference);
            result.add(curr);
        }
        MidtermDates =  result;
    }

    public void calcAssignmentDates(int howmany){
        //Use startDate and endDate to create quiz and assignment dates.
        ArrayList<Date> result = new ArrayList<Date>();
        long difference = (startDate.getTime()-endDate.getTime())/(howmany+1);
        Date curr = new Date();
        curr.setTime(startDate.getTime()+difference);
        result.add(curr);
        for (int i = 0; i < howmany; i++){
            curr.setTime(curr.getTime()+difference);
            result.add(curr);
        }
        AssignmentAndQuizDates = result;
    }

    //Parcelable methods: The Parcelable properties enable objects of the UniversityCourse class to be passed
    //from one activity to the next through intents.

    @Override
    public int describeContents() {
        return 0;
    }

    //Write data from a UniversityCourse into a parcel to be passed in intent.
    //The order of reading in a constructor from parcel below will retrieve data in the same order it was
    //written here.
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(CourseName);
        dest.writeLong(FinalDate.getTime());

        //Dates must be passed as longs
        dest.writeInt(MidtermDates.size()); //this is needed for constructing the array we will write this data into
        long[] mtdateslong = new long[MidtermDates.size()];
        for (int i = 0; i < MidtermDates.size(); i++){
            mtdateslong[i] = MidtermDates.get(i).getTime();
        }
        dest.writeLongArray(mtdateslong);

        dest.writeInt(AssignmentAndQuizDates.size());
        long[] assigndateslong = new long[AssignmentAndQuizDates.size()];
        for (int i = 0; i < AssignmentAndQuizDates.size(); i++){
            assigndateslong[i] = AssignmentAndQuizDates.get(i).getTime();
        }
        dest.writeLongArray(assigndateslong);

        dest.writeInt(FinalWt);
        dest.writeInt(MidtermWt);
        dest.writeInt(AssignmentsAndQuizzesWt);
        dest.writeInt(CourseWt);

        dest.writeLong(startDate.getTime());
        dest.writeLong(endDate.getTime());

    }

    //All Parcelables must have a CREATOR that implements these two methods
    //Used to regenerate the object in a new activity
    public static final Parcelable.Creator<UniversityCourse> CREATOR = new Parcelable.Creator<UniversityCourse>() {
        public UniversityCourse createFromParcel(Parcel in) {
            return new UniversityCourse(in);
        }

        public UniversityCourse[] newArray(int size) {
            return new UniversityCourse[size];
        }
    };

    //Constructor that takes a Parcel and gives you an object populated with its values
    //Data must be read in same order that they were written in
    private UniversityCourse(Parcel in) {
        CourseName = in.readString();

        //Time read as long, translated back into dates.
        long fdtime = in.readLong();
        FinalDate = new Date();
        FinalDate.setTime(fdtime);

        int sizemt = in.readInt();
        long[] mtdateslong = new long[sizemt];
        in.readLongArray(mtdateslong);
        MidtermDates = new ArrayList<Date>();
        for (int i = 0; i < mtdateslong.length; i++){
            Date mt = new Date();
            mt.setTime(mtdateslong[i]);
            MidtermDates.add(mt);
        }

        int sizeass = in.readInt();
        long[] adateslong = new long[sizeass];
        in.readLongArray(adateslong);
        AssignmentAndQuizDates = new ArrayList<Date>();
        for (int i = 0; i < adateslong.length; i++){
            Date ass = new Date();
            ass.setTime(adateslong[i]);
            MidtermDates.add(ass);
        }

        FinalWt = in.readInt();
        MidtermWt = in.readInt();
        AssignmentsAndQuizzesWt = in.readInt();
        CourseWt = in.readInt();

        long sdate = in.readLong();
        startDate = new Date();
        startDate.setTime(sdate);

        long edate = in.readLong();
        endDate = new Date();
        endDate.setTime(edate);
    }
}
