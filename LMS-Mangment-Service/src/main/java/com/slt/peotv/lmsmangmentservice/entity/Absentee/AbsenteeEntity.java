package com.slt.peotv.lmsmangmentservice.entity.Absentee;

import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "absentee")
@Setter
@Getter
@EqualsAndHashCode
public class AbsenteeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable=false)
    private String publicId;

    @Column(name = "Date", nullable = false)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "Is_halfday")
    private Boolean isHalfDay;

    @Column(name = "is_supervised_approved", nullable = false, columnDefinition = "int(10) unsigned default '0'")
    private Boolean isSupervisedApproved = false;

    @Column(name = "is_HOD_approved", nullable = false, columnDefinition = "int(10) unsigned default '0'")
    private Boolean isHODApproved = false;

    @Column(name = "swipe_err", columnDefinition = "int(10) unsigned default '0'")
    private Boolean swipeErr = false;

    @Column(name = "audited", nullable = false, columnDefinition = "int(10) unsigned default '0'")
    private Integer audited = 0;

    @Column(name = "etl_run_time", nullable = false, columnDefinition = "timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
//    @Temporal(TemporalType.TIMESTAMP)
    private Date etlRunTime;

    @Column(name = "is_nopay", columnDefinition = "int(10) unsigned default '0'")
    private Integer isNoPay = 0;

    // Getters and Setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getIsHalfDay() {
        return isHalfDay;
    }

    public void setIsHalfDay(Boolean isHalfDay) {
        this.isHalfDay = isHalfDay;
    }

    public Boolean getIsSupervisedApproved() {
        return isSupervisedApproved;
    }

    public void setIsSupervisedApproved(Boolean isSupervisedApproved) {
        this.isSupervisedApproved = isSupervisedApproved;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Boolean getHalfDay() {
        return isHalfDay;
    }

    public void setHalfDay(Boolean halfDay) {
        isHalfDay = halfDay;
    }

    public Boolean getSupervisedApproved() {
        return isSupervisedApproved;
    }

    public void setSupervisedApproved(Boolean supervisedApproved) {
        isSupervisedApproved = supervisedApproved;
    }

    public Boolean getHODApproved() {
        return isHODApproved;
    }

    public void setHODApproved(Boolean HODApproved) {
        isHODApproved = HODApproved;
    }

    public Boolean getIsHODApproved() {
        return isHODApproved;
    }

    public void setIsHODApproved(Boolean isHODApproved) {
        this.isHODApproved = isHODApproved;
    }

    public Boolean getSwipeErr() {
        return swipeErr;
    }

    public void setSwipeErr(Boolean swipeErr) {
        this.swipeErr = swipeErr;
    }

    public Integer getAudited() {
        return audited;
    }

    public void setAudited(Integer audited) {
        this.audited = audited;
    }

    public Date getEtlRunTime() {
        return etlRunTime;
    }

    public void setEtlRunTime(Date etlRunTime) {
        this.etlRunTime = etlRunTime;
    }

    public Integer getIsNoPay() {
        return isNoPay;
    }

    public void setIsNoPay(Integer isNoPay) {
        this.isNoPay = isNoPay;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AbsenteeEntity that = (AbsenteeEntity) o;
        return id == that.id && Objects.equals(publicId, that.publicId) && Objects.equals(date, that.date) && Objects.equals(user, that.user) && Objects.equals(isHalfDay, that.isHalfDay) && Objects.equals(isSupervisedApproved, that.isSupervisedApproved) && Objects.equals(isHODApproved, that.isHODApproved) && Objects.equals(swipeErr, that.swipeErr) && Objects.equals(audited, that.audited) && Objects.equals(etlRunTime, that.etlRunTime) && Objects.equals(isNoPay, that.isNoPay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicId, date, user, isHalfDay, isSupervisedApproved, isHODApproved, swipeErr, audited, etlRunTime, isNoPay);
    }
}
