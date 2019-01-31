package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 28/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class SincronizacionResponse {

    @SerializedName("SincronizarResult")
    @Expose
    private Sincronizacion sincronizacion;

    public Sincronizacion getSincronizacion() {
        return sincronizacion;
    }

    public void setSincronizacion(Sincronizacion sincronizacion) {
        this.sincronizacion = sincronizacion;
    }

    public class Sincronizacion{

        @SerializedName("Data")
        @Expose
        private SincronizacionResponseData sincronizacionResponseData;//*/

        @SerializedName("Error")
        @Expose
        private String error;

        @SerializedName("Exito")
        @Expose
        private Boolean exito;

        @SerializedName("HttpStatusCode")
        @Expose
        private int httpStatusCode;

        public SincronizacionResponseData getSincronizacionResponseData() {
            return sincronizacionResponseData;
        }

        public void setSincronizacionResponseData(SincronizacionResponseData sincronizacionResponseData) {
            this.sincronizacionResponseData = sincronizacionResponseData;
        }//*/

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
