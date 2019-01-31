package com.elektra.typhoon.objetos.request;

import com.google.gson.annotations.SerializedName;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 28/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class SincronizacionPost {

    @SerializedName("r")
    private SincronizacionData sincronizacionData;

    public SincronizacionData getSincronizacionData() {
        return sincronizacionData;
    }

    public void setSincronizacionData(SincronizacionData sincronizacionData) {
        this.sincronizacionData = sincronizacionData;
    }
}
