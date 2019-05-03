package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 30/04/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class Notificacion {

    @SerializedName("ID_NOTIFICACION")
    @Expose
    private int idNotificacion;

    @SerializedName("ID_ROL")
    @Expose
    private int idRol;

    @SerializedName("TITLE")
    @Expose
    private String title;

    @SerializedName("BODY")
    @Expose
    private String body;

    @SerializedName("FCH_MOD")
    @Expose
    private String fchMod;

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFchMod() {
        return fchMod;
    }

    public void setFchMod(String fchMod) {
        this.fchMod = fchMod;
    }

    public int getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }
}
