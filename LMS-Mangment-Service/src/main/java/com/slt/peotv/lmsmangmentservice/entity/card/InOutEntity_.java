package com.slt.peotv.lmsmangmentservice.entity.card;

import jakarta.persistence.*;

import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "InOut_")
public class InOutEntity_ {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String employeeID;
    private String userId;

    private Date pushIn;
    private Date pushOut;

    private Time pushInTime;
    private Time pushOutTime;

    public Time getPushInTime() {
        return pushInTime;
    }

    public void setPushInTime(Time pushInTime) {
        this.pushInTime = pushInTime;
    }

    public Time getPushOutTime() {
        return pushOutTime;
    }

    public void setPushOutTime(Time pushOutTime) {
        this.pushOutTime = pushOutTime;
    }

    public Date getPushIn() {
        return pushIn;
    }

    public void setPushIn(Date pushIn) {
        this.pushIn = pushIn;
    }

    public Date getPushOut() {
        return pushOut;
    }

    public void setPushOut(Date pushOut) {
        this.pushOut = pushOut;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }
}
