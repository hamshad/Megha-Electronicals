package com.gmhmccms.Retrofit;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gmhmccms.common.MySharedPreferences;
import com.gmhmccms.model.AddressInfoUpdate;
import com.gmhmccms.model.CityNameModal;
import com.gmhmccms.model.IncomeDetailsModal;
import com.gmhmccms.model.IncomeUpdateModal;
import com.gmhmccms.model.NextQuestion;
import com.gmhmccms.model.PaidFeesModel;
import com.gmhmccms.model.PreviousExams;
import com.gmhmccms.model.RemainingFeesModel;
import com.gmhmccms.model.Result;
import com.gmhmccms.model.SmsModel;
import com.gmhmccms.model.StateNameModal;
import com.gmhmccms.model.StdDocsModel;
import com.gmhmccms.model.StudentAddressInfoModel;
import com.gmhmccms.model.StudentInfoModel;
import com.gmhmccms.model.StudentListRequestModel;
import com.gmhmccms.model.StudentSubWiseModel;
import com.gmhmccms.model.SubNameCoutModel;
import com.gmhmccms.model.TakeExamModel;
import com.gmhmccms.model.TodayExamModel;
import com.gmhmccms.model.UpdateStudentInfoModal;
import com.gmhmccms.model.UserModel;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ServiceRepository {

    private static final String TAG = "ServiceRepository";
    MutableLiveData<Boolean> isError = new MutableLiveData<>();
    MySharedPreferences pref;

    public ServiceRepository(Context context) {
        pref = new MySharedPreferences(context);
    }

    public LiveData<UserModel> getLoginData(String email, String password) {

        MutableLiveData<UserModel> LoginData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        HashMap<String, Object> map = new HashMap<>();
        map.put("Email", email);
        map.put("Password", password);
        map.put("Token", pref.loadToken());
        Log.d("Token", pref.loadToken());

        Log.d(TAG, "Email: "+email+"\nPassword: "+password);

        service.getLogin(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull UserModel userModel) {
                        LoginData.postValue(userModel);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage(), e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "Complete Loading");
                    }
                });

        return LoginData;

    }

    public LiveData<ArrayList<PaidFeesModel>> getPaidFeesData() {

        MutableLiveData<ArrayList<PaidFeesModel>> PaidFeesData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        pref.loadLoginUserDetails();
        HashMap<String, String> map = new HashMap<>();
        map.put("Uid", pref.Uid);
        map.put("Id", pref.Id);

        service.getPaidFess(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<PaidFeesModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<PaidFeesModel> paidFeesModels) {
                        PaidFeesData.postValue(paidFeesModels);
                        if (pref.loadClassName().isEmpty())
                            pref.saveClassName(paidFeesModels.get(paidFeesModels.size() - 1).getClassName());
                        Log.d("onBindViewHolder", "Shared Preferences class name stored");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load PaidFees", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Loaded PaidFees");
                    }
                });

        return PaidFeesData;

    }

    public LiveData<ArrayList<RemainingFeesModel>> getRemainingFeesData() {

        MutableLiveData<ArrayList<RemainingFeesModel>> RemainingFeesData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        pref.loadLoginUserDetails();
        HashMap<String, String> map = new HashMap<>();
        map.put("Uid", pref.Uid);
        map.put("Id", pref.Id);

        service.getRemainingFees(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonElement>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull JsonElement jsonElement) {

                        ArrayList<RemainingFeesModel> list = new ArrayList<>();
                        try {
                            JSONArray array = new JSONArray(jsonElement.toString());
                            for (int i = 0; i < array.length(); i++) {
                                JSONArray array2 = new JSONArray(array.get(i).toString());
                                for (int j = 0; j < array2.length(); j++) {
//                                    JSONObject object = array2.getJSONObject(j);
//                                    Log.d("TAG", "onNext: "+object.toString());
//
//
////                                    list.add(model);
                                    JSONObject jsonObject = array2.getJSONObject(j);
                                    String ClassId = jsonObject.get("ClassId").toString();
                                    String ClassName = jsonObject.get("ClassName").toString();
                                    String FixedFee = jsonObject.get("FixedFee").toString();
                                    String BalanceFee = jsonObject.get("BalanceFee").toString();
                                    String Particular = jsonObject.get("Particular").toString();
                                    Log.d("TAG", "onNext: " + FixedFee);
                                    list.add(new RemainingFeesModel(ClassId, ClassName, FixedFee, BalanceFee, Particular));
                                }
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        RemainingFeesData.postValue(list);

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load RemainingFees", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Loaded RemainingFees");
                    }
                });

        return RemainingFeesData;

    }

    public LiveData<ArrayList<SubNameCoutModel>> getSubNameData() {

        MutableLiveData<ArrayList<SubNameCoutModel>> SubNameData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        pref.loadLoginUserDetails();
        HashMap<String, String> map = new HashMap<>();
        map.put("Uid", pref.Uid);
        map.put("Id", pref.Id);

        service.getSubNameCout(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<SubNameCoutModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<SubNameCoutModel> subNameCoutModels) {
                        SubNameData.postValue(subNameCoutModels);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load SubjectNames", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Loaded SubjectNames");
                    }
                });

        return SubNameData;

    }

    public LiveData<ArrayList<StudentSubWiseModel>> getStudentSubWise(String ClassId, String ClassMasterId, String SubjectId, String AttendanceStatus, String FromDate, String ToDate) {

        MutableLiveData<ArrayList<StudentSubWiseModel>> StudentSubWiseData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        pref.loadLoginUserDetails();
        HashMap<String, String> map = new HashMap<>();
        map.put("Uid", pref.Uid);
        map.put("Id", pref.Id);
        map.put("ClassId", ClassId);
        map.put("ClassMasterId", ClassMasterId);
        map.put("SubjectId", SubjectId);
        map.put("AttendanceStatus", AttendanceStatus);
        if (!FromDate.isEmpty() || !ToDate.isEmpty()) {
            map.put("FromDate", FromDate);
            map.put("ToDate", ToDate);
        }

        service.getStudentSubWise(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<StudentSubWiseModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<StudentSubWiseModel> studentSubWiseModels) {
                        StudentSubWiseData.postValue(studentSubWiseModels);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load StudentSubWiseData", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Loaded StudentSubWiseData");
                    }
                });

        return StudentSubWiseData;

    }

    public LiveData<Double> getProfileCompleted() {

        MutableLiveData<Double> ProfileCompleted = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        pref.loadLoginUserDetails();
        HashMap<String, String> map = new HashMap<>();
        map.put("Uid", pref.Uid);
        map.put("Id", pref.Id);

        service.getProfile(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonElement>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull JsonElement jsonElement) {
                        ProfileCompleted.postValue(jsonElement.getAsDouble());
                        Log.d("ProfileCompleted", "" + jsonElement.getAsDouble());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load ProfileCompleted", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Loaded ProfileCompleted");
                    }
                });

        return ProfileCompleted;
    }

    public LiveData<ArrayList<StdDocsModel>> getStdDocsData() {

        MutableLiveData<ArrayList<StdDocsModel>> StdDocsData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        pref.loadLoginUserDetails();
        HashMap<String, String> map = new HashMap<>();
        map.put("Uid", pref.Uid);
        map.put("StudentId", pref.Id);

        service.getStdDocs(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<StdDocsModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<StdDocsModel> stdDocsModels) {
                        StdDocsData.postValue(stdDocsModels);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load StdDocsData", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "load StdDocsData");
                    }
                });

        return StdDocsData;
    }

    public LiveData<ArrayList<SmsModel>> getSmsData() {

        MutableLiveData<ArrayList<SmsModel>> SmsData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        pref.loadLoginUserDetails();
        HashMap<String, String> map = new HashMap<>();
        map.put("Uid", pref.Uid);
        map.put("StudentId", pref.Id);

        service.getSms(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<SmsModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<SmsModel> smsModels) {
                        SmsData.postValue(smsModels);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load SmsData", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "loaded SmsData");
                    }
                });

        return SmsData;
    }

    public LiveData<String> changeStudentPasswordData(String password, String confirmPassword) {

        MutableLiveData<String> ChangeStudentPasswordData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        pref.loadLoginUserDetails();
        if (password == null || password.isEmpty()) {
            Log.d("ChangePasswordData", "empty password");
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("Uid", pref.Uid);
        map.put("StudentId", pref.Id);
        map.put("Password", password);
        map.put("ConfirmPassword", confirmPassword);

        service.changeStudentPassword(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        ChangeStudentPasswordData.postValue(s);
                        Log.d("ChangePasswordData", s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load ChangePasswordData", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "loaded ChangePasswordData");
                    }
                });

        return ChangeStudentPasswordData;
    }

    public LiveData<StudentInfoModel> getStudentInfoData() {

        MutableLiveData<StudentInfoModel> StudentInfoData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(true);
        pref.loadLoginUserDetails();

        HashMap<String, String> map = new HashMap<>();
        map.put("Uid", pref.Uid);
        map.put("Id", pref.Id);

        service.getStudentInfo(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<StudentInfoModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull StudentInfoModel studentInfoModel) {

//                        StudentInfoDatabase db = Room.databaseBuilder(application, StudentInfoDatabase.class, application.getString(R.string.app_name))
//                                .build();
//
//                        if (db.getCachedStudentInfo().getStudentInfoData(1) != null)
//                            db.getCachedStudentInfo().deleteStudentInfoCache();
//
//                        StudentInfoEntity entity = new StudentInfoEntity(studentInfoModel);
//                        entity.id = 1;
//                        db.getCachedStudentInfo().insert(entity);
//                        Log.d("StudentInfoCached", "Data saved in local");
//                        db.close();

                        StudentInfoData.postValue(studentInfoModel);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load StudentInfoData", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "loaded StudentInfoData");
                    }
                });

        return StudentInfoData;
    }

    public LiveData<JsonElement> getUpdateStudentInfoData(@androidx.annotation.NonNull UpdateStudentInfoModal modal) {

        MutableLiveData<JsonElement> UpdateStudentInfoData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(true);
        pref.loadLoginUserDetails();

        HashMap<String, String> map = new HashMap<>();
        map.put("Uid", pref.Uid);
        map.put("Id", pref.Id);
        map.put("StudentMobile", modal.getStudentMobile());
        map.put("ParentMobile", modal.getParentMobile());
        map.put("DateOfBirth", modal.getDateOfBirth());
        map.put("Gender", modal.getGender());
        map.put("BloodGroup", modal.getBloodGroup());
        map.put("Adhaar", modal.getAdhaar());
        map.put("Email", modal.getEmail());
        map.put("StudentFirstName", modal.getStudentFirstName());
        map.put("StudentMiddleName", modal.getStudentMiddleName());
        map.put("StudentLastName", modal.getStudentLastName());
        map.put("StudentFirstNameM", modal.getStudentFirstNameM());
        map.put("StudentMiddleNameM", modal.getStudentMiddleNameM());
        map.put("StudentLastNameM", modal.getStudentLastNameM());
        map.put("FatherFirstName", modal.getFatherFirstName());
        map.put("FatherMiddleName", modal.getFatherMiddleName());
        map.put("FatherLastName", modal.getFatherLastName());
        map.put("FatherFirstNameM", modal.getFatherFirstNameM());
        map.put("FatherMiddleNameM", modal.getFatherMiddleNameM());
        map.put("FatherLastNameM", modal.getFatherLastNameM());
        map.put("MotherFirstName", modal.getMotherFirstName());
        map.put("MotherMiddleName", modal.getMotherMiddleName());
        map.put("MotherLastName", modal.getMotherLastName());
        map.put("MotherFirstNameM", modal.getMotherFirstNameM());
        map.put("MotherMiddleNameM", modal.getMotherMiddleNameM());
        map.put("MotherLastNameM", modal.getMotherLastNameM());

        Log.d(TAG, modal.toString());

        service.getUpdateStudentInfo(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonElement>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull JsonElement res) {
                        UpdateStudentInfoData.postValue(res);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load UpdateStudentInfoData", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "loaded UpdateStudentInfoData");
                    }
                });

        return UpdateStudentInfoData;
    }

    public LiveData<ArrayList<StateNameModal>> getStateNameModalData() {

        MutableLiveData<ArrayList<StateNameModal>> StateNameModalData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(true);
        pref.loadLoginUserDetails();

        HashMap<String, String> map = new HashMap<>();
        map.put("Uid", pref.Uid);
        map.put("Id", pref.Id);

        service.getStateNameList(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<StateNameModal>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<StateNameModal> res) {

                        StateNameModalData.postValue(res);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load StateNameModalData", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "loaded StateNameModalData");
                    }
                });

        return StateNameModalData;
    }

    public LiveData<ArrayList<CityNameModal>> getCityNameModalData(String stateId) {

        MutableLiveData<ArrayList<CityNameModal>> CityNameModalData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(true);
        pref.loadLoginUserDetails();

        HashMap<String, String> map = new HashMap<>();
        map.put("Uid", pref.Uid);
        map.put("Id", pref.Id);
        map.put("StateId", stateId);

        service.getCityNameList(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<CityNameModal>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<CityNameModal> res) {

                        CityNameModalData.postValue(res);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load CityNameModalData", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "loaded CityNameModalData");
                    }
                });

        return CityNameModalData;
    }

    public LiveData<ArrayList<StudentAddressInfoModel>> getAddressInfoData(@NonNull String AddressType) {

        MutableLiveData<ArrayList<StudentAddressInfoModel>> AddressInfoData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(true);
        pref.loadLoginUserDetails();

        HashMap<String, String> map = new HashMap<>();
        map.put("Uid", pref.Uid);
        map.put("Id", pref.Id);
        map.put("AddressType", AddressType);

        service.getStudentAddressInfo(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<StudentAddressInfoModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<StudentAddressInfoModel> studentInfoModel) {

                        AddressInfoData.postValue(studentInfoModel);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load AddressInfoData "+AddressType, e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "loaded AddressInfoData "+AddressType);
                    }
                });

        return AddressInfoData;
    }

    public LiveData<JsonElement> getAddressInfoUpdate(AddressInfoUpdate modal) {

        MutableLiveData<JsonElement> AddressInfoUpdateData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(true);
        pref.loadLoginUserDetails();

        HashMap<String, String> map = new HashMap<>();
        map.put("Uid", pref.Uid);
        map.put("StudentId", pref.Id);
        map.put("AddressType", modal.getAddressType());
        map.put("StateId", modal.getStateId());
        map.put("CityId", modal.getCityId());
        map.put("Address", modal.getAddress());
        map.put("PinCode", modal.getPinCode());
        map.put("Tehsil", modal.getTehsil());
        map.put("Id", modal.getId());

        Log.d(TAG, "getAddressInfoUpdate: "+modal);

        service.getAddressInfoUpDate(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonElement>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull JsonElement res) {
                        AddressInfoUpdateData.postValue(res);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't update AddressInfoUpdateData " + modal.getAddressType(), e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Updated AddressInfoUpdateData " + modal.getAddressType());
                    }
                });

        return AddressInfoUpdateData;
    }

    public LiveData<ArrayList<IncomeDetailsModal>> getIncomeDetailsData() {

        MutableLiveData<ArrayList<IncomeDetailsModal>> IncomeDetailsData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(true);
        pref.loadLoginUserDetails();

        HashMap<String, String> map = new HashMap<>();
        map.put("Uid", pref.Uid);
        map.put("StudentId", pref.Id);

        service.getIncomeDetails(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<IncomeDetailsModal>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<IncomeDetailsModal> studentInfoModel) {

                        IncomeDetailsData.postValue(studentInfoModel);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load IncomeDetailsData", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "loaded IncomeDetailsData");
                    }
                });

        return IncomeDetailsData;
    }

    public LiveData<JsonElement> getIncomeUpdate(IncomeUpdateModal modal) {

        MutableLiveData<JsonElement> IncomeUpdateData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(true);
        pref.loadLoginUserDetails();

        HashMap<String, String> map = new HashMap<>();
        map.put("Uid", pref.Uid);
        map.put("StudentId", pref.Id);
        map.put("IncomeId", modal.getIncomeId());
        map.put("HaveCertificate", modal.getHaveCertificate());
        map.put("Amount", modal.getAmount());
        map.put("CertNo", modal.getCertNo());
        map.put("IssudeDate", modal.getIssudeDate());
        map.put("VlidUpTo", modal.getVlidUpTo());
        map.put("PanNo", modal.getPanNo());
        map.put("AdharNo", modal.getAdharNo());

        service.getIncomeUpdate(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonElement>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull JsonElement res) {
                        IncomeUpdateData.postValue(res);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't Update IncomeUpdateData ", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Updated IncomeUpdateData ");
                    }
                });

        return IncomeUpdateData;
    }

    public LiveData<JsonElement> getDocumentUpdateData(String Description, String DocTypeId, String SDId, RequestBody requestFile, String fileName) {

        MutableLiveData<JsonElement> DocumentUpdateData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        pref.loadLoginUserDetails();

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("Uid", pref.Uid);
        builder.addFormDataPart("StudentId", pref.Id);
        builder.addFormDataPart("Description", Description);
        builder.addFormDataPart("DocTypeId", DocTypeId);
        builder.addFormDataPart("SDId", SDId);

        if (requestFile != null) {
            builder.addFormDataPart("file", fileName, requestFile);
        }

        service.getDocumentUpdate(builder.build())
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonElement>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull JsonElement s) {
                        DocumentUpdateData.postValue(s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load DocumentUpdateData", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Loaded DocumentUpdateData");
                    }
                });

        return DocumentUpdateData;
    }

    public LiveData<String> getLogoff() {

        MutableLiveData<String> logoff = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(true);
        pref.loadLoginUserDetails();

        HashMap<String, String> map = new HashMap<>();
        map.put("Uid", pref.Uid);
        map.put("Id", pref.Id);

        service.logoff(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(true);
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        logoff.postValue(s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load Logoff", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "loaded logoff");
                    }
                });

        return logoff;
    }

    public LiveData<ArrayList<StudentListRequestModel>> getStudentListRequestData() {

        MutableLiveData<ArrayList<StudentListRequestModel>> StudentListRequestData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        pref.loadLoginUserDetails();
        HashMap<String, String> map = new HashMap<>();
        map.put("Uid", pref.Uid);
        map.put("Id", pref.Id);

        service.getStudentRequestList(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<StudentListRequestModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<StudentListRequestModel> studentListRequestModels) {
                        StudentListRequestData.postValue(studentListRequestModels);
                        Log.d("StudentListRequestData", "onNext: " + studentListRequestModels.size());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load StudentListRequestData", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Loaded StudentListRequestData");
                    }
                });

        return StudentListRequestData;
    }

    public LiveData<String> sendStudentRequest(String reason, String request, RequestBody requestFile, String fileName) {

        MutableLiveData<String> StudentRequest = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        pref.loadLoginUserDetails();
//        HashMap<String, RequestBody> map = new HashMap<>();
//        map.put("Uid", RequestBody.create(MediaType.get("text/plain"), pref.Uid));
//        map.put("ClassId", RequestBody.create(MediaType.get("text/plain"), pref.ClassId));
//        map.put("Id", RequestBody.create(MediaType.get("text/plain"), pref.Id));
//        map.put("Reason", RequestBody.create(MediaType.get("text/plain"), reason));
//        map.put("RequestFor", RequestBody.create(MediaType.get("text/plain"), request));

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("Uid", pref.Uid);
        builder.addFormDataPart("ClassId", pref.ClassId);
        builder.addFormDataPart("Id", pref.Id);
        builder.addFormDataPart("Reason", reason);
        builder.addFormDataPart("RequestFor", request);

        if (requestFile != null) {
//            RequestBody fileRequestBody = RequestBody.create(MediaType.get("*/*"), file);
////            map.put("file\"; filename=\"" + file.getName() + "\"", fileRequestBody);
            builder.addFormDataPart("file", fileName, requestFile);
        }

        service.sendStudentRequest(builder.build())
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        StudentRequest.postValue(s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load StudentRequest", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Loaded StudentRequest");
                    }
                });

        return StudentRequest;
    }

    public LiveData<ArrayList<TodayExamModel>> getTodayExamData() {
        MutableLiveData<ArrayList<TodayExamModel>> TodayExamData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        pref.loadLoginUserDetails();
        HashMap<String, String> map = new HashMap<>();
        map.put("StudentId", pref.Id);
        map.put("Uid", pref.Uid);

        service.getTodayExam(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<TodayExamModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<TodayExamModel> todayExamModels) {
                        TodayExamData.postValue(todayExamModels);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load TodayExam", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Loaded TodayExam");
                    }
                });

        return TodayExamData;
    }

    public LiveData<ArrayList<TakeExamModel>> getTakeExamData(String PaperId) {
        MutableLiveData<ArrayList<TakeExamModel>> TakeExamData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        pref.loadLoginUserDetails();
        HashMap<String, String> map = new HashMap<>();
        map.put("StudentId", pref.Id);
        map.put("PaperId", PaperId);
        map.put("Uid", pref.Uid);
        Log.d(TAG, PaperId);

        service.getTakeExam(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<TakeExamModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<TakeExamModel> takeExamModels) {
                        TakeExamData.postValue(takeExamModels);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load TakeExam", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Loaded TakeExam");
                    }
                });

        return TakeExamData;
    }

    public LiveData<NextQuestion> getNextQuestionData(String PaperId, String qno, String crId) {
        MutableLiveData<NextQuestion> NextQuestionData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        pref.loadLoginUserDetails();
        HashMap<String, String> map = new HashMap<>();
        map.put("PaperId", PaperId);
        map.put("qno", qno);
        map.put("StudentId", pref.Id);
        map.put("crId", crId);
        map.put("Uid", pref.Uid);

        service.getNextQuestion(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NextQuestion>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull NextQuestion nextQuestion) {
                        NextQuestionData.postValue(nextQuestion);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load NextQuestionData", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Loaded NextQuestionData");
                    }
                });

        return NextQuestionData;
    }

    public LiveData<JSONObject> getSaveData(String QuestionId, String SelectedAnswer, String PaperId, String StudentQuestionId) {
        MutableLiveData<JSONObject> SaveData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        pref.loadLoginUserDetails();
        HashMap<String, String> map = new HashMap<>();
        map.put("QuestionId", QuestionId);
        map.put("SelectedAnswer", SelectedAnswer);
        map.put("PaperId", PaperId);
        map.put("StudentQuestionId", StudentQuestionId);
        map.put("StudentId", pref.Id);
        map.put("Uid", pref.Uid);

        service.getSave(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull JSONObject jsonObject) {
                        Log.d(TAG, "SaveData: " + jsonObject);
                        SaveData.postValue(jsonObject);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load SaveData", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Loaded SaveData");
                    }
                });

        return SaveData;
    }

    public LiveData<JSONObject> getFinalSubmitData(String QuestionId, String SelectedAnswer, String PaperId, String StudentQuestionId) {
        MutableLiveData<JSONObject> FinalSubmitData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        pref.loadLoginUserDetails();
        HashMap<String, String> map = new HashMap<>();
        map.put("QuestionId", QuestionId);
        map.put("SelectedAnswer", SelectedAnswer);
        map.put("PaperId", PaperId);
        map.put("StudentQuestionId", StudentQuestionId);
        map.put("StudentId", pref.Id);
        map.put("Uid", pref.Uid);

        service.getFinalSubmit(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull JSONObject jsonObject) {
                        Log.d(TAG, "FinalSubmitData: " + jsonObject);
                        FinalSubmitData.postValue(jsonObject);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load FinalSubmitData", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Loaded FinalSubmitData");
                    }
                });

        return FinalSubmitData;
    }

    public LiveData<Result> getResultData(String PaperId) {
        MutableLiveData<Result> ResultData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        pref.loadLoginUserDetails();
        HashMap<String, String> map = new HashMap<>();
        map.put("StudentId", pref.Id);
        map.put("Uid", pref.Uid);
        map.put("PaperId", PaperId);
        Log.d(TAG, PaperId);

        service.getResult(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Result>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull Result results) {
                        ResultData.postValue(results);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load ResultData", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Loaded ResultData");
                    }
                });

        return ResultData;
    }

    public LiveData<ArrayList<PreviousExams>> getPreviousExamsData() {
        MutableLiveData<ArrayList<PreviousExams>> PreviousExamsData = new MutableLiveData<>();

        Service service = RetrofitInstance.getService(false);
        pref.loadLoginUserDetails();
        HashMap<String, String> map = new HashMap<>();
        map.put("StudentId", pref.Id);
        map.put("Uid", pref.Uid);

        service.getPreviousExams(map)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<PreviousExams>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isError.setValue(false);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<PreviousExams> previousExamModels) {
                        PreviousExamsData.postValue(previousExamModels);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Couldn't load PreviousExamsData", e);
                        isError.postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Loaded PreviousExamsData");
                    }
                });

        return PreviousExamsData;
    }

    public MutableLiveData<Boolean> isError() {
        return isError;
    }
}
