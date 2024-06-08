package com.MeghaElectronicals.retrofit;

import android.content.Context;
import android.util.Log;

import com.MeghaElectronicals.common.MySharedPreference;
import com.MeghaElectronicals.modal.DepartmentModal;
import com.MeghaElectronicals.modal.EmployeesListModal;
import com.MeghaElectronicals.modal.LoginModal;
import com.MeghaElectronicals.modal.StatusModal;
import com.MeghaElectronicals.modal.TasksListModal;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class ServiceRepository {

    private static final String TAG = "ServiceRepository";
    MySharedPreference pref;
    LoginModal userData;
    Service service;

    public ServiceRepository(Context context) {
        pref = new MySharedPreference(context);
        service = RetrofitInstance.getService();
        userData = new LoginModal(pref.fetchLogin());
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

        Log.d(TAG, "OfficeId: " + DID + ", OfficeId: " + userData.OfficeId() + ", Role" + userData.Role());

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
}
