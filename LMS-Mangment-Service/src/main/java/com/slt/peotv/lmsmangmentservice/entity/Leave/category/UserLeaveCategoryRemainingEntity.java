package com.slt.peotv.lmsmangmentservice.entity.Leave.category;

import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveCategoryEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "user_leave_category_remaining")
@Setter
@Getter
@EqualsAndHashCode
public class UserLeaveCategoryRemainingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String publicId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "leave_category_id")
    private LeaveCategoryEntity leaveCategory;

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

    public LeaveCategoryEntity getLeaveCategory() {
        return leaveCategory;
    }

    public void setLeaveCategory(LeaveCategoryEntity leaveCategory) {
        this.leaveCategory = leaveCategory;
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
        UserLeaveCategoryRemainingEntity remaining = (UserLeaveCategoryRemainingEntity) o;
        return Objects.equals(id, remaining.id) && Objects.equals(publicId, remaining.publicId) && Objects.equals(user, remaining.user) && Objects.equals(leaveCategory, remaining.leaveCategory) && Objects.equals(remainingLeaves, remaining.remainingLeaves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicId, user, leaveCategory, remainingLeaves);
    }
}
