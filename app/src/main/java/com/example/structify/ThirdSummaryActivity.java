package com.example.structify;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

//This activity spits out a summary of some calculations for the user to view and do a brief "sanity check"

public class ThirdSummaryActivity extends AppCompatActivity {

    //data from previous two activities
    private int NumCourses;
    private int StudyTime;
    private ArrayList<UniversityCourse> Courses;

    //for showing summary information
    private TextView OverallSummary;
    private TextView CourseSummary;

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

        //Put summaries into the GUI fields
        
    }
}
