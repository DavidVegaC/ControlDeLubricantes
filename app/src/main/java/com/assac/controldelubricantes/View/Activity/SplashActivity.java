package com.assac.controldelubricantes.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.assac.controldelubricantes.R;
import com.assac.controldelubricantes.Storage.PreferencesHelper;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 1500;

    private static final String TAG ="SplashActivity" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TimerTask task = new TimerTask()
        {
            @Override
            public void run() {
                Intent intent;

                boolean session= PreferencesHelper.isSignedIn(SplashActivity.this);
                if(session)
                {
                    Log.d(TAG,"En sesión");
                    intent=new Intent(SplashActivity.this, MainActivity.class);
                }else {
                    Log.d(TAG,"Fuera de sesión");
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }


                //intent=new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }
}
