package com.example.anytimetutor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity {

    TextView text1;
    RecyclerView tutors;
    public ProgressDialog mProgressDialog;

    private static final String FORMAT = "%02d:%02d:%02d:%02d";

    int seconds , minutes;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        text1=(TextView)findViewById(R.id.textView1);
        tutors = (RecyclerView) findViewById(R.id.recycler_view);
        tutors.setHasFixedSize(true);
        tutors.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //Initialising the timer
        Calendar currentDate = Calendar.getInstance();
        // set the year, hour, minute, second, and millisecond
        long currentDateInMillis = currentDate.getTimeInMillis();

        Intent in = getIntent();
        String myDate = in.getStringExtra("date_time");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = null;
        try {
            date = sdf.parse(myDate);
            Log.e("future date", String.valueOf(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date.getTime();
        Log.e("future date", String.valueOf(millis)+"  "+currentDateInMillis);

        new CountDownTimer((millis-currentDateInMillis), 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

                text1.setText(""+String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toDays(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(
                                TimeUnit.MILLISECONDS.toDays(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                text1.setText("done!");
            }
        }.start();

    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading ...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }


    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(getApplicationContext(), StudentHomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }
}