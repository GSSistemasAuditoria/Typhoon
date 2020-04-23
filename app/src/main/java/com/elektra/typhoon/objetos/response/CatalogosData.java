package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
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

    @SerializedName("EstatusRevision")
    @Expose
    private List<EstatusRevision> listEstatusRevision;

    @SerializedName("CatalogoRolesUsuario")
    @Expose
    private List<RolUsuario> listRolesUsuario;

    @SerializedName("CatalogoAppSettings")
    @Expose
    private List<Configuracion> listConfiguracion;

    @SerializedName("CatalogoEtapaSubAnexo")
    @Expose
    private List<EtapaSubAnexo> listEtapasSubAnexo;

    @SerializedName("CatalogoAniosRevision")
    @Expose
    private CatalogoAnios catalogoAnios;

    private List<HashMap<String, String>> CatalogoSettings;

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

    public List<EstatusRevision> getListEstatusRevision() {
        return listEstatusRevision;
    }

    public void setListEstatusRevision(List<EstatusRevision> listEstatusRevision) {
        this.listEstatusRevision = listEstatusRevision;
    }

    public List<RolUsuario> getListRolesUsuario() {
        return listRolesUsuario;
    }

    public void setListRolesUsuario(List<RolUsuario> listRolesUsuario) {
        this.listRolesUsuario = listRolesUsuario;
    }

    public List<Configuracion> getListConfiguracion() {
        return listConfiguracion;
    }

    public void setListConfiguracion(List<Configuracion> listConfiguracion) {
        this.listConfiguracion = listConfiguracion;
    }

    public List<EtapaSubAnexo> getListEtapasSubAnexo() {
        return listEtapasSubAnexo;
    }

    public void setListEtapasSubAnexo(List<EtapaSubAnexo> listEtapasSubAnexo) {
        this.listEtapasSubAnexo = listEtapasSubAnexo;
    }

    public CatalogoAnios getCatalogoAnios() {
        return catalogoAnios;
    }

    public void setCatalogoAnios(CatalogoAnios catalogoAnios) {
        this.catalogoAnios = catalogoAnios;
    }

    public List<HashMap<String, String>> getCatalogoSettings() {
        return CatalogoSettings;
    }

    public void setCatalogoSettings(List<HashMap<String, String>> catalogoSettings) {
        CatalogoSettings = catalogoSettings;
    }
}
