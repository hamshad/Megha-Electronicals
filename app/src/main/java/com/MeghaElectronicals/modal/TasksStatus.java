package com.MeghaElectronicals.modal;

//{
//    "TaskName": "task",
//    "Status": "Finished",
//    "CompletionDescription": "new task finished",
//    "StartDate": "2024-06-13T17:10:00",
//    "EndDate": "2024-06-13T20:10:00",
//    "Description": "new task"
//}

public record TasksStatus(String TaskName, String Status, String CompletionDescription, String Description, String StartDate, String EndDate) {}
