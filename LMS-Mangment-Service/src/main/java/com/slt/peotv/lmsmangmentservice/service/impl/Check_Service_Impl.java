package com.slt.peotv.lmsmangmentservice.service.impl;

import com.slt.peotv.lmsmangmentservice.entity.Absentee.AbsenteeEntity;
import com.slt.peotv.lmsmangmentservice.entity.Attendance.AttendanceEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.LeaveEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveCategoryRemainingEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveTypeRemaining;
import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveCategoryEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveTypeEntity;
import com.slt.peotv.lmsmangmentservice.entity.Movement.MovementsEntity;
import com.slt.peotv.lmsmangmentservice.entity.NoPay.NoPayEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.basic.RoleEntity;
import com.slt.peotv.lmsmangmentservice.entity.card.InOutEntity;
import com.slt.peotv.lmsmangmentservice.exceptions.ErrorMessages;
import com.slt.peotv.lmsmangmentservice.model.AbsenteeReq;
import com.slt.peotv.lmsmangmentservice.model.LeaveReq;
import com.slt.peotv.lmsmangmentservice.model.MovementReq;
import com.slt.peotv.lmsmangmentservice.model.types.MovementType;
import com.slt.peotv.lmsmangmentservice.repository.*;
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

@Service
public class Check_Service_Impl implements Check_Service {

    @Autowired
    private static AttendanceRepo attendanceRepo;
    @Autowired
    private static Utils utils;
    @Autowired
    private static NoPayRepo noPayRepo;
    private final ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private LMS_Service lmsService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private InOutRepo inOutRepo;
    @Autowired
    private MovementsRepo movementsRepo;
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
    @Autowired
    private UserLeaveTypeRemainingRepo userLeaveTypeRemainingRepo;

    public static boolean hasRole(Collection<RoleEntity> roles, String rol) {
        synchronized (roles) {
            return roles.stream()
                    .map(role -> role.getName().toUpperCase())
                    .anyMatch(name -> name.equals(rol));
        }
    }

