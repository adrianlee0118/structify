package com.example.structify;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.widget.NestedScrollView;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

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
//Second Input Activity: Course data (exam/assignment dates and weights). Dynamic GUI magic.
//Display activity: Shows the study allocations on a calendarview and includes option to add study schedule to
//Google Calendar via API.

public class MainActivity extends AppCompatActivity {

    //Structures for linking to .xml file through which we will get input for our methods
    private Button EnterCoursesBtn;
    private TextView start;
    private TextView end;
    private EditText weekday;
    private EditText weekend;
    private Spinner NumberCourses;

    //Global variables that we will use continually in methods. These four in particular are passed through the Intent
    //to the second activity.
    private Date StartDate;
    private Date EndDate;
    private int NumCourses;
    private int StudyTime;

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

        //Set dropdown content of spinner and its style (check layout, drawable and style resource files)
        String[] spinner = new String[]{"1","2","3","4","5","6","7","8"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item,spinner);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        NumberCourses.setAdapter(adapter);
        NumberCourses.setPrompt("Please select");

        setEnterCoursesBtnClick();
    }

    //Functionality for EnterCoursesBtn
    private void setEnterCoursesBtnClick(){
        EnterCoursesBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                //Extract inputs
                NumCourses = (int) NumberCourses.getSelectedItem();

                //Count the weekdays and weekends and use it with study time preferences to calculate study time
                int wkdy = 0, wknd = 0;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    StartDate = formatter.parse(start.getText().toString());
                } catch (ParseException e) {
                    Log.d("setEnterCourseBtnClick","start date is not valid");
                    Toast.makeText(MainActivity.this,"Enter valid start date",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                try {
                    EndDate = formatter.parse(end.getText().toString());
                } catch (ParseException e) {
                    Log.d("setEnterCourseBtnClick", "end date is not valid");
                    Toast.makeText(MainActivity.this,"Enter valid end date",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                LocalDate start = StartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate end = EndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                    if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY){
                        wknd++;
                    } else {
                        wkdy++;
                    }
                }
                StudyTime = Integer.parseInt(weekday.getText().toString())*wkdy +
                        Integer.parseInt(weekend.getText().toString())*wknd;

                //Dates have been checked. Study times and number of courses can not be negative.
                if(NumCourses == 0 && StudyTime > 0){
                    Toast.makeText(MainActivity.this,"Please double-check number of courses and study time " +
                            "preferences are correct", Toast.LENGTH_SHORT).show();
                } else if (end.isAfter(start)) {
                    Toast.makeText(MainActivity.this,"Please make sure semester start date is before " +
                            "semester end date",Toast.LENGTH_SHORT).show();
                } else {
                    //Create intent for next activity, move all important data over
                    Toast.makeText(MainActivity.this,"Awesome!",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, SecondInputActivity.class);
                    intent.putExtra("StartDate", StartDate.getTime());
                    intent.putExtra("EndDate",EndDate.getTime());
                    intent.putExtra("NumCourses",NumCourses);
                    intent.putExtra("StudyTime",StudyTime);
                    startActivity(intent);
                }
            }
        });
    }

}

