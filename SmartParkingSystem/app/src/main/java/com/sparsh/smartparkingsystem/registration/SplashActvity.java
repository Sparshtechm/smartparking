package com.sparsh.smartparkingsystem.registration;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.sparsh.smartparkingsystem.R;
import com.sparsh.smartparkingsystem.common.Constants;
import com.sparsh.smartparkingsystem.common.Preferences;
import com.sparsh.smartparkingsystem.dashboard.DashboardActivity;

public class SplashActvity extends AppCompatActivity {

// ******* Declaring SPLASH TIME OUT *******

    private final int SPLASH_TIME_OUT = 3000;

// ******* Declaring Class Objects *******

    Preferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_actvity);

        pref = new Preferences(SplashActvity.this);
        pref.set(Constants.kloginChk,"0");
        pref.commit();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                startActivity(new Intent(SplashActvity.this, DashboardActivity.class));
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
