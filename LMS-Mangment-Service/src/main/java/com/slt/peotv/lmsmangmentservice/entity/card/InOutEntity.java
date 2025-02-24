package com.slt.peotv.lmsmangmentservice.entity.card;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "InOut")
@Setter
@Getter
@EqualsAndHashCode
public class InOutEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String employeeID;
    private String userId;
    private Date date;
    private Date punchInMoa; // earliest moaning time -- date
    private Date punchInEv; // earliest eve time -- date

    private Time timeMoa; // earliest moaning time -- time
    private Time timeEve;// earliest eve time -- time

    private Boolean InOut;
    private Boolean isMoaning;

    private Boolean isEvening;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getPunchInMoa() {
        return punchInMoa;
    }

    public void setPunchInMoa(Date pushInMoa) {
        this.punchInMoa = pushInMoa;
    }

    public Date getPunchInEv() {
        return punchInEv;
    }

    public void setPunchInEv(Date pushInEv) {
        this.punchInEv = pushInEv;
    }

    public Time getTimeMoa() {
        return timeMoa;
    }

    public void setTimeMoa(Time timeMoa) {
        this.timeMoa = timeMoa;
    }

    public Time getTimeEve() {
        return timeEve;
    }

    public void setTimeEve(Time timeEve) {
        this.timeEve = timeEve;
    }

    public Boolean getInOut() {
        return InOut;
    }

    public void setInOut(Boolean inOut) {
        InOut = inOut;
    }

    public Boolean getMoaning() {
        return isMoaning;
    }

    public void setMoaning(Boolean moaning) {
        isMoaning = moaning;
    }

    public Boolean getEvening() {
        return isEvening;
    }

    public void setEvening(Boolean evening) {
        isEvening = evening;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InOutEntity that = (InOutEntity) o;
        return id == that.id && Objects.equals(employeeID, that.employeeID) && Objects.equals(userId, that.userId) && Objects.equals(date, that.date) && Objects.equals(punchInMoa, that.punchInMoa) && Objects.equals(punchInEv, that.punchInEv) && Objects.equals(timeMoa, that.timeMoa) && Objects.equals(timeEve, that.timeEve) && Objects.equals(InOut, that.InOut) && Objects.equals(isMoaning, that.isMoaning) && Objects.equals(isEvening, that.isEvening);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, employeeID, userId, date, punchInMoa, punchInEv, timeMoa, timeEve, InOut, isMoaning, isEvening);
    }
}
