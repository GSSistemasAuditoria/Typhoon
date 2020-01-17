package com.elektra.typhoon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.objetos.response.Barco;
import com.elektra.typhoon.objetos.response.CatalogoBarco;
import com.elektra.typhoon.utils.Utils;

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
            "LATITUDE REAL, " +
            "LONGITUDE REAL, " +
            "RADIO REAL, " +
            "NOMBRE TEXT)";

    public void createBarco(Barco barco){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_BARCO",barco.getIdBarco());
        values.put("NOMBRE", Utils.removeSpecialCharacters(barco.getNombre()));
        values.put("LATITUDE",barco.getLatitud());
        values.put("LONGITUDE",barco.getLongitud());
        values.put("RADIO",barco.getRadio());
        db.insertWithOnConflict(TP_CAT_BARCO, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<CatalogoBarco> readBarcos(){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<CatalogoBarco> listBarco = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID_BARCO,NOMBRE,LATITUDE,LONGITUDE,RADIO FROM ").append(TP_CAT_BARCO);
        Cursor cursor = db.rawQuery(query.toString(),null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    CatalogoBarco barco = new CatalogoBarco();
                    barco.setIdBarco(cursor.getInt(0));
                    barco.setNombre(cursor.getString(1));
                    barco.setLatitud(cursor.getDouble(2));
                    barco.setLongitud(cursor.getDouble(3));
                    barco.setRadio(cursor.getFloat(4));
                    listBarco.add(barco);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listBarco;
    }

    public void deleteBarco(){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.execSQL("delete from " + TP_CAT_BARCO);
        db.close();
    }

}
