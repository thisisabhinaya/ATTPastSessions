package com.example.anytimetutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.anytimetutor.SupportFiles.SharedPrefManager;
import com.example.anytimetutor.SupportFiles.User;
import com.example.anytimetutor.SupportFiles.VolleySingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

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
    String topic, format, p1,p2,p3;
    int radioButtonID,idx;
    View radioButton;

    TextView t_categorydb, t_subjectdb, t_topicdb;
    EditText e_date, e_time;
    Button search_tutor;

    RadioGroup venue, session, duration, incentive;
    RadioButton r_1,r_2,r_3,r_individual,r_group,r_any,r_incentive1,r_incentive2,r_incentive3,r_venue1,r_venue2;

    final Calendar myCalendar = Calendar.getInstance();
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAA9MuUykw:APA91bGLwfOenSE9i1AnGbZo_9-kag5z8ARw99cAev9Uevq8SUXVWvSyMNRpnfsuZyKguWo4-ZfhIU6e2L04iLU5xV_gO17gI6x5-nK4eqsBEhaqcUZCHvs_0bH-tgB6cvo-2VvVwMm6";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE, NOTIFICATION_ID;
    String TOPIC;

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

        final User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        final String id = user.getId();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference ppp = db.collection("publishers")
                .document("users")
                .collection(id)
                .document("personal_info");

        ppp.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot doc = task.getResult();
                    if(doc.get("pref1") != null) {
                        p1 = doc.get("pref1").toString();
                        p2 = doc.get("pref2").toString();
                        p3 = doc.get("pref3").toString();

                    }
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

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
                final String email = user.getEmail();

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
                req.put("stud_name", name);
                req.put("stud_email", email);
                req.put("category", category);
                req.put("subject", subject);
                req.put("topic", topic);
                req.put("date", date);
                req.put("time", time);
                req.put("duration", d);
                req.put("session_type", s);
                req.put("venue", vn);
                req.put("incentive", i);
                req.put("status", "pending");
                req.put("timestamp", ts);

                db.collection("subscribers")
                        .document(subject)
                        .collection("requests")
                        .document(ts.toString())
                        .set(req);

                TOPIC = "/topics/"+subject; //topic must match with what the receiver subscribed to
                NOTIFICATION_TITLE = "Time to Teach!!";
                NOTIFICATION_MESSAGE = "You have a new session request for "+subject;
                NOTIFICATION_ID = ts.toString();

                JSONObject notification = new JSONObject();
                JSONObject notifcationBody = new JSONObject();
                try {
                    notifcationBody.put("title", NOTIFICATION_TITLE);
                    notifcationBody.put("message", NOTIFICATION_MESSAGE);
                    notifcationBody.put("id", NOTIFICATION_ID);
                    notifcationBody.put("subject", subject);
                    notification.put("to", TOPIC);
                    notification.put("data", notifcationBody);
                } catch (JSONException e) {
                    Log.e(TAG, "onCreate: " + e.getMessage() );
                }
                Log.e("notification",notification.toString());

                if(subject.equals(p1) || subject.equals(p2) || subject.equals(p3))
                {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(subject.replaceAll(" ",""));
                    sendNotification(notification);
                }
                else
                {
                    sendNotification(notification);
                }
            }

        });

    }

    private void sendNotification(JSONObject notification) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        FirebaseMessaging.getInstance().subscribeToTopic(subject.replaceAll(" ",""));
                        Toast.makeText(SearchTutor.this, "Request sent successfully!", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        FirebaseMessaging.getInstance().subscribeToTopic(subject.replaceAll(" ",""));
                        Toast.makeText(SearchTutor.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        e_date.setText(sdf.format(myCalendar.getTime()));
    }
}
