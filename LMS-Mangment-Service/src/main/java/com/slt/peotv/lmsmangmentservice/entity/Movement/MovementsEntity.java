package com.slt.peotv.lmsmangmentservice.entity.Movement;

import com.slt.peotv.lmsmangmentservice.entity.Attendance.AttendanceEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import com.slt.peotv.lmsmangmentservice.model.types.MovementType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name= "movements")
@Setter
@Getter
@EqualsAndHashCode
public class MovementsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public String publicId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "In_Time", length = 45)
    private String inTime;

    @Column(name = "Out_Time", length = 45)
    private String outTime;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "Log_Time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logTime;

    @Column(name = "Sup_app_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date supAppTime; /// Supervisor approved Time

    @Column(name = "Man_App_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date manAppTime; /// manager approved Time

    @Column(name = "HOD_App_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date hodAppTime; /// HOD approved Time

    @Column(name = "category", length = 45)
    private String category;

    @Column(name = "Destination", length = 45)
    private String destination; /// location where employee went

    @Column(name = "Employee_ID", length = 45)
    private String employeeId;

    @Column(name = "REQ_TIME", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reqDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity hod; /// hod UserId

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity supervisor; ///supervisor Id
    private MovementType movementType;

    @Column(name = "ATT_SYNC")
    private Integer attSync = 0;

    private Date dueDate;
    private Date happenDate;
    private Boolean isPending;
    private Boolean isAccepted;
    private Boolean isExpired;

    private Boolean isHalfDay;
    private Boolean isLate;
    private Boolean isAbsent;
    private Boolean isUnSuccessfulAttdate;
    private Boolean isLateCover;

    private Boolean unAuthorized;

    @OneToOne
    @JoinColumn(name = "attendance_id")
    private AttendanceEntity attendance;

    public Boolean getUnAuthorized() {
        return unAuthorized;
    }

    public void setUnAuthorized(Boolean unAuthorized) {
        this.unAuthorized = unAuthorized;
    }

    public AttendanceEntity getAttendance() {
        return attendance;
    }

    public void setAttendance(AttendanceEntity attendance) {
        this.attendance = attendance;
    }

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

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    public Date getSupAppTime() {
        return supAppTime;
    }

    public void setSupAppTime(Date supAppTime) {
        this.supAppTime = supAppTime;
    }

    public Date getManAppTime() {
        return manAppTime;
    }

    public void setManAppTime(Date manAppTime) {
        this.manAppTime = manAppTime;
    }

    public Date getHodAppTime() {
        return hodAppTime;
    }

    public void setHodAppTime(Date hodAppTime) {
        this.hodAppTime = hodAppTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public Date getReqDate() {
        return reqDate;
    }

    public void setReqDate(Date reqDate) {
        this.reqDate = reqDate;
    }

    public UserEntity getHod() {
        return hod;
    }

    public void setHod(UserEntity hod) {
        this.hod = hod;
    }

    public UserEntity getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(UserEntity supervisor) {
        this.supervisor = supervisor;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public Integer getAttSync() {
        return attSync;
    }

    public void setAttSync(Integer attSync) {
        this.attSync = attSync;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getHappenDate() {
        return happenDate;
    }

    public void setHappenDate(Date happenDate) {
        this.happenDate = happenDate;
    }

    public Boolean getPending() {
        return isPending;
    }

    public void setPending(Boolean pending) {
        isPending = pending;
    }

    public Boolean getAccepted() {
        return isAccepted;
    }

    public void setAccepted(Boolean accepted) {
        isAccepted = accepted;
    }

    public Boolean getExpired() {
        return isExpired;
    }

    public void setExpired(Boolean expired) {
        isExpired = expired;
    }

    public Boolean getHalfDay() {
        return isHalfDay;
    }

    public void setHalfDay(Boolean halfDay) {
        isHalfDay = halfDay;
    }

    public Boolean getLate() {
        return isLate;
    }

    public void setLate(Boolean late) {
        isLate = late;
    }

    public Boolean getAbsent() {
        return isAbsent;
    }

    public void setAbsent(Boolean absent) {
        isAbsent = absent;
    }

    public Boolean getUnSuccessfulAttdate() {
        return isUnSuccessfulAttdate;
    }

    public void setUnSuccessfulAttdate(Boolean unSuccessfulAttdate) {
        isUnSuccessfulAttdate = unSuccessfulAttdate;
    }

    public Boolean getLateCover() {
        return isLateCover;
    }

    public void setLateCover(Boolean lateCover) {
        isLateCover = lateCover;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MovementsEntity that = (MovementsEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(publicId, that.publicId) && Objects.equals(user, that.user) && Objects.equals(inTime, that.inTime) && Objects.equals(outTime, that.outTime) && Objects.equals(comment, that.comment) && Objects.equals(logTime, that.logTime) && Objects.equals(supAppTime, that.supAppTime) && Objects.equals(manAppTime, that.manAppTime) && Objects.equals(hodAppTime, that.hodAppTime) && Objects.equals(category, that.category) && Objects.equals(destination, that.destination) && Objects.equals(employeeId, that.employeeId) && Objects.equals(reqDate, that.reqDate) && Objects.equals(hod, that.hod) && Objects.equals(supervisor, that.supervisor) && movementType == that.movementType && Objects.equals(attSync, that.attSync) && Objects.equals(dueDate, that.dueDate) && Objects.equals(happenDate, that.happenDate) && Objects.equals(isPending, that.isPending) && Objects.equals(isAccepted, that.isAccepted) && Objects.equals(isExpired, that.isExpired) && Objects.equals(isHalfDay, that.isHalfDay) && Objects.equals(isLate, that.isLate) && Objects.equals(isAbsent, that.isAbsent) && Objects.equals(isUnSuccessfulAttdate, that.isUnSuccessfulAttdate) && Objects.equals(isLateCover, that.isLateCover);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicId, user, inTime, outTime, comment, logTime, supAppTime, manAppTime, hodAppTime, category, destination, employeeId, reqDate, hod, supervisor, movementType, attSync, dueDate, happenDate, isPending, isAccepted, isExpired, isHalfDay, isLate, isAbsent, isUnSuccessfulAttdate, isLateCover);
    }
}
