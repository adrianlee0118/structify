package com.example.structify;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CalendarRow extends LinearLayout {

    // calendar components
    TextView Sunday;
    TextView Monday;
    TextView Tuesday;
    TextView Wednesday;
    TextView Thursday;
    TextView Friday;
    TextView Saturday;
    RelativeLayout Events;

    //track the depth
    int depth;

    // the reminders and events that need adding (String eventname, integer is the width of it)
    HashMap<String,TextView> NotificationIndex;
    ArrayList<TextView> Notifications;

    //Screen width markers and depth additions
    public static final int ScreenWidth = 411;
    public static final int DayWidth = 58;
    public static final int AddDepth = 10;

    public CalendarRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    /**
     * Load control xml layout
     */
    private void initControl(Context context, AttributeSet attrs)
    {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi.inflate(R.layout.calendar_row, this);
        assignUiElements();
    }

    private void assignUiElements() {
        // layout is inflated, assign local variables to components
        Sunday = findViewById(R.id.sunday_number);
        Monday = findViewById(R.id.monday_number);
        Tuesday = findViewById(R.id.tuesday_number);
        Wednesday = findViewById(R.id.wednesday_number);
        Thursday = findViewById(R.id.thursday_number);
        Friday = findViewById(R.id.friday_number);
        Saturday = findViewById(R.id.saturday_number);
        Events = findViewById(R.id.events);
    }

    /**
     * Display dates correctly in the textboxes,
     */
    public void updateCalendarRow(Map<Date,SemesterDays> CalendarIndex,Date beginning) {

        //Set starting depth
        depth = 5;

        //Set day of month labels in top row
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginning);
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

        calendar.setTime(beginning);
        //Create all notification graphics
        for (int i = 0; i < 7; i++){
            //Pull info for the days
            ArrayList<String> ER = CalendarIndex.get(calendar.getTime()).getExamEvents();
            ArrayList<Integer> ERID = CalendarIndex.get(calendar.getTime()).getExamCourseID();
            ArrayList<String> SR = CalendarIndex.get(calendar.getTime()).getStudyReminders();
            ArrayList<Integer> SRID = CalendarIndex.get(calendar.getTime()).getStudyCourseID();

            //Create or extend existing textboxes
            for (int j = 0; j < ER.size(); j++){

                
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
                lp.setMargins(0,0,0,0);
            }
        }
    }
}
