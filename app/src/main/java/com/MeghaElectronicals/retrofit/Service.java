package com.example.buzzertest.retrofit;

import com.example.buzzertest.modal.DepartmentModal;
import com.example.buzzertest.modal.EmployeesListModal;
import com.example.buzzertest.modal.LoginModal;
import com.example.buzzertest.modal.StatusModal;

import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Service {

    @POST("api/User/LoginApi")
    Single<LoginModal> getLogin(@Body HashMap<String, Object> map);

    @POST("api/User/StatusList")
    Single<List<StatusModal>> getStatusList();

    @POST("api/User/DepartmentList")
    Single<List<DepartmentModal>> getDepartmentList();

    @FormUrlEncoded
    @POST("api/User/EmployeesList")
    Single<List<EmployeesListModal>> getEmployeesList(@FieldMap HashMap<String, String> map);
}
