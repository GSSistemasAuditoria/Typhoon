package com.elektra.typhoon.objetos.response;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

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
	private String idEvidencia;

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

    @SerializedName("ID_REGISTRO")
    @Expose
    private int idRegistro;

    @SerializedName("LATITUD")
    @Expose
    private double latitude;

    @SerializedName("LONGITUD")
    @Expose
    private double longitude;

    @SerializedName("Historial")
    @Expose
    private List<Historico> listHistorico;

    @SerializedName("EVIDENCIA_FCH_MOD")
    @Expose
    private String fechaMod;

    @SerializedName("LOCATION")
    @Expose
    private String location;

    @SerializedName("ID_ROL")
    @Expose
    private int idRol;

    @SerializedName("ID_USUARIO")
    @Expose
    private String idUsuario;

    private Bitmap smallBitmap;
    private Bitmap originalBitmap;
    private int idRevision;
    private int idChecklist;
    private int idRubro;
    private int idPregunta;
    //private int idRegistro;
    private String contenidoPreview;
    private int idBarco;
    private int agregadoCoordinador;
    private int agregadoLider;
    private int nuevo;

    public Evidencia(){

    }

    public Evidencia(Bitmap imageBitmap){
        this.smallBitmap = imageBitmap;
    }

    public Evidencia(Bitmap smallBitmap,Bitmap originalBitmap,String idEvidencia){
        this.smallBitmap = smallBitmap;
        this.originalBitmap = originalBitmap;
        this.idEvidencia = idEvidencia;
    }

    public String getIdEvidencia() {
        return idEvidencia;
    }

    public void setIdEvidencia(String idEvidencia) {
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

    public int getIdRevision() {
        return idRevision;
    }

    public void setIdRevision(int idRevision) {
        this.idRevision = idRevision;
    }

    public int getIdChecklist() {
        return idChecklist;
    }

    public void setIdChecklist(int idChecklist) {
        this.idChecklist = idChecklist;
    }

    public int getIdRubro() {
        return idRubro;
    }

    public void setIdRubro(int idRubro) {
        this.idRubro = idRubro;
    }

    public int getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(int idPregunta) {
        this.idPregunta = idPregunta;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getContenidoPreview() {
        return contenidoPreview;
    }

    public void setContenidoPreview(String contenidoPreview) {
        this.contenidoPreview = contenidoPreview;
    }

    public int getIdBarco() {
        return idBarco;
    }

    public void setIdBarco(int idBarco) {
        this.idBarco = idBarco;
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

    public List<Historico> getListHistorico() {
        return listHistorico;
    }

    public void setListHistorico(List<Historico> listHistorico) {
        this.listHistorico = listHistorico;
    }

    public int getAgregadoCoordinador() {
        return agregadoCoordinador;
    }

    public void setAgregadoCoordinador(int agregadoCoordinador) {
        this.agregadoCoordinador = agregadoCoordinador;
    }

    public int getNuevo() {
        return nuevo;
    }

    public void setNuevo(int nuevo) {
        this.nuevo = nuevo;
    }

    public String getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(String fechaMod) {
        this.fechaMod = fechaMod;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getAgregadoLider() {
        return agregadoLider;
    }

    public void setAgregadoLider(int agregadoLider) {
        this.agregadoLider = agregadoLider;
    }
}
