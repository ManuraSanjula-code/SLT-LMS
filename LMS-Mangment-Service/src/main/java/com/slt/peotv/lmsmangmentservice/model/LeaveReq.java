package com.slt.peotv.lmsmangmentservice.model;

import java.util.Date;

public class LeaveReq {
    private Date fromDate;
    private Date toDate;
    private String leaveType;
    private String leaveCategory;
    private String description;
    private Boolean isHalfDay;
    private Long numOfDays;

    public Long getNumOfDays() {
        return numOfDays;
    }

    public void setNumOfDays(Long numOfDays) {
        this.numOfDays = numOfDays;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getLeaveCategory() {
        return leaveCategory;
    }

    public void setLeaveCategory(String leaveCategory) {
        this.leaveCategory = leaveCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getHalfDay() {
        return isHalfDay;
    }

    public void setHalfDay(Boolean halfDay) {
        isHalfDay = halfDay;
    }
}
