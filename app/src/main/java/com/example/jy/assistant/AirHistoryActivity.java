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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AirHistoryActivity extends AppCompatActivity {

    public static String start_year = "", start_month = "", start_day = "";
    public static String end_year = "", end_month = "", end_day = "";
    public static TextView startDate;
    public static TextView endDate;
    Button show_btn;
    JSONObject jsonObject,aqi_history_result_json;
    String url = "http://teamb-iot.calit2.net/da/sendAQIHistory";

    //date, pm, co,so2,no2,o3
    double  pm, co,so2,no2,o3;
    String date = "";
    LineChart chart;
    ArrayList<Entry> entries1,entries2,entries3,entries4,entries5;


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

        entries1 = new ArrayList<>();
        entries2 = new ArrayList<>();
        entries3 = new ArrayList<>();
        entries4 = new ArrayList<>();
        entries5 = new ArrayList<>();

        show_btn = (Button)findViewById(R.id.show_btn);
        show_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    jsonObject = new JSONObject();
                    try {
                        String start_date = start_year+"-"+start_month+"-"+start_day;
                        String end_date = end_year+"-"+end_month+"-"+end_day;


                        SharedPreferences prefs = getSharedPreferences("activity_login",0);
                        jsonObject.put("type", "HAQ-REQ");
                        jsonObject.put("user_seq_num", prefs.getInt("USN",-1));
                        jsonObject.put("start_date", start_date);
                        jsonObject.put("end_date", end_date);


                        Receive_json receive_json = new Receive_json();
                        aqi_history_result_json = receive_json.getResponseOf(AirHistoryActivity.this, jsonObject, url);

                        if(aqi_history_result_json != null) {
                            if (aqi_history_result_json.getString("success_or_fail").equals("aqiselectsuccess")) {



                                JSONArray cast = aqi_history_result_json.getJSONArray("aqi_data");
                                for (int i=0; i< cast.length(); i++) {
                                    JSONObject actor = cast.getJSONObject(i);
                                    //date, pm, co,so2,no2,o3
                                    date = actor.get("air_date").toString();
                                    pm = actor.getInt("AQI_PM");
                                    co = actor.getInt("AQI_CO");
                                    so2 = actor.getInt("AQI_SO2");
                                    no2 = actor.getInt("AQI_NO2");
                                    o3 = actor.getInt("AQI_O3");


                                    entries1.add(new Entry(i,(float)pm));
                                    entries2.add(new Entry(i,(float)co));
                                    entries3.add(new Entry(i,(float)so2));
                                    entries4.add(new Entry(i,(float)no2));
                                    entries5.add(new Entry(i,(float)o3));

                                    Log.w("select-data",date +" "+pm+" "+co+" "+so2+" "+no2+" "+o3+" ");

//                                    Log.w("select-data", String.valueOf(actor));

                                }


                                ArrayList<ILineDataSet> dataSets = new ArrayList<>();

                                LineDataSet set1 = new LineDataSet(entries1, "PM2.5");
//                                chartData.addDataSet(set1);
                                dataSets.add(set1);

                                LineDataSet set2 = new LineDataSet(entries2, "CO");
//                                chartData.addDataSet(set2);
                                dataSets.add(set2);

                                LineDataSet set3 = new LineDataSet(entries3, "SO2");
//                                chartData.addDataSet(set3);
                                dataSets.add(set3);

                                LineDataSet set4 = new LineDataSet(entries4, "NO2");
//                                chartData.addDataSet(set4);
                                dataSets.add(set4);

                                LineDataSet set5 = new LineDataSet(entries5, "O3");
//                                chartData.addDataSet(set5);
                                dataSets.add(set5);

                                LineData chartData = new LineData(dataSets);
                                chart.setData(chartData);
                                chart.animateXY(1000, 1000);
                                chart.invalidate();

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
        chart = (LineChart) findViewById(R.id.chart);

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
