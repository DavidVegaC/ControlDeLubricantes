package com.assac.controldelubricantes.Entities;

public class CompartmentEntity {
    private int idSqlLite;
    private int IdCompartment;
    private int IdProduct;

    private int IdCompartmentType;
    private String CompartmentName;
    private double Capacity;
    private int AlertCapacity;

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

    public CompartmentEntity(){

    }

    public CompartmentEntity(int idSqlLite, int idCompartment, int idProduct, int idCompartmentType, String compartmentName, double capacity, int alertCapacity, String registrationStatus) {
        this.idSqlLite = idSqlLite;
        IdCompartment = idCompartment;
        IdProduct = idProduct;
        IdCompartmentType = idCompartmentType;
        CompartmentName = compartmentName;
        Capacity = capacity;
        AlertCapacity = alertCapacity;
        RegistrationStatus = registrationStatus;
    }

    public CompartmentEntity(int idCompartment, int idProduct, int idCompartmentType, String compartmentName, double capacity, int alertCapacity, String registrationStatus) {
        IdCompartment = idCompartment;
        IdProduct = idProduct;
        IdCompartmentType = idCompartmentType;
        CompartmentName = compartmentName;
        Capacity = capacity;
        AlertCapacity = alertCapacity;
        RegistrationStatus = registrationStatus;
    }

    public int getIdSqlLite() {
        return idSqlLite;
    }

    public void setIdSqlLite(int idSqlLite) {
        this.idSqlLite = idSqlLite;
    }

    public int getIdCompartment() {
        return IdCompartment;
    }

    public void setIdCompartment(int idCompartment) {
        IdCompartment = idCompartment;
    }

    public int getIdProduct() {
        return IdProduct;
    }

    public void setIdProduct(int idProduct) {
        IdProduct = idProduct;
    }

    public int getIdCompartmentType() {
        return IdCompartmentType;
    }

    public void setIdCompartmentType(int idCompartmentType) {
        IdCompartmentType = idCompartmentType;
    }

    public String getCompartmentName() {
        return CompartmentName;
    }

    public void setCompartmentName(String compartmentName) {
        CompartmentName = compartmentName;
    }

    public double getCapacity() {
        return Capacity;
    }

    public void setCapacity(double capacity) {
        Capacity = capacity;
    }

    public int getAlertCapacity() {
        return AlertCapacity;
    }

    public void setAlertCapacity(int alertCapacity) {
        AlertCapacity = alertCapacity;
    }

    public String getRegistrationStatus() {
        return RegistrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        RegistrationStatus = registrationStatus;
    }
}
