package com.slt.peotv.lmsmangmentservice.entity.User.company;

import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveTypeEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "group")
@Setter
@Getter
@EqualsAndHashCode
public class GroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public String publicId;

    private String description;

    @ManyToOne
    @JoinColumn(name = "leave_type_id")
    private LeaveTypeEntity leaveType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LeaveTypeEntity getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveTypeEntity leaveType) {
        this.leaveType = leaveType;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GroupEntity that = (GroupEntity) o;
        return id == that.id && Objects.equals(publicId, that.publicId) && Objects.equals(description, that.description) && Objects.equals(leaveType, that.leaveType) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicId, description, leaveType, user);
    }
}
