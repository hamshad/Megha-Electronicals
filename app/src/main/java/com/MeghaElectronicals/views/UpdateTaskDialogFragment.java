package com.MeghaElectronicals.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.MeghaElectronicals.R;
import com.MeghaElectronicals.common.MySharedPreference;
import com.MeghaElectronicals.databinding.FragmentUpdateTaskDialogBinding;
import com.MeghaElectronicals.databinding.LoginAlertdialogBinding;
import com.MeghaElectronicals.retrofit.ServiceRepository;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UpdateTaskDialogFragment extends BottomSheetDialogFragment {

    private static final String TAG = "UpdateTaskDialog";
    public static boolean isBottomSheetUp = false;
    FragmentUpdateTaskDialogBinding ui;
    private boolean isDialogShown = false;
    String TaskName = "";
    int TaskId = 0;
    private CompositeDisposable disposable;
    private ServiceRepository repo;
    private MySharedPreference pref;

    public static UpdateTaskDialogFragment newInstance(String TaskName, int TaskId) {
        isBottomSheetUp = true;
        UpdateTaskDialogFragment updateTaskDialogFragment = new UpdateTaskDialogFragment();
        Log.d(TAG, "TaskName: " + TaskName);
        Log.d(TAG, "TaskId: " + TaskId);
        Bundle args = new Bundle();
        args.putString("TaskName", TaskName);
        args.putInt("TaskId", TaskId);
        updateTaskDialogFragment.setArguments(args);
        return updateTaskDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposable = new CompositeDisposable();
        repo = new ServiceRepository(requireContext());
        pref = new MySharedPreference(requireContext());
        if (getArguments() != null) {
            Log.d(TAG, "TaskName: " + getArguments().getString("TaskName"));
            Log.d(TAG, "TaskId: " + getArguments().getInt("TaskId"));
            TaskName = getArguments().getString("TaskName");
            TaskId = getArguments().getInt("TaskId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ui = FragmentUpdateTaskDialogBinding.inflate(inflater);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenHeight = displayMetrics.heightPixels;
        ui.getRoot().setMinimumHeight(screenHeight);

        ui.backButton.setOnClickListener(v -> UpdateTaskDialogFragment.this.dismiss());

        ui.taskName.setText(TaskName);
        ui.statusBS.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.my_autocomplete_spinner, pref.fetchLogin().Role().equalsIgnoreCase("Director") ? List.of("Completed", "Cancel") : List.of("Completed") ));
        ui.statusBS.setThreshold(25);
        ui.createNewTaskButton.setOnClickListener(v -> updateTask());

        return ui.getRoot();
    }

    private void updateTask() {
        ui.createNewTaskButton.setEnabled(false);
        if (ui.descriptionBS.getText() == null || ui.descriptionBS.getText().toString().isBlank()) {
            showDialog("Enter Completion Description!");
            return;
        }
        if (ui.statusBS.getText() == null || ui.statusBS.getText().toString().isBlank()) {
            showDialog("Select Task Status!");
            return;
        }
        String descriptionBS = ui.descriptionBS.getText().toString();
        String statusBS = ui.statusBS.getText().toString();

        disposable.add(
                repo.getTasksUpdate(TaskId, descriptionBS, statusBS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                jsonElement -> {
                                    Log.d(TAG, "logOff: " + jsonElement.toString());
                                    this.dismiss();
                                },
                                throwable -> showDialog("Couldn't update task!")
                        )
        );
        ui.createNewTaskButton.setEnabled(true);
    }

    private void showDialog(String error) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        LoginAlertdialogBinding login_alertDialog = LoginAlertdialogBinding.inflate(getLayoutInflater());

        TextView errorMessage = login_alertDialog.errorMessage;
        Button ok = login_alertDialog.okButton;

        errorMessage.setText(error);
        dialog.setView(login_alertDialog.getRoot());

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
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        isBottomSheetUp = false;
        disposable.clear();
    }
}
