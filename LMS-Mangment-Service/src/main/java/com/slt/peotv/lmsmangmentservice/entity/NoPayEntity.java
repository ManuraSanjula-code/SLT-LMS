package com.slt.peotv.lmsmangmentservice.entity;

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

    @Column(nullable = false)
    public String publicId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "submission_date", nullable = false)
    private Date submissionDate;

    @Column(name = "actual_date", nullable = false)
    private Date acctualDate;

    private Boolean isHalfDay;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Date getAcctual_date() {
        return acctualDate;
    }

    public void setAcctual_date(Date acctual_date) {
        this.acctualDate = acctual_date;
    }

    public Boolean getHalfDay() {
        return isHalfDay;
    }

    public void setHalfDay(Boolean halfDay) {
        isHalfDay = halfDay;
    }

    public Date getAcctualDate() {
        return acctualDate;
    }

    public void setAcctualDate(Date acctualDate) {
        this.acctualDate = acctualDate;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
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
        NoPayEntity that = (NoPayEntity) o;
        return id == that.id && Objects.equals(publicId, that.publicId) && Objects.equals(user, that.user) && Objects.equals(submissionDate, that.submissionDate) && Objects.equals(acctualDate, that.acctualDate) && Objects.equals(isHalfDay, that.isHalfDay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicId, user, submissionDate, acctualDate, isHalfDay);
    }
}
