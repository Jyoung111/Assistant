package com.example.jy.assistant;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AirHistoryActivity extends AppCompatActivity {

    public static String year = "", month = "", day = "";
    public static TextView startDate;
    public static TextView endDate;
    Button show_btn;
    JSONObject jsonObject,aqi_history_result_json;
    String url = "http://teamb-iot.calit2.net/da/signinAndroid";

    //date, pm, co,so2,no2,o3
    double date, pm, co,so2,no2,o3;


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

        show_btn = (Button)findViewById(R.id.show_btn);
        show_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    jsonObject = new JSONObject();
                    try {
                        String start_date = year+"-"+month+"-"+day;
                        String end_date = year+"-"+month+"-"+day;


                        SharedPreferences prefs = getSharedPreferences("activity_login",0);
                        jsonObject.put("type", "HAQ-REQ");
                        jsonObject.put("user_seq_num", prefs.getInt("USN",-1));
                        jsonObject.put("start_date", start_date);
                        jsonObject.put("end_date", end_date);


                        Receive_json receive_json = new Receive_json();
                        aqi_history_result_json = receive_json.getResponseOf(AirHistoryActivity.this, jsonObject, url);

                        if(aqi_history_result_json != null) {
                            if (aqi_history_result_json.getString("success_or_fail").equals("aqiselectsuccess")) {


                                List<String> allNames = new ArrayList<String>();

                                ArrayList<Entry> entries = new ArrayList<>();
                                entries.add(new Entry(4f, 0));


                                JSONArray cast = aqi_history_result_json.getJSONArray("abridged_cast");
                                for (int i=0; i<cast.length(); i++) {
                                    JSONObject actor = cast.getJSONObject(i);
                                    //date, pm, co,so2,no2,o3
                                    date = actor.getDouble("air_date");
                                    pm = actor.getDouble("aqi_pm");
                                    co = actor.getDouble("aqi_co");
                                    so2 = actor.getDouble("aqi_so2");
                                    no2 = actor.getDouble("aqi_no2");
                                    o3 = actor.getDouble("aqi_o3");


                                    String name = actor.getString("name");
                                    allNames.add(name);
                                }

                            }
                            else {
                                Toast.makeText(AirHistoryActivity.this, "Data not exist.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

        });


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
        chart.animateXY(1000, 1000);

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
