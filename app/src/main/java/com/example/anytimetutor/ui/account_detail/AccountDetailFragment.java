package com.example.anytimetutor.ui.account_detail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.anytimetutor.R;
import com.example.anytimetutor.StudentHomePage;
import com.example.anytimetutor.SupportFiles.SharedPrefManager;
import com.example.anytimetutor.SupportFiles.URLs;
import com.example.anytimetutor.SupportFiles.User;
import com.example.anytimetutor.SupportFiles.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountDetailFragment extends Fragment {

    TextView name, email, phone, sap_id, college, course, year, stream;
    String p, c, cou, y, str;

    public ProgressDialog mProgressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account_detail, container, false);

        name = (TextView) root.findViewById(R.id.name);
        email = (TextView) root.findViewById(R.id.email);
        phone = (TextView) root.findViewById(R.id.phone);
        sap_id = (TextView) root.findViewById(R.id.sap_id);
        college = (TextView) root.findViewById(R.id.college);
        course = (TextView) root.findViewById(R.id.course);
        year = (TextView) root.findViewById(R.id.year);
        stream = (TextView) root.findViewById(R.id.stream);

        final User user = SharedPrefManager.getInstance(getActivity()).getUser();
        final String id = user.getId();
        final String nm = user.getUsername();
        final String em = user.getEmail();

        showProgressDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_ACCOUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        hideProgressDialog();

                        //response = response.replace("register.php","");
                        Log.e("response",response);
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                JSONObject userJson = obj.getJSONObject("user");

                                //creating a new user object

                                p = userJson.getString("phone");
                                c= userJson.getString("college");
                                y = userJson.getString("year");
                                cou = userJson.getString("course");
                                str = userJson.getString("stream");

                                phone.setText(p);
                                college.setText(c);
                                course.setText(cou);
                                year.setText(y);
                                stream.setText(str);

                            } else {
                                Toast.makeText(getActivity(), obj.getString("error_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sap_id", id);
                return params;
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        name.setText(nm);
        email.setText(em);
        sap_id.setText(id);

        return root;
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
}