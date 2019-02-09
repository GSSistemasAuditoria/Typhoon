package com.elektra.typhoon.objetos.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 06/02/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class EvidenciaData {

    @SerializedName("ID_EVIDENCIA")
    @Expose
    private String idEvidencia;

    @SerializedName("NOMBRE")
    @Expose
    private String nombre;

    @SerializedName("CONTENIDO")
    @Expose
    private String contenido;

    @SerializedName("ID_ESTATUS")
    @Expose
    private int idEstatus;

    @SerializedName("ID_ETAPA")
    @Expose
    private int idEtapa;

    @SerializedName("ID_REGISTRO")
    @Expose
    private int idRegistro;

    public String getIdEvidencia() {
        return idEvidencia;
    }

    public void setIdEvidencia(String idEvidencia) {
        this.idEvidencia = idEvidencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public int getIdEstatus() {
        return idEstatus;
    }

    public void setIdEstatus(int idEstatus) {
        this.idEstatus = idEstatus;
    }

    public int getIdEtapa() {
        return idEtapa;
    }

    public void setIdEtapa(int idEtapa) {
        this.idEtapa = idEtapa;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }
}
