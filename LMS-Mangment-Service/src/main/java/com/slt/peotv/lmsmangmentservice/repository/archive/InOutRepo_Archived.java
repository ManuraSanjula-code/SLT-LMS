package com.slt.peotv.lmsmangmentservice.repository.archive;

import com.slt.peotv.lmsmangmentservice.entity.card.InOutEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface InOutRepo_Archived extends CrudRepository<InOutEntity, Long> {

    List<InOutEntity> findByEmployeeID(String employeeID);

    @Query("SELECT i FROM InOutEntity i WHERE i.employeeID = :employeeID AND i.isMoaning = true AND DATE(i.pushInOut) = DATE(:date) ORDER BY i.time ASC LIMIT 1")
    Optional<InOutEntity> findEarliestMorningPunch(String employeeID, Date date);

    // Get latest evening punch record for a specific date
    @Query("SELECT i FROM InOutEntity i WHERE i.employeeID = :employeeID AND i.isEvening = true AND DATE(i.pushInOut) = DATE(:date) ORDER BY i.time DESC LIMIT 1")
    Optional<InOutEntity> findLatestEveningPunch(String employeeID, Date date);

    // Get records before 8:30 AM on a specific date
    @Query("SELECT i FROM InOutEntity i WHERE i.employeeID = :employeeID AND i.time < '08:30:00' AND DATE(i.pushInOut) = DATE(:date)")
    List<InOutEntity> findPunchesBefore830AM(String employeeID, Date date);

    // Get records between 5:00 PM and 5:29 PM on a specific date
    @Query("SELECT i FROM InOutEntity i WHERE i.employeeID = :employeeID AND i.time BETWEEN '17:00:00' AND '17:29:00' AND DATE(i.pushInOut) = DATE(:date)")
    List<InOutEntity> findPunchesBetween5PMAnd529PM(String employeeID, Date date);

    // Get records after 9:00 AM on a specific date
    @Query("SELECT i FROM InOutEntity i WHERE i.employeeID = :employeeID AND i.time > '09:00:00' AND DATE(i.pushInOut) = DATE(:date)")
    List<InOutEntity> findPunchesAfter9AM(String employeeID, Date date);

    // Get records between 8:30 AM and 9:00 AM on a specific date
    @Query("SELECT i FROM InOutEntity i WHERE i.employeeID = :employeeID AND i.time BETWEEN '08:30:00' AND '09:00:00' AND DATE(i.pushInOut) = DATE(:date)")
    List<InOutEntity> findPunchesBetween830AMAnd9AM(String employeeID, Date date);

    // Check if employee arrived exactly at 8:30 AM and left between 5:00 - 5:30 PM on a specific date
    @Query("SELECT CASE WHEN COUNT(i) > 1 THEN true ELSE false END " +
            "FROM InOutEntity i " +
            "WHERE i.employeeID = :employeeID " +
            "AND i.time = '08:30:00' " +
            "AND EXISTS (SELECT 1 FROM InOutEntity e WHERE e.employeeID = i.employeeID AND e.time BETWEEN '17:00:00' AND '17:30:00' AND DATE(e.pushInOut) = DATE(:date)) " +
            "AND DATE(i.pushInOut) = DATE(:date)")
    boolean checkEmployeeCameOnTimeAndLeftOnTime(String employeeID, Date date); //🚨🚨

    // Check if employee worked full-day (8 hours 30 minutes) on a specific date
    @Query("SELECT CASE WHEN SUM(TIMESTAMPDIFF(MINUTE, MIN(i.time), MAX(i.time))) >= 510 THEN true ELSE false END " +
            "FROM InOutEntity i WHERE i.employeeID = :employeeID AND DATE(i.pushInOut) = DATE(:date)")
    boolean isFullDayWorker(String employeeID, Date date); //🚨🚨

    // Check if employee worked half-day (4 hours) on a specific date
    @Query("SELECT CASE WHEN SUM(TIMESTAMPDIFF(MINUTE, MIN(i.time), MAX(i.time))) >= 240 " +
            "AND SUM(TIMESTAMPDIFF(MINUTE, MIN(i.time), MAX(i.time))) < 510 THEN true ELSE false END " +
            "FROM InOutEntity i WHERE i.employeeID = :employeeID AND DATE(i.pushInOut) = DATE(:date)")
    boolean isHalfDayWorker(String employeeID, Date date); //🚨🚨

    // Check if employee covered additional 30 minutes after being late on a specific date
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END " +
            "FROM InOutEntity i " +
            "WHERE i.employeeID = :employeeID " +
            "AND TIMESTAMPDIFF(MINUTE, (SELECT MIN(e.time) FROM InOutEntity e WHERE e.employeeID = i.employeeID AND DATE(e.pushInOut) = DATE(:date)), " +
            "(SELECT MAX(e.time) FROM InOutEntity e WHERE e.employeeID = i.employeeID AND DATE(e.pushInOut) = DATE(:date))) >= 540 " +
            "AND DATE(i.pushInOut) = DATE(:date)")
    boolean didEmployeeCover30Minutes(String employeeID, Date date); //🚨🚨🚨

    // Earliest morning punch for a specific date
    @Query("SELECT i FROM InOutEntity i WHERE i.userId = :userId AND DATE(i.pushInOut) = :date AND FUNCTION('HOUR', i.time) < 12 ORDER BY i.time ASC LIMIT 1")
    Optional<InOutEntity> findEarliestMorningPunch_(String userId, Date date);

    // Latest morning punch for a specific date
    @Query("SELECT i FROM InOutEntity i WHERE i.userId = :userId AND DATE(i.pushInOut) = :date AND FUNCTION('HOUR', i.time) < 12 ORDER BY i.time DESC LIMIT 1")
    Optional<InOutEntity> findLatestMorningPunch(String userId, Date date); //🚨

    // Earliest evening punch for a specific date
    @Query("SELECT i FROM InOutEntity i WHERE i.userId = :userId AND DATE(i.pushInOut) = :date AND FUNCTION('HOUR', i.time) >= 12 ORDER BY i.time ASC LIMIT 1")
    Optional<InOutEntity> findEarliestEveningPunch(String userId, Date date); //🚨

    // Latest evening punch for a specific date
    @Query("SELECT i FROM InOutEntity i WHERE i.userId = :userId AND DATE(i.pushInOut) = :date AND FUNCTION('HOUR', i.time) >= 12 ORDER BY i.time DESC LIMIT 1")
    Optional<InOutEntity> findLatestEveningPunch_(String userId, Date date);

    // Get punch records for today
    @Query("SELECT i FROM InOutEntity i WHERE i.userId = :userId AND DATE(i.pushInOut) = CURRENT_DATE")
    List<InOutEntity> findTodayPunchRecords(String userId);

    // Get punch records for a specific date
    @Query("SELECT i FROM InOutEntity i WHERE i.userId = :userId AND DATE(i.pushInOut) = :date")
    List<InOutEntity> findPunchRecordsByDate(String userId, Date date);

    // Get punch records for a specific week or year
    @Query("SELECT i FROM InOutEntity i WHERE i.userId = :userId AND FUNCTION('YEAR', i.pushInOut) = :year AND FUNCTION('WEEK', i.pushInOut) = :week")
    List<InOutEntity> findPunchRecordsByWeek(String userId, int year, int week);

    @Query("SELECT i FROM InOutEntity i WHERE i.userId = :userId AND FUNCTION('YEAR', i.pushInOut) = :year")
    List<InOutEntity> findPunchRecordsByYear(String userId, int year);

    // Check if employee punched in before 8:30 AM (for a specific date)
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN TRUE ELSE FALSE END FROM InOutEntity i WHERE i.userId = :userId AND DATE(i.pushInOut) = :date AND (FUNCTION('HOUR', i.time) < 8 OR (FUNCTION('HOUR', i.time) = 8 AND FUNCTION('MINUTE', i.time) < 30))")
    boolean hasPunchedBefore830AM(String userId, Date date); //🚨

    // Check if employee punched out between 5:00 PM - 5:29 PM
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN TRUE ELSE FALSE END FROM InOutEntity i WHERE i.userId = :userId AND DATE(i.pushInOut) = :date AND FUNCTION('HOUR', i.time) = 17 AND FUNCTION('MINUTE', i.time) BETWEEN 0 AND 29")
    boolean hasPunchedOutBetween500to529PM(String userId, Date date); //🚨
}