package com.example.jy.assistant;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.SettingInjectorService;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity {

    ImageButton check_btn;
    TextView check_text;
    EditText email,password,password_confirm,fname,last_name;
    Receive_json receive_json;
    RadioGroup radioGroup;
    String sex;
    JSONObject jsonObject;
    ProgressDialog waitingDialog;
    Handler handler = new Handler();


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

        Button btn_signup = (Button)findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            //Check E-mail Duplicate
            @Override
            public void onClick(View v) {

                String email_str = email.getText().toString().trim();
                String password_str = password.getText().toString().trim();
                String password_confirm_str = password_confirm.getText().toString().trim();
                String fname_str = fname.getText().toString().trim();
                String last_name_str = last_name.getText().toString().trim();

                if(email_str.isEmpty()  || password_str.isEmpty()  || password_confirm_str.isEmpty()  || fname_str.isEmpty()  ||last_name_str.isEmpty() ) {
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
                    else {
                        Toast.makeText(SignUpActivity.this, "Input your last name.",Toast.LENGTH_SHORT).show();
                    }

                }else{
                    jsonObject = new JSONObject();
                    try {
                        jsonObject.put("e_mail", email.getText());
                        receive_json = new Receive_json();
                        receive_json.getResponseOf(SignUpActivity.this, jsonObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        String success_or_fail = Receive_json.getInstance().jsonObject.getString("success_or_fail");
                        if (success_or_fail.equals("success")) {
                            //Check Password

                            if(password.equals(password_confirm)){
                               jsonObject = new JSONObject();
                                try {
                                    radioGroup = (RadioGroup) findViewById(R.id.radio_sex);
                                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                                    {
                                        @Override
                                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                                            // checkedId is the RadioButton selected
                                            switch (checkedId){
                                                case 0:
                                                    sex = "m";
                                                    break;
                                                case 1:
                                                    sex = "w";
                                                    break;
                                            }
                                        }
                                    });
                                    jsonObject.put("e_mail", email.getText());
                                    jsonObject.put("password", password.getText());
                                    jsonObject.put("fname", fname.getText());
                                    jsonObject.put("lname", last_name.getText());
                                    jsonObject.put("sex", sex);

                                    Receive_json receive_json = new Receive_json();
                                    receive_json.getResponseOf(SignUpActivity.this, jsonObject);


                                    try {
                                        String success_or_fail1 = Receive_json.getInstance().jsonObject.getString("success_or_fail");
                                        Log.w("check , Success_or_fail",success_or_fail1);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }else{
                                Toast.makeText(SignUpActivity.this, "Password, Password Confirm not correct.",Toast.LENGTH_SHORT);
                            }

                        }else{
                            Toast.makeText(SignUpActivity.this, "E-mail Duplicated",Toast.LENGTH_SHORT);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });






    }
}
