package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 19/03/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class Anexo {

    @SerializedName("DESCRIPCION")
    @Expose
    private String descripcion;

    @SerializedName("ID_ANEXO")
    @Expose
    private int idAnexo;

    @SerializedName("ID_SUBANEXO")
    @Expose
    private int idSubAnexo;

    @SerializedName("SubAnexos")
    @Expose
    private List<Anexo> listSubAnexos;

    private String fechaSinc;
    private String fechaMod;

    private String titulo;
    private List<Anexo> listAnexos;
    private String base64;
    private String nombreArchivo;
    private int idEtapa;
    private String idDocumento;
    private int idRevision;
    private boolean seleccionado;

    public Anexo(){

    }

    public Anexo(String titulo){
        this.titulo = titulo;
    }

    public Anexo(String titulo,List<Anexo> listAnexos){
        this.titulo = titulo;
        this.listAnexos = listAnexos;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Anexo> getListAnexos() {
        return listAnexos;
    }

    public void setListAnexos(List<Anexo> listAnexos) {
        this.listAnexos = listAnexos;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdAnexo() {
        return idAnexo;
    }

    public void setIdAnexo(int idAnexo) {
        this.idAnexo = idAnexo;
    }

    public int getIdSubAnexo() {
        return idSubAnexo;
    }

    public void setIdSubAnexo(int idSubAnexo) {
        this.idSubAnexo = idSubAnexo;
    }

    public int getIdEtapa() {
        return idEtapa;
    }

    public void setIdEtapa(int idEtapa) {
        this.idEtapa = idEtapa;
    }

    public String getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(String idDocumento) {
        this.idDocumento = idDocumento;
    }

    public int getIdRevision() {
        return idRevision;
    }

    public void setIdRevision(int idRevision) {
        this.idRevision = idRevision;
    }

    public List<Anexo> getListSubAnexos() {
        return listSubAnexos;
    }

    public void setListSubAnexos(List<Anexo> listSubAnexos) {
        this.listSubAnexos = listSubAnexos;
    }

    public String getFechaSinc() {
        return fechaSinc;
    }

    public void setFechaSinc(String fechaSinc) {
        this.fechaSinc = fechaSinc;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public String getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(String fechaMod) {
        this.fechaMod = fechaMod;
    }
}
