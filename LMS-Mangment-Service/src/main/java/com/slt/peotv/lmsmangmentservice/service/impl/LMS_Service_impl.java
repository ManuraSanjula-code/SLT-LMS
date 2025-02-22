package com.slt.peotv.lmsmangmentservice.service.impl;

import com.slt.peotv.lmsmangmentservice.entity.Absentee.AbsenteeEntity;
import com.slt.peotv.lmsmangmentservice.entity.Attendance.AttendanceEntity;
import com.slt.peotv.lmsmangmentservice.entity.Attendance.types.AttendanceTypeEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.LeaveEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveCategoryEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveTypeEntity;
import com.slt.peotv.lmsmangmentservice.entity.MovementsEntity;
import com.slt.peotv.lmsmangmentservice.entity.NoPayEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import com.slt.peotv.lmsmangmentservice.repository.*;
import com.slt.peotv.lmsmangmentservice.repository.archive.InOutRepo_;
import com.slt.peotv.lmsmangmentservice.service.LMS_Service;
import com.slt.peotv.lmsmangmentservice.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LMS_Service_impl implements LMS_Service {

    @Autowired
    private AbsenteeRepo absenteeRepo;
    @Autowired
    private AttendanceRepo attendanceRepo;
    @Autowired
    private AttendanceTypeRepo  attendanceTypeRepo;
    @Autowired
    private AuthorityRepo authorityRepo;
    @Autowired
    private GroupRepo groupRepo;
    @Autowired
    private LeaveCategoryRepo leaveCategoryRepo;
    @Autowired
    private LeaveRepo leaveRepo;
    @Autowired
    private LeaveTypeRepo leaveTypeRepo;
    @Autowired
    private MovementsRepo movementsRepo;
    @Autowired
    private NoPayRepo noPayRepo;
    @Autowired
    private ProfilesRepo profilesRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private SectionRepo sectionRepo;
    @Autowired
    private UserLeaveCategoryRemainingRepo userLeaveCategoryRemainingRepo;
    @Autowired
    private UserLeaveCategoryTotalRepo userLeaveCategoryTotalRepo;
    @Autowired
    private UserLeaveTypeTotalRepo userLeaveTypeTotalRepo;
    @Autowired
    private UserLeaveTypeRemainingRepo userLeaveTypeRemainingRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private InOutRepo_ inOutRepo_;
    @Autowired
    private Utils utils;

    @Override
    public List<AbsenteeEntity> getAllAbsentee() {
        return List.of();
    }

    @Override
    public AbsenteeEntity getOneAbsentee(String publicId, String useId, String employeeId) {
        return null;
    }

    @Override
    public void saveAbsentee(String userId, String employeeId, Boolean isHalfDay, Boolean swipeErr) {

    }

    @Override
    public void deleteAbsentee(String publicId) {

    }

    @Override
    public List<AttendanceEntity> getAllAttendance() {
        return List.of();
    }

    @Override
    public List<AttendanceEntity> getAttendanceByUserId(String userId) {
        return List.of();
    }

    @Override
    public AttendanceEntity getOneAttendanceByEmployeeId(String employeeId) {
        return null;
    }

    @Override
    public void createMovements(MovementsEntity entity) {

    }

    @Override
    public List<MovementsEntity> getAllMovementByUser(UserEntity user) {
        return List.of();
    }

    @Override
    public List<MovementsEntity> getAllMovements() {
        return List.of();
    }

    @Override
    public MovementsEntity getMovement(String publicId) {
        return null;
    }

    @Override
    public void updateMovement(MovementsEntity entity, String publicId) {

    }

    @Override
    public void deleteMovements(String publicId) {

    }

    @Override
    public void createNoPay(NoPayEntity entity) {

    }

    @Override
    public List<NoPayEntity> getAllNoPayByUser(UserEntity user) {
        return List.of();
    }

    @Override
    public List<NoPayEntity> getAllNoPays() {
        return List.of();
    }

    @Override
    public NoPayEntity getNoPay(String publicId) {
        return null;
    }

    @Override
    public void updateNoPay(NoPayEntity entity, String publicId) {

    }

    @Override
    public void deleteNoPay(String publicId) {

    }

    @Override
    public void saveLeave(LeaveEntity entity) {

    }

    @Override
    public List<LeaveEntity> getAllLeaveByUserByPubicId(String user) {
        return List.of();
    }

    @Override
    public List<LeaveEntity> getAllLeaveByUserByEmployeeId(String user) {
        return List.of();
    }

    @Override
    public List<LeaveEntity> getAllLeaves() {
        return List.of();
    }

    @Override
    public LeaveEntity getOneLeave(String publicId) {
        return null;
    }

    @Override
    public void deleteLeave(String publicId) {

    }

    @Override
    public void saveUser(UserEntity entity) {

    }

    @Override
    public List<UserEntity> getAllUsers() {
        return List.of();
    }

    @Override
    public void updateUser(String user, UserEntity entity) {

    }

    @Override
    public UserEntity getUserByPublicId(String user) {
        return null;
    }

    @Override
    public UserEntity getUserByEmployeeId(String user) {
        return null;
    }

    @Override
    public void deleteUser(String user) {

    }

    @Override
    public void saveAttendanceType(String shortName) {

    }

    @Override
    public AttendanceTypeEntity getAttendanceType(String shortName) {
        return null;
    }

    @Override
    public void updateAttendanceType(String publicId, String shortName) {

    }

    @Override
    public void deleteAttendanceType(String shortName, String publicId) {

    }

    @Override
    public void saveLeaveCategory(String name) {

    }

    public LeaveCategoryEntity getLeaveCategory(String name, String publicId) {
        if (name != null) {
            Optional<LeaveCategoryEntity> result = leaveCategoryRepo.findByName(name);
            if (result.isPresent()) {
                return result.get();
            }
        }

        if (publicId != null) {
            Optional<LeaveCategoryEntity> publicResult = leaveCategoryRepo.findByPublicId(publicId);
            if (publicResult.isPresent()) {
                return publicResult.get();
            }
        }

        return null; // Return null if no category is found
    }

    @Override
    public void updateLeaveCategory(String old_name, String name, String publicId) {

    }

    @Override
    public void deleteLeaveCategory(String name, String publicId) {

    }

    @Override
    public void saveLeaveType(String name) {

    }

    @Override
    public LeaveTypeEntity getLeaveType(String name, String publicId) {
        if (name != null) {
            Optional<LeaveTypeEntity> result = leaveTypeRepo.findByName(name);
            if (result.isPresent()) {
                return result.get();
            }
        }

        if (publicId != null) {
            Optional<LeaveTypeEntity> publicResult = leaveTypeRepo.findByPublicId(publicId);
            if (publicResult.isPresent()) {
                return publicResult.get();
            }
        }

        return null;
    }

    @Override
    public void updateLeaveType(String old_name, String name) {

    }

    @Override
    public void deleteLeaveType(String name, String publicId) {

    }
}