    public static Date getYesterdayDate() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static NoPayEntity saveNoPayEntity(UserEntity user, AttendanceEntity attendanceEntity, Boolean isHalfDay, Boolean unSuccessful, Boolean isLate, Boolean isLateCover, Boolean isAbsent,
                                              Date accualDate) {
        if (attendanceEntity == null) {
            attendanceEntity = new AttendanceEntity();

            attendanceEntity.setPublicId(utils.generateId(10));
            attendanceEntity.setDate(getYesterdayDate());
            attendanceEntity.setIsHalfDay(isHalfDay);
            attendanceEntity.setIsUnSuccessful(unSuccessful);
            attendanceEntity.setLateCover(isLate);
            attendanceEntity.setLateCover(isLateCover);
            attendanceEntity.setIsAbsent(isAbsent);

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
    public void requestMovement(MovementReq req, Date dueDate) {

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

        MovementsEntity movementsEntity = modelMapper.map(req, MovementsEntity.class);
        movementsEntity.setPublicId(utils.generateId(10));
        movementsEntity.setUser(u);
        movementsEntity.setReqDate(new Date());
        movementsEntity.setLogTime(new Date());

        movementsEntity.setIsHalfDay(req.getHalfDay());
        movementsEntity.setIsAbsent(req.getAbsent());
        movementsEntity.setIsLate(req.getLate());
        movementsEntity.setIsLateCover(req.getLateCover());
        movementsEntity.setHappenDate(req.getHappenDate());

        movementsEntity.setIsUnSuccessfulAttdate(req.getIsUnSuccessfulAttdate());
        movementsEntity.setHappenDate(req.getHappenDate());
        movementsEntity.setIsUnSuccessfulAttdate(req.getUnSuccessfulAttdate());
        movementsEntity.setIsHalfDay(req.getHalfDay());
        movementsEntity.setDueDate(dueDate);

        movementsEntity.setIsHalfDay(req.getHalfDay());
        movementsEntity.setIsLateCover(req.getLateCover());
        movementsEntity.setIsPending(false);
        movementsEntity.setIsAccepted(false);
        movementsEntity.setIsExpired(false);
        movementsEntity.setIsLateCover(req.getLateCover());

        lmsService.createMovements(movementsEntity);

    }

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


        Date movementDate = entity.getHappenDate();
        List<MovementsEntity> byHappenDate = movementsRepo.findByHappenDate(movementDate);

        Optional<AttendanceEntity> attendance = attendanceRepo.findByUserAndDate(user, entity.getHappenDate());
        Optional<AbsenteeEntity> absentee = absenteeRepo.findByUserAndDate(user, entity.getHappenDate());
        if(attendance.isPresent()) {

            AttendanceEntity attendanceEntity = attendance.get();
            attendanceEntity.setResolve(true);
            attendanceRepo.save(attendanceEntity);

            if(absentee.isPresent()){
                AbsenteeEntity absenteeEntity = absentee.get();
                absenteeEntity.setIsArchived(true);
                absenteeEntity.setComment("EMPLOYEE RESOLVE HIS/HER " + (absenteeEntity.getIsHalfDay() ? "HALF DAY" : absenteeEntity.getIsAbsent() ? "ABSENT": "ISSUE WITH HIS/HER ATTENDANCE"));
                absenteeRepo.save(absenteeEntity);
            }
        }
    }

    @Override
    public void processMovementBySup(String superId, String moveId) {
        UserEntity employee = lmsService.getUserByEmployeeId(superId);
        if(employee == null)
            throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        Optional<MovementsEntity> byPublicId = movementsRepo.findByPublicId(moveId);
        if(byPublicId.isPresent())
            approvedMove(byPublicId.get());
        else
            throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
    }

    @Override
    public void processMovementByHOD(String hodId, String moveId) {
        UserEntity employee = lmsService.getUserByEmployeeId(hodId);
        if(employee == null)
            throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        Optional<MovementsEntity> byPublicId = movementsRepo.findByPublicId(moveId);
        if(byPublicId.isPresent())
            approvedMove(byPublicId.get());
        else
            throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

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

    @Override
    public void main() {

        List<AttendanceEntity> attendanceEntities = attendanceRepo.findOverdueEntities(new Date());
        List<AttendanceEntity> overdueEntities_filter = StreamSupport.stream(attendanceEntities.spliterator(), false)
                .filter(entity -> Boolean.TRUE.equals(entity.getIsUnAuthorized()) || Boolean.TRUE.equals(entity.getIsUnSuccessful()))
                .collect(Collectors.toList());

        overdueEntities_filter.forEach(entity -> {
            saveNoPayEntity(entity.getUser(), null, false,
                    false, false, false,
                    true, entity.getDate());
        });

        prerequisite();

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

            UserEntity user = getUser(entity.getUserId(), entity.getEmployeeID());

            /// CHECKING IF EMPLOYEE MIGHT PUT A LEAVE BEFORE SHE/HE ABSENT (HALF-DAY) -- EMPLOYEE DO
            List<LeaveEntity> byUserAndFromDateLessThanEqualAndToDateGreaterThanEqual = leaveRepo.findByUserAndFromDateLessThanEqualAndToDateGreaterThanEqual(user, new Date(), new Date());

            if (!byUserAndFromDateLessThanEqualAndToDateGreaterThanEqual.isEmpty()) {
                byUserAndFromDateLessThanEqualAndToDateGreaterThanEqual.forEach(leaveEntity -> {

                    /// DOUBLE CHECK LEAVE DATE MATCH CURRENT DATE AND WHETHER LEAVE APPROVED OR NOT
                    if (leaveEntity.getIsHODApproved() && leaveEntity.getIsSupervisedApproved() && leaveEntity.getToDate().equals(getYesterdayDate())) {

                        if (leaveEntity.getIsHalfDay()) {
                            /// GETTING ONLY HALF-DAYS
                            /// Employee absent || Employee make a leave before she/he absent
                            /// AND SHE/HE NOW USED THE LEAVE
                            /// cut of the leave because leave actually been used and mark as used

                            leaveEntity.setDescription("Absent - Leave Used");
                            leaveEntity.setNotUsed(false); /// WHICH MEANS EMPLOYEE USE THE LEAVE

                            /// CUT OF ONE OF THE LEAVES
                            UserLeaveTypeRemaining userLeaveTypeRemaining = getUserLeaveTypeRemaining(leaveEntity.getLeaveType().getName(), leaveEntity.getUser());
                            if (userLeaveTypeRemaining.getRemainingLeaves() < 1) {
                                userLeaveTypeRemaining.setRemainingLeaves(userLeaveTypeRemaining.getRemainingLeaves() - 1);
                                userLeaveTypeRemainingRepo.save(userLeaveTypeRemaining);
                            }

                            leaveRepo.save(leaveEntity);

                        }

                    } else {

                        /// LEAVE NOT APPROVED BUT EMPLOYEE ABSENT
                        /// CHECK ARE THERE ANY REMAINING LEAVES -- IF YES -> OKAY || IF NO -> NO_PAY
                        List<UserLeaveTypeRemaining> userLeaveCategoryRemaining = serviceEvent.getUserLeaveTypeRemaining(user);
                        boolean allMatch = userLeaveCategoryRemaining.stream().allMatch(userLeaveTypeRemaining -> userLeaveTypeRemaining.getRemainingLeaves() < 1);

                        if (allMatch) { /// NO REMAINING LEAVES
                        /// GOING NO PAY -- SET DESCRIPTION IN NO-PAY
                            leaveEntity.setIsPending(true);
                            leaveEntity.setDescription("EMPLOYEE IS ABSENT ALSO HE/SHE MAKE REQUEST TO LEAVE NOT APPROVED HENCE THIS LEAVE STILL PENDING");
                            Check_Service_Impl.saveNoPayEntity(leaveEntity.getUser(), null, false, true, false, false, false, leaveEntity.getHappenDate());

                        } else {
                            /// THERE ARE LEAVES

                            helper.handleAbsenteeReqHalf(leaveEntity.getUser());
                        }

                    }
                });
            } else {

                /// CHECKING IF EMPLOYEE MIGHT PUT A LEAVE BEFORE SHE/HE ABSENT (HALF-DAY) --- EMPLOYEE DO NOT
                /// CHECK ARE THERE ANY REMAINING LEAVES -- IF YES -> OKAY || IF NO -> NO_PAY

                List<UserLeaveTypeRemaining> userLeaveCategoryRemaining = serviceEvent.getUserLeaveTypeRemaining(user);
                boolean allMatch = userLeaveCategoryRemaining.stream().allMatch(userLeaveTypeRemaining -> userLeaveTypeRemaining.getRemainingLeaves() < 1);


                /// GOING NO PAY -- SET DESCRIPTION IN NO-PAY
                if (allMatch)
                    Check_Service_Impl.saveNoPayEntity(user, null, false, true, false, false, false, getYesterdayDate());

                /// THERE ARE LEAVES
                helper.handleAbsenteeReqHalf(user);
            }

        });
        /// Reporting Half Days  ********************************************************* --- END

        List<UserEntity> absentEmployeesToday = inOutRepo.findAbsentEmployeesYesterday(); /// Absent employees
        reportAbsent(absentEmployeesToday);
    }

