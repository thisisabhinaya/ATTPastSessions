package com.example.anytimetutor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.content.Intent;

public class ScannerActivity extends AppCompatActivity {

    public static TextView tvresult;
    private Button btn;
    Switch en;
    int MY_PERMISSIONS_REQUEST_CAMERA=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        getSupportActionBar().hide();

        en=(Switch)findViewById(R.id.switch1);
        en.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked) {
                        Intent intent = new Intent(ScannerActivity.this, ScanActivity.class);
                        en.setChecked(false);
                        startActivity(intent);
                }
            }
        });
    }
}
