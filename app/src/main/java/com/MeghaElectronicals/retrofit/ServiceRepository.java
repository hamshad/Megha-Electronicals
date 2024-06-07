package com.example.buzzertest.retrofit;

import android.content.Context;
import android.util.Log;

import com.example.buzzertest.MySharedPreference;
import com.example.buzzertest.modal.DepartmentModal;
import com.example.buzzertest.modal.EmployeesListModal;
import com.example.buzzertest.modal.LoginModal;
import com.example.buzzertest.modal.StatusModal;

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

        Log.d(TAG, "OfficeId: " + DID + ", OfficeId: " + userData.OfficeId());

        return service.getEmployeesList(formData);
    }
}
