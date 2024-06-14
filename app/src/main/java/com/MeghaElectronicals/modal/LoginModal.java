package com.MeghaElectronicals.modal;

//{
//    "access_token": "Kw0XrR-FR9ppSY5KMJlJ6KVjNohCQ9Z-JtH0hXlCJQ2iNfdtso4HMtp5koo5-ZrWZGwAEVYY8Cw5tjdMNpiW4DFntOOnyqf_eSWhUqQUIqJwSZKO_JYMNiaLA1sleP4FPuaYhASU168C3vwtZTu_VNM893Klfnz7zW1t06p-AIsU1b4DGMrUlv6zwkxeJUDf5LWajOokmoroGcXUxapmUkPVmWPgU4wUBo8e-0ekESDPV4cp_giFmuL-qW59WgWDpZFzZuEacY3WR4cLr5d2aK9s_jPyH--YX6X5SLfYChP4XhgiQCC-W9ptT02K8IXfQ7VWoITbjFz8Bejk0n4NR0fNO6L8XL4SE16wHpTUx7MSR_4Sx6qJCUhFliKtOKNR7vhu_QEeZr61YZip2g8G9MnFbkpt5lzNVgLoQQz5BhyXGpUt5ZscaqdtLG00FAhBzrWy_VtUTBXjUJtAgDhpuuz3bGvwjTZkhrBbF2JS7dxdIKOklw6wYT1_MsL-Ti87t_3k6U-Qmww5iBe9FCfVng",
//    "token_type": "bearer",
//    "Email": "meghaelectricals@gmail.com",
//    "issued": "07-06-2024 11:33:07",
//    "AccessTokenExpire": "21-06-2024 11:33:07",
//    "OfficeId": "b9e17060-c652-4a4c-936e-a5d88c7add91",
//    "EmpId": "689f83ae-9db8-43b1-a3d9-ff4fd2ce047e",
//    "Role": "Director",
//    "FullName": "Santosh Pawar"
//}

public record LoginModal(String access_token, String token_type, String Email, String issued, String AccessTokenExpire, String OfficeId, String EmpId, String Role, String FullName) {

//    public LoginModal(String rawData) {
//        this(
//                getValueFromJson(rawData, "access_token"),
//                getValueFromJson(rawData, "token_type"),
//                getValueFromJson(rawData, "Email"),
//                getValueFromJson(rawData, "issued"),
//                getValueFromJson(rawData, "AccessTokenExpire"),
//                getValueFromJson(rawData, "OfficeId"),
//                getValueFromJson(rawData, "EmpId"),
//                getValueFromJson(rawData, "Role"),
//                getValueFromJson(rawData, "FullName")
//        );
//    }
//
//    public String toJsonString() {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("access_token", access_token);
//            jsonObject.put("token_type", token_type);
//            jsonObject.put("Email", Email);
//            jsonObject.put("issued", issued);
//            jsonObject.put("AccessTokenExpire", AccessTokenExpire);
//            jsonObject.put("OfficeId", OfficeId);
//            jsonObject.put("EmpId", EmpId);
//            jsonObject.put("Role", Role);
//            jsonObject.put("FullName", FullName);
//        } catch (JSONException e) {
//            e.fillInStackTrace();
//        }
//        return jsonObject.toString();
//    }
}
