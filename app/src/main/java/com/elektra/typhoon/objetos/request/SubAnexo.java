package com.elektra.typhoon.objetos.request;

import com.elektra.typhoon.objetos.response.HistoricoAnexo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 22/03/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class SubAnexo {

    @SerializedName("ID_SUBANEXO")
    @Expose
    private int idSubAnexo;

    @SerializedName("ID_REVISION")
    @Expose
    private int idRevision;

    @SerializedName("FILE_NAME")
    @Expose
    private String fileName;

    @SerializedName("CONTENIDO")
    @Expose
    private String contenido;

    @SerializedName("ID_ETAPA")
    @Expose
    private int idEtapa;

    @SerializedName("HistorialSubAnexo")
    @Expose
    private List<HistoricoAnexo> listHistorico;

    @SerializedName("SUBANEXO_FCH_SINC")
    @Expose
    private String fechaSincronizacion;

    @SerializedName("SUBANEXO_FCH_MOD")
    @Expose
    private String fechaMod;

    @SerializedName("ID_ROL")
    @Expose
    private int idRol;

    @SerializedName("ID_USUARIO")
    @Expose
    private String idUsuario;

    public SubAnexo(int idSubAnexo, int idRevision, String fileName, String contenido, int idEtapa) {
        this.idSubAnexo = idSubAnexo;
        this.idRevision = idRevision;
        this.fileName = fileName;
        this.contenido = contenido;
        this.idEtapa = idEtapa;
    }

    public SubAnexo() {
    }

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public int getIdEtapa() {
        return idEtapa;
    }

    public void setIdEtapa(int idEtapa) {
        this.idEtapa = idEtapa;
    }

    public List<HistoricoAnexo> getListHistorico() {
        return listHistorico;
    }

    public void setListHistorico(List<HistoricoAnexo> listHistorico) {
        this.listHistorico = listHistorico;
    }

    public String getFechaSincronizacion() {
        return fechaSincronizacion;
    }

    public void setFechaSincronizacion(String fechaSincronizacion) {
        this.fechaSincronizacion = fechaSincronizacion;
    }

    public String getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(String fechaMod) {
        this.fechaMod = fechaMod;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
}
