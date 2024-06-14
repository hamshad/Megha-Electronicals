package com.MeghaElectronicals.retrofit;

import android.content.Context;
import android.util.Log;

import com.MeghaElectronicals.common.MySharedPreference;
import com.MeghaElectronicals.modal.DepartmentModal;
import com.MeghaElectronicals.modal.EmployeesListModal;
import com.MeghaElectronicals.modal.LoginModal;
import com.MeghaElectronicals.modal.StatusModal;
import com.MeghaElectronicals.modal.TasksListModal;
import com.MeghaElectronicals.modal.TasksStatus;
import com.google.gson.JsonElement;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.core.Single;

public class ServiceRepository {

    private static final String TAG = "ServiceRepository";
    MySharedPreference pref;
    LoginModal userData;
    Service service;

    public ServiceRepository(Context context) {
        pref = new MySharedPreference(context);
        service = RetrofitInstance.getService();
        userData = pref.fetchLogin();
    }

    public Single<LoginModal> getLoginData(String email, String password) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("Email", email);
        map.put("Password", password);
        map.put("Token", pref.fetchToken());
        Log.d("Token", pref.fetchToken());

        Log.d(TAG, "Email: " + email + "\nPassword: " + password);

        return service.getLogin(map);
    }

    public Single<List<StatusModal>> getStatusListData() {
        return service.getStatusList();
    }

    public Single<List<DepartmentModal>> getDepartmentListData() {
        return service.getDepartmentList();
    }

    public Single<List<EmployeesListModal>> getEmployeesListData(String DID) {
        HashMap<String, String> formData = new HashMap<>();
        formData.put("DID", DID);
        formData.put("OfficeId", userData.OfficeId());
        formData.put("Role", userData.Role());

        Log.d(TAG, "DID: " + DID + ", OfficeId: " + userData.OfficeId() + ", Role: " + userData.Role());

        return service.getEmployeesList(formData);
    }

    public Single<JsonElement> addTask(String TaskName, String Description, String StartDate, String AssignedToId, String StatusId, String DID) {
        HashMap<String, String> formData = new HashMap<>();
        formData.put("OfficeId", userData.OfficeId());
        formData.put("AssignedToId", AssignedToId);
        formData.put("DID", DID);
        formData.put("EmpId", userData.EmpId());
        formData.put("StatusId", StatusId);
        formData.put("TaskName", TaskName);
        formData.put("Description", Description);
        formData.put("StartDate", StartDate);
        formData.put("Role", userData.Role());

        formData.forEach((s, s2) -> Log.d(TAG, s+": "+s2));

        return service.addTasks(formData);
    }

    public Single<List<TasksListModal>> getTasksListData() {
        HashMap<String, String> formData = new HashMap<>();
        formData.put("EmpId", userData.EmpId());
        formData.put("Role", userData.Role());

        Log.d(TAG, "EmpId: " + userData.EmpId() + ", Role: " + userData.Role());

        return service.getTasksList(formData);
    }

    public Single<JsonElement> getTasksUpdate(int TaskId, String CompletionDescription, String Status) {
        HashMap<String, String> formData = new HashMap<>();
        formData.put("EmpId", userData.EmpId());
        formData.put("Role", userData.Role());
        formData.put("CompletionDate", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime()));
        formData.put("CompletionDescription", CompletionDescription);
        formData.put("Status", Status);
        formData.put("TaskId", String.valueOf(TaskId));

        formData.forEach((s, s2) -> Log.d(TAG, s+": "+s2));

        return service.getTasksUpdate(formData);
    }

    public Single<TasksStatus> getTasksStatus(String TaskId) {
        HashMap<String, String> formData = new HashMap<>();
        formData.put("TaskId", TaskId);

        Log.d(TAG, "TaskId: " + TaskId);

        return service.getTasksStatus(formData);
    }

    public Single<JsonElement> logOff() {
        HashMap<String, String> formData = new HashMap<>();
        formData.put("EmpId", userData.EmpId());

        formData.forEach((s, s2) -> Log.d(TAG, s+": "+s2));

        return service.logOff(formData);
    }
}
