package com.slt.peotv.lmsmangmentservice.service.impl;

import com.slt.peotv.lmsmangmentservice.entity.Absentee.AbsenteeEntity;
import com.slt.peotv.lmsmangmentservice.entity.Attendance.AttendanceEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveCategoryRemainingEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveTypeRemaining;
import com.slt.peotv.lmsmangmentservice.entity.Leave.LeaveEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveCategoryEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveTypeEntity;
import com.slt.peotv.lmsmangmentservice.entity.Movement.MovementsEntity;
import com.slt.peotv.lmsmangmentservice.entity.NoPay.NoPayEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.basic.RoleEntity;
import com.slt.peotv.lmsmangmentservice.exceptions.ErrorMessages;
import com.slt.peotv.lmsmangmentservice.model.AbsenteeReq;
import com.slt.peotv.lmsmangmentservice.model.LeaveReq;
import com.slt.peotv.lmsmangmentservice.model.MovementReq;
import com.slt.peotv.lmsmangmentservice.model.types.MovementType;
import com.slt.peotv.lmsmangmentservice.repository.*;
import com.slt.peotv.lmsmangmentservice.repository.archive.AttendanceRepo_;
import com.slt.peotv.lmsmangmentservice.service.Check_Service;
import com.slt.peotv.lmsmangmentservice.service.LMS_Service;
import com.slt.peotv.lmsmangmentservice.service.ServiceEvent;
import com.slt.peotv.lmsmangmentservice.utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.slt.peotv.lmsmangmentservice.entity.card.InOutEntity;

@Service
public class Check_Service_Impl implements Check_Service {

    @Service
    public class Helper {

        @Autowired
        private static AttendanceRepo attendanceRepo;

        @Autowired
        private static UserRepo userRepo;

        @Autowired
        private static ServiceEvent serviceEvent;

        @Autowired
        private static UserLeaveCategoryRemainingRepo userLeaveCategoryRemainingRepo;

        public void handleLateAndUnsuccessful(UserEntity user, AttendanceEntity attendanceEntity) {

            if (attendanceEntity != null)
                return;

            attendanceEntity.setIsUnSuccessful(true);

            UserLeaveCategoryRemainingEntity remaining_short_Leaves =
                    serviceEvent.getUserLeaveCategoryRemaining("SHORT_LEAVE", user.getUserId(), user.getEmployeeId());

            UserLeaveCategoryRemainingEntity remaining_half_Day =
                    serviceEvent.getUserLeaveCategoryRemaining("HALF_DAY", user.getUserId(), user.getEmployeeId());

            if (remaining_short_Leaves.getRemainingLeaves() < 1) { /// check are there any short leaves
                /// No short leaves

                attendanceEntity.setHalfDay(true);
                attendanceEntity.setIssues(true);

                if (remaining_half_Day.getRemainingLeaves() < 1) { /// check are there any half days
                    /// No half days

                    attendanceEntity.setIssue_description("GOING HALF DAY BUT REMAINING HALF DAY IS 0 SO GOING NO-PAY");

                    saveNoPayEntity(user, attendanceEntity, attendanceEntity.getIsHalfDay(),
                            attendanceEntity.getIsUnSuccessful(), attendanceEntity.getIsLate(),
                            attendanceEntity.getLateCover(), attendanceEntity.getAbsent(), attendanceEntity.getDate());
                } else {

                    attendanceEntity.setIssue_description("GOING HALF DAY BEFORE PASS THE DUE DATE PLEASE RESOLVE IT");
                    attendanceEntity.setDueDateForUA(getDueDate());

                    /// there are half days
                    /// there are half days consider as UnSuccessful Leave ======================

                    AbsenteeReq req = new AbsenteeReq();
                    req.setEmployeeId(user.getEmployeeId());
                    req.setUserId(user.getUserId());
                    req.setIsHalfDay(true);
                    req.setHappenDate(attendanceEntity.getDate());
                    req.setComment("GOING HALF DAY WITH-OUT NOTIFYING");

                    reportAbsent(req, true);
                    reportAttendance(attendanceEntity, false, false, true, false, false, true);
                }

            } else {
                /// there are short leaves

                attendanceEntity.setShortLeave(true);
                attendanceEntity.setIssues(true);

                remaining_short_Leaves.setRemainingLeaves(remaining_short_Leaves.getRemainingLeaves() - 1);
                userLeaveCategoryRemainingRepo.save(remaining_short_Leaves);
                userRepo.save(user);
            }

            assert attendanceEntity != null;
            attendanceRepo.save(attendanceEntity);
        }
    }
    @Autowired
    private AttendanceRepo_ attendanceRepo_;
    @Autowired
    private static AttendanceRepo attendanceRepo;
    @Autowired
    private LMS_Service lmsService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private static Utils utils;
    @Autowired
    private InOutRepo inOutRepo;
    @Autowired
    private MovementsRepo movementsRepo;
    @Autowired
    private static NoPayRepo noPayRepo;
    @Autowired
    private ServiceEvent serviceEvent;
    @Autowired
    private AbsenteeRepo absenteeRepo;
    @Autowired
    private LeaveTypeRepo leaveTypeRepo;
    @Autowired
    private UserLeaveCategoryRemainingRepo userLeaveCategoryRemainingRepo;
    @Autowired
    private Helper helper;
    @Autowired
    private LeaveRepo leaveRepo;

    private final ModelMapper modelMapper = new ModelMapper();

    public static boolean hasRole(Collection<RoleEntity> roles, String rol) {
        synchronized (roles) {
            return roles.stream()
                    .map(role -> role.getName().toUpperCase())
                    .anyMatch(name -> name.equals(rol));
        }
    }

