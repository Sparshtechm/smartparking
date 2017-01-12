package com.sparsh.smartparkingsystem.registration;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sparsh.smartparkingsystem.R;
import com.sparsh.smartparkingsystem.dashboard.DashboardActivity;

public class SplashActvity extends AppCompatActivity {

    private final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_splash_actvity);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

               startActivity(new Intent(SplashActvity.this, LoginActivity.class));
               finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
