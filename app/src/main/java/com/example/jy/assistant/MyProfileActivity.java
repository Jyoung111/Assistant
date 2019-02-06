package com.example.jy.assistant;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyProfileActivity extends AppCompatActivity {

    LinearLayout change_pwd_layout,deregistration_layout;
    Intent intent;

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
    }
}
