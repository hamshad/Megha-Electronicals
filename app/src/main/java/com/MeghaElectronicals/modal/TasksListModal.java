package com.MeghaElectronicals.modal;

//[
//    {
//        "TaskName": "task",
//        "Description": "last one",
//        "StartDate": "2024-06-07T05:21:00",
//        "CompletionDate": null,
//        "Status": "InProgress",
//        "ColorsName": "RED",
//        "EmployeName": "Santosh Pawar"
//    },

public record TasksList(String TaskName,
                        String Description,
                        String StartDate,
                        String CompletionDate,
                        String Status,
                        String ColorsName,
                        String EmployeName) {
}
