package com.example.structify;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.ParseException;
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

    //Store textviews from calendar rows so that they persist (visuals of our reminders).
    //We don't need to reference these except for when we put the content in.
    ArrayList<TextView> CalendarEvents;

    //Storing all dates paired with their events and study reminders encapsulated in SemesterDays
    //Semester days allows us to transform the data such that the Dates themselves are the index
    //by which we find information, rather than the courses.
    //We will use the data structured in this way to dictate the arrangement of the dynamic UI.
    private Map<Date,SemesterDays> CalendarIndex;
    private ArrayList<SemesterDays> CalendarInfo;

    //Lookups for assigning Month Strings and Colors
    private final String[] MonthLookup = {"Jan","Feb","Mar","Apr","May","Jun","July","Aug","Sep","Oct","Nov","Dec"};
    private final int[] ColorLookup = {Color.RED,Color.BLUE,Color.YELLOW,Color.GREEN,Color.MAGENTA,Color.CYAN,Color.BLACK,Color.GRAY};

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
        ImportGoogleCalendarBtn = findViewById(R.id.import_to_google_calendar_button);
        CalendarCanvas = findViewById(R.id.calendar_canvas);
        Log.d("YourCalendarActivity","GUI generated");

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
            Log.d("YourCalendarActivity","Added SemesterDays object for date: "+ date.toString());
        }
        Log.d("YourCalendarActivity","Catalogue of empty SemesterDays created and attached to CalendarIndex");

        //Store all events and reminders in the SemesterDays instances contained in the Hashmap
        for (int i = 0; i < NumCourses; i++){
            Log.d("YourCalendarActivity", "Populating CalendarIndex with information for course "+Courses.get(i).getCourseName());

            //For ease of reading, pull out all the information first
            double course_time = (Courses.get(i).getCourseWt()/100)*StudyTime;
            Log.d("YourCalendarActivity", "Course time is "+course_time);
            double fa = Courses.get(i).FinalAllocation(course_time);
            Log.d("YourCalendarActivity", "Final allocation is "+fa);
            double ma = Courses.get(i).MidtermAllocation(course_time)/(Courses.get(i).getMidtermDates().size());
            Log.d("YourCalendarActivity", "Midterm allocation per midterm is "+ma);
            double aa = Courses.get(i).AssignmentAllocation(course_time)/(Courses.get(i).getAssignmentAndQuizDates().size());
            Log.d("YourCalendarActivity", "Assignment allocation per assignment is "+aa);
            String name = Courses.get(i).getCourseName();
            Date fd = Courses.get(i).getFinalDate();
            ArrayList<Date> md = Courses.get(i).getMidtermDates();
            ArrayList<Date> ad = Courses.get(i).getAssignmentAndQuizDates();

            //Add reminders and studying to SemesterDay objects in the Map
            CalendarIndex.get(fd).addExamEvent(name+" Final Exam");
            CalendarIndex.get(fd).addExamCourse(i);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fd);
            Log.d("YourCalendarActivity","Final Date for Course "+(i+1)+" Added to CalendarIndex");
            //As per rules, studying for final exams spans two weeks and so reminders on all those days must be added
            for (int k = 1; k <=13; k++){
                if (calendar.get(Calendar.DAY_OF_MONTH) == 1){
                    calendar.add(Calendar.MONTH,-1);
                    int last_day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    calendar.set(Calendar.DAY_OF_MONTH,last_day);
                } else {
                    calendar.add(Calendar.DAY_OF_MONTH,-1);
                }
                Date sd = calendar.getTime();
                CalendarIndex.get(sd).addStudyReminder("Study "+Math.round((fa/13)*10)/10.0+" hours per day for "
                        +name+"'s Final Exam");
                CalendarIndex.get(sd).addStudyCourse(i);
            }
            Log.d("YourCalendarActivity","Final Reminders for Course "+(i+1)+" Added to CalendarIndex");

            for (int j = 0; j < md.size(); j++){
                CalendarIndex.get(md.get(j)).addExamEvent(name+" Midterm Exam "+Integer.toString(j+1));
                CalendarIndex.get(md.get(j)).addExamCourse(i);
                calendar.setTime(md.get(j));
                Log.d("YourCalendarActivity","Course "+(i+1)+" Midterm "+(j+1)+" Date Added to CalendarIndex");
                //As per rules, studying for midterm exams spans 1 week and so reminders on those days must be added
                for (int k = 1; k <=6; k++){
                    if (calendar.get(Calendar.DAY_OF_MONTH) == 1){
                        calendar.add(Calendar.MONTH,-1);
                        int last_day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        calendar.set(Calendar.DAY_OF_MONTH,last_day);
                    } else {
                        calendar.add(Calendar.DAY_OF_MONTH,-1);
                    }
                    Date sd = calendar.getTime();
                    CalendarIndex.get(sd).addStudyReminder("Study "+Math.round((ma/6)*10)/10.0+" hours "
                            +"per day for " +name+"'s Midterm Exam "+Integer.toString(j+1));
                    CalendarIndex.get(sd).addStudyCourse(i);
                }
                Log.d("YourCalendarActivity","Course "+(i+1)+" Midterm "+(j+1)+" Reminders Added to CalendarIndex");
            }

            for (int j = 0; j < ad.size(); j++){
                Log.d("YourCalendarActivity","Searching for date "+ad.get(j)+" in CalendarIndex");
                CalendarIndex.get(ad.get(j)).addExamEvent(name+" Assignment "+Integer.toString(j+1)+" Due");
                CalendarIndex.get(ad.get(j)).addExamCourse(i);
                calendar.setTime(ad.get(j));
                Log.d("YourCalendarActivity","Course "+(i+1)+" Assignment "+(j+1)+" Date Added to CalendarIndex");
                //As per rules, studying and working on assignments and quiz level tests spans 3 days and reminders must be added
                for (int k = 1; k <=3; k++){
                    if (calendar.get(Calendar.DAY_OF_MONTH) == 1){
                        calendar.add(Calendar.MONTH,-1);
                        int last_day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        calendar.set(Calendar.DAY_OF_MONTH,last_day);
                    } else {
                        calendar.add(Calendar.DAY_OF_MONTH,-1);
                    }
                    Date sd = calendar.getTime();
                    CalendarIndex.get(sd).addStudyReminder("Spend "+Math.round((aa/3)*10)/10.0+" hours "
                            +"per day on " +name+"'s Assignment "+Integer.toString(j+1));
                    CalendarIndex.get(sd).addStudyCourse(i);
                }
                Log.d("YourCalendarActivity","Course "+(i+1)+" Assignment "+(j+1)+" Reminders Added to CalendarIndex");
            }
        }

        //Inflate the weekviews in the calendars with all reminders and events as per the information in the Map
        CalendarEvents = new ArrayList<TextView>();
        Log.d("YourCalendarActivity","Data in CalendarIndex Completed. TextView index CalendarEvents created.");
        UpdateCalendarCanvas(Courses.get(0).getStartDate());

        //Set the button functionality to toggle months and to add events to Google Calendar!
        SetPrevMonthButtonClick();
        PreviousMonthButton.setVisibility(View.INVISIBLE);
        PreviousMonthButton.setOnClickListener(null);  //because we are at the first month
        SetNextMonthButtonClick();
        SetImportGoogleCalendarButtonClick();
        Log.d("YourCalendarActivity","All button functionality set.");
    }

    //Populate CalendarCanvas area with calendar_row inflated layouts with reminders
    //current_date should be beginning of month
    public void UpdateCalendarCanvas(Date current_date){

        Log.d("YourCalendarActivity","UpdateCalendarCanvas() started.");

        //Clear CalendarCanvas
        CalendarCanvas.removeAllViews();

        //Inflate enough rows to populate the calendar area for the month the current date is in
        //Set text of headers
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current_date);
        Date startmark = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        int M = calendar.get(Calendar.MONTH);
        String MM = Integer.toString(M+1);
        if (M+1<10){
            MM = "0"+MM;
        }
        int D = calendar.get(Calendar.DAY_OF_MONTH);
        String DD = Integer.toString(D);
        if(D < 10){
            DD = "0"+DD;
        }
        int Y = calendar.get(Calendar.YEAR);
        Log.d("YourCalendarActivity-UpdateCalendarCanvas","Date to search in CalendarIndex is "+Y+"-"+MM+"-"+DD);
        try {
            startmark = formatter.parse(Integer.toString(Y)+"-"+MM+"-"+DD);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Log.d("YourCalendarActivity-UpdateCalendarCanvas","Date at start is "+startmark);
        Year.setText(Integer.toString(calendar.get(Calendar.YEAR)));
        Month.setText(MonthLookup[calendar.get(Calendar.MONTH)]);
        Log.d("YourCalendarActivity-UpdateCalendarCanvas","Year and Month Headers Set");
        int current_month = calendar.get(Calendar.MONTH);
        Log.d("YourCalendarActivity-UpdateCalendarCanvas","Current month is "+calendar.get(Calendar.MONTH));
        //From this point onward, current_date is used to keep track of the first day of the week of interest as we build the GUI week by week using calendar_row layouts
        while(calendar.get(Calendar.MONTH)==current_month){

            Log.d("YourCalendarActivity-UpdateCalendarCanvas","Start walking through all dates in the month");

            //Inflate the calendar_row view -- calendar GUI is built row by row using the calendar_row view template layout
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View current_row = vi.inflate(R.layout.calendar_row,null);

            //Attach the textviews on calendar_row view so they can be accessed
            TextView Sunday = current_row.findViewById(R.id.sunday_number);
            TextView Monday = current_row.findViewById(R.id.monday_number);
            TextView Tuesday = current_row.findViewById(R.id.tuesday_number);
            TextView Wednesday = current_row.findViewById(R.id.wednesday_number);
            TextView Thursday = current_row.findViewById(R.id.thursday_number);
            TextView Friday = current_row.findViewById(R.id.friday_number);
            TextView Saturday = current_row.findViewById(R.id.saturday_number);
            ConstraintLayout Events = current_row.findViewById(R.id.events);

            M = calendar.get(Calendar.MONTH);
            MM = Integer.toString(M+1);
            if (M+1<10){
                MM = "0"+MM;
            }
            D = calendar.get(Calendar.DAY_OF_MONTH);
            DD = Integer.toString(D);
            if(D < 10){
                DD = "0"+DD;
            }
            Y = calendar.get(Calendar.YEAR);
            Log.d("YourCalendarActivity-UpdateCalendarCanvas","Date to search in CalendarIndex is "+Y+"-"+MM+"-"+DD);
            try {
                startmark = formatter.parse(Integer.toString(Y)+"-"+MM+"-"+DD);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //If first day is not a Sunday, move to the previous Sunday to fill the whole row with numbers
            if(CalendarIndex.get(startmark).getDay_of_week()!=1){
                Log.d("YourCalendarActivity-UpdateCalendarCanvas","calendar_row day walked back to a Sunday, "+startmark);
                calendar.add(Calendar.DAY_OF_MONTH,-(CalendarIndex.get(startmark).getDay_of_week()-1));
            }

            //Assign numbers to each of the days in the row - text size is taken care of in the calendar_row template
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
            Log.d("YourCalendarActivity-UpdateCalendarCanvas","Numbers in calendar_row set");

            //Move the cursor back to the beginning of the week
            calendar.setTime(startmark);
            if(CalendarIndex.get(startmark).getDay_of_week()!=1){
                Log.d("YourCalendarActivity-UpdateCalendarCanvas","calendar_row day walked back to a Sunday, "+startmark);
                calendar.add(Calendar.DAY_OF_MONTH,-(CalendarIndex.get(calendar.getTime()).getDay_of_week()-1));
            }

            //Create Map to keep track of the Textviews generated in the row, which will be stored in the
            //global ArrayList variable to ensure that the views persist
            Map<String,TextView> RowRef = new HashMap<String,TextView>();

            //Create textview reminders in the row view and set left, depth positions and width
            for (int i = 1; i <= 7; i++){

                //Create current date in same format as Date in CalendarIndex:
                Date curr = new Date();
                M = calendar.get(Calendar.MONTH);
                MM = Integer.toString(M+1);
                if (M+1<10){
                    MM = "0"+MM;
                }
                D = calendar.get(Calendar.DAY_OF_MONTH);
                DD = Integer.toString(D);
                if(D < 10){
                    DD = "0"+DD;
                }
                Y = calendar.get(Calendar.YEAR);
                Log.d("YourCalendarActivity-UpdateCalendarCanvas","Date to search in CalendarIndex is "+Y+"-"+MM+"-"+DD);
                try {
                    curr = formatter.parse(Integer.toString(Y)+"-"+MM+"-"+DD);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.d("YourCalendarActivity-UpdateCalendarCanvas","Making textviews for Day "+i+" schedule in the row, date: "+curr);


                //Find the date in CalendarIndex
                if (CalendarIndex.get(curr) != null){

                    Log.d("YourCalendarActivity-UpdateCalendarCanvas","Date found: "+Y+"-"+MM+"-"+DD);
                    int DayWidth = 58;
                    int AddDepth = 10;
                    int margin_top = 0; //tracking top margin for new textviews

                    //Create textviews for events, assign colors and add to CalendarEvents, mapped to RowRef
                    for (int j = 0; j < CalendarIndex.get(curr).getExamEvents().size(); j++){
                        //We don't have to check if a previous day has the same event--the way inputs are designed,
                        //events are unique and only last one day.
                        Log.d("YourCalendarActivity-UpdateCalendarCanvas","ExamEvents found for date "+Y+"-"+MM+"-"+DD);
                        TextView temp = new TextView(this);
                        ConstraintLayout.LayoutParams tparams = new ConstraintLayout.LayoutParams(0,0);
                        //set position at left of current day with current depth margin_top
                        tparams.setMargins((i-1)*DayWidth,margin_top,0,0);
                        temp.setLayoutParams(tparams);
                        margin_top+=AddDepth;
                        //make textbox occupy width of the current day in current_row, set text and color
                        temp.setText(CalendarIndex.get(curr).getExamEvents().get(j));
                        temp.setTextSize(5);
                        temp.setTextColor(Color.WHITE);
                        temp.setBackgroundColor(ColorLookup[CalendarIndex.get(curr).getExamCourseID().get(j)]);
                        //Add textbox to list for reference and to the view
                        CalendarEvents.add(temp);
                        Events.addView(temp,tparams);
                        RowRef.put(CalendarIndex.get(curr).getExamEvents().get(j),temp);
                        Log.d("YourCalendarActivity-UpdateCalendarCanvas","Exam event added to Calendar Graphic: "+CalendarIndex.get(curr).getExamEvents().get(j));
                    }

                    //Create textviews for reminders, assign colors and add to CalendarEvents, mapped to RowRef
                    for (int j = 0; j < CalendarIndex.get(curr).getStudyReminders().size(); j++){
                        //We need to check if previous days in the row have the reminder in question because
                        //they last for periods longer than a day (they are stored by name in RowRef)
                        //If the reminder does not exist in the row, make a new textbox
                        Log.d("YourCalendarActivity-UpdateCalendarCanvas","StudyReminders found for date "+Y+"-"+MM+"-"+DD);
                        if (!RowRef.containsKey(CalendarIndex.get(curr).getStudyReminders().get(j))){
                            TextView temp = new TextView(this);
                            ConstraintLayout.LayoutParams tparams = new ConstraintLayout.LayoutParams(DayWidth,10);
                            //set position at left of current day with current depth margin_top
                            tparams.setMargins((i-1)*DayWidth,margin_top,7*DayWidth-i*DayWidth,0);
                            temp.setLayoutParams(tparams);
                            margin_top+=AddDepth;
                            //make textbox occupy width of the current day in current_row, set text and color
                            temp.setText(CalendarIndex.get(curr).getStudyReminders().get(j));
                            temp.setTextSize(5);
                            temp.setTextColor(Color.WHITE);
                            temp.setBackgroundColor(ColorLookup[CalendarIndex.get(curr).getStudyCourseID().get(j)]);
                            //Add textbox to list for reference and to the view
                            CalendarEvents.add(temp);
                            Events.addView(temp,tparams);
                            RowRef.put(CalendarIndex.get(curr).getStudyReminders().get(j),temp);
                            Log.d("YourCalendarActivity-UpdateCalendarCanvas","Study reminder event added to calendar graphic: "+CalendarIndex.get(curr).getStudyReminders().get(j));
                        } else {
                            //If the reminder already exists in the row, extend the existing textbox
                            int new_width = RowRef.get(CalendarIndex.get(curr).getStudyReminders().get(j)).getWidth()
                                    +DayWidth;
                            RowRef.get(CalendarIndex.get(curr).getStudyReminders().get(j)).setWidth(new_width);
                            Log.d("YourCalendarActivity-UpdateCalendarCanvas","Existing study reminder event extended in calendar graphic: "+CalendarIndex.get(curr).getStudyReminders().get(j));

                        }
                    }

                }

                //Go to next day now that events and reminders for the current day have been added
                calendar.add(Calendar.DAY_OF_MONTH,1);
            }

            //Now that the whole week's GUI has been built, add it to CalendarCanvas and move to the next day, the first day of the next month.
            CalendarCanvas.addView(current_row);
            calendar.add(Calendar.DAY_OF_MONTH,1);
            startmark = calendar.getTime();
        }

        Log.d("YourCalendarActivity","End of UpdateCalendarCanvas reached");
    }

    private void SetPrevMonthButtonClick() {
        PreviousMonthButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Update the calendar area to show the next month
                //Note: UpdateCalendarCanvas clears the plot area and updates the headers
                String mo = Month.getText().toString();
                int month = 0;
                for (int i = 0; i < MonthLookup.length; i++){
                    if (MonthLookup[i] == mo){
                        month = i;
                    }
                }
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MONTH, month);
                calendar.add(Calendar.MONTH,-1);
                UpdateCalendarCanvas(calendar.getTime());

                //If we were on the last month before, make the next month button visible
                if (NextMonthButton.getVisibility() == View.INVISIBLE){
                    NextMonthButton.setVisibility(View.VISIBLE);
                    SetNextMonthButtonClick();
                    Log.d("YourCalendarActivity","NextMonthButton made Visible");
                }

                //If we are now at the first month, make the previous month button invisible and unclickable
                Calendar startmonth = Calendar.getInstance();
                startmonth.setTime(Courses.get(0).getStartDate());
                if (calendar.get(Calendar.MONTH) == startmonth.get(Calendar.MONTH)){
                    PreviousMonthButton.setVisibility(View.INVISIBLE);
                    PreviousMonthButton.setOnClickListener(null);
                    Log.d("YourCalendarActivity","NextMonthButton made Invisible");
                }

                Log.d("YourCalendarActivity","LastMonth method successfully run");
            }

        });
    }

    private void SetNextMonthButtonClick() {
        NextMonthButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String mo = Month.getText().toString();
                int month = 0;
                for (int i = 0; i < MonthLookup.length; i++){
                    if (MonthLookup[i] == mo){
                        month = i;
                    }
                }
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MONTH, month);
                calendar.add(Calendar.MONTH,1);
                UpdateCalendarCanvas(calendar.getTime());

                if (PreviousMonthButton.getVisibility() == View.INVISIBLE){
                    PreviousMonthButton.setVisibility(View.VISIBLE);
                    SetPrevMonthButtonClick();
                    Log.d("YourCalendarActivity","PrevMonthButton made Visible");
                }

                Calendar lastmonth = Calendar.getInstance();
                lastmonth.setTime(Courses.get(0).getEndDate());
                if (calendar.get(Calendar.MONTH) == lastmonth.get(Calendar.MONTH)){
                    NextMonthButton.setVisibility(View.INVISIBLE);
                    NextMonthButton.setOnClickListener(null);
                    Log.d("YourCalendarActivity","PrevMonthButton made Invisible");
                }

                Log.d("YourCalendarActivity","NextMonth method successfully run");
            }

        });
    }

    //Button that imports all events in the CalendarInfoArrayList to Google Calendar
    private void SetImportGoogleCalendarButtonClick() {
        ImportGoogleCalendarBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(YourCalendarActivity.this, ImportGoogleCalendarActivity.class);
                intent.putExtra("NumCourses",NumCourses);
                for (int i = 1; i <= NumCourses; i++){
                    intent.putExtra("Course "+i,(Parcelable)Courses.get(i-1));
                }
                intent.putExtra("StudyTime",StudyTime);
                startActivity(intent);
            }

        });
    }

    

}
