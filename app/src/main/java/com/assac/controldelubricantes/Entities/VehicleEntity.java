package com.assac.controldelubricantes.Entities;

public class VehicleEntity {

    private int idSqlLite;
    private int IdVehicle;
    private int IdCompany;
    private int IdModel;

    private String Plate;
    private String VehicleDescription;

    private String RegistrationStatus;

    public String getValorConsulta() {
        return ValorConsulta;
    }

    public void setValorConsulta(String valorConsulta) {
        ValorConsulta = valorConsulta;
    }

    public String getMensajeConsulta() {
        return MensajeConsulta;
    }

    public void setMensajeConsulta(String mensajeConsulta) {
        MensajeConsulta = mensajeConsulta;
    }

    public String ValorConsulta;
    public String MensajeConsulta;

    public VehicleEntity() {
    }

    public VehicleEntity(int idSqlLite, int idVehicle, int idCompany, int idModel, String plate, String vehicleDescription, String registrationStatus) {
        this.idSqlLite = idSqlLite;
        IdVehicle = idVehicle;
        IdCompany = idCompany;
        IdModel = idModel;
        Plate = plate;
        VehicleDescription = vehicleDescription;
        RegistrationStatus = registrationStatus;
    }

    public VehicleEntity(int idVehicle, int idCompany, int idModel, String plate, String vehicleDescription, String registrationStatus) {
        IdVehicle = idVehicle;
        IdCompany = idCompany;
        IdModel = idModel;
        Plate = plate;
        VehicleDescription = vehicleDescription;
        RegistrationStatus = registrationStatus;
    }

    public int getIdSqlLite() {
        return idSqlLite;
    }

    public void setIdSqlLite(int idSqlLite) {
        this.idSqlLite = idSqlLite;
    }

    public int getIdVehicle() {
        return IdVehicle;
    }

    public void setIdVehicle(int idVehicle) {
        IdVehicle = idVehicle;
    }

    public int getIdCompany() {
        return IdCompany;
    }

    public void setIdCompany(int idCompany) {
        IdCompany = idCompany;
    }

    public int getIdModel() {
        return IdModel;
    }

    public void setIdModel(int idModel) {
        IdModel = idModel;
    }

    public String getPlate() {
        return Plate;
    }

    public void setPlate(String plate) {
        Plate = plate;
    }

    public String getVehicleDescription() {
        return VehicleDescription;
    }

    public void setVehicleDescription(String vehicleDescription) {
        VehicleDescription = vehicleDescription;
    }

    public String getRegistrationStatus() {
        return RegistrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        RegistrationStatus = registrationStatus;
    }
}

