package com.example.structify;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/*
*
* Main activity: Basic overview inputs about the semester are input (dates, preferences).
* Second Input Activity: Course data (exam/assignment dates and weights). Dynamic GUI magic.
* YourCalendar activity: Shows the study allocations on a calendarview and includes option to add study schedule to
* Google Calendar via API.
*
* */

public class MainActivity extends AppCompatActivity {

    private Button EnterCoursesBtn;
    private TextView start;
    private TextView end;
    private EditText weekday;
    private EditText weekend;
    private Spinner NumberCourses;
    ProgressDialog mProgress;

    private Date StartDate;
    private Date EndDate;
    private int NumCourses;
    private int StudyTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //All activities except for MainActivity are destroyed when exit is clicked in YourCalendarActivity
        //or ImportGoogleCalendarActivity, and this code will shut down MainActivity (and thus the whole app)
        //if MainActivity was reached via those routes.
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        EnterCoursesBtn = findViewById(R.id.enter_course_info);
        start = findViewById(R.id.startDate);
        end = findViewById(R.id.endDate);
        weekday = findViewById(R.id.WeekdayTime);
        weekend = findViewById(R.id.WeekendTime);
        NumberCourses = (Spinner) findViewById(R.id.NumCourses);

        String[] spinner = new String[]{"1","2","3","4","5","6","7","8"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item,spinner);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        NumberCourses.setAdapter(adapter);
        NumberCourses.setPrompt("Please select");

        setEnterCoursesBtnClick();
    }

    private void setEnterCoursesBtnClick(){
        EnterCoursesBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                NumCourses = Integer.parseInt( NumberCourses.getSelectedItem().toString() );

                int wkdy = 0, wknd = 0;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    StartDate = formatter.parse(start.getText().toString());
                } catch (ParseException e) {
                    Toast.makeText(MainActivity.this,"Enter valid start date",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                try {
                    EndDate = formatter.parse(end.getText().toString());
                } catch (ParseException e) {
                    Toast.makeText(MainActivity.this,"Enter valid end date",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                LocalDate start = StartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate end = EndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                try {
                    if (end.isAfter(start)){
                        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY){
                                wknd++;
                            } else {
                                wkdy++;
                            }
                        }
                    }
                } catch (RuntimeException e){
                    Toast.makeText(MainActivity.this,"Please make sure semester start date is before " +
                            "semester end date",Toast.LENGTH_SHORT).show();
                }
                StudyTime = Integer.parseInt(weekday.getText().toString())*wkdy +
                        Integer.parseInt(weekend.getText().toString())*wknd;

                if(StudyTime == 0){
                    Toast.makeText(MainActivity.this,"Please double-check number of courses and study time " +
                            "preferences are correct", Toast.LENGTH_SHORT).show();
                    throw new RuntimeException();
                } else {
                    mProgress.show();
                    Toast.makeText(MainActivity.this,"Awesome!",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,SecondInputActivity.class);
                    intent.putExtra("StartDate", StartDate.getTime());
                    intent.putExtra("EndDate",EndDate.getTime());
                    intent.putExtra("NumCourses",NumCourses);
                    intent.putExtra("StudyTime",StudyTime);
                    startActivity(intent);
                    mProgress.dismiss();
                }
            }
        });
    }

}

