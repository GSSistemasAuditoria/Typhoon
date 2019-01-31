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
}
