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
public class DatosRequest {

    @SerializedName("ID_REVISION")
    @Expose
    private int idRevision;

    @SerializedName("LocalEvidencias")
    @Expose
    private List<Evidencia> localEvidencias;

    @SerializedName("LocalSubanexos")
    @Expose
    private List<SubAnexo> localSubanexo;

    public int getIdRevision() {
        return idRevision;
    }

    public void setIdRevision(int idRevision) {
        this.idRevision = idRevision;
    }

    public List<Evidencia> getLocalEvidencias() {
        return localEvidencias;
    }

    public void setLocalEvidencias(List<Evidencia> localEvidencias) {
        this.localEvidencias = localEvidencias;
    }

    public List<SubAnexo> getLocalSubanexo() {
        return localSubanexo;
    }

    public void setLocalSubanexo(List<SubAnexo> localSubanexo) {
        this.localSubanexo = localSubanexo;
    }
}
