package com.MeghaElectronicals.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.MeghaElectronicals.R;
import com.MeghaElectronicals.adapter.TaskListAdapter;
import com.MeghaElectronicals.common.MySharedPreference;
import com.MeghaElectronicals.databinding.ActivityMainBinding;
import com.MeghaElectronicals.modal.LoginModal;
import com.MeghaElectronicals.network.NetworkUtil;
import com.MeghaElectronicals.retrofit.ServiceRepository;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ActivityMainBinding ui;
    private MySharedPreference pref;
    private ServiceRepository repo;
    private CompositeDisposable disposable = new CompositeDisposable();


    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkUtil.isConnected(this)) {
            Snackbar.make(ui.getRoot(), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", v -> recreate())
                    .show();
        }
        getTaskLists();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ui = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(ui.getRoot());

        pref = new MySharedPreference(this);
        repo = new ServiceRepository(this);

        // Manually Configuring Work Manager (not REQUIRED) and added WAKE_LOCK permission in manifest file
//        WorkManager.initialize(this, new Configuration.Builder().build());

        ui.drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ui.mainDrawer.openDrawer(GravityCompat.START);
            }
        });

        ui.mainDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                TextView username = drawerView.findViewById(R.id.username);
                String drawerText = new LoginModal(pref.fetchLogin()).FullName().split(" ")[0];
                username.setText(drawerText);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

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

    private void getTaskLists() {
        disposable.add(
                repo.getTasksListData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(tasksListModals -> {
                            Log.d(TAG, "getTaskLists: " + tasksListModals.toString());
                            ui.taskListRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            ui.taskListRecycler.setAdapter(new TaskListAdapter(tasksListModals, getApplicationContext()));
                        }, throwable -> {
                            Log.d(TAG, "getTaskLists: " + throwable.toString());
                        })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}