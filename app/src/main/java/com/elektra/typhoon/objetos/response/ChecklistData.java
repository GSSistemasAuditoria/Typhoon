package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 29/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class ChecklistData {

    @SerializedName("ID_CHECKLIST")
    @Expose
    private int idChecklist;

    @SerializedName("ID_ESTATUS")
    @Expose
    private int idEstatus;

    @SerializedName("ID_LOGO")
    @Expose
    private int idLogo;

    @SerializedName("ID_TIPO_REVISION")
    @Expose
    private int idTipoRevision;

    @SerializedName("NOMBRE")
    @Expose
    private String nombre;

    @SerializedName("PONDERACION")
    @Expose
    private int ponderacion;

    @SerializedName("Rubros")
    @Expose
    private List<Rubro> listRubros;//*/

    private int idRevision;

    public int getIdChecklist() {
        return idChecklist;
    }

    public void setIdChecklist(int idChecklist) {
        this.idChecklist = idChecklist;
    }

    public int getIdEstatus() {
        return idEstatus;
    }

    public void setIdEstatus(int idEstatus) {
        this.idEstatus = idEstatus;
    }

    public int getIdLogo() {
        return idLogo;
    }

    public void setIdLogo(int idLogo) {
        this.idLogo = idLogo;
    }

    public int getIdTipoRevision() {
        return idTipoRevision;
    }

    public void setIdTipoRevision(int idTipoRevision) {
        this.idTipoRevision = idTipoRevision;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPonderacion() {
        return ponderacion;
    }

    public void setPonderacion(int ponderacion) {
        this.ponderacion = ponderacion;
    }

    public List<Rubro> getListRubros() {
        return listRubros;
    }

    public void setListRubros(List<Rubro> listRubros) {
        this.listRubros = listRubros;
    }//*/

    public int getIdRevision() {
        return idRevision;
    }

    public void setIdRevision(int idRevision) {
        this.idRevision = idRevision;
    }
}
