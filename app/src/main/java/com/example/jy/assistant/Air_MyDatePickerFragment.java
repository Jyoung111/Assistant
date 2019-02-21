package com.example.jy.assistant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


@SuppressLint("ValidFragment")
public class Air_MyDatePickerFragment extends DialogFragment {

    View dateView;

    @SuppressLint("ValidFragment")
    public Air_MyDatePickerFragment(View v) {
        this.dateView = v;
        // setStyle(DialogFragment.STYLE_NORMAL,R.style.MyDatePickerDialogTheme);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
    }


    private DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    Toast.makeText(getActivity(), "selected date is " + view.getYear() +
                            " / " + (view.getMonth()+1) +
                            " / " + view.getDayOfMonth(), Toast.LENGTH_SHORT).show();

                    int pick_day = day;
                    int pick_month = month+1;
                    int pick_year = year;


                String day_str =""+ pick_day ;
                String month_str=""+pick_month ;
                String year_str=""+pick_year;



                if(pick_day >= 0 && pick_day <= 9) {
                        day_str = "0"+ day_str;

                 }
                 if(pick_month>= 0 && pick_month<= 9){
                        month_str = "0"+month_str;
                 }

                 switch (dateView.getId()){
                    case R.id.startDate:
                        AirHistoryActivity.startDate.setText(month_str + "/" + day_str+ "/" +year_str);
                        AirHistoryActivity.start_day = day_str;
                        AirHistoryActivity.start_month = month_str;
                        AirHistoryActivity.start_year = year_str;
                        break;

                    case R.id.endDate:
                        AirHistoryActivity.endDate.setText(month_str + "/" + day_str+ "/" +year_str);
                        AirHistoryActivity.end_day = day_str;
                        AirHistoryActivity.end_month = month_str;
                        AirHistoryActivity.end_year = year_str;
                     break;
                 }



                }
            };

}
