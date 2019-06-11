package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 06/06/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class CerrarSesionResponse {

    @SerializedName("CerrarSesionResult")
    @Expose
    private CerrarSesion cerrarSesion;

    public CerrarSesion getCerrarSesion() {
        return cerrarSesion;
    }

    public void setCerrarSesion(CerrarSesion cerrarSesion) {
        this.cerrarSesion = cerrarSesion;
    }

    public class CerrarSesion{

        @SerializedName("Error")
        @Expose
        private String error;

        @SerializedName("Exito")
        @Expose
        private Boolean exito;

        @SerializedName("HttpStatusCode")
        @Expose
        private int httpStatusCode;

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
    }
}
