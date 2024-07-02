package com.MeghaElectronicals.notification;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.MeghaElectronicals.R;
import com.MeghaElectronicals.alarm.MyMediaPlayer;
import com.MeghaElectronicals.alarm.SetAlarm;
import com.MeghaElectronicals.common.MyFunctions;
import com.MeghaElectronicals.common.MySharedPreference;
import com.MeghaElectronicals.modal.LoginModal;
import com.MeghaElectronicals.modal.TasksStatus;
import com.MeghaElectronicals.retrofit.ServiceRepository;
import com.MeghaElectronicals.views.StopAlarmActivity;

import java.text.ParseException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
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

        Runnable releaseWakeLock = MyMediaPlayer.runWakeLock(context);

        disposable.add(
                repo.getTasksStatus(getInputData().getString("TaskId"))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retryWhen(error -> {
                            Log.d("NotificationWorker", "doWork: retrying the TaskStatus api!");
                            return error.flatMap(throwable -> Flowable.just(new Object()));
                        })
                        .subscribe(this::onSuccessResponse, this::onErrorResponse)
        );

        releaseWakeLock.run();

        return Result.success();
    }

    private void onSuccessResponse(TasksStatus status) {

        Log.d("TasksStatus", "onSuccessResponse: " + status.toString());

        if (status.Status().equalsIgnoreCase(context.getString(R.string.finished)) || status.Status().equalsIgnoreCase(context.getString(R.string.cancel))) {
            new SetAlarm().removeAlarm(context, getInputData().getString("task"), getInputData().getString("desc"), Integer.parseInt(getInputData().getString("TaskId")));
        } else {
            try {
                new SetAlarm().setAlarm(context, getInputData().getString("task"), getInputData().getString("desc"), Integer.parseInt(getInputData().getString("TaskId")), status.StartDate(), status.EndDate(), MyFunctions.isInFuture(status.EndDate()));
            } catch (ParseException e) {
                e.fillInStackTrace();
            }
        }

        String TaskName = status.TaskName();
        String Description = status.CompletionDescription() == null ? status.Description() : status.CompletionDescription();

        LoginModal loginModal = pref.fetchLogin();
        Log.d("isCreatedByMe", "onSuccessResponse: "+loginModal.EmpId().equals(status.CreatedBy()));
        boolean isCreatedByMe = loginModal.EmpId().equals(status.CreatedBy()) && MyFunctions.isInFuture(status.StartDate());
        int alarm_type = loginModal.Role().equalsIgnoreCase("Director") ? R.raw.director_alarm
                : isCreatedByMe ? R.raw.soft_alarm : R.raw.alarm_ringtone;
        Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + alarm_type);

        MyMediaPlayer.startPlayer(context, uri, !isCreatedByMe);

        NotificationService.sendNotification(context, TaskName, Description);

        if (isCreatedByMe) return;

        Intent intent = new Intent(context, StopAlarmActivity.class);
        intent.putExtra("task", TaskName)
                .putExtra("desc", Description);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setAction(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.startActivity(intent);
        } else if (MyFunctions.isInForeground()) {
            context.startActivity(intent);
        }
    }

    private void onErrorResponse(Throwable throwable) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(context, "Couldn't play alarm!", Toast.LENGTH_SHORT).show());
        Log.e("NotificationWorker", "onErrorResponse: "+throwable.toString());
    }

    @Override
    public void onStopped() {
        super.onStopped();
        disposable.clear();
    }
}
