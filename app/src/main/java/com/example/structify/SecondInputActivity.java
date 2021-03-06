package com.example.structify;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
*
* The second page, where, given the number of courses, course data is input. The button will store
* all values from the editText fields. Steps:
* 1) Dynamically generate all input fields for courses using a for loop based on NumCourses.
*        -EditTexts are stored in an ArrayList so that they persist
*        -EditTexts have string IDs attached using a Map
* 2) Receive user input through GUI
* 3) Activity takes data from input fields and puts them into objects of UniversityCourse class.
*
* */


public class SecondInputActivity extends AppCompatActivity {

    private Date StartDate;
    private Date EndDate;
    private LocalDate startdate;
    private LocalDate enddate;
    private int NumCourses;
    private int StudyTime;

    private Map<String,EditText> InputFieldIDs;
    private ArrayList<UniversityCourse> Courses;

    private LinearLayout Canvas;
    private Button SubmitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondinput);

        Bundle extras = getIntent().getExtras();
        StartDate = new Date();
        StartDate.setTime(extras.getLong("StartDate"));
        EndDate = new Date();
        EndDate.setTime(extras.getLong("EndDate"));
        startdate = StartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        enddate = EndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        NumCourses = extras.getInt("NumCourses");
        StudyTime = extras.getInt("StudyTime");

        InputFieldIDs = new HashMap<String,EditText>();
        Courses = new ArrayList<UniversityCourse>();

        Canvas = findViewById(R.id.canvas);
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = NumCourses; i > 0; i--){

            View current = vi.inflate(R.layout.course_form,null);

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

            Canvas.addView(current, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        SubmitBtn = findViewById(R.id.submit_details);
        setSubmitBtnClick();
    }


    private void setSubmitBtnClick(){
        SubmitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean proceedFlag = true;

                Courses.clear();

                for (int i = 1; i <= NumCourses; i++){

                    UniversityCourse temp = new UniversityCourse();

                    temp.setStartDate(StartDate);
                    temp.setEndDate(EndDate);

                    if (TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Name")
                            .getText().toString().trim())){
                        Toast.makeText(SecondInputActivity.this,"You forgot to enter Course "
                                +Integer.toString(i)+" Name", Toast.LENGTH_SHORT).show();
                        proceedFlag = false;
                    } else {
                        temp.setCourseName(InputFieldIDs.get("Course "+Integer.toString(i)+" Name").getText().toString());
                        Log.d("Second","Got Course "+Integer.toString(i)+" Name");
                    }

                    if (i == 1){
                        int sum = 0;
                        for (int j = 1; j <= NumCourses; j++){
                            if (TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(j)+" Weight").getText().toString().trim())){
                                Toast.makeText(SecondInputActivity.this,"You forgot to enter Course "
                                        +Integer.toString(j)+" Weight",Toast.LENGTH_SHORT).show();
                                proceedFlag = false;
                            }
                            sum+=Integer.parseInt(InputFieldIDs.get("Course "+Integer.toString(j)+" Weight").getText().toString());
                        }
                        if (sum != 100){
                            Toast.makeText(SecondInputActivity.this,"Your course weights don't sum " +
                                    "to 100!",Toast.LENGTH_SHORT).show();
                            proceedFlag = false;
                        } else {
                            if (Integer.parseInt(InputFieldIDs.get("Course "+Integer.toString(i)+" Weight").getText().toString())
                            !=0){
                                Log.d("Second","Got Course "+Integer.toString(i)+" Weight");
                                temp.setCourseWt(Integer.parseInt(InputFieldIDs.get("Course "+Integer.toString(i)+" Weight").getText().toString()));
                            } else {
                                Toast.makeText(SecondInputActivity.this,"Course " + Integer.toString(i)
                                        + " Final Weight cannot equal zero",Toast.LENGTH_SHORT).show();
                                proceedFlag = false;
                            }
                        }
                    } else {
                        if (Integer.parseInt(InputFieldIDs.get("Course "+Integer.toString(i)+" Weight").getText().toString())
                                !=0){
                            Log.d("Second","Got Course "+Integer.toString(i)+" Weight");
                            temp.setCourseWt(Integer.parseInt(InputFieldIDs.get("Course "+Integer.toString(i)+" Weight").getText().toString()));
                        } else {
                            Toast.makeText(SecondInputActivity.this,"Course " + Integer.toString(i)
                                    + " Final Weight cannot equal zero",Toast.LENGTH_SHORT).show();
                            proceedFlag = false;
                        }
                    }

                    if (Integer.parseInt(InputFieldIDs.get("Course "+Integer.toString(i)+" Final Weight").getText().toString())
                            +Integer.parseInt(InputFieldIDs.get("Course "+Integer.toString(i)+" Midterm Weight").getText().toString())
                            +Integer.parseInt(InputFieldIDs.get("Course "+Integer.toString(i)+" Assignment Weight").getText().toString())
                            !=100){
                        Toast.makeText(SecondInputActivity.this,"Your Final, Midterm and Assignment " +
                                "Weights for Course "+Integer.toString(i)+" don't add up to 100.",
                                Toast.LENGTH_SHORT).show();
                        proceedFlag = false;
                    } else {
                        Log.d("Second","Got Course "+Integer.toString(i)+" exam and assign weights");
                        temp.setFinalWt(Integer.parseInt(InputFieldIDs.get("Course "+Integer.toString(i)+" Final Weight").getText().toString()));
                        temp.setMidtermWt(Integer.parseInt(InputFieldIDs.get("Course "+Integer.toString(i)+" Midterm Weight").getText().toString()));
                        temp.setAssignmentsAndQuizzesWt(Integer.parseInt(InputFieldIDs.get("Course "+Integer.toString(i)+" Assignment Weight").getText().toString()));
                    }

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    if (TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Final Date").getText().toString().trim())){
                        Toast.makeText(SecondInputActivity.this,"You forgot to enter Course "+Integer.toString(i)+" Final Date",Toast.LENGTH_SHORT).show();
                        proceedFlag = false;
                    } else {
                        try {
                            Date fd = formatter.parse(InputFieldIDs.get("Course "+Integer.toString(i)+" Final Date").getText().toString());
                            LocalDate finaldate = fd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            if (!finaldate.isBefore(startdate) && !finaldate.isAfter(enddate)){
                                Log.d("Second","Got Course "+Integer.toString(i)+" Final Date");
                                temp.setFinalDate(fd);
                            } else {
                                Toast.makeText(SecondInputActivity.this,"Make sure Course "
                                        +Integer.toString(i)+" Final Date is in range",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (ParseException e) {
                            Toast.makeText(SecondInputActivity.this,"Please enter a valid Final Date " +
                                    "for Course "+Integer.toString(i),Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    if (TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Midterm Date 1").getText().toString().trim()) &&
                            TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Midterm Date 2").getText().toString().trim()) &&
                            TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Number Midterms").getText().toString().trim())){
                        Toast.makeText(SecondInputActivity.this,"Please enter some information about"+
                                " Course "+Integer.toString(i)+" Midterm Exam(s)",Toast.LENGTH_SHORT).show();
                        proceedFlag = false;
                    } else if (TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Midterm Date 1").getText().toString().trim()) &&
                            TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Midterm Date 2").getText().toString().trim())) {
                        try {
                            temp.calcMTDate(Integer.parseInt(InputFieldIDs.get("Course "+Integer.toString(i)+" Number Midterms").getText().toString()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ArrayList<Date> MTD = new ArrayList<Date>();

                        for (int j = 1; j <= 2; j++){

                            if (!TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Midterm Date "
                                    +Integer.toString(j)).getText().toString().trim())){
                                try {
                                    Date mt = formatter.parse(InputFieldIDs.get("Course "+Integer.toString(i)+" Midterm Date "+Integer.toString(j)).getText().toString());
                                    LocalDate midtermdate = mt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                    if (!midtermdate.isBefore(startdate) && !midtermdate.isAfter(enddate)){
                                        MTD.add(mt);
                                    } else {
                                        Toast.makeText(SecondInputActivity.this,"Make sure Course "
                                                        +Integer.toString(i)+" Midterm Dates are in range",
                                                Toast.LENGTH_SHORT).show();
                                        proceedFlag = false;
                                    }
                                } catch (ParseException e) {
                                    Toast.makeText(SecondInputActivity.this,"Please enter valid " +
                                                    "Midterm Dates for Course "+Integer.toString(i),
                                            Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        }

                        temp.setMidtermDates(MTD);
                    }

                    if (Integer.parseInt(InputFieldIDs.get("Course "+Integer.toString(i)+" Assignment Weight").getText().toString())
                            != 0 && TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Assignment 1 Date").getText().toString().trim())
                            && TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Assignment 2 Date").getText().toString().trim())
                            && TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Assignment 3 Date").getText().toString().trim())
                            && TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Assignment 4 Date").getText().toString().trim())
                            && TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Assignment 5 Date").getText().toString().trim())
                            && TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Assignment 6 Date").getText().toString().trim())
                            && TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Number Assignments").getText().toString().trim())){
                        Toast.makeText(SecondInputActivity.this,"Please enter some information about"+
                                " Course "+Integer.toString(i)+" Assignment(s)",Toast.LENGTH_SHORT).show();
                        proceedFlag = false;
                    } else if (TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Assignment 1 Date").getText().toString().trim())
                            && TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Assignment 2 Date").getText().toString().trim())
                            && TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Assignment 3 Date").getText().toString().trim())
                            && TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Assignment 4 Date").getText().toString().trim())
                            && TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Assignment 5 Date").getText().toString().trim())
                            && TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Assignment 6 Date").getText().toString().trim())) {
                        temp.calcAssignmentDates(Integer.parseInt(InputFieldIDs.get("Course "+Integer.toString(i)+" Number Assignments").getText().toString()));
                    } else {
                        ArrayList<Date> AD = new ArrayList<Date>();

                        for (int j = 1; j <= 6; j++){

                            if (!TextUtils.isEmpty(InputFieldIDs.get("Course "+Integer.toString(i)+" Assignment "
                                    + Integer.toString(j)+" Date").getText().toString().trim())){
                                try {
                                    Date ad = formatter.parse(InputFieldIDs.get("Course "+Integer.toString(i)+" Midterm Date 1").getText().toString());
                                    LocalDate assigndate = ad.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                    if (!assigndate.isBefore(startdate) && !assigndate.isAfter(enddate)){
                                        AD.add(ad);
                                    } else {
                                        Toast.makeText(SecondInputActivity.this,"Make sure Course "
                                                        +Integer.toString(i)+" Assignment Dates are in range",
                                                Toast.LENGTH_SHORT).show();
                                        proceedFlag = false;
                                    }
                                } catch (ParseException e) {
                                    Toast.makeText(SecondInputActivity.this,"Please enter valid " +
                                                    "Assignment Dates for Course "+Integer.toString(i),
                                            Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        }

                        temp.setAssignmentAndQuizDates(AD);
                    }

                    Courses.add(temp);
                }

                if (proceedFlag){
                    Intent intent = new Intent(SecondInputActivity.this, ThirdSummaryActivity.class);
                    intent.putExtra("StudyTime",StudyTime);
                    intent.putExtra("NumCourses",NumCourses);
                    for (int i = 1; i <= NumCourses; i++){
                        intent.putExtra("Course "+i,(Parcelable)Courses.get(i-1));
                    }
                    startActivity(intent);
                } else {
                    Toast.makeText(SecondInputActivity.this,"Please fix your " +
                                    "inputs and press the button again :).",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.d("SecondInputActivity", "onBackPressed Called");
        finish();
    }

}
