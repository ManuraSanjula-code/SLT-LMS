package com.slt.peotv.lmsmangmentservice.entity;

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

    @Column(nullable = false)
    public String publicId; //✅ ***

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; //✅ ***

    @Column(name = "In_Time", length = 45, nullable = false)
    private String inTime; /// Swipe Error ✅

    @Column(name = "Out_Time", length = 45, nullable = false)
    private String outTime; /// Swipe Error ✅

    @Column(name = "Comment", columnDefinition = "TEXT")
    private String comment; //✅

    @Column(name = "Status", length = 45, nullable = false)
    private String status = "Pending (HOD approval)";

    @Column(name = "Log_Time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logTime; /// System Time //✅

    @Column(name = "Sup_app_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date supAppTime; /// Supervisor approved Time

    @Column(name = "Man_App_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date manAppTime; /// manager approved Time

    @Column(name = "HOD_App_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date hodAppTime; /// HOD approved Time ✅
    ///
    @Column(name = "category", length = 45)
    private String category; //✅

    @Column(name = "Destination", length = 45)
    private String destination; ///loaction where employee went ✅

    @Column(name = "Employee_ID", length = 45, nullable = false)
    private String employeeId; //✅

    @Column(name = "REQ_TIME", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reqDate;  /// Request time ✅

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity hod; /// hodUserId

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity supervisor; ///supervicerId

    @Column(name = "ATT_SYNC")
    private Integer attSync = 0;

    private Date absentdate;
    private Date latedate;
    private Date unSuccessfulAttdate; //unSuccessful attendance

    public Date getUnSuccessfulAttdate() {
        return unSuccessfulAttdate;
    }

    public void setUnSuccessfulAttdate(Date unSuccessfulAttdate) {
        this.unSuccessfulAttdate = unSuccessfulAttdate;
    }

    public Date getLatedate() {
        return latedate;
    }

    public void setLatedate(Date latedate) {
        this.latedate = latedate;
    }

    public Date getAbsentdate() {
        return absentdate;
    }

    public void setAbsentdate(Date absentdate) {
        this.absentdate = absentdate;
    }


    private MovementType movementType; //✅ ***

    public Date getHodAppTime() {
        return hodAppTime;
    }

    public void setHodAppTime(Date hodAppTime) {
        this.hodAppTime = hodAppTime;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

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

    public Date getReqDate() {
        return reqDate;
    }

    public void setReqDate(Date reqDate) {
        this.reqDate = reqDate;
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

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Integer getAttSync() {
        return attSync;
    }

    public void setAttSync(Integer attSync) {
        this.attSync = attSync;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MovementsEntity that = (MovementsEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(publicId, that.publicId) && Objects.equals(user, that.user) && Objects.equals(inTime, that.inTime) && Objects.equals(outTime, that.outTime) && Objects.equals(comment, that.comment) && Objects.equals(status, that.status) && Objects.equals(logTime, that.logTime) && Objects.equals(supAppTime, that.supAppTime) && Objects.equals(manAppTime, that.manAppTime) && Objects.equals(hodAppTime, that.hodAppTime) && Objects.equals(category, that.category) && Objects.equals(destination, that.destination) && Objects.equals(employeeId, that.employeeId) && Objects.equals(reqDate, that.reqDate) && Objects.equals(hod, that.hod) && Objects.equals(supervisor, that.supervisor) && Objects.equals(attSync, that.attSync) && Objects.equals(absentdate, that.absentdate) && Objects.equals(latedate, that.latedate) && Objects.equals(unSuccessfulAttdate, that.unSuccessfulAttdate) && movementType == that.movementType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicId, user, inTime, outTime, comment, status, logTime, supAppTime, manAppTime, hodAppTime, category, destination, employeeId, reqDate, hod, supervisor, attSync, absentdate, latedate, unSuccessfulAttdate, movementType);
    }
}
