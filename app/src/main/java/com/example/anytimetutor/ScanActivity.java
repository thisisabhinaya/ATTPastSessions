package com.example.anytimetutor;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import com.google.zxing.Result;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
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
        Toast.makeText(this, rawResult.toString(),Toast.LENGTH_LONG).show();
        onBackPressed();
    }
}
