package com.example.anytimetutor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.anytimetutor.SupportFiles.SharedPrefManager;
import com.example.anytimetutor.SupportFiles.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SearchTutor extends AppCompatActivity {

    String category;
    String subject;
    String topic, format;
    int radioButtonID,idx;
    View radioButton;

    TextView t_categorydb, t_subjectdb, t_topicdb;
    EditText e_date, e_time;
    Button search_tutor;

    RadioGroup venue, session, duration, incentive;
    RadioButton r_1,r_2,r_3,r_individual,r_group,r_any,r_incentive1,r_incentive2,r_incentive3,r_venue1,r_venue2;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tutor);

        Intent in= getIntent();
        category= in.getStringExtra("Category");
        subject= in.getStringExtra("Subject");
        topic= in.getStringExtra("Topic");

        t_categorydb= (TextView)findViewById(R.id.t_categorydb);
        t_subjectdb= (TextView)findViewById(R.id.t_subjectdb);
        t_topicdb= (TextView)findViewById(R.id.t_topicdb);
        e_date = (EditText) findViewById(R.id.e_date);
        e_time = (EditText) findViewById(R.id.e_time);
        search_tutor = (Button) findViewById(R.id.search_tutor);
        venue = (RadioGroup) findViewById(R.id.venue);
        incentive = (RadioGroup) findViewById(R.id.incentive);
        duration = (RadioGroup) findViewById(R.id.duration);
        session = (RadioGroup) findViewById(R.id.session);
        r_1 = (RadioButton) findViewById(R.id.r_1);
        r_2 = (RadioButton) findViewById(R.id.r_2);
        r_3 = (RadioButton) findViewById(R.id.r_3);
        r_individual = (RadioButton) findViewById(R.id.r_individual);
        r_group = (RadioButton) findViewById(R.id.r_group);
        r_any = (RadioButton) findViewById(R.id.r_any);
        r_incentive1 = (RadioButton) findViewById(R.id.r_incentive1);
        r_incentive2 = (RadioButton) findViewById(R.id.r_incentive2);
        r_incentive3 = (RadioButton) findViewById(R.id.r_incentive3);
        r_venue1 = (RadioButton) findViewById(R.id.r_venue1);
        r_venue2 = (RadioButton) findViewById(R.id.r_venue2);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        e_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog datePickerDialog = new DatePickerDialog(SearchTutor.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        e_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SearchTutor.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                if (hourOfDay == 0) {
                                    hourOfDay += 12;
                                    format = "AM";
                                }
                                else if (hourOfDay == 12) {
                                    format = "PM";
                                }
                                else if (hourOfDay > 12) {
                                    hourOfDay -= 12;
                                    format = "PM";
                                }
                                else {
                                    format = "AM";
                                }
                                e_time.setText(hourOfDay + ":" + minute + format);
                            }
                        }, hour, minute, false);

                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        t_categorydb.setText(category);
        t_subjectdb.setText(subject);
        t_topicdb.setText(topic);

        search_tutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonID = duration.getCheckedRadioButtonId();
                radioButton = duration.findViewById(radioButtonID);
                idx = duration.indexOfChild(radioButton);

                RadioButton r_d = (RadioButton) duration.getChildAt(idx);
                String d = r_d.getText().toString();

                radioButtonID = session.getCheckedRadioButtonId();
                radioButton = session.findViewById(radioButtonID);
                idx = session.indexOfChild(radioButton);

                RadioButton r_s = (RadioButton) session.getChildAt(idx);
                String s = r_s.getText().toString();

                radioButtonID = incentive.getCheckedRadioButtonId();
                radioButton = incentive.findViewById(radioButtonID);
                idx = incentive.indexOfChild(radioButton);

                RadioButton r_i = (RadioButton) incentive.getChildAt(idx);
                String i = r_i.getText().toString();

                radioButtonID = venue.getCheckedRadioButtonId();
                radioButton = venue.findViewById(radioButtonID);
                idx = venue.indexOfChild(radioButton);

                RadioButton r_v = (RadioButton) venue.getChildAt(idx);
                String vn = r_v.getText().toString();

                String date = e_date.getText().toString();
                String time = e_time.getText().toString();

                final User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                final String id = user.getId();
                final String name = user.getUsername();

                //Date object
                Date c_d= new Date();
                //getTime() returns current time in milliseconds
                long c_t = c_d.getTime();
                //Passed the milliseconds to constructor of Timestamp class
                Timestamp ts = new Timestamp(c_t);
                System.out.println("Current Time Stamp: "+ts);

                final FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> req = new HashMap<>();
                req.put("stud_id", id);
                req.put("category", category);
                req.put("subject", subject);
                req.put("topic", topic);
                req.put("date", date);
                req.put("time", time);
                req.put("duration", d);
                req.put("session", s);
                req.put("venue", vn);
                req.put("incentive", i);
                req.put("status", "pending");
                req.put("timestamp", ts);

                db.collection("subscribers")
                        .document(subject)
                        .collection("requests")
                        .document(ts.toString())
                        .set(req);

            }
        });

    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        e_date.setText(sdf.format(myCalendar.getTime()));
    }
}
