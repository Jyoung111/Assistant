package com.example.jy.assistant;

import android.app.ActionBar;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {

    ImageButton check_btn;
    TextView check_text;
    EditText email,password,password_confirm,fname,last_name;
    Receive_json receive_json;
    RadioGroup radioGroup;
    RadioButton radioButton_man,radioButton_woman;
    String sex;
    JSONObject jsonObject, signup_result_json;
    String email_str, password_str,password_confirm_str,fname_str,last_name_str;
    Handler handler = new Handler();
    String url = "http://teamb-iot.calit2.net/da/signupAndroid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
//        check_btn = (ImageButton) findViewById(R.id.check_btn);
//        check_text = (TextView) findViewById(R.id.check_text);
//        check_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                check_text.setVisibility(View.VISIBLE);
//                check_text.setText("Clear!");
//                check_text.setTextColor(Color.parseColor("#008000"));
//            }
//        });

        //
        //ActionBar Setting
        //
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        TextView title = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.mytext);
        title.setText("Sign up");

        ImageButton backbtn = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.backbtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        password_confirm = (EditText)findViewById(R.id.password_confirm);
        fname = (EditText)findViewById(R.id.fname);
        last_name = (EditText)findViewById(R.id.last_name);

        radioGroup = (RadioGroup) findViewById(R.id.radio_sex);
        radioButton_man = (RadioButton)findViewById(R.id.man_radioButton);
        radioButton_woman = (RadioButton)findViewById(R.id.woman_radioButton);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch (checkedId){
                    case R.id.man_radioButton:
                        sex = "m";
                        break;
                    case R.id.woman_radioButton:
                        sex = "w";
                        break;
                }
            }
        });

        Button btn_signup = (Button)findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            //Check E-mail Duplicate
            @Override
            public void onClick(View v) {

                email_str = email.getText().toString().trim();
                password_str = password.getText().toString().trim();
                password_confirm_str = password_confirm.getText().toString().trim();
                fname_str = fname.getText().toString().trim();
                last_name_str = last_name.getText().toString().trim();

                if(email_str.isEmpty()  || password_str.isEmpty()  || password_confirm_str.isEmpty()  || fname_str.isEmpty()  ||last_name_str.isEmpty() ||!radioButton_man.isChecked() || !radioButton_woman.isChecked() ) {
                    if(email_str.isEmpty()) {
                        Toast.makeText(SignUpActivity.this, "Input your e-mail.",Toast.LENGTH_SHORT).show();
                    }else if(password_str.isEmpty()  ){
                        Toast.makeText(SignUpActivity.this, "Input your password.",Toast.LENGTH_SHORT).show();
                    }else if( password_confirm_str.isEmpty() ){
                        Toast.makeText(SignUpActivity.this, "Input your password confirm.",Toast.LENGTH_SHORT).show();
                    }
                    else if(fname_str.isEmpty()){
                        Toast.makeText(SignUpActivity.this, "Input your first name.",Toast.LENGTH_SHORT).show();
                    }
                    else if(last_name_str.isEmpty()){
                        Toast.makeText(SignUpActivity.this, "Input your last name.",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SignUpActivity.this, "Check your sex",Toast.LENGTH_SHORT).show();
                    }


                }else{
                        //Check Password
                        if(password_str.equals(password_confirm_str)){

                            jsonObject = new JSONObject();
                            try {
                                jsonObject.put("type", "SUE-REQ");
                                jsonObject.put("e_mail", email.getText());
                                jsonObject.put("hash_pwd", password.getText());
                                jsonObject.put("fname", fname.getText());
                                jsonObject.put("lname", last_name.getText());
                                jsonObject.put("sex", sex);

                                Receive_json receive_json = new Receive_json();
                                signup_result_json = receive_json.getResponseOf(SignUpActivity.this, jsonObject,url);

                                if(signup_result_json.getString("success_or_fail").equals("success")){
                                    Toast.makeText(SignUpActivity.this, "Send Authentication E-mail.",Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(SignUpActivity.this, "E-mail Duplicated.",Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }else{
                            Toast.makeText(SignUpActivity.this, "Password, Password Confirm not correct.",Toast.LENGTH_SHORT).show();
                        }



                }

            }
        });






    }
}
