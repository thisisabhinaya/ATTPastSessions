package com.example.anytimetutor.ui.home;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.MediaController;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anytimetutor.SupportFiles.RequestAdapter;
import com.example.anytimetutor.ScannerActivity;
import com.example.anytimetutor.SupportFiles.SharedPrefManager;
import com.example.anytimetutor.SupportFiles.User;
import com.example.anytimetutor.TanviProfileActivity;
import com.example.anytimetutor.ui.home.ExpandableListViewFiles.ParentLevelAdapter;
import com.example.anytimetutor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    // VideoView mVideoView;
    MediaController m;
    User user;

    DatabaseReference myRef;
    DocumentReference request_API;
    CollectionReference pending_req_fire;
    FirebaseFirestore db;

    HashMap<String,List<String>> coursemap;
    HashMap<String,List<String>> subcoursemap;
    HashMap<String, List<String>> request_det;
    Map<String,Object> request_id;

    List<String> topic, tutee_name, sess_date, sess_time, sess_status, req_id, subject, date_time;
    List<String> mItemHeaders;
    List<String> mSubItemHeaders;
    List<String> listDataHeader;

    TextView text_scanid, blank_req;
    CardView tanvi_profile;
    RecyclerView pending_req, upcoming_sessions;

    public boolean flag_empty=false;
    int pen_count=0,pen_actual=0;

    public ProgressDialog mProgressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        text_scanid = (TextView) root.findViewById(R.id.text_scanid);
        blank_req = (TextView) root.findViewById(R.id.blank_requests);
        pending_req = (RecyclerView) root.findViewById(R.id.recyclerview1);
        pending_req.setHasFixedSize(true);
        pending_req.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        upcoming_sessions= (RecyclerView) root.findViewById(R.id.recyclerview2);
        upcoming_sessions.setHasFixedSize(true);
        upcoming_sessions.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        user = SharedPrefManager.getInstance(getActivity()).getUser();
        //String stat = sharedPreferences.getString("scan_status",null);;

        pen_count=0;
        pen_actual=0;

        String stat = user.getScanStatus();
        Log.e("scan status",stat);
        if(stat.equals("0")) {
            text_scanid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(getActivity(), ScannerActivity.class);
                    startActivity(in);
                }
            });
        }
        else
        {
            text_scanid.setVisibility(View.GONE);
        }

        tanvi_profile = (CardView) root.findViewById(R.id.tanvi_profile);
        tanvi_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), TanviProfileActivity.class);
                startActivity(in);
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        db = FirebaseFirestore.getInstance();
        pending_req_fire = db.collection("subscribers");
        request_det = new HashMap<String, List<String>>();

        topic = new ArrayList<>();
        tutee_name = new ArrayList<>();
        sess_date = new ArrayList<>();
        sess_time = new ArrayList<>();
        sess_status = new ArrayList<>();
        subject = new ArrayList<>();
        req_id = new ArrayList<>();
        date_time = new ArrayList<>();

        coursemap = new HashMap<String,List<String>>();
        subcoursemap = new HashMap<String,List<String>>();
        request_id = new HashMap<String,Object>();
        //a hashmap to store each request tutor has received

        listDataHeader = new ArrayList<>();
        mItemHeaders = new ArrayList<>();
        mSubItemHeaders = new ArrayList<>();

        showProgressDialog();

        getDataFromFirebase();

        db.collection("publishers")
                .document("users")
                .collection(user.getId())
                .document("requests_made")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            // Document found in the offline cache
                            DocumentSnapshot document = task.getResult();
                            if(document.getData()!=null) {
                                request_id = document.getData();
                                Log.e("requests: ", String.valueOf(request_id));

                                pen_count = request_id.size();
                                for (Map.Entry<String, Object> request : request_id.entrySet()) {
                                    String r_id = request.getKey();
                                    String r_sub = request.getValue().toString();
                                    get_pending_requests(r_id, r_sub);
                                }
                            }
                            else
                            {
                                blank_req.setVisibility(View.VISIBLE);
                                flag_empty = true;
                                pending_req.invalidate();
                                pending_req.setVisibility(View.GONE);
                            }
                            hideProgressDialog();
                        } else {
                            //Log.d(TAG, "Cached get failed: ", task.getException());
                        }
                    }
                });

        // Init top level data
        //listDataHeader = new ArrayList<>();
        //String[] mItemHeaders = getResources().getStringArray(R.array.items_array_expandable_level_one);
        //String[] mItemHeaders = headings.toArray(new String[headings.size()]);
        //Collections.addAll(listDataHeader, mItemHeaders.toArray());

