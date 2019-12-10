package com.example.structify;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.graphics.Typeface.BOLD;

//The second page, where, given the number of courses, course data is input. The button will store
//all values from the editText fields. Steps:
//1) Dynamically generate all input fields for courses using a for loop based on NumCourses.
//        -EditTexts are stored in an ArrayList so that they persist
//        -EditTexts have string IDs attached using a Map
//2) Receive user input through GUI
//3) Activity takes data from input fields and puts them into objects of UniversityCourse class.

public class SecondInputActivity extends AppCompatActivity {

    //Global variables that we will use continually in methods, passed through intent from MainActivity.
    private Date StartDate;
    private Date EndDate;
    private int NumCourses;
    private int StudyTime;

    //Making dynamically created EditText fields readable.
    private Map<String,EditText> InputFieldIDs;
    private List<LinearLayout> Panels;  //not accessed, just to allow layouts to persist outside loops
    private List<HorizontalScrollView> WtPanels;  //not accessed, just allowing layouts to persist
    private List<TextView> Labels;      //not accessed, just to allow labels to persist outside loop
    private List<EditText> InputFields;

    //All Course Data stored according to index (e.g. i=0 to i=7)
    private ArrayList<String> CourseNames;
    private ArrayList<Integer> CourseWeights;
    private ArrayList<Integer> FinalWeights;
    private ArrayList<Date> FinalDates;
    private ArrayList<Integer> MTWeights;
    private ArrayList<ArrayList<Date>> MTDates;
    private ArrayList<Integer> AssignWts;
    private ArrayList<ArrayList<Date>> AssignDates;


    //The layout where we will put all of the dynamic UI-generated fields
    private NestedScrollView Canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondinput);

        //Grabbing data from MainActivity
        Bundle extras = getIntent().getExtras();
        StartDate = new Date();
        StartDate.setTime(extras.getLong("StartDate"));
        EndDate = new Date();
        EndDate.setTime(extras.getLong("EndDate"));
        NumCourses = extras.getInt("NumCourses");
        StudyTime = extras.getInt("StudyTime");

        //Setting our base
        Canvas = findViewById(R.id.canvas);

        //Standard formatting for base linearLayout, other components' formats are defined in styles.xml
        LinearLayout.LayoutParams base = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        base.setMargins(0,16,0,0);
        base.setLayoutDirection(LinearLayout.VERTICAL);
        LinearLayout linearLayoutbase = new LinearLayout(this);
        linearLayoutbase.setLayoutParams(base);

        //Create the input interface using Textview and editText modules containing fields to be filled out
        //for all of the courses
        for (int i = 1; i <= NumCourses; i++){

            //First level: Course name info
            LinearLayout linearLayout0 = new LinearLayout(new ContextThemeWrapper(this, R.style.layout_rows_style),null,0);
            Panels.add(linearLayout0);
            TextView c = new TextView(new ContextThemeWrapper(this,R.style.textview_style),null,0);
            Labels.add(c);
            c.setText("Course " + Integer.toString(i)+ " Name");
            linearLayout0.addView(c);
            EditText cc = new EditText(new ContextThemeWrapper(this,R.style.edittext_style),null,0);
            InputFields.add(cc);
            cc.setHint("Enter course "+Integer.toString(i)+" name");
            linearLayout0.addView(cc);
            //Store the ID!
            String tname = "Course" + Integer.toString(i)+"Name";
            InputFieldIDs.put(tname, cc);
            linearLayoutbase.addView(linearLayout0);

            //Second Level has 3 items in a side scroller: Final Wt, Midterm Wt, Assignments/Quiz Wts
            HorizontalScrollView linearLayout1 = new HorizontalScrollView(new ContextThemeWrapper(this, R.style.layout_rows_style),null,0);
            WtPanels.add(linearLayout1);
            TextView fwt = new TextView(new ContextThemeWrapper(this,R.style.textview_style),null,0);
            Labels.add(fwt);
            fwt.setText("Final"+Integer.toString(i)+"Weight");
            linearLayout1.addView(fwt);
            EditText fwtt = new EditText(new ContextThemeWrapper(this,R.style.edittext_style),null,0);
            InputFields.add(fwtt);
            fwtt.setHint("Enter percentage");
            linearLayout1.addView(fwtt);
            tname = "Course"+Integer.toString(i)+"FinalWt";
            InputFieldIDs.put(tname,fwtt);
            TextView mtwt = new TextView(new ContextThemeWrapper(this,R.style.textview_style),null,0);
            Labels.add(mtwt);
            mtwt.setText("Midterm"+Integer.toString(i)+"Weight");
            linearLayout1.addView(mtwt);
            EditText mtwtt = new EditText(new ContextThemeWrapper(this,R.style.edittext_style),null,0);
            InputFields.add(mtwtt);
            mtwtt.setHint("Enter percentage");
            linearLayout1.addView(mtwtt);
            tname = "Course"+Integer.toString(i)+"MidtermWt";
            InputFieldIDs.put(tname,mtwtt);
            TextView aswt = new TextView(new ContextThemeWrapper(this,R.style.textview_style),null,0);
            Labels.add(aswt);
            aswt.setText("AssignmentandQuiz"+Integer.toString(i)+"Weight");
            linearLayout1.addView(aswt);
            EditText aswtt = new EditText(new ContextThemeWrapper(this,R.style.edittext_style),null,0);
            InputFields.add(aswtt);
            aswtt.setHint("Enter percentage");
            linearLayout1.addView(aswtt);
            tname = "Course"+Integer.toString(i)+"AssignmentWt";
            InputFieldIDs.put(tname,aswtt);
            linearLayoutbase.addView(linearLayout1);

            //3rd level: Final Exam Date
            LinearLayout linearLayout2 = new LinearLayout(new ContextThemeWrapper(this, R.style.layout_rows_style),null,0);
            Panels.add(linearLayout2);
            TextView fd = new TextView(new ContextThemeWrapper(this,R.style.textview_style),null,0);
            Labels.add(fd);
            fd.setText("Final " + Integer.toString(i)+ " Date");
            linearLayout2.addView(fd);
            EditText fdd = new EditText(new ContextThemeWrapper(this,R.style.edittext_style),null,0);
            InputFields.add(fdd);
            fdd.setHint("YYYY-MM-DD");
            linearLayout2.addView(fdd);
            tname = "Final" + Integer.toString(i)+"Date";
            InputFieldIDs.put(tname, fdd);
            linearLayoutbase.addView(linearLayout2);


            linearLayoutbase.addView(linearLayout2);

        }

        Canvas.addView(linearLayoutbase);
    }

    //Create a CardView that contains a linear layout which contains a Textview which contains some
    //data and return it. Basically the building blocks of the dynamic nested viewport.
    //We will call this anytime we have some fields to add.
    private View cardItemView(String str){
        CardView cardView = new CardView(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(3,4,3,4);
        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(8);
        cardView.setCardElevation(8);


        cardView.setUseCompatPadding(true);

        // text view
        TextView textView = new TextView(this);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        textView.setLayoutParams(params1);
        textView.setText(str);
        textView.setPadding(24,24,24,24);

        cardView.addView(textView);

        return cardView;

    }
}
