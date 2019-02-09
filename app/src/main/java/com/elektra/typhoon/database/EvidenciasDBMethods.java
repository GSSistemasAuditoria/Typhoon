package com.elektra.typhoon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.utils.Utils;

import java.io.IOException;
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
            "ID_REVISION INTEGER, " +
            "ID_CHECKLIST INTEGER, " +
            "ID_RUBRO INTEGER, " +
            "ID_PREGUNTA INTEGER, " +
            "ID_EVIDENCIA TEXT, " +
	        "ID_REGISTRO INTEGER, " +
            "ID_BARCO INTEGER, " +
            "NOMBRE TEXT, " +
	        "CONTENIDO TEXT, " +
            "CONTENIDO_PREVIEW TEXT, " +
	        "ID_ESTATUS INTEGER, " +
	        "ID_ETAPA INTEGER, " +
            "PRIMARY KEY (ID_REVISION,ID_CHECKLIST,ID_RUBRO,ID_PREGUNTA,ID_EVIDENCIA))";

    public void createEvidencia(Evidencia evidencia){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_EVIDENCIA",evidencia.getIdEvidencia());
        values.put("NOMBRE",evidencia.getNombre());
        values.put("CONTENIDO",evidencia.getContenido());
        if(evidencia.getContenidoPreview() == null){
            try {
                if(!evidencia.getNombre().contains("pdf")) {
                    Bitmap bitmap = Utils.base64ToBitmap(evidencia.getContenido());
                    Bitmap bitmapResize = Utils.resizeImageBitmap(bitmap);
                    String base64 = Utils.bitmapToBase64(bitmapResize);
                    values.put("CONTENIDO_PREVIEW", base64);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            values.put("CONTENIDO_PREVIEW", evidencia.getContenidoPreview());
        }
        values.put("ID_ESTATUS",evidencia.getIdEstatus());
        values.put("ID_ETAPA",evidencia.getIdEtapa());
        values.put("ID_REVISION",evidencia.getIdRevision());
        values.put("ID_CHECKLIST",evidencia.getIdChecklist());
        values.put("ID_RUBRO",evidencia.getIdRubro());
        values.put("ID_PREGUNTA",evidencia.getIdPregunta());
        values.put("ID_REGISTRO",evidencia.getIdRegistro());
        values.put("ID_BARCO",evidencia.getIdBarco());
        db.insertWithOnConflict(TP_TRAN_CL_EVIDENCIA, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<Evidencia> readEvidencias(String condition, String[] args,boolean flagJson) throws IOException {
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<Evidencia> listEvidencia = new ArrayList<>();
        String query = "SELECT ID_EVIDENCIA,NOMBRE,CONTENIDO_PREVIEW,ID_ESTATUS,ID_ETAPA,ID_REVISION,ID_CHECKLIST,ID_RUBRO,ID_PREGUNTA,ID_REGISTRO,ID_BARCO,CONTENIDO FROM " + TP_TRAN_CL_EVIDENCIA;
        if(condition != null){
            query = query + " " + condition;
        }
        Cursor cursor = db.rawQuery(query,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    Evidencia evidencia = new Evidencia();
                    evidencia.setIdEvidencia(cursor.getString(0));
                    evidencia.setNombre(cursor.getString(1));
                    //evidencia.setContenido(cursor.getString(2));
                    if(!flagJson) {
                        if (!evidencia.getNombre().contains("pdf")) {
                            evidencia.setSmallBitmap(Utils.base64ToBitmap(cursor.getString(2)));
                        }
                    }else{
                        evidencia.setContenido(cursor.getString(11));
                    }
                    evidencia.setIdEstatus(cursor.getInt(3));
                    evidencia.setIdEtapa(cursor.getInt(4));
                    evidencia.setIdRevision(cursor.getInt(5));
                    evidencia.setIdChecklist(cursor.getInt(6));
                    evidencia.setIdRubro(cursor.getInt(7));
                    evidencia.setIdPregunta(cursor.getInt(8));
                    evidencia.setIdRegistro(cursor.getInt(9));
                    evidencia.setIdBarco(cursor.getInt(10));
                    listEvidencia.add(evidencia);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listEvidencia;
    }

    public Evidencia readEvidencia(String condition, String[] args) throws IOException {
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        Evidencia evidencia = null;
        String query = "SELECT ID_EVIDENCIA,NOMBRE,CONTENIDO,ID_ESTATUS,ID_ETAPA,ID_REVISION,ID_CHECKLIST,ID_RUBRO,ID_PREGUNTA,ID_REGISTRO,ID_BARCO FROM " + TP_TRAN_CL_EVIDENCIA;
        if(condition != null){
            query = query + " " + condition;
        }
        Cursor cursor = db.rawQuery(query,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    evidencia = new Evidencia();
                    evidencia.setIdEvidencia(cursor.getString(0));
                    evidencia.setNombre(cursor.getString(1));
                    if(!evidencia.getNombre().contains("pdf")) {
                        evidencia.setOriginalBitmap(Utils.base64ToBitmap(cursor.getString(2)));
                    }
                    evidencia.setContenido(cursor.getString(2));
                    //evidencia.setContenidoPreview(cursor.getString(2));
                    //evidencia.setSmallBitmap(Utils.base64ToBitmap(cursor.getString(2)));
                    evidencia.setIdEstatus(cursor.getInt(3));
                    evidencia.setIdEtapa(cursor.getInt(4));
                    evidencia.setIdRevision(cursor.getInt(5));
                    evidencia.setIdChecklist(cursor.getInt(6));
                    evidencia.setIdRubro(cursor.getInt(7));
                    evidencia.setIdPregunta(cursor.getInt(8));
                    evidencia.setIdRegistro(cursor.getInt(9));
                    evidencia.setIdBarco(cursor.getInt(10));
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
