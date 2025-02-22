package com.slt.peotv.lmsmangmentservice.entity.archive.types;

import com.slt.peotv.lmsmangmentservice.entity.archive.AttendanceEntity_;
import jakarta.persistence.*;

@Entity
@Table(name = "attendance_type")
public class AttendanceTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable=false)
    private String publicId;

    @Column(nullable=false)
    private String shortName;

    @Column(nullable=false)
    private String Description;

    @ManyToOne
    @JoinColumn(name = "attendance_id", nullable = false)
    private AttendanceEntity_ attendance;

    public AttendanceEntity_ getAttendance() {
        return attendance;
    }

    public void setAttendance(AttendanceEntity_ attendance) {
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
