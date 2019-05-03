package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 30/04/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class ResponseNotificaciones {

    @SerializedName("GetTranNotificacionesResult")
    @Expose
    private Notificaciones notificaciones;

    public Notificaciones getNotificaciones() {
        return notificaciones;
    }

    public void setNotificaciones(Notificaciones notificaciones) {
        this.notificaciones = notificaciones;
    }

    public class Notificaciones{

        @SerializedName("Error")
        @Expose
        private String error;

        @SerializedName("Exito")
        @Expose
        private Boolean exito;

        @SerializedName("HttpStatusCode")
        @Expose
        private int httpStatusCode;

        @SerializedName("Data")
        @Expose
        private List<Notificacion> notificaciones;

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public Boolean getExito() {
            return exito;
        }

        public void setExito(Boolean exito) {
            this.exito = exito;
        }

        public int getHttpStatusCode() {
            return httpStatusCode;
        }

        public void setHttpStatusCode(int httpStatusCode) {
            this.httpStatusCode = httpStatusCode;
        }

        public List<Notificacion> getNotificaciones() {
            return notificaciones;
        }

        public void setNotificaciones(List<Notificacion> notificaciones) {
            this.notificaciones = notificaciones;
        }
    }
}
