package com.elektra.typhoon.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.elektra.typhoon.constants.Constants;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 04/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */

public class TyphoonDataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = Constants.DB_NAME;
    private static final int DATABASE_VERSION = 1;
    private Context context;

    public TyphoonDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(BarcoDBMethods.QUERY_CREATE_TABLE_TP_CAT_BARCO);
        sqLiteDatabase.execSQL(ChecklistDBMethods.QUERY_CREATE_TABLE_TP_TRAN_CHECKLIST);
        sqLiteDatabase.execSQL(EvidenciasDBMethods.QUERY_CREATE_TABLE_TP_TRAN_CL_EVIDENCIA);
        sqLiteDatabase.execSQL(FoliosDBMethods.QUERY_CREATE_TABLE_TP_TRAN_REVISION);
        sqLiteDatabase.execSQL(UsuarioDBMethods.QUERY_CREATE_TABLE_TP_CAT_USUARIO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
