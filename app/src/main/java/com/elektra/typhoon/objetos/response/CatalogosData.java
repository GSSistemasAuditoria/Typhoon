package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 30/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class CatalogosData {

    @SerializedName("CatalogoBarcos")
    @Expose
    private List<Barco> listBarcos;

    public List<Barco> getListBarcos() {
        return listBarcos;
    }

    public void setListBarcos(List<Barco> listBarcos) {
        this.listBarcos = listBarcos;
    }
}
