package com.elektra.typhoon.objetos.request;

import com.elektra.typhoon.objetos.response.Evidencia;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 13/05/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class ValidaDatosRequest {

    @SerializedName("data")
    @Expose
    private DatosRequest datosRequest;

    public DatosRequest getDatosRequest() {
        return datosRequest;
    }

    public void setDatosRequest(DatosRequest datosRequest) {
        this.datosRequest = datosRequest;
    }
}
