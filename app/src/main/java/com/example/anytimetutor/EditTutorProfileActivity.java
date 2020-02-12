package com.example.anytimetutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.anytimetutor.SupportFiles.SharedPrefManager;
import com.example.anytimetutor.SupportFiles.URLs;
import com.example.anytimetutor.SupportFiles.User;
import com.example.anytimetutor.SupportFiles.VolleySingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditTutorProfileActivity extends AppCompatActivity {

    Spinner pref1, pref2, pref3;
    EditText bio, link_url;
    Button cancel, submit;

    String p1,p2,p3;
    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tutor_profile);

        final User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        final String id = user.getId();
        final String name = user.getUsername();

        Intent in = getIntent();
        String pb = in.getStringExtra("bio");

        pref1 = (Spinner) findViewById(R.id.pref1);
        pref2 = (Spinner) findViewById(R.id.pref2);
        pref3 = (Spinner) findViewById(R.id.pref3);
        bio = (EditText) findViewById(R.id.bio);
        link_url = (EditText) findViewById(R.id.link_url);
        cancel = (Button) findViewById(R.id.cancel);
        submit = (Button) findViewById(R.id.submit);

        bio.setText(pb);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference ppp = db.collection("publishers")
                .document("users")
                .collection(id)
                .document("personal_info");

        showProgressDialog();

        ppp.get().addOnCompleteListener(new OnCompleteListener <DocumentSnapshot> () {
            @Override
            public void onComplete(@NonNull Task < DocumentSnapshot > task) {
                if (task.isSuccessful()) {

                    hideProgressDialog();

                    DocumentSnapshot doc = task.getResult();
                    if(doc.get("pref1") != null) {
                        p1 = doc.get("pref1").toString();
                        p2 = doc.get("pref2").toString();
                        p3 = doc.get("pref3").toString();

                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.preference_items, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        //Setting original preference 1 in spinner
                        pref1.setAdapter(adapter);
                        if (p1 != null) {
                            int spinnerPosition = adapter.getPosition(p1);
                            pref1.setSelection(spinnerPosition);
                        }

                        //Setting original preference 2 in spinner
                        pref2.setAdapter(adapter);
                        if (p2 != null) {
                            int spinnerPosition = adapter.getPosition(p2);
                            pref2.setSelection(spinnerPosition);
                        }

                        //Setting original preference 3 in spinner
                        pref3.setAdapter(adapter);
                        if (p3 != null) {
                            int spinnerPosition = adapter.getPosition(p3);
                            pref3.setSelection(spinnerPosition);
                        }
                    }
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgressDialog();

                String np1 = pref1.getSelectedItem().toString();
                String np2 = pref2.getSelectedItem().toString();
                String np3 = pref3.getSelectedItem().toString();

                final String b = bio.getText().toString();
                final String l = link_url.getText().toString();

                if((!np1.equals(p2) && !np1.equals(p3) && !np2.equals(p3) && !np1.equals("None"))|| (np2.equals("None") && np3.equals("None") && !np1.equals("None")))
                {

                    if(p1!=null) {
                        // [START update_delete_field]
                        DocumentReference change_p1 = db.collection("subscribers")
                                .document(p1);

                        DocumentReference change_p2 = db.collection("subscribers")
                                .document(p2);

                        DocumentReference change_p3 = db.collection("subscribers")
                                .document(p3);

                        // Remove the 'capital' field from the document
                        Map<String, Object> updates = new HashMap<>();
                        updates.put(id, FieldValue.delete());

                        change_p1.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            // [START_EXCLUDE]
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                            // [START_EXCLUDE]
                        });

                        change_p2.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            // [START_EXCLUDE]
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                            // [START_EXCLUDE]
                        });

                        change_p3.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            // [START_EXCLUDE]
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                            // [START_EXCLUDE]
                        });
                        // [END update_delete_field]
                    }

                    Map<String, Object> tutor = new HashMap<>();
                    tutor.put(id, name);

                    db.collection("subscribers")
                            .document(np1)
                            .set(tutor);

                    if(!np2.equals("None"))
                    {
                        db.collection("subscribers")
                                .document(np2)
                                .set(tutor);
                    }

                    if(!np3.equals("None"))
                    {
                        db.collection("subscribers")
                                .document(np3)
                                .set(tutor);
                    }

                    Map<String, Object> pref = new HashMap<>();
                    pref.put("pref1", np1);
                    pref.put("pref2", np2);
                    pref.put("pref3", np3);

                    db.collection("publishers")
                            .document("users")
                            .collection(id)
                            .document("personal_info")
                            .set(pref);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_UPDATE_TUTOR_PROFILE,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    hideProgressDialog();
//                            response = response.replace("update_scan.php","");
                                    Log.e("response",response);
                                    try {
                                        //converting response to json object
                                        JSONObject obj = new JSONObject(response);

                                        //if no error in response
                                        if (obj.getBoolean("error")) {
                                            Toast.makeText(getApplicationContext(), "An unknown error occurred...", Toast.LENGTH_SHORT).show();
                                        } else {

                                            Toast.makeText(getApplicationContext(), "Profile update successful!!", Toast.LENGTH_SHORT).show();

                                            Intent in = new Intent(EditTutorProfileActivity.this, StudentHomePage.class);
                                            ActivityCompat.finishAffinity(EditTutorProfileActivity.this);
                                            startActivity(in);

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("bio", b);
                            params.put("sap_id", id);
                            params.put("linkedin", l);
                            return params;
                        }
                    };

                    VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Preference 1 cannot be None and Preferences cannot be same..", Toast.LENGTH_LONG).show();
                }

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
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}
