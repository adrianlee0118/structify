package com.example.structify;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static android.graphics.Typeface.BOLD;

//The second page, where, given the number of courses, course data is input. The button will store
//all values from the editText fields. Since the editTexts are generated dynamically via java and thus can only
//have integer ids, use a map to store variable names as strings, mapped to the int ids.

public class SecondInputActivity extends AppCompatActivity {

    //Global variables that we will use continually in methods, passed through intent from MainActivity.
    private Date StartDate;
    private Date EndDate;
    private int NumCourses;
    private int StudyTime;

    //A Map for referencing EditText data by name, to be instantiated as fields are created
    //An array to store the new UniversityCourse objects.
    private Map<String,EditText> Data;
    private ArrayList<UniversityCourse> Semester;

    //The layout where we will put all of the dynamic UI-generated fields
    private NestedScrollView Canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondinput);

        Bundle extras = getIntent().getExtras();
        StartDate = new Date();
        StartDate.setTime(extras.getLong("StartDate"));
        EndDate = new Date();
        EndDate.setTime(extras.getLong("EndDate"));
        NumCourses = extras.getInt("NumCourses");
        StudyTime = extras.getInt("StudyTime");

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
            TextView c = new TextView(new ContextThemeWrapper(this,R.style.textview_style),null,0);
            c.setText("Course " + Integer.toString(i)+ " Name");
            linearLayout0.addView(c);
            EditText cc = new EditText(new ContextThemeWrapper(this,R.style.edittext_style),null,0);
            cc.setHint("Enter course name");
            linearLayout0.addView(cc);
            //Store the ID!
            String tname = "Course" + Integer.toString(i)+"Name";
            Data.put(tname, cc);
            linearLayoutbase.addView(linearLayout0);

            //Second Level: Final Wt, Midterm Wt, Assignments/Quiz Wts
            LinearLayout linearLayout1 = new LinearLayout(new ContextThemeWrapper(this, R.style.layout_rows_style),null,0);
            TextView fwt = new TextView(new ContextThemeWrapper(this,R.style.textview_style),null,0);
            fwt.setText("Final 1 Weight");
            linearLayout0.addView(fwt);
            EditText fwtt = new EditText(new ContextThemeWrapper(this,R.style.edittext_style),null,0);

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
