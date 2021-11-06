package com.assac.controldelubricantes.Entities;

public class ReasonEntity {
    private int idSqlLite;
    private int IdReason;
    private int IdProduct;

    private String ReasonName;
    private int ReasonNumber;

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

    public ReasonEntity(int idSqlLite, int idReason, int idProduct, String reasonName, int reasonNumber, String registrationStatus) {
        this.idSqlLite = idSqlLite;
        IdReason = idReason;
        IdProduct = idProduct;
        ReasonName = reasonName;
        ReasonNumber = reasonNumber;
        RegistrationStatus = registrationStatus;
    }

    public ReasonEntity(int idReason, int idProduct, String reasonName, int reasonNumber, String registrationStatus) {
        IdReason = idReason;
        IdProduct = idProduct;
        ReasonName = reasonName;
        ReasonNumber = reasonNumber;
        RegistrationStatus = registrationStatus;
    }

    public int getIdSqlLite() {
        return idSqlLite;
    }

    public void setIdSqlLite(int idSqlLite) {
        this.idSqlLite = idSqlLite;
    }

    public int getIdReason() {
        return IdReason;
    }

    public void setIdReason(int idReason) {
        IdReason = idReason;
    }

    public int getIdProduct() {
        return IdProduct;
    }

    public void setIdProduct(int idProduct) {
        IdProduct = idProduct;
    }

    public String getReasonName() {
        return ReasonName;
    }

    public void setReasonName(String reasonName) {
        ReasonName = reasonName;
    }

    public int getReasonNumber() {
        return ReasonNumber;
    }

    public void setReasonNumber(int reasonNumber) {
        ReasonNumber = reasonNumber;
    }

    public String getRegistrationStatus() {
        return RegistrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        RegistrationStatus = registrationStatus;
    }
}
