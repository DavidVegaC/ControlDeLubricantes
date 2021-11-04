package com.assac.controldelubricantes.Entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WorkShift implements Serializable {

    @Expose
    @SerializedName("IdWorkShift")
    public int IdWorkShift;

    @Expose
    @SerializedName("ShiftName")
    public String ShiftName;

    @Expose
    @SerializedName("ShiftNickName")
    public String ShiftNickName;

    @Expose
    @SerializedName("StartTime")
    public String StartTime;

    @Expose
    @SerializedName("EndTime")
    public String EndTime;

    public WorkShift(int idWorkShift, String shiftName, String shiftNickName, String startTime, String endTime) {
        IdWorkShift = idWorkShift;
        ShiftName = shiftName;
        ShiftNickName = shiftNickName;
        StartTime = startTime;
        EndTime = endTime;
    }

    public WorkShift() {
    }

    public int getIdWorkShift() {
        return IdWorkShift;
    }

    public void setIdWorkShift(int idWorkShift) {
        IdWorkShift = idWorkShift;
    }

    public String getShiftName() {
        return ShiftName;
    }

    public void setShiftName(String shiftName) {
        ShiftName = shiftName;
    }

    public String getShiftNickName() {
        return ShiftNickName;
    }

    public void setShiftNickName(String shiftNickName) {
        ShiftNickName = shiftNickName;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }
}
