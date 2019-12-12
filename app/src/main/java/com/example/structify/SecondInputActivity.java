package com.example.structify;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//The second page, where, given the number of courses, course data is input. The button will store
//all values from the editText fields. Steps:
//1) Dynamically generate all input fields for courses using a for loop based on NumCourses.
//        -EditTexts are stored in an ArrayList so that they persist
//        -EditTexts have string IDs attached using a Map
//2) Receive user input through GUI
//3) Activity takes data from input fields and puts them into objects of UniversityCourse class.

public class SecondInputActivity extends AppCompatActivity {

    //Global variables that we will use continually in methods, passed through intent from MainActivity.
    private Date StartDate;
    private Date EndDate;
    private int NumCourses;
    private int StudyTime;

    //Making dynamically created EditText fields readable.
    private Map<String,EditText> InputFieldIDs;
    private List<LinearLayout> Panels;  //not accessed, just to allow layouts to persist outside loops
    private List<HorizontalScrollView> HorPanels;  //not accessed, just allowing layouts to persist
    private List<TextView> Labels;      //not accessed, just to allow labels to persist outside loop
    private List<EditText> InputFields;

    //Where we store input data
    private ArrayList<UniversityCourse> Courses;

    //The layout where we will put all of the dynamic UI-generated fields
    private LinearLayout Canvas;
    private Button SubmitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondinput);

        //Grabbing data from MainActivity
        Bundle extras = getIntent().getExtras();
        StartDate = new Date();
        StartDate.setTime(extras.getLong("StartDate"));
        EndDate = new Date();
        EndDate.setTime(extras.getLong("EndDate"));
        NumCourses = extras.getInt("NumCourses");
        StudyTime = extras.getInt("StudyTime");

        //Map for keeping track of dynamically generated edittexts
        InputFieldIDs = new HashMap<String,EditText>();

        //Setting our base and creating inflater for inflating course_form template layout views
        Canvas = findViewById(R.id.canvas);
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = NumCourses; i >= 1; i--){

            //Inflate an instance of course form for every course
            View current = vi.inflate(R.layout.course_form,null);

            //Map field IDs for later access
            TextView course = current.findViewById(R.id.textView1);
            course.setText("Course "+Integer.toString(i)+" Name");
            EditText course_name = current.findViewById(R.id.course_name);
            EditText course_weight = current.findViewById(R.id.course_weight);
            EditText final_weight = current.findViewById(R.id.final_weight);
            EditText midterm_weight = current.findViewById(R.id.midterm_weight);
            EditText assignment_weight = current.findViewById(R.id.assignment_weight);
            EditText final_date = current.findViewById(R.id.final_date);
            EditText midterm_date1 = current.findViewById(R.id.midterm_date1);
            EditText midterm_date2 = current.findViewById(R.id.midterm_date2);
            EditText number_midterms = current.findViewById(R.id.number_midterms);
            EditText assignment1 = current.findViewById(R.id.assignment1);
            EditText assignment2 = current.findViewById(R.id.assignment2);
            EditText assignment3 = current.findViewById(R.id.assignment3);
            EditText assignment4 = current.findViewById(R.id.assignment4);
            EditText assignment5 = current.findViewById(R.id.assignment5);
            EditText assignment6 = current.findViewById(R.id.assignment6);
            EditText number_assignments = current.findViewById(R.id.number_assignments);

            InputFieldIDs.put("Course "+Integer.toString(i)+" Name",course_name);
            InputFieldIDs.put("Course "+Integer.toString(i)+" Weight",course_weight);
            InputFieldIDs.put("Course "+Integer.toString(i)+" Final Weight",final_weight);
            InputFieldIDs.put("Course "+Integer.toString(i)+" Midterm Weight",midterm_weight);
            InputFieldIDs.put("Course "+Integer.toString(i)+" Assignment Weight",assignment_weight);
            InputFieldIDs.put("Course "+Integer.toString(i)+" Final Date",final_date);
            InputFieldIDs.put("Course "+Integer.toString(i)+" Midterm Date 1",midterm_date1);
            InputFieldIDs.put("Course "+Integer.toString(i)+" Midterm Date 2",midterm_date2);
            InputFieldIDs.put("Course "+Integer.toString(i)+" Number Midterms",number_midterms);
            InputFieldIDs.put("Course "+Integer.toString(i)+" Assignment 1 Date",assignment1);
            InputFieldIDs.put("Course "+Integer.toString(i)+" Assignment 2 Date",assignment2);
            InputFieldIDs.put("Course "+Integer.toString(i)+" Assignment 3 Date",assignment3);
            InputFieldIDs.put("Course "+Integer.toString(i)+" Assignment 4 Date",assignment4);
            InputFieldIDs.put("Course "+Integer.toString(i)+" Assignment 5 Date",assignment5);
            InputFieldIDs.put("Course "+Integer.toString(i)+" Assignment 6 Date",assignment6);
            InputFieldIDs.put("Course "+Integer.toString(i)+" Number Assignments",number_assignments);

            current.setPadding(0,0,0,100);

            //Add the inflated view
            Canvas.addView(current, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        SubmitBtn = findViewById(R.id.submit_details);
        setSubmitBtnClick();

        Iterator it = InputFieldIDs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Log.d("SecondActivity","Added "+pair.getKey()+ " -> " + pair.getValue() + " to InputFieldIDs");
            it.remove(); // avoids a ConcurrentModificationException
        }
    }


    private void setSubmitBtnClick(){
        SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Read all data and create UniversityCourse objects
            }
        });
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
