package com.slt.peotv.lmsmangmentservice.service;

import com.slt.peotv.lmsmangmentservice.entity.Absentee.AbsenteeEntity;
import com.slt.peotv.lmsmangmentservice.entity.Attendance.AttendanceEntity;
import com.slt.peotv.lmsmangmentservice.entity.Attendance.types.AttendanceTypeEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.LeaveEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveCategoryEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveTypeEntity;
import com.slt.peotv.lmsmangmentservice.entity.MovementsEntity;
import com.slt.peotv.lmsmangmentservice.entity.NoPayEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;

import java.util.List;

public interface LMS_Service {

    public List<AbsenteeEntity> getAllAbsentee();
    public AbsenteeEntity getOneAbsentee(String publicId, String useId, String employeeId);
    public void saveAbsentee(String userId,String employeeId, Boolean isHalfDay, Boolean swipeErr);
    public void deleteAbsentee(String publicId);

    public List<AttendanceEntity> getAllAttendance();
    public List<AttendanceEntity> getAttendanceByUserId(String userId);
    public AttendanceEntity getOneAttendanceByEmployeeId(String employeeId);

    public void createMovements(MovementsEntity entity);
    public List<MovementsEntity> getAllMovementByUser(UserEntity user);
    public List<MovementsEntity> getAllMovements();
    public MovementsEntity getMovement(String publicId);
    public void updateMovement(MovementsEntity entity, String publicId);
    public void deleteMovements(String publicId);

    public void createNoPay(NoPayEntity entity);
    public List<NoPayEntity> getAllNoPayByUser(UserEntity user);
    public List<NoPayEntity> getAllNoPays();
    public NoPayEntity getNoPay(String publicId);
    public void updateNoPay(NoPayEntity entity, String publicId);
    public void deleteNoPay(String publicId);

    public void saveLeave(LeaveEntity entity);
    public List<LeaveEntity> getAllLeaveByUserByPubicId(String user);
    public List<LeaveEntity> getAllLeaveByUserByEmployeeId(String user);
    public List<LeaveEntity> getAllLeaves();
    public LeaveEntity getOneLeave(String publicId);
    public void deleteLeave(String publicId);

    public void saveUser(UserEntity entity);
    public List<UserEntity> getAllUsers();
    public void updateUser(String user, UserEntity entity);
    public UserEntity getUserByPublicId(String user);
    public UserEntity getUserByEmployeeId(String user);
    public void deleteUser(String user);

    public void saveAttendanceType(String shortName);
    public AttendanceTypeEntity getAttendanceType(String shortName);
    public void updateAttendanceType(String publicId, String shortName);
    public void deleteAttendanceType(String shortName, String publicId);

    public void saveLeaveCategory(String name);
    public LeaveCategoryEntity getLeaveCategory(String name,String publicId);
    public void updateLeaveCategory(String old_name, String name,String publicId);
    public void deleteLeaveCategory(String name, String publicId);

    public void saveLeaveType(String name);
    public LeaveTypeEntity getLeaveType(String name,String publicId);
    public void updateLeaveType(String old_name,String name);
    public void deleteLeaveType(String name,String publicId);

}
