package com.example.jy.assistant;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText email;
    Handler handler = new Handler();
    JSONObject jsonObject, forgotPassword_result_json;
    String url = "http://teamb-iot.calit2.net/da/forgotPasswordAndroid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Set ActionBar + BackButton in ActionBar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        TextView title = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.mytext);
        title.setText("Forgot Password");

        email = (EditText)findViewById(R.id.email);


        ImageButton backbtn = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.backbtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btn_password_change = (Button) findViewById(R.id.btn_password_change);
        btn_password_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().matches("")){
                    Toast.makeText(ForgotPasswordActivity.this, "Input your email", Toast.LENGTH_SHORT).show();
                }else {
                    jsonObject = new JSONObject();
                    try {
                        jsonObject.put("type", "FPW-REQ");
                        jsonObject.put("e_mail", email.getText());

                        Receive_json receive_json = new Receive_json();
                        forgotPassword_result_json = receive_json.getResponseOf(ForgotPasswordActivity.this, jsonObject, url);

                        if (forgotPassword_result_json.getString("success_or_fail").equals("success")) {


                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Email is not exist.", Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                Intent intent = new Intent(ForgotPasswordActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        WakeupTimer wakeupTimer = new WakeupTimer();


    }


    public class WakeupTimer {
        Timer timer;

        public WakeupTimer() {
            timer = new Timer();
            timer.schedule(new RemindTask(),
                    0,        //initial delay
                    500);  //subsequent rate
        }

        class RemindTask extends TimerTask {
            public void run() {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 100);
            }
        }
    }

}
