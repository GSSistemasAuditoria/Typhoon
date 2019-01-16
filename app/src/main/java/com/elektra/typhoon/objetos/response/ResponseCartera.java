package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 14/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class ResponseCartera {

    @SerializedName("GetCarteraRevisionesResult")
    @Expose
    private CarteraRevisiones carteraRevisiones;

    public CarteraRevisiones getCarteraRevisiones() {
        return carteraRevisiones;
    }

    public void setCarteraRevisiones(CarteraRevisiones carteraRevisiones) {
        this.carteraRevisiones = carteraRevisiones;
    }

    public class CarteraRevisiones{

        @SerializedName("Data")
        @Expose
        private List<FolioRevision> folioRevision;
        @SerializedName("Error")
        @Expose
        private String error;
        @SerializedName("Exito")
        @Expose
        private Boolean exito;
        @SerializedName("HttpStatusCode")
        @Expose
        private int httpStatusCode;

        public List<FolioRevision> getFolioRevision() {
            return folioRevision;
        }

        public void setFolioRevision(List<FolioRevision> folioRevision) {
            this.folioRevision = folioRevision;
        }

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
