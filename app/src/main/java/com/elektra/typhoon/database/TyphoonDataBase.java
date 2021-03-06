package com.elektra.typhoon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    private String QUERY_CREATE_TABLE_LOG_ERROR = "CREATE TABLE LOG_ERROR (" +
            "LOG TEXT)";

    public TyphoonDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(BarcoDBMethods.QUERY_CREATE_TABLE_TP_CAT_BARCO);
        sqLiteDatabase.execSQL(ChecklistDBMethods.QUERY_CREATE_TABLE_TP_CAT_CHEKLIST);
        sqLiteDatabase.execSQL(ChecklistDBMethods.QUERY_CREATE_TABLE_TP_CAT_CL_PREGUNTA);
        sqLiteDatabase.execSQL(ChecklistDBMethods.QUERY_CREATE_TABLE_TP_CAT_CL_RUBRO);
        sqLiteDatabase.execSQL(ChecklistDBMethods.QUERY_CREATE_TABLE_TP_TRAN_CL_RESPUESTA);
        sqLiteDatabase.execSQL(EvidenciasDBMethods.QUERY_CREATE_TABLE_TP_TRAN_CL_EVIDENCIA);
        sqLiteDatabase.execSQL(FoliosDBMethods.QUERY_CREATE_TABLE_TP_TRAN_REVISION);
        sqLiteDatabase.execSQL(UsuarioDBMethods.QUERY_CREATE_TABLE_TP_CAT_USUARIO);
        sqLiteDatabase.execSQL(CatalogosDBMethods.QUERY_CREATE_TABLE_TP_CAT_CL_ESTATUS_EVIDENCIA);
        sqLiteDatabase.execSQL(CatalogosDBMethods.QUERY_CREATE_TABLE_TP_CAT_CL_ETAPA_EVIDENCIA);
        sqLiteDatabase.execSQL(CatalogosDBMethods.QUERY_CREATE_TABLE_TP_CAT_CL_RESPUESTA);
        sqLiteDatabase.execSQL(CatalogosDBMethods.QUERY_CREATE_TABLE_ESTATUS_REVISION);
        sqLiteDatabase.execSQL(CatalogosDBMethods.QUERY_CREATE_TABLE_ROLES_USUARIO);
        sqLiteDatabase.execSQL(CatalogosDBMethods.QUERY_CREATE_TABLE_TP_CAT_ETAPA_SUBANEXO);
        sqLiteDatabase.execSQL(HistoricoDBMethods.QUERY_CREATE_TABLE_TP_TRAN_HISTORIAL_EVIDENCIA);
        sqLiteDatabase.execSQL(HistoricoDBMethods.QUERY_CREATE_TABLE_TP_TRAN_HISTORIAL_SUBANEXO);
        sqLiteDatabase.execSQL(AnexosDBMethods.QUERY_CREATE_TABLE_TP_CAT_ANEXOS);
        sqLiteDatabase.execSQL(AnexosDBMethods.QUERY_CREATE_TABLE_TP_REL_REVISION_ANEXOS);
        sqLiteDatabase.execSQL(AnexosDBMethods.QUERY_CREATE_TABLE_TP_TRAN_ANEXOS);
        sqLiteDatabase.execSQL(CatalogosDBMethods.QUERY_CREATE_TABLE_TP_CAT_ANIOS);
        sqLiteDatabase.execSQL(NotificacionesDBMethods.QUERY_CREATE_TABLE_TP_TRAN_NOTIFICACIONES);
        sqLiteDatabase.execSQL(QUERY_CREATE_TABLE_LOG_ERROR);
    }

    public void deleteAll(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + BarcoDBMethods.TP_CAT_BARCO);
        db.execSQL("delete from " + ChecklistDBMethods.TP_CAT_CHEKLIST);
        db.execSQL("delete from " + ChecklistDBMethods.TP_CAT_CL_PREGUNTA);
        db.execSQL("delete from " + ChecklistDBMethods.TP_CAT_CL_RUBRO);
        db.execSQL("delete from " + ChecklistDBMethods.TP_TRAN_CL_RESPUESTA);
        db.execSQL("delete from " + EvidenciasDBMethods.TP_TRAN_CL_EVIDENCIA);
        db.execSQL("delete from " + FoliosDBMethods.TP_TRAN_REVISION);
        db.execSQL("delete from " + UsuarioDBMethods.TP_CAT_USUARIO);
        db.execSQL("delete from " + CatalogosDBMethods.TP_CAT_CL_ESTATUS_EVIDENCIA);
        db.execSQL("delete from " + CatalogosDBMethods.TP_CAT_CL_ETAPA_EVIDENCIA);
        db.execSQL("delete from " + CatalogosDBMethods.TP_CAT_CL_RESPUESTA);
        db.execSQL("delete from " + CatalogosDBMethods.TP_CAT_ESTATUS_REVISION);
        db.execSQL("delete from " + CatalogosDBMethods.TP_CAT_ROLES_USUARIO);
        db.execSQL("delete from " + CatalogosDBMethods.TP_CAT_ETAPA_SUBANEXO);
        db.execSQL("delete from " + CatalogosDBMethods.TP_CAT_ANIOS);
        db.execSQL("delete from " + HistoricoDBMethods.TP_TRAN_HISTORIAL_EVIDENCIA);
        db.execSQL("delete from " + HistoricoDBMethods.TP_TRAN_HISTORIAL_SUBANEXO);
        db.execSQL("delete from " + AnexosDBMethods.TP_TRAN_ANEXOS);
        db.execSQL("delete from " + AnexosDBMethods.TP_REL_REVISION_ANEXOS);
        db.execSQL("delete from " + AnexosDBMethods.TP_CAT_ANEXOS);
        db.execSQL("delete from " + NotificacionesDBMethods.TP_TRAN_NOTIFICACIONES);
        db.execSQL("delete from LOG_ERROR");
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static void createError(Context context, String error){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,Context.MODE_PRIVATE,null);
        ContentValues mValues = new ContentValues();
        mValues.put("LOG", error);
        long result = db.insert("LOG_ERROR", null, mValues);
        Log.e("TyphoonDB", "Result: " + result + " : Values: " + error);
    }

    public static String getErrores(Context context){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,Context.MODE_PRIVATE,null);
        Cursor cursor = db.rawQuery("Select LOG from LOG_ERROR order by rowid desc limit 5", null);
        String errores = "";
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    errores += cursor.getString(0) + "  |  ";
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
        return errores;
    }
}
