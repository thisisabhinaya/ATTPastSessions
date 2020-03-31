package com.example.anytimetutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anytimetutor.SupportFiles.RequestAdapter;
import com.example.anytimetutor.SupportFiles.SharedPrefManager;
import com.example.anytimetutor.SupportFiles.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PastSessions extends AppCompatActivity {
    RecyclerView recycler_view_past;
    public ProgressDialog mProgressDialog;
    Button tutor, tutee;
    TextView your_past_exists, your_past_doesnt;
    User user;
    String id;
    int i;
    String p1, p2, p3, sub;
    HashMap<String, List<String>> request_made_det;
    Map<String, Object> made_id;
    public boolean flag_empty = false;
    CollectionReference pub_ref, sub_ref;
    List<String> topic, tutee_name, sess_date, sess_time, sess_status, req_id, subject, accept_stat;
    List<Date> date_sort;
    ArrayList<String> keys_reqID, values_subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_sessions);
        your_past_exists = (TextView) findViewById(R.id.your_past_exists);
        your_past_doesnt = (TextView) findViewById(R.id.your_past_doesnt);
        tutee = (Button) findViewById(R.id.Tutee);
        tutor = (Button) findViewById(R.id.Tutor);

        recycler_view_past = (RecyclerView) findViewById(R.id.recycler_view_past);
        recycler_view_past.setHasFixedSize(true);
        recycler_view_past.setLayoutManager(new LinearLayoutManager(this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        id = user.getId();

        topic = new ArrayList<>();
        tutee_name = new ArrayList<>();
        sess_date = new ArrayList<>();
        sess_time = new ArrayList<>();
        sess_status = new ArrayList<>();
        subject = new ArrayList<>();
        req_id = new ArrayList<>();
        date_sort = new ArrayList<>();
        accept_stat = new ArrayList<>();
        made_id = new HashMap<String, Object>();
        keys_reqID = new ArrayList<String>();
        values_subject = new ArrayList<String>();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        pub_ref = db.collection("publishers");
        sub_ref = db.collection("subscribers");

        your_past_exists.setVisibility(View.GONE);

        //a hashmap to store each request student has made as tutee
        request_made_det = new HashMap<String, List<String>>();

        hideProgressDialog();
//tutee's completed sessions
        tutee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keys_reqID = new ArrayList<>();
                values_subject = new ArrayList<>();

                //to get requests made as a tutee
                pub_ref.document("users")
                        .collection(id)
                        .document("requests_made")
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Document found in the offline cache
                            DocumentSnapshot document = task.getResult();
                            if (document.getData() != null)
                                //hashmap of all request IDs and subject name
                                made_id = document.getData();
                            Log.e("data", "Cached document data- requests_made: " + made_id);
                            //TODO: IF NO REQUESTS MADE AT ALL
                            for (Map.Entry<String, Object> entry : made_id.entrySet()) {

                                keys_reqID.add(entry.getKey());
                                values_subject.add(entry.getValue().toString());
                            }
                            //printing keys and values to check
                            for (String str : keys_reqID) {
                                Log.e("keys (reqIDs): ", str);
                            }
                            for (String str : values_subject) {
                                Log.e("values(subjects): ", str);
                            }
                            //locating further info about each request in that subject
                            if (keys_reqID.isEmpty()) {
                                your_past_exists.setVisibility(View.GONE);
                                your_past_doesnt.setVisibility(View.VISIBLE);

                                flag_empty = true;
                                recycler_view_past.invalidate();
                                recycler_view_past.setVisibility(View.GONE);

                            } else {
                                for (i = 0; i < values_subject.size(); i++) {
                                    sub = values_subject.get(i);
                                    Log.e("val", sub);
                                    sub_ref.document(sub).collection("requests").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                            if (queryDocumentSnapshots.isEmpty()) {

                                            }
                                            else {
                                                your_past_exists.setVisibility(View.VISIBLE);
                                                your_past_doesnt.setVisibility(View.GONE);
                                                recycler_view_past.setVisibility(View.VISIBLE);

                                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                                    if (documentSnapshot.get("status").equals("completed") && documentSnapshot.get("timestamp").equals(keys_reqID.get(i)))
                                                    {

                                                        Log.e("Found", "found");
                                                        String top = (String) documentSnapshot.get("topic");
                                                        //String name = (String) documentSnapshot.get("stud_name");
                                                        String date = (String) documentSnapshot.get("date");
                                                        String time = (String) documentSnapshot.get("time");
                                                        //String datetime = (String) documentSnapshot.get("date_time");
                                                        //String id = (String) documentSnapshot.getId();
                                            //todo: try with containskey()
                                            /*
                                            if(accept_id.containsKey(id))
                                                accept_stat.add("accepted");
                                            else if(reject_id.containsKey(id))
                                                accept_stat.add("rejected");
                                            else
                                                accept_stat.add("null");*/
                                                        subject.add(sub);
                                                        topic.add(top);
                                                        //tutee_name.add(name);
                                                        sess_date.add(date);
                                                        sess_time.add(time);
                                                        //sess_status.add(status);
                                                        req_id.add(keys_reqID.get(i));
                                                    }

                                                }

                                                //to show recent requests first
                                                Collections.reverse(topic);
                                                //Collections.reverse(tutee_name);
                                                Collections.reverse(sess_date);
                                                Collections.reverse(sess_time);
                                                //Collections.reverse(sess_status);
                                                Collections.reverse(req_id);
                                                //Collections.reverse(accept_stat);

                                                request_made_det.put("topic", topic);
                                                //request_made_det.put("tutee_name", tutee_name);
                                                request_made_det.put("sess_date", sess_date);
                                                request_made_det.put("sess_time", sess_time);
                                                //request_made_det.put("sess_status", sess_status);
                                                request_made_det.put("req_id", req_id);
                                                request_made_det.put("subject", subject);
                                                //request_made_det.put("accept_stat", accept_stat);

                                                //creating recyclerview adapter
                                                RequestAdapter adapter = new RequestAdapter(getApplicationContext(), request_made_det, "past sessions");
                                                //to clear adapter
                                                adapter.notifyDataSetChanged();

                                                //setting adapter to recyclerview
                                                recycler_view_past.setAdapter(adapter);
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
                        } else {
                            //Log.d(TAG, "Cached get failed: ", task.getException());
                        }
                    }
                });

            }

        });
