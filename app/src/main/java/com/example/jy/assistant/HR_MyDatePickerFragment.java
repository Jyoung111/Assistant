package com.example.jy.assistant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;


@SuppressLint("ValidFragment")
public class HR_MyDatePickerFragment extends DialogFragment {

    View dateView;

    @SuppressLint("ValidFragment")
    public HR_MyDatePickerFragment(View v) {
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

                 HeartRateHistoryActivity.day = day;
                 HeartRateHistoryActivity.month = month+1;
                 HeartRateHistoryActivity.year = year;

                String day_str =""+ HeartRateHistoryActivity.day ;
                String month_str=""+HeartRateHistoryActivity.month ;
                String year_str=""+HeartRateHistoryActivity.year ;



                if(HeartRateHistoryActivity.day >= 0 && HeartRateHistoryActivity.day <= 9) {
                        day_str = "0"+ day_str;

                 }
                 if(HeartRateHistoryActivity.month >= 0 && HeartRateHistoryActivity.month <= 9){
                        month_str = "0"+month_str;
                 }

                 switch (dateView.getId()){
                    case R.id.startDate:
                        HeartRateHistoryActivity.startDate.setText(month_str + "/" + day_str+ "/" +year_str);
                     break;

                    case R.id.endDate:
                        HeartRateHistoryActivity.endDate.setText(month_str + "/" + day_str+ "/" +year_str);
                     break;
                 }
                }
            };

}
