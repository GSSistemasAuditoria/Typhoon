package com.elektra.typhoon.objetos.response;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 07/03/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class LatLng {

    private double latitude;
    private double longitude;

    public LatLng(double latitude,double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
