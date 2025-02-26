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
import java.util.Optional;

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


    @Query("SELECT i FROM InOutEntity i WHERE i.date = CURRENT_DATE - 1")
    List<InOutEntity> findYesterdayInOutRecords();

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

    @Query("SELECT i FROM InOutEntity i WHERE DATE(i.pushInOut) = DATE(:yesterday) AND i.time BETWEEN '17:00:00' AND '23:59:00'")
    List<InOutEntity> findEmployeesLeavingBetween5And530_(@Param("yesterday") Date yesterday);

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

    Optional<InOutEntity> findInOutByUserAndDate(@Param("userId") String userId, @Param("date") Date date);

    @Query("SELECT i FROM InOutEntity i WHERE i.date = :yesterday " +
            "AND i.timeMoa < '08:30:00' AND i.isEvening = false")
    List<InOutEntity> findMorningPunchOnly(@Param("yesterday") Date yesterday);

    @Query("SELECT i FROM InOutEntity i WHERE i.date = :yesterday " +
            "AND i.timeEve BETWEEN '17:00:00' AND '23:59:59' AND i.isMoaning = false")
    List<InOutEntity> findEveningPunchOnly(@Param("yesterday") Date yesterday);

    /// ==========

    // Employees who did not arrive before 8:30 AM
    @Query("SELECT i FROM InOutEntity i WHERE i.date = :yesterday AND i.timeMoa IS NULL")
    List<InOutEntity> findEmployeesNotArrivedBefore830AM(@Param("yesterday") Date yesterday);

    // Employees who arrived between 8:30 AM - 9:00 AM
    @Query("SELECT i FROM InOutEntity i WHERE i.date = :yesterday AND i.timeMoa BETWEEN '08:30:00' AND '09:00:00'")
    List<InOutEntity> findEmployeesArrivedBetween830And900AM(@Param("yesterday") Date yesterday);

    // Employees who arrived between 10:00 AM - 11:59 AM
    @Query("SELECT i FROM InOutEntity i WHERE i.date = :yesterday AND i.timeMoa BETWEEN '10:00:00' AND '11:59:59'")
    List<InOutEntity> findEmployeesArrivedBetween10AMAnd1159AM(@Param("yesterday") Date yesterday);

    // Employees who arrived between 12:00 PM - 2:00 PM
    @Query("SELECT i FROM InOutEntity i WHERE i.date = :yesterday AND i.timeMoa BETWEEN '12:00:00' AND '14:00:00'")
    List<InOutEntity> findEmployeesArrivedBetween12PMAnd2PM(@Param("yesterday") Date yesterday);

    // Employees who arrived between 2:00 PM - 5:00 PM
    @Query("SELECT i FROM InOutEntity i WHERE i.date = :yesterday AND i.timeMoa BETWEEN '14:00:00' AND '17:00:00'")
    List<InOutEntity> findEmployeesArrivedBetween2PMAnd5PM(@Param("yesterday") Date yesterday);

    /// ===========

    // Employees who arrived late (after 8:30 AM) but before 9:00 AM
    @Query("SELECT i FROM InOutEntity i WHERE i.date = :yesterday AND i.timeMoa BETWEEN '08:30:00' AND '09:00:00'")
    List<InOutEntity> findEmployeesArrivedBetween830And900AM_1(@Param("yesterday") Date yesterday);

    // Employees who arrived late (after 9:00 AM)
    @Query("SELECT i FROM InOutEntity i WHERE i.date = :yesterday AND i.timeMoa > '09:00:00'")
    List<InOutEntity> findEmployeesArrivedLateAfter9AM(@Param("yesterday") Date yesterday);

    // Employees who arrived after 12:30 PM (more than 4 hours late) → Half-day policy applies
    @Query("SELECT i FROM InOutEntity i WHERE i.date = :yesterday AND i.timeMoa > '12:30:00'")
    List<InOutEntity> findEmployeesHalfDay(@Param("yesterday") Date yesterday);

    // Employees who arrived late but covered their extra work time
    @Query("SELECT i FROM InOutEntity i WHERE i.date = :yesterday AND i.timeMoa > '08:30:00' AND TIMESTAMPDIFF(MINUTE, i.timeEve, '17:30:00') >= TIMESTAMPDIFF(MINUTE, '08:30:00', i.timeMoa)")
    List<InOutEntity> findEmployeesWhoCoveredLateTime(@Param("yesterday") Date yesterday);

    // Employees who arrived late and did NOT cover their late work time
    @Query("SELECT i FROM InOutEntity i WHERE i.date = :yesterday AND i.timeMoa > '08:30:00' AND TIMESTAMPDIFF(MINUTE, i.timeEve, '17:30:00') < TIMESTAMPDIFF(MINUTE, '08:30:00', i.timeMoa)")
    List<InOutEntity> findEmployeesWhoDidNotCoverLateTime(@Param("yesterday") Date yesterday);

}

