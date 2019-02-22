package com.example.jy.assistant;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HeartRateHistoryActivity extends AppCompatActivity {
    public static String start_year = "", start_month = "", start_day = "";
    public static String end_year = "", end_month = "", end_day = "";
    public static int year = 0, month = 0, day = 0;
    public static TextView startDate;
    public static TextView endDate;
    LineDataSet set1;
    Button show_btn;
    JSONObject jsonObject, hr_history_result_json;
    String url = "http://teamb-iot.calit2.net/da/sendHRHistory";
    int hr = 0;
    String date = "";
    ArrayList<Integer> hr_list = new ArrayList<>();
    ArrayList<String> date_list = new ArrayList<>();
    ArrayList<Entry> entries1 = new ArrayList<>();
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    TableLayout hr_table;
    LineChart chart;
    AppController app;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate_history);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        TextView title = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.mytext);
        title.setText("HR History");

        ImageButton backbtn = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.backbtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        hr_table = (TableLayout) findViewById(R.id.hr_table);

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

        LineDataSet dataset = new LineDataSet(entries, "HR");

        LineData data = new LineData(dataset);
        chart.setData(data);

        chart.animateXY(1000, 1000);


        //DatePicker Settings
        ImageView startCal = (ImageView) findViewById(R.id.startCal);
        ImageView endCal = (ImageView) findViewById(R.id.endCal);
        startDate = (TextView) findViewById(R.id.startDate);
        endDate = (TextView) findViewById(R.id.endDate);

        startCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new HR_MyDatePickerFragment(startDate);
//                newFragment.setStyle(DialogFragment.STYLE_NORMAL,R.style.DatePickerTheme);
                newFragment.show(getSupportFragmentManager(), "Start Date");
            }
        });

        endCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new HR_MyDatePickerFragment(endDate);
//                newFragment.setStyle(DialogFragment.STYLE_NORMAL,R.style.DatePickerTheme);
                newFragment.show(getSupportFragmentManager(), "End Date");

            }
        });

        show_btn = (Button) findViewById(R.id.show_btn);

        show_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                app = AppController.getInstance();
                init();

                jsonObject = new JSONObject();
                try {
                    String start_date = start_year + "-" + start_month + "-" + start_day;
                    String end_date = end_year + "-" + end_month + "-" + end_day;


                    SharedPreferences prefs = getSharedPreferences("activity_login", 0);
                    jsonObject.put("type", "HHR-REQ");
                    jsonObject.put("user_seq_num", prefs.getInt("USN", -1));
                    jsonObject.put("start_date", start_date);
                    jsonObject.put("end_date", end_date);

                    Receive_json receive_json = new Receive_json();
                    hr_history_result_json = receive_json.getResponseOf(HeartRateHistoryActivity.this, jsonObject, url);

                    if (hr_history_result_json != null) {
                        if (hr_history_result_json.getString("success_or_fail").equals("hrselectsuccess")) {

                            JSONArray cast = hr_history_result_json.getJSONArray("HR_data");
                            if (cast.length() > 0) {
                                for (int i = 0; i < cast.length(); i++) {
                                    JSONObject actor = cast.getJSONObject(i);
                                    //date, pm, co,so2,no2,o3
                                    date = actor.get("heart_date").toString();
                                    hr = actor.getInt("heart_rate");

                                    hr_list.add(hr);
                                    date_list.add(date);

                                    //Save Chart Data
                                    entries1.add(new Entry(i, (float) hr));
                                }


                                set1 = new LineDataSet(entries1, "PM2.5");
                                set1.setColor(getResources().getColor(R.color.hr_chart));
                                set1.setCircleColor(getResources().getColor(R.color.hr_chart));
                                dataSets.add(set1);

                                //Table Settings
                                int cnt = 1;
                                for (String table_date : date_list) {
                                    if (date_list.size() != cnt) {
                                        TableRow row = new TableRow(HeartRateHistoryActivity.this);

                                        TextView date_txt = new TextView(HeartRateHistoryActivity.this);
                                        date_txt.setText(table_date);
                                        date_txt.setGravity(Gravity.CENTER);
                                        date_txt.setBackgroundResource(R.drawable.table_border);
                                        row.addView(date_txt);

                                        TextView hr_txt = new TextView(HeartRateHistoryActivity.this);
                                        hr_txt.setText("" + hr_list.get(cnt));
                                        hr_txt.setGravity(Gravity.CENTER);
                                        hr_txt.setBackgroundResource(R.drawable.table_border);
                                        row.addView(hr_txt);

                                        hr_table.addView(row, cnt);
                                        cnt++;
                                    }
                                }


                                XAxis xAxis = chart.getXAxis();
                                xAxis.setLabelRotationAngle(-40);

                                xAxis.setLabelCount(cnt,true);
                                xAxis.setValueFormatter(new IAxisValueFormatter() {
                                    @Override
                                    public String getFormattedValue(float value, AxisBase axis) {
                                        int index = (int) value;
                                        return date_list.get(index);
                                    }
                                });
                                //Show Graph
                                LineData chartData = new LineData(dataSets);
                                chart.clear();
                                chart.setData(chartData);
                                chart.setVisibleXRangeMaximum(50);
                                chart.animateXY(1000, 1000);
                                chart.invalidate();


                            } else {
                                Toast.makeText(HeartRateHistoryActivity.this, "Data not exist.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(HeartRateHistoryActivity.this, "Data not exist.", Toast.LENGTH_SHORT).show();
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                show_btn.setClickable(false);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        show_btn.setClickable(true);
                    }
                }, 2000);
            }

        });


    }


    public void init() {

        int count = hr_table.getChildCount();
        for (int i = 1; i < count; i++) {
            View child = hr_table.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }
        entries1.clear();
        dataSets.clear();
        date_list.clear();
        hr_list.clear();
    }

}
