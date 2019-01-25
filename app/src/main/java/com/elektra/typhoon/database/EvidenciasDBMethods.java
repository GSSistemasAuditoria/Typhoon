package com.elektra.typhoon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.objetos.response.Evidencia;

import java.util.ArrayList;
import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 16/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class EvidenciasDBMethods {

    private Context context;

    public EvidenciasDBMethods(Context context){
        this.context = context;
    }

    public static final String TP_TRAN_CL_EVIDENCIA = "TP_TRAN_CL_EVIDENCIA";

    public static String QUERY_CREATE_TABLE_TP_TRAN_CL_EVIDENCIA = "CREATE TABLE " + TP_TRAN_CL_EVIDENCIA + " (" +
        "ID_EVIDENCIA INTEGER PRIMARY KEY, " +
	    "NOMBRE TEXT, " +
	    "CONTENIDO TEXT, " +
	    "ID_ESTATUS INTEGER, " +
	    "ID_ETAPA INTEGER)";

    public void createEvidencia(Evidencia evidencia){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_EVIDENCIA",evidencia.getIdEvidencia());
        values.put("NOMBRE",evidencia.getNombre());
        values.put("CONTENIDO",evidencia.getContenido());
        values.put("ID_ESTATUS",evidencia.getIdEstatus());
        values.put("ID_ETAPA",evidencia.getIdEtapa());
        db.insertWithOnConflict(TP_TRAN_CL_EVIDENCIA, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<Evidencia> readEvidencias(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<Evidencia> listEvidencia = new ArrayList<>();
        String query = "SELECT ID_EVIDENCIA,NOMBRE,CONTENIDO,ID_ESTATUS,ID_ETAPA FROM " + TP_TRAN_CL_EVIDENCIA;
        if(condition != null){
            query = query + " " + condition;
        }
        Cursor cursor = db.rawQuery(query,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    Evidencia evidencia = new Evidencia();
                    evidencia.setIdEvidencia(cursor.getInt(0));
                    evidencia.setNombre(cursor.getString(1));
                    evidencia.setContenido(cursor.getString(2));
                    evidencia.setIdEstatus(cursor.getInt(3));
                    evidencia.setIdEtapa(cursor.getInt(4));
                    listEvidencia.add(evidencia);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listEvidencia;
    }

    public Evidencia readEvidencia(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        Evidencia evidencia = null;
        String query = "SELECT ID_EVIDENCIA,NOMBRE,CONTENIDO,ID_ESTATUS,ID_ETAPA FROM " + TP_TRAN_CL_EVIDENCIA;
        if(condition != null){
            query = query + " " + condition;
        }
        Cursor cursor = db.rawQuery(query,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    evidencia = new Evidencia();
                    evidencia.setIdEvidencia(cursor.getInt(0));
                    evidencia.setNombre(cursor.getString(1));
                    evidencia.setContenido(cursor.getString(2));
                    evidencia.setIdEstatus(cursor.getInt(3));
                    evidencia.setIdEtapa(cursor.getInt(4));
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return evidencia;
    }

    public void updateEvidencia(ContentValues values,String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.update(TP_TRAN_CL_EVIDENCIA,values,condition,args);
        db.close();
    }

    public void deleteEvidencia(String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.delete(TP_TRAN_CL_EVIDENCIA, condition,args);
        db.close();
    }

}
