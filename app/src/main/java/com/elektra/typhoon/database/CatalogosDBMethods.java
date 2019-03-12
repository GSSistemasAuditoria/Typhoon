package com.elektra.typhoon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.objetos.response.EstatusEvidencia;
import com.elektra.typhoon.objetos.response.EstatusRevision;
import com.elektra.typhoon.objetos.response.EtapaEvidencia;
import com.elektra.typhoon.objetos.response.RolUsuario;
import com.elektra.typhoon.objetos.response.TipoRespuesta;

import java.util.ArrayList;
import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 01/02/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class CatalogosDBMethods {

    private Context context;

    public CatalogosDBMethods(Context context){
        this.context = context;
    }

    public static final String TP_CAT_CL_ESTATUS_EVIDENCIA = "TP_CAT_CL_ESTATUS_EVIDENCIA";
    public static final String TP_CAT_CL_ETAPA_EVIDENCIA = "TP_CAT_CL_ETAPA_EVIDENCIA";
    public static final String TP_CAT_CL_RESPUESTA = "TP_CAT_CL_RESPUESTA";
    public static final String TP_CAT_ESTATUS_REVISION = "TP_CAT_ESTATUS_REVISION";
    public static final String TP_CAT_ROLES_USUARIO = "TP_CAT_ROLES_USUARIO";

    public static String QUERY_CREATE_TABLE_TP_CAT_CL_ESTATUS_EVIDENCIA = "CREATE TABLE " + TP_CAT_CL_ESTATUS_EVIDENCIA + " (" +
            "ID_ESTATUS INTEGER PRIMARY KEY, " +
            "DESCRIPCION TEXT)";

    public void createEstatusEvidencia(EstatusEvidencia estatusEvidencia){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_ESTATUS",estatusEvidencia.getIdEstatus());
        values.put("DESCRIPCION",estatusEvidencia.getDescripcion());
        db.insertWithOnConflict(TP_CAT_CL_ESTATUS_EVIDENCIA, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<EstatusEvidencia> readEstatusEvidencia(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<EstatusEvidencia> listEstatus = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID_ESTATUS,DESCRIPCION FROM ").append(TP_CAT_CL_ESTATUS_EVIDENCIA);
        if(condition != null){
            query.append(" ").append(condition);
        }
        Cursor cursor = db.rawQuery(query.toString(),args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    EstatusEvidencia estatusEvidencia = new EstatusEvidencia();
                    estatusEvidencia.setIdEstatus(cursor.getInt(0));
                    estatusEvidencia.setDescripcion(cursor.getString(1));
                    listEstatus.add(estatusEvidencia);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listEstatus;
    }

    public void deleteEstatusEvidencia(){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.execSQL("delete from "+ TP_CAT_CL_ESTATUS_EVIDENCIA);
        db.close();
    }

    //**********************************************************************************************

    public static String QUERY_CREATE_TABLE_TP_CAT_CL_ETAPA_EVIDENCIA = "CREATE TABLE " + TP_CAT_CL_ETAPA_EVIDENCIA + " (" +
            "ID_ETAPA INTEGER PRIMARY KEY, " +
            "ID_USUARIO TEXT, " +
            "DESCRIPCION TEXT)";

    public void createEtapaEvidencia(EtapaEvidencia etapaEvidencia){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_ETAPA",etapaEvidencia.getIdEtapa());
        values.put("DESCRIPCION",etapaEvidencia.getDescripcion());
        values.put("ID_USUARIO",etapaEvidencia.getIdUsuario());
        db.insertWithOnConflict(TP_CAT_CL_ETAPA_EVIDENCIA, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<EtapaEvidencia> readEtapaEvidencia(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<EtapaEvidencia> listEtapas = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID_ETAPA,ID_USUARIO,DESCRIPCION FROM ").append(TP_CAT_CL_ETAPA_EVIDENCIA);
        if(condition != null){
            query.append(" ").append(condition);
        }
        Cursor cursor = db.rawQuery(query.toString(),args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    EtapaEvidencia etapaEvidencia = new EtapaEvidencia();
                    etapaEvidencia.setIdEtapa(cursor.getInt(0));
                    etapaEvidencia.setIdUsuario(cursor.getString(1));
                    etapaEvidencia.setDescripcion(cursor.getString(2));
                    listEtapas.add(etapaEvidencia);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listEtapas;
    }

    public void deleteEtapaEvidencia(){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.execSQL("delete from "+ TP_CAT_CL_ETAPA_EVIDENCIA);
        db.close();
    }

    //**********************************************************************************************

    public static String QUERY_CREATE_TABLE_TP_CAT_CL_RESPUESTA = "CREATE TABLE " + TP_CAT_CL_RESPUESTA + " (" +
            "ID_RESPUESTA INTEGER PRIMARY KEY, " +
            "ID_TIPO_RESPUESTA INTEGER, " +
            "DESCRIPCION TEXT)";

    public void createTipoRespuesta(TipoRespuesta tipoRespuesta){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_RESPUESTA",tipoRespuesta.getIdRespuesta());
        values.put("ID_TIPO_RESPUESTA",tipoRespuesta.getIdTipoRespuesta());
        values.put("DESCRIPCION",tipoRespuesta.getDescripcion());
        db.insertWithOnConflict(TP_CAT_CL_RESPUESTA, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<TipoRespuesta> readTipoRespuesta(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<TipoRespuesta> listTipoRespuesta = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID_RESPUESTA,ID_TIPO_RESPUESTA,DESCRIPCION FROM ").append(TP_CAT_CL_RESPUESTA);
        if(condition != null){
            query.append(" ").append(condition);
        }
        Cursor cursor = db.rawQuery(query.toString(),args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    TipoRespuesta tipoRespuesta = new TipoRespuesta();
                    tipoRespuesta.setIdRespuesta(cursor.getInt(0));
                    tipoRespuesta.setIdTipoRespuesta(cursor.getInt(1));
                    tipoRespuesta.setDescripcion(cursor.getString(2));
                    listTipoRespuesta.add(tipoRespuesta);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listTipoRespuesta;
    }

    public void deleteTipoRespuesta(){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.execSQL("delete from "+ TP_CAT_CL_RESPUESTA);
        db.close();
    }

    //**********************************************************************************************

    public static String QUERY_CREATE_TABLE_ESTATUS_REVISION = "CREATE TABLE " + TP_CAT_ESTATUS_REVISION + " (" +
            "ID_ESTATUS INTEGER PRIMARY KEY, " +
            "DESCRIPCION TEXT, " +
            "SRC TEXT)";

    public void createEstatusRevision(EstatusRevision estatusRevision){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_ESTATUS",estatusRevision.getIdEstatus());
        values.put("DESCRIPCION",estatusRevision.getDescripcion());
        values.put("SRC",estatusRevision.getImagen());
        db.insertWithOnConflict(TP_CAT_ESTATUS_REVISION, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<EstatusRevision> readEstatusRevision(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<EstatusRevision> listEstatusRevision = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID_ESTATUS,DESCRIPCION,SRC FROM ").append(TP_CAT_ESTATUS_REVISION);
        if(condition != null){
            query.append(" ").append(condition);
        }
        Cursor cursor = db.rawQuery(query.toString(),args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    EstatusRevision estatusRevision = new EstatusRevision();
                    estatusRevision.setIdEstatus(cursor.getInt(0));
                    estatusRevision.setDescripcion(cursor.getString(1));
                    estatusRevision.setImagen(cursor.getString(2));
                    listEstatusRevision.add(estatusRevision);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listEstatusRevision;
    }

    public void deleteEstatusRevision(){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.execSQL("delete from "+ TP_CAT_ESTATUS_REVISION);
        db.close();
    }

    //**********************************************************************************************

    public static String QUERY_CREATE_TABLE_ROLES_USUARIO = "CREATE TABLE " + TP_CAT_ROLES_USUARIO + " (" +
            "ID_ROL INTEGER PRIMARY KEY, " +
            "DESCRIPCION TEXT)";

    public void createRolUsuario(RolUsuario rolUsuario){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_ROL",rolUsuario.getIdRol());
        values.put("DESCRIPCION",rolUsuario.getDescripcion());
        db.insertWithOnConflict(TP_CAT_ROLES_USUARIO, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<RolUsuario> readRolesUsuario(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<RolUsuario> listRolesUsuario = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID_ROL,DESCRIPCION FROM ").append(TP_CAT_ROLES_USUARIO);
        if(condition != null){
            query.append(" ").append(condition);
        }
        Cursor cursor = db.rawQuery(query.toString(),args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    RolUsuario rolUsuario = new RolUsuario();
                    rolUsuario.setIdRol(cursor.getInt(0));
                    rolUsuario.setDescripcion(cursor.getString(1));
                    listRolesUsuario.add(rolUsuario);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listRolesUsuario;
    }

    public void deleteRolesUsuario(){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.execSQL("delete from "+ TP_CAT_ROLES_USUARIO);
        db.close();
    }
}
