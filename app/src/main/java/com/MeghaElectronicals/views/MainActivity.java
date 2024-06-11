package com.MeghaElectronicals.views;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
    private BottomSheetBehavior<View> bottomSheetBehavior;


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

        bottomSheetBehavior = BottomSheetBehavior.from(ui.bottomSheetMain.getRoot());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        Log.d(TAG, "MOBILE SDK: " + Build.VERSION.SDK_INT);
        Log.d(TAG, "APP SDK: " + 27);

        pref = new MySharedPreference(this);
        repo = new ServiceRepository(this);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenHeight = displayMetrics.heightPixels;
        int peekHeight = (int) (screenHeight * 0.65); // Set peek height to 65% of the screen height
        bottomSheetBehavior.setPeekHeight(peekHeight);

        // Manually Configuring Work Manager (not REQUIRED) and added WAKE_LOCK permission in manifest file
//        WorkManager.initialize(this, new Configuration.Builder().build());


        setListenersAndStuff();
        handleBackPress();

    }

    private void setListenersAndStuff() {
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
                    disposable.add(
                            repo.logOff()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            jsonElement -> {
                                                Log.d(TAG, "addTask: " + jsonElement.toString());
                                                pref.deleteLogin();
                                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                finish();
                                            },
                                            throwable -> Toast.makeText(getApplicationContext(), "Couldn't Logout", Toast.LENGTH_LONG).show()
                                    )
                    );
                }
                return true;
            }
        });

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.d(TAG, "Bottom Sheet Expanded");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.d(TAG, "Bottom Sheet Collapsed");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.d(TAG, "Bottom Sheet Dragging");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.d(TAG, "Bottom Sheet Settling");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.d(TAG, "Bottom Sheet Hidden");
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        Log.d(TAG, "Bottom Sheet Half Expanded");
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }

    public void openBottomSheet() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void getTaskLists() {
        disposable.add(
                repo.getTasksListData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(tasksListModals -> {
                            Log.d(TAG, "getTaskLists: " + tasksListModals.toString());
                            ui.taskListRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            ui.taskListRecycler.setAdapter(new TaskListAdapter(tasksListModals, MainActivity.this));
                        }, throwable -> {
                            Log.d(TAG, "getTaskLists: " + throwable.toString());
                        })
        );
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {

                Rect outRect = new Rect();
                ui.bottomSheetMain.getRoot().getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY()))
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    private void handleBackPress() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN)
                    finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}