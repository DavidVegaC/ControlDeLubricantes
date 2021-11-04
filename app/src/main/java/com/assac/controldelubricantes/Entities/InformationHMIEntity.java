package com.assac.controldelubricantes.Entities;

public class InformationHMIEntity {

    //LLegada de Datos
    public int indiceBomba;
    public int numeroBomba;

    public int opCodeSecundario;

    public String placa;
    public String tipoTAG;
    public String idVehiculo;
    public String idVID;
    public int codigoProducto;
    public int idCompartimiento;
    public int codigoCliente;
    public int codigoArea;
    public String prefix;
    public String latitud;
    public String longitud;

    public InformationHMIEntity() {
    }

    public InformationHMIEntity(int indiceBomba, int numeroBomba, int opCodeSecundario, String placa, String tipoTAG, String idVehiculo, String idVID, int codigoProducto, int idCompartimiento, int codigoCliente, int codigoArea, String prefix, String latitud, String longitud) {
        this.indiceBomba = indiceBomba;
        this.numeroBomba = numeroBomba;
        this.opCodeSecundario = opCodeSecundario;
        this.placa = placa;
        this.tipoTAG = tipoTAG;
        this.idVehiculo = idVehiculo;
        this.idVID = idVID;
        this.codigoProducto = codigoProducto;
        this.idCompartimiento = idCompartimiento;
        this.codigoCliente = codigoCliente;
        this.codigoArea = codigoArea;
        this.prefix = prefix;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public int getIndiceBomba() {
        return indiceBomba;
    }

    public void setIndiceBomba(int indiceBomba) {
        this.indiceBomba = indiceBomba;
    }

    public int getNumeroBomba() {
        return numeroBomba;
    }

    public void setNumeroBomba(int numeroBomba) {
        this.numeroBomba = numeroBomba;
    }

    public int getOpCodeSecundario() {
        return opCodeSecundario;
    }

    public void setOpCodeSecundario(int opCodeSecundario) {
        this.opCodeSecundario = opCodeSecundario;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getTipoTAG() {
        return tipoTAG;
    }

    public void setTipoTAG(String tipoTAG) {
        this.tipoTAG = tipoTAG;
    }

    public String getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(String idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getIdVID() {
        return idVID;
    }

    public void setIdVID(String idVID) {
        this.idVID = idVID;
    }

    public int getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(int codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public int getIdCompartimiento() {
        return idCompartimiento;
    }

    public void setIdCompartimiento(int idCompartimiento) {
        this.idCompartimiento = idCompartimiento;
    }

    public int getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(int codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public int getCodigoArea() {
        return codigoArea;
    }

    public void setCodigoArea(int codigoArea) {
        this.codigoArea = codigoArea;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
