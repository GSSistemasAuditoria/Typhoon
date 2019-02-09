package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 01/02/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class TipoRespuesta {

    @SerializedName("DESCRIPCION")
    @Expose
    private String descripcion;

    @SerializedName("ID_RESPUESTA")
    @Expose
    private int idRespuesta;

    @SerializedName("ID_TIPO_RESPUESTA")
    @Expose
    private int idTipoRespuesta;

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(int idRespuesta) {
        this.idRespuesta = idRespuesta;
    }

    public int getIdTipoRespuesta() {
        return idTipoRespuesta;
    }

    public void setIdTipoRespuesta(int idTipoRespuesta) {
        this.idTipoRespuesta = idTipoRespuesta;
    }
}
