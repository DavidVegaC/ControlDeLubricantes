package com.assac.controldelubricantes.Entities;

public class ModelCompartmentEntity {
    private int idSqlLite;
    private int IdModelCompartment;
    private int IdModel;
    private int IdCompartment;
    private int CompartmentNumber;

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

    public ModelCompartmentEntity() {
    }

    public ModelCompartmentEntity(int idSqlLite, int idModelCompartment, int idModel, int idCompartment, int compartmentNumber, String registrationStatus) {
        this.idSqlLite = idSqlLite;
        IdModelCompartment = idModelCompartment;
        IdModel = idModel;
        IdCompartment = idCompartment;
        CompartmentNumber = compartmentNumber;
        RegistrationStatus = registrationStatus;
    }

    public ModelCompartmentEntity(int idModelCompartment, int idModel, int idCompartment, int compartmentNumber, String registrationStatus) {
        IdModelCompartment = idModelCompartment;
        IdModel = idModel;
        IdCompartment = idCompartment;
        CompartmentNumber = compartmentNumber;
        RegistrationStatus = registrationStatus;
    }

    public int getIdSqlLite() {
        return idSqlLite;
    }

    public void setIdSqlLite(int idSqlLite) {
        this.idSqlLite = idSqlLite;
    }

    public int getIdModelCompartment() {
        return IdModelCompartment;
    }

    public void setIdModelCompartment(int idModelCompartment) {
        IdModelCompartment = idModelCompartment;
    }

    public int getIdModel() {
        return IdModel;
    }

    public void setIdModel(int idModel) {
        IdModel = idModel;
    }

    public int getIdCompartment() {
        return IdCompartment;
    }

    public void setIdCompartment(int idCompartment) {
        IdCompartment = idCompartment;
    }

    public int getCompartmentNumber() {
        return CompartmentNumber;
    }

    public void setCompartmentNumber(int compartmentNumber) {
        CompartmentNumber = compartmentNumber;
    }

    public String getRegistrationStatus() {
        return RegistrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        RegistrationStatus = registrationStatus;
    }
}
