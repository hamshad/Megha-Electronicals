package com.example.buzzertest;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import com.example.buzzertest.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ActivityMainBinding ui;
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean o) {
            Log.d(TAG, "onActivityResult: "+o);
        }
    });

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onStart() {
        super.onStart();
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ui = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(ui.getRoot());
        FirebaseApp.initializeApp(this);

        // Manually Configuring Work Manager (not REQUIRED) and added WAKE_LOCK permission in manifest file
//        WorkManager.initialize(this, new Configuration.Builder().build());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                Log.d(TAG, "onComplete: "+task.getResult());
                ui.tokenText.setText(task.getResult());
            }
        });
    }
}