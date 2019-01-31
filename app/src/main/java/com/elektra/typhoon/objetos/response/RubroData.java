package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 29/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class RubroData {

    @SerializedName("ESTATUS")
    @Expose
    private int estatus;

    @SerializedName("ID_RUBRO")
    @Expose
    private int idRubro;

    @SerializedName("NOMBRE")
    @Expose
    private String nombre;

    @SerializedName("Preguntas")
    @Expose
    private List<PreguntaData> listPreguntas;//*/

    private int idChecklist;
    private int idRevision;

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public int getIdRubro() {
        return idRubro;
    }

    public void setIdRubro(int idRubro) {
        this.idRubro = idRubro;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<PreguntaData> getListPreguntas() {
        return listPreguntas;
    }

    public void setListPreguntas(List<PreguntaData> listPreguntas) {
        this.listPreguntas = listPreguntas;
    }

    public int getIdChecklist() {
        return idChecklist;
    }

    public void setIdChecklist(int idChecklist) {
        this.idChecklist = idChecklist;
    }

    public int getIdRevision() {
        return idRevision;
    }

    public void setIdRevision(int idRevision) {
        this.idRevision = idRevision;
    }
}
