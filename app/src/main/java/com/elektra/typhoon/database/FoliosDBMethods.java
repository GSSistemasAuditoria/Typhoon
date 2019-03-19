package com.elektra.typhoon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.objetos.response.FolioRevision;

import java.util.ArrayList;
import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 16/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class FoliosDBMethods {

    private Context context;

    public FoliosDBMethods(Context context){
        this.context = context;
    }

    public static final String TP_TRAN_REVISION = "TP_TRAN_REVISION";

    public static String QUERY_CREATE_TABLE_TP_TRAN_REVISION = "CREATE TABLE " + TP_TRAN_REVISION + " (" +
            "ID_REVISION INTEGER PRIMARY KEY, " +
            "NOMBRE TEXT, " +
            "ID_TIPO_REVISION INTEGER, " +
            "ID_USUARIO TEXT, " +
            "FECHA_INICIO TEXT, " +
            "FECHA_FIN TEXT, " +
            "ESTATUS INTEGER)";

    public void createFolio(FolioRevision folioRevision){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_REVISION",folioRevision.getIdRevision());
        values.put("NOMBRE",folioRevision.getNombre());
        values.put("ID_TIPO_REVISION",folioRevision.getIdTipoRevision());
        values.put("ID_USUARIO",folioRevision.getIdUsuario());
        values.put("FECHA_INICIO",folioRevision.getFechaInicio());
        values.put("FECHA_FIN",folioRevision.getFechaFin());
        values.put("ESTATUS",folioRevision.getEstatus());
        db.insertWithOnConflict(TP_TRAN_REVISION, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<FolioRevision> readFolios(){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<FolioRevision> listFolios = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID_REVISION,NOMBRE,ID_TIPO_REVISION,ID_USUARIO,FECHA_INICIO,FECHA_FIN,ESTATUS FROM ").append(TP_TRAN_REVISION);
        Cursor cursor = db.rawQuery(query.toString(),null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    FolioRevision folioRevision = new FolioRevision();
                    folioRevision.setIdRevision(cursor.getInt(0));
                    folioRevision.setNombre(cursor.getString(1));
                    folioRevision.setIdTipoRevision(cursor.getInt(2));
                    folioRevision.setIdUsuario(cursor.getString(3));
                    folioRevision.setFechaInicio(cursor.getString(4));
                    folioRevision.setFechaFin(cursor.getString(5));
                    folioRevision.setEstatus(cursor.getInt(6));
                    listFolios.add(folioRevision);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listFolios;
    }

    public FolioRevision readFolio(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        FolioRevision folioRevision = null;
        /*StringBuilder query = new StringBuilder();
        query.append("SELECT ID_REVISION,NOMBRE,ID_TIPO_REVISION,ID_USUARIO,FECHA_INICIO,FECHA_FIN,ESTATUS FROM ").append(TP_TRAN_REVISION);
        if(condition != null){
            query.append(" ").append(condition);
        }
        Cursor cursor = db.rawQuery(query.toString(),args);//*/
        Cursor cursor = db.rawQuery(condition,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    folioRevision = new FolioRevision();
                    folioRevision.setIdRevision(cursor.getInt(0));
                    folioRevision.setNombre(cursor.getString(1));
                    folioRevision.setIdTipoRevision(cursor.getInt(2));
                    folioRevision.setIdUsuario(cursor.getString(3));
                    folioRevision.setFechaInicio(cursor.getString(4));
                    folioRevision.setFechaFin(cursor.getString(5));
                    folioRevision.setEstatus(cursor.getInt(6));
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return folioRevision;
    }

    public void updateFolio(ContentValues values,String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.update(TP_TRAN_REVISION,values,condition,args);
        db.close();
    }

    public void deleteFolio(String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.delete(TP_TRAN_REVISION, condition,args);
        db.close();
    }
}
