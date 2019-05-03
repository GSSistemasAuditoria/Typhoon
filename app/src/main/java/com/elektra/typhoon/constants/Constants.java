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
    //public static String URL_PUBLIC = "http://10.89.69.112/TyphoonService/TyphoonService.svc/";
    public static String URL_PUBLIC = "http://www.auditoriags.com/TyphoonService/TyphoonService.svc/";
    //public static String URL_PUBLIC = "http://www.auditoriags.com/TyphoonService_Test/TyphoonService.svc/";

    //**********************************************************************************************
    public static String DB_NAME = "TyphoonDB";
    public static String SP_NAME = "TyphoonSP";

    //**********************************************************************************************
    public static String MSG_ERR_CONN = "No se pudo conectar al servidor";

    //**********************************************************************************************
    public static String EMAIL_REGEX = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$";
    public static String DATE_FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.SSS";

    //**********************************************************************************************
    public final static String PATH = Environment.getExternalStorageDirectory().getPath() + "/Typhoon/tempPhotos/evidencia.jpg";

    //**********************************************************************************************
    public final static String INTENT_FOLIO_TAG = "folio";
    public final static String INTENT_FECHA_INICIO_TAG = "fechaInicio";
    public final static String INTENT_FECHA_FIN_TAG = "fechaFin";
    public final static String INTENT_ESTATUS_TAG = "estatus";

    public final static String SP_LOGIN_TAG = "login";
    public final static String SP_JWT_TAG = "jwt";
    public final static String SP_LIMITE_EVIDENCIAS = "limiteEvidencias";
    public final static String SP_GPS_FLAG = "gpsFlag";
    public final static String SP_GPS_GEOCERCA = "gpsGeocerca";
    public final static String SP_VALIDA_FECHA = "validaFecha";
    public final static String SP_FIREBASE_TOKEN = "firebaseToken";
}
