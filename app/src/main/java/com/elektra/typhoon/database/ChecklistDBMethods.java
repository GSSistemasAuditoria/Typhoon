package com.elektra.typhoon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.objetos.response.Checklist;
import com.elektra.typhoon.objetos.response.ChecklistData;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.PreguntaData;
import com.elektra.typhoon.objetos.response.ResponseCartera;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.objetos.response.RubroData;

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

    public static final String TP_CAT_CHEKLIST = "TP_CAT_CHEKLIST";
    public static final String TP_CAT_CL_RUBRO = "TP_CAT_CL_RUBRO";
    public static final String TP_CAT_CL_PREGUNTA = "TP_CAT_CL_PREGUNTA";
    public static final String TP_TRAN_CL_RESPUESTA = "TP_TRAN_CL_RESPUESTA";

    public static String QUERY_CREATE_TABLE_TP_CAT_CHEKLIST = "CREATE TABLE " + TP_CAT_CHEKLIST + " (" +
            "ID_REVISION INTEGER, " +
            "ID_CHECKLIST INTEGER, " +
            "ID_ESTATUS INTEGER, " +
            "ID_LOGO INTEGER, " +
            "ID_TIPO_REVISION INTEGER, " +
            "NOMBRE TEXT, " +
            "PONDERACION INTEGER, " +
            "PRIMARY KEY (ID_REVISION,ID_CHECKLIST))";

    public void createChecklist(ChecklistData checklist){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_REVISION",checklist.getIdRevision());
        values.put("ID_CHECKLIST",checklist.getIdChecklist());
        values.put("ID_ESTATUS",checklist.getIdEstatus());
        values.put("ID_LOGO",checklist.getIdLogo());
        values.put("ID_TIPO_REVISION",checklist.getIdTipoRevision());
        values.put("NOMBRE",checklist.getNombre());
        values.put("PONDERACION",checklist.getPonderacion());
        db.insertWithOnConflict(TP_CAT_CHEKLIST, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<ChecklistData> readChecklists(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<ChecklistData> listChecklist = new ArrayList<>();
        String query = "SELECT ID_REVISION,ID_CHECKLIST,ID_ESTATUS,ID_LOGO,ID_TIPO_REVISION,NOMBRE,PONDERACION FROM " + TP_CAT_CHEKLIST;
        if(condition != null){
            query = query + " " + condition;
        }
        Cursor cursor = db.rawQuery(query,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    ChecklistData checklist = new ChecklistData();
                    checklist.setIdRevision(cursor.getInt(0));
                    checklist.setIdChecklist(cursor.getInt(1));
                    checklist.setIdEstatus(cursor.getInt(2));
                    checklist.setIdLogo(cursor.getInt(3));
                    checklist.setIdTipoRevision(cursor.getInt(4));
                    checklist.setNombre(cursor.getString(5));
                    checklist.setPonderacion(cursor.getInt(6));
                    listChecklist.add(checklist);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listChecklist;
    }

    public ChecklistData readChecklist(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ChecklistData checklist = null;
        String query = "SELECT ID_REVISION,ID_CHECKLIST,ID_ESTATUS,ID_LOGO,ID_TIPO_REVISION,NOMBRE,PONDERACION FROM " + TP_CAT_CHEKLIST;
        if(condition != null){
            query = query + " " + condition;
        }
        Cursor cursor = db.rawQuery(query,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    checklist = new ChecklistData();
                    checklist.setIdRevision(cursor.getInt(0));
                    checklist.setIdChecklist(cursor.getInt(1));
                    checklist.setIdEstatus(cursor.getInt(2));
                    checklist.setIdLogo(cursor.getInt(3));
                    checklist.setIdTipoRevision(cursor.getInt(4));
                    checklist.setNombre(cursor.getString(5));
                    checklist.setPonderacion(cursor.getInt(6));
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return checklist;
    }

    public void updateChecklist(ContentValues values,String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.update(TP_CAT_CHEKLIST,values,condition,args);
        db.close();
    }

    public void deleteChecklist(String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.delete(TP_CAT_CHEKLIST, condition,args);
        db.close();
    }

    //**********************************************************************************************

    public static String QUERY_CREATE_TABLE_TP_CAT_CL_RUBRO = "CREATE TABLE " + TP_CAT_CL_RUBRO + " (" +
            "ID_REVISION INTEGER, " +
            "ID_CHECKLIST INTEGER, " +
            "ID_RUBRO INTEGER, " +
            "ESTATUS INTEGER, " +
            "NOMBRE TEXT, " +
            "PRIMARY KEY (ID_REVISION,ID_CHECKLIST,ID_RUBRO))";

    public void createRubro(RubroData rubroData){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_REVISION",rubroData.getIdRevision());
        values.put("ID_CHECKLIST",rubroData.getIdChecklist());
        values.put("ID_RUBRO",rubroData.getIdRubro());
        values.put("ESTATUS",rubroData.getEstatus());
        values.put("NOMBRE",rubroData.getNombre());
        db.insertWithOnConflict(TP_CAT_CL_RUBRO, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<RubroData> readRubro(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<RubroData> listRubro = new ArrayList<>();
        String query = "SELECT ID_REVISION,ID_CHECKLIST,ID_RUBRO,ESTATUS,NOMBRE FROM " + TP_CAT_CL_RUBRO;
        if(condition != null){
            query = query + " " + condition;
        }
        Cursor cursor = db.rawQuery(query,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    RubroData rubroData = new RubroData();
                    rubroData.setIdRevision(cursor.getInt(0));
                    rubroData.setIdChecklist(cursor.getInt(1));
                    rubroData.setIdRubro(cursor.getInt(2));
                    rubroData.setEstatus(cursor.getInt(3));
                    rubroData.setNombre(cursor.getString(4));
                    listRubro.add(rubroData);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listRubro;
    }

    //**********************************************************************************************

    public static String QUERY_CREATE_TABLE_TP_CAT_CL_PREGUNTA = "CREATE TABLE " + TP_CAT_CL_PREGUNTA + " (" +
            "ID_REVISION INTEGER, " +
            "ID_CHECKLIST INTEGER, " +
            "ID_PREGUNTA INTEGER, " +
            "ID_TIPO_RESPUESTA INTEGER, " +
            "ID_RUBRO INTEGER, " +
            "ESTATUS INTEGER, " +
            "DESCRIPCION TEXT, " +
            "PRIMARY KEY (ID_REVISION,ID_CHECKLIST,ID_RUBRO,ID_PREGUNTA))";

    public void createPregunta(PreguntaData preguntaData){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_REVISION",preguntaData.getIdRevision());
        values.put("ID_CHECKLIST",preguntaData.getIdChecklist());
        values.put("ID_RUBRO",preguntaData.getIdRubro());
        values.put("ID_PREGUNTA",preguntaData.getIdPregunta());
        values.put("ID_TIPO_RESPUESTA",preguntaData.getIdTipoRespuesta());
        values.put("ESTATUS",preguntaData.getEstatus());
        values.put("DESCRIPCION",preguntaData.getDescripcion());
        db.insertWithOnConflict(TP_CAT_CL_PREGUNTA, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<Pregunta> readPregunta(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<Pregunta> listPreguntas = new ArrayList<>();
        String query = "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_TIPO_RESPUESTA,ID_RUBRO,ESTATUS,DESCRIPCION FROM " + TP_CAT_CL_PREGUNTA;
        if(condition != null){
            query = query + " " + condition;
        }
        Cursor cursor = db.rawQuery(query,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    Pregunta pregunta = new Pregunta();
                    pregunta.setIdRevision(cursor.getInt(0));
                    pregunta.setIdChecklist(cursor.getInt(1));

                    listPreguntas.add(pregunta);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listPreguntas;
    }

    //**********************************************************************************************

    public static String QUERY_CREATE_TABLE_TP_TRAN_CL_RESPUESTA = "CREATE TABLE " + TP_TRAN_CL_RESPUESTA + " (" +
            "ID_REVISION INTEGER, " +
            "ID_CHECKLIST INTEGER, " +
            "ID_PREGUNTA INTEGER, " +
            "ID_RUBRO INTEGER, " +
            "ID_ESTATUS INTEGER, " +
            "ID_BARCO INTEGER, " +
            "ID_REGISTRO INTEGER, " +
            "ID_RESPUESTA INTEGER, " +
            "PRIMARY KEY (ID_REVISION,ID_CHECKLIST,ID_RUBRO,ID_PREGUNTA,ID_BARCO,ID_REGISTRO))";

    public void createRespuesta(RespuestaData respuestaData){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_REVISION",respuestaData.getIdRevision());
        values.put("ID_CHECKLIST",respuestaData.getIdChecklist());
        values.put("ID_RUBRO",respuestaData.getIdRubro());
        values.put("ID_PREGUNTA",respuestaData.getIdPregunta());
        values.put("ID_ESTATUS",respuestaData.getIdEstatus());
        values.put("ID_BARCO",respuestaData.getIdBarco());
        values.put("ID_REGISTRO",respuestaData.getIdRegistro());
        values.put("ID_RESPUESTA",respuestaData.getIdRespuesta());
        db.insertWithOnConflict(TP_TRAN_CL_RESPUESTA, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<RespuestaData> readRespuesta(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<RespuestaData> listRespuestas = new ArrayList<>();
        String query = "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_RUBRO,ID_ESTATUS,ID_BARCO,ID_REGISTRO,ID_RESPUESTA FROM " + TP_TRAN_CL_RESPUESTA;
        if(condition != null){
            query = query + " " + condition;
        }
        Cursor cursor = db.rawQuery(query,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    RespuestaData respuestaData = new RespuestaData();
                    respuestaData.setIdRevision(cursor.getInt(0));
                    respuestaData.setIdChecklist(cursor.getInt(1));
                    respuestaData.setIdPregunta(cursor.getInt(2));
                    respuestaData.setIdRubro(cursor.getInt(3));
                    respuestaData.setIdEstatus(cursor.getInt(4));
                    respuestaData.setIdBarco(cursor.getInt(5));
                    respuestaData.setIdRegistro(cursor.getInt(6));
                    respuestaData.setIdRespuesta(cursor.getInt(7));
                    listRespuestas.add(respuestaData);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listRespuestas;
    }
}
