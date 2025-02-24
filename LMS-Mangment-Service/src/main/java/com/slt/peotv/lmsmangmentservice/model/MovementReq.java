package com.slt.peotv.lmsmangmentservice.model;

import com.slt.peotv.lmsmangmentservice.model.types.MovementType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MovementReq {
    private String employeeId; //✅
    private String userId;
    private MovementType movementType; //✅
    private String comment; //✅
    private String inTime; //✅
    private String outTime; //✅
    private String destination; //✅
    private String category; //✅

    private Date absentdate;
    private Date latedate;
    private Date unSuccessfulAttdate; //unSuccessful attendance

    private Boolean isLate;
    private Boolean isAbsent;
    private Boolean isLateCover;
    private Boolean isUnSuccessfulAttdate;

    public void setUnSuccessfulAttdate(Boolean unSuccessfulAttdate) {
        isUnSuccessfulAttdate = unSuccessfulAttdate;
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

    public Boolean getLateCover() {
        return isLateCover;
    }

    public void setLateCover(Boolean lateCover) {
        isLateCover = lateCover;
    }

    public Date getAbsentdate() {
        return absentdate;
    }

    public void setAbsentdate(Date absentdate) {
        this.absentdate = absentdate;
    }

    public Date getLatedate() {
        return latedate;
    }

    public void setLatedate(Date latedate) {
        this.latedate = latedate;
    }

    public Date getUnSuccessfulAttdate() {
        return unSuccessfulAttdate;
    }

    public void setUnSuccessfulAttdate(Date unSuccessfulAttdate) {
        this.unSuccessfulAttdate = unSuccessfulAttdate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
