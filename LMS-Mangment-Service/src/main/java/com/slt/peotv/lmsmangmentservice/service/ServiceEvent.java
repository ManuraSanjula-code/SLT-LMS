package com.slt.peotv.lmsmangmentservice.service;

import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveCategoryRemainingEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveCategoryTotalEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveTypeRemaining;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveTypeTotalEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.basic.AuthorityEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.basic.RoleEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.company.ProfilesEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.company.SectionEntity;
import com.slt.peotv.lmsmangmentservice.model.ProfileReq;

import java.util.Collection;
import java.util.List;

public interface ServiceEvent {
    public void userCreatedEvent();

    public void saveUserLeaveCategoryRemaining(String cat_name, String user_id, String employee_id, Integer remainingLeaves);
    public UserLeaveCategoryRemainingEntity getUserLeaveCategoryRemaining(String cat_name, String user_id, String employee_id);
    public List<UserLeaveCategoryRemainingEntity> getUserLeaveCategoryRemaining(String user_id, String employee_id);
    public void deleteUserLeaveCategoryRemaining(String publicId);

    public void saveUserLeaveCategoryTotal(String cat_name, String user_id, String employee_id, Integer totalLeaves);
    public List<UserLeaveCategoryTotalEntity> getUserLeaveCategoryTotal(String user_id, String employee_id); // @@@
    public List<UserLeaveCategoryTotalEntity> getUserLeaveCategoryTotal(UserEntity user);
    public UserLeaveCategoryTotalEntity getUserLeaveCategoryTotal(String cat_name, String user_id, String employee_id);
    public void deleteUserLeaveCategoryTotal(String publicId);


    public void saveUserLeaveTypeRemaining(String user_id, String employee_id, Integer remainingLeaves, String type_name);
    public List<UserLeaveTypeRemaining> getUserLeaveTypeRemaining(String user_id, String employee_id); // @@@
    public List<UserLeaveTypeRemaining> getUserLeaveTypeRemaining(UserEntity user); // @@@
    public UserLeaveTypeRemaining getUserLeaveTypeRemaining(String type_name,String user_id, String employee_id);
    public void deleteUserLeaveTypeRemaining(String publicId);

    public void saveUserLeaveTypeTotal(String user_id, String employee_id, Integer totalLeaves, String type_name);
    public UserLeaveTypeTotalEntity getUserLeaveTypeTotal(String user_id, String employee_id);
    public UserLeaveTypeTotalEntity getUserLeaveTypeTotal(String type_name, String user_id, String employee_id);
    public void deleteUserLeaveTypeTotal(String publicId);

    ProfilesEntity createProfiles(String name, ProfileReq req);
    SectionEntity createSection(String name);
    SectionEntity getSection(String name);
    ProfilesEntity getProfiles(String name);

    AuthorityEntity createAuthority(String name);
    RoleEntity createRole(String name, Collection<AuthorityEntity> authorities);
}
