## About
Takes milestone dates (like exams, assignment due dates and other deliverable dates) from multiple course syllabi in a university semester and combines them with a user's study time preferences and relative importances of the courses (course weights) to generate a comprehensive study/work schedule for the semester that allocates available study time to all assignments and exams based on percentage weights stated in course syllabi and on overall course weights stated by the user. The idea is that all course items will be completed in a timely fashion with an adequate and not excessive amount of time spent, and the schedule owner will only need to spend minimal amounts of time planning their workflow.

Time distribution rules
1) Final exam study time = (Weekday time in whole semester+Weekend time in whole semester)*(Course Weight)*(Final Exam Percentage in Course)
2) Final exam study time is distributed over the 13 days leading up to the exam date.
3) Midterm exam study time = (Weekday time in whole semester+Weekend time in whole semester)*(Course Weight)*(Midterm Exam Percentage in Course)/(Number of Midterms in Course)
4) Midterm exam study time is distributed over the 6 days leading up to the exam date.
5) Assignment work time = (Weekday time in whole semester+Weekend time in whole semester)*(Course Weight)*(Assignment Percentage in Course)/(Number of Assignments in Course)
6) Assignment work time is distributed over the 3 days leading up to the exam date.

## Usage:
1) MainActivity (accepts basic user inputs about study time and number of courses) 
2) SecondInputActivity (dynamically generates forms for all courses where user inputs course syllabi data)
3) ThirdInputActivity (generates a summary of study time distributions for all courses for review by user) 
4) YourCalendarActivity (generates a GUI so user can preview all exam events and study reminders over the semester in a scrollable graphical calendar format), and gives the user the option of saving the calendars as PNG to the phone's gallery or importing the schedule outright to Google Calendar

<img src="https://github.com/adrianl0118/Structify/blob/master/docs/YourCalendarActivity2.png" alt="" width="200">   <img src="https://github.com/adrianl0118/Structify/blob/master/docs/YourCalendarActivity3.png" alt="" width="200">    <img src="https://github.com/adrianl0118/Structify/blob/master/docs/YourCalendarActivity4.png" alt="" width="200">    <img src="https://github.com/adrianl0118/Structify/blob/master/docs/YourCalendarActivity5.png" alt="" width="200">

5) ImportGoogleCalendarActivity (imports all study schedule data to a new entry in the user's Google Calendar List, where it can all be deleted at ease if required)

<img src="https://github.com/adrianl0118/Structify/blob/master/docs/GoogleCalendar.PNG" alt="" width="800">

## TODO:
- Fix bugs in gallery bitmap generation function in YourCalendarActivity
- Upgrade GUI styles
