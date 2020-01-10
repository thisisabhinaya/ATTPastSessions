package com.example.anytimetutor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    EditText e_first_name,e_last_name, e_password, e_repassword, e_phone, e_email , e_sapid;
    Spinner s_year, s_college_name, s_course, s_stream;
    Button b_scanid, b_register;
    TextView t_login;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, StudentHomePage.class));
            return;
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        e_first_name = (EditText) findViewById(R.id.e_first_name);
        e_last_name = (EditText) findViewById(R.id.e_last_name);
        e_password = (EditText) findViewById(R.id.e_password);
        e_repassword = (EditText) findViewById(R.id.e_repassword);
        e_phone = (EditText) findViewById(R.id.e_phone);
        e_email = (EditText) findViewById(R.id.e_email);
        s_year = (Spinner) findViewById(R.id.s_year);
        e_sapid = (EditText) findViewById(R.id.e_sapid);
        s_college_name = (Spinner) findViewById(R.id.s_college_name);
        s_course = (Spinner) findViewById(R.id.s_course);
        s_stream = (Spinner) findViewById(R.id.s_stream);
        b_scanid = (Button) findViewById(R.id.b_scanid);
        b_register = (Button) findViewById(R.id.b_register);
        t_login = (TextView) findViewById(R.id.t_login);

        t_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SignUp.this,MainActivity.class);
                finish();
                startActivity(in);
            }
        });

        b_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!e_first_name.getText().toString().equals(""))
                {
                    if(!e_last_name.getText().toString().equals(""))
                    {
                        if(!e_password.getText().toString().equals("") && !e_repassword.getText().toString().equals(""))
                        {
                            if(e_password.getText().toString().equals(e_repassword.getText().toString()))
                            {
                                if(!e_phone.getText().toString().equals(""))
                                {
                                    if(!e_email.getText().toString().equals(""))
                                    {
                                        String year = s_year.getSelectedItem().toString();
                                        if(!year.equals("Select year"))
                                        {
                                            if(!e_sapid.getText().toString().equals(""))
                                            {
                                                String college = s_college_name.getSelectedItem().toString();
                                                if(!college.equals("Select college"))
                                                {
                                                    String course = s_course.getSelectedItem().toString();
                                                    if(!course.equals("Select course"))
                                                    {
                                                        String stream = s_stream.getSelectedItem().toString();
                                                        if(!stream.equals("Select stream"))
                                                        {
                                                            registerUser();
                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(getApplicationContext(),"Stream cannot be empty",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }else
                                                    {
                                                        Toast.makeText(getApplicationContext(),"Course cannot be empty",Toast.LENGTH_SHORT).show();
                                                    }
                                                }else
                                                {
                                                    Toast.makeText(getApplicationContext(),"College Name cannot be empty",Toast.LENGTH_SHORT).show();
                                                }
                                            }else
                                            {
                                                e_sapid.setError("Enter sap id");
                                                Toast.makeText(getApplicationContext(),"Sap ID cannot be empty",Toast.LENGTH_SHORT).show();
                                            }
                                        }else
                                        {
                                            Toast.makeText(getApplicationContext(),"Year cannot be empty",Toast.LENGTH_SHORT).show();
                                        }
                                    }else
                                    {
                                        e_email.setError("Enter email");
                                        Toast.makeText(getApplicationContext(),"Email cannot be empty",Toast.LENGTH_SHORT).show();
                                    }
                                }else
                                {
                                    e_phone.setError("Enter phone number");
                                    Toast.makeText(getApplicationContext(),"Phone cannot be empty",Toast.LENGTH_SHORT).show();
                                }
                            }else
                            {
                                e_repassword.setError("Enter matching passwords");
                                Toast.makeText(getApplicationContext(),"Your password does not match the reentered password",Toast.LENGTH_SHORT).show();
                            }
                        }else
                        {
                            e_password.setError("Enter password");
                            Toast.makeText(getApplicationContext(),"Password cannot be empty",Toast.LENGTH_SHORT).show();
                        }
                    }else
                    {
                        e_last_name.setError("Enter last name");
                        Toast.makeText(getApplicationContext(),"Last Name cannot be empty",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    e_first_name.setError("Enter first name");
                    Toast.makeText(getApplicationContext(),"First Name cannot be empty",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser() {

        final String first_name = e_first_name.getText().toString();
        final String last_name = e_last_name.getText().toString();
        final String pass = e_password.getText().toString();
        final String phone = e_phone.getText().toString();
        final String reg_email = e_email.getText().toString();
        final String year = s_year.getSelectedItem().toString();
        final String sapid = e_sapid.getText().toString();
        final String college_name = s_college_name.getSelectedItem().toString();
        final String course = s_course.getSelectedItem().toString();
        final String stream = s_stream.getSelectedItem().toString();
        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);

                        response = response.replace("register.php","");
                        Log.e("response",response);
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                //Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                JSONObject userJson = obj.getJSONObject("user");

                                //creating a new user object
                                User user = new User(
                                        userJson.getString("sap_id"),
                                        userJson.getString("first_name"),
                                        userJson.getString("email")
                                );

                                //storing the user in shared preferences
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                //starting the profile activity
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
                params.put("first_name", first_name);
                params.put("last_name", last_name);
                params.put("email", reg_email);
                params.put("password", pass);
                params.put("year_of_study", year);
                params.put("college", college_name);
                params.put("course", course);
                params.put("stream", stream);
                params.put("sap_id", sapid);
                params.put("phone_number", phone);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }

}
