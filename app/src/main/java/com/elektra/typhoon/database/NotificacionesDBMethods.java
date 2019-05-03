package com.elektra.typhoon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.objetos.response.Notificacion;

import java.util.ArrayList;
import java.util.List;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 30/04/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class NotificacionesDBMethods {

    private Context context;

    public NotificacionesDBMethods(Context context){
        this.context = context;
    }

    public static final String TP_TRAN_NOTIFICACIONES = "TP_TRAN_NOTIFICACIONES";

    public static String QUERY_CREATE_TABLE_TP_TRAN_NOTIFICACIONES = "CREATE TABLE " + TP_TRAN_NOTIFICACIONES + " (" +
            "ID_NOTIFICACION INTEGER PRIMARY KEY, " +
            "ID_ROL INTEGER, " +
            "TITLE TEXT, " +
            "BODY TEXT, " +
            "FECHA_MOD TEXT)";

    public void createNotificacion(Notificacion notificacion){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_NOTIFICACION",notificacion.getIdNotificacion());
        values.put("ID_ROL",notificacion.getIdRol());
        values.put("TITLE",notificacion.getTitle());
        values.put("BODY",notificacion.getBody());
        values.put("FECHA_MOD",notificacion.getFchMod());
        db.insertWithOnConflict(TP_TRAN_NOTIFICACIONES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<Notificacion> readNotificaciones(int idRol){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<Notificacion> listNotificaciones = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID_NOTIFICACION,ID_ROL,TITLE,BODY,FECHA_MOD FROM ").append(TP_TRAN_NOTIFICACIONES).append(" WHERE ID_ROL = ?");
        Cursor cursor = db.rawQuery(query.toString(),new String[]{String.valueOf(idRol)});
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    Notificacion notificacion = new Notificacion();
                    notificacion.setIdNotificacion(cursor.getInt(0));
                    notificacion.setIdRol(cursor.getInt(1));
                    notificacion.setTitle(cursor.getString(2));
                    notificacion.setBody(cursor.getString(3));
                    notificacion.setFchMod(cursor.getString(4));
                    listNotificaciones.add(notificacion);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listNotificaciones;
    }

    public void deleteNotificacion(int idRol){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.delete(TP_TRAN_NOTIFICACIONES, "ID_ROL = ?",new String[]{String.valueOf(idRol)});
        db.close();
    }
}
