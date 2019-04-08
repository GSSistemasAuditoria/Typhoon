package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 22/03/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class HistoricoAnexo {

    @SerializedName("ID_SUBANEXO")
    @Expose
    private int idSubAnexo;

    @SerializedName("ID_REVISION")
    @Expose
    private int idRevision;

    @SerializedName("ID_ETAPA")
    @Expose
    private int idEtapa;

    @SerializedName("ID_USUARIO")
    @Expose
    private String idUsuario;

    @SerializedName("NOMBRE")
    @Expose
    private String nombre;

    @SerializedName("MOTIVO")
    @Expose
    private String motivoRechazo;

    @SerializedName("SUBANEXO_FCH_MOD")
    @Expose
    private String fechaMod;

    @SerializedName("GUID")
    @Expose
    private String guid;

    @SerializedName("SUBANEXO_FCH_SINC")
    @Expose
    private String fechaSincronizacion;

    @SerializedName("ID_ROL")
    @Expose
    private int idRol;

    public int getIdSubAnexo() {
        return idSubAnexo;
    }

    public void setIdSubAnexo(int idSubAnexo) {
        this.idSubAnexo = idSubAnexo;
    }

    public int getIdRevision() {
        return idRevision;
    }

    public void setIdRevision(int idRevision) {
        this.idRevision = idRevision;
    }

    public int getIdEtapa() {
        return idEtapa;
    }

    public void setIdEtapa(int idEtapa) {
        this.idEtapa = idEtapa;
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

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public String getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(String fechaMod) {
        this.fechaMod = fechaMod;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getFechaSincronizacion() {
        return fechaSincronizacion;
    }

    public void setFechaSincronizacion(String fechaSincronizacion) {
        this.fechaSincronizacion = fechaSincronizacion;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }
}
