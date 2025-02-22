package com.slt.peotv.lmsmangmentservice.entity.archive;

import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;

@Entity
@Table(name="attendance_")
@Getter
@Setter
public class AttendanceEntity_ {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable=false)
    private String publicId; //D

    @Column(nullable=false)
    private Boolean isHalfDay; //D

    @Column(nullable=false)
    private Boolean isAbsent; //D

    @Column(nullable=false)
    private Boolean isUnSuccessful; //D

    @Column(nullable=false)
    private Date date; // D

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; //D

    @Column(nullable=false)
    private Boolean isFullDay;  //D

    private Date arrival_date; //D

    private Time arrival_time; //D

    private Time left_time; //D

    @Column(nullable=false)
    private Boolean isLate; //D

    @Column(nullable=false)
    private Boolean lateCover; //D

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
}
