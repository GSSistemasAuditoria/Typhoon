package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 15/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class ResponseNuevoUsuario {

    @SerializedName("InsertaNuevoUsuarioResult")
    @Expose
    private NuevoUsuario nuevoUsuario;

    public NuevoUsuario getNuevoUsuario() {
        return nuevoUsuario;
    }

    public void setNuevoUsuario(NuevoUsuario nuevoUsuario) {
        this.nuevoUsuario = nuevoUsuario;
    }

    public class NuevoUsuario{

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
