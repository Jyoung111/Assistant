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

public class DeleteAccountActivity extends AppCompatActivity {

    EditText password, password_confirm;
    Button btn_delete;

    JSONObject jsonObject, signin_result_json;
    String url = "http://teamb-iot.calit2.net/da/deregistrationAndroid";
    String pwd_str, pwd_confirm_str;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        TextView title = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.mytext);
        title.setText("Deregistration");

        ImageButton backbtn = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.backbtn);

        password = (EditText) findViewById(R.id.password);
        password_confirm = (EditText) findViewById(R.id.password_confirm);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {

                pwd_str = password.getText().toString().trim();
                pwd_confirm_str = password_confirm.getText().toString().trim();
                if(pwd_str.isEmpty() || pwd_confirm_str.isEmpty()){
                    if(pwd_str.isEmpty()){
                        Toast.makeText(DeleteAccountActivity.this, "Input your password", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(DeleteAccountActivity.this, "Input your password confirm", Toast.LENGTH_SHORT).show();
                    }
                }else {

                    if(pwd_str.equals(pwd_confirm_str)){
                        jsonObject = new JSONObject();
                        try {
                            prefs = getSharedPreferences("activity_login",0);
                            jsonObject.put("type", "USD-CMD");
                            jsonObject.put("user_seq_num",  prefs.getInt("USN",-1));
                            jsonObject.put("now_pwd", password.getText());

                            Receive_json receive_json = new Receive_json();
                            signin_result_json = receive_json.getResponseOf(DeleteAccountActivity.this, jsonObject, url);

                            if(signin_result_json != null) {
                                if (signin_result_json.getString("success_or_fail").equals("deregistrationsuccess")) {
                                    prefs.edit().clear().apply();


                                    Intent intent = new Intent(DeleteAccountActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(DeleteAccountActivity.this, "Success deregistration.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(DeleteAccountActivity.this, "Incorrect login information", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(DeleteAccountActivity.this, "Password, Password Confirm not correct.",Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
