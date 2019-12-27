# Structify

Takes milestone dates (like exams, assignment due dates and other deliverable dates) from multiple course syllabi in a university semester and combines them with a user's study time preferences and relative importances of the courses (course weights) to generate a comprehensive study/work schedule for the semester that allocates available study time to all assignments and exams based on percentage weights stated in course syllabi and on overall course weights stated by the user.

The bounds of the plan are the start and end dates of the semester, and the app distributes studytime according to the following rules:
1) Final exam study time = (Weekday time in whole semester+Weekend time in whole semester)*(Course Weight)*(Final Exam Percentage in Course)
2) Final exam study time is distributed over the 13 days leading up to the exam date.
3) Midterm exam study time = (Weekday time in whole semester+Weekend time in whole semester)*(Course Weight)*(Midterm Exam Percentage in Course)/(Number of Midterms in Course)
4) Midterm exam study time is distributed over the 6 days leading up to the exam date.
5) Assignment work time = (Weekday time in whole semester+Weekend time in whole semester)*(Course Weight)*(Assignment Percentage in Course)/(Number of Assignments in Course)
6) Assignment work time is distributed over the 3 days leading up to the exam date.

Structure of activities in the app is as follows (in order):
1) MainActivity (accepts basic user inputs about study time and number of courses) 
2) SecondInputActivity (dynamically generates forms for all courses where user inputs course syllabi data) 
3) ThirdInputActivity (generates a summary of study time distributions for all courses for review by user) 
4) YourCalendarActivity (generates a GUI so user can preview all exam events and study reminders over the semester in a graphical calendar format) 
5) ImportGoogleCalendarActivity (imports all study schedule data to the user's Google Calendar)

Auxiliary classes used in the app:
1) UniversityCourse (a parcelable container class that accepts initial user course syllabi data from SecondInputActivity GUI and stores it in a logical manner)
2) SemesterDays (a container class that enables us to transform course-by-course data such that it is indexed by date to facilitate eventual population of the calendar GUI--also re-packages some of the text to a higher-level format)
3) ImportGoogleCalendarTask (an asynchronous task that pulls data from UniversityCourse objects and performs all the Google Calendar inputs in the background so that the UI of ImportGoogleCalendarActivity remains responsive)

The idea of this app can be applied to any period of time where multiple projects are undertaken, where a user either knows the relative importances of all said projects beforehand or has a preference as to how to distribute time between them. In the context of a busy and chaotic university semester, the plan created by this app would help a user avoid over-committing time to one particular deliverable while ensuring all assignments, projects and study undertakings receive a mathematically-deserved amount of attention with respect to time.
