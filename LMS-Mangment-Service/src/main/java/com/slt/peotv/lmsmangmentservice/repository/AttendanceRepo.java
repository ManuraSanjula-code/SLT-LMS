package com.slt.peotv.lmsmangmentservice.repository;

import com.slt.peotv.lmsmangmentservice.entity.Attendance.AttendanceEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepo extends CrudRepository<AttendanceEntity, Long> {
    List<AttendanceEntity> findByUser(UserEntity user);
    Optional<AttendanceEntity> findByPublicId(String publicId);
    boolean existsByUserAndDate(UserEntity user, Date date);
    Optional<AttendanceEntity> findByUserAndDate(UserEntity user, Date date);
    List<AttendanceEntity> findByIsAbsentTrue();
    List<AttendanceEntity> findByIsHalfDayTrue();
    List<AttendanceEntity> findByIsUnSuccessfulTrue();
    List<AttendanceEntity> findByIsFullDayTrue();
    List<AttendanceEntity> findByIsLateTrue();
    List<AttendanceEntity> findByIsLateTrueAndLateCoverFalse();
    List<AttendanceEntity> findByDate(Date date);
    List<AttendanceEntity> findByDateBetween(Date startDate, Date endDate);
    List<AttendanceEntity> findByUser_Id(Long userId);
    List<AttendanceEntity> findByDateAndArrivalTimeBetween(Date date, Time startTime, Time endTime);

    @Query("SELECT a FROM AttendanceEntity a WHERE a.date = CURRENT_DATE - 1")
    List<AttendanceEntity> findYesterdayAttendance();

    // Get attendance for today
    @Query("SELECT a FROM AttendanceEntity a WHERE a.date = CURRENT_DATE")
    List<AttendanceEntity> findTodayAttendance();

    // Get attendance within a specific time period
    @Query("SELECT a FROM AttendanceEntity a WHERE a.date BETWEEN :startDate AND :endDate")
    List<AttendanceEntity> findAttendanceByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
