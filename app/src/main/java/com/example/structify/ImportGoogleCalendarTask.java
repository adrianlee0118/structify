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

        Map<Date,String> Index = new HashMap<Date,String>();

        for (int i = 0; i < Courses.size(); i++){

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

            if (Index.containsKey(fd)){

                String temp = Index.get(fd);
                int insert_pos = temp.indexOf("----S");
                String desc = temp.substring(0,insert_pos-1)+ name +" Final Exam"+"\n"+"\n"
                        +temp.substring(insert_pos,temp.length()-1);
                Index.replace(fd, desc);
            } else {

                String desc = "----Exams and Assignments----"+"\n"+"\n"+ name +" Final Exam"
                        +"\n"+"\n"+"----Studying----"+"\n"+"\n";
                Index.put(fd,desc);
            }

            Date date = new Date();
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(fd);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            int M, D, Y;
            String MM, DD;

            for (int j = 0; j < 14; j++){

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

                    String desc = Index.get(date);
                    String add = "Study "+Math.round((fa/13)*10)/10.0+"  hours for "+ name +
                            "'s Final Exam"+"\n"+"\n";
                    Index.replace(date,desc+add);
                } else {

                    String desc = "----Exams and Assignments----"+"\n"+"\n"+"----Studying----"+"\n"
                            +"\n"+"Study "+Math.round((fa/13)*10)/10.0+"hours for "+name+
                            "'s Final Exam"+"\n"+"\n";
                    Index.put(date,desc);
                }

            }

            for (int k = 0; k < md.size(); k++){

                if (Index.containsKey(md.get(k))){

                    String temp = Index.get(md.get(k));
                    int insert_pos = temp.indexOf("----S");
                    String desc = temp.substring(0,insert_pos-1)+ name +" Midterm Exam "+(k+1)+"\n"+
                            "\n"+temp.substring(insert_pos,temp.length()-1);
                    Index.replace(md.get(k),desc);
                } else {

                    String desc = "----Exams and Assignments----"+"\n"+"\n"+ name +" Midterm Exam "
                            +(k+1)+"\n"+"\n"+"----Studying----"+"\n"+"\n";
                    Index.put(md.get(k),desc);
                }

                calendar.setTime(md.get(k));

                for (int l = 0; l < 6; l++){

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

                        String desc = Index.get(date);
                        String add = "Study "+Math.round((ma/6)*10)/10.0+" hours for "+ name +
                                "'s Midterm Exam "+(k+1)+"\n"+"\n";
                        Index.replace(date,desc+add);
                    } else {

                        String desc = "----Exams and Assignments----"+"\n"+"\n"+"----Studying----"+
                                "\n"+"\n"+"Study "+Math.round((ma/6)*10)/10.0+" hours for "+name+
                                "'s Midterm Exam "+(k+1)+"\n"+"\n";
                        Index.put(date,desc);
                    }
                }
            }

            for (int k = 0; k < ad.size(); k++){

                if (Index.containsKey(ad.get(k))){

                    String temp = Index.get(ad.get(k));
                    int insert_pos = temp.indexOf("----S");
                    String desc = temp.substring(0,insert_pos-1)+ name +" Assignment "+(k+1)+
                            " Due"+"\n"+"\n"+temp.substring(insert_pos,temp.length()-1);
                    Index.replace(ad.get(k),desc);
                } else {

                    String desc = "----Exams and Assignments----"+"\n"+"\n"+ name +" Assignment "+
                            (k+1)+" Due"+"\n"+"\n"+"----Studying----"+"\n"+"\n";
                    Index.put(ad.get(k),desc);
                }


                calendar.setTime(ad.get(k));

                for (int l = 0; l < 3; l++){

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

                        String desc = Index.get(date);
                        String add = "Spend "+Math.round((aa/3)*10)/10.0+" hours on "+ name +
                                "'s Assignment "+(k+1)+"\n"+"\n";
                        Index.replace(date,desc+add);
                    } else {

                        String desc = "----Exams and Assignments----"+"\n"+"\n"+"----Studying----"
                                +"\n"+"\n"+"Spend "+Math.round((aa/3)*10)/10.0+" hours on "
                                + name +"'s Assignment "+(k+1)+"\n"+"\n";
                        Index.put(date,desc);
                    }
                }
            }
        }

        com.google.api.services.calendar.model.Calendar newcalendar = new com.google.api.services
                .calendar.model.Calendar();
        Log.d("ImportGoogleCalendarTask","new calendar created");
        newcalendar.setSummary("Structify Study Program");
        newcalendar.setTimeZone("America/Vancouver");
        String calendar_description = "Study reminders from Structify app";
        newcalendar.setDescription(calendar_description);
        Log.d("ImportGoogleCalendarTask","new calendar description set as: "+calendar_description);

        try {
            mService.calendars().insert(newcalendar).execute();
            Log.d("ImportGoogleCalendarTask","New calendar added to CalendarList");
        } catch (UserRecoverableAuthIOException e){
            mActivity.startActivityForResult(e.getIntent(), mActivity.REQUEST_AUTHORIZATION);
        } catch (IOException e) {
            Log.d("ImportGoogleCalendarTask","Error adding new calendar to CalendarList");
            e.printStackTrace();
            return;
        }

        String createdcalendarid = "";
        String pageToken = null;
        CalendarList calendarList = null;
        try {
            calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        List<CalendarListEntry> items = calendarList.getItems();
        Log.d("ImportGoogleCalendarTask","CalendarList instantiated and being searched for description");
        do {
            for (CalendarListEntry calendarListEntry : items) {

                Log.d("ImportGoogleCalendarTask","Check description: "+calendarListEntry.getDescription());
                if (calendarListEntry.getDescription() != null && calendarListEntry.getDescription().equals(calendar_description)){
                    createdcalendarid = calendarListEntry.getId();
                    Log.d("ImportGoogleCalendarTask","Created calendar's ID found from " +
                            "CalendarList: "+createdcalendarid);
                    break;
                }
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        CalendarListEntry structify_calendar = new CalendarListEntry();
        try {
            structify_calendar = mService.calendarList().get(createdcalendarid).execute();
            Log.d("ImportGoogleCalendarTask","Retrieved new calendar from CalendarList");
        } catch (IOException e) {
            Log.d("ImportGoogleCalendarTask","Error retrieving new calendar from CalendarList");
            e.printStackTrace();
            return;
        }

        structify_calendar.setColorId("9");

        Iterator IndexIterator = Index.entrySet().iterator();
        while (IndexIterator.hasNext()){
            Map.Entry mapElement = (Map.Entry)IndexIterator.next();
            addCalendarEvent((Date) mapElement.getKey(),(String) mapElement.getValue(),
                    createdcalendarid);
        }

        try {
            CalendarListEntry updatedCalendarListEntry = mService.calendarList()
                    .update(structify_calendar.getId(), structify_calendar)
                    .execute();
            Log.d("ImportGoogleCalendarTask","New calendar updated to CalendarList");
        } catch (IOException e) {
            Log.d("ImportGoogleCalendarTask","Error updating new calendar to CalendarList");
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
        
        try {
            mService.events().insert(calID,event).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