    @Override
    /// CHECK IT PLEASE ⚠️ DATA IS MISSING OR NOT
    public void requestMovement(MovementReq req, Date dueDate) { /// ✅

        UserEntity u = lmsService.getUserByUserId(
                (req.getUserId() != null && !req.getUserId().isEmpty()) ? req.getUserId() : req.getEmployeeId()
        );
        UserLeaveTypeRemaining casual = getUserLeaveTypeRemaining("CASUAL", u);
        UserLeaveTypeRemaining annual = getUserLeaveTypeRemaining("ANNUAL", u);
        UserLeaveTypeRemaining sick = getUserLeaveTypeRemaining("SICK", u);
        UserLeaveTypeRemaining special = getUserLeaveTypeRemaining("SPECIAL", u);
        UserLeaveTypeRemaining duty = getUserLeaveTypeRemaining("DUTY", u);
        UserLeaveTypeRemaining maternityLeave = getUserLeaveTypeRemaining("MATERNITY_LEAVE", u);

        switch (req.getCategory()) {
            case "CASUAL" -> {
                if (casual.getRemainingLeaves() < 1) return;
            }
            case "ANNUAL" -> {
                if (annual.getRemainingLeaves() < 1) return;
            }
            case "SICK" -> {
                if (sick.getRemainingLeaves() < 1) return;
            }
            case "SPECIAL" -> {
                if (special.getRemainingLeaves() < 1) return;
            }
            case "DUTY" -> {
                if (duty.getRemainingLeaves() < 1) return;
            }
            case "MATERNITY LEAVE" -> {
                if (maternityLeave.getRemainingLeaves() < 1) return;
            }
            default -> {
                throw new IllegalArgumentException("Invalid leave type: " + req.getCategory());
            }
        }

        /*MovementsEntity movementsEntity = new MovementsEntity();
        movementsEntity.setPublicId(utils.generateId(10));*/

        MovementsEntity movementsEntity = modelMapper.map(req, MovementsEntity.class);
        movementsEntity.setPublicId(utils.generateId(10));
        movementsEntity.setUser(u);
        movementsEntity.setReqDate(new Date());
        movementsEntity.setLogTime(new Date());

        movementsEntity.setHalfDay(req.getHalfDay());
        movementsEntity.setIsAbsent(req.getAbsent());
        movementsEntity.setIsLate(req.getLate());
        movementsEntity.setIsLateCover(req.getLateCover());

        movementsEntity.setIsUnSuccessfulAttdate(req.getIsUnSuccessfulAttdate());
        movementsEntity.setHappenDate(req.getHappenDate());
        movementsEntity.setUnSuccessfulAttdate(req.getUnSuccessfulAttdate());
        movementsEntity.setHalfDay(req.getHalfDay());
        movementsEntity.setDueDate(dueDate);

        movementsEntity.setHalfDay(req.getHalfDay());
        movementsEntity.setLateCover(req.getLateCover());
        movementsEntity.setIsPending(false);
        movementsEntity.setIsAccepted(false);
        movementsEntity.setIsExpired(false);
        movementsEntity.setLateCover(req.getLateCover());

        lmsService.createMovements(movementsEntity);

    }

    /// Supervicer and HOD can see employee all the remain leaves and absents and nopays and movements 🔔🔔🔔🔔🔔🔔🔔🔔🔔🔔🔔🔔
    /// CHECK IT PLEASE ⚠️ DATA IS MISSING OR NOT
    public void approvedMove(MovementsEntity entity) {
        UserEntity user = lmsService.getUserByEmployeeId(entity.getUser().getEmployeeId());
        MovementType movementType = entity.getMovementType();

        /// When Adding a due date make sure put extra 1 month 2 weeks
        List<UserLeaveTypeRemaining> userLeaveTypeRemainingRepo = serviceEvent.getUserLeaveTypeRemaining(entity.getUser());

        List<UserLeaveTypeRemaining> filteredList = userLeaveTypeRemainingRepo.stream()
                .filter(userLeaveTypeRemaining -> userLeaveTypeRemaining.getRemainingLeaves() < 1)
                .collect(Collectors.toList());

        boolean allMatch = userLeaveTypeRemainingRepo.stream().allMatch(userLeaveTypeRemaining -> userLeaveTypeRemaining.getRemainingLeaves() < 1);
        if (allMatch) return;


        if (movementType == MovementType.ABSENT) {

            /// ============================================

            Date movementDate = entity.getHappenDate();
            /// Get the no pays related to that date also absent is true
            /// and roll back the one of the leaves

            /// ============================================

            ///  what is type employee should have if employee is absent
            ///  and check that leave type and check how many remain are there

            /// if there is no remaining leaves --> movement should be reject and it will consider as no pay (reject)
            /// if there is no remaining leaves --> reson is valid ???

            ///  there is remaining leaves --> reason is valid rollback (data changes)
            ///  there is remaining leaves --> reason is not valid rollback data not changing (becuase system automatically put absent) (reject)

        } else if (movementType == MovementType.LATEWORK) {

            /// ============================================

            Date movementDate = entity.getHappenDate();
            /// Get the no pays related to that date also absent is true
            /// and roll back the one of the leaves

            /// ============================================

            ///  check are there any short leaves and half days
            ///  2 raise conditions--> if there is no shot-leaves and but there are half days
            ///                     --> if there is shot-leaves and but there are no half days  (stupid)

            ///  reason is valid --> rollback and data changes --> but no shot leaves BUT there are half days still. so revert back the half day
            ///                                                --> but no half days BUT there are shot leaves still. so --- revert back the short leave
            ///                                                --> but no half days and no short leaves -- consider as nopay and request was rejected

            /// reason is not valid --> reject      --> but no shot leaves BUT there are half days still. cut off half day
            /// (employee need to do the late work)  --> but no half days BUT there are shot leaves still. cut of short leaves
            ///                                       --> but no half days and no short leaves -- consider as nopay

            /// did not do the late work --> but no shot leaves BUT there are half days still. cut off half day
            ///                          --> but no half days BUT there are shot leaves still. cut of short leaves
            ///                           --> but no half days and no short leaves -- consider as nopay

        } else if (movementType == MovementType.UNSUCCESSFUL) {
            /// ============================================

            Date movementDate = entity.getHappenDate();
            /// Get the no pays related to that date also absent is true
            /// and roll back the one of the leaves

            /// ============================================

        } else if (movementType == MovementType.LATE) {
            /// ============================================

            Date movementDate = entity.getHappenDate();
            /// Get the no pays related to that date also absent is true
            /// and roll back the one of the leaves

            /// ============================================
        }
    }

    @Override
    public void processMovementBySup(String superId, String moveId) {

    }
    @Override
    public void processMovementByHOD(String hodId, String moveId) {

    }
    @Override
    public void processMovementParticularUserBySup(String superId, String userId) {

    }
    @Override
    public void processMovementParticularUserByHOD(String hodId, String userId) {

    }
    @Override
    public void processMovementParticularIdsBySup(String superId, List<String> ids) {

    }
    @Override
    public void processMovementParticularIdsByHOD(String hodId, List<String> ids) {

    }

