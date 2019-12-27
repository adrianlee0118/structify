package com.example.structify;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
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

        //Map the events to dates, so that only one event is made for each day that describes all of the
        //study obligations and test events.
        ArrayList<Event> AllEvents = new ArrayList<Event>();
        Map<Date,Event> Index = new HashMap<Date,Event>();

        //For all courses...
        for (int i = 0; i < Courses.size(); i++){

            //For ease of reading, pull out all the information first
            double course_time = Courses.get(i).getCourseWt()*StudyTime;
            double fa = Courses.get(i).FinalAllocation(course_time);
            double ma = Courses.get(i).MidtermAllocation(course_time)/(Courses.get(i).getMidtermDates().size());
            double aa = Courses.get(i).AssignmentAllocation(course_time)/(Courses.get(i).getAssignmentAndQuizDates().size());
            String name = Courses.get(i).getCourseName();
            Date fd = Courses.get(i).getFinalDate();
            ArrayList<Date> md = Courses.get(i).getMidtermDates();
            ArrayList<Date> ad = Courses.get(i).getAssignmentAndQuizDates();

            //Create and insert final events
            if (Index.get(fd) != null){
                String temp = Index.get(fd).getDescription();
                int insert_pos = 0;
                for (int j = 0; j < temp.length();j++){
                    if (temp.substring(j,j+4) == "----S"){
                        insert_pos = j-1;
                        break;
                    }
                }
                String desc = temp.substring(0,insert_pos)+ Courses.get(i).getCourseName()+" Final Exam"+"\n"+"\n"
                        +temp.substring(insert_pos+1,temp.length()-1);
                Index.get(fd).setDescription(desc);
            } else {
                Event event = new Event();
                event.setDescription("----Exams and Assignments----"+"\n"+"\n"+Courses.get(i).getCourseName()+" Final Exam"
                        +"\n"+"\n"+"----Studying----"+"\n"+"\n");
                AllEvents.add(event);
                Index.put(fd,event);
            }

            //Add final exam study reminders
            Date date = new Date();
            date.setTime(fd.getTime()-86400000);
            for (int j = 0; j < 14; j++){
                if (Index.get(date) != null){
                    String desc = Index.get(date).getDescription();
                    String add = "Study "+Math.round((fa/13)*10)/10.0+"hours for "+Courses.get(i).getCourseName()+"'s Final Exam"+"\n"+"\n";
                    Index.get(fd).setDescription(desc+add);
                } else {
                    Event event = new Event();
                    event.setDescription("----Exams and Assignments----"+"\n"+"\n"+"----Studying----"+"\n"+"\n"+"Study "+Math.round((fa/13)*10)/10.0+"hours for "
                            +Courses.get(i).getCourseName()+"'s Final Exam"+"\n"+"\n");
                    AllEvents.add(event);
                    Index.put(fd,event);
                }
                date.setTime(date.getTime()-86400000);
            }


            for (int k = 0; k < md.size(); k++){

            }

            for (int k = 0; k < ad.size(); k++){

            }
        }

    }

    public Event MakeEvent(Date date, String coursename, String workdescription){


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
        //Use dates only, no times, to set the all-day event.
        Date startDate = date;
        Date endDate = new Date(startDate.getTime() + 86400000);  //added milliseconds in a day

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

        return event;
    }
}
