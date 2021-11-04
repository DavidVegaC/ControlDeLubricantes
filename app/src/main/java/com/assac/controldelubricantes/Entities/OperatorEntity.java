package com.assac.controldelubricantes.Entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OperatorEntity implements Serializable {

    private int idSqlLite;
    private int IdOperator;
    private String OperatorKey;
    private String OperatorUser;
    private String OperatorPassword;

    private String PersonName;
    private String FirstLastName;
    private String SecondLastName;
    private String Photocheck;

    private int IdOperatorValidationMap;
    private String ValidationDescription;

    private String RegistrationStatus;

    private String ValorConsulta;
    private String MensajeConsulta;

    public OperatorEntity() {
    }

    public OperatorEntity( int idOperator, String operatorKey, String operatorUser, String operatorPassword, String personName, String firstLastName, String secondLastName, String photocheck, int idOperatorValidationMap, String validationDescription, String registrationStatus) {
        IdOperator = idOperator;
        OperatorKey = operatorKey;
        OperatorUser = operatorUser;
        OperatorPassword = operatorPassword;
        PersonName = personName;
        FirstLastName = firstLastName;
        SecondLastName = secondLastName;
        Photocheck = photocheck;
        IdOperatorValidationMap = idOperatorValidationMap;
        ValidationDescription = validationDescription;
        RegistrationStatus = registrationStatus;
    }

    public OperatorEntity(int idSqlLite, int idOperator, String operatorKey, String operatorUser, String operatorPassword, String personName, String firstLastName, String secondLastName, String photocheck, int idOperatorValidationMap, String validationDescription, String registrationStatus) {
        this.idSqlLite = idSqlLite;
        IdOperator = idOperator;
        OperatorKey = operatorKey;
        OperatorUser = operatorUser;
        OperatorPassword = operatorPassword;
        PersonName = personName;
        FirstLastName = firstLastName;
        SecondLastName = secondLastName;
        Photocheck = photocheck;
        IdOperatorValidationMap = idOperatorValidationMap;
        ValidationDescription = validationDescription;
        RegistrationStatus = registrationStatus;
    }

    public int getIdSqlLite() {
        return idSqlLite;
    }

    public void setIdSqlLite(int idSqlLite) {
        this.idSqlLite = idSqlLite;
    }

    public int getIdOperator() {
        return IdOperator;
    }

    public void setIdOperator(int idOperator) {
        IdOperator = idOperator;
    }

    public String getOperatorKey() {
        return OperatorKey;
    }

    public void setOperatorKey(String operatorKey) {
        OperatorKey = operatorKey;
    }

    public String getOperatorUser() {
        return OperatorUser;
    }

    public void setOperatorUser(String operatorUser) {
        OperatorUser = operatorUser;
    }

    public String getOperatorPassword() {
        return OperatorPassword;
    }

    public void setOperatorPassword(String operatorPassword) {
        OperatorPassword = operatorPassword;
    }

    public String getPersonName() {
        return PersonName;
    }

    public void setPersonName(String personName) {
        PersonName = personName;
    }

    public String getFirstLastName() {
        return FirstLastName;
    }

    public void setFirstLastName(String firstLastName) {
        FirstLastName = firstLastName;
    }

    public String getSecondLastName() {
        return SecondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        SecondLastName = secondLastName;
    }

    public String getPhotocheck() {
        return Photocheck;
    }

    public void setPhotocheck(String photocheck) {
        Photocheck = photocheck;
    }

    public int getIdOperatorValidationMap() {
        return IdOperatorValidationMap;
    }

    public void setIdOperatorValidationMap(int idOperatorValidationMap) {
        IdOperatorValidationMap = idOperatorValidationMap;
    }

    public String getValidationDescription() {
        return ValidationDescription;
    }

    public void setValidationDescription(String validationDescription) {
        ValidationDescription = validationDescription;
    }

    public String getRegistrationStatus() {
        return RegistrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        RegistrationStatus = registrationStatus;
    }

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
}
