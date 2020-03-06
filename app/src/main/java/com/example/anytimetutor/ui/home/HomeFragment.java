package com.example.anytimetutor.ui.home;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.VideoView;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.anytimetutor.ScannerActivity;
import com.example.anytimetutor.StudentHomePage;
import com.example.anytimetutor.SupportFiles.SharedPrefManager;
import com.example.anytimetutor.SupportFiles.User;
import com.example.anytimetutor.TanviProfileActivity;
import com.example.anytimetutor.ui.home.ExpandableListViewFiles.ParentLevelAdapter;
import com.example.anytimetutor.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    // VideoView mVideoView;
    MediaController m;
    DatabaseReference myRef;
    HashMap<String,List<String>> coursemap;
    HashMap<String,List<String>> subcoursemap;
    List<String> mItemHeaders;
    List<String> mSubItemHeaders;
    List<String> listDataHeader;
    TextView text_scanid;
    CardView tanvi_profile;

    public ProgressDialog mProgressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        text_scanid = (TextView) root.findViewById(R.id.text_scanid);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        User user = SharedPrefManager.getInstance(getActivity()).getUser();
        //String stat = sharedPreferences.getString("scan_status",null);;
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
        coursemap = new HashMap<String,List<String>>();
        subcoursemap = new HashMap<String,List<String>>();
        listDataHeader = new ArrayList<>();
        mItemHeaders = new ArrayList<>();
        mSubItemHeaders = new ArrayList<>();

        showProgressDialog();

        getDataFromFirebase();

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

                hideProgressDialog();
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