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
public class SincronizacionResponseData {

    @SerializedName("Checklists")
    @Expose
    private List<ChecklistData> listChecklist;//*/

    @SerializedName("ESTATUS")
    @Expose
    private int estatus;

    @SerializedName("FECHA_INICIO")
    @Expose
    private String fechaInicio;

    @SerializedName("FECHA_FIN")
    @Expose
    private String fechaFin;

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

    @SerializedName("TraClRespuesta")
    @Expose
    private List<RespuestaData> listRespuestas;

    /*@SerializedName("Anexos")
    @Expose
    private List<Anexo> listAnexos;//*/

    public List<ChecklistData> getListChecklist() {
        return listChecklist;
    }

    public void setListChecklist(List<ChecklistData> listChecklist) {
        this.listChecklist = listChecklist;
    }//*/

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
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

    public List<RespuestaData> getListRespuestas() {
        return listRespuestas;
    }

    public void setListRespuestas(List<RespuestaData> listRespuestas) {
        this.listRespuestas = listRespuestas;
    }

    /*public List<Anexo> getListAnexos() {
        return listAnexos;
    }

    public void setListAnexos(List<Anexo> listAnexos) {
        this.listAnexos = listAnexos;
    }//*/
}