    public static Date getYesterdayDate() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /// CHECK IT PLEASE ⚠️ DATA IS MISSING OR NOT
    CHECK IT PLEASE ⚠️ DATA IS MISSING OR NOT
    public static NoPayEntity saveNoPayEntity(UserEntity user, AttendanceEntity attendanceEntity, Boolean isHalfDay, Boolean unSuccessful, Boolean isLate, Boolean isLateCover, Boolean isAbsent,
                                              Date accualDate) {
        if (attendanceEntity == null) {
            attendanceEntity = new AttendanceEntity();

            attendanceEntity.setPublicId(utils.generateId(10));
            attendanceEntity.setDate(getYesterdayDate());
            attendanceEntity.setHalfDay(isHalfDay);
            attendanceEntity.setUnSuccessful(unSuccessful);
            attendanceEntity.setLateCover(isLate);
            attendanceEntity.setLateCover(isLateCover);
            attendanceEntity.setAbsent(isAbsent);

            attendanceRepo.save(attendanceEntity);

        }
        NoPayEntity nopayEntity = new NoPayEntity();

        nopayEntity.setUser(user);
        nopayEntity.setPublicId(utils.generateId(10));
        nopayEntity.setAcctualDate(accualDate == null ? new Date() : accualDate);
        nopayEntity.setSubmissionDate(new Date());
        nopayEntity.setSubmissionDate(new Date());

        nopayEntity.setIsHalfDay(isHalfDay);
        nopayEntity.setUnSuccessful(unSuccessful);
        nopayEntity.setIsLate(isLate);
        nopayEntity.setIsLateCover(isLateCover);
        nopayEntity.setIsAbsent(isAbsent);

        nopayEntity.setHappenDate(accualDate);

        StringBuilder description = new StringBuilder();

        if (isAbsent) description.append("Absent on ").append(accualDate).append(". ");
        if (isHalfDay) description.append("Half-day on ").append(accualDate).append(". ");
        if (unSuccessful) description.append("Unsuccessful attendance on ").append(accualDate).append(". ");
        if (isLate) description.append("Late on ").append(accualDate).append(". ");
        if (isLateCover) description.append("Late cover on ").append(accualDate).append(". ");

        String finalDescription = description.toString().trim();
        nopayEntity.setComment(finalDescription);
        nopayEntity.setAttendance(attendanceEntity);

        attendanceEntity.setIsNoPay(true);
        attendanceRepo.save(attendanceEntity);

        nopayEntity = noPayRepo.save(nopayEntity);

        return nopayEntity;
    }

