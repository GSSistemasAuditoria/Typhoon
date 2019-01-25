package com.elektra.typhoon.objetos.response;

import android.widget.RadioGroup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 17/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class Pregunta {

    @SerializedName("ID_PREGUNTA")
    @Expose
        private int idPregunta;
    @SerializedName("ID_RUBRO")
    @Expose
            private int idRubro;
    @SerializedName("DESCRIPCION")
    @Expose
            private String descripcion;
    @SerializedName("ESTATUS")
    @Expose
    private int estatus;

    private List<Evidencia> listEvidencias;

    private RadioGroup radioGroup;

    private boolean cumple;

    public Pregunta(){

    }

    public Pregunta(int idPregunta,int idRubro,String descripcion,int estatus){
        this.idPregunta = idPregunta;
        this.idRubro = idRubro;
        this.descripcion = descripcion;
        this.estatus = estatus;
    }

    public int getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(int idPregunta) {
        this.idPregunta = idPregunta;
    }

    public int getIdRubro() {
        return idRubro;
    }

    public void setIdRubro(int idRubro) {
        this.idRubro = idRubro;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public List<Evidencia> getListEvidencias() {
        return listEvidencias;
    }

    public void setListEvidencias(List<Evidencia> listEvidencias) {
        this.listEvidencias = listEvidencias;
    }

    public RadioGroup getRadioGroup() {
        return radioGroup;
    }

    public void setRadioGroup(RadioGroup radioGroup) {
        this.radioGroup = radioGroup;
    }

    public boolean isCumple() {
        return cumple;
    }

    public void setCumple(boolean cumple) {
        this.cumple = cumple;
    }
}
