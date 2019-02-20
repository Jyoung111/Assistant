package com.example.jy.assistant;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity {

    String email, pwd_str,pwd_confirm_str;
    Button btn_password_change ;
    EditText password, password_confirm;
    JSONObject jsonObject, changePassword_result_json;
    String url = "http://teamb-iot.calit2.net/da/forgotchangepwAndroid2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        TextView title = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.mytext);
        title.setText("Password Change");

        ImageButton backbtn = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.backbtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        password = (EditText) findViewById(R.id.password);
        password_confirm = (EditText) findViewById(R.id.password_confirm);


        btn_password_change  = (Button) findViewById(R.id.btn_password_change);
        btn_password_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pwd_str = password.getText().toString();
                pwd_confirm_str = password_confirm.getText().toString();

                if(pwd_str.isEmpty() || pwd_confirm_str.isEmpty()){

                    if(password.getText().toString().matches("") ){
                        Toast.makeText(ChangePasswordActivity.this, "Input your password", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ChangePasswordActivity.this, "Input your password confirm", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    if(pwd_str.equals(pwd_confirm_str)){
                        jsonObject = new JSONObject();
                        try {
                            jsonObject.put("type", "HPW-REQ");
                            jsonObject.put("e_mail", email);
                            jsonObject.put("new_pwd", password.getText());

                            Receive_json receive_json = new Receive_json();
                            changePassword_result_json = receive_json.getResponseOf(ChangePasswordActivity.this, jsonObject, url);

                            if (changePassword_result_json.getString("success_or_fail").equals("changesuccess")) {
                                Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
                                Toast.makeText(ChangePasswordActivity.this, "Success password change.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, "Fail password change.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(ChangePasswordActivity.this, "Password, Password Confirm not correct.",Toast.LENGTH_SHORT).show();
                    }


                }


            }
        });

    }
}
