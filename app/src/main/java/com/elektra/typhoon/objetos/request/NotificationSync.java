package com.elektra.typhoon.objetos.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 03/06/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class NotificationSync {

    @SerializedName("IsNotification")
    @Expose
    private boolean isNotification;

    @SerializedName("RolesNotificationValidadas")
    @Expose
    private List<Integer> rolesNotificacionValidadas;

    @SerializedName("RolesNotificationRechazadas")
    @Expose
    private List<Integer> rolesNotificacionRechazadas;

    public boolean isNotification() {
        return isNotification;
    }

    public void setNotification(boolean notification) {
        isNotification = notification;
    }

    public List<Integer> getRolesNotificacionValidadas() {
        return rolesNotificacionValidadas;
    }

    public void setRolesNotificacionValidadas(List<Integer> rolesNotificacionValidadas) {
        this.rolesNotificacionValidadas = rolesNotificacionValidadas;
    }

    public List<Integer> getRolesNotificacionRechazadas() {
        return rolesNotificacionRechazadas;
    }

    public void setRolesNotificacionRechazadas(List<Integer> rolesNotificacionRechazadas) {
        this.rolesNotificacionRechazadas = rolesNotificacionRechazadas;
    }
}
