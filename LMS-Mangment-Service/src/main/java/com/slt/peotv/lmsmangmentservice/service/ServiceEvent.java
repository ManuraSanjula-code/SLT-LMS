package com.slt.peotv.lmsmangmentservice.service;

import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveCategoryRemainingEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveCategoryTotalEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveTypeRemaining;

public interface ServiceEvent {
    public void userCreatedEvent();

    public void saveUserLeaveCategoryRemaining(String cat_name, String user_id, String employee_id, Integer remainingLeaves, boolean isUpdate);
    public UserLeaveCategoryRemainingEntity getUserLeaveCategoryRemaining(String cat_name, String user_id, String employee_id);
    public UserLeaveCategoryRemainingEntity getUserLeaveCategoryRemaining(String user_id, String employee_id);
    public void deleteUserLeaveCategoryRemaining(String publicId);

    public void saveUserLeaveCategoryTotal(String cat_name, String user_id, String employee_id, Integer totalLeaves);
    public UserLeaveCategoryTotalEntity getUserLeaveCategoryTotal(String user_id, String employee_id);
    public UserLeaveCategoryTotalEntity getUserLeaveCategoryTotal(String cat_name, String user_id, String employee_id);
    public void deleteUserLeaveCategoryTotal(String publicId);


    public void saveUserLeaveTypeRemaining(String user_id, String employee_id, Integer remainingLeaves, String type_name, boolean isUpdate);
    public UserLeaveTypeRemaining getUserLeaveTypeRemaining(String user_id, String employee_id);
    public UserLeaveTypeRemaining getUserLeaveTypeRemaining(String type_name,String user_id, String employee_id);
    public void deleteUserLeaveTypeRemaining(String publicId);

    public void saveUserLeaveTypeTotal(String user_id, String employee_id, Integer totalLeaves, String type_name, boolean isUpdate);
    public UserLeaveTypeRemaining getUserLeaveTypeTotal(String user_id, String employee_id);
    public UserLeaveTypeRemaining getUserLeaveTypeTotal(String type_name, String user_id, String employee_id);
    public void deleteUserLeaveTypeTotal(String publicId);

}