    @Override
    /// CHECK IT PLEASE ⚠️ DATA IS MISSING OR NOT
    public void reportAttendance(InOutEntity inout, Boolean fullday, Boolean unAuthorized, Boolean unSuccessful, Boolean late, Boolean late_cover, Boolean half_day) {

        UserEntity userByEmployeeId = lmsService.getUserByEmployeeId(inout.getEmployeeID());

        if (userByEmployeeId == null) return;

        if (attendanceRepo.existsByUserAndDate(userByEmployeeId, getYesterdayDate())) return;

        AttendanceEntity attendance = new AttendanceEntity();
        attendance.setPublicId(utils.generateId(10));
        attendance.setUser(userByEmployeeId);
        attendance.setDate(inout.getDate());

        attendance.setIsLate(late);
        attendance.setLateCover(late_cover);
        attendance.setIsUnSuccessful(unSuccessful);
        attendance.setIsUnAuthorized(unAuthorized);
        attendance.setIsFullDay(fullday);
        attendance.setIsHalfDay(half_day);

        attendance.setArrivalDate(inout.getPunchInMoa());
        attendance.setArrivalTime(inout.getTimeMoa());
        attendance.setLeftTime(inout.getTimeEve());

        if (unAuthorized) {
            attendance.setDueDateForUA(getDueDate());
            attendance.setIssues(true);
            attendance.setIssueDescription("GOING UNAUTHORIZED DUE TO THE  " + (half_day ? "HALF DAY " : "UNKNOWN REASON PLEASE CHECK ATTENDANCE") + "AND BEFORE PASS THE DUE DATE PLEASE RESOLVE IT");

        } else if (unSuccessful) {
            attendance.setIssues(true);
            helper.handleLateAndUnsuccessful(userByEmployeeId, attendance);
            attendance.setDueDateForUA(getDueDate()); /// Get all the un-successful attendance if date goes make it no pay
            attendance.setIssueDescription("GOING UNSUCCESSFUL DUE TO THE  " +
                    (half_day ? "HALF DAY " : "UNKNOWN REASON PLEASE CHECK ATTENDANCE") +
                    " AND BEFORE PASS THE DUE DATE PLEASE RESOLVE IT");
        }

        attendanceRepo.save(attendance);

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

        attendance.setIsLate(late);
        attendance.setLateCover(late_cover);
        attendance.setIsUnSuccessful(unSuccessful);
        attendance.setIsUnAuthorized(unAuthorized);
        attendance.setIsFullDay(fullday);
        attendance.setIsHalfDay(half_day);

        if (inOutEntity != null) {
            attendance.setArrivalDate(inOutEntity.getPunchInMoa());
            attendance.setArrivalTime(inOutEntity.getTimeMoa());
            attendance.setLeftTime(inOutEntity.getTimeEve());
        } else {
            attendance.setArrivalDate(attendanceEntity.getArrivalDate());
            attendance.setArrivalTime(attendanceEntity.getArrivalTime());
            attendance.setLeftTime(attendanceEntity.getLeftTime());
        }

        if (unAuthorized) {
            attendance.setDueDateForUA(getDueDate());
            attendance.setIssues(true);
            attendance.setIssueDescription("GOING UNAUTHORIZED DUE TO THE  " +
                    (half_day ? "HALF DAY " : "UNKNOWN REASON PLEASE CHECK ATTENDANCE") +
                    " AND BEFORE PASS THE DUE DATE PLEASE RESOLVE IT");

        } else if (unSuccessful) {
            helper.handleLateAndUnsuccessful(userByEmployeeId, attendance);
            attendance.setDueDateForUA(getDueDate());
            attendance.setIssues(true);
            attendance.setIssueDescription("GOING UNSUCCESSFUL DUE TO THE  " +
                    (half_day ? "HALF DAY " : "UNKNOWN REASON PLEASE CHECK ATTENDANCE") +
                    " AND BEFORE PASS THE DUE DATE PLEASE RESOLVE IT");
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
        leaveEntity.setIsSupervisedApproved(false);
        leaveEntity.setHappenDate(happenDate);

        leaveRepo.save(leaveEntity);
    }

    @Override
    /// Day absents
    public void reportAbsent(List<UserEntity> absentEmployeesToday) {

        absentEmployeesToday.forEach(employee -> {

            /// CHECKING IF EMPLOYEE MIGHT PUT A LEAVE BEFORE SHE/HE ABSENT (FULL-DAY) -- EMPLOYEE DO
            List<LeaveEntity> byUserAndFromDateLessThanEqualAndToDateGreaterThanEqual = leaveRepo.findByUserAndFromDateLessThanEqualAndToDateGreaterThanEqual(employee, new Date(), new Date());

            if (!byUserAndFromDateLessThanEqualAndToDateGreaterThanEqual.isEmpty()) { /// IF PASSES WHICH MEANS EMPLOYEE DO MAKE LEAVE

                byUserAndFromDateLessThanEqualAndToDateGreaterThanEqual.forEach(leaveEntity -> {

                    /// DOUBLE CHECK LEAVE DATE MATCH CURRENT DATE AND WHETHER LEAVE APPROVED OR NOT
                    if (leaveEntity.getIsHODApproved() && leaveEntity.getIsSupervisedApproved() && leaveEntity.getToDate().equals(getYesterdayDate())) {

                        leaveEntity.setDescription("Absent - Leave Used");
                        leaveEntity.setNotUsed(false); /// WHICH MEANS EMPLOYEE USE THE LEAVE

                        /// CUT OF ONE OF THE LEAVES
                        UserLeaveTypeRemaining userLeaveTypeRemaining = getUserLeaveTypeRemaining(leaveEntity.getLeaveType().getName(), leaveEntity.getUser());
                        if (userLeaveTypeRemaining.getRemainingLeaves() < 1) {
                            userLeaveTypeRemaining.setRemainingLeaves(userLeaveTypeRemaining.getRemainingLeaves() - 1);
                            userLeaveTypeRemainingRepo.save(userLeaveTypeRemaining);
                        }

                        leaveRepo.save(leaveEntity);

                    } else {

                        helper.handleAbsenteeReqFull(employee, leaveEntity);
                        reportAttendance(employee, false, true, false, false, false, false);

                    }

                });
            } else {

                helper.handleAbsenteeReqFull(employee);

                reportAttendance(employee, false, true, false, false, false, false);

            }

        });
    }

    /// Absent Req for unSuccessful, Short_Leave, LateCover, Late
    public void reportAbsent(AbsenteeReq req) {
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
        absenteeEntity.setComment(req.getComment() != null ? req.getComment() : "");

        absenteeEntity.setIsPending(false);
        absenteeEntity.setIsAccepted(false);


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

        List<AbsenteeEntity> byUser = absenteeRepo.findByUser(u);

        byUser = byUser.stream()
                .filter(absentee -> absentee.getDate().equals(req.getHappenDate()))
                .collect(Collectors.toList());

        if (u != null) {

            LeaveCategoryEntity leaveCategory = lmsService.getLeaveCategory(req.getLeaveCategory());
            LeaveTypeEntity leaveType = lmsService.getLeaveType(req.getLeaveType());


            /// Check ae there any leaves

            List<UserLeaveTypeRemaining> userLeaveTypeRemainingRepo_ = serviceEvent.getUserLeaveTypeRemaining(u);

            List<UserLeaveTypeRemaining> filteredList = userLeaveTypeRemainingRepo_.stream()
                    .filter(userLeaveTypeRemaining -> userLeaveTypeRemaining.getRemainingLeaves() < 1)
                    .collect(Collectors.toList());

            boolean allMatch = userLeaveTypeRemainingRepo_.stream().allMatch(userLeaveTypeRemaining -> userLeaveTypeRemaining.getRemainingLeaves() < 1);

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

                UserLeaveTypeRemaining userLeaveTypeRemaining = getUserLeaveTypeRemaining(leaveEntity.getLeaveType().getName(), leaveEntity.getUser());
                if (userLeaveTypeRemaining.getRemainingLeaves() < 1) {
                    userLeaveTypeRemaining.setRemainingLeaves(userLeaveTypeRemaining.getRemainingLeaves() - 1);
                    userLeaveTypeRemainingRepo.save(userLeaveTypeRemaining);
                }
                byUser.forEach(absenteeEntity -> {
                    absenteeEntity.setIsArchived(true);
                    absenteeEntity.setComment("EMPLOYEE RESOLVE HIS/HER " + (absenteeEntity.getIsHalfDay() ? "HALF DAY" : absenteeEntity.getIsAbsent() ? "ABSENT": "ISSUE WITH HIS/HER ATTENDANCE"));
                    absenteeRepo.save(absenteeEntity);

                    Optional<AttendanceEntity> byUserAndDate = attendanceRepo.findByUserAndDate(u, absenteeEntity.getDate());
                    if(byUserAndDate.isPresent()) {
                        AttendanceEntity attendanceEntity = byUserAndDate.get();
                        attendanceEntity.setResolve(true);
                        attendanceRepo.save(attendanceEntity);
                    }
                });

            } else {
            }
        } else {
        }

    }

