package com.MeghaElectronicals.modal;

//{
//    "TaskName": "task",
//    "Status": "Finished",
//    "CompletionDescription": "new task finished",
//    "StartDate": "2024-06-13T17:10:00",
//    "EndDate": "2024-06-13T20:10:00",
//    "Description": "new task"
//    "CreatedBy": "689f83ae-9db8-43b1-a3d9-ff4fd2ce047e"
//}

public record TasksStatus(String TaskName, String Status, String CompletionDescription, String Description, String StartDate, String EndDate, String CreatedBy) {}
