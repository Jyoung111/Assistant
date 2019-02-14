package com.example.jy.assistant;

import android.app.ActionBar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class AirHistoryActivity extends AppCompatActivity {

    public static int year = 0, month = 0, day = 0;
    public static TextView startDate;
    public static TextView endDate;


    public int getyear(){
        return year;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_history);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        TextView title = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.mytext);
        title.setText("AQI History");

        ImageButton backbtn = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.backbtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

         startDate = (TextView) findViewById(R.id.startDate);
         endDate = (TextView) findViewById(R.id.endDate);


        //Create Chart
        LineChart chart = (LineChart) findViewById(R.id.chart);

        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawBorders(false);

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisLeft().setDrawGridLines(false);
        //Set XAxis Bottom
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setDrawGridLines(false);


        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(4f, 0));
        entries.add(new Entry(8f, 1));
        entries.add(new Entry(6f, 2));
        entries.add(new Entry(12f, 3));
        entries.add(new Entry(18f, 4));
        entries.add(new Entry(9f, 5));

        LineDataSet dataset = new LineDataSet(entries, "PM2.5");

        LineData data = new LineData(dataset);
        chart.setData(data);

        //DatePicker Settings
        ImageView startCal = (ImageView) findViewById(R.id.startCal);
        ImageView endCal = (ImageView) findViewById(R.id.endCal);

        startCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new Air_MyDatePickerFragment(startDate);
//                newFragment.setStyle(DialogFragment.STYLE_NORMAL,R.style.DatePickerTheme);
                newFragment.show(getSupportFragmentManager(), "Start Date");
            }
        });

        endCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new Air_MyDatePickerFragment(endDate);
//                newFragment.setStyle(DialogFragment.STYLE_NORMAL,R.style.DatePickerTheme);
                newFragment.show(getSupportFragmentManager(), "End Date");

            }
        });

    }
}
