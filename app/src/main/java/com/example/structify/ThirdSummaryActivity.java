package com.example.structify;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

//This activity spits out a summary of some calculations for the user to view and do a brief "sanity check"

public class ThirdSummaryActivity extends AppCompatActivity {

    private int NumCourses;
    private int StudyTime;
    private ArrayList<UniversityCourse> Courses;

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

        //Put summaries into the GUI
    }
}
