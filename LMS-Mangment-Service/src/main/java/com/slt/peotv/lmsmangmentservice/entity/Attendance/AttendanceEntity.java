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
@Table(name = "attendance")
@Getter
@Setter
@EqualsAndHashCode
public class AttendanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String publicId;

    @Column(nullable = false)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    private Boolean isFullDay;
    private Date arrival_date;
    private Time arrival_time;
    private Time left_time;
    private Boolean isLate;
    private Boolean lateCover;
    private Boolean isHalfDay;
    private Boolean isShortLeave;
    private Boolean isAbsent;
    private Boolean isUnSuccessful;
    private Boolean isNoPay;
    private Boolean issues;
    private Boolean isUnAuthorized;

    @Column(length = 1000)
    private String issue_description;

    private Date dueDateForUA;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
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

    public Time getLeft_time() {
        return left_time;
    }

    public void setLeft_time(Time left_time) {
        this.left_time = left_time;
    }

    public Boolean getLate() {
        return isLate;
    }

    public void setLate(Boolean late) {
        isLate = late;
    }

    public Boolean getLateCover() {
        return lateCover;
    }

    public void setLateCover(Boolean lateCover) {
        this.lateCover = lateCover;
    }

    public Boolean getHalfDay() {
        return isHalfDay;
    }

    public void setHalfDay(Boolean halfDay) {
        isHalfDay = halfDay;
    }

    public Boolean getShortLeave() {
        return isShortLeave;
    }

    public void setShortLeave(Boolean shortLeave) {
        isShortLeave = shortLeave;
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

    public Boolean getNoPay() {
        return isNoPay;
    }

    public void setNoPay(Boolean noPay) {
        isNoPay = noPay;
    }

    public Boolean getIssues() {
        return issues;
    }

    public void setIssues(Boolean issues) {
        this.issues = issues;
    }

    public Boolean getUnAuthorized() {
        return isUnAuthorized;
    }

    public void setUnAuthorized(Boolean unAuthorized) {
        isUnAuthorized = unAuthorized;
    }

    public String getIssue_description() {
        return issue_description;
    }

    public void setIssue_description(String issue_description) {
        this.issue_description = issue_description;
    }

    public Date getDueDateForUA() {
        return dueDateForUA;
    }

    public void setDueDateForUA(Date dueDateForUA) {
        this.dueDateForUA = dueDateForUA;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceEntity that = (AttendanceEntity) o;
        return id == that.id && Objects.equals(publicId, that.publicId) && Objects.equals(date, that.date) && Objects.equals(user, that.user) && Objects.equals(isFullDay, that.isFullDay) && Objects.equals(arrival_date, that.arrival_date) && Objects.equals(arrival_time, that.arrival_time) && Objects.equals(left_time, that.left_time) && Objects.equals(isLate, that.isLate) && Objects.equals(lateCover, that.lateCover) && Objects.equals(isHalfDay, that.isHalfDay) && Objects.equals(isShortLeave, that.isShortLeave) && Objects.equals(isAbsent, that.isAbsent) && Objects.equals(isUnSuccessful, that.isUnSuccessful) && Objects.equals(isNoPay, that.isNoPay) && Objects.equals(issues, that.issues) && Objects.equals(isUnAuthorized, that.isUnAuthorized) && Objects.equals(issue_description, that.issue_description) && Objects.equals(dueDateForUA, that.dueDateForUA);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicId, date, user, isFullDay, arrival_date, arrival_time, left_time, isLate, lateCover, isHalfDay, isShortLeave, isAbsent, isUnSuccessful, isNoPay, issues, isUnAuthorized, issue_description, dueDateForUA);
    }
}
