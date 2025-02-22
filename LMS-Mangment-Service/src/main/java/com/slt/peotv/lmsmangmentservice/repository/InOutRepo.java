package com.slt.peotv.lmsmangmentservice.repository;

import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import com.slt.peotv.lmsmangmentservice.entity.card.InOutEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface InOutRepo extends CrudRepository<InOutEntity, Long> {

    // Get all records based on employeeID
    List<InOutEntity> findByEmployeeID(String employeeID);

    // Get the earliest pushIn for the current date (using pushInTime)
    // 1. Get current date InOut using employeeID and userId
    @Query("SELECT io FROM InOutEntity io WHERE io.employeeID = ?1 AND io.userId = ?2 AND DATE(io.pushInOut) = CURRENT_DATE")
    List<InOutEntity> findByEmployeeIDAndUserIdToday(String employeeID, String userId);

    // 2. Get InOut data for a specific duration (year, month, date)
    @Query("SELECT io FROM InOutEntity io WHERE io.employeeID = ?1 AND io.userId = ?2 AND io.pushInOut BETWEEN ?3 AND ?4")
    List<InOutEntity> findByEmployeeIDAndUserIdBetweenDates(String employeeID, String userId, LocalDate startDate, LocalDate endDate);

    // 3. Get employees who arrived before 8:30 AM today
    @Query("SELECT io FROM InOutEntity io WHERE io.pushInOut = CURRENT_DATE AND io.time < '08:30:00'")
    List<InOutEntity> findEmployeesArrivedBefore830();

    // 4. Get employees who arrived between 8:30 AM - 9:00 AM today
    @Query("SELECT io FROM InOutEntity io WHERE io.pushInOut = CURRENT_DATE AND io.time BETWEEN '08:30:00' AND '09:00:00'")
    List<InOutEntity> findEmployeesArrivedBetween830And900();

    // 5. Get employees who left between 5:00 PM - 5:30 PM today
    @Query("SELECT io FROM InOutEntity io WHERE io.pushInOut = CURRENT_DATE AND io.time BETWEEN '17:00:00' AND '17:30:00'")
    List<InOutEntity> findEmployeesLeftBetween500And530();

    // 6. Get employees who arrived between 8:30 AM - 9:00 AM and covered their late hours (worked an additional 30 minutes)
    @Query("SELECT io FROM InOutEntity io WHERE io.pushInOut = CURRENT_DATE AND io.time BETWEEN '08:30:00' AND '09:00:00' AND io.time + INTERVAL 30 MINUTE >= '17:30:00'")
    List<InOutEntity> findEmployeesCoveredLateHours();

    // 7a. Check if employee punched before 8:30 AM today
    @Query("SELECT CASE WHEN COUNT(io) > 0 THEN TRUE ELSE FALSE END FROM InOutEntity io WHERE io.employeeID = ?1 AND io.userId = ?2 AND io.pushInOut = CURRENT_DATE AND io.time < '08:30:00'")
    boolean didEmployeePunchBefore830(String employeeID, String userId);

    // 7b. Check if employee punched between 5:00 PM - 5:30 PM today
    @Query("SELECT CASE WHEN COUNT(io) > 0 THEN TRUE ELSE FALSE END FROM InOutEntity io WHERE io.employeeID = ?1 AND io.userId = ?2 AND io.pushInOut = CURRENT_DATE AND io.time BETWEEN '17:00:00' AND '17:30:00'")
    boolean didEmployeePunchBetween500And530(String employeeID, String userId);

    // 7c. Check if employee covered their late hours
    @Query("SELECT CASE WHEN COUNT(io) > 0 THEN TRUE ELSE FALSE END FROM InOutEntity io WHERE io.employeeID = ?1 AND io.userId = ?2 AND io.pushInOut = CURRENT_DATE AND io.time BETWEEN '08:30:00' AND '09:00:00' AND io.time + INTERVAL 30 MINUTE >= '17:30:00'")
    boolean didEmployeeCoverLateHours(String employeeID, String userId);

    @Query("SELECT u FROM UserEntity u WHERE u.employeeId NOT IN (SELECT io.employeeID FROM InOutEntity io WHERE DATE(io.pushInOut) = CURRENT_DATE)")
    List<UserEntity> findAbsentEmployeesToday();


    @Query("SELECT u FROM UserEntity u WHERE u.employeeId NOT IN (SELECT io.employeeID FROM InOutEntity io WHERE DATE(io.pushInOut) = CURRENT_DATE - INTERVAL 1 DAY)")
    List<UserEntity> findAbsentEmployeesYesterday();

    @Query("SELECT i FROM InOutEntity i WHERE i.employeeID = :employeeID AND i.userId = :userId AND DATE(i.pushInOut) = DATE(:yesterday)")
    InOutEntity findCurrentDayInOut(@Param("employeeID") String employeeID, @Param("userId") String userId, @Param("yesterday") Date yesterday);

    @Query("SELECT i FROM InOutEntity i WHERE DATE(i.pushInOut) BETWEEN DATE(:startDate) AND DATE(:endDate)")
    List<InOutEntity> findByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT i FROM InOutEntity i WHERE DATE(i.pushInOut) = DATE(:yesterday) AND i.time <= '08:30:00'")
    List<InOutEntity> findEmployeesBefore830(@Param("yesterday") Date yesterday);

    @Query("SELECT i FROM InOutEntity i WHERE DATE(i.pushInOut) = DATE(:yesterday) AND i.time > '08:30:00' AND i.time <= '09:00:00'")
    List<InOutEntity> findEmployeesBetween830And9(@Param("yesterday") Date yesterday);

    @Query("SELECT i FROM InOutEntity i WHERE DATE(i.pushInOut) = DATE(:yesterday) AND i.time BETWEEN '17:00:00' AND '17:30:00'")
    List<InOutEntity> findEmployeesLeavingBetween5And530(@Param("yesterday") Date yesterday);

    @Query("SELECT i FROM InOutEntity i WHERE DATE(i.pushInOut) = DATE(:yesterday) AND i.time > '08:30:00' AND i.time <= '09:00:00' AND i.InOut = true")
    List<InOutEntity> findLateEmployeesWithCoveredTime(@Param("yesterday") Date yesterday);

    @Query("SELECT io FROM InOutEntity io WHERE DATE(i.pushInOut) = DATE(:yesterday) io.time BETWEEN '08:30:00' AND '09:00:00' AND io.time + INTERVAL 30 MINUTE >= '17:30:00'")
    List<InOutEntity> findEmployeesCoveredLateHoursYesterday(@Param("yesterday") Date yesterday);

    @Query("SELECT COUNT(i) > 0 FROM InOutEntity i WHERE i.employeeID = :employeeID AND i.userId = :userId AND DATE(i.pushInOut) = DATE(:yesterday) AND i.time <= '08:30:00'")
    boolean didEmployeeArriveBefore830(@Param("employeeID") String employeeID, @Param("userId") String userId, @Param("yesterday") Date yesterday);

    @Query("SELECT COUNT(i) > 0 FROM InOutEntity i WHERE i.employeeID = :employeeID AND i.userId = :userId AND DATE(i.pushInOut) = DATE(:yesterday) AND i.time BETWEEN '17:00:00' AND '17:30:00'")
    boolean didEmployeeLeaveBetween5And530(@Param("employeeID") String employeeID, @Param("userId") String userId, @Param("yesterday") Date yesterday);

    @Query("SELECT COUNT(i) > 0 FROM InOutEntity i WHERE i.employeeID = :employeeID AND i.userId = :userId AND DATE(i.pushInOut) = DATE(:yesterday) AND i.time > '08:30:00' AND i.time <= '09:00:00' AND NOT i.InOut")
    boolean didEmployeeNotCoverLateTime(@Param("employeeID") String employeeID, @Param("userId") String userId, @Param("yesterday") Date yesterday);
}