    public static Date getDueDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);  // Add 1 month
        calendar.add(Calendar.WEEK_OF_YEAR, 1); // Add 1 extra week
        return calendar.getTime(); // Return as Date object
    }

    @Override
    public void main() {

        /// CHECK ALL THE UNSUCCESSFUL AND UNAUTHORIZED ATTENDANCE PASS THE DUE DATE OR NOT ***********************************************
        /// ‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️

        /// ************************************************** TO ALL UNAUTHORIZED -- START ********************************************************
        ///  ==== but there is no leave by default system will sign no-pays ======

        /// And check are there any punches during 1pm or 2pm or 10am --- in that case ABSENT under half-day (if half day time fulfilled) were create to the employee
        ///         also Attendance under half-day (if half day time fulfilled)
        ///         --- record were crated under unAuthorized employee need to might resolve using a leave or make movement
        ///         /// employee can resolve these absent record using a leave
        ///         /// and after that ABSENT ENTITY mark as archives and in the description set why is archive ??? and in attendance is_resolve variable became true =====
        ///         /// also employee make a movement under category of unAuthorized

        /// ************************************************** TO ALL UNAUTHORIZED -- END ********************************************************

        /// ************* What if employee punch the card at 1pm has no short leaves ???? --- move = FUN@1(UNAUTHORIZED)
        /// ************* if employee forgot to swipe the card which consider as  --- move = FUN@2(UNAUTHORIZED) ---> going as no pay


        /// SICK, CASUAL , ETC ... all have full day and half days

        /// **************** employee make leave but still came to work

        /// If employee apply a leave but came to the office that leave will be canceled and unAuthorized attendance pop in the system
        /// And to resolve this employee might make request leave under type of SICK, CASUAL, ANNUAl and category of HALF_DAY, FULL_DAY or
        /// make movement %%%% (ALSO if employee is work full day in that case employee leave will be canceled it will consider full day work) and
        ///                    (ALSO if employee is work half-day in that case employee leave will be canceled it will consider half-day work)
        /// leave canceled mean that leave consider as archive and employee need to make another leave will be created depending on
        /// --- he/she inout (full-day, half-day leave)
        /// employee also can make a movement request -- in both cases
        /// short leave does not apply ::: --- under attendance unAuthorized attendance also set to is_resolve to true

        /// ***************** if employee is absent without making a leave --- move = ABSENT

        /// ==== but there is no leave by default system will sign no-pays ======

        /// if employee is absent without make a proper leave that case there is ABSENT were created to the employee
        /// also Attendance record were crated under unAuthorized to solve he/she make *** LEAVE ***  or request movement
        /// if she/he make leave ABSENT ENTITY mark as archives and in the description set why is archive ??? nd in attendance is_resolve variable became true
        /// but employee make movement ABSENT ENTITY mark as archives and in the description set why is archive ??? admin have to accept the movement
        /// when making leave she/he need to pick wheater is halfday or full day depending on he/she inout also to the movement

        /// ************************* OTHER -------------------------------

        /// if employee is late but he do not cover late work in 3rd time  UNSUCCESSFUL attendance pop in the system --- move = LATE_WORK / LATE / UNSUCCESSFUL
        /// first 2 short leaves will cut it of when its coming to LATE_WORK / LATE in 3rd time it will going as UNSUCCESSFUL Attendance under halfday
        /// can resolve using a leave or make movemnt ==== but there is no leave by default system will sign no-pays ======

        /// ***************************************************************************************************************
        /// ***************************************************************************************************************
        /// ***************************************************************************************************************
        /// ------------------------------------------- IMPLEMENTATION ----------------------------------------------------


        /// Get All Attendance ALL THE UN-AUTHORIZED AND UN-SUCCESSFUL ----------------------------------------------------------------------------------------

        List<AttendanceEntity> attendanceEntities = attendanceRepo.findOverdueEntities(new Date());
        List<AttendanceEntity> overdueEntities_filter = StreamSupport.stream(attendanceEntities.spliterator(), false)
                .filter(entity -> Boolean.TRUE.equals(entity.getIsUnAuthorized()) || Boolean.TRUE.equals(entity.getUnSuccessful()))
                .collect(Collectors.toList());

        overdueEntities_filter.forEach(entity -> {
            saveNoPayEntity(entity.getUser(), null, false,
                    false, false, false,
                    true, entity.getDate());
        });

    }

    public List<InOutEntity> getMorningPunchOnlyRecords() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Date yesterdayDate = Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return inOutRepo.findMorningPunchOnly(yesterdayDate);
    }

    public List<InOutEntity> getEveningPunchOnlyRecords() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Date yesterdayDate = Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return inOutRepo.findEveningPunchOnly(yesterdayDate);
    }

    @Override
    public void prerequisite() {

        /// Employees coming before 8.30 am
        Set<InOutEntity> employeesArrivedBefore830 = new HashSet<>(inOutRepo.findEmployeesBefore830(getYesterdayDate()));

        /// Employees leave the office between 5.00 - 5.30 pm
        Set<InOutEntity> employeesLeftBetween500And530 = new HashSet<>(inOutRepo.findEmployeesLeavingBetween5And530_(getYesterdayDate()));

        /// Employees Not Arrived Before 830AM
        //Set<InOutEntity> inOutEntities_EmployeesNotArrivedBefore830AM = new HashSet<>(inOutRepo.findEmployeesNotArrivedBefore830AM(getYesterdayDate()));

        /// Arrived Between 10AM And 11.59AM
        //Set<InOutEntity> inOutEntities_EmployeesArrivedBetween10AMAnd1159AM = new HashSet<>(inOutRepo.findEmployeesArrivedBetween10AMAnd1159AM(getYesterdayDate()));

        /// Arrived Between 12PM And 2PM
        //Set<InOutEntity> inOutEntities_EmployeesArrivedBetween12PMAnd2PM = new HashSet<>(inOutRepo.findEmployeesArrivedBetween12PMAnd2PM(getYesterdayDate()));

        /// Arrived Between 2PM And 5PM
        //Set<InOutEntity> inOutEntities_EmployeesArrivedBetween2PMAnd5PM = new HashSet<>(inOutRepo.findEmployeesArrivedBetween2PMAnd5PM(getYesterdayDate()));

        /// Arrived Late After 9AM
        //Set<InOutEntity> inOutEntities_ArrivedLateAfter9AM = new HashSet<>(inOutRepo.findEmployeesArrivedLateAfter9AM(getYesterdayDate()));

        /// Employees Half-Day
        Set<InOutEntity> inOutEntities_EmployeesHalfDay = new HashSet<>(inOutRepo.findEmployeesHalfDay(getYesterdayDate()));

        /// Employees who Covered LateTime - 1
        Set<InOutEntity> inOutEntities_EmployeesWhoCoveredLateTime = new HashSet<>(inOutRepo.findEmployeesWhoCoveredLateTime(getYesterdayDate()));

        /// Employees Who Did Not Cover LateTime
        Set<InOutEntity> inOutEntities_EmployeesWhoDidNotCoverLateTime = new HashSet<>(inOutRepo.findEmployeesWhoDidNotCoverLateTime(getYesterdayDate()));

        /// Employees Who came between 8.30 - 9.00 am
        Set<InOutEntity> employeesArrivedBetween830And900 = new HashSet<>(inOutRepo.findEmployeesBetween830And9(getYesterdayDate()));


        /// Cover -- FULL DAY ✅
        /// Cover -- UnAuthorized ✅ (swipe error)
        /// Cover -- UnSuccessFull ✅ (Late and Late work do not cover)
        /// Cover -- LATE ✅ (Late and Late work do cover so it will consider as full day)
        /// Cover -- HALF-DAY ✅

        /// Report employee who has full day attendance and who has swipe error ***************************************** --- START

        if (employeesArrivedBefore830.equals(employeesLeftBetween500And530)) {
            /// On Time Employees and full day
            Set<InOutEntity> commonEmployees = new HashSet<>(employeesArrivedBefore830);
            commonEmployees.retainAll(employeesLeftBetween500And530);

            for (InOutEntity commonEmployee : commonEmployees)
                reportAttendance(commonEmployee, true, false, false, false, false, false);

        } else {
            /// UnAuthorized employees (employees who forgot to swipe the card)

            HashSet<InOutEntity> inOutEntities_MorningPunch = new HashSet<>(getMorningPunchOnlyRecords());
            HashSet<InOutEntity> inOutEntities_EveningPunch = new HashSet<>(getEveningPunchOnlyRecords());

            for (InOutEntity employee : inOutEntities_MorningPunch)
                reportAttendance(employee, false, true, false, false, false, false);

            for (InOutEntity employee : inOutEntities_EveningPunch)
                reportAttendance(employee, false, true, false, false, false, false);

            NoPayEntity noPayEntity = new NoPayEntity();
            /// GOING NO-PAY
        }


        /// Report employee who has full day attendance and who has swipe error ***************************************** --- END

        /// Reporting Late employees  ********************************************************* --- START
        employeesArrivedBetween830And900.forEach(entity -> {

            inOutEntities_EmployeesWhoDidNotCoverLateTime.forEach(dnclt -> {
                reportAttendance(dnclt, false, false, true, true, false, false);
            });

            inOutEntities_EmployeesWhoCoveredLateTime.forEach(clt -> {
                reportAttendance(clt, true, false, false, true, true, false);
            });
        });
        /// Reporting Late employees  ********************************************************* --- END


        /// Reporting Half Days  ********************************************************* --- START
        inOutEntities_EmployeesHalfDay.forEach(entity -> {

            /// Check any leaves are remaining if do not nopay if does okay !!

            /// ALSO CHECK DOES EMPLOYEE APPLY A HALF DAY LEAVE AND GET APPROVED 🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑
            /// ===================== AND THERE IS NO LEAVES SYSTEM AUTOMATICALLY SIGNS NO-PAYS =================== 🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑

            AbsenteeReq req = new AbsenteeReq();
            req.setEmployeeId(entity.getEmployeeID());
            req.setUserId(entity.getUserId());
            req.setIsHalfDay(true);
            req.setHappenDate(entity.getDate());
            req.setComment("GOING HALF DAY WITH-OUT NOTIFYING");

            reportAbsent(req, true);
            reportAttendance(entity, true, true, false, false, false, true);

            /// SYSTEM AUTOMATICALLY CUT IT OF IF DUE DATE CAME AND IT WILL CONSIDER AS NO-PAY


        });
        /// Reporting Half Days  ********************************************************* --- END

        /*if (employeesArrivedBetween830And900.equals(employeesCoveredLateHours)) {
            ///  Late Employees and cover their work

            Set<InOutEntity> commonEmployees = new HashSet<>(employeesArrivedBefore830);
            commonEmployees.retainAll(employeesLeftBetween500And530);

            for (InOutEntity employee : commonEmployees)
                reportAttendance(employee, false, true,false,  false, false, false);

        } else {
            /// Late employees those who not cover late work
            for (InOutEntity employee : employeesArrivedBefore830)
                reportAttendance(employee, false, true,false,  false, false, false);
        }*/


        List<UserEntity> absentEmployeesToday = inOutRepo.findAbsentEmployeesYesterday(); /// Absent employees
        reportAbsent(absentEmployeesToday);
    }

    /*
      An employee punches their card when they arrive, and this punch is stored in our database. They punch again at 5:30 PM.
      If the employee arrives on time, our system checks whether there is an attendance record for that employee on the current day.
      If an entry exists, we save the latest morning punch-in as well as the latest evening punch-out.
      We then retrieve both the earliest morning and evening records to calculate the total working hours,
      determining whether the employee has worked a full day or a half day.
      It is possible for an employee to have multiple punch-ins and punch-outs in both the morning and evening.
      In such cases, we take the earliest punch-in from the morning and the earliest punch-out from the evening to calculate attendance.

      If an employee has only a morning punch-in without an evening punch-out, it is considered an unsuccessful (or unauthorized) attendance.
      If an employee punches in later than the designated time, it is marked as late.
      Additionally, if the employee arrives more than 30 minutes late, it is considered a short leave.
      If the employee repeats the same behavior the next day, it is again marked as short leave.
      However, if the same pattern continues on the third day, it is considered a half-day absence.

      And employee need to cover is late work (imagine he came within 30 min waiting period) if employee do not cover it is considered a short leave.
      If the employee repeats the same behavior the next day, it is again marked as short leave.
      However, if the same pattern continues on the third day, it is considered a half-day absence.

      if employee new (means he/she start working not more than 1 year) which means no leaves
      if employee work at 2 years which means there are leaves depending on when he/she end the their 1st year
      if employee work at 3 year he/she what ever she want until all leaves are gone
    * */

    @Override
    /// CHECK IT PLEASE ⚠️ DATA IS MISSING OR NOT
    CHECK IT PLEASE ⚠️ DATA IS MISSING OR NOT
    public void reportAttendance(InOutEntity inout, Boolean fullday, Boolean unAuthorized, Boolean unSuccessful, Boolean late, Boolean late_cover, Boolean half_day) {

        UserEntity userByEmployeeId = lmsService.getUserByEmployeeId(inout.getEmployeeID());

        if (userByEmployeeId == null) return;

        if (attendanceRepo.existsByUserAndDate(userByEmployeeId, getYesterdayDate())) return;

        AttendanceEntity attendance = new AttendanceEntity();
        attendance.setPublicId(utils.generateId(10));
        attendance.setUser(userByEmployeeId);
        attendance.setDate(inout.getDate());

        attendance.setLate(late);
        attendance.setLateCover(late_cover);
        attendance.setUnSuccessful(unSuccessful);
        attendance.setUnAuthorized(unAuthorized);
        attendance.setFullDay(fullday);
        attendance.setHalfDay(half_day);

        attendance.setArrival_date(inout.getPunchInMoa());
        attendance.setArrival_time(inout.getTimeMoa());
        attendance.setLeft_time(inout.getTimeEve());

        if (unAuthorized) {
            attendance.setDueDateForUA(getDueDate());
            attendance.setIssues(true);
            attendance.setIssue_description("GOING UNAUTHORIZED DUE TO THE  " + (half_day ? "HALF DAY " : "UNKNOWN REASON PLEASE CHECK ATTENDANCE") + "AND BEFORE PASS THE DUE DATE PLEASE RESOLVE IT");

        } else if (unSuccessful) {
            helper.handleLateAndUnsuccessful(userByEmployeeId, attendance);
            attendance.setDueDateForUA(getDueDate()); /// Get all the un-successful attendance if date goes make it no pay
        }

        attendanceRepo.save(attendance);

    }

    @Override
    /// CHECK IT PLEASE ⚠️ DATA IS MISSING OR NOT
    CHECK IT PLEASE ⚠️ DATA IS MISSING OR NOT
    public <T> void reportAttendance(Object obj, Boolean fullday, Boolean unAuthorized, Boolean unSuccessful, Boolean late, Boolean late_cover, Boolean half_day) {
        InOutEntity inOutEntity = null;
        AttendanceEntity attendanceEntity = null;

        if (obj instanceof InOutEntity) {
            inOutEntity = (InOutEntity) obj;
        } else if (obj instanceof AttendanceEntity) {
            attendanceEntity = (AttendanceEntity) obj;
        } else {
            System.out.println("Unknown Class");
            return;
        }

        // Dynamically fetch UserEntity based on the type of obj
        UserEntity userByEmployeeId = (inOutEntity != null) ?
                lmsService.getUserByEmployeeId(inOutEntity.getEmployeeID()) :
                lmsService.getUserByEmployeeId(attendanceEntity.getUser().getEmployeeId());

        if (userByEmployeeId == null) return;

        if (attendanceRepo.existsByUserAndDate(userByEmployeeId, getYesterdayDate())) return;

        AttendanceEntity attendance = new AttendanceEntity();
        attendance.setPublicId(utils.generateId(10));
        attendance.setUser(userByEmployeeId);
        attendance.setDate((inOutEntity != null) ? inOutEntity.getDate() : attendanceEntity.getDate());

        attendance.setLate(late);
        attendance.setLateCover(late_cover);
        attendance.setUnSuccessful(unSuccessful);
        attendance.setUnAuthorized(unAuthorized);
        attendance.setFullDay(fullday);
        attendance.setHalfDay(half_day);

        if (inOutEntity != null) {
            attendance.setArrival_date(inOutEntity.getPunchInMoa());
            attendance.setArrival_time(inOutEntity.getTimeMoa());
            attendance.setLeft_time(inOutEntity.getTimeEve());
        } else {
            attendance.setArrival_date(attendanceEntity.getArrival_date());
            attendance.setArrival_time(attendanceEntity.getArrival_time());
            attendance.setLeft_time(attendanceEntity.getLeft_time());
        }

        if (unAuthorized) {
            attendance.setDueDateForUA(getDueDate());
            attendance.setIssues(true);
            attendance.setIssue_description("GOING UNAUTHORIZED DUE TO THE  " +
                    (half_day ? "HALF DAY " : "UNKNOWN REASON PLEASE CHECK ATTENDANCE") +
                    " AND BEFORE PASS THE DUE DATE PLEASE RESOLVE IT");

        } else if (unSuccessful) {
            helper.handleLateAndUnsuccessful(userByEmployeeId, attendance);
            attendance.setDueDateForUA(getDueDate());
        }

        attendanceRepo.save(attendance);
    }

    private UserEntity getUser(String user_id, String employee_id) {
        UserEntity userByEmployeeId = lmsService.getUserByEmployeeId(user_id);
        UserEntity userByUserId = lmsService.getUserByUserId(user_id);

        UserEntity user = null;

        if (userByEmployeeId != null)
            user = userByEmployeeId;
        else if (userByUserId != null)
            user = userByUserId;
        return user;
    }

    public void saveLeave(UserEntity user, Date happenDate) {
        LeaveEntity leaveEntity = new LeaveEntity();
        leaveEntity.setPublicId(utils.generateId(10));
        leaveEntity.setUser(user);

        leaveEntity.setSubmitDate(new Date());
        leaveEntity.setFromDate(new Date());

        leaveEntity.setIsHODApproved(false);
        leaveEntity.setSupervisedApproved(false);
        leaveEntity.setHappenDate(happenDate);

        leaveRepo.save(leaveEntity);
    }

    @Override
    /// CHECK IT PLEASE ⚠️ DATA IS MISSING OR NOT
    CHECK IT PLEASE ⚠️ DATA IS MISSING OR NOT
    /// Day absents
    public void reportAbsent(List<UserEntity> absentEmployeesToday) {


        /// if employee is absent without make a proper leave that case there is ABSENT were created to the employee
        /// also Attendance record were crated under unAuthorized to solve he/she make *** LEAVE ***  or request movement
        /// if she/he make leave ABSENT ENTITY mark as archives and in the description set why is archive ??? nd in attendance is_resolve variable became true
        /// but employee make movement ABSENT ENTITY mark as archives and in the description set why is archive ??? admin have to accept the movement
        /// when making leave she/he need to pick wheater is halfday or full day depending on he/she inout also to the movement

        /// ALSO CHECK DOES EMPLOYEE APPLY A HALF DAY LEAVE AND GET APPROVED 🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑
        /// ===================== AND THERE IS NO LEAVES SYSTEM AUTOMATICALLY SIGNS NO-PAYS =================== 🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑🛑


        absentEmployeesToday.forEach(employee -> {

            UserEntity user = getUser(employee.getUserId(), employee.getEmployeeId());
            if (user == null) throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

            /// **************************************************************


            AttendanceEntity attendance = new AttendanceEntity();
            attendance.setPublicId(utils.generateId(10));
            attendance.setUser(employee);
            attendance.setDate(new Date());

            attendance.setLate(false);
            attendance.setLateCover(false);
            attendance.setUnSuccessful(false);

            attendance.setUnAuthorized(true);
            attendance.setIsAbsent(true);

            attendance.setFullDay(false);
            attendance.setHalfDay(false);
            attendance.setDueDateForUA(getDueDate());
            attendance.setIssues(true);

            attendance.setIssue_description("GOING UNAUTHORIZED DUE TO THE  " + "ABSENT WITH OUT MAKING A LEAVE " +
                    " AND BEFORE PASS THE DUE DATE PLEASE RESOLVE IT");

            attendanceRepo.save(attendance);

            /// **************************************************************

            AbsenteeEntity absenteeEntity = new AbsenteeEntity();
            absenteeEntity.setPublicId(utils.generateId(10));
            absenteeEntity.setUser(user);
            absenteeEntity.setDate(new Date());
            absenteeEntity.setIsHODApproved(false);
            absenteeEntity.setIsSupervisedApproved(false);
            absenteeEntity.setAudited(0);
            absenteeEntity.setIsNoPay(0);

            absenteeEntity.setIsAbsent(true);
            absenteeEntity.setIsLate(false);
            absenteeEntity.setIsLateCover(false);
            absenteeEntity.setIsUnSuccessfulAttdate(false);
            absenteeEntity.setIsHalfDay(false);

            absenteeEntity.setHappenDate(getYesterdayDate());

            absenteeEntity.setIsPending(false);
            absenteeEntity.setIsAccepted(false);

            absenteeRepo.save(absenteeEntity);

        });
    }

    @Override
    /// CHECK IT PLEASE ⚠️ DATA IS MISSING OR NOT
    CHECK IT PLEASE ⚠️ DATA IS MISSING OR NOT
    /// Absent Req for unSuccessful, Short_Leave, LateCover, Late
    public void reportAbsent_(AbsenteeReq req, Boolean special) {
        UserEntity user = getUser(req.getUserId(), req.getEmployeeId());
        if (user == null) throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        AbsenteeEntity absenteeEntity = new AbsenteeEntity();
        absenteeEntity.setPublicId(utils.generateId(10));
        absenteeEntity.setUser(user);
        absenteeEntity.setDate(new Date());
        absenteeEntity.setIsHODApproved(false);
        absenteeEntity.setIsSupervisedApproved(false);
        absenteeEntity.setAudited(0);
        absenteeEntity.setIsNoPay(0);

        absenteeEntity.setIsAbsent(req.getAbsent() != null ? req.getAbsent() : false);
        absenteeEntity.setIsLate(req.getLate() != null ? req.getLate() : false);
        absenteeEntity.setIsLateCover(req.getLateCover() != null ? req.getLateCover() : false);
        absenteeEntity.setIsUnSuccessfulAttdate(req.getUnSuccessfulAttdate() != null ? req.getUnSuccessfulAttdate() : false);
        absenteeEntity.setIsHalfDay(req.getHalfDay() != null ? req.getHalfDay() : false);
        absenteeEntity.setIsArchived(req.getArchived() != null ? req.getArchived() : false);

        absenteeEntity.setIsPending(false);
        absenteeEntity.setIsAccepted(false);

        List<UserLeaveTypeRemaining> userLeaveCategoryRemaining = serviceEvent.getUserLeaveTypeRemaining(user);
        boolean allMatch = userLeaveCategoryRemaining.stream().allMatch(userLeaveTypeRemaining -> userLeaveTypeRemaining.getRemainingLeaves() < 1);

        if(special)
            absenteeRepo.save(absenteeEntity);
        else if (!allMatch)
            absenteeRepo.save(absenteeEntity);
    }

    private UserLeaveTypeRemaining getUserLeaveTypeRemaining(String name, UserEntity user) {
        return serviceEvent.getUserLeaveTypeRemaining(name, user.getUserId(), user.getEmployeeId());
    }

    @Override
    public void requestALeave(LeaveReq req, String userId, String employeeId) { ///  Leave Request user - userId
        UserEntity u = lmsService.getUserByUserId(
                (userId != null && !userId.isEmpty()) ? userId : employeeId
        );

        if (u != null) {

            LeaveCategoryEntity leaveCategory = lmsService.getLeaveCategory(req.getLeaveCategory());
            LeaveTypeEntity leaveType = lmsService.getLeaveType(req.getLeaveType());

            UserLeaveTypeRemaining casual = getUserLeaveTypeRemaining("CASUAL", u);
            UserLeaveTypeRemaining annual = getUserLeaveTypeRemaining("ANNUAL", u);
            UserLeaveTypeRemaining sick = getUserLeaveTypeRemaining("SICK", u);
            UserLeaveTypeRemaining special = getUserLeaveTypeRemaining("SPECIAL", u);
            UserLeaveTypeRemaining duty = getUserLeaveTypeRemaining("DUTY", u);
            UserLeaveTypeRemaining maternityLeave = getUserLeaveTypeRemaining("MATERNITY_LEAVE", u);

            switch (leaveType.getName()) {
                case "CASUAL" -> {
                    if (casual.getRemainingLeaves() < 1) return;
                }
                case "ANNUAL" -> {
                    if (annual.getRemainingLeaves() < 1) return;
                }
                case "SICK" -> {
                    if (sick.getRemainingLeaves() < 1) return;
                }
                case "SPECIAL" -> {
                    if (special.getRemainingLeaves() < 1) return;
                }
                case "DUTY" -> {
                    if (duty.getRemainingLeaves() < 1) return;
                }
//                case "SHORT_LEAVE" -> {
//                    if (u.getTot_SHORT_LEAVE_Leaves() < 1) return;
//                }
                case "MATERNITY_LEAVE" -> {
                    if (maternityLeave.getRemainingLeaves() < 1) return;
                }
                default -> {
                    throw new IllegalArgumentException("Invalid leave type: " + leaveType.getName());
                }
            }

            /// Check ae there any leaves

            List<UserLeaveTypeRemaining> userLeaveTypeRemainingRepo = serviceEvent.getUserLeaveTypeRemaining(u);

            List<UserLeaveTypeRemaining> filteredList = userLeaveTypeRemainingRepo.stream()
                    .filter(userLeaveTypeRemaining -> userLeaveTypeRemaining.getRemainingLeaves() < 1)
                    .collect(Collectors.toList());

            boolean allMatch = userLeaveTypeRemainingRepo.stream().allMatch(userLeaveTypeRemaining -> userLeaveTypeRemaining.getRemainingLeaves() < 1);

            if (!allMatch) {

                LeaveEntity leaveEntity = new LeaveEntity();
                leaveEntity.setPublicId(utils.generateId(10));
                leaveEntity.setUser(u);
                leaveEntity.setSubmitDate(new Date());

                leaveEntity.setIsNoPay(0);

                leaveEntity.setFromDate(req.getFromDate());
                leaveEntity.setToDate(req.getToDate());

                leaveEntity.setLeaveCategory(leaveCategory);
                leaveEntity.setLeaveType(leaveType);

                leaveEntity.setIsSupervisedApproved(false);
                leaveEntity.setIsHODApproved(false);
                leaveEntity.setIsHalfDay(req.getHalfDay());
                leaveEntity.setNumOfDays(req.getNumOfDays());
                leaveEntity.setDescription(req.getDescription());

                leaveEntity.setUnSuccessful(false);
                leaveEntity.setIsLate(false);
                leaveEntity.setIsLateCover(false);
                leaveEntity.setIsShort_Leave(false);
                leaveEntity.setIsAccepted(false);
                leaveEntity.setIsPending(false);
                leaveEntity.setNotUsed(false);

                lmsService.saveLeave(leaveEntity);
            } else
                return;

            /// ------------ Haven't implement system to check NoPay || if employee make leave also have no leaves it conasider sa no pay
            /// -------------------------------------------------------------
        } else
            return;

    }

    public void approvedLeaveBySup(LeaveEntity entity) {
        UserEntity user = lmsService.getUserByEmployeeId(entity.getUser().getEmployeeId());

        UserLeaveTypeRemaining casual = getUserLeaveTypeRemaining("CASUAL", user);
        UserLeaveTypeRemaining annual = getUserLeaveTypeRemaining("ANNUAL", user);
        UserLeaveTypeRemaining sick = getUserLeaveTypeRemaining("SICK", user);
        UserLeaveTypeRemaining special = getUserLeaveTypeRemaining("SPECIAL", user);
        UserLeaveTypeRemaining duty = getUserLeaveTypeRemaining("DUTY", user);
        UserLeaveTypeRemaining maternityLeave = getUserLeaveTypeRemaining("MATERNITY_LEAVE", user);

        switch (entity.getLeaveType().getName()) {
            case "CASUAL" -> {
                if (casual.getRemainingLeaves() > 0) {
                    entity.setSupervisedApproved(true);
                    casual.setRemainingLeaves(casual.getRemainingLeaves() - 1);
                }
            }
            case "ANNUAL" -> {
                if (annual.getRemainingLeaves() > 0) {
                    entity.setSupervisedApproved(true);
                    annual.setRemainingLeaves(annual.getRemainingLeaves() - 1);
                }
            }
            case "SICK" -> {
                if (sick.getRemainingLeaves() > 0) {
                    entity.setSupervisedApproved(true);
                    sick.setRemainingLeaves(sick.getRemainingLeaves() - 1);
                }
            }
            case "SPECIAL" -> {
                if (special.getRemainingLeaves() > 0) {
                    entity.setSupervisedApproved(true);
                    special.setRemainingLeaves(special.getRemainingLeaves() - 1);
                }
            }
            case "DUTY" -> {
                if (duty.getRemainingLeaves() > 0) {
                    entity.setSupervisedApproved(true);
                    duty.setRemainingLeaves(casual.getRemainingLeaves() - 1);
                }
            }
            case "MATERNITY_LEAVE" -> {
                if (maternityLeave.getRemainingLeaves() > 0) {
                    entity.setSupervisedApproved(true);
                    maternityLeave.setRemainingLeaves(casual.getRemainingLeaves() - 1);
                }
            }
            default -> {
                throw new IllegalArgumentException("Invalid leave type: " + entity.getLeaveType().getName());
            }
        }
        userRepo.save(user);
        lmsService.saveLeave(entity);
    }

    public void approvedLeaveByHOD(LeaveEntity entity) {
        UserEntity user = lmsService.getUserByEmployeeId(entity.getUser().getEmployeeId());

        UserLeaveTypeRemaining casual = getUserLeaveTypeRemaining("CASUAL", user);
        UserLeaveTypeRemaining annual = getUserLeaveTypeRemaining("ANNUAL", user);
        UserLeaveTypeRemaining sick = getUserLeaveTypeRemaining("SICK", user);
        UserLeaveTypeRemaining special = getUserLeaveTypeRemaining("SPECIAL", user);
        UserLeaveTypeRemaining duty = getUserLeaveTypeRemaining("DUTY", user);
        UserLeaveTypeRemaining maternityLeave = getUserLeaveTypeRemaining("MATERNITY_LEAVE", user);

        switch (entity.getLeaveType().getName()) {
            case "CASUAL" -> {
                if (casual.getRemainingLeaves() > 0) {
                    entity.setHODApproved(true);
                    casual.setRemainingLeaves(casual.getRemainingLeaves() - 1);
                }
            }
            case "ANNUAL" -> {
                if (annual.getRemainingLeaves() > 0) {
                    entity.setHODApproved(true);
                    annual.setRemainingLeaves(annual.getRemainingLeaves() - 1);
                }
            }
            case "SICK" -> {
                if (sick.getRemainingLeaves() > 0) {
                    entity.setHODApproved(true);
                    sick.setRemainingLeaves(sick.getRemainingLeaves() - 1);
                }
            }
            case "SPECIAL" -> {
                if (special.getRemainingLeaves() > 0) {
                    entity.setHODApproved(true);
                    special.setRemainingLeaves(special.getRemainingLeaves() - 1);
                }
            }
            case "DUTY" -> {
                if (duty.getRemainingLeaves() > 0) {
                    entity.setHODApproved(true);
                    duty.setRemainingLeaves(casual.getRemainingLeaves() - 1);
                }
            }
            case "MATERNITY_LEAVE" -> {
                if (maternityLeave.getRemainingLeaves() > 0) {
                    entity.setHODApproved(true);
                    maternityLeave.setRemainingLeaves(casual.getRemainingLeaves() - 1);
                }
            }
            default -> {
                throw new IllegalArgumentException("Invalid leave type: " + entity.getLeaveType().getName());
            }
        }
        userRepo.save(user);
        lmsService.saveLeave(entity);
    }

    @Override
    public void processLeaveBySup(String superId, String leaveId) {
        LeaveEntity entity = lmsService.getOneLeave(leaveId);
        UserEntity employee = lmsService.getUserByEmployeeId(superId);

        if (employee == null || entity == null) return;

        if (hasRole(employee.getRoles(), "SUPERVISOR")) approvedLeaveBySup(entity);

    }

    @Override
    public void processLeaveByHOD(String hodId, String leaveId) {
        /// HOD by using his/her id and get particular leave accept it using leaveId

        LeaveEntity entity = lmsService.getOneLeave(leaveId);
        UserEntity employee = lmsService.getUserByEmployeeId(hodId);

        if (employee == null || entity == null) return;

        if (hasRole(employee.getRoles(), "HOD"))
            approvedLeaveByHOD(entity);
    }

    List<LeaveEntity> getAllLeavesByPubicId(String userId) {
        return lmsService.getAllLeaveByUserByPubicId(userId);
    }

    public List<LeaveEntity> getAllLeavesByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of(); // Return an empty list if no IDs are provided
        }

        // Using a thread-safe collection to avoid ConcurrentModificationException
        CopyOnWriteArrayList<LeaveEntity> result = new CopyOnWriteArrayList<>();

        ids.parallelStream()
                .map(lmsService::getOneLeave) // Assuming getOneLeave() fetches LeaveEntity by ID
                .filter(leave -> leave != null) // Ignore null results
                .forEach(result::add);

        return result;
    }

    @Override
    public void processLeaveParticularUserBySup(String superId, String userId) {
        /// Supervisor by using his/her id and get particular leave accept it using userId
        List<LeaveEntity> allLeaveByUserByPubicId = getAllLeavesByPubicId(userId);
        UserEntity employee = lmsService.getUserByEmployeeId(superId);

        if (employee == null || allLeaveByUserByPubicId == null || allLeaveByUserByPubicId.isEmpty()) return;

        if (hasRole(employee.getRoles(), "SUPERVISOR"))
            allLeaveByUserByPubicId.forEach(this::approvedLeaveBySup);

    }

    @Override
    public void processLeaveParticularUserByHOD(String hodId, String userId) {
        /// HOD by using his/her id and get particular leave accept §it using userId
        List<LeaveEntity> allLeaveByUserByPubicId = getAllLeavesByPubicId(userId);
        UserEntity employee = lmsService.getUserByEmployeeId(hodId);

        if (employee == null || allLeaveByUserByPubicId == null || allLeaveByUserByPubicId.isEmpty()) return;

        if (hasRole(employee.getRoles(), "SUPERVISOR"))
            allLeaveByUserByPubicId.forEach(this::approvedLeaveByHOD);
    }

    @Override
    public void processLeaveParticularIdsBySup(String superId, List<String> ids) {
        /// Supervisor by using his/her id and get particular leave accept it using List of Ids
        List<LeaveEntity> allLeaveByIds = getAllLeavesByIds(ids);
        UserEntity employee = lmsService.getUserByEmployeeId(superId);

        if (employee == null || allLeaveByIds == null || allLeaveByIds.isEmpty()) return;

        if (hasRole(employee.getRoles(), "SUPERVISOR"))
            allLeaveByIds.forEach(this::approvedLeaveBySup);
    }

    @Override
    public void processLeaveParticularIdsByHOD(String hodId, List<String> ids) {
        /// HOD by using his/her id and get particular leave accept it using List of Ids
        List<LeaveEntity> allLeaveByIds = getAllLeavesByIds(ids);
        UserEntity employee = lmsService.getUserByEmployeeId(hodId);

        if (employee == null || allLeaveByIds == null || allLeaveByIds.isEmpty()) return;

        if (hasRole(employee.getRoles(), "SUPERVI§SOR"))
            allLeaveByIds.forEach(this::approvedLeaveByHOD);
    }


    @Override
    public void getAllTheInOutRecordsFromSLT() {
        /// First get the all the data and using employee id query the our local database
    }

}
