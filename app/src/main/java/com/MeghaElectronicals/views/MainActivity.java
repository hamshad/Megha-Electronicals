package com.MeghaElectronicals.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.MeghaElectronicals.R;
import com.MeghaElectronicals.adapter.TaskListAdapter;
import com.MeghaElectronicals.common.MySharedPreference;
import com.MeghaElectronicals.databinding.ActivityMainBinding;
import com.MeghaElectronicals.databinding.LoaderDialogBinding;
import com.MeghaElectronicals.network.NetworkUtil;
import com.MeghaElectronicals.retrofit.ServiceRepository;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.function.Function;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ActivityMainBinding ui;
    private MySharedPreference pref;
    private ServiceRepository repo;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private TaskListAdapter taskListAdapter;


    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkUtil.isConnected(this)) {
            Snackbar.make(ui.getRoot(), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", v -> recreate())
                    .show();
        }
        getTaskLists();
        if (ui.refreshTaskList.isRefreshing()) ui.refreshTaskList.setRefreshing(false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ui = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(ui.getRoot());

        Log.d(TAG, "MOBILE SDK: " + Build.VERSION.SDK_INT);
        Log.d(TAG, "APP SDK: " + 27);

        pref = new MySharedPreference(this);
        repo = new ServiceRepository(this);

        taskListAdapter = new TaskListAdapter(this);
        ui.taskListRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ui.taskListRecycler.setAdapter(taskListAdapter);
        // Manually Configuring Work Manager (not REQUIRED) and added WAKE_LOCK permission in manifest file
//        WorkManager.initialize(this, new Configuration.Builder().build());

        ui.refreshTaskList.setOnRefreshListener(this::getTaskLists);

        setListenersAndStuff();
    }

    private void setListenersAndStuff() {

        if (pref.fetchLogin().Role().equalsIgnoreCase("Employees")) {
            ui.mainNavView.getMenu().findItem(R.id.new_task).setVisible(false);
        }

        ui.drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ui.mainDrawer.openDrawer(GravityCompat.START);
            }
        });

        ui.infoBtn.setOnClickListener(v -> revealInfoCard(ui.infoRevealCard.getVisibility() == View.INVISIBLE));

        ui.mainDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                TextView username = drawerView.findViewById(R.id.username);
                String drawerText = pref.fetchLogin().FullName().split(" ")[0];
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
                                                Log.d(TAG, "logOff: " + jsonElement.toString());
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
    }

    private void getTaskLists() {
        Function<Boolean, Void> loader = loader();
        if (taskListAdapter.getItemCount() == 0) loader.apply(true);
        new Handler().postDelayed(() ->
                disposable.add(
                        repo.getTasksListData()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(tasksListModals -> {
                                    Log.d(TAG, "getTaskLists: " + tasksListModals.toString());
                                    taskListAdapter.addTasksList(tasksListModals);
                                    loader.apply(false);
                                }, throwable -> {
                                    Log.d(TAG, "getTaskLists: " + throwable.toString());
                                    runOnUiThread(() -> Toast.makeText(this, "Couldn't Load Tasks!", Toast.LENGTH_SHORT).show());
                                    loader.apply(false);
                                })
                ), 2000);
        ui.refreshTaskList.setRefreshing(false);
    }

    private void revealInfoCard(boolean open) {

        if (!open) {
            concealInfoCard();
            return;
        }

        Animator animator = ViewAnimationUtils.createCircularReveal(
                ui.infoRevealCard,
                ui.infoRevealCard.getWidth(),
                0,
                0f,
                (float) Math.hypot(ui.infoRevealCard.getWidth(), ui.infoRevealCard.getHeight()));

        // Set a natural ease-in/ease-out interpolator.
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        ui.infoRevealCard.setVisibility(View.VISIBLE);
        // Finally start the animation
        animator.start();
    }

    private void concealInfoCard() {

        Animator animator = ViewAnimationUtils.createCircularReveal(
                ui.infoRevealCard,
                ui.infoRevealCard.getWidth(),
                0,
                (float) Math.hypot(ui.infoRevealCard.getWidth(), ui.infoRevealCard.getHeight()),
                0f);

        // Set a natural ease-in/ease-out interpolator.
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ui.infoRevealCard.setVisibility(View.INVISIBLE);
            }
        });
        // Finally start the animation
        animator.start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (ui.infoRevealCard.getVisibility() == View.VISIBLE) {

                Rect outRect = new Rect();
                ui.infoRevealCard.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY()))
                    concealInfoCard();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // Function Currying
    private Function<Boolean, Void> loader() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.LoaderDialog);
        LoaderDialogBinding loaderUi = LoaderDialogBinding.inflate(getLayoutInflater());

        dialog.setView(loaderUi.getRoot());

        AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogAnimation;
        alertDialog.getWindow().getAttributes().width = ui.getRoot().getLayoutParams().width;

        return (Boolean open) -> {
            if (open) alertDialog.show();
            else alertDialog.dismiss();
            return null;
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}