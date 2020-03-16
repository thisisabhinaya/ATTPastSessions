package com.example.anytimetutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText e_password, e_username;
    Button b_login;
    TextView t_register;
    ProgressBar progressBar;
    String p1,p2,p3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, StudentHomePage.class));
        }

        t_register = (TextView) findViewById(R.id.t_register);
        t_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this,SignUp.class);
                finish();
                startActivity(in);
            }
        });

        e_username = (EditText) findViewById(R.id.e_username);
        e_password = (EditText) findViewById(R.id.e_password);
        b_login = (Button) findViewById(R.id.b_login);
        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(e_username.getText().toString()))
                {
                   if(!TextUtils.isEmpty(e_password.getText().toString()))
                   {
                       userLogin(e_username.getText().toString(),e_password.getText().toString());
                   }
                   else
                   {
                       e_password.setError("Enter password");
                       Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
                   }
                }
                else
                {
                    e_username.setError("Enter username");
                    Toast.makeText(getApplicationContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void userLogin(String un, String pass) {
        //first getting the values
        final String username = un;
        final String password = pass;

        progressBar.setVisibility(View.VISIBLE);

        //if everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);

                        //response = response.replace("register.php","");
                        Log.e("response",response);
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                final JSONObject userJson = obj.getJSONObject("user");

                                //creating a new user object
                                User user = new User(
                                        userJson.getString("sap_id"),
                                        userJson.getString("first_name"),
                                        userJson.getString("email"),
                                        userJson.getString("scan_status")
                                );

                                //storing the user in shared preferences
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                /*
                                Make them subscribe to their own preferences in case they have logged out before this*/
                                final User user1 = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                                final String id = user1.getId();
                                //final String name = user1.getUsername();

                                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                                final CollectionReference pub=db.collection("publishers");
                                //Get the preferences from tutee/student side
                                db.collection("publishers")
                                        .document("users")
                                        .collection(id)
                                        .document("personal_info")
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                                            {
                                             @Override
                                             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                 if (task.isSuccessful()) {
                                                     DocumentSnapshot doc = task.getResult();
                                                     if (doc.get("pref1") != null) {
                                                         p1 = doc.get("pref1").toString();
                                                         p2 = doc.get("pref2").toString();
                                                         p3 = doc.get("pref3").toString();
                                                         Log.e("p1", p1);
                                                         Log.e("p2", p2);
                                                         Log.e("p3", p3);

                                                         //Set the preferences for Tutor side of this tutee/student
                                                         // i.e make them a subscriber to these preferences
                                                         FirebaseMessaging.getInstance().subscribeToTopic("/topics/"+p1);
                                                         FirebaseMessaging.getInstance().subscribeToTopic("/topics/"+p2);
                                                         FirebaseMessaging.getInstance().subscribeToTopic("/topics/"+p3);


                                                         //starting the profile activity
                                                         Intent in = new Intent(getApplicationContext(), StudentHomePage.class);
                                                         try {
                                                             in.putExtra("first_name",userJson.getString("first_name"));
                                                             in.putExtra("email",userJson.getString("email"));
                                                             startActivity(in);
                                                             finish();
                                                         } catch (JSONException e) {
                                                             e.printStackTrace();
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
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_SHORT).show();
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
                params.put("email", username);
                params.put("password", password);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
