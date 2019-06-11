package com.elektra.typhoon.objetos.request;

import com.elektra.typhoon.objetos.response.Anexo;
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

    @SerializedName("ES_MOVIL")
    @Expose
    private boolean esMovil;

    @SerializedName("REVISION_FCH_MOD")
    @Expose
    private String revisionFechaMod;

    @SerializedName("Checklists")
    @Expose
    private List<ChecklistData> listChecklist;

    @SerializedName("TraClRespuesta")
    @Expose
    private List<RespuestaData> listRespuestas;

    @SerializedName("TranSubAnexo")
    @Expose
    private List<SubAnexo> listSubAnexos;//*/

    //@SerializedName("ULTIMA_SYNC")
    //@Expose
    //private boolean ultimaSincronizacion;

    @SerializedName("ID_USUARIO_SESION")
    @Expose
    private String idUsuarioSesion;

    @SerializedName("SyncMode")
    @Expose
    private int syncMode;

    @SerializedName("Notifications")
    @Expose
    private NotificationSync notificationSync;

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

    public boolean isEsMovil() {
        return esMovil;
    }

    public void setEsMovil(boolean esMovil) {
        this.esMovil = esMovil;
    }

    public List<SubAnexo> getListSubAnexos() {
        return listSubAnexos;
    }

    public void setListSubAnexos(List<SubAnexo> listSubAnexos) {
        this.listSubAnexos = listSubAnexos;
    }//*/

    /*public boolean isUltimaSincronizacion() {
        return ultimaSincronizacion;
    }//*/

    /*public void setUltimaSincronizacion(boolean ultimaSincronizacion) {
        this.ultimaSincronizacion = ultimaSincronizacion;
    }//*/

    public String getIdUsuarioSesion() {
        return idUsuarioSesion;
    }

    public void setIdUsuarioSesion(String idUsuarioSesion) {
        this.idUsuarioSesion = idUsuarioSesion;
    }

    public int getSyncMode() {
        return syncMode;
    }

    public void setSyncMode(int syncMode) {
        this.syncMode = syncMode;
    }

    public NotificationSync getNotificationSync() {
        return notificationSync;
    }

    public void setNotificationSync(NotificationSync notificationSync) {
        this.notificationSync = notificationSync;
    }
}
