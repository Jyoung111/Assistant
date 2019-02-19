package com.example.jy.assistant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    Button login_btn;
    TextView signup_text;
    Intent intent;
    EditText email, password;
    JSONObject jsonObject, signin_result_json;
    String url = "http://teamb-iot.calit2.net/da/signinAndroid";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_btn = (Button) findViewById(R.id.btn_login);
        signup_text = (TextView) findViewById(R.id.signup_text);
        email = (EditText) findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(email.getText().toString().matches("") || password.getText().toString().matches("")){
                    if(email.getText().toString().matches("")){
                        Toast.makeText(LoginActivity.this, "Input your email", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(LoginActivity.this, "Input your password", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    jsonObject = new JSONObject();
                    try {
                        jsonObject.put("type", "SGI-REQ");
                        jsonObject.put("e_mail", email.getText());
                        jsonObject.put("hash_pwd", password.getText());

                        Receive_json receive_json = new Receive_json();
                        signin_result_json = receive_json.getResponseOf(LoginActivity.this, jsonObject, url);

                        if(signin_result_json != null) {
                            if (signin_result_json.getString("success_or_fail").equals("verifysuccess")) {

                                Status.USN = signin_result_json.getInt("user_seq_num");
                                Status.email = email.getText().toString();

                                intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);

                                Log.w("Receive", "" + Status.USN + " " + "" + Status.email);

                            } else if (signin_result_json.getString("success_or_fail").equals("activefail")){
                                Toast.makeText(LoginActivity.this, "Activation need to verify", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Incorrect login information", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });







    }

    public void onClick(View v) {
        switch (v.getId()){

            case R.id.signup_text:
                intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;

            case R.id.forgot_password:
                intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                break;

        }
    }




}
