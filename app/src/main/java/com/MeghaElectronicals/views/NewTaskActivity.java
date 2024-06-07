package com.example.buzzertest.views;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.buzzertest.R;
import com.example.buzzertest.databinding.ActivityNewTaskBinding;
import com.example.buzzertest.databinding.LoginAlertdialogBinding;
import com.example.buzzertest.modal.DepartmentModal;
import com.example.buzzertest.modal.EmployeesListModal;
import com.example.buzzertest.modal.StatusModal;
import com.example.buzzertest.network.NetworkUtil;
import com.example.buzzertest.retrofit.ServiceRepository;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    private String StatusId = "", EmployeeId = "";
    private Date startDate = null;
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

                @SuppressLint("SimpleDateFormat")
                DatePickerDialog fromDatePicker = new DatePickerDialog(
                        NewTaskActivity.this,
                        (view, year, month, dayOfMonth) -> {
                            String fromDate = dayOfMonth + "-" + (month + 1) + "-" + year;
                            try {
                                startDate = new SimpleDateFormat("dd-MM-yyyy").parse(fromDate);
                                String startDateText = new SimpleDateFormat("dd-MMM-yyyy").format(startDate);
                                ui.startDateButton.setText(startDateText);
                            } catch (ParseException e) {
                                e.fillInStackTrace();
                            }
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
            if (ui.startDateButton.getText() == getString(R.string.select_start_date) || startDate == null) {
                showDialog("Select Start Date!");
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
                    Department: %s,
                    Employee: %s,
                    Status: %s,
                    AssignedToId: %s,
                    StatusId: %s.""", TaskName, Description, department, employees, status, EmployeeId, StatusId));
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
        disposable.add(
                repo.getEmployeesListData(modal.DID())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                employeesListModals -> {
                                    Log.d(TAG, "Employees: " + employeesListModals.toString());
                                    ui.employees.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.my_autocomplete_spinner, employeesListModals.stream().map(EmployeesListModal::Name).collect(Collectors.toList())));
                                    ui.employees.setThreshold(50);
                                    ui.employees.setOnItemClickListener((parent, view, position, id) -> EmployeeId = employeesListModals.get(position).EmpId());
                                },
                                this::showError
                        )
        );
    }

    private void showError(Throwable error) {
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
        alertDialog.setOnShowListener(dialog12 -> isDialogShown = true);
        alertDialog.setOnDismissListener(dialog1 -> isDialogShown = false);
        if (!isDialogShown) alertDialog.show();
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