package com.elektra.typhoon.objetos.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 12/06/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class RequestCatalogos {

    @SerializedName("loginModel")
    @Expose
    private CatalogoPost catalogoPost;

    public CatalogoPost getCatalogoPost() {
        return catalogoPost;
    }

    public void setCatalogoPost(CatalogoPost catalogoPost) {
        this.catalogoPost = catalogoPost;
    }

    public class CatalogoPost{

        @SerializedName("USER_NAME")
        @Expose
        private String userName;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
