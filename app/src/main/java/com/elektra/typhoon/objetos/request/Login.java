package com.elektra.typhoon.objetos.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 29/04/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class Login {

    @SerializedName("USER_NAME")
    @Expose
    private String userName;

    @SerializedName("LLAVE_MAESTRA")
    @Expose
    private String password;

    @SerializedName("FB_TOKEN")
    @Expose
    private String fbToken;

    @SerializedName("ANDROID_ID")
    @Expose
    private String androidID;//*/

    @SerializedName("IS_LOGOUT")
    @Expose
    private int isLogout;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFbToken() {
        return fbToken;
    }

    public void setFbToken(String fbToken) {
        this.fbToken = fbToken;
    }

    public String getAndroidID() {
        return androidID;
    }

    public void setAndroidID(String androidID) {
        this.androidID = androidID;
    }//*/

    public int getIsLogout() {
        return isLogout;
    }

    public void setIsLogout(int isLogout) {
        this.isLogout = isLogout;
    }
}
