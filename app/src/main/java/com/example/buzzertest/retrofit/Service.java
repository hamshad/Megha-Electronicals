package com.gmhmccms.Retrofit;

import com.gmhmccms.model.CityNameModal;
import com.gmhmccms.model.IncomeDetailsModal;
import com.gmhmccms.model.NextQuestion;
import com.gmhmccms.model.PaidFeesModel;
import com.gmhmccms.model.PreviousExams;
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
import com.gmhmccms.model.UserModel;
import com.google.gson.JsonElement;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Service {

    @POST("api/User/Login/")
    Observable<UserModel> getLogin(@Body HashMap<String, Object> map);

    @POST("api/User/RemainingFees/")
    @FormUrlEncoded
    Observable<JsonElement> getRemainingFees(@FieldMap HashMap<String, String> map);

    @POST("api/User/PaidFees/")
    @FormUrlEncoded
    Observable<ArrayList<PaidFeesModel>> getPaidFess(@FieldMap HashMap<String, String> map);

    @POST("api/User/SubNameCout/")
    @FormUrlEncoded
    Observable<ArrayList<SubNameCoutModel>> getSubNameCout(@FieldMap HashMap<String, String> map);

    @POST("api/User/StudentSubWise")
    @FormUrlEncoded
    Observable<ArrayList<StudentSubWiseModel>> getStudentSubWise(@FieldMap HashMap<String, String> map);

    @POST("api/User/Profile")
    @FormUrlEncoded
    Observable<JsonElement> getProfile(@FieldMap HashMap<String, String> map);

    @POST("api/User/StdDocs")
    @FormUrlEncoded
    Observable<ArrayList<StdDocsModel>> getStdDocs(@FieldMap HashMap<String, String> map);

    @POST("api/User/Sms")
    @FormUrlEncoded
    Observable<ArrayList<SmsModel>> getSms(@FieldMap HashMap<String, String> map);

    @POST("api/User/ChangeStudentPassword")
    @FormUrlEncoded
    Observable<String> changeStudentPassword(@FieldMap HashMap<String, String> map);



    //---------------------------- INFO -------------------------------------------

    @POST("api/User/StudentInfo")
    @FormUrlEncoded
    Observable<StudentInfoModel> getStudentInfo(@FieldMap HashMap<String, String> map);

    @POST("api/User/UpdateStudentInfo")
    @FormUrlEncoded
    Observable<JsonElement> getUpdateStudentInfo(@FieldMap HashMap<String, String> map);

    @POST("api/User/StudentAddressInfo")
    @FormUrlEncoded
    Observable<ArrayList<StudentAddressInfoModel>> getStudentAddressInfo(@FieldMap HashMap<String, String> map);

    @POST("api/User/AddressInfoUpdate")
    @FormUrlEncoded
    Observable<JsonElement> getAddressInfoUpDate(@FieldMap HashMap<String, String> map);

    @POST("api/User/StateNameList")
    @FormUrlEncoded
    Observable<ArrayList<StateNameModal>> getStateNameList(@FieldMap HashMap<String, String> map);

    @POST("api/User/CityNameList")
    @FormUrlEncoded
    Observable<ArrayList<CityNameModal>> getCityNameList(@FieldMap HashMap<String, String> map);

    @POST("api/User/IncomeDetails")
    @FormUrlEncoded
    Observable<ArrayList<IncomeDetailsModal>> getIncomeDetails(@FieldMap HashMap<String, String> map);

    @POST("api/User/IncomeUpdate")
    @FormUrlEncoded
    Observable<JsonElement> getIncomeUpdate(@FieldMap HashMap<String, String> map);

    @POST("api/User/DocumentUpdate")
    Observable<JsonElement> getDocumentUpdate(@Body RequestBody requestBody);


    //----------------------------------------------------------------------------




    @POST("api/User/Logoff")
    @FormUrlEncoded
    Observable<String> logoff(@FieldMap HashMap<String, String> map);

    //    @Multipart
    @POST("api/User/StudentRequest")
    Observable<String> sendStudentRequest(@Body RequestBody requestBody);
//    Observable<String> sendStudentRequest(@PartMap HashMap<String, RequestBody> map);

    @POST("api/User/StudentRequestList")
    @FormUrlEncoded
    Observable<ArrayList<StudentListRequestModel>> getStudentRequestList(@FieldMap HashMap<String, String> map);


    //------------------- Exams Api ------------------------------------------------
    @POST("api/OnlineExam/TodayExam")
    @FormUrlEncoded
    Observable<ArrayList<TodayExamModel>> getTodayExam(@FieldMap HashMap<String, String> map);

    @POST("api/OnlineExam/TakeExam")
    @FormUrlEncoded
    Observable<ArrayList<TakeExamModel>> getTakeExam(@FieldMap HashMap<String, String> map);

    @POST("api/OnlineExam/NextQuestion")
    @FormUrlEncoded
    Observable<NextQuestion> getNextQuestion(@FieldMap HashMap<String, String> map);

    @POST("api/OnlineExam/Save")
    @FormUrlEncoded
    Observable<JSONObject> getSave(@FieldMap HashMap<String, String> map);

    @POST("api/OnlineExam/FinalSubmit")
    @FormUrlEncoded
    Observable<JSONObject> getFinalSubmit(@FieldMap HashMap<String, String> map);

    @POST("api/OnlineExam/Result")
    @FormUrlEncoded
    Observable<Result> getResult(@FieldMap HashMap<String, String> map);

    @POST("api/OnlineExam/PreviousExams")
    @FormUrlEncoded
    Observable<ArrayList<PreviousExams>> getPreviousExams(@FieldMap HashMap<String, String> map);
    //--------------------------------------------------------------------

}
