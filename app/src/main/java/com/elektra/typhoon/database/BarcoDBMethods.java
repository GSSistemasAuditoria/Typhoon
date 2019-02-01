package com.elektra.typhoon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.objetos.response.Barco;
import com.elektra.typhoon.objetos.response.CatalogoBarco;

import java.util.ArrayList;
import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 16/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class BarcoDBMethods {

    private Context context;

    public BarcoDBMethods(Context context){
        this.context = context;
    }

    public static final String TP_CAT_BARCO = "TP_CAT_BARCO";

    public static String QUERY_CREATE_TABLE_TP_CAT_BARCO = "CREATE TABLE " + TP_CAT_BARCO + " (" +
            "ID_BARCO INTEGER PRIMARY KEY, " +
            "NOMBRE TEXT)";

    public void createBarco(Barco barco){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_BARCO",barco.getIdBarco());
        values.put("NOMBRE",barco.getNombre());
        db.insertWithOnConflict(TP_CAT_BARCO, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<CatalogoBarco> readBarcos(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<CatalogoBarco> listBarco = new ArrayList<>();
        String query = "SELECT ID_BARCO,NOMBRE FROM " + TP_CAT_BARCO;
        if(condition != null){
            query = query + " " + condition;
        }
        Cursor cursor = db.rawQuery(query,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    CatalogoBarco barco = new CatalogoBarco();
                    barco.setIdBarco(cursor.getInt(0));
                    barco.setNombre(cursor.getString(1));
                    listBarco.add(barco);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listBarco;
    }

    /*public Barco readBarco(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        Barco barco = null;
        String query = "SELECT ID_BARCO,NOMBRE FROM " + TP_CAT_BARCO;
        if(condition != null){
            query = query + " " + condition;
        }
        Cursor cursor = db.rawQuery(query,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    barco = new Barco();
                    barco.setIdBarco(cursor.getInt(0));
                    barco.setNombre(cursor.getString(1));
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return barco;
    }//*/

    public void updateBarco(ContentValues values,String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.update(TP_CAT_BARCO,values,condition,args);
        db.close();
    }

    public void deleteBarco(String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.delete(TP_CAT_BARCO, condition,args);
        db.close();
    }

}
