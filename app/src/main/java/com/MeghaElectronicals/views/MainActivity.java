package com.example.buzzertest.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.example.buzzertest.MySharedPreference;
import com.example.buzzertest.R;
import com.example.buzzertest.databinding.ActivityMainBinding;
import com.example.buzzertest.network.NetworkUtil;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ActivityMainBinding ui;
    private MySharedPreference pref;


    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkUtil.isConnected(this)) {
            Snackbar.make(ui.getRoot(), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", v -> recreate())
                    .show();
        }
    }


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
                if (menuItem.getItemId() == R.id.new_task) {
                    startActivity(new Intent(getApplicationContext(), NewTaskActivity.class));
                    new Handler(Looper.getMainLooper()).post(() -> ui.mainDrawer.close());
                }
                if (menuItem.getItemId() == R.id.log_out) {
                    pref.deleteLogin();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
                return true;
            }
        });
    }
}