package com.MeghaElectronicals.modal;

//[
//    {
//        "TaskName": "Name of a Task",
//        "Description": "Description of the Task",
//        "StartDate": "2024-06-08T05:34:00",
//        "EndDate": "2024-06-08T08:34:00",
//        "Status": "InProgress",
//        "ColorsName": "RED",
//        "EmployeName": "Santosh Pawar"
//    }
//[

public record TasksListModal(String TaskName,
                             String Department,
                             String TaskId,
                             String Description,
                             String StartDate,
                             String EndDate,
                             String Status,
                             String ColorsName,
                             String EmployeName) {
}
