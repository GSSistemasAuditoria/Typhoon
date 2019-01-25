package com.elektra.typhoon.constants;

import android.os.Environment;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 04/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class Constants {

    //url del servicio
    public static String URL_PUBLIC = "http://10.89.69.112/TyphoonService/TyphoonService.svc/";

    //**********************************************************************************************
    public static String DB_NAME = "TyphoonDB";

    //**********************************************************************************************
    public static String MSG_ERR_CONN = "No se pudo conectar al servidor";

    //**********************************************************************************************
    public static String EMAIL_REGEX = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$";

    //**********************************************************************************************
    public final static String PATH = Environment.getExternalStorageDirectory().getPath() + "/Typhoon/";
}
