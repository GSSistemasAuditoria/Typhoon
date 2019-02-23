package com.elektra.typhoon.database;

import android.content.Context;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 22/02/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class HistoricoDBMethods {

    private Context context;

    public HistoricoDBMethods(Context context){
        this.context = context;
    }

    public static final String TP_TRAN_HISTORIAL_EVIDENCIA = "TP_CAT_BARCO";

    public static String QUERY_CREATE_TABLE_TP_TRAN_HISTORIAL_EVIDENCIA = "CREATE TABLE " + TP_TRAN_HISTORIAL_EVIDENCIA + " (" +
            "ID_EVIDENCIA INTEGER, " +
            "ID_ETAPA INTEGER, " +
            "ID_USUARIO INTEGER, " +
            "MOTIVO TEXT, " +
            "CONSEC INTEGER)";
}
