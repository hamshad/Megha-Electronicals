package com.MeghaElectronicals.views;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.MeghaElectronicals.databinding.ActivityStopAlarmBinding;

public class StopAlarmActivity extends AppCompatActivity {


    ActivityStopAlarmBinding ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = ActivityStopAlarmBinding.inflate(getLayoutInflater());
        setContentView(ui.getRoot());

        ui.stopAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}