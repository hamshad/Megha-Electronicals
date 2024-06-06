package com.example.buzzertest;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import com.example.buzzertest.databinding.ActivityMainBinding;
import com.example.buzzertest.views.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ActivityMainBinding ui;
    private MySharedPreference pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ui = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(ui.getRoot());

        pref = new MySharedPreference(this);

        // Manually Configuring Work Manager (not REQUIRED) and added WAKE_LOCK permission in manifest file
//        WorkManager.initialize(this, new Configuration.Builder().build());

        ui.drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ui.mainDrawer.openDrawer(GravityCompat.START);
            }
        });

        ui.mainNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.add_employee) {
                    Toast.makeText(MainActivity.this, "Add Employee", Toast.LENGTH_SHORT).show();
                }
                if (menuItem.getItemId() == R.id.log_out) {
                    Toast.makeText(MainActivity.this, "Log Out", Toast.LENGTH_SHORT).show();
                    pref.deleteLogin();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
                return true;
            }
        });
    }
}