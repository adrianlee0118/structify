package com.example.structify;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import java.io.IOException;
import java.util.ArrayList;
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

            //Pull out all data from the course
            double course_time = Courses.get(i).getCourseWt()*StudyTime;
            double fa = Courses.get(i).FinalAllocation(course_time);
            double ma = Courses.get(i).MidtermAllocation(course_time)/(Courses.get(i).getMidtermDates().size());
            double aa = Courses.get(i).AssignmentAllocation(course_time)/(Courses.get(i).getAssignmentAndQuizDates().size());
            String name = Courses.get(i).getCourseName();
            Date fd = Courses.get(i).getFinalDate();
            ArrayList<Date> md = Courses.get(i).getMidtermDates();
            ArrayList<Date> ad = Courses.get(i).getAssignmentAndQuizDates();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            //Create and insert final events
            if (Index.get(fd) != null){
                //If a reminder for the day has already been entered....
                String temp = Index.get(fd).getDescription();
                int insert_pos = 0;
                for (int j = 0; j < temp.length();j++){
                    if (temp.substring(j,j+4) == "----S"){
                        insert_pos = j-1;
                        break;
                    }
                }
                String desc = temp.substring(0,insert_pos)+ name +" Final Exam"+"\n"+"\n"
                        +temp.substring(insert_pos+1,temp.length()-1);
                Index.get(fd).setDescription(desc);
            } else {
                //If no reminder for that day exists yet....
                Event event = new Event();
                event.setDescription("----Exams and Assignments----"+"\n"+"\n"+ name +" Final Exam"
                        +"\n"+"\n"+"----Studying----"+"\n"+"\n");

                //Create the all day event date bounds
                //Use dates only, no times, to set the all-day event.
                Date startDate = fd;
                Date endDate = new Date(startDate.getTime() + 86400000);  //added milliseconds in a day

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

                AllEvents.add(event);
                Index.put(fd,event);
            }

            //Add final exam study reminders
            Date date = new Date();
            date.setTime(fd.getTime()-86400000);     //go to the day before the final
            //for the 13 days before the final....
            for (int j = 0; j < 14; j++){
                if (Index.get(date) != null){
                    //if a reminder for the day exists already....
                    String desc = Index.get(date).getDescription();
                    String add = "Study "+Math.round((fa/13)*10)/10.0+"hours for "+ name +"'s Final Exam"+"\n"+"\n";
                    Index.get(date).setDescription(desc+add);
                } else {
                    //If a reminder for the day does not exist yet...
                    Event event = new Event();
                    event.setDescription("----Exams and Assignments----"+"\n"+"\n"+"----Studying----"+"\n"+"\n"+"Study "+Math.round((fa/13)*10)/10.0+"hours for "
                            + name +"'s Final Exam"+"\n"+"\n");

                    //Create the all day event date bounds
                    //Use dates only, no times, to set the all-day event.
                    Date startDate = date;
                    Date endDate = new Date(startDate.getTime() + 86400000);  //added milliseconds in a day

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

                    AllEvents.add(event);
                    Index.put(fd,event);
                }
                date.setTime(date.getTime()-86400000);   //go to the previous day
            }

            //For all midterms in the course....
            for (int k = 0; k < md.size(); k++){
                //Create and insert midterm events
                if (Index.get(md.get(k)) != null){
                    //If a reminder for the day has already been entered....
                    String temp = Index.get(md.get(k)).getDescription();
                    int insert_pos = 0;
                    for (int l = 0; l < temp.length();l++){
                        if (temp.substring(l,l+4) == "----S"){
                            insert_pos = l-1;
                            break;
                        }
                    }
                    String desc = temp.substring(0,insert_pos)+ name +" Midterm Exam "+(k+1)+"\n"+"\n"
                            +temp.substring(insert_pos+1,temp.length()-1);
                    Index.get(md.get(k)).setDescription(desc);
                } else {
                    //If no reminder for that day exists yet....
                    Event event = new Event();
                    event.setDescription("----Exams and Assignments----"+"\n"+"\n"+ name +" Midterm Exam "+(k+1)
                            +"\n"+"\n"+"----Studying----"+"\n"+"\n");

                    //Create the all day event date bounds
                    //Use dates only, no times, to set the all-day event.
                    Date startDate = md.get(k);
                    Date endDate = new Date(startDate.getTime() + 86400000);  //added milliseconds in a day

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

                    AllEvents.add(event);
                    Index.put(md.get(k),event);
                }

                
            }

            //For all assignments in the course...
            for (int k = 0; k < ad.size(); k++){

            }
        }

    }

}
