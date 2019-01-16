package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 14/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class FolioRevision {

    @SerializedName("ESTATUS")
    @Expose
    private int estatus;
    @SerializedName("FECHA_FIN")
    @Expose
    private String fechaFin;
    @SerializedName("FECHA_INICIO")
    @Expose
    private String fechaInicio;
    @SerializedName("ID_REVISION")
    @Expose
    private int idRevision;
    @SerializedName("ID_TIPO_REVISION")
    @Expose
    private int idTipoRevision;
    @SerializedName("ID_USUARIO")
    @Expose
    private String idUsuario;
    @SerializedName("NOMBRE")
    @Expose
    private String nombre;

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public int getIdRevision() {
        return idRevision;
    }

    public void setIdRevision(int idRevision) {
        this.idRevision = idRevision;
    }

    public int getIdTipoRevision() {
        return idTipoRevision;
    }

    public void setIdTipoRevision(int idTipoRevision) {
        this.idTipoRevision = idTipoRevision;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
