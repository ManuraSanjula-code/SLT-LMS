package com.slt.peotv.lmsmangmentservice.entity.Leave.category;

import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveTypeEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "user_leave_type_total")
@Setter
@Getter
@EqualsAndHashCode
public class UserLeaveTypeTotalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable=false)
    private String publicId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveTypeEntity leaveType;

    private Integer totalLeaves;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public LeaveTypeEntity getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveTypeEntity leaveType) {
        this.leaveType = leaveType;
    }

    public Integer getTotalLeaves() {
        return totalLeaves;
    }

    public void setTotalLeaves(Integer totalLeaves) {
        this.totalLeaves = totalLeaves;
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
        UserLeaveTypeTotalEntity that = (UserLeaveTypeTotalEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(publicId, that.publicId) && Objects.equals(user, that.user) && Objects.equals(leaveType, that.leaveType) && Objects.equals(totalLeaves, that.totalLeaves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicId, user, leaveType, totalLeaves);
    }
}

