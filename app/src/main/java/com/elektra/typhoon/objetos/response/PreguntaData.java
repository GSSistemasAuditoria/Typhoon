package com.elektra.typhoon.objetos.response;

import android.widget.RadioGroup;

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
public class PreguntaData {

    @SerializedName("DESCRIPCION")
    @Expose
    private String descripcion;

    @SerializedName("ESTATUS")
    @Expose
    private int estatus;

    @SerializedName("ID_PREGUNTA")
    @Expose
    private int idPregunta;

    @SerializedName("ID_RUBRO")
    @Expose
    private int idRubro;

    @SerializedName("ID_TIPO_RESPUESTA")
    @Expose
    private int idTipoRespuesta;

    @SerializedName("Evidencias")
    @Expose
    private List<Evidencia> listEvidencias;

    @SerializedName("IS_TIERRA")
    @Expose
    private boolean isTierra;

    private int idRevision;
    private int idChecklist;

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

    public int getIdTipoRespuesta() {
        return idTipoRespuesta;
    }

    public void setIdTipoRespuesta(int idTipoRespuesta) {
        this.idTipoRespuesta = idTipoRespuesta;
    }

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

    public List<Evidencia> getListEvidencias() {
        return listEvidencias;
    }

    public void setListEvidencias(List<Evidencia> listEvidencias) {
        this.listEvidencias = listEvidencias;
    }

    public boolean isTierra() {
        return isTierra;
    }

    public void setTierra(boolean tierra) {
        isTierra = tierra;
    }
}
