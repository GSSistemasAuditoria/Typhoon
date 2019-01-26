package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 16/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class Barco {

    @SerializedName("ID_BARCO")
    @Expose
    private int idBarco;

    @SerializedName("NOMBRE")
    @Expose
    private String nombre;

    private List<Rubro> listRubros;

    public int getIdBarco() {
        return idBarco;
    }

    public Barco(){

    }

    public Barco(int idBarco,String nombre){
        this.idBarco = idBarco;
        this.nombre = nombre;
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

    public List<Rubro> getListRubros() {
        return listRubros;
    }

    public void setListRubros(List<Rubro> listRubros) {
        this.listRubros = listRubros;
    }
}
