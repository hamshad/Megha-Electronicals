package com.MeghaElectronicals.modal;

//[
//    {
//        "TaskName": "add new task",
//        "Department": "Development",
//        "TaskId": 1,
//        "Description": "new task for testing",
//        "StartDate": "2024-06-14T18:20:00",
//        "EndDate": "2024-06-14T18:25:00",
//        "CompletionDate": null,
//        "CompletionDescription": null,
//        "Status": "In-Progress",
//        "ColorsName": "RED",
//        "EmployeName": "Employees",
//        "EmpId": "7f86810c-d90a-4f65-a98c-9a644f0f89d6",
//        "CreatedBy": "689f83ae-9db8-43b1-a3d9-ff4fd2ce047e",
//        "CreatedName": "Santosh Pawar"
//    }
//]

public record TasksListModal(String TaskName,
                             String Department,
                             int TaskId,
                             String Description,
                             String StartDate,
                             String EndDate,
                             String CompletionDate,
                             String CompletionDescription,
                             String Status,
                             String ColorsName,
                             String EmployeName,
                             String AssignedToId,
                             String CreatedBy,
                             String CreatedName) {
}
