package com.example.structify;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class YourCalendarActivity extends AppCompatActivity {

    //Course data from previous activities
    private int NumCourses;
    private ArrayList<UniversityCourse> Courses;
    private int StudyTime;

    //Storing all dates paired with their events and study reminders encapsulated in SemesterDays
    //We will use the data structured in this way to dictate the arrangement of the dynamic UI.
    private Map<Date,SemesterDays> CalendarIndex;
    private ArrayList<SemesterDays> CalendarInfo;

    //UI components to be manipulated
    private TextView Year;
    private TextView Month;
    private ImageButton PreviousMonthButton;
    private ImageButton NextMonthButton;
    private Button ImportGoogleCalendarBtn;
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
        StudyTime = extras.getInt("StudyTime");

        //Link program to UI
        Year = findViewById(R.id.date_display_year);
        Month = findViewById(R.id.date_display_date);
        PreviousMonthButton = findViewById(R.id.calendar_prev_button);
        NextMonthButton = findViewById(R.id.calendar_next_button);
        CalendarCanvas = findViewById(R.id.calendar_canvas);

        //Initialize all dates in the Map, attached to SemesterDays that are blank but with day of week index
        LocalDate start = Courses.get(0).getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = Courses.get(0).getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        CalendarInfo = new ArrayList<SemesterDays>();
        CalendarIndex = new HashMap<Date, SemesterDays>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            SemesterDays temp = new SemesterDays();
            Date t = java.util.Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(t);
            temp.setDay_of_week(calendar.get(Calendar.DAY_OF_WEEK));
            CalendarInfo.add(temp);
            CalendarIndex.put(t,CalendarInfo.get(CalendarInfo.size()-1));
        }

        //Store all events and reminders in the SemesterDays instances contained in the Hashmap
        for (int i = 0; i < NumCourses; i++){
            //For ease of reading, pull out all the information first
            double course_time = Courses.get(i).getCourseWt()*StudyTime;
            double fa = Courses.get(i).FinalAllocation(course_time);
            double ma = Courses.get(i).MidtermAllocation(course_time)/(Courses.get(i).getMidtermDates().size());
            double aa = Courses.get(i).AssignmentAllocation(course_time)/(Courses.get(i).getAssignmentAndQuizDates().size());
            String name = Courses.get(i).getCourseName();
            Date fd = Courses.get(i).getFinalDate();
            ArrayList<Date> md = new ArrayList<Date>();
            md = Courses.get(i).getMidtermDates();
            ArrayList<Date> ad = new ArrayList<Date>();
            ad = Courses.get(i).getAssignmentAndQuizDates();

            //Add reminders and studying to SemesterDay objects in the Map
            CalendarIndex.get(fd).addExamEvent(name+" Final Exam");
            CalendarIndex.get(fd).addExamCourse(i);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fd);
            for (int k = 1; k <=13; k++){
                calendar.set(Calendar.DAY_OF_MONTH,-1);
                Date sd = calendar.getTime();
                CalendarIndex.get(sd).addStudyReminder("Study "+Math.round((fa/13)*10)/10.0+" hours per day for "
                        +name+"'s Final Exam");
                CalendarIndex.get(sd).addStudyCourse(i);
            }

            for (int j = 0; j < md.size(); j++){
                CalendarIndex.get(md.get(j)).addExamEvent(name+" Midterm Exam "+Integer.toString(j+1));
                CalendarIndex.get(md.get(j)).addExamCourse(i);
                calendar.setTime(md.get(j));
                for (int k = 1; k <=6; k++){
                    calendar.set(Calendar.DAY_OF_MONTH,-1);
                    Date sd = calendar.getTime();
                    CalendarIndex.get(md.get(j)).addStudyReminder("Study "+Math.round((ma/6)*10)/10.0+" hours "
                            +"per day for " +name+"'s Midterm Exam "+Integer.toString(j+1));
                    CalendarIndex.get(md.get(j)).addStudyCourse(i);
                }
            }

            for (int j = 0; j < ad.size(); j++){
                CalendarIndex.get(ad.get(j)).addExamEvent(name+" Assignment "+Integer.toString(j+1)+" Due");
                CalendarIndex.get(ad.get(j)).addExamCourse(i);
                calendar.setTime(ad.get(j));
                for (int k = 1; k <=3; k++){
                    calendar.set(Calendar.DAY_OF_MONTH,-1);
                    Date sd = calendar.getTime();
                    CalendarIndex.get(ad.get(j)).addStudyReminder("Spend "+Math.round((aa/3)*10)/10.0+" hours "
                            +"per day on " +name+"'s Assignment "+Integer.toString(j+1));
                    CalendarIndex.get(ad.get(j)).addStudyCourse(i);
                }
            }
        }

        //Inflate the weekviews in the calendars with all reminders and events as per the information in the Map
        UpdateCalendarCanvas(Courses.get(0).getStartDate());

        //Set the button functionality to toggle months and to add events to Google Calendar!
        SetPrevMonthButtonClick();
        SetNextMonthButtonClick();
        SetImportGoogleCalendarButtonClick();
    }

    //Populate CalendarCanvas area with calendar_row inflated layouts with reminders
    //current_date should be beginning of month
    public void UpdateCalendarCanvas(Date current_date){

        //Inflate enough rows to populate the calendar area for the month the current date is in
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current_date);
        int current_month = calendar.get(Calendar.MONTH);
        while(calendar.get(Calendar.MONTH)==current_month){
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View current_row = vi.inflate(R.layout.calendar_row,null);

            //Set the day numbers on the row view
            TextView Sunday = current_row.findViewById(R.id.sunday_number);
            TextView Monday = current_row.findViewById(R.id.monday_number);
            TextView Tuesday = current_row.findViewById(R.id.tuesday_number);
            TextView Wednesday = current_row.findViewById(R.id.wednesday_number);
            TextView Thursday = current_row.findViewById(R.id.thursday_number);
            TextView Friday = current_row.findViewById(R.id.friday_number);
            TextView Saturday = current_row.findViewById(R.id.saturday_number);
            RelativeLayout Events = current_row.findViewById(R.id.events);

            //If first day is not a Sunday, move to the Sunday
            if(CalendarIndex.get(calendar.getTime()).getDay_of_week()!=1){
                calendar.add(Calendar.DAY_OF_MONTH,-(CalendarIndex.get(calendar.getTime()).getDay_of_week()-1));
            }

            int DayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            Sunday.setText(Integer.toString(DayOfMonth));
            calendar.add(Calendar.DAY_OF_MONTH,1);
            DayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            Monday.setText(Integer.toString(DayOfMonth));
            calendar.add(Calendar.DAY_OF_MONTH,1);
            DayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            Tuesday.setText(Integer.toString(DayOfMonth));
            calendar.add(Calendar.DAY_OF_MONTH,1);
            DayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            Wednesday.setText(Integer.toString(DayOfMonth));
            calendar.add(Calendar.DAY_OF_MONTH,1);
            DayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            Thursday.setText(Integer.toString(DayOfMonth));
            calendar.add(Calendar.DAY_OF_MONTH,1);
            DayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            Friday.setText(Integer.toString(DayOfMonth));
            calendar.add(Calendar.DAY_OF_MONTH,1);
            DayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            Saturday.setText(Integer.toString(DayOfMonth));

            //Create textview reminders in the row view and set left, depth positions and width
            for (int i = 1; i <= 7; i++){

            }

            CalendarCanvas.addView(current_row);
        }
    }

    private void SetPrevMonthButtonClick() {
        PreviousMonthButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Do Stuff
            }

        });
    }

    private void SetNextMonthButtonClick() {
        NextMonthButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Do Stuff
            }

        });
    }

    //Button that imports all events in the CalendarInfoArrayList to Google Calendar
    private void SetImportGoogleCalendarButtonClick() {
        ImportGoogleCalendarBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Do Stuff
            }

        });
    }
}
