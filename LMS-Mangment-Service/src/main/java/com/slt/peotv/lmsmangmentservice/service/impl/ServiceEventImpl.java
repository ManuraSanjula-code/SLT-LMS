package com.slt.peotv.lmsmangmentservice.service.impl;

import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveCategoryRemainingEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveCategoryTotalEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveTypeRemaining;
import com.slt.peotv.lmsmangmentservice.service.ServiceEvent;
import org.springframework.stereotype.Service;

@Service
public class ServiceEventImpl implements ServiceEvent {
    @Override
    public void userCreatedEvent() {

    }

    @Override
    public void saveUserLeaveCategoryRemaining(String cat_name, String user_id, String employee_id, Integer remainingLeaves, boolean isUpdate) {

    }

    @Override
    public UserLeaveCategoryRemainingEntity getUserLeaveCategoryRemaining(String cat_name, String user_id, String employee_id) {
        return null;
    }

    @Override
    public UserLeaveCategoryRemainingEntity getUserLeaveCategoryRemaining(String user_id, String employee_id) {
        return null;
    }

    @Override
    public void deleteUserLeaveCategoryRemaining(String publicId) {

    }

    @Override
    public void saveUserLeaveCategoryTotal(String cat_name, String user_id, String employee_id, Integer totalLeaves) {

    }

    @Override
    public UserLeaveCategoryTotalEntity getUserLeaveCategoryTotal(String user_id, String employee_id) {
        return null;
    }

    @Override
    public UserLeaveCategoryTotalEntity getUserLeaveCategoryTotal(String cat_name, String user_id, String employee_id) {
        return null;
    }

    @Override
    public void deleteUserLeaveCategoryTotal(String publicId) {

    }

    @Override
    public void saveUserLeaveTypeRemaining(String user_id, String employee_id, Integer remainingLeaves, String type_name, boolean isUpdate) {

    }

    @Override
    public UserLeaveTypeRemaining getUserLeaveTypeRemaining(String user_id, String employee_id) {
        return null;
    }

    @Override
    public UserLeaveTypeRemaining getUserLeaveTypeRemaining(String type_name, String user_id, String employee_id) {
        return null;
    }

    @Override
    public void deleteUserLeaveTypeRemaining(String publicId) {

    }

    @Override
    public void saveUserLeaveTypeTotal(String user_id, String employee_id, Integer totalLeaves, String type_name, boolean isUpdate) {

    }

    @Override
    public UserLeaveTypeRemaining getUserLeaveTypeTotal(String user_id, String employee_id) {
        return null;
    }

    @Override
    public UserLeaveTypeRemaining getUserLeaveTypeTotal(String type_name, String user_id, String employee_id) {
        return null;
    }

    @Override
    public void deleteUserLeaveTypeTotal(String publicId) {

    }
}
