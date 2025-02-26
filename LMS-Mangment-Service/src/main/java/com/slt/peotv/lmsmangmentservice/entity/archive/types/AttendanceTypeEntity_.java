package com.slt.peotv.lmsmangmentservice.entity.archive.types;

import com.slt.peotv.lmsmangmentservice.entity.Attendance.AttendanceEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "attendance_type_")
public class AttendanceTypeEntity_ {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String publicId;

    private String shortName;

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
