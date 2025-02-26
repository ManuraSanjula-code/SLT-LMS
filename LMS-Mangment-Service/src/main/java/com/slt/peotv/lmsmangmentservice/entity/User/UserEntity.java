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
        UserEntity user = (UserEntity) o;
        return id == user.id && Objects.equals(userId, user.userId) && Objects.equals(employeeId, user.employeeId) && Objects.equals(roles, user.roles) && Objects.equals(profiles, user.profiles) && Objects.equals(sections, user.sections) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, employeeId, roles, profiles, sections, email);
    }
}
