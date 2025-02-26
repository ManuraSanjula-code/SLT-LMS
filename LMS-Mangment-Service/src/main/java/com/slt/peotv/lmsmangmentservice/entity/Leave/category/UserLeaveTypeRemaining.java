package com.slt.peotv.lmsmangmentservice.entity.Leave.category;

import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveTypeEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.util.Objects;

@Entity
@Table(name = "user_leave_type_remaining")
@Setter
@Getter
@EqualsAndHashCode
public class UserLeaveTypeRemaining {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String publicId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "leave_type_id")
    private LeaveTypeEntity leaveType;

    private Integer remainingLeaves;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public LeaveTypeEntity getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveTypeEntity leaveType) {
        this.leaveType = leaveType;
    }

    public Integer getRemainingLeaves() {
        return remainingLeaves;
    }

    public void setRemainingLeaves(Integer remainingLeaves) {
        this.remainingLeaves = remainingLeaves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserLeaveTypeRemaining that = (UserLeaveTypeRemaining) o;
        return Objects.equals(id, that.id) && Objects.equals(publicId, that.publicId) && Objects.equals(user, that.user) && Objects.equals(leaveType, that.leaveType) && Objects.equals(remainingLeaves, that.remainingLeaves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicId, user, leaveType, remainingLeaves);
    }
}


