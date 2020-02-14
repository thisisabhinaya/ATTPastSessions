package com.example.anytimetutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RequestDetailsActivity extends AppCompatActivity {

    String id,subject;
    TextView stud_name, email, date, time, duration, topic, venue, sess_type, incentive;
    Button accept, reject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        stud_name = (TextView) findViewById(R.id.stud_name);
        email = (TextView) findViewById(R.id.email);
        date= (TextView) findViewById(R.id.date);
        time = (TextView) findViewById(R.id.time);
        duration= (TextView) findViewById(R.id.duration);
        topic = (TextView) findViewById(R.id.topic);
        venue = (TextView) findViewById(R.id.venue);
        sess_type = (TextView) findViewById(R.id.sess_type);
        incentive = (TextView) findViewById(R.id.incentive);
        accept = (Button) findViewById(R.id.accept);
        reject = (Button) findViewById(R.id.reject);

        Intent in = getIntent();
        id = in.getStringExtra("id");
        subject = in.getStringExtra("subject");

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference ses_det = db.collection("subscribers")
                .document(subject)
                .collection("requests")
                .document(id);

        ses_det.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    stud_name.setText(doc.get("stud_name").toString());
                    email.setText(doc.get("stud_email").toString());
                    date.setText(doc.get("date").toString());
                    time.setText(doc.get("time").toString());
                    topic.setText(doc.get("topic").toString());
                    duration.setText(doc.get("duration").toString());
                    sess_type.setText(doc.get("session_type").toString());
                    venue.setText(doc.get("venue").toString());
                    incentive.setText(doc.get("incentive").toString());
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });


    }
}
