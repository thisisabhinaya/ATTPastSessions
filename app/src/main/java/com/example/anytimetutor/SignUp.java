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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    EditText e_first_name,e_last_name, e_password, e_repassword, e_phone, e_email , e_sapid;
    Spinner s_year, s_college_name, s_course, s_stream;
    Button b_scanid, b_register;
    TextView t_login;
    ProgressBar progressBar;

    String firstname, lastname, stringsappy,stringpass, email1, phone;

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
        //b_scanid = (Button) findViewById(R.id.b_scanid);
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

                firstname=e_first_name.getText().toString();
                lastname=e_last_name.getText().toString();
                stringsappy= e_sapid.getText().toString();
                phone=e_phone.getText().toString();
                email1=e_email.getText().toString();
                stringpass=e_password.getText().toString();

                if(!firstname.equals("")&& !hasNum(firstname)&& !hasSpl(firstname))
                {
                    if(!lastname.equals("") && !hasNum(lastname)&& !hasSpl(lastname))
                    {
                        if(!stringpass.equals("") && !e_repassword.getText().toString().equals("") && !(stringpass.length()<8))
                        {
                            if(e_password.getText().toString().equals(e_repassword.getText().toString()))
                            {
                                if(!phone.equals("") && (phone.length()==10) && !hasSpl(phone))
                                {
                                    if(!email1.equals("") && isValidEmail(email1))
                                    {
                                        String year = s_year.getSelectedItem().toString();
                                        if(!year.equals("Select year"))
                                        {
                                            //SAP ID has to be between 10 and 12 digits, 11 digits is actual length+1 if digit preceding 0
                                            if(!stringsappy.equals("") && !(stringsappy.length()<10) && !(stringsappy.length()>12))
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
                                                            Toast.makeText(getApplicationContext(),"Invalid Stream, try again!",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }else
                                                    {
                                                        Toast.makeText(getApplicationContext(),"Invalid Course, try again!",Toast.LENGTH_SHORT).show();
                                                    }
                                                }else
                                                {
                                                    Toast.makeText(getApplicationContext(),"Invalid College Name, try again!",Toast.LENGTH_SHORT).show();
                                                }
                                            }else
                                            {
                                                e_sapid.setError("Enter unique, valid SAP ID");
                                                Toast.makeText(getApplicationContext(),"Invalid SAP ID, ",Toast.LENGTH_SHORT).show();
                                            }
                                        }else
                                        {
                                            Toast.makeText(getApplicationContext(),"Invalid Year, try again!",Toast.LENGTH_SHORT).show();
                                        }
                                    }else
                                    {
                                        e_email.setError("Enter valid email");
                                        Toast.makeText(getApplicationContext(),"Invalid Email, try again!",Toast.LENGTH_SHORT).show();
                                    }
                                }else
                                {
                                    e_phone.setError("Enter 10 digit phone number");
                                    Toast.makeText(getApplicationContext(),"Invalid Phone try again!",Toast.LENGTH_SHORT).show();
                                }
                            }else
                            {
                                e_repassword.setError("Enter matching passwords");
                                Toast.makeText(getApplicationContext(),"Your password does not match the reentered password",Toast.LENGTH_SHORT).show();
                            }
                        }else
                        {
                            e_password.setError("Enter password of 8 characters");
                            Toast.makeText(getApplicationContext(),"Invalid Password, try again!",Toast.LENGTH_SHORT).show();
                        }
                    }else
                    {
                        e_last_name.setError("Enter valid last name");
                        Toast.makeText(getApplicationContext(),"Invalid Last Name, try again!",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    e_first_name.setError("Enter valid first name");
                    Toast.makeText(getApplicationContext(),"Invalid First Name, try again!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean hasNum(String str)
    {
        boolean flag=false; char ch;
        for (int i=0; i<str.length();i++)
        {

            ch= str.charAt(i);
            if (Character.isDigit(ch)) {
                flag = true;
                break;
            }

        }
        return flag;
    }
    private boolean hasSpl(String str)
    { String splchars="!@#$%^&*()~`+-/*?,[]{}";
        boolean flag=false; char ch; String ch1;
        for (int i=0; i<str.length();i++)
        {
            ch1="";
            ch= str.charAt(i);
            ch1=ch+"";
            if (splchars.contains(ch1))
            {
                flag=true;
                break;
            }

        }
        return flag;
    }
    private  boolean isValidEmail(String email)
    {

        String reg1 = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        String reg2 = "^[A-Za-z0-9+_.-]+@(.+)$";

        Pattern pattern1 = Pattern.compile(reg1);
        Pattern pattern2 = Pattern.compile(reg2);
        //for checking there arent multiple fullstops in mail ID
        Matcher matcher1 = pattern1.matcher(email);
        //for checking the username of email has only digtis, alphabets and dots, dashes, underscores
        Matcher matcher2 = pattern2.matcher(email);
        if( matcher1.matches() && matcher2.matches())
            return true;
        else
            return false;
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
                                        userJson.getString("email"),
                                        userJson.getString("scan_status")
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
