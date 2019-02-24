package com.example.jy.assistant;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyProfileActivity extends AppCompatActivity {

    LinearLayout change_pwd_layout,deregistration_layout,sensorlist_layout;
    Intent intent;
    JSONObject jsonObject, profile_result_jsonObject;
    String url = "http://teamb-iot.calit2.net/da/senduserinfo";
    TextView email_text, fname_text, lname_text, sex_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);


        //Set ActionBar + BackButton in ActionBar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        TextView title = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.mytext);
        title.setText("My Profile");
        ImageButton backbtn = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.backbtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        change_pwd_layout = (LinearLayout)findViewById(R.id.change_pwd_layout);
        deregistration_layout = (LinearLayout)findViewById(R.id.deregistraion_layout);
        sensorlist_layout = (LinearLayout)findViewById(R.id.sensorlist_layout);

        change_pwd_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MyProfileActivity.this, ChangeNowPwdActivity.class);
                startActivity(intent);
            }
        });

        deregistration_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MyProfileActivity.this, DeleteAccountActivity.class);
                startActivity(intent);
            }
        });

        sensorlist_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MyProfileActivity.this, SensorListActivity.class);
                startActivity(intent);
            }
        });

        email_text = (TextView)findViewById(R.id.email);
        fname_text = (TextView)findViewById(R.id.first_name);
        lname_text = (TextView)findViewById(R.id.last_name);
        sex_text = (TextView)findViewById(R.id.gender);


        receive_user_info();

    }

    public void receive_user_info() {


        jsonObject = new JSONObject();
        try {

            SharedPreferences prefs = getSharedPreferences("activity_login",0);
            jsonObject.put("type", "SGI-REQ");
            jsonObject.put("user_seq_num",prefs.getInt("USN",-1));

            Receive_json receive_json = new Receive_json();
            profile_result_jsonObject = receive_json.getResponseOf(MyProfileActivity.this, jsonObject, url);

            if (profile_result_jsonObject != null) {
                if (profile_result_jsonObject.getString("success_or_fail").equals("profilesuccess")) {

                    String email = profile_result_jsonObject.getString("e_mail");
                    String fname = profile_result_jsonObject.getString("fname");
                    String lname = profile_result_jsonObject.getString("lname");
                    String sex = profile_result_jsonObject.getString("sex");


                    if(email != null && fname != null && lname != null && sex != null) {
                        email_text.setText(email);
                        fname_text.setText(fname);
                        lname_text.setText(lname);
                        sex_text.setText(sex);
                    }

                    Log.w("Receive", "" + Status.USN + " " + "" + Status.EMAIL);

                } else {
                    Toast.makeText(MyProfileActivity.this, "User Info Loading Fail", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
