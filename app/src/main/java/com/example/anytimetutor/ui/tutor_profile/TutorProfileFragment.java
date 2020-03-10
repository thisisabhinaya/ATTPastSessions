package com.example.anytimetutor.ui.tutor_profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.anytimetutor.EditTutorProfileActivity;
import com.example.anytimetutor.R;
import com.example.anytimetutor.SupportFiles.SharedPrefManager;
import com.example.anytimetutor.SupportFiles.URLs;
import com.example.anytimetutor.SupportFiles.User;
import com.example.anytimetutor.SupportFiles.VolleySingleton;
import com.example.anytimetutor.ViewRequestActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TutorProfileFragment extends Fragment {

    TextView name, bio;
    TextView session_count;
    Button edit, view;

    public ProgressDialog mProgressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tutor_profile, container, false);
       // final TextView textView = root.findViewById(R.id.text_gallery);

        name = (TextView) root.findViewById(R.id.name);
        bio = (TextView) root.findViewById(R.id.bio);
        //session_count = (TextView) root.findViewById(R.id.session_count);
        edit = (Button) root.findViewById(R.id.edit);
        view = (Button)root.findViewById(R.id.view);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), EditTutorProfileActivity.class);
                in.putExtra("bio",bio.getText().toString());
                startActivity(in);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), ViewRequestActivity.class);
                startActivity(in);
            }
        });

        final User user = SharedPrefManager.getInstance(getActivity()).getUser();
        final String id = user.getId();

        showProgressDialog();

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
                                Toast.makeText(getActivity(), "An unknown error occurred...", Toast.LENGTH_SHORT).show();
                            } else {
                                name.setText(obj.getString("first_name")+" "+obj.getString("last_name"));
                                bio.setText(obj.getString("tutor_bio"));
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

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}