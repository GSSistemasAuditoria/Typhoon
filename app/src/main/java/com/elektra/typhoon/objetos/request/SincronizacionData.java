package com.elektra.typhoon.objetos.request;

import com.elektra.typhoon.objetos.response.ChecklistData;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 28/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class SincronizacionData {

    @SerializedName("ID_REVISION")
    @Expose
    private int idRevision;

    @SerializedName("ESTATUS")
    @Expose
    private int estatus;//*/

    @SerializedName("REVISION_FCH_MOD")
    @Expose
    private String revisionFechaMod;

    @SerializedName("Checklists")
    @Expose
    private List<ChecklistData> listChecklist;

    @SerializedName("TraClRespuesta")
    @Expose
    private List<RespuestaData> listRespuestas;

    public int getIdRevision() {
        return idRevision;
    }

    public void setIdRevision(int idRevision) {
        this.idRevision = idRevision;
    }

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }//*/

    public List<ChecklistData> getListChecklist() {
        return listChecklist;
    }

    public void setListChecklist(List<ChecklistData> listChecklist) {
        this.listChecklist = listChecklist;
    }

    public List<RespuestaData> getListRespuestas() {
        return listRespuestas;
    }

    public void setListRespuestas(List<RespuestaData> listRespuestas) {
        this.listRespuestas = listRespuestas;
    }

    public String getRevisionFechaMod() {
        return revisionFechaMod;
    }

    public void setRevisionFechaMod(String revisionFechaMod) {
        this.revisionFechaMod = revisionFechaMod;
    }
}
