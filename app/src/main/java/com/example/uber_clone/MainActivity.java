package com.example.uber_clone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button driverbtn;
    Button customerbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        driverbtn=(Button)findViewById(R.id.driver_btn);
        customerbtn=(Button)findViewById(R.id.customer_btn);

        driverbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,driver_login_page.class);
                startActivity(intent);
            }
        });
        customerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,customer_login_page.class);
                startActivity(intent);
            }
        });

    }
}
