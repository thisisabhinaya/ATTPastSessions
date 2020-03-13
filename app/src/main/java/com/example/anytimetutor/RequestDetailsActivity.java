package com.example.anytimetutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anytimetutor.SupportFiles.SharedPrefManager;
import com.example.anytimetutor.SupportFiles.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class RequestDetailsActivity extends AppCompatActivity {

    String id,subject, accept_stat;
    TextView stud_name, email, date, time, duration, topic, venue, sess_type, incentive;
    Button accept, reject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        accept_stat = in.getStringExtra("accept_stat");

        if(accept_stat.equals("accepted"))
        {
            accept.setEnabled(false);
            reject.setEnabled(false);
            reject.setVisibility(View.GONE);
            accept.setText("ACCEPTED");
            accept.setBackgroundColor(Color.GREEN);
            reject.setBackgroundColor(Color.GRAY);
        } else if(accept_stat.equals("rejected"))
        {
            accept.setEnabled(false);
            reject.setEnabled(false);
            reject.setText("REJECTED");
            accept.setVisibility(View.GONE);
            reject.setBackgroundColor(Color.RED);
            accept.setBackgroundColor(Color.GRAY);
        }

        final User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        final String stud_id = user.getId();

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

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference ppp = db.collection("subscribers")
                        .document(subject)
                        .collection("requests")
                        .document(id);

                final User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                final String tutor_id = user.getId();
                final String tutor_name= user.getUsername();

                Map<String, Object> req = new HashMap<>();
                req.put(tutor_id, tutor_name);

                Map<String, Object> req2 = new HashMap<>();
                req2.put(id, subject);

                db.collection("publishers")
                        .document("users")
                        .collection(stud_id)
                        .document("requests_accepted").set(req2,SetOptions.merge());

                ppp.collection("response").document("accepted_by").set(req, SetOptions.merge());

                Toast.makeText(getApplicationContext(), "You have accepted the request..", Toast.LENGTH_LONG).show();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference ppp = db.collection("subscribers")
                        .document(subject)
                        .collection("requests")
                        .document(id);

                final User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                final String tutor_id = user.getId();
                final String tutor_name= user.getUsername();

                Map<String, Object> req = new HashMap<>();
                req.put(tutor_id, tutor_name);

                Map<String, Object> req2 = new HashMap<>();
                req2.put(id, subject);

                db.collection("publishers")
                        .document("users")
                        .collection(stud_id)
                        .document("requests_rejected").set(req2,SetOptions.merge());

                ppp.collection("response").document("rejected_by").set(req, SetOptions.merge());

                Toast.makeText(getApplicationContext(), "You have rejected the request..", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