    public void approvedLeaveBySup(LeaveEntity entity) {
        UserEntity user = lmsService.getUserByEmployeeId(entity.getUser().getEmployeeId());
        
        UserLeaveTypeRemaining userLeaveTypeRemaining = getUserLeaveTypeRemaining(entity.getLeaveType().getName(), entity.getUser());
        if (userLeaveTypeRemaining.getRemainingLeaves() < 1) {
            userLeaveTypeRemaining.setRemainingLeaves(userLeaveTypeRemaining.getRemainingLeaves() - 1);
            userLeaveTypeRemainingRepo.save(userLeaveTypeRemaining);
        }
        userRepo.save(user);
        lmsService.saveLeave(entity);
    }

    public void approvedLeaveByHOD(LeaveEntity entity) {
        UserEntity user = lmsService.getUserByEmployeeId(entity.getUser().getEmployeeId());

        UserLeaveTypeRemaining userLeaveTypeRemaining = getUserLeaveTypeRemaining(entity.getLeaveType().getName(), entity.getUser());
        if (userLeaveTypeRemaining.getRemainingLeaves() < 1) {
            userLeaveTypeRemaining.setRemainingLeaves(userLeaveTypeRemaining.getRemainingLeaves() - 1);
            userLeaveTypeRemainingRepo.save(userLeaveTypeRemaining);
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

        public void handleAbsenteeReqFull(UserEntity employee, LeaveEntity leaveEntity) {
            List<UserLeaveTypeRemaining> userLeaveCategoryRemaining = serviceEvent.getUserLeaveTypeRemaining(leaveEntity.getUser());
            boolean allMatch = userLeaveCategoryRemaining.stream().allMatch(userLeaveTypeRemaining -> userLeaveTypeRemaining.getRemainingLeaves() < 1);


            UserEntity user = getUser(employee.getUserId(), employee.getEmployeeId());
            if (user == null) throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

            AttendanceEntity attendance = new AttendanceEntity();
            attendance.setPublicId(utils.generateId(10));
            attendance.setUser(employee);
            attendance.setDate(new Date());

            attendance.setIsLate(false);
            attendance.setLateCover(false);
            attendance.setIsUnSuccessful(false);

            attendance.setIsUnAuthorized(true);
            attendance.setIsAbsent(true);

            attendance.setIsFullDay(false);
            attendance.setIsHalfDay(false);
            attendance.setDueDateForUA(getDueDate());
            attendance.setIssues(true);

            attendance.setIssueDescription("GOING UNAUTHORIZED DUE TO THE  " + "ABSENT WITH OUT MAKING A LEAVE " +
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
            absenteeEntity.setComment("EMPLOYEE ABSENT IN TODAY");
            absenteeEntity.setHappenDate(getYesterdayDate());

            absenteeEntity.setIsPending(false);
            absenteeEntity.setIsAccepted(false);

            absenteeRepo.save(absenteeEntity);


            if (allMatch) { /// NO REMAINING LEAVES

                /// GOING NO PAY -- SET DESCRIPTION IN NO-PAY, FULL DAY IS TURE
                leaveEntity.setIsPending(true);
                leaveEntity.setDescription("EMPLOYEE IS ABSENT ALSO HE/SHE MAKE REQUEST TO LEAVE NOT APPROVED HENCE THIS LEAVE STILL PENDING");

                /// SET FULL DAY IS TURE
                Check_Service_Impl.saveNoPayEntity(leaveEntity.getUser(), null, false, false, false, false, true, leaveEntity.getHappenDate());

            }

            reportAttendance(user, false, true, false, false, false, false);
        }

        public void handleAbsenteeReqFull(UserEntity employee) {
            List<UserLeaveTypeRemaining> userLeaveCategoryRemaining = serviceEvent.getUserLeaveTypeRemaining(employee);
            boolean allMatch = userLeaveCategoryRemaining.stream().allMatch(userLeaveTypeRemaining -> userLeaveTypeRemaining.getRemainingLeaves() < 1);


            UserEntity user = getUser(employee.getUserId(), employee.getEmployeeId());
            if (user == null) throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

            AttendanceEntity attendance = new AttendanceEntity();
            attendance.setPublicId(utils.generateId(10));
            attendance.setUser(employee);
            attendance.setDate(new Date());

            attendance.setIsLate(false);
            attendance.setLateCover(false);
            attendance.setIsUnSuccessful(false);

            attendance.setIsUnAuthorized(true);
            attendance.setIsAbsent(true);

            attendance.setIsFullDay(false);
            attendance.setIsHalfDay(false);
            attendance.setDueDateForUA(getDueDate());
            attendance.setIssues(true);

            attendance.setIssueDescription("GOING UNAUTHORIZED DUE TO THE  " + "ABSENT WITH OUT MAKING A LEAVE " +
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
            absenteeEntity.setComment("EMPLOYEE ABSENT IN TODAY");
            absenteeEntity.setHappenDate(getYesterdayDate());

            absenteeEntity.setIsPending(false);
            absenteeEntity.setIsAccepted(false);

            absenteeRepo.save(absenteeEntity);

            reportAttendance(user, false, true, false, false, false, false);
        }

        public void handleAbsenteeReqHalf(UserEntity entity) {
            AbsenteeReq req = new AbsenteeReq();
            req.setEmployeeId(entity.getEmployeeId());
            req.setUserId(entity.getUserId());
            req.setIsHalfDay(true);
            req.setHappenDate(getYesterdayDate());
            req.setComment("GOING HALF DAY WITH-OUT NOTIFYING");

            reportAbsent(req);

            reportAttendance(entity, false, true, false, false, false, true);
        }

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

                attendanceEntity.setIsHalfDay(true);
                attendanceEntity.setIssues(true);

                if (remaining_half_Day.getRemainingLeaves() < 1) { /// check are there any half days
                  /// No half days

                    attendanceEntity.setIssueDescription("GOING HALF DAY BUT REMAINING HALF DAY IS 0 SO GOING NO-PAY");

                    saveNoPayEntity(user, attendanceEntity, attendanceEntity.getIsHalfDay(),
                            attendanceEntity.getIsUnSuccessful(), attendanceEntity.getIsLate(),
                            attendanceEntity.getLateCover(), attendanceEntity.getIsAbsent(), attendanceEntity.getDate());
                } else {

                    attendanceEntity.setIssueDescription("GOING HALF DAY BEFORE PASS THE DUE DATE PLEASE RESOLVE IT");
                    attendanceEntity.setDueDateForUA(getDueDate());

                    /// there are half days
                    /// there are half days consider as UnSuccessful Leave ======================

                    AbsenteeReq req = new AbsenteeReq();
                    req.setEmployeeId(user.getEmployeeId());
                    req.setUserId(user.getUserId());
                    req.setIsHalfDay(true);
                    req.setHappenDate(attendanceEntity.getDate());
                    req.setComment("GOING HALF DAY WITH-OUT NOTIFYING");

                    reportAbsent(req);
                    reportAttendance(attendanceEntity, false, true, false, false, false, true);
                }

            } else {
                /// there are short leaves

                attendanceEntity.setIsShortLeave(true);
                attendanceEntity.setIssues(true);

                remaining_short_Leaves.setRemainingLeaves(remaining_short_Leaves.getRemainingLeaves() - 1);
                userLeaveCategoryRemainingRepo.save(remaining_short_Leaves);
                userRepo.save(user);
            }

            assert attendanceEntity != null;
            attendanceRepo.save(attendanceEntity);
        }
    }

}
