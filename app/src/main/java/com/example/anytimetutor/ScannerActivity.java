package com.example.anytimetutor;

import androidx.appcompat.app.AppCompatActivity;
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

                if(isChecked){
                    Intent intent = new Intent(ScannerActivity.this, ScanActivity.class);
                    en.setChecked(false);
                    startActivity(intent);
                }
            }
        });
    }
}
