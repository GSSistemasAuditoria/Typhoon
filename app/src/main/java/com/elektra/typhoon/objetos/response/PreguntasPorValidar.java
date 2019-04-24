package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 17/04/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class PreguntasPorValidar{

    @SerializedName("IdBarcos")
    @Expose
    private List<Integer> listIdBarco;

    @SerializedName("IdPregunta")
    @Expose
    private int idPregunta;

    public List<Integer> getListIdBarco() {
        return listIdBarco;
    }

    public void setListIdBarco(List<Integer> listIdBarco) {
        this.listIdBarco = listIdBarco;
    }

    public int getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(int idPregunta) {
        this.idPregunta = idPregunta;
    }
}