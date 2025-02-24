package com.slt.peotv.lmsmangmentservice.entity.Attendance;

import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name="attendance")
@Getter
@Setter
@EqualsAndHashCode
public class AttendanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable=false)
    private String publicId;

    @Column(nullable=false)
    private Date date;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable=false)
    private Boolean isFullDay;

    private Date arrival_date;

    private Time arrival_time;

    private Time left_time;

    @Column(nullable=false)
    private Boolean isLate; // ❌❌

    @Column(nullable=false)
    private Boolean lateCover; // ❌❌

    @Column(nullable=false)
    private Boolean isHalfDay; //❌❌

    @Column(nullable=false)
    private Boolean isShortLeave; //❌❌

    @Column(nullable=false)
    private Boolean isAbsent; //❌❌

    @Column(nullable=false)
    private Boolean isUnSuccessful; // ❌❌

    public Time getLeft_time() {
        return left_time;
    }

    public void setLeft_time(Time left_time) {
        this.left_time = left_time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Boolean getHalfDay() {
        return isHalfDay;
    }

    public void setHalfDay(Boolean halfDay) {
        isHalfDay = halfDay;
    }

    public Boolean getAbsent() {
        return isAbsent;
    }

    public void setAbsent(Boolean absent) {
        isAbsent = absent;
    }

    public Boolean getUnSuccessful() {
        return isUnSuccessful;
    }

    public void setUnSuccessful(Boolean unSuccessful) {
        isUnSuccessful = unSuccessful;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Boolean getFullDay() {
        return isFullDay;
    }

    public void setFullDay(Boolean fullDay) {
        isFullDay = fullDay;
    }

    public Date getArrival_date() {
        return arrival_date;
    }

    public void setArrival_date(Date arrival_date) {
        this.arrival_date = arrival_date;
    }

    public Time getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(Time arrival_time) {
        this.arrival_time = arrival_time;
    }

    public Boolean getLate() {
        return isLate;
    }

    public void setLate(Boolean late) {
        isLate = late;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public Boolean getLateCover() {
        return lateCover;
    }

    public void setLateCover(Boolean lateCover) {
        this.lateCover = lateCover;
    }

    public Boolean getShortLeave() {
        return isShortLeave;
    }

    public void setShortLeave(Boolean shortLeave) {
        isShortLeave = shortLeave;
    }
}
