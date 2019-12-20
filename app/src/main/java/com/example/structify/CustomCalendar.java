package com.example.structify;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class CustomCalendar extends LinearLayout {

    // calendar components
    LinearLayout header;
    ImageView btnPrev;
    ImageView btnNext;
    TextView txtDisplayDate;
    TextView txtDateYear;
    GridView gridView;

    public CustomCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    private void assignUiElements() {
        // layout is inflated, assign local variables to components
        header = findViewById(R.id.headings);
        btnPrev = findViewById(R.id.calendar_prev_button);
        btnNext = findViewById(R.id.calendar_next_button);
        txtDateYear = findViewById(R.id.date_display_year);
        txtDisplayDate = findViewById(R.id.date_display_date);
        gridView = findViewById(R.id.calendar_grid);
    }

    /**
     * Load control xml layout
     */
    private void initControl(Context context, AttributeSet attrs)
    {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi.inflate(R.layout.activity_yourcalendar, this);
        assignUiElements();
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar(HashSet<Date> events, Date date)
    {
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);                            //Set the day to the 1st of month
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;   //shift 1 because days of week 1-indexed from Sunday, the int is for 0-indexing in our ArrayList from the first Sunday
        //sample was shift 2 (minus 2) because they indexed 0 at Monday not Sunday.

        // Subtract days (adding a negative day is move back in time) so that we start from a Sunday
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // Add the dates to the array for the entire month
        while (cells.size() < calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        {
            //Date array grows until it's as large as the month's size
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);   //moving to the next day, adding 1
        }

        // update grid using Date array cells, Hashmap events input and an Adapter
        gridView.setAdapter(new CalendarAdapter(getContext(), cells, events));

        // update title
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE,d MMM,yyyy");
        String[] dateToday = sdf.format(date.getTime()).split(",");
        txtDisplayDate.setText(dateToday[1]);
        txtDateYear.setText(dateToday[2]);
    }
}
