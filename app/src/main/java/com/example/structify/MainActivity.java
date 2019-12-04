package com.example.structify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //private EditText editText;
    private Button EnterCoursesBtn;
    //private ArrayList<String> data = new ArrayList<>();
    //private static final String URL = "http://numbersapi.com/1..";
    private LinearLayout Canvas;
    private String startDate;
    private String endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EnterCoursesBtn = findViewById(R.id.submitBtn);

        Canvas = findViewById(R.id.linearChildParent);

        //Use Dynamic UI to create entry fields as required.

        //Find another Github Android app that shows a calendar view to reverse-engineer

        //Use Calendar Trial to input reminders in to Google Maps API.

        setSubmitBtnClick();
    }
}
