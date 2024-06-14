package com.MeghaElectronicals.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.MeghaElectronicals.R;
import com.MeghaElectronicals.common.MySharedPreference;
import com.MeghaElectronicals.databinding.ActivitySplashScreenBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.Executors;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";
    ActivitySplashScreenBinding ui;
    private MySharedPreference pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(ui.getRoot());
        FirebaseApp.initializeApp(this);

        pref = new MySharedPreference(this);

        // TODO: remove temp login
//        LoginModal modal = new LoginModal(
//                "Kw0XrR-FR9ppSY5KMJlJ6KVjNohCQ9Z-JtH0hXlCJQ2iNfdtso4HMtp5koo5-ZrWZGwAEVYY8Cw5tjdMNpiW4DFntOOnyqf_eSWhUqQUIqJwSZKO_JYMNiaLA1sleP4FPuaYhASU168C3vwtZTu_VNM893Klfnz7zW1t06p-AIsU1b4DGMrUlv6zwkxeJUDf5LWajOokmoroGcXUxapmUkPVmWPgU4wUBo8e-0ekESDPV4cp_giFmuL-qW59WgWDpZFzZuEacY3WR4cLr5d2aK9s_jPyH--YX6X5SLfYChP4XhgiQCC-W9ptT02K8IXfQ7VWoITbjFz8Bejk0n4NR0fNO6L8XL4SE16wHpTUx7MSR_4Sx6qJCUhFliKtOKNR7vhu_QEeZr61YZip2g8G9MnFbkpt5lzNVgLoQQz5BhyXGpUt5ZscaqdtLG00FAhBzrWy_VtUTBXjUJtAgDhpuuz3bGvwjTZkhrBbF2JS7dxdIKOklw6wYT1_MsL-Ti87t_3k6U-Qmww5iBe9FCfVng",
//                "bearer",
//                "meghaelectricals@gmail.com",
//                "07-06-2024 11:33:07",
//                "21-06-2024 11:33:07",
//                "b9e17060-c652-4a4c-936e-a5d88c7add91",
//                "689f83ae-9db8-43b1-a3d9-ff4fd2ce047e",
//                "Director",
//                "Santosh Pawar"
//        );
//        pref.saveLogin(modal.toJsonString());

        Executors.newSingleThreadExecutor().execute(() -> FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                Log.d(TAG, "onComplete: " + task.getResult());
                pref.saveToken(task.getResult());
            }
        }));

        char[] app_name = getResources().getString(R.string.app_name).toCharArray();
        int DELAY_ANIMATION = 200;
        Handler handler = new Handler(Looper.getMainLooper());

        for (char c : app_name) {
            DELAY_ANIMATION += 60;
            handler.postDelayed(() -> ui.appNameSplash.append(String.valueOf(c)), DELAY_ANIMATION);
        }
        DELAY_ANIMATION += 200;
        handler.postDelayed(() -> {
            Intent intent = new Intent(this, pref.fetchLogin() == null ? LoginActivity.class : MainActivity.class);
            startActivity(intent);
            finish();
        }, DELAY_ANIMATION);
    }
}