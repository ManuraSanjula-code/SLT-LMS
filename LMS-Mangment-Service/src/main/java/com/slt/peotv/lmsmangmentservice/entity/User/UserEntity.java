package com.slt.peotv.lmsmangmentservice.entity.User;

import com.slt.peotv.lmsmangmentservice.entity.User.basic.RoleEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.company.ProfilesEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.company.SectionEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name="users")
@Setter
@Getter
@EqualsAndHashCode
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable=false)
    private String userId;

    @Column(nullable=false)
    private String employeeId;

    @ManyToMany(cascade= { CascadeType.PERSIST }, fetch = FetchType.EAGER )
    @JoinTable(name="users_roles",
            joinColumns=@JoinColumn(name="users_id",referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="roles_id",referencedColumnName="id"))
    @Column(nullable=false)
    private Collection<RoleEntity> roles;

    @ManyToMany(cascade= { CascadeType.PERSIST }, fetch = FetchType.EAGER )
    @JoinTable(name="users_profile",
            joinColumns=@JoinColumn(name="users_id",referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="profile_id",referencedColumnName="id"))
    @Column(nullable=false)
    private Collection<ProfilesEntity> profiles;

    @ManyToMany(cascade= { CascadeType.PERSIST }, fetch = FetchType.EAGER )
    @JoinTable(name="users_section",
            joinColumns=@JoinColumn(name="users_id",referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="section_id",referencedColumnName="id"))
    @Column(nullable=false)
    private Collection<SectionEntity> sections;

    @Column(nullable=false, length=120)
    private String email;

    private Integer tot_CASUAl_Leaves = 10;
    private Integer tot_ANNUAL_Leaves = 10;
    private Integer tot_SICK_Leaves = 10;
    private Integer tot_SPECIAL_Leaves = 10;
    private Integer tot_DUTY_Leaves = 10;
    private Integer tot_SHORT_LEAVE_Leaves = 10;
    private Integer tot_MATERNITY_LEAVE_Leaves = 10;
    private Integer tot_HALF_DAYS = 10;

    public Integer getTot_HALF_DAYS() {
        return tot_HALF_DAYS;
    }

    public void setTot_HALF_DAYS(Integer tot_HALF_DAYS) {
        this.tot_HALF_DAYS = tot_HALF_DAYS;
    }

    public Integer getTot_CASUAl_Leaves() {
        return tot_CASUAl_Leaves;
    }

    public void setTot_CASUAl_Leaves(Integer tot_CASUAl_Leaves) {
        this.tot_CASUAl_Leaves = tot_CASUAl_Leaves;
    }

    public Integer getTot_ANNUAL_Leaves() {
        return tot_ANNUAL_Leaves;
    }

    public void setTot_ANNUAL_Leaves(Integer tot_ANNUAL_Leaves) {
        this.tot_ANNUAL_Leaves = tot_ANNUAL_Leaves;
    }

    public Integer getTot_SICK_Leaves() {
        return tot_SICK_Leaves;
    }

    public void setTot_SICK_Leaves(Integer tot_SICK_Leaves) {
        this.tot_SICK_Leaves = tot_SICK_Leaves;
    }

    public Integer getTot_SPECIAL_Leaves() {
        return tot_SPECIAL_Leaves;
    }

    public void setTot_SPECIAL_Leaves(Integer tot_SPECIAL_Leaves) {
        this.tot_SPECIAL_Leaves = tot_SPECIAL_Leaves;
    }

    public Integer getTot_DUTY_Leaves() {
        return tot_DUTY_Leaves;
    }

    public void setTot_DUTY_Leaves(Integer tot_DUTY_Leaves) {
        this.tot_DUTY_Leaves = tot_DUTY_Leaves;
    }

    public Integer getTot_SHORT_LEAVE_Leaves() {
        return tot_SHORT_LEAVE_Leaves;
    }

    public void setTot_SHORT_LEAVE_Leaves(Integer tot_SHORT_LEAVE_Leaves) {
        this.tot_SHORT_LEAVE_Leaves = tot_SHORT_LEAVE_Leaves;
    }

    public Integer getTot_MATERNITY_LEAVE_Leaves() {
        return tot_MATERNITY_LEAVE_Leaves;
    }

    public void setTot_MATERNITY_LEAVE_Leaves(Integer tot_MATERNITY_LEAVE_Leaves) {
        this.tot_MATERNITY_LEAVE_Leaves = tot_MATERNITY_LEAVE_Leaves;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Collection<SectionEntity> getSections() {
        return sections;
    }

    public void setSections(Collection<SectionEntity> sections) {
        this.sections = sections;
    }

    public long getId() {
        return id;
    }

    public Collection<ProfilesEntity> getProfiles() {
        return profiles;
    }

    public void setProfiles(Collection<ProfilesEntity> profiles) {
        this.profiles = profiles;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(nullable = false)
    public Collection<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Collection<RoleEntity> roles) {
        this.roles = roles;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return id == that.id && Objects.equals(userId, that.userId) && Objects.equals(employeeId, that.employeeId) && Objects.equals(roles, that.roles) && Objects.equals(profiles, that.profiles) && Objects.equals(sections, that.sections) && Objects.equals(email, that.email) && Objects.equals(tot_CASUAl_Leaves, that.tot_CASUAl_Leaves) && Objects.equals(tot_ANNUAL_Leaves, that.tot_ANNUAL_Leaves) && Objects.equals(tot_SICK_Leaves, that.tot_SICK_Leaves) && Objects.equals(tot_SPECIAL_Leaves, that.tot_SPECIAL_Leaves) && Objects.equals(tot_DUTY_Leaves, that.tot_DUTY_Leaves) && Objects.equals(tot_SHORT_LEAVE_Leaves, that.tot_SHORT_LEAVE_Leaves) && Objects.equals(tot_MATERNITY_LEAVE_Leaves, that.tot_MATERNITY_LEAVE_Leaves) && Objects.equals(tot_HALF_DAYS, that.tot_HALF_DAYS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, employeeId, roles, profiles, sections, email, tot_CASUAl_Leaves, tot_ANNUAL_Leaves, tot_SICK_Leaves, tot_SPECIAL_Leaves, tot_DUTY_Leaves, tot_SHORT_LEAVE_Leaves, tot_MATERNITY_LEAVE_Leaves, tot_HALF_DAYS);
    }
}
