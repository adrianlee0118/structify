package com.example.structify;

import android.os.Bundle;
import android.provider.CalendarContract;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class YourCalendarActivity extends AppCompatActivity {

    //Course data from previous activities
    private int NumCourses;
    private ArrayList<UniversityCourse> Courses;

    //Storing all dates paired with their events and study reminders encapsulated in SemesterDays
    private HashMap<Date,SemesterDays> CalendarIndex;

    //UI components to be manipulated
    private TextView Year;
    private TextView Month;
    private ImageButton PreviousMonthButton;
    private ImageButton NextMonthButton;
    private LinearLayout CalendarCanvas;

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

        //Link program to UI
        Year = findViewById(R.id.date_display_year);
        Month = findViewById(R.id.date_display_date);
        PreviousMonthButton = findViewById(R.id.calendar_prev_button);
        NextMonthButton = findViewById(R.id.calendar_next_button);
        CalendarCanvas = findViewById(R.id.calendar_canvas);

        //Initialize all days in the HashMap


        //Store all events and reminders in the SemesterDays instances contained in the Hashmap
        for (int i = 1; i <= NumCourses; i++){

        }

        //Inflate the weekviews in the calendars with all reminders and events as per the information in the Map


        //Display the Calendar preview and set the button functionality to add events to Google Calendar!
    }
}
