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

    private Date date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "Is_halfday")
    private Boolean isHalfDay;

    @Column(name = "is_supervised_approved", columnDefinition = "int(10) unsigned default '0'")
    private Boolean isSupervisedApproved = false;

    @Column(name = "is_HOD_approved", columnDefinition = "int(10) unsigned default '0'")
    private Boolean isHODApproved = false;

    @Column(name = "audited", columnDefinition = "int(10) unsigned default '0'")
    private Integer audited = 0;

    @Column(name = "is_nopay", columnDefinition = "int(10) unsigned default '0'")
    private Integer isNoPay = 0;
    private Boolean isPending;
    private Boolean isAccepted;

    private Boolean isLate;
    private Boolean isAbsent;
    private Boolean isUnSuccessfulAttdate;
    private Boolean isLateCover;
    private Date happenDate;

    private Boolean isArchived;
    private String comment;

    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public Integer getAudited() {
        return audited;
    }

    public void setAudited(Integer audited) {
        this.audited = audited;
    }

    public Integer getIsNoPay() {
        return isNoPay;
    }

    public void setIsNoPay(Integer isNoPay) {
        this.isNoPay = isNoPay;
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

    public Boolean getLate() {
        return isLate;
    }

    public void setLate(Boolean late) {
        isLate = late;
    }

    public Boolean getAbsent() {
        return isAbsent;
    }

    public void setAbsent(Boolean absent) {
        isAbsent = absent;
    }

    public Boolean getUnSuccessfulAttdate() {
        return isUnSuccessfulAttdate;
    }

    public void setUnSuccessfulAttdate(Boolean unSuccessfulAttdate) {
        isUnSuccessfulAttdate = unSuccessfulAttdate;
    }

    public Boolean getLateCover() {
        return isLateCover;
    }

    public void setLateCover(Boolean lateCover) {
        isLateCover = lateCover;
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
        AbsenteeEntity that = (AbsenteeEntity) o;
        return id == that.id && Objects.equals(publicId, that.publicId) && Objects.equals(date, that.date) && Objects.equals(user, that.user) && Objects.equals(isHalfDay, that.isHalfDay) && Objects.equals(isSupervisedApproved, that.isSupervisedApproved) && Objects.equals(isHODApproved, that.isHODApproved) && Objects.equals(audited, that.audited) && Objects.equals(isNoPay, that.isNoPay) && Objects.equals(isPending, that.isPending) && Objects.equals(isAccepted, that.isAccepted) && Objects.equals(isLate, that.isLate) && Objects.equals(isAbsent, that.isAbsent) && Objects.equals(isUnSuccessfulAttdate, that.isUnSuccessfulAttdate) && Objects.equals(isLateCover, that.isLateCover) && Objects.equals(happenDate, that.happenDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicId, date, user, isHalfDay, isSupervisedApproved, isHODApproved, audited, isNoPay, isPending, isAccepted, isLate, isAbsent, isUnSuccessfulAttdate, isLateCover, happenDate);
    }
}
