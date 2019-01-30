package com.example.jy.assistant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button login_btn;
    TextView signup_text;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_btn = (Button) findViewById(R.id.btn_login);
        signup_text = (TextView) findViewById(R.id.signup_text);


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signup_text:
                intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.forgot_password:
                intent = new Intent(MainActivity.this, FindPasswordActivity.class);
                startActivity(intent);
                break;

        }
    }
}
