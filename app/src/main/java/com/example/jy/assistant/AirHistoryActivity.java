package com.example.jy.assistant;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
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
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    LineDataSet set1,set2,set3,set4,set5;
    Button btn_pm,btn_co,btn_so2,btn_o3,btn_no2;
    ArrayList<String> date_list=new ArrayList<String>();
    TableLayout aqi_history_table;
    ArrayList<Integer> pm_list,co_list,so2_list,no2_list,o3_list;

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

        aqi_history_table = (TableLayout)findViewById(R.id.aqi_history_table);



        entries1 = new ArrayList<>();
        entries2 = new ArrayList<>();
        entries3 = new ArrayList<>();
        entries4 = new ArrayList<>();
        entries5 = new ArrayList<>();
        dataSets = new ArrayList<>();
        pm_list = new ArrayList<>();
        co_list = new ArrayList<>();
        so2_list = new ArrayList<>();
        no2_list = new ArrayList<>();
        o3_list = new ArrayList<>();

        btn_pm = (Button)findViewById(R.id.btn_pm);
        btn_co = (Button)findViewById(R.id.btn_co);
        btn_so2 = (Button)findViewById(R.id.btn_so2);
        btn_no2 = (Button)findViewById(R.id.btn_no2);
        btn_o3 = (Button)findViewById(R.id.btn_o3);


        show_btn = (Button)findViewById(R.id.show_btn);
        show_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    init();

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
                                if (cast.length() > 0) {
                                    for (int i = 0; i < cast.length(); i++) {
                                        JSONObject actor = cast.getJSONObject(i);
                                        //date, pm, co,so2,no2,o3
                                        date = actor.get("air_date").toString();
                                        pm = actor.getInt("AQI_PM");
                                        co = actor.getInt("AQI_CO");
                                        so2 = actor.getInt("AQI_SO2");
                                        no2 = actor.getInt("AQI_NO2");
                                        o3 = actor.getInt("AQI_O3");

                                        pm_list.add((int)pm);
                                        co_list.add((int)co);
                                        so2_list.add((int)so2);
                                        no2_list.add((int)no2);
                                        o3_list.add((int)o3);


                                        //Save Chart Data
                                        entries1.add(new Entry(i, (float) pm));
                                        entries2.add(new Entry(i, (float) co));
                                        entries3.add(new Entry(i, (float) so2));
                                        entries4.add(new Entry(i, (float) no2));
                                        entries5.add(new Entry(i, (float) o3));

                                        //Save Date
                                        date_list.add(date);
                                    }


                                    set1 = new LineDataSet(entries1, "PM2.5");
                                    set1.setColor(getResources().getColor(R.color.pm_chart));
                                    set1.setCircleColor(getResources().getColor(R.color.pm_chart));
                                    dataSets.add(set1);

                                    set2 = new LineDataSet(entries2, "CO");
//                                dataSets.add(set2);

                                    set3 = new LineDataSet(entries3, "SO2");
//                                dataSets.add(set3);

                                    set4 = new LineDataSet(entries4, "NO2");
//                                dataSets.add(set4);

                                    set5 = new LineDataSet(entries5, "O3");
//                                dataSets.add(set5);




                                    //Table Settings
//                                    Typeface type = Typeface.createFromAsset(getAssets(),"fonts/sarabun_medium.ttf");
                                    int cnt = 1;
                                    for(String table_date : date_list){
                                        if(date_list.size() != cnt) {
                                            TableRow row = new TableRow(AirHistoryActivity.this);

                                            TextView date_txt = new TextView(AirHistoryActivity.this);
                                            date_txt.setText(table_date);
                                            date_txt.setGravity(Gravity.CENTER);
                                            date_txt.setBackgroundResource(R.drawable.table_border);
//                                        date_txt.setTypeface(type);
                                            row.addView(date_txt);

                                            TextView pm_txt = new TextView(AirHistoryActivity.this);
                                            pm_txt.setText(String.valueOf(pm_list.get(cnt)));
                                            pm_txt.setGravity(Gravity.CENTER);
                                            pm_txt.setBackgroundResource(R.drawable.table_border);
//                                        pm_txt.setTypeface(type);
                                            row.addView(pm_txt);

                                            TextView co_txt = new TextView(AirHistoryActivity.this);
                                            co_txt.setText(String.valueOf(co_list.get(cnt)));
                                            co_txt.setGravity(Gravity.CENTER);
                                            co_txt.setBackgroundResource(R.drawable.table_border);
//                                        co_txt.setTypeface(type);
                                            row.addView(co_txt);

                                            TextView so2_txt = new TextView(AirHistoryActivity.this);
                                            so2_txt.setText(String.valueOf(so2_list.get(cnt)));
                                            so2_txt.setGravity(Gravity.CENTER);
                                            so2_txt.setBackgroundResource(R.drawable.table_border);
//                                        so2_txt.setTypeface(type);
                                            row.addView(so2_txt);

                                            TextView no2_txt = new TextView(AirHistoryActivity.this);
                                            no2_txt.setText(String.valueOf(no2_list.get(cnt)));
                                            no2_txt.setGravity(Gravity.CENTER);
                                            no2_txt.setBackgroundResource(R.drawable.table_border);
//                                        pm_txt.setTypeface(type);
                                            row.addView(no2_txt);

                                            TextView o3_txt = new TextView(AirHistoryActivity.this);
                                            o3_txt.setText(String.valueOf(o3_list.get(cnt)));
                                            o3_txt.setGravity(Gravity.CENTER);
                                            o3_txt.setBackgroundResource(R.drawable.table_border);
//                                        pm_txt.setTypeface(type);
                                            row.addView(o3_txt);

                                            aqi_history_table.addView(row, cnt);
                                            cnt++;
                                        }
                                    }


                                    //Show Graph
                                    LineData chartData = new LineData(dataSets);
                                    chart.clear();
                                    chart.setData(chartData);
                                    chart.animateXY(1000, 1000);
                                    chart.invalidate();




                                }
                                else{
                                    Toast.makeText(AirHistoryActivity.this, "Data not exist.", Toast.LENGTH_SHORT).show();
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
        entries.add(new Entry(0, 0));
        entries.add(new Entry(1, 1));
        entries.add(new Entry(2, 2));
        entries.add(new Entry(3, 3));
        entries.add(new Entry(4, 4));
        entries.add(new Entry(5, 5));

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



        btn_pm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(set1 != null) {
                    dataSets.clear();
                    set1 = new LineDataSet(entries1, "PM2.5");
                    set1.setColor(getResources().getColor(R.color.pm_chart));
                    set1.setCircleColor(getResources().getColor(R.color.pm_chart));
                    dataSets.add(set1);
                    LineData chartData = new LineData(dataSets);
                    chart.clear();
                    chart.setData(chartData);
                    chart.animateXY(1000, 1000);
                    chart.invalidate();
                }
            }
        });
        btn_co.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(set2 != null){
                    dataSets.clear();
                    set2 = new LineDataSet(entries2, "CO");
                    set2.setColor(getResources().getColor(R.color.co_chart));
                    set2.setCircleColor(getResources().getColor(R.color.co_chart));
                    dataSets.add(set2);
                    LineData chartData = new LineData(dataSets);
                    chart.clear();
                    chart.setData(chartData);
                    chart.animateXY(1000, 1000);
                    chart.invalidate();
                }
            }
        });
        btn_so2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(set3 != null) {
                    dataSets.clear();
                    set3 = new LineDataSet(entries3, "SO2");
                    set3.setColor(getResources().getColor(R.color.so2_chart));
                    set3.setCircleColor(getResources().getColor(R.color.so2_chart));
                    dataSets.add(set3);
                    LineData chartData = new LineData(dataSets);

                    chart.clear();
                    chart.setData(chartData);
                    chart.animateXY(1000, 1000);
                    chart.invalidate();
                }
            }
        });
        btn_no2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(set4!= null){
                    dataSets.clear();
                    set4 = new LineDataSet(entries4, "NO2");
                    set4.setColor(getResources().getColor(R.color.no2_chart));
                    set4.setCircleColor(getResources().getColor(R.color.no2_chart));
                    dataSets.add(set4);
                    LineData chartData = new LineData(dataSets);
                    chart.clear();
                    chart.setData(chartData);
                    chart.animateXY(1000, 1000);
                    chart.invalidate();
                }
            }
        });
        btn_o3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(set5 != null) {
                    dataSets.clear();
                    set5 = new LineDataSet(entries5, "O3");
                    set5.setColor(getResources().getColor(R.color.o3_chart));
                    set5.setCircleColor(getResources().getColor(R.color.o3_chart));
                    dataSets.add(set5);
                    LineData chartData = new LineData(dataSets);
                    chart.clear();
                    chart.setData(chartData);
                    chart.animateXY(1000, 1000);
                    chart.invalidate();
                }
            }
        });





    }

    public void init(){

        int count = aqi_history_table.getChildCount();
        for (int i = 1; i < count; i++) {
            View child = aqi_history_table.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }
        entries1.clear();
        entries2.clear();
        entries3.clear();
        entries4.clear();
        entries5.clear();
        dataSets.clear();
        date_list.clear();
        pm_list.clear();
        co_list.clear();
        so2_list.clear();
        no2_list.clear();
        o3_list.clear();
    }
}
