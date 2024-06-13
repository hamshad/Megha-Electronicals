package com.MeghaElectronicals.views;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventCallback;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.MeghaElectronicals.R;
import com.MeghaElectronicals.alarm.AlarmReceiver;
import com.MeghaElectronicals.common.MySharedPreference;
import com.MeghaElectronicals.databinding.ActivityLoginBinding;
import com.MeghaElectronicals.modal.LoginModal;
import com.MeghaElectronicals.network.NetworkUtil;
import com.MeghaElectronicals.retrofit.ServiceRepository;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    ActivityLoginBinding ui;
    View rootView;
    boolean isDialogShown = false;
    private ServiceRepository repo;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private MySharedPreference pref;
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventCallback sensorEventCallback;


    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean o) {
            Log.d(TAG, "onActivityResult: " + o);
        }
    });

    private final ActivityResultLauncher<Intent> requestOverlayPermission = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            Log.d(TAG, "OVERLAY PERMISSION: " + Settings.canDrawOverlays(getApplicationContext()));
        }
    });

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            requestOverlayPermission.launch(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkUtil.isConnected(this)) {
            Snackbar.make(ui.getRoot(), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", v -> recreate())
                    .show();
        }

        if (ui.stopAnimRadio.getVisibility() == TextView.VISIBLE && !ui.stopAnimRadio.isChecked())
            makeSensorAnimation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(ui.getRoot());

        pref = new MySharedPreference(this);
        repo = new ServiceRepository(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ui.loginBackgroundImage.setRenderEffect(RenderEffect.createBlurEffect(50f, 50f, Shader.TileMode.REPEAT));
        }

        rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) {
                int translationY = -Math.min(keypadHeight, ui.loginCard.getHeight());
                int tranY = -(keypadHeight / 2);
                Log.d(TAG, "keypadHeight" + keypadHeight);
                Log.d(TAG, "screenHeight" + screenHeight);
                Log.d(TAG, "translationY" + translationY);
                Log.d(TAG, "tranY" + tranY);

                ui.appName.animate().alpha(0).setDuration(150).start();
                ui.loginCard.animate().translationY(tranY).setDuration(200).start();
            } else {
                Log.d(TAG, "keypadHeight" + keypadHeight);
                Log.d(TAG, "screenHeight" + screenHeight);
                ui.appName.animate().alpha(1).setDuration(250).start();
                ui.loginCard.animate().translationY(0).setDuration(200).start();
            }
        });

        ui.loginButton.setOnClickListener(this::onLoginButtonClicked);

        ui.loginBackgroundImage.setOnLongClickListener(v -> {
            if (ui.stopAnimRadio.getVisibility() == View.GONE) {
                ui.stopAnimRadio.setVisibility(View.VISIBLE);
                makeSensorAnimation();
            } else {
                ui.stopAnimRadio.setVisibility(View.GONE);
                sensorManager.unregisterListener(sensorEventCallback);
                recreate();
            }
            return true;
        });


        // TODO: REMOVE TEMPORARY ALARM
        ui.loginButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setAlarm(LoginActivity.this, "TASK", "DESCRIPTION");
                return false;
            }
        });

    }

    private void makeSensorAnimation() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorEventCallback = new SensorEventCallback() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                super.onSensorChanged(event);
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                System.out.println("X: " + x + " Y: " + y + " Z: " + z);

                ui.loginCard.setRotationX(y * 3);
                ui.loginCard.setRotationY(x * 3);
            }
        };
        sensorManager.registerListener(sensorEventCallback, sensor, SensorManager.SENSOR_DELAY_GAME);
        ui.stopAnimRadio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "onCheckedChanged: " + isChecked);
            if (isChecked) {
                sensorManager.unregisterListener(sensorEventCallback);
                ui.loginCard.setRotationX(0);
                ui.loginCard.setRotationY(0);
            } else {
                sensorManager.registerListener(sensorEventCallback, sensor, SensorManager.SENSOR_DELAY_GAME);
            }
        });
    }

    private void onLoginButtonClicked(View view) {
        String email = Objects.requireNonNull(ui.loginEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(ui.loginPassword.getText()).toString().trim();

        ui.loginButton.setEnabled(false);
        ui.loginButton.setText(null);
        ui.loginButtonProgress.setVisibility(View.VISIBLE);

        if (email.isEmpty() && password.isEmpty()) {
            showDialog("Enter the Credentials");
        } else if (email.isEmpty()) {
            showDialog("Enter the Email");
        } else if (password.isEmpty()) {
            showDialog("Enter the Password");
        } else if (NetworkUtil.isConnected(this)) {
            showDialog("No Internet Connection");
        } else {
            authentication(email, password);
        }
    }

    private void authentication(String email, String password) {
        disposable.add(
                repo.getLoginData(email, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::showData,
                                this::showError
                        )
        );
    }

    private void showData(LoginModal data) {

        Log.d("LOGIN API", data.toJsonString());

        pref.saveLogin(data.toJsonString());

        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showError(Throwable error) {
        if (!ui.loginButton.isEnabled() && error instanceof HttpException httpException) {

            Response<?> response = httpException.response();

            if (response != null && response.errorBody() != null) {
                try {
                    // Convert error body to a string
                    String errorBodyString = response.errorBody().string();

                    // Parse the JSON string to extract the error message
                    JSONObject jsonObject = new JSONObject(errorBodyString);
                    String errorMessage = jsonObject.getString("Message");

                    showDialog(errorMessage);
                } catch (Exception ex) {
                    ex.fillInStackTrace();
                    showDialog("Something went wrong!");
                }
            } else {
                showDialog(error.getMessage());
            }
        } else {
            showDialog("Something went wrong!");
        }
        Log.d(TAG, "showError: " + error.toString());
    }

    private void showDialog(String error) {

        ui.loginButtonProgress.setVisibility(View.GONE);
        ui.loginButton.setText(getString(R.string.login));
        ui.loginButton.setEnabled(true);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        View login_alertDialog = getLayoutInflater().inflate(R.layout.login_alertdialog, null);

        TextView errorMessage = login_alertDialog.findViewById(R.id.error_message);
        Button ok = login_alertDialog.findViewById(R.id.ok_button);

        errorMessage.setText(error);
        dialog.setView(login_alertDialog);

        AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogAnimation;
        ok.setOnClickListener(view -> alertDialog.dismiss());
        alertDialog.setOnDismissListener(dialog1 -> isDialogShown = false);
        if (!isDialogShown) {
            isDialogShown = true;
            alertDialog.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ui.stopAnimRadio.getVisibility() == View.VISIBLE)
            sensorManager.unregisterListener(sensorEventCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
        if (ui.stopAnimRadio.getVisibility() == View.VISIBLE)
            sensorManager.unregisterListener(sensorEventCallback);
    }


    public void setAlarm(Context context, String task, String desc) {
        Intent intentAlarmReceiver = new Intent(context, AlarmReceiver.class);
        intentAlarmReceiver.putExtra("task", task);
        intentAlarmReceiver.putExtra("desc", desc);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentAlarmReceiver, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Toast.makeText(context, "ALARM SET", Toast.LENGTH_SHORT).show();
        manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 3000, pendingIntent);
    }
}