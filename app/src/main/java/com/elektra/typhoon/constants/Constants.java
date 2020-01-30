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

    //108
    //public static String URL_PUBLIC = "PvNI3N9gGpvQqKsqGRbt8Nhx+46FExYXFBcSyaRedhgB7ahOqJicHA1dLGudNZ81CIoMlPNYfBZea8rM5p41NQ==";
    //CARLOS
    //public static String URL_PUBLIC = "ofjcUcZ+Cnrw2m6EQ/yxhnkcZ6r6+elNXbDnDzaLtZwVFNVvn8+DbM0Sb+tRqDCWEqfizoSFlBQXRzZLRU1trA==";
    //ARTUR
    //public static String URL_PUBLIC = "LsiGo/AXyECN6RUcXJZ1mqc/Bu+u6ZyjRNu5Y6dMDMD9UNX4K2O+W3fRR98yWkmUeZZe609LuB7ut16ANgNZHQ==";
    //SP1
    //public static String URL_PUBLIC = "4KyixEVLzWvCxpff3GzyEtrWvoIZqTWAk+RdY/8owt3bY77EKveLaKzihai9kyUfJ8DIauaZdXYF+Dkiu3GjQg==";
    //SP2
    public static String URL_PUBLIC = "4KyixEVLzWvCxpff3GzyEtrWvoIZqTWAk+RdY/8owt2hhfZzM4s0hMvTD+1UXMSXPdrmQ+QpKkpuyb7Z/+vMk6b7z4Fe9+9GYKdV6tL+VS0=";


    //DSI
    public static final String URL_DSI = "https://authns.desadsi.gs/nidp/";

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
