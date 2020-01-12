package com.example.structify;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


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
            Log.d("ImportGoogleCalendarTask","Starting background activities");
            addCalendarEvents();
            mActivity.updateResultsText("Data successfully imported to Google Calendar!");
            Log.d("ImportGoogleCalendarTask","Data successfully imported to Google Calendar!");

        } catch (Exception e) {
            mActivity.updateStatus("The following error occurred:\n" +
                    e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void addCalendarEvents() {

        //Map the event descriptions to dates, so that only one event is made for each day
        //that describes all of the study obligations and test events.
        Map<Date,String> Index = new HashMap<Date,String>();

        //For all courses...
        for (int i = 0; i < Courses.size(); i++){

            //Pull out all data from the course
            double course_time = Courses.get(i).getCourseWt()*StudyTime/100;
            double fa = Courses.get(i).FinalAllocation(course_time);
            double ma = Courses.get(i).MidtermAllocation(course_time)/(Courses.get(i)
                    .getMidtermDates().size());
            double aa = Courses.get(i).AssignmentAllocation(course_time)/(Courses.get(i)
                    .getAssignmentAndQuizDates().size());
            String name = Courses.get(i).getCourseName();
            Date fd = Courses.get(i).getFinalDate();
            ArrayList<Date> md = Courses.get(i).getMidtermDates();
            ArrayList<Date> ad = Courses.get(i).getAssignmentAndQuizDates();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            //Create and insert final events
            if (Index.containsKey(fd)){
                //If a reminder for the day has already been entered....
                String temp = Index.get(fd);
                Log.d("ImportGoogleCalendarTask", "Finding break in existing string: "+temp);
                int insert_pos = temp.indexOf("----S");
                Log.d("ImportGoogleCalendarTask", "Insert position will be "+insert_pos);
                String desc = temp.substring(0,insert_pos-1)+ name +" Final Exam"+"\n"+"\n"
                        +temp.substring(insert_pos,temp.length()-1);
                Index.replace(fd, desc);
            } else {
                //If no reminder for that day exists yet....
                String desc = "----Exams and Assignments----"+"\n"+"\n"+ name +" Final Exam"
                        +"\n"+"\n"+"----Studying----"+"\n"+"\n";
                Index.put(fd,desc);
            }

            //Go to the day of the final...
            Date date = new Date();
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(fd);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            int M, D, Y;
            String MM, DD;

            //Add final exam study reminders
            //for the 13 days before the final....
            for (int j = 0; j < 14; j++){

                //go to the previous day
                calendar.add(java.util.Calendar.DAY_OF_MONTH,-1);
                M = calendar.get(java.util.Calendar.MONTH);
                MM = Integer.toString(M+1);
                if (M+1<10){
                    MM = "0"+MM;
                }
                D = calendar.get(java.util.Calendar.DAY_OF_MONTH);
                DD = Integer.toString(D);
                if(D < 10){
                    DD = "0"+DD;
                }
                Y = calendar.get(java.util.Calendar.YEAR);
                try {
                    date = formatter.parse(Integer.toString(Y)+"-"+MM+"-"+DD);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (Index.containsKey(date)){
                    //if a reminder for the day exists already....
                    String desc = Index.get(date);
                    String add = "Study "+Math.round((fa/13)*10)/10.0+"  hours for "+ name +
                            "'s Final Exam"+"\n"+"\n";
                    Index.replace(date,desc+add);
                } else {
                    //If a reminder for the day does not exist yet...
                    String desc = "----Exams and Assignments----"+"\n"+"\n"+"----Studying----"+"\n"
                            +"\n"+"Study "+Math.round((fa/13)*10)/10.0+"hours for "+name+
                            "'s Final Exam"+"\n"+"\n";
                    Index.put(date,desc);
                }

            }

            //For all midterms in the course....
            for (int k = 0; k < md.size(); k++){
                //Create and insert midterm events
                if (Index.containsKey(md.get(k))){
                    //If a reminder for the day has already been entered....
                    String temp = Index.get(md.get(k));
                    Log.d("ImportGoogleCalendarTask", "Finding break in existing string: "+temp);
                    int insert_pos = temp.indexOf("----S");
                    Log.d("ImportGoogleCalendarTask", "Insert position will be "+insert_pos);
                    String desc = temp.substring(0,insert_pos-1)+ name +" Midterm Exam "+(k+1)+"\n"+
                            "\n"+temp.substring(insert_pos,temp.length()-1);
                    Index.replace(md.get(k),desc);
                } else {
                    //If no reminder for that day exists yet....
                    String desc = "----Exams and Assignments----"+"\n"+"\n"+ name +" Midterm Exam "
                            +(k+1)+"\n"+"\n"+"----Studying----"+"\n"+"\n";
                    Index.put(md.get(k),desc);
                }

                //Go to the day of the current midterm
                calendar.setTime(md.get(k));

                //Add midterm exam study reminders
                //for the 6 days before the midterm....
                for (int l = 0; l < 6; l++){
                    //go to the previous day
                    calendar.add(java.util.Calendar.DAY_OF_MONTH,-1);
                    M = calendar.get(java.util.Calendar.MONTH);
                    MM = Integer.toString(M+1);
                    if (M+1<10){
                        MM = "0"+MM;
                    }
                    D = calendar.get(java.util.Calendar.DAY_OF_MONTH);
                    DD = Integer.toString(D);
                    if(D < 10){
                        DD = "0"+DD;
                    }
                    Y = calendar.get(java.util.Calendar.YEAR);
                    try {
                        date = formatter.parse(Integer.toString(Y)+"-"+MM+"-"+DD);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (Index.containsKey(date)){
                        //if a reminder for the day exists already....
                        String desc = Index.get(date);
                        String add = "Study "+Math.round((ma/6)*10)/10.0+" hours for "+ name +
                                "'s Midterm Exam "+(k+1)+"\n"+"\n";
                        Index.replace(date,desc+add);
                    } else {
                        //If a reminder for the day does not exist yet...
                        String desc = "----Exams and Assignments----"+"\n"+"\n"+"----Studying----"+
                                "\n"+"\n"+"Study "+Math.round((ma/6)*10)/10.0+" hours for "+name+
                                "'s Midterm Exam "+(k+1)+"\n"+"\n";
                        Index.put(date,desc);
                    }
                }
            }

            //For all assignments in the course...
            for (int k = 0; k < ad.size(); k++){
                //Create and insert assignment events
                if (Index.containsKey(ad.get(k))){
                    //If a reminder for the day has already been entered....
                    String temp = Index.get(ad.get(k));
                    Log.d("ImportGoogleCalendarTask", "Finding break in existing string: "+temp);
                    int insert_pos = temp.indexOf("----S");
                    Log.d("ImportGoogleCalendarTask", "Insert position will be "+insert_pos);
                    String desc = temp.substring(0,insert_pos-1)+ name +" Assignment "+(k+1)+
                            " Due"+"\n"+"\n"+temp.substring(insert_pos,temp.length()-1);
                    Index.replace(ad.get(k),desc);
                } else {
                    //If no reminder for that day exists yet....
                    String desc = "----Exams and Assignments----"+"\n"+"\n"+ name +" Assignment "+
                            (k+1)+" Due"+"\n"+"\n"+"----Studying----"+"\n"+"\n";
                    Index.put(ad.get(k),desc);
                }

                //Go to the day of the current assignment
                calendar.setTime(ad.get(k));

                //Add assignment study reminders
                //for the 3 days before the assignment....
                for (int l = 0; l < 3; l++){
                    //go to the previous day
                    calendar.add(java.util.Calendar.DAY_OF_MONTH,-1);
                    M = calendar.get(java.util.Calendar.MONTH);
                    MM = Integer.toString(M+1);
                    if (M+1<10){
                        MM = "0"+MM;
                    }
                    D = calendar.get(java.util.Calendar.DAY_OF_MONTH);
                    DD = Integer.toString(D);
                    if(D < 10){
                        DD = "0"+DD;
                    }
                    Y = calendar.get(java.util.Calendar.YEAR);
                    try {
                        date = formatter.parse(Integer.toString(Y)+"-"+MM+"-"+DD);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (Index.containsKey(date)){
                        //if a reminder for the day exists already....
                        String desc = Index.get(date);
                        String add = "Spend "+Math.round((aa/3)*10)/10.0+" hours on "+ name +
                                "'s Assignment "+(k+1)+"\n"+"\n";
                        Index.replace(date,desc+add);
                    } else {
                        //If a reminder for the day does not exist yet...
                        String desc = "----Exams and Assignments----"+"\n"+"\n"+"----Studying----"
                                +"\n"+"\n"+"Spend "+Math.round((aa/3)*10)/10.0+" hours on "
                                + name +"'s Assignment "+(k+1)+"\n"+"\n";
                        Index.put(date,desc);
                    }
                }
            }
        }

        //Create a new Google Calendar
        com.google.api.services.calendar.model.Calendar newcalendar = new com.google.api.services
                .calendar.model.Calendar();
        newcalendar.setSummary("Structify Study Program");
        newcalendar.setTimeZone("America/Vancouver");
        String calendar_description = "Study reminders from Structify app";  //used to find calendarid later
        newcalendar.setDescription(calendar_description);

        //Insert the new calendar -- we will access it again in addCalendarEvent() using description as identifier to find calendarid
        //Insert to calendars() --> also inserted to CalendarList by default
        try {
            mService.calendars().insert(newcalendar).execute();
        } catch (UserRecoverableAuthIOException e){
            mActivity.startActivityForResult(e.getIntent(), mActivity.REQUEST_AUTHORIZATION);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //Retrieve the created calendar using Calendarlist.get(), using description as identifier
        String createdcalendarid = "";
        String pageToken = null;
        CalendarList calendarList = null;
        try {
            calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        //iterate through the CalendarList
        List<CalendarListEntry> items = calendarList.getItems();
        do {
            for (CalendarListEntry calendarListEntry : items) {
                //Find the calendar via its description as the Id is a randomly generated email address from Google
                if (calendarListEntry.getDescription().equals(calendar_description)){
                    createdcalendarid = calendarListEntry.getId();
                    Log.d("ImportGoogleCalendarTask","Created calendar's ID found from " +
                            "CalendarList: "+createdcalendarid);
                    break;
                }
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        //Extract the relevant CalendarListEntry, update it and then merge the changes back in
        CalendarListEntry structify_calendar = new CalendarListEntry();
        try {
            structify_calendar = mService.calendarList().get(createdcalendarid).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //Set color of the calendar to green same as the app (as per https://stackoverflow.com/questions/31979023/google-calender-api-setcolorid)
        structify_calendar.setColorId("9");

        //Add all the events to the calendar!
        Iterator IndexIterator = Index.entrySet().iterator();
        while (IndexIterator.hasNext()){
            Map.Entry mapElement = (Map.Entry)IndexIterator.next();
            addCalendarEvent((Date) mapElement.getKey(),(String) mapElement.getValue(),
                    createdcalendarid);
        }

        // Merge the altered calendar back into the user's CalendarList
        try {
            CalendarListEntry updatedCalendarListEntry = mService.calendarList()
                    .update(structify_calendar.getId(), structify_calendar)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void addCalendarEvent(Date date, String eventdesc, String calID){

        Event event = new Event()
                .setSummary("Structify")
                .setLocation("Vancouver, BC, Canada")
                .setDescription(eventdesc);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String DateStr = dateFormat.format(date);

        //Set event to occupy 9AM to 11AM on the day
        DateTime startDateTime = new DateTime(DateStr+"T09:00:00-08:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Vancouver");
        event.setStart(start);

        DateTime endDateTime = new DateTime(DateStr+"T11:00:00-08:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Vancouver");
        event.setEnd(end);

        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        //Add the event to the new calendar
        try {
            mService.events().insert(calID,event).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
