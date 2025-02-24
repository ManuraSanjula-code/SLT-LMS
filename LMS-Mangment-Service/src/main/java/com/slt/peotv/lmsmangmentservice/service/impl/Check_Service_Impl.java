package com.slt.peotv.lmsmangmentservice.service.impl;

import com.slt.peotv.lmsmangmentservice.entity.Absentee.AbsenteeEntity;
import com.slt.peotv.lmsmangmentservice.entity.Attendance.AttendanceEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveCategoryRemainingEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveCategoryTotalEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveTypeRemaining;
import com.slt.peotv.lmsmangmentservice.entity.archive.AttendanceEntity_;
import com.slt.peotv.lmsmangmentservice.entity.Leave.LeaveEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveCategoryEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveTypeEntity;
import com.slt.peotv.lmsmangmentservice.entity.MovementsEntity;
import com.slt.peotv.lmsmangmentservice.entity.NoPayEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.basic.RoleEntity;
import com.slt.peotv.lmsmangmentservice.entity.card.InOutEntity_;
import com.slt.peotv.lmsmangmentservice.exceptions.ErrorMessages;
import com.slt.peotv.lmsmangmentservice.model.LeaveReq;
import com.slt.peotv.lmsmangmentservice.model.MovementReq;
import com.slt.peotv.lmsmangmentservice.model.types.MovementType;
import com.slt.peotv.lmsmangmentservice.repository.*;
import com.slt.peotv.lmsmangmentservice.repository.archive.AttendanceRepo_;
import com.slt.peotv.lmsmangmentservice.repository.archive.InOutRepo_Archived;
import com.slt.peotv.lmsmangmentservice.service.Check_Service;
import com.slt.peotv.lmsmangmentservice.service.LMS_Service;
import com.slt.peotv.lmsmangmentservice.service.ServiceEvent;
import com.slt.peotv.lmsmangmentservice.utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
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

            UserLeaveCategoryRemainingEntity remaining_short_Leaves =
                    serviceEvent.getUserLeaveCategoryRemaining("SHORT_LEAVE", user.getUserId(), user.getEmployeeId());

            UserLeaveCategoryRemainingEntity remaining_half_Day =
                    serviceEvent.getUserLeaveCategoryRemaining("HALF_DAY", user.getUserId(), user.getEmployeeId());

            if (remaining_short_Leaves.getRemainingLeaves() < 1) { /// check are there any short leaves
            /// No short leaves

                if (remaining_half_Day.getRemainingLeaves() < 1) { /// check are there any half days
                /// No half days
                    saveNoPayEntity(user,true,false,false,false, false);
                } else {

                    /// there are half days
                    remaining_half_Day.setRemainingLeaves(remaining_half_Day.getRemainingLeaves() - 1);

                    userRepo.save(user);
                    if(attendanceEntity != null)
                     attendanceRepo.save(attendanceEntity);
                    userLeaveCategoryRemainingRepo.save(remaining_short_Leaves);
                }

            } else {
                /// there are short leaves

                remaining_short_Leaves.setRemainingLeaves(remaining_short_Leaves.getRemainingLeaves() - 1);
                userLeaveCategoryRemainingRepo.save(remaining_short_Leaves);
                userRepo.save(user);
                if(attendanceEntity != null)
                    attendanceRepo.save(attendanceEntity);
            }
        }
    }
    /// Process Leaves and request leaves ✅✅ ❌
    /// Process movement and request movement ❌✅
    /// Checking user have enough leaves ✅❌
    /// User NoPay System  ❌

    ///  What are the types of movements --- inform of a unauthorized swapping (unSuccessful attendance), no enough half days, or leaves, or shortLeaves,
    /// absent and no leaves , if employee is late forgot to do the late work ❌

    /// While request leaves make sure check are there any remaining leaves
    /// if employee is late 2 time consider as short-leave || make sure check that

    /// if employee is late 3rd time consider as half-day and employee keep doing this it will end all the half days and ---
    ///  ----- finally employee going nopay || make sure check that

    /// and employee absent because of certain reasons that will important but forgot to make leave he/she can make movement
    /// by default if employee forgot to make leave he/she can make movement that tell the reason other wise it will cut of your extra leaves and extra leaves
    /// is over you will employee to no-pay || same reason apply to late comers ( also system give certain time give you to make movement)
    /// some reason server is down it can't be absent
    ///
    /// ‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️ ---- if our server not store the records it will not sider as absesnt and
    ///
    /// 🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨 before main process start happen compare with slt main data with local data
    ///                                                                   determin the employee attedence
    ///
    /// How Movement Happens ---- (first system checking are there any leaves avalaible or not)
    ///                          1). if employee absent and also no enough half days, or leaves, or shortLeaves (he/she can req movement) -- no pay
    ///                          2). if employee is late to work (he/she can make a movement) -- no pay
    ///                          3). if employee make forgot to swipe or unauthorized swapping (unSuccessful attendance) -- he/she can make movement
    ///
    /// Movement Process --------
    ///                          1). if employee absent using making movement she/he preventing any issue
    ///                                 -- issue : if employee absent due to very valid reason it will not consider and employee leaves would not change
    ///                                          !! how but there is no leaves but reason is valid
    ///
    ///                          2). if employee is late to work using making movement she/he preventing any issue
    ///                                 -- issue : employee 2 day late it will marks as short leave but if employee again late it will consider as half day
    ///                                            making movement employee can tell the reason and get approval and work normally and employee short leave
    ///                                            half day remain the same
    ///
    ///                          3). if employee make forgot to swipe or unauthorized swapping he/she can make movement and waier otherwise it will going no-pay
    ///                                 -- conditions : employee has to make a movement certain time duration
    ///
    ///                         noted : employee can make a movement with in certain time duration other wise !! specially unauthorized people if they don't
    ///                                 it will mark as no-pay
    ///
    /// Movement process not accepted -----
    ///             1). if employee absent using making movement she/he preventing any issue but not accepted
    ///                  -- there are two raise conditions -> employee have enough leaves it will cut off from leaves
    ///                                                    -> no leaves it will consider as no-pay
    ///
    ///             2). if employee is late to work using making movement
    ///                  -- there four raise conditions -> 1st and 2nd days consider as short-leave and 3rd day consider as half day
    ///                                                  -> if there is no short leave
    ///                                                  -> if there are no half days
    ///                                                  -> if there no short leaves and half days --- it will going nopay
    ///
    ///             3). if employee make forgot to swipe or unauthorized swapping --> it will directly consider as no pay or $$ make leave <--|
    ///                    !! there is boolean call unSuccessful to indicate this !! ------------------------------------------------------|
    ///                     and same rules applies
    ///
    /// ---- when HOD or supervice approves leave or movement supervice or HOD(he/she) need see all the leaves and remaining leaves
    ///
    ///
    /// if employee request a leave and get accepted --
    ///                     in that day system need to dected the employee and high-ligth she/he already requst a leave but she/he came within day it will
    ///                     not consider as a leave 😂 but person late (it will consider as shortleave if employee repate another one day again system detec as
    ///                     short leave but in third day system consider as half day --- employee can waier by making movemnet)
    ///
    ///
    /// what if employee late going home :
    ///
    /// what if employee decied to take the lunch going to outside the bulding employee i need to punch the card, once he/she arrived again she/he punch the card
    ///
    /// what if there is no leaves but it's important (make a specail function to requst movement like that )
    /// ----------------------------------------------------  Calculate the final outcome ---------------------------------------------------------------------

    @Autowired
    private AttendanceRepo_ attendanceRepo_;

    @Autowired
    private AttendanceRepo attendanceRepo;
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
    public void requestMovement(MovementReq req) {

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

        movementsEntity.setIsAbsent(req.getAbsent());
        movementsEntity.setIsLate(req.getLate());
        movementsEntity.setIsLateCover(req.getLateCover());
        movementsEntity.setIsUnSuccessfulAttdate(req.getIsUnSuccessfulAttdate());
        movementsEntity.setIsPending(false);
        movementsEntity.setIsAccepted(false);
        movementsEntity.setIsExpired(false);
        lmsService.createMovements(movementsEntity);

    }

    /// Supervicer and HOD can see employee all the remain leaves and absents and nopays and movements 🔔🔔🔔🔔🔔🔔🔔🔔🔔🔔🔔🔔
    public void approvedMove(MovementsEntity entity) {
        UserEntity user = lmsService.getUserByEmployeeId(entity.getUser().getEmployeeId());
        MovementType movementType = entity.getMovementType();

        /// When Adding a due date make sure put extra 1 month 2 weeks

        if (movementType == MovementType.ABSENT) {
            ///  what is type employee should have if employee is absent
            ///  and check that leave type and check how many remain are there

            /// if there is no remaining leaves --> movement should be reject and it will consider as no pay (reject)
            /// if there is no remaining leaves --> reson is valid ???

            ///  there is remaining leaves --> reason is valid rollback (data changes)
            ///  there is remaining leaves --> reason is not valid rollback data not changing (becuase system automatically put absent) (reject)

        } else if (movementType == MovementType.LATEWORK) {
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

    public static NoPayEntity saveNoPayEntity(UserEntity user, Boolean isHalfDay, Boolean unSuccessful, Boolean isLate, Boolean isLateCover,Boolean isAbsent) {
        NoPayEntity nopayEntity = new NoPayEntity();
        nopayEntity.setUser(user);
        nopayEntity.setPublicId(utils.generateId(10));
        nopayEntity.setAcctual_date(new Date());
        nopayEntity.setSubmissionDate(new Date());
        nopayEntity.setIsHalfDay(isHalfDay);
        nopayEntity.setUnSuccessful(unSuccessful);
        nopayEntity.setIsLate(isLate);
        nopayEntity.setIsLateCover(isLateCover);
        nopayEntity.setIsLateCover(isLateCover);
        nopayEntity.setIsAbsent(isAbsent);
        nopayEntity = noPayRepo.save(nopayEntity);

        return nopayEntity;
    }
    @Override
    public void main() {


        /// Get All Attendance -----------------------------------------------------------------

        List<AttendanceEntity> yesterdayAttendance = attendanceRepo.findYesterdayAttendance();

        yesterdayAttendance.forEach(attendanceEntity -> {

            Boolean isLate = attendanceEntity.getIsLate();
            Boolean lateCover = attendanceEntity.getLateCover();
            Boolean unSuccessful = attendanceEntity.getUnSuccessful();
            Boolean isHalfDay = attendanceEntity.getIsHalfDay();

            // Convert Time objects to milliseconds
            long diffInMillis = attendanceEntity.getArrival_time().getTime() - attendanceEntity.getLeft_time().getTime();

            // 4 hours in milliseconds
            long fourHoursInMillis = 4 * 60 * 60 * 1000;

            if (diffInMillis == fourHoursInMillis) {
                System.out.println("The time difference is exactly 4 hours.");
                attendanceEntity.setHalfDay(true);
                isHalfDay = true;
            } else {
                System.out.println("The time difference is NOT exactly 4 hours.");
            }

            UserEntity user = userRepo.findByEmployeeId(attendanceEntity.getUser().getEmployeeId());
            if (user == null) return;

            if(isHalfDay){
                UserLeaveCategoryRemainingEntity remaining_half_Day =
                        serviceEvent.getUserLeaveCategoryRemaining("HALF_DAY", user.getUserId(), user.getEmployeeId());

                if (remaining_half_Day.getRemainingLeaves() < 1) { /// check are there any half days
                    /// No half days
                    saveNoPayEntity(user,true,false,false,false, false);
                } else {

                    /// there are half days
                    remaining_half_Day.setRemainingLeaves(remaining_half_Day.getRemainingLeaves() - 1);

                    userRepo.save(user);
                    attendanceRepo.save(attendanceEntity);
                    userLeaveCategoryRemainingRepo.save(remaining_half_Day);
                }
            }
            else if(isLate && !lateCover) {
                helper.handleLateAndUnsuccessful(user, attendanceEntity);
            } else if (isLate) {
                helper.handleLateAndUnsuccessful(user, attendanceEntity);
            } else if (unSuccessful) {
                helper.handleLateAndUnsuccessful(user, attendanceEntity);
            }
        });


        /// Get all the leaves  ---------------------------------------------------------------------------------
        /// -----------------------------------------------------------------------------------------------------
        /// -----------------------------------------------------------------------------------------------------

        /// if employee notify a half day but in that day he/she not going half day it will not consider as half days
        /// if employee notify a short leave day but in that day he/she not going short leave it will not consider as short leave
        /// if employee notify a leave day but in that day he/she not going leave it will not consider as leave


        /// if admin not approved the leaving request but he/she absent today it will cut off one leave but there is no leaves it will going nopay

        /// Get all the movements -------------------------------------------------------------------------------
        /// -----------------------------------------------------------------------------------------------------
        /// -----------------------------------------------------------------------------------------------------

        Iterable<MovementsEntity> all = movementsRepo.findOverdueEntities(new Date());

        List<MovementsEntity> filtered = StreamSupport.stream(all.spliterator(), false)
                .filter(entity -> Boolean.TRUE.equals(entity.getIsLate()) ||
                        Boolean.TRUE.equals(entity.getIsUnSuccessfulAttdate()) ||
                        Boolean.TRUE.equals(entity.getIsLateCover()) ||
                        Boolean.TRUE.equals(entity.getIsAbsent()))
                .collect(Collectors.toList());

        filtered.forEach(movement -> {
            if (!movement.getIsAccepted() && movement.getIsPending()) {
                ///  Check due date pass or not if pass movement expired and employee leaves might reduce and there is no leaves it will consider as nopay
                movement.setExpired(true);
                List<UserLeaveTypeRemaining> userLeaveCategoryRemaining = serviceEvent.getUserLeaveTypeRemaining(movement.getUser());

                List<UserLeaveTypeRemaining> filteredList = userLeaveCategoryRemaining.stream()
                        .filter(userLeaveTypeRemaining -> userLeaveTypeRemaining.getRemainingLeaves() < 1)
                        .collect(Collectors.toList());

                boolean allMatch = userLeaveCategoryRemaining.stream().allMatch(userLeaveTypeRemaining -> userLeaveTypeRemaining.getRemainingLeaves() < 1);

                if (allMatch) {
                    System.out.println("All elements have remainingLeaves < 1");

                    saveNoPayEntity(movement.getUser(),movement.getIsHalfDay(),
                            movement.getIsUnSuccessfulAttdate(),movement.getIsLate(),movement.getIsLateCover(),
                            movement.getIsAbsent());

                } else {
                    System.out.println("At least one element has remainingLeaves >= 1");
                }
            }

        });

        /// check the due date expire or not
        /// if employee is late he/she can make a movement with in certain time duration another wise employee leaves might reduce and there is no leaves it will consider as nopay
        /// if employee is unSuccessful he/she can make a movement with in certain time duration another wise employee leaves might reduce and there is no leaves it will consider as nopay
        /// if employee is absent he/she can make a movement with in certain time duration another wise employee leaves might reduce and there is no leaves it will consider as nopay

        /// Handle Absents --------------------------------------------------------------------------------------
        ///  -----------------------------------------------------------------------------------------------------
        ///  -----------------------------------------------------------------------------------------------------

        /// if employee is unSuccessful in today he/she can make a absent request
        /// if employee is half-day in today he/she can make a absent request


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
    public void reportAttendance(InOutEntity inout, Boolean fullday, Boolean unAuthorized, Boolean late, Boolean late_cover, Boolean half_day) {
        UserEntity userByEmployeeId = lmsService.getUserByEmployeeId(inout.getEmployeeID());

        if (userByEmployeeId == null) return;

        if (attendanceRepo.existsByUserAndDate(userByEmployeeId, getYesterdayDate())) return;


        AttendanceEntity attendance = new AttendanceEntity();
        attendance.setPublicId(utils.generateId(10));
        attendance.setUser(userByEmployeeId);
        attendance.setDate(inout.getDate());

        attendance.setLate(late);
        attendance.setLateCover(late_cover);
        attendance.setUnSuccessful(unAuthorized);
        attendance.setFullDay(fullday);
        attendance.setHalfDay(half_day);

        attendance.setArrival_date(inout.getPunchInMoa());
        attendance.setArrival_time(inout.getTimeMoa());
        attendance.setLeft_time(inout.getTimeEve());

        attendanceRepo.save(attendance);

    }

    @Override
    public void reportAbsent(List<InOutEntity> inout, List<UserEntity> absentEmployeesToday) {

    }

    @Override
    public void reportAbsent(InOutEntity inout, List<UserEntity> absentEmployeesToday) {

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

    @Override
    public void reportAbsent(List<UserEntity> absentEmployeesToday) {

        absentEmployeesToday.forEach(employee -> {

            UserEntity user = getUser(employee.getUserId(), employee.getEmployeeId());
            if (user == null) throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
            ;

            AbsenteeEntity absenteeEntity = new AbsenteeEntity();
            absenteeEntity.setPublicId(utils.generateId(10));
            absenteeEntity.setUser(user);
            absenteeEntity.setDate(new Date());
            absenteeEntity.setSwipeErr(false);
            absenteeEntity.setIsHODApproved(false);
            absenteeEntity.setIsSupervisedApproved(false);
            absenteeEntity.setAudited(0);
            absenteeEntity.setIsNoPay(0);

            absenteeEntity.setIsAbsent(false);
            absenteeEntity.setIsLate(false);
            absenteeEntity.setIsLateCover(false);
            absenteeEntity.setIsUnSuccessfulAttdate(false);
            absenteeEntity.setIsHalfDay(false);

            absenteeEntity.setIsPending(false);
            absenteeEntity.setIsAccepted(false);

            List<UserLeaveTypeRemaining> userLeaveCategoryRemaining = serviceEvent.getUserLeaveTypeRemaining(user);

            boolean allMatch = userLeaveCategoryRemaining.stream().allMatch(userLeaveTypeRemaining -> userLeaveTypeRemaining.getRemainingLeaves() < 1);

            if (allMatch) {
                System.out.println("All elements have remainingLeaves < 1");
                absenteeEntity.setIsNoPay(1);

                saveNoPayEntity(user,true,false,false,false, true);


            } else {
                absenteeEntity.setIsNoPay(0);
                System.out.println("At least one element has remainingLeaves >= 1");
                /// Cut of the leave category if employee is absent
            }
            absenteeRepo.save(absenteeEntity);

        });
    }

    @Override
    public void prerequisite() {
        Set<InOutEntity> employeesArrivedBefore830 = new HashSet<>(inOutRepo.findEmployeesBefore830(getYesterdayDate()));
        Set<InOutEntity> employeesLeftBetween500And530 = new HashSet<>(inOutRepo.findEmployeesLeavingBetween5And530(getYesterdayDate()));

        if (employeesArrivedBefore830.equals(employeesLeftBetween500And530)) {
            /// On Time Employees and full day
            Set<InOutEntity> commonEmployees = new HashSet<>(employeesArrivedBefore830);
            commonEmployees.retainAll(employeesLeftBetween500And530);

            for (InOutEntity commonEmployee : commonEmployees)
                reportAttendance(commonEmployee, true, false, false, false, false);

        } else {
            /// UnSuccessful or UnAuthorized employees
            for (InOutEntity employee : employeesArrivedBefore830)
                reportAttendance(employee, false, true, false, false, false);
        }

        // =======================================================================================================

        Set<InOutEntity> employeesArrivedBetween830And900 = new HashSet<>(inOutRepo.findEmployeesBetween830And9(getYesterdayDate()));
        Set<InOutEntity> employeesCoveredLateHours = new HashSet<>(inOutRepo.findEmployeesCoveredLateHoursYesterday(getYesterdayDate()));

        if (employeesArrivedBetween830And900.equals(employeesCoveredLateHours)) {
            ///  Late Employees and cover their work

            Set<InOutEntity> commonEmployees = new HashSet<>(employeesArrivedBefore830);
            commonEmployees.retainAll(employeesLeftBetween500And530);

            for (InOutEntity commonEmployee : commonEmployees)
                reportAttendance(commonEmployee, false, false, true, true, false);

        } else {
            /// Late employees those who not cover late work
            for (InOutEntity employee : employeesArrivedBefore830)
                reportAttendance(employee, false, false, true, false, false);
        }

        List<UserEntity> absentEmployeesToday = inOutRepo.findAbsentEmployeesYesterday(); /// Absent employees
        reportAbsent(absentEmployeesToday);
    }

    private UserLeaveTypeRemaining getUserLeaveTypeRemaining(String name, UserEntity user) {
        return serviceEvent.getUserLeaveTypeRemaining(name, user.getUserId(), user.getEmployeeId());
    }

    /// If employee make leave but he/she have no leaves it leave not accepting by admin but she/he absent it will consider sa no pay
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

            LeaveEntity leaveEntity = new LeaveEntity();
            leaveEntity.setPublicId(utils.generateId(10));
            leaveEntity.setSubmitDate(new Date());

            leaveEntity.setFromDate(req.getFromDate());
            leaveEntity.setToDate(req.getToDate());

            leaveEntity.setLeaveCategory(leaveCategory);
            leaveEntity.setLeaveType(leaveType);

            leaveEntity.setIsSupervisedApproved(false);
            leaveEntity.setIsHODApproved(false);
            leaveEntity.setIsHalfDay(req.getHalfDay());
            leaveEntity.setNumOfDays(req.getNumOfDays());
            leaveEntity.setDescription(req.getDescription());

            lmsService.saveLeave(leaveEntity);

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
    public void processLeaveBySup(String superId, String leaveId) { /// Process All leaves and select certain leave and process , adding soon
    /// Supervisor by using his/her id and get particular leave accept it using leaveId
        LeaveEntity entity = lmsService.getOneLeave(leaveId);
        UserEntity employee = lmsService.getUserByEmployeeId(superId);

        if (employee == null || entity == null) return;

        if (hasRole(employee.getRoles(), "SUPERVISOR")) {
            approvedLeaveBySup(entity);
        }
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

        if (hasRole(employee.getRoles(), "SUPERVISOR"))
            allLeaveByIds.forEach(this::approvedLeaveByHOD);
    }


    @Override
    public void getAllTheInOutRecordsFromSLT() {
        /// First get the all the data and using employee id query the our local database
    }

    /// Request a movement for absent ( certain period of time ) other wise it consider as leave ||
    /// 1st year employee has no leaves and 2nd year has levaes but under certain regulations 3rd year employee have no regulations
}