//tutee's completed sessions
        tutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keys_reqID = new ArrayList<String>();
                values_subject = new ArrayList<String>();

                                          /* FOR TUTOR BUTTON
       db.collection("publishers")

                .document("users")
                .collection(id)
                .document("personal_info")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    //hideProgressDialog();

                    DocumentSnapshot doc = task.getResult();
                    if (doc.get("pref1") != null) {
                        p1 = doc.get("pref1").toString();
                        p2 = doc.get("pref2").toString();
                        p3 = doc.get("pref3").toString();

                        Log.e("p1", p1);
                        Log.e("p2", p2);
                        Log.e("p3", p3);

                        get_requests_made(p1);
                        get_requests_made(p2);
                        get_requests_made(p3);

                    }
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });*/

            }
        });

         /* public void get_requests_made(final String preference)
    {
        sub_ref.document(p1).collection("requests").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    your_past_exists.setVisibility(View.GONE);
                    your_past_doesnt.setVisibility(View.VISIBLE);

                    flag_empty = true;
                    recycler_view_past.invalidate();
                    recycler_view_past.setVisibility(View.GONE);
                    //Toast.makeText(getApplicationContext(), "There are no session requests made",Toast.LENGTH_LONG).show();

                } else
                {
                    your_past_exists.setVisibility(View.VISIBLE);
                    your_past_doesnt.setVisibility(View.GONE);
                    recycler_view_past.setVisibility(View.VISIBLE);

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        if(documentSnapshot.get("status").equals("completed") && !documentSnapshot.get("stud_id").equals(id))
                        { //!documentSnapshot.get("stud_id").equals(id) means student ID for current user who made req is not that of tutor
                            String top = (String) documentSnapshot.get("topic");
                            String name = (String) documentSnapshot.get("stud_name");
                            String date = (String) documentSnapshot.get("date");
                            String time = (String) documentSnapshot.get("time");
                            String status = (String) documentSnapshot.get("status");
                            String datetime = (String) documentSnapshot.get("date_time");
                            String id = (String) documentSnapshot.getId();
                            subject.add(p1);


                            if(accept_id.containsKey(id))
                                accept_stat.add("accepted");
                            else if(reject_id.containsKey(id))
                                accept_stat.add("rejected");
                            else
                                accept_stat.add("null");

                            topic.add(top);
                            //date_sort.add(d);
                            tutee_name.add(name);
                            sess_date.add(date);
                            sess_time.add(time);
                            sess_status.add(status);
                            req_id.add(id);
                        }

                    }

                    //to show recent requests first
                    Collections.reverse(topic);
                    Collections.reverse(tutee_name);
                    Collections.reverse(sess_date);
                    Collections.reverse(sess_time);
                    Collections.reverse(sess_status);
                    Collections.reverse(req_id);
                    Collections.reverse(accept_stat);

                    request_det.put("topic", topic);
                    request_det.put("tutee_name", tutee_name);
                    request_det.put("sess_date", sess_date);
                    request_det.put("sess_time", sess_time);
                    request_det.put("sess_status", sess_status);
                    request_det.put("req_id", req_id);
                    request_det.put("subject", subject);
                    request_det.put("accept_stat", accept_stat);

                    //creating recyclerview adapter
                    RequestAdapter adapter = new RequestAdapter(getApplicationContext(), request_det, "tutor profile");
                    //to clear adapter
                    adapter.notifyDataSetChanged();

                    //setting adapter to recyclerview
                    recyclerView.setAdapter(adapter);


                }
            }
        });/*
        topic = new ArrayList<>();
        tutee_name = new ArrayList<>();
        sess_date = new ArrayList<>();
        sess_time = new ArrayList<>();
        sess_status = new ArrayList<>();
        subject = new ArrayList<>();
        req_id = new ArrayList<>();
        date_sort = new ArrayList<>();
        accept_stat = new ArrayList<>();

        //a hashmap to store each request tutor has received
        request_det = new HashMap<String, List<String>>();

        request_API.document(preference).collection("requests").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        hideProgressDialog();
                        flag_empty = false;


                        if (queryDocumentSnapshots.isEmpty()) {
                            your_req.setVisibility(View.GONE);
                            blank_req.setVisibility(View.VISIBLE);

                            flag_empty = true;
                            recyclerView.invalidate();
                            recyclerView.setVisibility(View.GONE);
                            //Toast.makeText(getApplicationContext(), "There are no session requests for this",Toast.LENGTH_LONG).show();

                        } else {
                            your_req.setVisibility(View.VISIBLE);
                            blank_req.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                hideProgressDialog();
                                if (documentSnapshot.get("status").equals("pending") && !documentSnapshot.get("stud_id").equals(id)) {

                                    String top = (String) documentSnapshot.get("topic");
                                    String name = (String) documentSnapshot.get("stud_name");
                                    String date = (String) documentSnapshot.get("date");
                                    String time = (String) documentSnapshot.get("time");
                                    String status = (String) documentSnapshot.get("status");
                                    String id = (String) documentSnapshot.getId();
                                    subject.add(preference);

                                    if(accept_id.containsKey(id))
                                        accept_stat.add("accepted");
                                    else if(reject_id.containsKey(id))
                                        accept_stat.add("rejected");
                                    else
                                        accept_stat.add("null");

                                    topic.add(top);
                                    tutee_name.add(name);
                                    sess_date.add(date);
                                    sess_time.add(time);
                                    sess_status.add(status);
                                    req_id.add(id);
                                }
                            }
                            //to show recent requests first
                            Collections.reverse(topic);
                            Collections.reverse(tutee_name);
                            Collections.reverse(sess_date);
                            Collections.reverse(sess_time);
                            Collections.reverse(sess_status);
                            Collections.reverse(req_id);
                            Collections.reverse(accept_stat);

                            //there are requests for this so set adapter for each request

                            request_det.put("topic", topic);
                            request_det.put("tutee_name", tutee_name);
                            request_det.put("sess_date", sess_date);
                            request_det.put("sess_time", sess_time);
                            request_det.put("sess_status", sess_status);
                            request_det.put("req_id", req_id);
                            request_det.put("subject", subject);
                            request_det.put("accept_stat", accept_stat);

                            //creating recyclerview adapter
                            RequestAdapter adapter = new RequestAdapter(getApplicationContext(), request_det, "tutor profile");
                            //to clear adapter

                            adapter.notifyDataSetChanged();
                            //recyclerView.invalidate();
                            //setting adapter to recyclerview
                            recyclerView.setAdapter(adapter);

                        }


                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressDialog();
                        Log.e("Couldn't connect", "Couldn't connect");
                        Toast.makeText(getApplicationContext(), "Couldn't connect",
                                Toast.LENGTH_LONG).show();

                    }
                });
    }*/
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
