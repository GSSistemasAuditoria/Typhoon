package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 04/03/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class ResponseDescargaPdf {

    @SerializedName("DownloadInformePreguntaResult")
    @Expose
    private DescargaPDF descargaPDF;

    public DescargaPDF getDescargaPDF() {
        return descargaPDF;
    }

    public void setDescargaPDF(DescargaPDF descargaPDF) {
        this.descargaPDF = descargaPDF;
    }

    public class DescargaPDF{

        @SerializedName("Data")
        @Expose
        private DocumentoPDF documentoPDF;

        @SerializedName("Error")
        @Expose
        private String error;

        @SerializedName("Exito")
        @Expose
        private Boolean exito;

        @SerializedName("HttpStatusCode")
        @Expose
        private int httpStatusCode;

        public DocumentoPDF getDocumentoPDF() {
            return documentoPDF;
        }

        public void setDocumentoPDF(DocumentoPDF documentoPDF) {
            this.documentoPDF = documentoPDF;
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

    public class DocumentoPDF{

        @SerializedName("Contenido")
        @Expose
        private String base64;

        @SerializedName("Nombre")
        @Expose
        private String nombre;

        public String getBase64() {
            return base64;
        }

        public void setBase64(String base64) {
            this.base64 = base64;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
    }
}
