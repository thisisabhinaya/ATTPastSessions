package com.example.anytimetutor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000; //Splash screen time out time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                //Creating a new session to check if user had logged in previously
/*                SessionManager session = new SessionManager(SplashScreen.this);

                //Check is user is logged in, if false then user is directed to login screen from session manager class
                session.checkLogin();

                if(session.isLoggedIn()) {

                    //Enter here if user is already logged in
                    //Get stored user session details
                    HashMap<String, String> user = session.getUserDetails();

                    // Get User type
                    String type = user.get(SessionManager.KEY_TYPE);

                    if (type.equals("admin2")) {*/

                        //User type is admin 2 (Top management)
                        Intent i = new Intent(SplashScreen.this, WelcomeActivity.class);
                        startActivity(i);
/*                    } else if (type.equals("admin1")) {
                        //User type is admin 2 (Middle management)
                        Intent i = new Intent(SplashScreen.this, Admin1Activity.class);
                        startActivity(i);
                    } else {
                        //User type is admin 2 (Regular employee)
                        Intent i = new Intent(SplashScreen.this, Main2Activity.class);
                        startActivity(i);
                    }*/
                finish();
                }

                // close this activity


        }, SPLASH_TIME_OUT);
    }
}
