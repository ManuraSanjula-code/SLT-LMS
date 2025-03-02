package com.slt.peotv.lmsmangmentservice.entity.Leave.category;

import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveCategoryEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "user_leave_category_total")
@Setter
@Getter
@EqualsAndHashCode
public class UserLeaveCategoryTotalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable=false)
    private String publicId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "leave_category_id", nullable = false)
    private LeaveCategoryEntity leaveCategory;

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

    public LeaveCategoryEntity getLeaveCategory() {
        return leaveCategory;
    }

    public void setLeaveCategory(LeaveCategoryEntity leaveCategory) {
        this.leaveCategory = leaveCategory;
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
        UserLeaveCategoryTotalEntity that = (UserLeaveCategoryTotalEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(publicId, that.publicId) && Objects.equals(user, that.user) && Objects.equals(leaveCategory, that.leaveCategory) && Objects.equals(totalLeaves, that.totalLeaves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicId, user, leaveCategory, totalLeaves);
    }
}

