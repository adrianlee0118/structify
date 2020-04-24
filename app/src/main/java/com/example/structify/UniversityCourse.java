package com.example.structify;

import android.icu.text.SimpleDateFormat;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

/*
*
* A container class generated for each course that stores all of that course's information.
* Parcelable so objects can be passed between 2nd and 3rd activities.
*
*/

public class UniversityCourse implements Parcelable {

    private String CourseName;

    private Date FinalDate;
    private ArrayList<Date> MidtermDates;
    private ArrayList<Date> AssignmentAndQuizDates;

    private double FinalWt;
    private double MidtermWt;
    private double AssignmentsAndQuizzesWt;
    private double CourseWt;

    private Date startDate;
    private Date endDate;


    public UniversityCourse() {
    }

    public UniversityCourse(String name, Date finex, double finwt, double mtwt, double asgnwt, Date st,
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

    public double getFinalWt() {
        return FinalWt;
    }

    public void setFinalWt(double finalWt) {
        FinalWt = finalWt;
    }

    public double getMidtermWt() {
        return MidtermWt;
    }

    public void setMidtermWt(double midtermWt) {
        MidtermWt = midtermWt;
    }

    public double getAssignmentsAndQuizzesWt() {
        return AssignmentsAndQuizzesWt;
    }

    public void setAssignmentsAndQuizzesWt(double assignmentsAndQuizzesWt) {
        AssignmentsAndQuizzesWt = assignmentsAndQuizzesWt;
    }

    public double getCourseWt() {
        return CourseWt;
    }

    public void setCourseWt(double courseWt) {
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

    public double FinalAllocation(double CourseTime){

        return Math.round(((CourseTime/100)*FinalWt)*10)/10.0;
    }

    public double MidtermAllocation(double CourseTime){

        return Math.round(((CourseTime/100)*MidtermWt)*10)/10.0;
    }

    public double AssignmentAllocation(double CourseTime){

        return Math.round(((CourseTime/100)*AssignmentsAndQuizzesWt)*10)/10.0;
    }

    public void calcMTDate(int howmany) throws ParseException {

        ArrayList<Date> result = new ArrayList<Date>();
        long difference = Math.abs((startDate.getTime()-endDate.getTime()))/(howmany+1);
        long curr = Math.abs(startDate.getTime());
        for (int i = 0; i < howmany; i++){
            curr=difference+curr;
            Date temp1 = new Date();
            temp1.setTime(curr);

            LocalDate currld = temp1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            int M = currld.getMonthValue();
            String MM = Integer.toString(M);
            if (M+1<10){
                MM = "0"+MM;
            }
            int D = currld.getDayOfMonth();
            String DD = Integer.toString(D);
            if(D < 10){
                DD = "0"+DD;
            }
            int Y = currld.getYear();

            try {
                temp1 = formatter.parse(Integer.toString(Y)+"-"+MM+"-"+DD);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            result.add(temp1);
        }
        MidtermDates =  result;
    }

    public void calcAssignmentDates(int howmany){

        ArrayList<Date> result = new ArrayList<Date>();
        long difference = Math.abs((startDate.getTime()-endDate.getTime()))/(howmany+1);
        long curr = Math.abs(startDate.getTime());
        for (int i = 0; i < howmany; i++){
            curr = difference + curr;
            Date temp1 = new Date();
            temp1.setTime(curr);

            LocalDate currld = temp1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            int M = currld.getMonthValue();
            String MM = Integer.toString(M);
            if (M+1<10){
                MM = "0"+MM;
            }
            int D = currld.getDayOfMonth();
            String DD = Integer.toString(D);
            if(D < 10){
                DD = "0"+DD;
            }
            int Y = currld.getYear();

            try {
                temp1 = formatter.parse(Integer.toString(Y)+"-"+MM+"-"+DD);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            result.add(temp1);
        }
        AssignmentAndQuizDates = result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(CourseName);
        dest.writeLong(FinalDate.getTime());

        dest.writeInt(MidtermDates.size());
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

        dest.writeDouble(FinalWt);
        dest.writeDouble(MidtermWt);
        dest.writeDouble(AssignmentsAndQuizzesWt);
        dest.writeDouble(CourseWt);

        dest.writeLong(startDate.getTime());
        dest.writeLong(endDate.getTime());

    }

    public static final Parcelable.Creator<UniversityCourse> CREATOR = new Parcelable.Creator<UniversityCourse>() {
        public UniversityCourse createFromParcel(Parcel in) {
            return new UniversityCourse(in);
        }

        public UniversityCourse[] newArray(int size) {
            return new UniversityCourse[size];
        }
    };

    private UniversityCourse(Parcel in) {
        CourseName = in.readString();

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
            AssignmentAndQuizDates.add(ass);
        }

        FinalWt = in.readDouble();
        MidtermWt = in.readDouble();
        AssignmentsAndQuizzesWt = in.readDouble();
        CourseWt = in.readDouble();

        long sdate = in.readLong();
        startDate = new Date();
        startDate.setTime(sdate);

        long edate = in.readLong();
        endDate = new Date();
        endDate.setTime(edate);

    }
}
