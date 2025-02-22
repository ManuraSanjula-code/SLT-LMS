package com.slt.peotv.lmsmangmentservice.entity.Attendance.types;

import com.slt.peotv.lmsmangmentservice.entity.Attendance.AttendanceEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "attendance_type")
@Setter
@Getter
@EqualsAndHashCode
public class AttendanceTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable=false)
    private String publicId;

    @Column(nullable=false)
    private String shortName;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceTypeEntity that = (AttendanceTypeEntity) o;
        return id == that.id && Objects.equals(publicId, that.publicId) && Objects.equals(shortName, that.shortName) && Objects.equals(Description, that.Description) && Objects.equals(attendance, that.attendance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicId, shortName, Description, attendance);
    }

    @Column(nullable=false)
    private String Description;

    @ManyToOne
    @JoinColumn(name = "attendance_id", nullable = false)
    private AttendanceEntity attendance;

    public AttendanceEntity getAttendance() {
        return attendance;
    }

    public void setAttendance(AttendanceEntity attendance) {
        this.attendance = attendance;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }
}
