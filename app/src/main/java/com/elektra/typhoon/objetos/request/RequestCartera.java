package com.elektra.typhoon.objetos.request;

import com.google.gson.annotations.SerializedName;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 14/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class RequestCartera {

    @SerializedName("f")
    private CarteraData carteraData;

    public CarteraData getCarteraData() {
        return carteraData;
    }

    public void setCarteraData(CarteraData carteraData) {
        this.carteraData = carteraData;
    }
}
