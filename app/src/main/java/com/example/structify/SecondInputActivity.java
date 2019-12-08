package com.example.structify;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;

import java.util.ArrayList;
import java.util.Date;

//The second page, where, given the number of courses, course data is input. The button will calculate

public class SecondInputActivity extends AppCompatActivity {

    //Global variables that we will use continually in methods, passed through intent from MainActivity.
    private Date StartDate;
    private Date EndDate;
    private int NumCourses;
    private int StudyTime;

    private ArrayList<UniversityCourse> Semester;

    //The layout where we will put all of the dynamic UI-generated fields
    private NestedScrollView Canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondinput);

        Bundle extras = getIntent().getExtras();
        StartDate = new Date();
        StartDate.setTime(extras.getLong("StartDate"));
        EndDate = new Date();
        EndDate.setTime(extras.getLong("EndDate"));
        NumCourses = extras.getInt("NumCourses");
        StudyTime = extras.getInt("StudyTime");

        Canvas = findViewById(R.id.canvas);

        //Standard formatting for all the linear layouts we will add
        LinearLayout.LayoutParams linearLayoutparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //Base linear layout
        LinearLayout linearLayoutbase = new LinearLayout(this);
        linearLayoutparams.setMargins(0,32,0,0);
        linearLayoutparams.setLayoutDirection(LinearLayout.VERTICAL);
        linearLayoutbase.setLayoutParams(linearLayoutparams);

        //Create the input interface using Textview and editText modules containing fields to be filled out
        for (int i = 0; i < NumCourses; i++){

            //First level: Course name info
            LinearLayout linearLayout0 = new LinearLayout(this);
            linearLayoutparams.setMargins(0,8,0,0);
            linearLayoutparams.setLayoutDirection(LinearLayout.HORIZONTAL);
            linearLayout0.setLayoutParams(linearLayoutparams);
            TextView c = new TextView(this);
            c.setText("Course Name");
            c.setTextSize(14);
            //Bold
            c.setPadding(8,0,0,0);
            linearLayout0.addView(c);
            EditText cc = new EditText(this);
            cc.setHint("Enter course name");
            cc.setTextSize(14);
            cc.setPadding(8,0,0,0);
            linearLayout0.addView(cc);
            linearLayoutbase.addView(linearLayout0);

            //Second Level: Final Wt, Midterm Wt, Assignments/Quiz Wts
            LinearLayout linearLayout1 = new LinearLayout(this);
        }

        Canvas.addView(linearLayoutbase);
    }

    //Create a CardView that contains a linear layout which contains a Textview which contains some
    //data and return it. Basically the building blocks of the dynamic nested viewport.
    //We will call this anytime we have some fields to add.
    private View cardItemView(String str){
        CardView cardView = new CardView(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(3,4,3,4);
        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(8);
        cardView.setCardElevation(8);


        cardView.setUseCompatPadding(true);

        // text view
        TextView textView = new TextView(this);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        textView.setLayoutParams(params1);
        textView.setText(str);
        textView.setPadding(24,24,24,24);

        cardView.addView(textView);

        return cardView;

    }
}
