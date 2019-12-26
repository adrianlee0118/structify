package com.example.structify;

import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ImportGoogleCalendarTask extends AsyncTask <Void,Void,Void> {

    Calendar mService;
    private ImportGoogleCalendarActivity mActivity;
    private ArrayList<UniversityCourse> Courses;
    private int StudyTime;

    /**
     * Constructor.
     * @param activity ImportGoogleCalendarActivity that spawned this task.
     */
    ImportGoogleCalendarTask(ImportGoogleCalendarActivity activity, ArrayList<UniversityCourse> courses,
                             int studytime) {
        this.mActivity = activity;
        this.Courses = courses;
        this.StudyTime = studytime;
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

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    ImportGoogleCalendarActivity.REQUEST_AUTHORIZATION);

        } catch (Exception e) {
            mActivity.updateStatus("The following error occurred:\n" +
                    e.getMessage());
        }
        if (mActivity.mProgress.isShowing()) {
            mActivity.mProgress.dismiss();
        }
        return null;
    }

    public void addCalendarEvents() {

        //Insert a new calendar and add calendarevents

        Event event = new Event()
                .setSummary("Adrian's Birthday")
                .setLocation("Richmond, BC, Canada")
                .setDescription("Self-explanatory.");

        DateTime startDateTime = new DateTime("2020-01-18T09:00:00-08:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setStart(start);

        DateTime endDateTime = new DateTime("2020-01-18T17:00:00-08:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setEnd(end);

        /*String[] recurrence = new String[]{"RULE:FREQ=DAILY;COUNT=2"};
        event.setRecurrence(Arrays.asList(recurrence));*/

        EventAttendee[] attendees = new EventAttendee[]{
                new EventAttendee().setEmail("test@example.com"),
                new EventAttendee().setEmail("sbrin@example.com"),
        };
        event.setAttendees(Arrays.asList(attendees));

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
