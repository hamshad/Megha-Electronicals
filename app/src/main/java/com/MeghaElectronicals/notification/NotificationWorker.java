package com.MeghaElectronicals.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.MeghaElectronicals.R;
import com.MeghaElectronicals.alarm.MyMediaPlayer;
import com.MeghaElectronicals.common.MySharedPreference;
import com.MeghaElectronicals.modal.TasksStatus;
import com.MeghaElectronicals.retrofit.ServiceRepository;
import com.MeghaElectronicals.views.StopAlarmActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NotificationWorker extends Worker {

    Context context;
    MySharedPreference pref;
    CompositeDisposable disposable;
    ServiceRepository repo;
//    MediaPlayer player;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        pref = new MySharedPreference(context);
        disposable = new CompositeDisposable();
        repo = new ServiceRepository(context);
//        player = MediaPlayer.create(context, R.raw.alarm_clock_old);
        Log.d("Work Manager", "NotificationWorker: " + context.getString(R.string.app_name));
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("Work Manager", "doWork: Work Manager Executed");

        disposable.add(
                repo.getTasksStatus(getInputData().getString("TaskId"))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onSuccessResponse, this::onErrorResponse)
        );

        return Result.success();
    }

    private void onSuccessResponse(TasksStatus status) {

        Log.d("TasksStatus", "onSuccessResponse: " + status.toString());

        if (status.Status().equalsIgnoreCase("Finished") && status.Status().equalsIgnoreCase("Rejected"))
            return;

//        player.setVolume(1.0f, 1.0f);
//        player.setLooping(true);
//        player.start();

        MyMediaPlayer.startPlayer(context);

        NotificationService.sendNotification(context, getInputData().getString("task"), getInputData().getString("desc"));

        Intent intent = new Intent(context, StopAlarmActivity.class);
        intent.putExtra("task", getInputData().getString("task"))
                .putExtra("desc", getInputData().getString("desc"));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setAction(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(intent);
    }

    private void onErrorResponse(Throwable throwable) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(context, "Couldn't play alarm!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onStopped() {
        super.onStopped();
        disposable.clear();
    }
}
