package com.example.structify;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class ImportGoogleCalendarTask extends AsyncTask <Void,Void,Void> {

    private ImportGoogleCalendarActivity mActivity;
    private ArrayList<UniversityCourse> Courses;
    private int StudyTime;
    Calendar mService;

    /**
     * Constructor.
     * @param activity ImportGoogleCalendarActivity that spawned this task.
     */
    ImportGoogleCalendarTask(ImportGoogleCalendarActivity activity, ArrayList<UniversityCourse> courses,
                             int studytime, Calendar mservice) {
        this.mActivity = activity;
        this.Courses = courses;
        this.StudyTime = studytime;
        this.mService = mservice;
    }

    /**
     * Background task to call Google Calendar API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            addCalendarEvents();
            mActivity.updateResultsText("Data successfully imported to Google Calendar!");

        } catch (Exception e) {
            mActivity.updateStatus("The following error occurred:\n" +
                    e.getMessage());
        }
        if (mActivity.mProgress.isShowing()) {
            mActivity.mProgress.dismiss();
        }
        return null;
    }

    public void addCalendarEvents() throws IOException {

        //Add the new calendar


        //Insert a new calendar and add calendarevents
        for (int i = 0; i < Courses.size(); i++){



            for (int j = 0; j < Courses.get(i).getMidtermDates().size(); j++){

            }

            for (int j = 0; j < Courses.get(i).getAssignmentAndQuizDates().size(); j++){

            }
        }

    }

    public void MakeEvent(Date date, String coursename, String workdescription){

        //Create the basic information depending on the type of work it is
        Event event = new Event();
        if (workdescription == "Final Exam"){
            event.setSummary(coursename+" "+workdescription);
            event.setDescription("Take the "+coursename+" "+workdescription);
        } else if (workdescription.substring(0,6) == "Midterm"){
            event.setSummary(coursename+" "+workdescription);
            event.setDescription("Take the "+coursename+" "+workdescription);
        } else {
            event.setSummary(coursename+" "+workdescription);
            event.setDescription(coursename+" "+workdescription+" due");
        }

        //Create the all day event date bounds
        Date startDate = date;
        Date endDate = new Date(startDate.getTime() + 86400000);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDateStr = dateFormat.format(startDate);
        String endDateStr = dateFormat.format(endDate);

        DateTime startDateTime = new DateTime(startDateStr);
        DateTime endDateTime = new DateTime(endDateStr);

        EventDateTime startEventDateTime = new EventDateTime().setDate(startDateTime);
        EventDateTime endEventDateTime = new EventDateTime().setDate(endDateTime);
        startEventDateTime.setTimeZone("America/Vancouver");
        endEventDateTime.setTimeZone("America/Vancouver");

        event.setStart(startEventDateTime);
        event.setEnd(endEventDateTime);

        //set the reminders
        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
        try {
            mService.events().insert(calendarId, event).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
