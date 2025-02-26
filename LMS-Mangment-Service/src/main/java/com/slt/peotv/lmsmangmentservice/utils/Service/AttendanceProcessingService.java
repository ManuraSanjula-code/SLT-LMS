package com.slt.peotv.lmsmangmentservice.utils.Service;

import com.slt.peotv.lmsmangmentservice.entity.Leave.LeaveEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveCategoryRemainingEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveTypeRemaining;
import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveTypeEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import com.slt.peotv.lmsmangmentservice.entity.card.InOutEntity;
import com.slt.peotv.lmsmangmentservice.repository.InOutRepo;
import com.slt.peotv.lmsmangmentservice.repository.LeaveRepo;
import com.slt.peotv.lmsmangmentservice.repository.UserLeaveCategoryRemainingRepo;
import com.slt.peotv.lmsmangmentservice.repository.UserLeaveTypeRemainingRepo;
import com.slt.peotv.lmsmangmentservice.service.ServiceEvent;
import com.slt.peotv.lmsmangmentservice.service.impl.Check_Service_Impl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceProcessingService {

    @Autowired
    private LeaveRepo leaveRepository;
    @Autowired
    private InOutRepo inOutRepository;
    @Autowired
    private ServiceEvent serviceEvent;
    @Autowired
    private UserLeaveTypeRemainingRepo userLeaveTypeRemainingRepo;
    @Autowired
    private UserLeaveCategoryRemainingRepo userLeaveCategoryRemainingRepo;
    @Autowired
    private Check_Service_Impl checkService;

    @Transactional
    public void processEmployeeLeave(Long userId, Date processDate, Boolean isProved) {
        List<LeaveEntity> activeLeaves = leaveRepository.findActiveLeaveByUserAndDate(userId, processDate);

        if (activeLeaves.isEmpty()) {
            return; // No leave to process
        }

        Optional<InOutEntity> attendanceRecord = inOutRepository.findInOutByUserAndDate(userId.toString(), processDate);

        for (LeaveEntity leave : activeLeaves) {
            if (attendanceRecord.isPresent()) {
                InOutEntity inOut = attendanceRecord.get();
                UserEntity user = leave.getUser();

                boolean isLate = checkLateArrival(inOut);
                boolean isShortLeave = checkShortLeave(inOut);
                boolean isHalfDay = checkHalfDay(inOut);
                boolean isFullDayAttendance = checkFullAttendance(inOut);

                UserLeaveCategoryRemainingEntity remaining_half_Day =
                        serviceEvent.getUserLeaveCategoryRemaining("HALF_DAY", user.getUserId(), user.getEmployeeId());
                UserLeaveCategoryRemainingEntity remaining_short_Leaves =
                        serviceEvent.getUserLeaveCategoryRemaining("SHORT_LEAVE", user.getUserId(), user.getEmployeeId());

                if (isFullDayAttendance) {
                    leave.setNotUsed(true); // Employee attended fully, so leave is not used.
                } else if (isHalfDay) {

                    /// leave will be canceled and another unAuthorized leave will pop up
                    checkService.saveLeave(user, processDate);

                    /*leave.setNotUsed(true);
                    leave.setIsHalfDay(true);

                    if (remaining_half_Day.getRemainingLeaves() < 1) {
                        remaining_half_Day.setRemainingLeaves(remaining_half_Day.getRemainingLeaves() - 1);
                        userLeaveCategoryRemainingRepo.save(remaining_half_Day);
                    }else{
                        if(leave.getUser() != null)
                            Check_Service_Impl.saveNoPayEntity(leave.getUser(), true, false, false, false, false);
                    }*/


                } else if (isShortLeave) {
                    leave.setNotUsed(true);
                    leave.setIsShort_Leave(true);
                    if (remaining_short_Leaves.getRemainingLeaves() < 1) {
                        remaining_short_Leaves.setRemainingLeaves(remaining_short_Leaves.getRemainingLeaves() - 1);
                        userLeaveCategoryRemainingRepo.save(remaining_short_Leaves);
                    }else{
                        if (remaining_half_Day.getRemainingLeaves() < 1) {

                            /// unAuthorized leave will pop up
                            checkService.saveLeave(user, processDate);

                        }else{
                            if(leave.getUser() != null)
                                Check_Service_Impl.saveNoPayEntity(leave.getUser(), true, false, false, false, false);
                        }
                    }

                    leave.setDescription("Short Leave Used");

                } else if (isLate) {
                    leave.setNotUsed(true);
                    leave.setDescription("Late but did not cover hours");

                    /// unAuthorized leave will pop up
                    checkService.saveLeave(user, processDate);

                }

            } else {
                leave.setNotUsed(false);
                // No attendance record found → Employee was absent
                leave.setDescription("Absent - Leave Used");
                LeaveTypeEntity leaveType = leave.getLeaveType();

                UserEntity user = leave.getUser();
                if (user != null)
                    switch (leaveType.getName()) {
                        case "CASUAL" -> {
                            UserLeaveTypeRemaining casual = getUserLeaveTypeRemaining("CASUAL", user);
                            if (casual.getRemainingLeaves() < 1) {
                                casual.setRemainingLeaves(casual.getRemainingLeaves() - 1);
                                userLeaveTypeRemainingRepo.save(casual);
                            }else{
                                Check_Service_Impl.saveNoPayEntity(leave.getUser(), false, false, false, false, true);
                            }
                        }
                        case "ANNUAL" -> {
                            UserLeaveTypeRemaining annual = getUserLeaveTypeRemaining("ANNUAL", user);
                            if (annual.getRemainingLeaves() < 1) {
                                annual.setRemainingLeaves(annual.getRemainingLeaves() - 1);
                                userLeaveTypeRemainingRepo.save(annual);
                            }else{
                                Check_Service_Impl.saveNoPayEntity(leave.getUser(), false, false, false, false, true);
                            }
                        }
                        case "SICK" -> {
                            UserLeaveTypeRemaining sick = getUserLeaveTypeRemaining("SICK", user);
                            if (sick.getRemainingLeaves() < 1) {
                                sick.setRemainingLeaves(sick.getRemainingLeaves() - 1);
                                userLeaveTypeRemainingRepo.save(sick);
                            }else{
                                Check_Service_Impl.saveNoPayEntity(leave.getUser(), false, false, false, false, true);
                            }
                        }
                        case "SPECIAL" -> {
                            UserLeaveTypeRemaining special = getUserLeaveTypeRemaining("SPECIAL", user);
                            if (special.getRemainingLeaves() < 1) {
                                special.setRemainingLeaves(special.getRemainingLeaves() - 1);
                                userLeaveTypeRemainingRepo.save(special);
                            }else{
                                Check_Service_Impl.saveNoPayEntity(leave.getUser(), false, false, false, false, true);
                            }
                        }
                        case "DUTY" -> {
                            UserLeaveTypeRemaining duty = getUserLeaveTypeRemaining("DUTY", user);
                            if (duty.getRemainingLeaves() < 1) {
                                duty.setRemainingLeaves(duty.getRemainingLeaves() - 1);
                                userLeaveTypeRemainingRepo.save(duty);
                            }else{
                                Check_Service_Impl.saveNoPayEntity(leave.getUser(), false, false, false, false, true);
                            }
                        }
                        case "MATERNITY_LEAVE" -> {
                            UserLeaveTypeRemaining maternityLeave = getUserLeaveTypeRemaining("MATERNITY_LEAVE", user);
                            if (maternityLeave.getRemainingLeaves() < 1) {
                                maternityLeave.setRemainingLeaves(maternityLeave.getRemainingLeaves() - 1);
                                userLeaveTypeRemainingRepo.save(maternityLeave);
                            }else{
                                Check_Service_Impl.saveNoPayEntity(leave.getUser(), false, false, false, false, true);
                            }
                        }
                        default -> {
                            throw new IllegalArgumentException("Invalid leave type: " + leaveType.getName());
                        }
                    }
                else
                    return;
            }

            leaveRepository.save(leave);
        }
    }

    private UserLeaveTypeRemaining getUserLeaveTypeRemaining(String name, UserEntity user) {
        return serviceEvent.getUserLeaveTypeRemaining(name, user.getUserId(), user.getEmployeeId());
    }


    private boolean checkLateArrival(InOutEntity inOut) {
        return inOut.getPunchInMoa() != null && inOut.getTimeMoa().after(Time.valueOf("09:00:00"));
    }

    private boolean checkShortLeave(InOutEntity inOut) {
        return inOut.getTimeEve() != null && inOut.getTimeEve().before(Time.valueOf("16:00:00"));
    }

    private boolean checkHalfDay(InOutEntity inOut) {
        return (inOut.getTimeMoa() != null && inOut.getTimeEve() == null) ||
                (inOut.getTimeMoa() == null && inOut.getTimeEve() != null);
    }

    private boolean checkFullAttendance(InOutEntity inOut) {
        return inOut.getTimeMoa() != null && inOut.getTimeEve() != null;
    }
}

