package com.example.anytimetutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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

public class ViewRequestActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    public ProgressDialog mProgressDialog;
    Button pref1,pref2,pref3;
    TextView blank_req, your_req;

    CollectionReference request_API;

    public boolean flag_empty=false;
    String p1,p2,p3;
    User user;
    String id;

    HashMap<String, List<String>> request_det;
    Map<String, Object> accept_id;
    Map<String,Object> reject_id;

    List<String> topic, tutee_name, sess_date, sess_time, sess_status, req_id, subject, accept_stat;
    List<Date> date_sort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);
        blank_req=(TextView)findViewById(R.id.blank_requests);
        your_req=(TextView)findViewById(R.id.your_req);

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
        accept_id = new HashMap<String,Object>();
        reject_id = new HashMap<String,Object>();

        pref1 = (Button) findViewById(R.id.pref1);
        pref2 = (Button) findViewById(R.id.pref2);
        pref3 = (Button) findViewById(R.id.pref3);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        request_API = db.collection("subscribers");

        your_req.setVisibility(View.GONE);

        //a hashmap to store each request tutor has received
        request_det = new HashMap<String, List<String>>();

        //DocumentReference ppp = ;

        showProgressDialog();

        db.collection("publishers")
                .document("users")
                .collection(id)
                .document("requests_accepted")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Document found in the offline cache
                    DocumentSnapshot document = task.getResult();
                    if(document.getData()!=null)
                        accept_id = document.getData();
                        Log.e("data", "Cached document data: " + accept_id);
                } else {
                    //Log.d(TAG, "Cached get failed: ", task.getException());
                }
            }
        });

        db.collection("publishers")
                .document("users")
                .collection(id)
                .document("requests_rejected")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Document found in the offline cache
                    DocumentSnapshot document = task.getResult();
                    if(document.getData()!=null)
                        reject_id = document.getData();
                        Log.e("data", "Cached document data: " + reject_id);
                } else {
                    //Log.d(TAG, "Cached get failed: ", task.getException());
                }
            }
        });

        db.collection("publishers")
                .document("users")
                .collection(id)
                .document("personal_info")
                .get().addOnCompleteListener(new OnCompleteListener <DocumentSnapshot> () {
            @Override
            public void onComplete(@NonNull Task < DocumentSnapshot > task) {
                if (task.isSuccessful()) {

                    hideProgressDialog();

                    DocumentSnapshot doc = task.getResult();
                    if(doc.get("pref1") != null) {
                        p1 = doc.get("pref1").toString();
                        p2 = doc.get("pref2").toString();
                        p3 = doc.get("pref3").toString();

                        Log.e("p1", p1);
                        Log.e("p2", p2);
                        Log.e("p3", p3);

                        pref1.setText(p1);
                        pref2.setText(p2);
                        pref3.setText(p3);
                            request_API.document(p1).collection("requests").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
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

                                            if(documentSnapshot.get("status").equals("pending") && !documentSnapshot.get("stud_id").equals(id)) {
                                                String top = (String) documentSnapshot.get("topic");
                                                String name = (String) documentSnapshot.get("stud_name");
                                                String date = (String) documentSnapshot.get("date");
                                                String time = (String) documentSnapshot.get("time");
                                                String status = (String) documentSnapshot.get("status");
                                                String datetime = (String) documentSnapshot.get("date_time");
                                                String id = (String) documentSnapshot.getId();
                                                subject.add(p1);

                                                /*Date d = null;
                                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                                                try {
                                                    d = df.parse(datetime);
                                                    Log.e("datetime", String.valueOf(d));
                                                } catch (ParseException ex) {
                                                    Log.v("Exception", ex.getLocalizedMessage());
                                                }*/

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
                            });
                        }
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        pref1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                get_requests(p1);

            }
        });

        pref2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgressDialog();
                get_requests(p2);
            }
        });

        pref3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgressDialog();
                get_requests(p3);
            }
        });
    }

    public void get_requests(final String preference)
    {
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

}
