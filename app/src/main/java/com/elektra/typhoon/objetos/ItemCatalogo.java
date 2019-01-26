package com.elektra.typhoon.objetos;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 25/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class ItemCatalogo {

    private int id;
    private String descripcion;

    public ItemCatalogo(){

    }

    public ItemCatalogo(int id,String descripcion){
        this.id = id;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
