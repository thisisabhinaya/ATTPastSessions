package com.example.anytimetutor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

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

public class ScanActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler{

    private ZBarScannerView mScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZBarScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
        getSupportActionBar().hide();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(rawResult.getText().toString()));
        //startActivity(intent);
        Toast.makeText(this, rawResult.getContents(),Toast.LENGTH_LONG).show();
        final User user = SharedPrefManager.getInstance(this).getUser();
        final String id = user.getId();

        //Comparing entered sap id during registration and scanned id through barcode
        if(user.getId().equals(rawResult.getContents().substring(1,rawResult.getContents().length())))
        {
            //Updating scan status in database
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_UPDATE_SCAN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

//                            response = response.replace("update_scan.php","");
                            Log.e("response",response);
                            try {
                                //converting response to json object
                                JSONObject obj = new JSONObject(response);

                                //if no error in response
                                if (obj.getBoolean("error")) {
                                    Toast.makeText(getApplicationContext(), "An unknown error occurred...", Toast.LENGTH_SHORT).show();
                                } else {

                                    //Updating scan status value in local cache (Shared Preference)
                                    SharedPrefManager.getInstance(getApplicationContext()).update_scan_status("1");

                                    Toast.makeText(getApplicationContext(), "ID verification successful!!", Toast.LENGTH_SHORT).show();

                                    Intent in = new Intent(ScanActivity.this, StudentHomePage.class);
                                    ActivityCompat.finishAffinity(ScanActivity.this);
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
                    params.put("scan_status", "1");
                    params.put("sap_id", id);
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

        }
        //If entered sap id and scanned id does not match
        else {
            Toast.makeText(this, "Invalid SAP ID", Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }
}
