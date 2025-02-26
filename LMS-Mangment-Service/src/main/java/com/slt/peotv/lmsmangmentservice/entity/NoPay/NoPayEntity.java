package com.slt.peotv.lmsmangmentservice.entity.NoPay;

import com.slt.peotv.lmsmangmentservice.entity.Attendance.AttendanceEntity;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nopay")
@Setter
@Getter
@EqualsAndHashCode
public class NoPayEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public String publicId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "submission_date")
    private Date submissionDate;

    @Column(name = "actual_date")
    private Date acctualDate;

    private Boolean isHalfDay; ///  To indicate half-day ---- because user is half-day he/she can make absent or leave or make movement
    private Boolean unSuccessful; /// To indicate unSuccessful ---- because user is unSuccessful he/she can make absent or leave or make movement
    private Boolean isLate; /// To indicate isLate ---- because user is isLate he/she can make absent or leave or make movement
    private Boolean isLateCover; /// To indicate latecover---- because user is latecover he/she can make absent or leave or make movement
    private Boolean isAbsent; /// To indicate isAbsent ---- because user is isAbsent he/she can make absent or leave or make movement

    @OneToOne
    @JoinColumn(name = "attendance_id")
    private AttendanceEntity attendance;

    private String comment;

    private Date happenDate;

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

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Date getAcctualDate() {
        return acctualDate;
    }

    public void setAcctualDate(Date acctualDate) {
        this.acctualDate = acctualDate;
    }

    public Boolean getHalfDay() {
        return isHalfDay;
    }

    public void setHalfDay(Boolean halfDay) {
        isHalfDay = halfDay;
    }

    public Boolean getUnSuccessful() {
        return unSuccessful;
    }

    public void setUnSuccessful(Boolean unSuccessful) {
        this.unSuccessful = unSuccessful;
    }

    public Boolean getLate() {
        return isLate;
    }

    public void setLate(Boolean late) {
        isLate = late;
    }

    public Boolean getLateCover() {
        return isLateCover;
    }

    public void setLateCover(Boolean lateCover) {
        isLateCover = lateCover;
    }

    public Boolean getAbsent() {
        return isAbsent;
    }

    public void setAbsent(Boolean absent) {
        isAbsent = absent;
    }

    public AttendanceEntity getAttendance() {
        return attendance;
    }

    public void setAttendance(AttendanceEntity attendance) {
        this.attendance = attendance;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getHappenDate() {
        return happenDate;
    }

    public void setHappenDate(Date happenDate) {
        this.happenDate = happenDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NoPayEntity that = (NoPayEntity) o;
        return id == that.id && Objects.equals(publicId, that.publicId) && Objects.equals(user, that.user) && Objects.equals(submissionDate, that.submissionDate) && Objects.equals(acctualDate, that.acctualDate) && Objects.equals(isHalfDay, that.isHalfDay) && Objects.equals(unSuccessful, that.unSuccessful) && Objects.equals(isLate, that.isLate) && Objects.equals(isLateCover, that.isLateCover) && Objects.equals(isAbsent, that.isAbsent) && Objects.equals(attendance, that.attendance) && Objects.equals(comment, that.comment) && Objects.equals(happenDate, that.happenDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicId, user, submissionDate, acctualDate, isHalfDay, unSuccessful, isLate, isLateCover, isAbsent, attendance, comment, happenDate);
    }
}
