package com.example.structify;

import android.os.Bundle;
import android.provider.CalendarContract;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class YourCalendarActivity extends AppCompatActivity {

    //Course data from previous activities
    private int NumCourses;
    private ArrayList<UniversityCourse> Courses;

    //Storing all dates paired with their events and study reminders
    private ArrayList<SemesterDays> CalendarIndex;


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

        //Store all events and reminders for all days in the semester

        //Make the calendar preview!


    }
}
