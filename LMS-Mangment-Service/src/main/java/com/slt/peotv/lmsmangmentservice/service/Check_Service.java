package com.slt.peotv.lmsmangmentservice.service;

import com.slt.peotv.lmsmangmentservice.entity.Attendance.AttendanceEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import com.slt.peotv.lmsmangmentservice.entity.card.InOutEntity;
import com.slt.peotv.lmsmangmentservice.model.AbsenteeReq;
import com.slt.peotv.lmsmangmentservice.model.LeaveReq;
import com.slt.peotv.lmsmangmentservice.model.MovementReq;

import java.util.Date;
import java.util.List;

public interface Check_Service {

    public void requestMovement(MovementReq req, Date dueDate);
    public void processMovementBySup(String superId, String moveId);
    public void processMovementByHOD(String hodId, String moveId);
    public void processMovementParticularUserBySup(String superId, String userId);
    public void processMovementParticularUserByHOD(String hodId, String userId);
    public void processMovementParticularIdsBySup(String superId, List<String> ids);
    public void processMovementParticularIdsByHOD(String hodId, List<String> ids);

    public void main();
    public void prerequisite();

    public void reportAttendance(InOutEntity inOut,Boolean fullday, Boolean unAuthorized, Boolean unSuccessful,Boolean late, Boolean late_cover,Boolean half_day);
    public <T> void reportAttendance(Object obj,Boolean fullday, Boolean unAuthorized, Boolean unSuccessful,Boolean late, Boolean late_cover,Boolean half_day);

    public void reportAbsent(List<UserEntity> absentEmployeesToday);
    public void reportAbsent(AbsenteeReq req);

    public void requestALeave(LeaveReq req, String user,String employeeId);
    public void processLeaveBySup(String superId, String leaveId);
    public void processLeaveByHOD(String hodId, String leaveId);
    public void processLeaveParticularUserBySup(String superId, String userId);
    public void processLeaveParticularUserByHOD(String hodId, String userId);
    public void processLeaveParticularIdsBySup(String superId, List<String> ids);
    public void processLeaveParticularIdsByHOD(String hodId, List<String> ids);

    public void getAllTheInOutRecordsFromSLT();
}
