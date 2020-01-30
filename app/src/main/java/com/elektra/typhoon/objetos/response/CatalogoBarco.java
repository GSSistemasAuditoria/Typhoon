package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 30/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class CatalogoBarco {

    @SerializedName("ID_BARCO")
    @Expose
    private int idBarco;

    @SerializedName("NOMBRE")
    @Expose
    private String nombre;

    @SerializedName("LONGITUD")
    @Expose
    private double longitud;

    @SerializedName("LATITUD")
    @Expose
    private double latitud;

    @SerializedName("RADIO")
    @Expose
    private float radio;

    public CatalogoBarco() {
    }

    public void updateBarco(CatalogoBarco mBarco) {
        this.idBarco = mBarco.idBarco;
        this.nombre = mBarco.nombre;
        this.longitud = mBarco.longitud;
        this.latitud = mBarco.latitud;
        this.radio = mBarco.radio;
        this.listRubros = mBarco.listRubros;
    }

    private List<RubroData> listRubros;

    public int getIdBarco() {
        return idBarco;
    }

    public void setIdBarco(int idBarco) {
        this.idBarco = idBarco;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<RubroData> getListRubros() {
        return listRubros;
    }

    public void setListRubros(List<RubroData> listRubros) {
        this.listRubros = listRubros;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public float getRadio() {
        return radio;
    }

    public void setRadio(float radio) {
        this.radio = radio;
    }
}
