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
public class DatosPorValidar {

    @SerializedName("IdSubanexos")
    @Expose
    private List<Integer> listIdSubanexos;

    @SerializedName("Preguntas")
    @Expose
    private List<PreguntasPorValidar> listPreguntas;

    public List<Integer> getListIdSubanexos() {
        return listIdSubanexos;
    }

    public void setListIdSubanexos(List<Integer> listIdSubanexos) {
        this.listIdSubanexos = listIdSubanexos;
    }

    public List<PreguntasPorValidar> getListPreguntas() {
        return listPreguntas;
    }

    public void setListPreguntas(List<PreguntasPorValidar> listPreguntas) {
        this.listPreguntas = listPreguntas;
    }
}
