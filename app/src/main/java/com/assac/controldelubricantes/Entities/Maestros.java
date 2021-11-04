package com.assac.controldelubricantes.Entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Maestros implements Serializable {


    @Expose
    @SerializedName("Hardwares")
    public List<Hardware> Hardwares;

    @Expose
    @SerializedName("Drivers")
    public List<Driver> Drivers;

    @Expose
    @SerializedName("Hoses")
    public List<Hose> Hoses;

    @Expose
    @SerializedName("Operators")
    public List<OperatorEntity> operatorEntities;

    @Expose
    @SerializedName("Plates")
    public List<Plate> Plates;

    @Expose
    @SerializedName("WorkShifts")
    public List<WorkShift> WorkShifts;

    public Maestros() {
        List<Hardware> Hardwares = new ArrayList<>();
        List<Driver> Drivers= new ArrayList<>();
        List<Hose> Hoses= new ArrayList<>();
        List<OperatorEntity> operatorEntities = new ArrayList<>();
        List<Plate> Plates= new ArrayList<>();
    }

    public Maestros(List<Hardware> hardwares, List<Driver> drivers, List<Hose> hoses, List<OperatorEntity> operatorEntities, List<Plate> plates, List<WorkShift> workShifts) {
        Hardwares = hardwares;
        Drivers = drivers;
        Hoses = hoses;
        this.operatorEntities = operatorEntities;
        Plates = plates;
        WorkShifts = workShifts;
    }


    public List<Hardware> getHardwares() {
        return Hardwares;
    }

    public void setHardwares(List<Hardware> hardwares) {
        Hardwares = hardwares;
    }

    public List<Driver> getDrivers() {
        return Drivers;
    }

    public void setDrivers(List<Driver> Drivers) {
        this.Drivers = Drivers;
    }

    public List<Hose> getHoses() {
        return Hoses;
    }

    public void setHoses(List<Hose> hoses) {
        Hoses = hoses;
    }

    public List<OperatorEntity> getOperatorEntities() {
        return operatorEntities;
    }

    public void setOperatorEntities(List<OperatorEntity> operatorEntities) {
        this.operatorEntities = operatorEntities;
    }

    public List<Plate> getPlates() {
        return Plates;
    }

    public void setPlates(List<Plate> plates) {
        Plates = plates;
    }

    public List<WorkShift> getWorkShifts() {
        return WorkShifts;
    }

    public void setWorkShifts(List<WorkShift> workShifts) {
        WorkShifts = workShifts;
    }
}
