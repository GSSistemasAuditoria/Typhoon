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

    @SerializedName("CatalogoEstatusEvidencia")
    @Expose
    private List<EstatusEvidencia> listEstatusEvidencia;

    @SerializedName("CatalogoEtapasEvidencia")
    @Expose
    private List<EtapaEvidencia> listEtapasEvidencia;

    @SerializedName("CatalogoRespuestasCl")
    @Expose
    private List<TipoRespuesta> listTiposRespuesta;//*/

    public List<Barco> getListBarcos() {
        return listBarcos;
    }

    public void setListBarcos(List<Barco> listBarcos) {
        this.listBarcos = listBarcos;
    }

    public List<EstatusEvidencia> getListEstatusEvidencia() {
        return listEstatusEvidencia;
    }

    public void setListEstatusEvidencia(List<EstatusEvidencia> listEstatusEvidencia) {
        this.listEstatusEvidencia = listEstatusEvidencia;
    }

    public List<EtapaEvidencia> getListEtapasEvidencia() {
        return listEtapasEvidencia;
    }

    public void setListEtapasEvidencia(List<EtapaEvidencia> listEtapasEvidencia) {
        this.listEtapasEvidencia = listEtapasEvidencia;
    }

    public List<TipoRespuesta> getListTiposRespuesta() {
        return listTiposRespuesta;
    }

    public void setListTiposRespuesta(List<TipoRespuesta> listTiposRespuesta) {
        this.listTiposRespuesta = listTiposRespuesta;
    }//*/
}
