package com.elektra.typhoon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.objetos.response.Anexo;

import java.util.ArrayList;
import java.util.List;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 20/03/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class AnexosDBMethods {

    private Context context;

    public AnexosDBMethods(Context context){
        this.context = context;
    }

    public static final String TP_CAT_ANEXOS = "TP_CAT_ANEXOS";
    public static final String TP_REL_REVISION_ANEXOS = "TP_REL_REVISION_ANEXOS";
    public static final String TP_TRAN_ANEXOS = "TP_TRAN_ANEXOS";

    public static String QUERY_CREATE_TABLE_TP_CAT_ANEXOS = "CREATE TABLE " + TP_CAT_ANEXOS + " (" +
            "ID_ANEXO INTEGER, " +
            "ID_SUBANEXO INTEGER, " +
            "DESCRIPCION TEXT, " +
            "PRIMARY KEY (ID_ANEXO,ID_SUBANEXO))";

    public void createCatalogoAnexo(Anexo anexo){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_ANEXO",anexo.getIdAnexo());
        values.put("ID_SUBANEXO",anexo.getIdSubAnexo());
        values.put("DESCRIPCION",anexo.getDescripcion());
        db.insertWithOnConflict(TP_CAT_ANEXOS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<Anexo> readCatalogoAnexos(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<Anexo> listAnexos = new ArrayList<>();
        Cursor cursor = db.rawQuery(condition,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    Anexo anexo = new Anexo();
                    anexo.setIdAnexo(cursor.getInt(0));
                    anexo.setIdSubAnexo(cursor.getInt(1));
                    anexo.setDescripcion(cursor.getString(2));
                    listAnexos.add(anexo);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listAnexos;
    }

    public void updateCatalogoAnexo(ContentValues values,String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.update(TP_CAT_ANEXOS,values,condition,args);
        db.close();
    }

    public void deleteCatalogoAnexo(String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.delete(TP_CAT_ANEXOS, condition,args);
        db.close();
    }

    //**********************************************************************************************

    public static String QUERY_CREATE_TABLE_TP_REL_REVISION_ANEXOS = "CREATE TABLE " + TP_REL_REVISION_ANEXOS + " (" +
            "ID_ANEXO INTEGER, " +
            "ID_REVISION INTEGER, " +
            "PRIMARY KEY (ID_ANEXO,ID_REVISION))";

    public void createRelacionRevisionAnexo(int idAnexo, int idRevision){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_ANEXO",idAnexo);
        values.put("ID_REVISION",idRevision);
        db.insertWithOnConflict(TP_REL_REVISION_ANEXOS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<Integer> readRelacionRevisionAnexo(int idRevision){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<Integer> relacion = new ArrayList<>();
        String query = "SELECT ID_ANEXO FROM " + TP_REL_REVISION_ANEXOS + " WHERE ID_REVISION = ?";
        Cursor cursor = db.rawQuery(query,new String[]{String.valueOf(idRevision)});
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    relacion.add(cursor.getInt(0));
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return relacion;
    }

    public void deleteRelacionRevisionAnexo(String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.delete(TP_REL_REVISION_ANEXOS, condition,args);
        db.close();
    }

    //**********************************************************************************************

    public static String QUERY_CREATE_TABLE_TP_TRAN_ANEXOS = "CREATE TABLE " + TP_TRAN_ANEXOS + " (" +
            "ID_REVISION INTEGER, " +
            "ID_ANEXO INTEGER, " +
            "ID_SUBANEXO INTEGER, " +
            "ID_DOCUMENTO TEXT, " +
            "ID_ETAPA INTEGER, " +
            "DOCUMENTO TEXT, " +
            "NOMBRE TEXT, " +
            "SUBANEXO_FCH_SINC DATE, " +
            "SUBANEXO_FCH_MOD DATE, " +
            "SELECCIONADO INTEGER, " +
            "ID_ROL INTEGER, " +
            "ID_USUARIO TEXT, " +
            "PRIMARY KEY (ID_REVISION,ID_SUBANEXO))";

    public void createAnexo(Anexo anexo){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_REVISION",anexo.getIdRevision());
        //values.put("ID_ANEXO",anexo.getIdAnexo());
        values.put("ID_SUBANEXO",anexo.getIdSubAnexo());
        //values.put("ID_DOCUMENTO",anexo.getIdDocumento());
        values.put("ID_ETAPA",anexo.getIdEtapa());
        values.put("DOCUMENTO",anexo.getBase64());
        values.put("NOMBRE",anexo.getNombreArchivo());
        values.put("SUBANEXO_FCH_SINC",anexo.getFechaSinc());
        values.put("SUBANEXO_FCH_MOD",anexo.getFechaMod());
        values.put("SELECCIONADO",0);
        values.put("ID_ROL",anexo.getIdRol());
        values.put("ID_USUARIO",anexo.getIdUsuario());
        db.insertWithOnConflict(TP_TRAN_ANEXOS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<Anexo> readAnexos(String query,String[] args){
        CursorWindowFixer.fix();
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<Anexo> listAnexos = new ArrayList<>();
        Cursor cursor = db.rawQuery(query,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    Anexo anexo = new Anexo();
                    anexo.setIdRevision(cursor.getInt(0));
                    //anexo.setIdAnexo(cursor.getInt(1));
                    anexo.setIdSubAnexo(cursor.getInt(2));
                    anexo.setIdDocumento(cursor.getString(3));
                    anexo.setIdEtapa(cursor.getInt(4));
                    anexo.setBase64(cursor.getString(5));
                    anexo.setNombreArchivo(cursor.getString(6));
                    anexo.setFechaSinc(cursor.getString(7));
                    anexo.setFechaMod(cursor.getString(9));
                    if(cursor.getInt(8) == 0){
                        anexo.setSeleccionado(false);
                    }else{
                        anexo.setSeleccionado(true);
                    }
                    anexo.setIdRol(cursor.getInt(10));
                    anexo.setIdUsuario(cursor.getString(11));
                    listAnexos.add(anexo);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listAnexos;
    }

    public List<Anexo> readAnexosSinDocumento(String query,String[] args){
        CursorWindowFixer.fix();
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<Anexo> listAnexos = new ArrayList<>();
        Cursor cursor = db.rawQuery(query,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    Anexo anexo = new Anexo();
                    anexo.setIdRevision(cursor.getInt(0));
                    //anexo.setIdAnexo(cursor.getInt(1));
                    anexo.setIdSubAnexo(cursor.getInt(2));
                    anexo.setIdDocumento(cursor.getString(3));
                    anexo.setIdEtapa(cursor.getInt(4));
                    //anexo.setBase64(cursor.getString(5));
                    anexo.setNombreArchivo(cursor.getString(5));
                    anexo.setFechaSinc(cursor.getString(6));
                    if(cursor.getInt(7) == 0){
                        anexo.setSeleccionado(false);
                    }else{
                        anexo.setSeleccionado(true);
                    }
                    anexo.setFechaMod(cursor.getString(8));
                    anexo.setIdRol(cursor.getInt(9));
                    anexo.setIdUsuario(cursor.getString(10));
                    listAnexos.add(anexo);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listAnexos;
    }

    public void updateAnexo(ContentValues values,String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.update(TP_TRAN_ANEXOS,values,condition,args);
        db.close();
    }

    public void deleteAnexo(String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.delete(TP_TRAN_ANEXOS, condition,args);
        db.close();
    }
}
