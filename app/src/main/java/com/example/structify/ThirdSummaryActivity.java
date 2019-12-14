package com.example.structify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

//This activity spits out a summary of some calculations for the user to view and do a brief "sanity check"

public class ThirdSummaryActivity extends AppCompatActivity {

    //data from previous two activities
    private int NumCourses;
    private int StudyTime;
    private ArrayList<UniversityCourse> Courses;

    //for showing summary information
    private TextView OverallSummary;
    private TextView CourseSummary;

    //button for moving to next activity;
    private Button PreviewCalendarBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thirdsummary);

        Bundle extras = getIntent().getExtras();
        NumCourses = extras.getInt("NumCourses");
        StudyTime = extras.getInt("StudyTime");
        Courses = new ArrayList<>();
        for (int i = 1; i <= NumCourses; i++){
            UniversityCourse temp_course = (UniversityCourse) extras.getParcelable("Course "+i);
            Courses.add(temp_course);
        }

        OverallSummary = findViewById(R.id.overall_summary);
        CourseSummary = findViewById(R.id.course_summary);
        PreviewCalendarBtn = findViewById(R.id.CalendarBtn);

        //Put summaries into the GUI fields
        String overall_message = "";
        String course_message = "";

        LocalDate sdate = Courses.get(0).getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate edate = Courses.get(0).getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        overall_message += "This semester starts on "+sdate.getMonth().getDisplayName(TextStyle.FULL, Locale.US)
                +" "+sdate.getDayOfMonth()+ ", "+sdate.getYear()+", and ends on "+edate.getMonth().getDisplayName(TextStyle.FULL, Locale.US)
                +" "+edate.getDayOfMonth()+ ", "+edate.getYear()+"."+"\n"+"\n";

        overall_message += "Total available study time for the semester from all weekdays and weekends is "+
                StudyTime+" hours. This is distributed over all "+NumCourses+" courses as follows:"+"\n"+"\n";

        for (int i = 1; i <= NumCourses; i++){
            double course_time = (Courses.get(i).getCourseWt()*StudyTime)/100;

            overall_message += "Total study time for "+Courses.get(i).getCourseName()+" will be "+
                    course_time+" hours, given Course Weight in semester of "+
                    Courses.get(i).getCourseWt()+"%, distributed by credit weight of course components."+"\n"+"\n";

            course_message += "Study time for "+Courses.get(i).getCourseName()+"'s final exam will be "+
                    (Courses.get(i).FinalAllocation(course_time)+" hours, given Final Weight in semester of "+
                    Courses.get(i).getCourseWt()+"%."+"\n"+"\n");
            LocalDate fdate = Courses.get(i).getFinalDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            course_message += "The final exam will take place on "+fdate.getMonth().getDisplayName(TextStyle.FULL, Locale.US)
                    +" "+fdate.getDayOfMonth()+"."+"\n"+"\n";

            course_message += "Study time for "+Courses.get(i).getCourseName()+"'s midterm exams will be "+
                    (Courses.get(i).MidtermAllocation(course_time)+" hours, given Midterm Weight in semester of " +
                            Courses.get(i).getMidtermWt()+"%."+"\n"+"\n");
            if (Courses.get(i).getMidtermDates().size() > 1){
                course_message += "The midterm exams will take place on ";
                for (int j = 0; j < Courses.get(i).getMidtermDates().size(); j++){
                    if (j == Courses.get(i).getMidtermDates().size()-1){
                        course_message += ", and ";
                    } else {
                        course_message += ", ";
                    }
                    LocalDate mdate = Courses.get(i).getMidtermDates().get(j).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    course_message += mdate.getMonth().getDisplayName(TextStyle.FULL, Locale.US) +" "+mdate.getDayOfMonth();
                }
                course_message += "."+"\n"+"\n";
            } else {
                LocalDate mdate = Courses.get(i).getMidtermDates().get(0).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                course_message += "The midterm exam will take place on "+mdate.getMonth().getDisplayName(TextStyle.FULL, Locale.US)
                        +" "+mdate.getDayOfMonth()+"."+"\n"+"\n";
            }

            course_message += "Work time for "+Courses.get(i).getCourseName()+"'s assignments will be "+
                    (Courses.get(i).AssignmentAllocation(course_time)+" hours, given Assignment Weight in semester of " +
                            Courses.get(i).getAssignmentsAndQuizzesWt()+"%."+"\n"+"\n");
            if (Courses.get(i).getAssignmentAndQuizDates().size() > 1){
                course_message += "The assignments will be due on ";
                for (int j = 0; j < Courses.get(i).getAssignmentAndQuizDates().size(); j++){
                    if (j == Courses.get(i).getAssignmentAndQuizDates().size()-1){
                        course_message += ", and ";
                    } else {
                        course_message += ", ";
                    }
                    LocalDate adate = Courses.get(i).getAssignmentAndQuizDates().get(j).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    course_message += adate.getMonth().getDisplayName(TextStyle.FULL, Locale.US) +" "+adate.getDayOfMonth();
                }
                course_message += "."+"\n"+"\n"+"\n";
            } else {
                LocalDate adate = Courses.get(i).getAssignmentAndQuizDates().get(0).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                course_message += "The midterm exam will take place on "+adate.getMonth().getDisplayName(TextStyle.FULL, Locale.US)
                        +" "+adate.getDayOfMonth()+"."+"\n"+"\n"+"\n";
            }

        }

        overall_message += "*For detailed course breakdowns, see below.";

        course_message += "*Note for all courses, this app will evenly distribute study time for final exams over the two weeks (14 days) leading up to that date."+"\n";
        course_message += "*Note for all courses, this app will evenly distribute study time for midterm exams over the week (7days) leading up to that date."+"\n";
        course_message += "*Note for all courses, this app will evenly distribute study time for assignments over the 3 days leading up to that date."+"\n";

        OverallSummary.setText(overall_message);
        CourseSummary.setText(course_message);

        setPreviewCalendarBtnClick();
    }

    private void setPreviewCalendarBtnClick(){
        PreviewCalendarBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent().setClass(getBaseContext (), YourCalendarActivity.class);
                for (int i = 1; i <= NumCourses; i++){
                    intent.putExtra("Course "+i,(Parcelable)Courses.get(i-1));
                }
                startActivity(intent);
            }
        });
    }
}
