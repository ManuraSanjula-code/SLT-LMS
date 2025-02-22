package com.slt.peotv.lmsmangmentservice.repository.archive;

import com.slt.peotv.lmsmangmentservice.entity.archive.AttendanceEntity_;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepo_ extends CrudRepository<AttendanceEntity_, Long> {
    List<AttendanceEntity_> findByUser(UserEntity user);
    Optional<AttendanceEntity_> findByPublicId(String publicId);
    boolean existsByUserAndDate(UserEntity user, Date date);
    Optional<AttendanceEntity_> findByUserAndDate(UserEntity user, Date date);
    List<AttendanceEntity_> findByIsAbsentTrue();
    List<AttendanceEntity_> findByIsHalfDayTrue();
    List<AttendanceEntity_> findByIsUnSuccessfulTrue();
    List<AttendanceEntity_> findByIsFullDayTrue();
    List<AttendanceEntity_> findByIsLateTrue();
    List<AttendanceEntity_> findByIsLateTrueAndLateCoverFalse();
    List<AttendanceEntity_> findByDate(Date date);
    List<AttendanceEntity_> findByDateBetween(Date startDate, Date endDate);
    List<AttendanceEntity_> findByUser_Id(Long userId);
    List<AttendanceEntity_> findByDateAndArrivalTimeBetween(Date date, Time startTime, Time endTime);
}
