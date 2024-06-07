package com.example.buzzertest.modal;

import static com.example.buzzertest.MyFunctions.getValueFromJson;

import com.example.buzzertest.MyFunctions;

import org.json.JSONException;
import org.json.JSONObject;

public record LoginModal(String access_token, String token_type, String Email, String issued, String OfficeId, String Role, String FullName) {

    public LoginModal(String rawData) {
        this(
                getValueFromJson(rawData, "access_token"),
                getValueFromJson(rawData, "token_type"),
                getValueFromJson(rawData, "Email"),
                getValueFromJson(rawData, "issued"),
                getValueFromJson(rawData, "OfficeId"),
                getValueFromJson(rawData, "Role"),
                getValueFromJson(rawData, "FullName")
        );
    }

    public String toJsonString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", access_token);
            jsonObject.put("token_type", token_type);
            jsonObject.put("Email", Email);
            jsonObject.put("issued", issued);
            jsonObject.put("OfficeId", OfficeId);
            jsonObject.put("Role", Role);
            jsonObject.put("FullName", FullName);
        } catch (JSONException e) {
            e.fillInStackTrace();
        }
        return jsonObject.toString();
    }
}
