package com.elektra.typhoon.objetos.response;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Proyecto: TYPHOONE
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 29/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class RespuestaData {

    @SerializedName("ID_BARCO")
    @Expose
    private int idBarco;

    @SerializedName("ID_CHECKLIST")
    @Expose
    private int idChecklist;

    @SerializedName("ID_ESTATUS")
    @Expose
    private int idEstatus;

    @SerializedName("ID_PREGUNTA")
    @Expose
    private int idPregunta;

    @SerializedName("ID_REGISTRO")
    @Expose
    private int idRegistro;

    @SerializedName("ID_RESPUESTA")
    @Expose
    private Integer idRespuesta;

    @SerializedName("ID_REVISION")
    @Expose
    private int idRevision;

    @SerializedName("ID_RUBRO")
    @Expose
    private int idRubro;

    private int sincronizado;

    public int getIdBarco() {
        return idBarco;
    }

    public void setIdBarco(int idBarco) {
        this.idBarco = idBarco;
    }

    public int getIdChecklist() {
        return idChecklist;
    }

    public void setIdChecklist(int idChecklist) {
        this.idChecklist = idChecklist;
    }

    public int getIdEstatus() {
        return idEstatus;
    }

    public void setIdEstatus(int idEstatus) {
        this.idEstatus = idEstatus;
    }

    public int getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(int idPregunta) {
        this.idPregunta = idPregunta;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public Integer getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(Integer idRespuesta) {
        this.idRespuesta = idRespuesta;
    }

    public int getIdRevision() {
        return idRevision;
    }

    public void setIdRevision(int idRevision) {
        this.idRevision = idRevision;
    }

    public int getIdRubro() {
        return idRubro;
    }

    public void setIdRubro(int idRubro) {
        this.idRubro = idRubro;
    }

    public int getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RespuestaData){
            return this.idPregunta == ((RespuestaData) obj).idPregunta;
        }
        return super.equals(obj);
    }
}
