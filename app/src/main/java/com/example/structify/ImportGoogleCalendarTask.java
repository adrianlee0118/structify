package com.example.structify;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
        Log.d("ImportGoogleCalendarTask","Data added");
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
        }
        return null;
    }

    public void addCalendarEvents() {

        // Create a new calendar
        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services
                .calendar.model.Calendar();
        calendar.setSummary("Structify Study Program");
        calendar.setTimeZone("America/Vancouver");
        calendar.setId("Structify");  //Use this ID in addCalendarEvent()

        // Insert the new calendar -- we will access it again in addCalendarEvent() via its ID "Structify"
        try {
            mService.calendars().insert(calendar).execute();
            Log.d("ImportGoogleCalendarTask","New calendar Structify inserted");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ImportGoogleCalendarTask","Error inserting new calendar");
        }

        //Map the event descriptions to dates, so that only one event is made for each day
        //that describes all of the study obligations and test events.
        ArrayList<String> AllEvents = new ArrayList<String>();
        Map<Date,String> Index = new HashMap<Date,String>();

        //For all courses...
        for (int i = 0; i < Courses.size(); i++){

            //Pull out all data from the course
            double course_time = Courses.get(i).getCourseWt()*StudyTime;
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
            Log.d("ImportGoogleCalendarTask","Data from Course "+(i+1)+" Pulled");

            //Create and insert final events
            if (Index.get(fd) != null){
                //If a reminder for the day has already been entered....
                String temp = Index.get(fd);
                int insert_pos = 0;
                for (int j = 0; j < temp.length();j++){
                    if (temp.substring(j,j+4) == "----S"){
                        insert_pos = j-1;
                        break;
                    }
                }
                String desc = temp.substring(0,insert_pos)+ name +" Final Exam"+"\n"+"\n"
                        +temp.substring(insert_pos+1,temp.length()-1);
                Index.replace(fd, desc);
                Log.d("ImportGoogleCalendarTask","Final Exam Date for Course "+(i+1)+
                        " Added to an existing day in the ArrayList");
            } else {
                //If no reminder for that day exists yet....
                String desc = "----Exams and Assignments----"+"\n"+"\n"+ name +" Final Exam"
                        +"\n"+"\n"+"----Studying----"+"\n"+"\n";
                AllEvents.add(desc);
                Index.put(fd,desc);
                Log.d("ImportGoogleCalendarTask","Final Exam Date for Course "+(i+1)+
                        " Added to the ArrayList, along with a new Day of events");
            }

            //Add final exam study reminders
            Date date = new Date();
            date.setTime(fd.getTime()-86400000);     //go to the day before the final
            //for the 13 days before the final....
            for (int j = 0; j < 14; j++){
                if (Index.get(date) != null){
                    //if a reminder for the day exists already....
                    String desc = Index.get(date);
                    String add = "Study "+Math.round((fa/13)*10)/10.0+" hours for "+ name +
                            "'s Final Exam"+"\n"+"\n";
                    Index.replace(date,desc+add);
                    Log.d("ImportGoogleCalendarTask","Final Exam Reminders for Course "+
                            (i+1)+" Added to an existing day in the ArrayList");
                } else {
                    //If a reminder for the day does not exist yet...
                    String desc = "----Exams and Assignments----"+"\n"+"\n"+"----Studying----"+"\n"
                            +"\n"+"Study "+Math.round((fa/13)*10)/10.0+"hours for "+name+
                            "'s Final Exam"+"\n"+"\n";
                    AllEvents.add(desc);
                    Index.put(date,desc);
                    Log.d("ImportGoogleCalendarTask","Final Exam Reminders for Course "+
                            (i+1)+" Added to ArrayList, along with a new day");
                }
                date.setTime(date.getTime()-86400000);   //go to the previous day
            }

            //For all midterms in the course....
            for (int k = 0; k < md.size(); k++){
                //Create and insert midterm events
                if (Index.get(md.get(k)) != null){
                    //If a reminder for the day has already been entered....
                    String temp = Index.get(md.get(k));
                    int insert_pos = 0;
                    for (int l = 0; l < temp.length();l++){
                        if (temp.substring(l,l+4) == "----S"){
                            insert_pos = l-1;
                            break;
                        }
                    }
                    String desc = temp.substring(0,insert_pos)+ name +" Midterm Exam "+(k+1)+"\n"+
                            "\n"+temp.substring(insert_pos+1,temp.length()-1);
                    Index.replace(md.get(k),desc);
                    Log.d("ImportGoogleCalendarTask","Midterm Exam "+(k+1)+
                            " Date for Course "+(i+1)+" Added to an existing day in the ArrayList");
                } else {
                    //If no reminder for that day exists yet....
                    String desc = "----Exams and Assignments----"+"\n"+"\n"+ name +" Midterm Exam "
                            +(k+1)+"\n"+"\n"+"----Studying----"+"\n"+"\n";
                    AllEvents.add(desc);
                    Index.put(md.get(k),desc);
                    Log.d("ImportGoogleCalendarTask","Midterm Exam "+(k+1)+
                            " Date for Course "+(i+1)+
                            " Added to the ArrayList, along with a new day");
                }

                //Add midterm exam study reminders
                date.setTime(md.get(k).getTime()-86400000);  //go to the day before the current midterm
                //for the 6 days before the midterm....
                for (int l = 0; l < 6; l++){
                    if (Index.get(date) != null){
                        //if a reminder for the day exists already....
                        String desc = Index.get(date);
                        String add = "Study "+Math.round((ma/6)*10)/10.0+" hours for "+ name +
                                "'s Midterm Exam "+(k+1)+"\n"+"\n";
                        Index.replace(date,desc+add);
                        Log.d("ImportGoogleCalendarTask","Midterm Exam "+(k+1)+
                                " Reminders for Course "+(i+1)+
                                " Added to an existing day in the ArrayList");
                    } else {
                        //If a reminder for the day does not exist yet...
                        String desc = "----Exams and Assignments----"+"\n"+"\n"+"----Studying----"+
                                "\n"+"\n"+"Study "+Math.round((ma/6)*10)/10.0+" hours for "+name+
                                "'s Midterm Exam "+(k+1)+"\n"+"\n";
                        AllEvents.add(desc);
                        Index.put(date,desc);
                        Log.d("ImportGoogleCalendarTask","Midterm Exam "+(k+1)+" Reminders for Course "+(i+1)+" Added to ArrayList, along with a new date");
                    }
                    date.setTime(date.getTime()-86400000);   //go to the previous day
                }
            }

            //For all assignments in the course...
            for (int k = 0; k < ad.size(); k++){
                //Create and insert assignment events
                if (Index.get(ad.get(k)) != null){
                    //If a reminder for the day has already been entered....
                    String temp = Index.get(ad.get(k));
                    int insert_pos = 0;
                    for (int l = 0; l < temp.length();l++){
                        if (temp.substring(l,l+4) == "----S"){
                            insert_pos = l-1;
                            break;
                        }
                    }
                    String desc = temp.substring(0,insert_pos)+ name +" Assignment "+(k+1)+
                            " Due"+"\n"+"\n"
                            +temp.substring(insert_pos+1,temp.length()-1);
                    Index.replace(ad.get(k),desc);
                    Log.d("ImportGoogleCalendarTask","Assignment "+(k+1)+
                            " Date for Course "+(i+1)+" Added to an existing day in the ArrayList");
                } else {
                    //If no reminder for that day exists yet....
                    String desc = "----Exams and Assignments----"+"\n"+"\n"+ name +" Assignment "+
                            (k+1)+" Due"+"\n"+"\n"+"----Studying----"+"\n"+"\n";
                    AllEvents.add(desc);
                    Index.put(ad.get(k),desc);
                    Log.d("ImportGoogleCalendarTask","Assignment "+(k+1)+
                            " Date for Course "+(i+1)+
                            " Added to the ArrayList along with a new day");
                }

                //Add assignment study reminders
                date.setTime(ad.get(k).getTime()-86400000);//go to the day before the current assignment
                //for the 3 days before the assignment....
                for (int l = 0; l < 3; l++){
                    if (Index.get(date) != null){
                        //if a reminder for the day exists already....
                        String desc = Index.get(date);
                        String add = "Spend "+Math.round((aa/3)*10)/10.0+" hours on "+ name +
                                "'s Assignment "+(k+1)+"\n"+"\n";
                        Index.replace(date,desc+add);
                        Log.d("ImportGoogleCalendarTask","Assignment "+(k+1)+
                                " Reminders for Course "+(i+1)+
                                " Added to an existing day in ArrayList");
                    } else {
                        //If a reminder for the day does not exist yet...
                        String desc = "----Exams and Assignments----"+"\n"+"\n"+"----Studying----"+"\n"+"\n"+"Spend "+Math.round((aa/3)*10)/10.0+" hours on "
                                + name +"'s Assignment "+(k+1)+"\n"+"\n";
                        AllEvents.add(desc);
                        Index.put(date,desc);
                        Log.d("ImportGoogleCalendarTask","Assignment "+(k+1)+" Reminders for Course "+(i+1)+" Added to ArrayList, along with a new day");
                    }
                    date.setTime(date.getTime()-86400000);   //go to the previous day
                }
            }
        }

        Iterator IndexIterator = Index.entrySet().iterator();
        //Add all the events to the calendar!
        while (IndexIterator.hasNext()){
            Map.Entry mapElement = (Map.Entry)IndexIterator.next();
            addCalendarEvent((Date) mapElement.getKey(),(String) mapElement.getValue());
        }
    }

    public void addCalendarEvent(Date date, String eventdesc){

        Event event = new Event()
                .setSummary("Structify Exam Events and Study Reminders")
                .setLocation("Vancouver, BC, Canada")
                .setDescription(eventdesc);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String DateStr = dateFormat.format(date);
        Log.d("ImportGoogleCalendarTask","assignment reminder startdate is "+dateFormat.format(date));

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

        //Add the event to the new calendar (ID: "Structify") created in the parent method
        String calendarId = "Structify";
        try {
            mService.events().insert(calendarId, event).execute();
            Log.d("ImportGoogleCalendarTask","New event inserted on date "+DateStr);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ImportGoogleCalendarTask","Error inserting event on date "+DateStr);
        }
    }
}
