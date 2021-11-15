package com.assac.controldelubricantes.Entities;

public class DataFormEntity {

    //LLegada de Datos
    public int indiceBomba;
    public int numeroBomba;
    public String nombreManguera;

    public int direccion;

    public String placa;
    private String tag;

    private int idCompartimiento;

    public boolean solicitaConductor;
    public boolean solicitaOperador;
    public boolean solicitaHorometro;
    public boolean solicitaKilometraje;
    public boolean validaHorometro;
    public boolean validaKilometraje;
    public boolean solicitaPreseteo;

    public double kilometrajeActual;
    public double kilometrajeMinimo;
    public double kilometrajeMaximo;

    public double horometroActual;
    public double horometroMinimo;
    public double horometroMaximo;

    public int preseteoMinimo;
    public int preseteoMaximo;


    //Salida de Datos
    public String kilometraje;
    public String horometro;
    public int preSeteo;
    public String idConductor;
    public String nombreConductor;
    public String idOperador;
    public String nombreOperador;
    public String latitud;
    public String longitud;

    public int razon;

    public String comentario;

    public DataFormEntity() {
    }

    public DataFormEntity(int indiceBomba, int numeroBomba, String placa, String tag, int idCompartimiento, boolean solicitaConductor, boolean solicitaOperador, boolean solicitaHorometro, boolean solicitaKilometraje, boolean validaHorometro, boolean validaKilometraje, boolean solicitaPreseteo, double kilometrajeActual, double kilometrajeMinimo, double kilometrajeMaximo, double horometroActual, double horometroMinimo, double horometroMaximo, int preseteoMinimo, int preseteoMaximo) {
        this.indiceBomba = indiceBomba;
        this.numeroBomba = numeroBomba;
        this.placa = placa;
        this.tag = tag;
        this.idCompartimiento = idCompartimiento;
        this.solicitaConductor = solicitaConductor;
        this.solicitaOperador = solicitaOperador;
        this.solicitaHorometro = solicitaHorometro;
        this.solicitaKilometraje = solicitaKilometraje;
        this.validaHorometro = validaHorometro;
        this.validaKilometraje = validaKilometraje;
        this.solicitaPreseteo = solicitaPreseteo;
        this.kilometrajeActual = kilometrajeActual;
        this.kilometrajeMinimo = kilometrajeMinimo;
        this.kilometrajeMaximo = kilometrajeMaximo;
        this.horometroActual = horometroActual;
        this.horometroMinimo = horometroMinimo;
        this.horometroMaximo = horometroMaximo;
        this.preseteoMinimo = preseteoMinimo;
        this.preseteoMaximo = preseteoMaximo;
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

    public int getDireccion() {
        return direccion;
    }

    public void setDireccion(int direccion) {
        this.direccion = direccion;
    }

    public String getNombreManguera() {
        return nombreManguera;
    }

    public void setNombreManguera(String nombreManguera) {
        this.nombreManguera = nombreManguera;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getIdCompartimiento() {
        return idCompartimiento;
    }

    public void setIdCompartimiento(int idCompartimiento) {
        this.idCompartimiento = idCompartimiento;
    }

    public boolean isSolicitaConductor() {
        return solicitaConductor;
    }

    public void setSolicitaConductor(boolean solicitaConductor) {
        this.solicitaConductor = solicitaConductor;
    }

    public boolean isSolicitaOperador() {
        return solicitaOperador;
    }

    public void setSolicitaOperador(boolean solicitaOperador) {
        this.solicitaOperador = solicitaOperador;
    }

    public boolean isSolicitaHorometro() {
        return solicitaHorometro;
    }

    public void setSolicitaHorometro(boolean solicitaHorometro) {
        this.solicitaHorometro = solicitaHorometro;
    }

    public boolean isSolicitaKilometraje() {
        return solicitaKilometraje;
    }

    public void setSolicitaKilometraje(boolean solicitaKilometraje) {
        this.solicitaKilometraje = solicitaKilometraje;
    }

    public boolean isValidaHorometro() {
        return validaHorometro;
    }

    public void setValidaHorometro(boolean validaHorometro) {
        this.validaHorometro = validaHorometro;
    }

    public boolean isValidaKilometraje() {
        return validaKilometraje;
    }

    public void setValidaKilometro(boolean validaKilometraje) {
        this.validaKilometraje = validaKilometraje;
    }

    public boolean isSolicitaPreseteo() {
        return solicitaPreseteo;
    }

    public void setSolicitaPreseteo(boolean solicitaPreseteo) {
        this.solicitaPreseteo = solicitaPreseteo;
    }

    public double getKilometrajeActual() {
        return kilometrajeActual;
    }

    public void setKilometrajeActual(double kilometrajeActual) {
        this.kilometrajeActual = kilometrajeActual;
    }

    public double getKilometrajeMinimo() {
        return kilometrajeMinimo;
    }

    public void setKilometrajeMinimo(double kilometrajeMinimo) {
        this.kilometrajeMinimo = kilometrajeMinimo;
    }

    public double getKilometrajeMaximo() {
        return kilometrajeMaximo;
    }

    public void setKilometrajeMaximo(double kilometrajeMaximo) {
        this.kilometrajeMaximo = kilometrajeMaximo;
    }

    public double getHorometroActual() {
        return horometroActual;
    }

    public void setHorometroActual(double horometroActual) {
        this.horometroActual = horometroActual;
    }

    public double getHorometroMinimo() {
        return horometroMinimo;
    }

    public void setHorometroMinimo(double horometroMinimo) {
        this.horometroMinimo = horometroMinimo;
    }

    public double getHorometroMaximo() {
        return horometroMaximo;
    }

    public void setHorometroMaximo(double horometroMaximo) {
        this.horometroMaximo = horometroMaximo;
    }

    public String getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(String kilometraje) {
        this.kilometraje = kilometraje;
    }

    public String getHorometro() {
        return horometro;
    }

    public void setHorometro(String horometro) {
        this.horometro = horometro;
    }

    public int getPreSeteo() {
        return preSeteo;
    }

    public void setPreSeteo(int preSeteo) {
        this.preSeteo = preSeteo;
    }

    public String getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(String idConductor) {
        this.idConductor = idConductor;
    }

    public String getNombreConductor() {
        return nombreConductor;
    }

    public void setNombreConductor(String nombreConductor) {
        this.nombreConductor = nombreConductor;
    }

    public String getIdOperador() {
        return idOperador;
    }

    public void setIdOperador(String idOperador) {
        this.idOperador = idOperador;
    }

    public String getNombreOperador() {
        return nombreOperador;
    }

    public void setNombreOperador(String nombreOperador) {
        this.nombreOperador = nombreOperador;
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

    public int getPreseteoMinimo() {
        return preseteoMinimo;
    }

    public void setPreseteoMinimo(int preseteoMinimo) {
        this.preseteoMinimo = preseteoMinimo;
    }

    public int getPreseteoMaximo() {
        return preseteoMaximo;
    }

    public void setPreseteoMaximo(int preseteoMaximo) {
        this.preseteoMaximo = preseteoMaximo;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getRazon() {
        return razon;
    }

    public void setRazon(int razon) {
        this.razon = razon;
    }
}
