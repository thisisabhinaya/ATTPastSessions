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
import java.util.HashMap;
import java.util.List;

public class ViewRequestActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String p1,p2,p3;
    Button pref1,pref2,pref3;
    public ProgressDialog mProgressDialog;
    HashMap<String, List<String>> request_det;
    List<String> topic, tutee_name, sess_date, sess_time, sess_status, req_id, subject;
    public boolean flag_empty=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);
        final TextView blank_req, your_req, ask_to_select;
        blank_req=(TextView)findViewById(R.id.blank_requests);
        your_req=(TextView)findViewById(R.id.your_req);
        ask_to_select=(TextView)findViewById(R.id.ask_to_select_a_subj);
        topic = new ArrayList<>();
        tutee_name = new ArrayList<>();
        sess_date = new ArrayList<>();
        sess_time = new ArrayList<>();
        sess_status = new ArrayList<>();
        subject = new ArrayList<>();
        req_id = new ArrayList<>();

        pref1 = (Button) findViewById(R.id.pref1);
        pref2 = (Button) findViewById(R.id.pref2);
        pref3 = (Button) findViewById(R.id.pref3);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final CollectionReference request_API = db.collection("subscribers");
        final User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        final String id = user.getId();
        final String name = user.getUsername();
        your_req.setVisibility(View.GONE);
        //DocumentReference ppp = ;

        showProgressDialog();

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
                        //a hashmap to store each request tutor has received
                        request_det = new HashMap<String, List<String>>();

                        pref1.setText(p1);
                        pref2.setText(p2);
                        pref3.setText(p3);
                            request_API.document(p1).collection("requests").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            String top = (String) documentSnapshot.get("topic");
                                            String name = (String) documentSnapshot.get("stud_name");
                                            String date = (String) documentSnapshot.get("date");
                                            String time = (String) documentSnapshot.get("time");
                                            String status = (String) documentSnapshot.get("status");
                                            String id = (String) documentSnapshot.getId();
                                            subject.add(p1);

                                            topic.add(top);
                                            tutee_name.add(name);
                                            sess_date.add(date);
                                            sess_time.add(time);
                                            sess_status.add(status);
                                            req_id.add(id);

                                        }

                                        request_det.put("topic", topic);
                                        request_det.put("tutee_name", tutee_name);
                                        request_det.put("sess_date", sess_date);
                                        request_det.put("sess_time", sess_time);
                                        request_det.put("sess_status", sess_status);
                                        request_det.put("req_id", req_id);
                                        request_det.put("subject", subject);

                                        //creating recyclerview adapter
                                        RequestAdapter adapter = new RequestAdapter(getApplicationContext(), request_det);

                                        //setting adapter to recyclerview
                                        recyclerView.setAdapter(adapter);
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
                    ask_to_select.setVisibility(View.GONE);
                topic = new ArrayList<>();
                tutee_name = new ArrayList<>();
                sess_date = new ArrayList<>();
                sess_time = new ArrayList<>();
                sess_status = new ArrayList<>();
                subject = new ArrayList<>();
                req_id = new ArrayList<>();

                request_API.document(p1).collection("requests").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots){
                    hideProgressDialog();
                    flag_empty = false;
                    if (queryDocumentSnapshots.isEmpty())
                    {

                        your_req.setVisibility(View.GONE);
                        blank_req.setVisibility(View.VISIBLE);

                        flag_empty = true;
                        //Toast.makeText(getApplicationContext(), "There are no session requests for this",Toast.LENGTH_LONG).show();

                    }
                    else
                    {

                    your_req.setVisibility(View.VISIBLE);
                    blank_req.setVisibility(View.GONE);


                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        hideProgressDialog();
                        String top = (String) documentSnapshot.get("topic");
                        String name = (String) documentSnapshot.get("stud_name");
                        String date = (String) documentSnapshot.get("date");
                        String time = (String) documentSnapshot.get("time");
                        String status = (String) documentSnapshot.get("status");
                        String id = (String) documentSnapshot.getId();
                        subject.add(p1);

                        topic.add(top);
                        tutee_name.add(name);
                        sess_date.add(date);
                        sess_time.add(time);
                        sess_status.add(status);
                        req_id.add(id);

                        /*}
                    }
                        if(flag_empty==true);//dont set adapter because there are no requests
                        else
                        {*/
                        //there are requests for this so set adapter for each request
                        request_det.put("topic", topic);
                        request_det.put("tutee_name", tutee_name);
                        request_det.put("sess_date", sess_date);
                        request_det.put("sess_time", sess_time);
                        request_det.put("sess_status", sess_status);
                        request_det.put("req_id", req_id);
                        request_det.put("subject", subject);

                        //creating recyclerview adapter
                        RequestAdapter adapter = new RequestAdapter(getApplicationContext(), request_det);

                        //setting adapter to recyclerview
                        recyclerView.setAdapter(adapter);
                        }
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
        });

        pref2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ask_to_select.setVisibility(View.GONE);
                showProgressDialog();
                topic = new ArrayList<>();
                tutee_name = new ArrayList<>();
                sess_date = new ArrayList<>();
                sess_time = new ArrayList<>();
                sess_status = new ArrayList<>();
                subject = new ArrayList<>();
                req_id = new ArrayList<>();

                request_API.document(p2).collection("requests").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        hideProgressDialog();
                        flag_empty = false;
                        if (queryDocumentSnapshots.isEmpty()) {
                            your_req.setVisibility(View.GONE);
                            blank_req.setVisibility(View.VISIBLE);
                            flag_empty = true;
                            //Toast.makeText(getApplicationContext(), "There are no session requests for this",Toast.LENGTH_LONG).show();

                        }
                        else
                            {
                            your_req.setVisibility(View.VISIBLE);
                            blank_req.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                hideProgressDialog();
                                String top = (String) documentSnapshot.get("topic");
                                String name = (String) documentSnapshot.get("stud_name");
                                String date = (String) documentSnapshot.get("date");
                                String time = (String) documentSnapshot.get("time");
                                String status = (String) documentSnapshot.get("status");
                                String id = (String) documentSnapshot.getId();
                                subject.add(p2);

                                topic.add(top);
                                tutee_name.add(name);
                                sess_date.add(date);
                                sess_time.add(time);
                                sess_status.add(status);
                                req_id.add(id);

                            /*}
                        }
                            if (flag_empty == true) ;//dont set adapter because there are no requests
                            else {
                            //there are requests for this so set adapter for each request*/
                                request_det.put("topic", topic);
                                request_det.put("tutee_name", tutee_name);
                                request_det.put("sess_date", sess_date);
                                request_det.put("sess_time", sess_time);
                                request_det.put("sess_status", sess_status);
                                request_det.put("req_id", req_id);
                                request_det.put("subject", subject);

                                //creating recyclerview adapter
                                RequestAdapter adapter = new RequestAdapter(getApplicationContext(), request_det);

                                //setting adapter to recyclerview
                                recyclerView.setAdapter(adapter);


                            }
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
        });

        pref3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ask_to_select.setVisibility(View.GONE);
                showProgressDialog();
                topic = new ArrayList<>();
                tutee_name = new ArrayList<>();
                sess_date = new ArrayList<>();
                sess_time = new ArrayList<>();
                sess_status = new ArrayList<>();
                subject = new ArrayList<>();
                req_id = new ArrayList<>();

                request_API.document(p3).collection("requests").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        hideProgressDialog();
                        flag_empty = false;


                        if (queryDocumentSnapshots.isEmpty()) {
                            your_req.setVisibility(View.GONE);
                            blank_req.setVisibility(View.VISIBLE);

                            flag_empty = true;
                            //Toast.makeText(getApplicationContext(), "There are no session requests for this",Toast.LENGTH_LONG).show();

                        } else {
                            your_req.setVisibility(View.VISIBLE);
                            blank_req.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String top = (String) documentSnapshot.get("topic");
                                String name = (String) documentSnapshot.get("stud_name");
                                String date = (String) documentSnapshot.get("date");
                                String time = (String) documentSnapshot.get("time");
                                String status = (String) documentSnapshot.get("status");
                                String id = (String) documentSnapshot.getId();
                                subject.add(p3);

                                topic.add(top);
                                tutee_name.add(name);
                                sess_date.add(date);
                                sess_time.add(time);
                                sess_status.add(status);
                                req_id.add(id);

                            /*}
                        }
                            if (flag_empty == true);//dont set adapter because there are no requests
                            else {*/
                                //there are requests for this so set adapter for each request


                                request_det.put("topic", topic);
                                request_det.put("tutee_name", tutee_name);
                                request_det.put("sess_date", sess_date);
                                request_det.put("sess_time", sess_time);
                                request_det.put("sess_status", sess_status);
                                request_det.put("req_id", req_id);
                                request_det.put("subject", subject);

                                //creating recyclerview adapter
                                RequestAdapter adapter = new RequestAdapter(getApplicationContext(), request_det);

                                //setting adapter to recyclerview
                                recyclerView.setAdapter(adapter);
                            }
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
}
