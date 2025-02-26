package com.slt.peotv.lmsmangmentservice.entity.Leave;

import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveCategoryEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveTypeEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Builder
@Entity
@Table(name="leave_requests")
@Setter
@Getter
@EqualsAndHashCode
public class LeaveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    public String publicId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "submit_date", nullable = false)
    private Date submitDate;

    @Column(name = "from_date", nullable = false)
    private Date fromDate;

    @Column(name = "to_date", nullable = false)
    private Date toDate;

    @ManyToOne
    @JoinColumn(name = "leave_category_id")
    private LeaveCategoryEntity leaveCategory;

    @ManyToOne
    @JoinColumn(name = "leave_type_id")
    private LeaveTypeEntity leaveType;

    @Column(name = "is_supervised_approved", nullable = false, columnDefinition = "int(10) unsigned default '0'")
    @Builder.Default
    private Boolean isSupervisedApproved = false;

    @Column(name = "is_HOD_approved", nullable = false, columnDefinition = "int(10) unsigned default '0'")
    @Builder.Default
    private Boolean isHODApproved = false;

    @Column(name = "is_nopay", columnDefinition = "int(10) unsigned default '0'")
    private Integer isNoPay = 0;

    @Column(name = "num_of_days")
    private Long numOfDays;

    @Column(name = "description")
    private String description;

    @Column(name = "is_halfday")
    private Boolean isHalfDay;

    private Boolean unSuccessful;
    private Boolean isLate;
    private Boolean isLateCover;
    private Boolean isShort_Leave;
    private Boolean isPending;
    private Boolean isAccepted;
    private Boolean notUsed;

    private Date happenDate;

    public LeaveEntity() {
    }

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

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public LeaveCategoryEntity getLeaveCategory() {
        return leaveCategory;
    }

    public void setLeaveCategory(LeaveCategoryEntity leaveCategory) {
        this.leaveCategory = leaveCategory;
    }

    public LeaveTypeEntity getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveTypeEntity leaveType) {
        this.leaveType = leaveType;
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

    public Integer getIsNoPay() {
        return isNoPay;
    }

    public void setIsNoPay(Integer isNoPay) {
        this.isNoPay = isNoPay;
    }

    public Long getNumOfDays() {
        return numOfDays;
    }

    public void setNumOfDays(Long numOfDays) {
        this.numOfDays = numOfDays;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Boolean getShort_Leave() {
        return isShort_Leave;
    }

    public void setShort_Leave(Boolean short_Leave) {
        isShort_Leave = short_Leave;
    }

    public Boolean getPending() {
        return isPending;
    }

    public void setPending(Boolean pending) {
        isPending = pending;
    }

    public Boolean getAccepted() {
        return isAccepted;
    }

    public void setAccepted(Boolean accepted) {
        isAccepted = accepted;
    }

    public Boolean getNotUsed() {
        return notUsed;
    }

    public void setNotUsed(Boolean notUsed) {
        this.notUsed = notUsed;
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
        LeaveEntity that = (LeaveEntity) o;
        return id == that.id && Objects.equals(publicId, that.publicId) && Objects.equals(user, that.user) && Objects.equals(submitDate, that.submitDate) && Objects.equals(fromDate, that.fromDate) && Objects.equals(toDate, that.toDate) && Objects.equals(leaveCategory, that.leaveCategory) && Objects.equals(leaveType, that.leaveType) && Objects.equals(isSupervisedApproved, that.isSupervisedApproved) && Objects.equals(isHODApproved, that.isHODApproved) && Objects.equals(isNoPay, that.isNoPay) && Objects.equals(numOfDays, that.numOfDays) && Objects.equals(description, that.description) && Objects.equals(isHalfDay, that.isHalfDay) && Objects.equals(unSuccessful, that.unSuccessful) && Objects.equals(isLate, that.isLate) && Objects.equals(isLateCover, that.isLateCover) && Objects.equals(isShort_Leave, that.isShort_Leave) && Objects.equals(isPending, that.isPending) && Objects.equals(isAccepted, that.isAccepted) && Objects.equals(notUsed, that.notUsed) && Objects.equals(happenDate, that.happenDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicId, user, submitDate, fromDate, toDate, leaveCategory, leaveType, isSupervisedApproved, isHODApproved, isNoPay, numOfDays, description, isHalfDay, unSuccessful, isLate, isLateCover, isShort_Leave, isPending, isAccepted, notUsed, happenDate);
    }
}
