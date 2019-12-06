package com.example.structify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;

import java.util.ArrayList;

//This APP: Given semester start and end dates, the number of courses a student will take during the semester,
//information on all exam and assignment dates and credit percentages (i.e. worth or weight),
//the target amount of time per weekday/weekend a student is willing to study and even the relative importances
//of all of the courses, this app will generate a study schedule for a student in that semester that is underpinned
//by important exam and assignment dates and allocates study time (that the student has specified) based on percentage
// weight and relative importance over all courses that theoretically helps a student A) not miss an important due
//date and B) not over-study or over-commit to a given assignment or test given its worth in the overall semester
//overview. This app attempts to normalize the distribution of study time so that a student can ensure each assignment,
//test and project receives an adequate amount of attention and study ranges are based on the rules: 1) Final exam
//studying begins 2 weeks before the day of (allocated time distributed evenly over 13 days) and 2) Midterm exam
//studying begins 1 week before the day of.

//Main activity: Basic overview inputs about the semester are input (dates, preferences).
//Second Input Activity: Course data (exam/assignment dates and weights).
//Display activity: Shows the study allocations on a calendarview and includes option to add study schedule to
//Google Calendar via API.

public class MainActivity extends AppCompatActivity {

    //Structures for linking to .xml file through which we will get input for our methods
    private Button EnterCoursesBtn;
    private TextView start;
    private TextView end;
    private TextView weekday;
    private TextView weekend;
    private Spinner NumberCourses;

    //Global variables that we will use continually in methods
    private String startDate;
    private String endDate;
    private int NumCourses;
    private int StudyTime;

    //The layout where we will put all of the dynamic UI-generated fields
    private NestedScrollView Canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Link variables to layout
        EnterCoursesBtn = findViewById(R.id.enter_course_info);
        start = findViewById(R.id.startDate);
        end = findViewById(R.id.endDate);
        weekday = findViewById(R.id.WeekdayTime);
        weekend = findViewById(R.id.WeekendTime);
        NumberCourses = (Spinner) findViewById(R.id.NumCourses);
        Canvas = findViewById(R.id.canvas);

        //Set dropdown content of spinner and its style (check layout, drawable and style resource files)
        String[] spinner = new String[]{"1","2","3","4","5","6","7","8"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item,spinner);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        NumberCourses.setAdapter(adapter);
        NumberCourses.setPrompt("Please select");



        //Find another Github Android app that shows a calendar view to reverse-engineer

        //Use Calendar Trial to input reminders in to Google Maps API.

        setEnterCoursesBtnClick();
    }

    //Functionality for EnterCoursesBtn
    private void setEnterCoursesBtnClick(){
        EnterCoursesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDate = start.getText().toString();
                endDate = end.getText().toString();
                StudyTime =
                NumCourses = (int) NumberCourses.getSelectedItem();

                //If inputs are no good...
                if(NumCourses == 0){
                    Toast.makeText(MainActivity.this,"Enter a valid input", Toast.LENGTH_SHORT).show();
                }else{

                    //Output the fields for entering course information plus a button for activating the calculation
                    //and date storage functions
                    //loadUrlData(inputVal);
                }
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

