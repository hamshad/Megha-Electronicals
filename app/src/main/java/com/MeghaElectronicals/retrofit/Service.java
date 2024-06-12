package com.MeghaElectronicals.retrofit;

import com.MeghaElectronicals.modal.DepartmentModal;
import com.MeghaElectronicals.modal.EmployeesListModal;
import com.MeghaElectronicals.modal.LoginModal;
import com.MeghaElectronicals.modal.StatusModal;
import com.MeghaElectronicals.modal.TasksListModal;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
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

    @FormUrlEncoded
    @POST("api/User/AddTasks")
    Single<JsonElement> addTasks(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/User/TasksList")
    Single<List<TasksListModal>> getTasksList(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/User/TasksUpdate")
    Single<JsonElement> getTasksUpdate(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/User/Logoff")
    Single<JsonElement> logOff(@FieldMap HashMap<String, String> map);
}
