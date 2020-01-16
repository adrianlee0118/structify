package com.example.structify;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class YourCalendarActivity extends AppCompatActivity {

    static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1003;

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

    //Lookups for assigning Month Strings and Colors
    private final String[] MonthLookup = {"Jan","Feb","Mar","Apr","May","Jun","July","Aug","Sep","Oct","Nov","Dec"};
    private final int[] ColorLookup = {Color.parseColor("#CC0000"),
            Color.parseColor("#000099"),Color.parseColor("#009900"),
            Color.parseColor("#CCCC00"),Color.parseColor("#CC0066"),
            Color.parseColor("#00CCCC"),Color.parseColor("#CC6600"),
            Color.parseColor("#202020")};

    //Call permissions from manifest for saving images to directory
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //UI components to be manipulated
    private LinearLayout Headings;
    private TextView Year;
    private TextView Month;
    private ImageButton PreviousMonthButton;
    private ImageButton NextMonthButton;
    private Button ImportGalleryButton;
    private Button ImportGoogleCalendarBtn;
    private Button DoNotImportBtn;
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
        Headings = findViewById(R.id.headings);
        Year = findViewById(R.id.date_display_year);
        Month = findViewById(R.id.date_display_date);
        PreviousMonthButton = findViewById(R.id.calendar_prev_button);
        NextMonthButton = findViewById(R.id.calendar_next_button);
        ImportGalleryButton = findViewById(R.id.import_to_gallery_button);
        ImportGoogleCalendarBtn = findViewById(R.id.import_to_google_calendar_button);
        DoNotImportBtn = findViewById((R.id.do_not_import_button));
        CalendarCanvas = findViewById(R.id.calendar_canvas);

        //Initialize all dates in the Map, attached to SemesterDays that are blank but with day of week index
        LocalDate start = Courses.get(0).getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = Courses.get(0).getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        CalendarIndex = new HashMap<Date, SemesterDays>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            SemesterDays temp = new SemesterDays();
            Date t = java.util.Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(t);
            temp.setDay_of_week(calendar.get(Calendar.DAY_OF_WEEK));
            CalendarIndex.put(t,temp);
        }

        //Store all events and reminders in the SemesterDays instances contained in the Hashmap
        for (int i = 0; i < NumCourses; i++){

            //For ease of reading, pull out all the information first
            double course_time = (Courses.get(i).getCourseWt()/100)*StudyTime;
            double fa = Courses.get(i).FinalAllocation(course_time);
            double ma = Courses.get(i).MidtermAllocation(course_time)/(Courses.get(i).getMidtermDates().size());
            double aa = Courses.get(i).AssignmentAllocation(course_time)/(Courses.get(i).getAssignmentAndQuizDates().size());
            String name = Courses.get(i).getCourseName();
            Date fd = Courses.get(i).getFinalDate();
            ArrayList<Date> md = Courses.get(i).getMidtermDates();
            ArrayList<Date> ad = Courses.get(i).getAssignmentAndQuizDates();

            //Add reminders and studying to SemesterDay objects in the Map
            CalendarIndex.get(fd).addExamEvent(name+" Final Exam");
            CalendarIndex.get(fd).addExamCourse(i);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fd);
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

            for (int j = 0; j < md.size(); j++){
                CalendarIndex.get(md.get(j)).addExamEvent(name+" Midterm Exam "+Integer.toString(j+1));
                CalendarIndex.get(md.get(j)).addExamCourse(i);
                calendar.setTime(md.get(j));
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
            }

            for (int j = 0; j < ad.size(); j++){
                CalendarIndex.get(ad.get(j)).addExamEvent(name+" Assignment "+Integer.toString(j+1)+" Due");
                CalendarIndex.get(ad.get(j)).addExamCourse(i);
                calendar.setTime(ad.get(j));
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
            }
        }

        //Inflate the weekviews in the calendars with all reminders and events as per the information in the Map
        CalendarEvents = new ArrayList<TextView>();
        UpdateCalendarCanvas(Courses.get(0).getStartDate());

        //Set the button functionality to toggle months and to add events to Google Calendar!
        SetPrevMonthButtonClick();
        PreviousMonthButton.setVisibility(View.INVISIBLE);
        PreviousMonthButton.setOnClickListener(null);  //because we are at the first month
        SetNextMonthButtonClick();
        SetImportGalleryButtonClick();
        SetImportGoogleCalendarButtonClick();
    }

    //Populate CalendarCanvas area with calendar_row inflated layouts with reminders
    //current_date should be beginning of month
    public void UpdateCalendarCanvas(Date current_date){

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
        try {
            startmark = formatter.parse(Integer.toString(Y)+"-"+MM+"-"+DD);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Year.setText(Integer.toString(calendar.get(Calendar.YEAR)));
        Month.setText(MonthLookup[calendar.get(Calendar.MONTH)]);
        int current_month = calendar.get(Calendar.MONTH);
        //From this point onward, current_date is used to keep track of the first day of the week of interest as we build the GUI week by week using calendar_row layouts
        while(calendar.get(Calendar.MONTH)==current_month){

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
            try {
                startmark = formatter.parse(Integer.toString(Y)+"-"+MM+"-"+DD);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //If first day is not a Sunday, move to the previous Sunday to fill the whole row with numbers
            if(CalendarIndex.get(startmark).getDay_of_week()!=1){
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

            //Move the cursor left_border_only to the beginning of the week
            calendar.setTime(startmark);
            if(CalendarIndex.get(startmark).getDay_of_week()!=1){
                calendar.add(Calendar.DAY_OF_MONTH,-(CalendarIndex.get(calendar.getTime()).getDay_of_week()-1));
            }

            //Create Map to keep track of the Textviews generated in the row
            //Note: The Textviews will be stored in CalendarEvents as well to ensure they persist.
            Map<String,TextView> RowRef = new HashMap<String,TextView>();
            //Create boolean matrix to track available spaces in the calendar_row grid to guide insertion of new events--values false by default
            boolean occupied[][] = new boolean[10][7];
            //Create lookup for gridlines--so we can access them based on a number index
            int[] xGridlines = new int[]{R.id.x0,R.id.x1,R.id.x2,R.id.x3,R.id.x4,R.id.x5,R.id.x6,
                    R.id.x7};
            int[] yGridlines = new int[]{R.id.y0,R.id.y1,R.id.y2,R.id.y3,R.id.y4,R.id.y5,R.id.y6,
                    R.id.y7,R.id.y8,R.id.y9,R.id.y10};

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
                try {
                    curr = formatter.parse(Integer.toString(Y)+"-"+MM+"-"+DD);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //Find the date in CalendarIndex
                if (CalendarIndex.get(curr) != null){

                    //Extend textviews for existing reminders
                    for (int j = 0; j < CalendarIndex.get(curr).getStudyReminders().size(); j++){
                        //We need to check if previous days in the row have the reminder in question because
                        //they last for periods longer than a day (they are stored by name in RowRef)
                        if (RowRef.containsKey(CalendarIndex.get(curr).getStudyReminders().get(j))) {
                            //If the reminder already exists in the row, extend the existing textbox
                            ConstraintLayout.LayoutParams tparams =
                                    (ConstraintLayout.LayoutParams) RowRef.get(CalendarIndex.get(curr)
                                            .getStudyReminders().get(j)).getLayoutParams();
                            tparams.rightToRight = xGridlines[i];
                            RowRef.get(CalendarIndex.get(curr).getStudyReminders().get(j))
                                    .setLayoutParams(tparams);
                            //find the y position of the existing textview, to be combined with current
                            //x position to mark in boolean matrix as occupied
                            int topID = tparams.topToTop;
                            int ypos;
                            for (ypos = 0; ypos < yGridlines.length; ypos++){
                                if (topID == yGridlines[ypos]){
                                    break;
                                }
                            }
                            occupied[ypos][i-1] = true;
                        }
                    }

                    //Create new textviews for new reminders that are not yet stored in RowRef
                    for (int j = 0; j < CalendarIndex.get(curr).getStudyReminders().size(); j++){
                        if (!RowRef.containsKey(CalendarIndex.get(curr).getStudyReminders().get(j))){
                            TextView temp = new TextView(this);
                            temp.setId(TextView.generateViewId());
                            ConstraintLayout.LayoutParams tparams = new ConstraintLayout.LayoutParams(0,0);
                            //set position of view using guidelines
                            int xpos = i-1;
                            int ypos;
                            for (ypos = 0; ypos < occupied.length; ypos++){
                                if (occupied[ypos][xpos] == false){
                                    break;
                                }
                            }
                            tparams.leftToLeft = xGridlines[xpos];
                            tparams.rightToRight = xGridlines[xpos+1];
                            tparams.topToTop = yGridlines[ypos];
                            tparams.bottomToBottom = yGridlines[ypos+1];
                            tparams.validate();
                            //Add content
                            temp.setText(CalendarIndex.get(curr).getStudyReminders().get(j));
                            temp.setTextSize(6);
                            temp.setTextColor(Color.WHITE);
                            temp.setBackgroundColor(ColorLookup[CalendarIndex.get(curr).getStudyCourseID().get(j)]);
                            //Add textbox to list for reference and to the view
                            CalendarEvents.add(temp);
                            Events.addView(temp,tparams);
                            RowRef.put(CalendarIndex.get(curr).getStudyReminders().get(j),temp);
                            //Mark space as occupied in the boolean matrix
                            occupied[ypos][xpos] = true;
                        }
                    }

                    //Create textviews for events, all of which are new
                    for (int j = 0; j < CalendarIndex.get(curr).getExamEvents().size(); j++){
                        //We don't have to check if a previous day has the same event--the way
                        //inputs are designed, events are unique and only last one day.
                        TextView temp = new TextView(this);
                        temp.setId(TextView.generateViewId());
                        ConstraintLayout.LayoutParams tparams =
                                new ConstraintLayout.LayoutParams(0,0);
                        //set position of view using guidelines
                        int xpos = i-1;
                        int ypos;
                        for (ypos = 0; ypos < occupied.length; ypos++){
                            if (occupied[ypos][xpos] == false){
                                break;
                            }
                        }
                        tparams.leftToLeft = xGridlines[xpos];
                        tparams.rightToRight = xGridlines[xpos+1];
                        tparams.topToTop = yGridlines[ypos];
                        tparams.bottomToBottom = yGridlines[ypos+1];
                        tparams.validate();
                        //Add content
                        temp.setText(CalendarIndex.get(curr).getExamEvents().get(j));
                        temp.setTextSize(6);
                        temp.setTextColor(Color.WHITE);
                        temp.setBackgroundColor(ColorLookup[CalendarIndex.get(curr)
                                .getExamCourseID().get(j)]);
                        //Add textbox to list for reference and to the view
                        CalendarEvents.add(temp);
                        Events.addView(temp,tparams);
                        RowRef.put(CalendarIndex.get(curr).getExamEvents().get(j),temp);
                        //Mark space as occupied in the boolean matrix
                        occupied[ypos][xpos] = true;
                    }

                }

                //Go to next day now that events and reminders for the current day have been added
                calendar.add(Calendar.DAY_OF_MONTH,1);
            }

            //Draw 6 vertical lines on top of everything to clearly delineate the days of the week
            for (int k = 1; k <= 6; k++){
                //For the Events ConstraintLayout Area...
                TextView temp = new TextView(this);
                temp.setId(TextView.generateViewId());
                ConstraintLayout.LayoutParams tparams = new ConstraintLayout.LayoutParams(0,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                tparams.leftToLeft = xGridlines[k];
                tparams.rightToRight = xGridlines[k+1];
                tparams.validate();
                Events.addView(temp,tparams);
                temp.setElevation(2);
                temp.setBackgroundResource(R.drawable.left_border_only);
            }

            //Now that the whole week's GUI has been built, add it to CalendarCanvas and move to the next day, the first day of the next month.
            CalendarCanvas.addView(current_row);
            calendar.add(Calendar.DAY_OF_MONTH,1);
            startmark = calendar.getTime();
        }

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
                calendar.set(Calendar.DAY_OF_MONTH,1);
                //Check if the first day is before the first date in the semester
                if (calendar.getTime().before(Courses.get(0).getStartDate())){
                    calendar.setTime(Courses.get(0).getStartDate());
                }
                UpdateCalendarCanvas(calendar.getTime());

                //If we were on the last month before, make the next month button visible
                if (NextMonthButton.getVisibility() == View.INVISIBLE){
                    NextMonthButton.setVisibility(View.VISIBLE);
                    SetNextMonthButtonClick();
                }

                //If we are now at the first month, make the previous month button invisible and unclickable
                Calendar startmonth = Calendar.getInstance();
                startmonth.setTime(Courses.get(0).getStartDate());
                if (calendar.get(Calendar.MONTH) == startmonth.get(Calendar.MONTH)){
                    PreviousMonthButton.setVisibility(View.INVISIBLE);
                    PreviousMonthButton.setOnClickListener(null);
                }

            }

        });
    }

    private void SetNextMonthButtonClick() {
        NextMonthButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("YourCalendarActivity", "NextMonthButtonClicked");
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
                calendar.set(Calendar.DAY_OF_MONTH,1);
                UpdateCalendarCanvas(calendar.getTime());

                if (PreviousMonthButton.getVisibility() == View.INVISIBLE){
                    PreviousMonthButton.setVisibility(View.VISIBLE);
                    SetPrevMonthButtonClick();
                }

                Calendar lastmonth = Calendar.getInstance();
                lastmonth.setTime(Courses.get(0).getEndDate());
                if (calendar.get(Calendar.MONTH) == lastmonth.get(Calendar.MONTH)){
                    NextMonthButton.setVisibility(View.INVISIBLE);
                    NextMonthButton.setOnClickListener(null);
                }
                Log.d("YourCalendarActivity", "NextMonthButton operations finished");
            }

        });
    }

    //Button that imports the Calendar previews as PNGs into the gallery
    private void SetImportGalleryButtonClick() {
        ImportGalleryButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {

                Log.d("YourCalendarActivity", "ImportGalleryButton Clicked");

                //Bring to first month
                UpdateCalendarCanvas(Courses.get(0).getStartDate());
                Log.d("YourCalendarActivity","ImportGallery: startmonth reached");

                //Check user permissions and request if needed
                isStoragePermissionGranted();

                Calendar startmonth = Calendar.getInstance();
                startmonth.setTime(Courses.get(0).getStartDate());
                Log.d("YourCalendarActivity", "ImportGalleryButton: First month is: "+startmonth);

                Calendar lastmonth = Calendar.getInstance();
                lastmonth.setTime(Courses.get(0).getEndDate());
                Log.d("YourCalendarActivity", "ImportGalleryButton: Last month is: "+lastmonth);

                final int month_duration = lastmonth.get(Calendar.MONTH)-startmonth.get(Calendar.MONTH)+1;
                Log.d("YourCalendarActivity","ImportGallery: month duration is "+month_duration);

                //Save all month views as a PNG
                for (int i = 0; i < month_duration; i++){
                    //Create the bitmap image of the current configuration of CalendarCanvas to be saved
                    LinearLayout canvas = CalendarCanvas;
                    canvas.setDrawingCacheEnabled(true);
                    canvas.setBackgroundColor(Color.WHITE);
                    canvas.buildDrawingCache(true);
                    Bitmap bitmap = Bitmap.createBitmap(canvas.getDrawingCache());

                    //Create the proper file output stream
                    String dir = Environment.getExternalStorageDirectory().toString();
                    File myDir = new File(dir+"/structify");
                    if(!myDir.exists()){
                        myDir.mkdirs();
                        Log.d("YourCalendarActivity","New directory made in pictures");
                    }
                    File output_image = new File(myDir, "Structify_"+(i+1)+".png");
                    FileOutputStream fos = null;

                    try {
                        fos = new FileOutputStream(output_image);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,
                                "Structify_"+i,"Strucify Month "+i);
                        Log.d("YourCalendarActivity","Calendar "+(i+1)+" added to Gallery");

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.d("YourCalendarActivity","Error importing to Gallery - File not found");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("YourCalendarActivity","Error importing to Gallery - IO Exception");
                    }

                    //Use a new thread join to ensure NextMonthButton is performed completely before actions continue
                    if (i != month_duration-1){
                        Thread t = new Thread() {
                            public void run() {
                                NextMonthButton.performClick();
                            }
                        };
                        t.start();
                        try {
                            t.join();
                        } catch (InterruptedException e) {
                            Log.d("YourCalendarActivity","Problems joining NextCalendarButton Thread");
                            e.printStackTrace();
                        }
                    }

                }

                Toast.makeText(YourCalendarActivity.this,"Calendars for all months have been imported to your gallery!", Toast.LENGTH_SHORT).show();
                Log.d("YourCalendarActivity","All calendar images imported to gallery");
            }

        });
    }

    /*static final int STEP_ONE_COMPLETE = 0;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case STEP_ONE_COMPLETE:
                    NextMonthButton.performClick();
                    break;
            }
        }
    };

    //Combines storing bitmap as png and nextmonth
    public void MonthToPNG(int i){

        CalendarCanvas.setBackgroundColor(Color.WHITE);
        CalendarCanvas.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(CalendarCanvas.getDrawingCache());

        //Create the proper file output stream
        String dir = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(dir+"/structify");
        if(!myDir.exists()){
            myDir.mkdirs();
            Log.d("YourCalendarActivity","New directory made in pictures");
        }
        File output_image = new File(myDir, "Structify_"+(i)+".png");
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(output_image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,
                    "Structify_"+i,"Structify Month "+i);
            Log.d("YourCalendarActivity","Calendar "+(i)+" added to Gallery");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("YourCalendarActivity","Error importing to Gallery - File not found");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("YourCalendarActivity","Error importing to Gallery - IO Exception");
        }

        Message msg = Message.obtain();
        msg.what = STEP_ONE_COMPLETE;
        handler.sendMessage(msg);
    }*/

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


    //Button that exits the app without importing anything to google calendar
    private void SetDoNotImportButtonClick() {
        DoNotImportBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("YourCalendarActivity","Finish Called");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
            }

        });
    }

    //Method for checking and requesting storage permissions explicitly at runtime
    private void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d("YourCalendarActivity","Write storage permission is available");
                Toast.makeText(YourCalendarActivity.this, "Write External Storage " +
                        "permission allows us to do store images. Please allow this permission in " +
                        "App Settings.", Toast.LENGTH_LONG).show();
            } else {
                Log.d("YourCalendarActivity","Write storage permission is not available");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.d("YourCalendarActivity","Write storage permission is available");
            Toast.makeText(YourCalendarActivity.this, "Write External Storage " +
                    "permission allows us to do store images. Please allow this permission in " +
                    "App Settings.", Toast.LENGTH_LONG).show();
        }
    }

    //Permission result callback
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.d("YourCalendarActivity","Write storage permission was granted");
            //resume tasks needing this permission
        } else {
            Log.d("YourCalendarActivity","Write storage permission request was denied");
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("YourCalendarActivity", "onBackPressed Called");
        finish();
    }
}
