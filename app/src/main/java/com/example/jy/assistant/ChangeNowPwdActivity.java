package com.example.jy.assistant;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class ChangeNowPwdActivity extends AppCompatActivity {

    EditText now_pwd, new_pwd, new_pwd_confirm;
    Button btn_password_change;
    String now_pwd_str,new_pwd_str,new_pwd_confirm_str;
    JSONObject jsonObject, changeNowPwd_result_json;
    String url = "http://teamb-iot.calit2.net/da/changepwAndroid";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_now_pwd);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        TextView title = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.mytext);
        title.setText("Password Change");

        ImageButton backbtn = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.backbtn);

        now_pwd = (EditText) findViewById(R.id.now_password);
        new_pwd = (EditText) findViewById(R.id.password);
        new_pwd_confirm = (EditText) findViewById(R.id.password_confirm);
        btn_password_change = (Button) findViewById(R.id.btn_password_change);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_password_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                now_pwd_str = now_pwd.getText().toString().trim();
                new_pwd_str = new_pwd.getText().toString().trim();
                new_pwd_confirm_str = new_pwd_confirm.getText().toString().trim();

                if(now_pwd_str.isEmpty() || new_pwd_str.isEmpty()|| new_pwd_confirm_str.isEmpty()){

                    if(now_pwd_str.isEmpty() ){
                        Toast.makeText(ChangeNowPwdActivity.this, "Input your current password", Toast.LENGTH_SHORT).show();
                    }else if(new_pwd_str.isEmpty()){
                        Toast.makeText(ChangeNowPwdActivity.this, "Input your new password", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ChangeNowPwdActivity.this, "Input your new password confirm", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    if(new_pwd_str.equals(new_pwd_confirm_str)){
                        jsonObject = new JSONObject();
                        try {
                            //Save Login data
                            SharedPreferences prefs = getSharedPreferences("activity_login",0);
                            SharedPreferences.Editor editor = prefs.edit();



                            jsonObject.put("type", "HPW-REQ");
                            jsonObject.put("user_seq_num", prefs.getInt("USN",-1));
                            jsonObject.put("now_pwd", now_pwd_str);
                            jsonObject.put("new_pwd", new_pwd_str);

                            Receive_json receive_json = new Receive_json();
                            changeNowPwd_result_json = receive_json.getResponseOf(ChangeNowPwdActivity.this, jsonObject, url);

                            if (changeNowPwd_result_json.getString("success_or_fail").equals("changesuccess")) {
                                editor.putString("password",new_pwd_str);
                                editor.apply();

                                Toast.makeText(ChangeNowPwdActivity.this, "Success password change.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ChangeNowPwdActivity.this, HomeActivity.class);
                                startActivity(intent);


                            } else {
                                Toast.makeText(ChangeNowPwdActivity.this, "Fail password change.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(ChangeNowPwdActivity.this, "Password, Password Confirm not correct.",Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });





    }
}