//        mVideoView = (VideoView)root.findViewById(R.id.videoView);
//
//        Uri uri = Uri.parse("android.resource://"+getActivity().getPackageName()+"/"+R.raw.att_video);
//
//        mVideoView.setVideoURI(uri);
//        MediaController m = new MediaController(getActivity());
//        m.setAnchorView(mVideoView);
//        mVideoView.setMediaController(m);

        //mVideoView.start();

        return root;

    }

    void getDataFromFirebase(){
        //Read from the database
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot,String s) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot d : dataSnapshot.getChildren()) {
                    //Getting Value from 1 to 10 in ArrayList(tasks) Topic names
                    mItemHeaders.add(d.getKey().toString());
                    for(DataSnapshot d2 : d.getChildren())
                    {
                        mSubItemHeaders.add(d2.getValue().toString());
                    }
                    subcoursemap.put(d.getKey().toString(),mSubItemHeaders);
                    mSubItemHeaders = new ArrayList<>();
                }

                //Putting key & tasks(ArrayList) in HashMap
                coursemap.put(dataSnapshot.getKey(),mItemHeaders);
                listDataHeader.add(dataSnapshot.getKey());

                mItemHeaders = new ArrayList<>();
                //String value = dataSnapshot.getValue(String.class);
                Log.e("name", "Course Map: " + coursemap);
                Log.e("name", "Sub course map: " + subcoursemap);
                Log.e("name", "List data header: " + listDataHeader);

                final ExpandableListView mExpandableListView = (ExpandableListView) getActivity().findViewById(R.id.expandableListView_Parent);
                if (mExpandableListView != null) {
                    ParentLevelAdapter parentLevelAdapter = new ParentLevelAdapter(getActivity(), listDataHeader, coursemap, subcoursemap);
                    mExpandableListView.setAdapter(parentLevelAdapter);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //Log.d(TAG, "onChildChanged: "+dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //task.remove("" + dataSnapshot.getValue());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                // Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // mTasksDatabaseReference.addChildEventListener(mChildEventListener);
    }

    public void get_pending_requests(final String request_id, final String sub)
    {

        pending_req_fire.document(sub).collection("requests").document(request_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            pen_actual++;
                            // Document found in the offline cache
                            DocumentSnapshot document = task.getResult();
                            if(document.getData()!=null) {

                                flag_empty = false;

                                if(document.get("status").toString().equals("pending"))
                                {
                                    Log.e("Individual Request: ",document.get("topic").toString());
                                    topic.add(document.get("topic").toString());
                                    tutee_name.add(document.get("stud_name").toString());
                                    sess_date.add(document.get("date").toString());
                                    sess_time.add(document.get("time").toString());
                                    sess_status.add(document.get("status").toString());
                                    date_time.add(document.get("date_time").toString());
                                    req_id.add(request_id);
                                    subject.add(sub);
                                }
                            }
                            else
                            {
                                pen_actual++;
                                flag_empty = true;
                            }

                            if(pen_actual == pen_count)
                            {

                                    flag_empty = false;
                                    //to show recent requests first
                                    Collections.reverse(topic);
                                    Collections.reverse(tutee_name);
                                    Collections.reverse(sess_date);
                                    Collections.reverse(sess_time);
                                    Collections.reverse(sess_status);
                                    Collections.reverse(req_id);
                                Collections.reverse(date_time);

                                    //there are requests for this so set adapter for each request

                                    request_det.put("topic", topic);
                                    request_det.put("tutee_name", tutee_name);
                                    request_det.put("sess_date", sess_date);
                                    request_det.put("sess_time", sess_time);
                                    request_det.put("sess_status", sess_status);
                                    request_det.put("req_id", req_id);
                                    request_det.put("subject", subject);
                                request_det.put("date_time", date_time);

                                    Log.e("Map: ", request_det.toString());

                                    //creating recyclerview adapter
                                    RequestAdapter adapter = new RequestAdapter(getActivity(), request_det, "home");
                                    //to clear adapter

                                    adapter.notifyDataSetChanged();
                                    //recyclerView.invalidate();
                                    //setting adapter to recyclerview
                                    pending_req.setAdapter(adapter);

                            }

                        } else {
                            //Log.d(TAG, "Cached get failed: ", task.getException());
                        }


                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressDialog();
                        Log.e("Couldn't connect", "Couldn't connect");
                        Toast.makeText(getActivity(), "Couldn't connect",
                                Toast.LENGTH_LONG).show();

                    }
                });
    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
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
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}