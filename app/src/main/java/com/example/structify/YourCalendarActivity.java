package com.example.structify;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class YourCalendarActivity extends AppCompatActivity {

    private int NumCourses;
    private ArrayList<UniversityCourse> Courses;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yourcalendar);

        Bundle extras = getIntent().getExtras();
        NumCourses = extras.getInt("NumCourses");
        Courses = new ArrayList<>();
        for (int i = 1; i <= NumCourses; i++){
            UniversityCourse temp_course = (UniversityCourse) extras.getParcelable("Course "+i);
            Courses.add(temp_course);
        }

        //Make the calendar preview!
        
    }
}
