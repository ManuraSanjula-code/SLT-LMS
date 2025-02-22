package com.slt.peotv.lmsmangmentservice.repository.archive;

import com.slt.peotv.lmsmangmentservice.entity.card.InOutEntity_;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InOutRepo_ extends CrudRepository<InOutEntity_, Long> {

    // Get all records based on employeeID
    List<InOutEntity_> findByEmployeeID(String employeeID);

    // Get the earliest pushIn for the current date (using pushInTime)
    @Query("SELECT e FROM InOutEntity e WHERE e.employeeID = :employeeID AND DATE(e.pushIn) = CURRENT_DATE " +
            "ORDER BY e.pushInTime ASC LIMIT 1")
    Optional<InOutEntity_> findEarliestPushIn(@Param("employeeID") String employeeID);

    // Get the earliest pushOut for the current date (using pushOutTime)
    @Query("SELECT e FROM InOutEntity e WHERE e.employeeID = :employeeID AND DATE(e.pushOut) = CURRENT_DATE " +
            "ORDER BY e.pushOutTime ASC LIMIT 1")
    Optional<InOutEntity_> findEarliestPushOut(@Param("employeeID") String employeeID);

    // Check if the employee has any movements before 8:30 AM
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM InOutEntity e " +
            "WHERE e.employeeID = :employeeID AND DATE(e.pushIn) = CURRENT_DATE AND e.pushInTime < '08:30:00'")
    boolean hasMovementsBefore830(@Param("employeeID") String employeeID);

    // Check if the employee has any movement after 9:00 AM
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM InOutEntity e " +
            "WHERE e.employeeID = :employeeID AND DATE(e.pushIn) = CURRENT_DATE AND e.pushInTime > '09:00:00'")
    boolean hasMovementsAfter900(@Param("employeeID") String employeeID);

    // Check if an employee came after 9:00 AM
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM InOutEntity e " +
            "WHERE e.employeeID = :employeeID AND DATE(e.pushIn) = CURRENT_DATE AND e.pushInTime > '09:00:00'")
    boolean cameAfter900(@Param("employeeID") String employeeID);

    // Check if an employee came after 10:00 AM
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM InOutEntity e " +
            "WHERE e.employeeID = :employeeID AND DATE(e.pushIn) = CURRENT_DATE AND e.pushInTime > '10:00:00'")
    boolean cameAfter1000(@Param("employeeID") String employeeID);

    // Check if an employee who came before 8:30 AM can leave between 5:00-5:30 PM
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM InOutEntity e " +
            "WHERE e.employeeID = :employeeID AND DATE(e.pushIn) = CURRENT_DATE AND e.pushInTime < '08:30:00' " +
            "AND e.pushOutTime BETWEEN '17:00:00' AND '17:30:00'")
    boolean canLeaveOnTime(@Param("employeeID") String employeeID);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM InOutEntity e " +
            "WHERE e.employeeID = :employeeID AND DATE(e.pushIn) = CURRENT_DATE AND e.pushInTime < '08:30:00' " +
            "AND e.pushOutTime BETWEEN '17:00:00' AND '00:00:00'")
    boolean canLeaveOnTime_(@Param("employeeID") String employeeID);

    // Check if an employee who came after 9:00 AM needs to work additional 30 minutes
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM InOutEntity e " +
            "WHERE e.employeeID = :employeeID AND DATE(e.pushIn) = CURRENT_DATE AND e.pushInTime > '09:00:00' " +
            "AND e.pushOutTime >= '17:30:00'")
    boolean needsExtra30Minutes(@Param("employeeID") String employeeID);
}

