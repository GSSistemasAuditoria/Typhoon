package com.elektra.typhoon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.objetos.response.Checklist;

import java.util.ArrayList;
import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 16/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class ChecklistDBMethods {

    private Context context;

    public ChecklistDBMethods(Context context){
        this.context = context;
    }

    public static final String TP_TRAN_CHECKLIST = "TP_TRAN_CHECKLIST";

    public static String QUERY_CREATE_TABLE_TP_TRAN_CHECKLIST = "CREATE TABLE " + TP_TRAN_CHECKLIST + " (" +
            "ID_REVISION INTEGER, " +
            "ID_CHECKLIST INTEGER, " +
            "ID_BARCO INTEGER, " +
            "ESTATUS INTEGER, " +
	        "CALIFICACION INTEGER, " +
            "PRIMARY KEY (ID_REVISION,ID_CHECKLIST,ID_BARCO))";

    public void createChecklist(Checklist checklist){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_REVISION",checklist.getIdRevision());
        values.put("ID_CHECKLIST",checklist.getIdChecklist());
        values.put("ID_BARCO",checklist.getIdBarco());
        values.put("ESTATUS",checklist.getEstatus());
        values.put("CALIFICACION",checklist.getCalificacion());
        db.insertWithOnConflict(TP_TRAN_CHECKLIST, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<Checklist> readChecklists(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<Checklist> listChecklist = new ArrayList<>();
        String query = "SELECT ID_REVISION,ID_CHECKLIST,ID_BARCO,ESTATUS,CALIFICACION FROM " + TP_TRAN_CHECKLIST;
        if(condition != null){
            query = query + " " + condition;
        }
        Cursor cursor = db.rawQuery(query,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    Checklist checklist = new Checklist();
                    checklist.setIdRevision(cursor.getInt(0));
                    checklist.setIdChecklist(cursor.getInt(1));
                    checklist.setIdBarco(cursor.getInt(2));
                    checklist.setEstatus(cursor.getInt(3));
                    checklist.setCalificacion(cursor.getInt(4));
                    listChecklist.add(checklist);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listChecklist;
    }

    public Checklist readChecklist(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        Checklist checklist = null;
        String query = "SELECT ID_REVISION,ID_CHECKLIST,ID_BARCO,ESTATUS,CALIFICACION FROM " + TP_TRAN_CHECKLIST;
        if(condition != null){
            query = query + " " + condition;
        }
        Cursor cursor = db.rawQuery(query,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    checklist = new Checklist();
                    checklist.setIdRevision(cursor.getInt(0));
                    checklist.setIdChecklist(cursor.getInt(1));
                    checklist.setIdBarco(cursor.getInt(2));
                    checklist.setEstatus(cursor.getInt(3));
                    checklist.setCalificacion(cursor.getInt(4));
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return checklist;
    }

    public void updateChecklist(ContentValues values,String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.update(TP_TRAN_CHECKLIST,values,condition,args);
        db.close();
    }

    public void deleteChecklist(String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.delete(TP_TRAN_CHECKLIST, condition,args);
        db.close();
    }
}
