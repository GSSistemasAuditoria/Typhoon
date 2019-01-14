package com.elektra.typhoon.objetos;

/**
 * Proyecto: TYPHOON
 * Autor: Francis Susana Carreto Espinoza
 * Fecha: 10/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */


public class Folio {
    private String folio;
    private String fecha;
    private String descripcion;

    public Folio(){}

    public Folio(String fecha) {
        this.fecha = fecha;
    }

    public Folio(String folio, String fecha, String descripcion) {
        this.folio = folio;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
