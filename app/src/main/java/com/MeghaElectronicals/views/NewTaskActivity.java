package com.MeghaElectronicals.views;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.MeghaElectronicals.R;
import com.MeghaElectronicals.databinding.ActivityNewTaskBinding;
import com.MeghaElectronicals.databinding.LoginAlertdialogBinding;
import com.MeghaElectronicals.modal.DepartmentModal;
import com.MeghaElectronicals.modal.EmployeesListModal;
import com.MeghaElectronicals.modal.StatusModal;
import com.MeghaElectronicals.network.NetworkUtil;
import com.MeghaElectronicals.retrofit.ServiceRepository;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Response;

public class NewTaskActivity extends AppCompatActivity {

    private static final String TAG = "NewTaskActivity";
    ActivityNewTaskBinding ui;
    private boolean isDialogShown = false;
    private ServiceRepository repo;
    private String StatusId = "", AssignedToEmployeeId = "", DID = "";
    private final CompositeDisposable disposable = new CompositeDisposable();


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
        ui = ActivityNewTaskBinding.inflate(getLayoutInflater());
        setContentView(ui.getRoot());

        repo = new ServiceRepository(this);
        getStatusAndDepartment();

        ui.backButton.setOnClickListener((v) -> finish());

        ui.startDateButton.setOnClickListener(v -> {
            Date date = Calendar.getInstance().getTime();
            if (!isDialogShown) {

                // DATE PICKER
                @SuppressLint("SimpleDateFormat")
                DatePickerDialog fromDatePicker = new DatePickerDialog(
                        NewTaskActivity.this,
                        (view, year, month, dayOfMonth) -> {
                            String fromDate = dayOfMonth + "-" + (month + 1) + "-" + year;
                            try {
                                Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(fromDate);
                                String startDateText = new SimpleDateFormat("dd-MM-yyyy").format(startDate);
                                ui.startDateButton.setText(startDateText);
                            } catch (ParseException e) {
                                e.fillInStackTrace();
                            }

                            // TIME PICKER
                            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                                    (view1, hourOfDay, minute) -> ui.startDateButton.append(" " + String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)),
                                    Integer.parseInt(new SimpleDateFormat("hh").format(date)),
                                    Integer.parseInt(new SimpleDateFormat("mm").format(date)),
                                    false);

                            // Set the initial AM/PM value
                            if (new SimpleDateFormat("a", Locale.getDefault()).format(date).equals("AM")) {
                                timePickerDialog.updateTime(Integer.parseInt(new SimpleDateFormat("hh").format(date)), Integer.parseInt(new SimpleDateFormat("mm").format(date)));
                            } else {
                                timePickerDialog.updateTime(Integer.parseInt(new SimpleDateFormat("hh").format(date)) + 12, Integer.parseInt(new SimpleDateFormat("mm").format(date)));
                            }
                            timePickerDialog.show();
                        },
                        Integer.parseInt(new SimpleDateFormat("yyyy").format(date)),
                        Integer.parseInt(new SimpleDateFormat("MM").format(date)) - 1,
                        Integer.parseInt(new SimpleDateFormat("dd").format(date)));

                fromDatePicker.setOnShowListener(dialog -> {
                    isDialogShown = true;

                    fromDatePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getApplicationContext().getResources().getColor(R.color.black, null));
                    fromDatePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getApplicationContext().getResources().getColor(R.color.black, null));
                });
                fromDatePicker.setOnDismissListener(dialog -> isDialogShown = false);

                fromDatePicker.show();
            }
        });

        ui.createNewTaskButton.setOnClickListener(v -> {
            ui.createNewTaskButton.setEnabled(false);
            if (ui.task.getText() == null || ui.task.getText().toString().isBlank()) {
                showDialog("Enter Task Name!");
                return;
            }
            if (ui.description.getText() == null || ui.description.getText().toString().isBlank()) {
                showDialog("Enter Description!");
                return;
            }
            if (ui.department.getText() == null || ui.department.getText().toString().isBlank()) {
                showDialog("Select Department!");
                return;
            }
            if (ui.employees.getText() == null || ui.employees.getText().toString().isBlank()) {
                showDialog("Select Employees!");
                return;
            }
            if (ui.status.getText() == null || ui.status.getText().toString().isBlank()) {
                showDialog("Select Status!");
                return;
            }
            if (ui.startDateButton.getText() == getString(R.string.select_start_date)) {
                showDialog("Select Start Date and Time!");
                return;
            }
            if (!ui.startDateButton.getText().toString().contains(":")) {
                showDialog("Select Start Time!");
                return;
            }

            String TaskName = ui.task.getText().toString();
            String Description = ui.description.getText().toString();
            String department = ui.department.getText().toString();
            String employees = ui.employees.getText().toString();
            String status = ui.status.getText().toString();

            Log.d(TAG, String.format("""
                    [Create Task]:
                    Task: %s,
                    Description: %s,
                    StartDate: %s,
                    Department: %s,
                    Employee: %s,
                    Status: %s,
                    AssignedToId: %s,
                    DID: %s,
                    StatusId: %s.""", TaskName, Description, ui.startDateButton.getText(), department, employees, status, AssignedToEmployeeId, DID, StatusId));

            addTask(TaskName, Description, ui.startDateButton.getText().toString().concat(":00"), AssignedToEmployeeId, StatusId, DID);
        });
    }

    private void getStatusAndDepartment() {
        disposable.add(
                Single.zip(repo.getStatusListData(), repo.getDepartmentListData(), StatusAndDepartmentModal::new)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::showStatusAndDepartmentData,
                                this::showError
                        )
        );
    }

    private void showStatusAndDepartmentData(StatusAndDepartmentModal modal) {
        Log.d(TAG, "showStatusAndDepartmentData: " + modal.toString());
        ui.department.setAdapter(new ArrayAdapter<>(this, R.layout.my_autocomplete_spinner, modal.departmentModalList().stream().map(DepartmentModal::Name).collect(Collectors.toList())));
        ui.department.setThreshold(50);
        ui.status.setAdapter(new ArrayAdapter<>(this, R.layout.my_autocomplete_spinner, modal.statusModalsList().stream().map((StatusModal::Name)).collect(Collectors.toList())));
        ui.status.setThreshold(50);
        ui.department.setOnItemClickListener((parent, view, position, id) -> getEmployeesList(modal.departmentModalList().get(position)));
        ui.status.setOnItemClickListener((parent, view, position, id) -> StatusId = modal.statusModalsList().get(position).StatusId());
    }

    private void getEmployeesList(DepartmentModal modal) {
        DID = modal.DID();
        disposable.add(
                repo.getEmployeesListData(modal.DID())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                employeesListModals -> {
                                    Log.d(TAG, "Employees: " + employeesListModals.toString());
                                    ui.employees.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.my_autocomplete_spinner, employeesListModals.stream().map(EmployeesListModal::Name).collect(Collectors.toList())));
                                    ui.employees.setThreshold(50);
                                    ui.employees.setOnItemClickListener((parent, view, position, id) -> AssignedToEmployeeId = employeesListModals.get(position).EmpId());
                                },
                                this::showError
                        )
        );
    }

    private void addTask(String TaskName, String Description, String StartDate, String AssignedToId, String StatusId, String DID) {
        disposable.add(
                repo.addTask(TaskName, Description, StartDate, AssignedToId, StatusId, DID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                jsonElement -> {
                                    Log.d(TAG, "addTask: " + jsonElement.toString());
                                    Toast.makeText(this, "New Task Created Successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                },
                                this::showError
                        )
        );
    }

    private void showError(Throwable error) {
        ui.createNewTaskButton.setEnabled(true);
        if (!isDialogShown && error instanceof HttpException httpException) {

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
        Log.d(TAG, "showError: Concurrent Api Error\n" + error.toString());
    }

    private void showDialog(String error) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
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
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    private record StatusAndDepartmentModal(List<StatusModal> statusModalsList,
                                            List<DepartmentModal> departmentModalList) {
    }
}