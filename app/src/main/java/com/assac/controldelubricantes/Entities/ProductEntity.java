package com.assac.controldelubricantes.Entities;

public class ProductEntity {

    private int idSqlLite;
    private int IdProduct;

    private int NumberProduct;
    private String ProductName;

    public String MeasurementUnit;
    public String ElipseCode;

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

    public ProductEntity() {
    }

    public ProductEntity(int idSqlLite, int idProduct, int numberProduct, String productName, String measurementUnit, String elipseCode, String registrationStatus) {
        this.idSqlLite = idSqlLite;
        IdProduct = idProduct;
        NumberProduct = numberProduct;
        ProductName = productName;
        MeasurementUnit = measurementUnit;
        ElipseCode = elipseCode;
        RegistrationStatus = registrationStatus;
    }

    public ProductEntity(int idProduct, int numberProduct, String productName, String measurementUnit, String elipseCode, String registrationStatus) {
        IdProduct = idProduct;
        NumberProduct = numberProduct;
        ProductName = productName;
        MeasurementUnit = measurementUnit;
        ElipseCode = elipseCode;
        RegistrationStatus = registrationStatus;
    }

    public int getIdSqlLite() {
        return idSqlLite;
    }

    public void setIdSqlLite(int idSqlLite) {
        this.idSqlLite = idSqlLite;
    }

    public int getIdProduct() {
        return IdProduct;
    }

    public void setIdProduct(int idProduct) {
        IdProduct = idProduct;
    }

    public int getNumberProduct() {
        return NumberProduct;
    }

    public void setNumberProduct(int numberProduct) {
        NumberProduct = numberProduct;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getMeasurementUnit() {
        return MeasurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        MeasurementUnit = measurementUnit;
    }

    public String getElipseCode() {
        return ElipseCode;
    }

    public void setElipseCode(String elipseCode) {
        ElipseCode = elipseCode;
    }

    public String getRegistrationStatus() {
        return RegistrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        RegistrationStatus = registrationStatus;
    }
}
