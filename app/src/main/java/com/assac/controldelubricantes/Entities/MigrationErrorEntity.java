package com.assac.controldelubricantes.Entities;

import java.io.Serializable;

public class MigrationErrorEntity implements Serializable {

    private int IdMigrationError;
    private String MigrationStartDate;
    private String ErrorDescription;
    private String ErrorDate;
    private String RegistrationUser;

    public MigrationErrorEntity() {
    }

    public MigrationErrorEntity(String migrationStartDate, String errorDescription, String errorDate, String registrationUser) {
        MigrationStartDate = migrationStartDate;
        ErrorDescription = errorDescription;
        ErrorDate = errorDate;
        RegistrationUser = registrationUser;
    }

    public int getIdMigrationError() {
        return IdMigrationError;
    }

    public void setIdMigrationError(int idMigrationError) {
        IdMigrationError = idMigrationError;
    }

    public String getMigrationStartDate() {
        return MigrationStartDate;
    }

    public void setMigrationStartDate(String migrationStartDate) {
        MigrationStartDate = migrationStartDate;
    }

    public String getErrorDescription() {
        return ErrorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        ErrorDescription = errorDescription;
    }

    public String getErrorDate() {
        return ErrorDate;
    }

    public void setErrorDate(String errorDate) {
        ErrorDate = errorDate;
    }

    public String getRegistrationUser() {
        return RegistrationUser;
    }

    public void setRegistrationUser(String registrationUser) {
        RegistrationUser = registrationUser;
    }
}