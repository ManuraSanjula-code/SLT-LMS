package com.slt.peotv.lmsmangmentservice.entity.Leave.category;

import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveTypeEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "user_leave_type_total")
@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserLeaveTypeTotalEntity {
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

    private Integer totalLeaves;

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

    public Integer getTotalLeaves() {
        return totalLeaves;
    }

    public void setTotalLeaves(Integer totalLeaves) {
        this.totalLeaves = totalLeaves;
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

