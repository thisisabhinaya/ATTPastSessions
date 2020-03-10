package com.example.anytimetutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.example.anytimetutor.SupportFiles.SharedPrefManager;
import com.example.anytimetutor.SupportFiles.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

public class ViewRequestActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String p1,p2,p3;
    Button pref1,pref2,pref3;
    public ProgressDialog mProgressDialog;
    HashMap<String, List<String>> request_det;
    List<String> topic, tutee_name, sess_date, sess_time, sess_status, req_id, subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);

        pref1 = (Button) findViewById(R.id.pref1);
        pref2 = (Button) findViewById(R.id.pref2);
        pref3 = (Button) findViewById(R.id.pref3);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final CollectionReference request_API = db.collection("subscribers");//.document(p1).collection("requests");

        final User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        final String id = user.getId();
        final String name = user.getUsername();

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

                        Log.e("p1",p1);
                        Log.e("p2",p2);
                        Log.e("p3",p3);

                        request_det = new HashMap<String, List<String>>();

                        pref1.setText(p1);
                        pref2.setText(p2);
                        pref3.setText(p3);

                        request_API.document(p1).collection("requests").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                                {
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
                            }

                        });
                        request_det.put("topic", topic);
                        request_det.put("tutee_name", tutee_name);
                        request_det.put("sess_date", sess_date);
                        request_det.put("sess_time", sess_time);
                        request_det.put("sess_status", sess_status);
                        request_det.put("id", req_id);
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
                    }
                });

        pref1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("subscribers").document(p1).collection("requests")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("data", document.getId() + " => " + document.getData());
                                    }
                                } else {
                                    //Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });

        pref2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("subscribers").document(p2).collection("requests")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //Log.d(TAG, document.getId() + " => " + document.getData());
                                    }
                                } else {
                                    //Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });

        pref3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("subscribers").document(p3).collection("requests")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //Log.d(TAG, document.getId() + " => " + document.getData());
                                    }
                                } else {
                                    //Log.w(TAG, "Error getting documents.", task.getException());
                                }
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
