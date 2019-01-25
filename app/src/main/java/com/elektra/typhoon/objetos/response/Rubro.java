package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 18/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class Rubro {

    @SerializedName("ID_REVISION")
    @Expose
    private int idRevision;

    @SerializedName("ID_CHECKLIST")
    @Expose
    private int idChecklist;

    @SerializedName("ID_BARCO")
    @Expose
    private int id_barco;

    @SerializedName("ID_RUBRO")
    @Expose
    private int idRubro;

    @SerializedName("CALIFICACION")
    @Expose
    private int calificacion;

    private List<Pregunta> listPreguntas;

    public int getIdRevision() {
        return idRevision;
    }

    public void setIdRevision(int idRevision) {
        this.idRevision = idRevision;
    }

    public int getIdChecklist() {
        return idChecklist;
    }

    public void setIdChecklist(int idChecklist) {
        this.idChecklist = idChecklist;
    }

    public int getId_barco() {
        return id_barco;
    }

    public void setId_barco(int id_barco) {
        this.id_barco = id_barco;
    }

    public int getIdRubro() {
        return idRubro;
    }

    public void setIdRubro(int idRubro) {
        this.idRubro = idRubro;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public List<Pregunta> getListPreguntas() {
        return listPreguntas;
    }

    public void setListPreguntas(List<Pregunta> listPreguntas) {
        this.listPreguntas = listPreguntas;
    }
}
