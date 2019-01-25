package com.elektra.typhoon.objetos.response;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 16/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class Evidencia {

    @SerializedName("ID_EVIDENCIA")
    @Expose
	private int idEvidencia;

    @SerializedName("NOMBRE")
    @Expose
	private String nombre;

    @SerializedName("CONTENIDO")
    @Expose
	private String contenido;

    @SerializedName("ID_ESTATUS")
    @Expose
	private int idEstatus;

    @SerializedName("ID_ETAPA")
    @Expose
	private int idEtapa;

    private Bitmap smallBitmap;

    private Bitmap originalBitmap;

    public Evidencia(){

    }

    public Evidencia(Bitmap imageBitmap){
        this.smallBitmap = imageBitmap;
    }

    public Evidencia(Bitmap smallBitmap,Bitmap originalBitmap,int idEvidencia){
        this.smallBitmap = smallBitmap;
        this.originalBitmap = originalBitmap;
        this.idEvidencia = idEvidencia;
    }

    public int getIdEvidencia() {
        return idEvidencia;
    }

    public void setIdEvidencia(int idEvidencia) {
        this.idEvidencia = idEvidencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public int getIdEstatus() {
        return idEstatus;
    }

    public void setIdEstatus(int idEstatus) {
        this.idEstatus = idEstatus;
    }

    public int getIdEtapa() {
        return idEtapa;
    }

    public void setIdEtapa(int idEtapa) {
        this.idEtapa = idEtapa;
    }

    public Bitmap getSmallBitmap() {
        return smallBitmap;
    }

    public void setSmallBitmap(Bitmap smallBitmap) {
        this.smallBitmap = smallBitmap;
    }

    public Bitmap getOriginalBitmap() {
        return originalBitmap;
    }

    public void setOriginalBitmap(Bitmap originalBitmap) {
        this.originalBitmap = originalBitmap;
    }
}
