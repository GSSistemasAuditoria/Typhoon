package com.elektra.typhoon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.objetos.response.ResponseLogin;

import java.util.ArrayList;
import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 16/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class UsuarioDBMethods {

    private Context context;

    public UsuarioDBMethods(Context context){
        this.context = context;
    }

    public static final String TP_CAT_USUARIO = "TP_CAT_USUARIO";

    public static String QUERY_CREATE_TABLE_TP_CAT_USUARIO = "CREATE TABLE " + TP_CAT_USUARIO + " (" +
            "ID_USUARIO TEXT PRIMARY KEY, " +
            "CORREO TEXT, " +
            "NOMBRE TEXT, " +
            "PASSWORD TEXT, " +
            "INTERNO INTEGER, " +
            "ID_ROL INTEGER, " +
            "ESTATUS INTEGER)";

    public void createUsuario(ResponseLogin.Usuario usuario){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_USUARIO",usuario.getIdUsuario());
        values.put("CORREO",usuario.getCorreo());
        values.put("NOMBRE",usuario.getNombre());
        values.put("PASSWORD",usuario.getPassword());
        if(usuario.getInterno()) {
            values.put("INTERNO", 1);
        }else{
            values.put("INTERNO", 0);
        }
        values.put("ID_ROL",usuario.getIdrol());
        values.put("ESTATUS",usuario.getEstatus());
        db.insertWithOnConflict(TP_CAT_USUARIO, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<ResponseLogin.Usuario> readUsuarios(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<ResponseLogin.Usuario> listUsuarios = new ArrayList<>();
        String query = "SELECT ID_USUARIO,CORREO,NOMBRE,PASSWORD,INTERNO,ID_ROL,ESTATUS FROM " + TP_CAT_USUARIO;
        if(condition != null){
            query = query + " " + condition;
        }
        Cursor cursor = db.rawQuery(query,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    ResponseLogin.Usuario usuario = new ResponseLogin.Usuario();
                    usuario.setIdUsuario(cursor.getString(0));
                    usuario.setCorreo(cursor.getString(1));
                    usuario.setNombre(cursor.getString(2));
                    usuario.setPassword(cursor.getString(3));
                    if(cursor.getInt(4) == 1){
                        usuario.setInterno(true);
                    }else{
                        usuario.setInterno(false);
                    }
                    usuario.setIdrol(cursor.getInt(5));
                    usuario.setEstatus(cursor.getInt(6));
                    listUsuarios.add(usuario);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listUsuarios;
    }

    public ResponseLogin.Usuario readUsuario(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ResponseLogin.Usuario usuario = null;
        String query = "SELECT ID_USUARIO,CORREO,NOMBRE,PASSWORD,INTERNO,ID_ROL,ESTATUS FROM " + TP_CAT_USUARIO;
        if(condition != null){
            query = query + " " + condition;
        }
        Cursor cursor = db.rawQuery(query,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    usuario = new ResponseLogin.Usuario();
                    usuario.setIdUsuario(cursor.getString(0));
                    usuario.setCorreo(cursor.getString(1));
                    usuario.setNombre(cursor.getString(2));
                    usuario.setPassword(cursor.getString(3));
                    if(cursor.getInt(4) == 1){
                        usuario.setInterno(true);
                    }else{
                        usuario.setInterno(false);
                    }
                    usuario.setIdrol(cursor.getInt(5));
                    usuario.setEstatus(cursor.getInt(6));
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return usuario;
    }

    public void updateUsuario(ContentValues values,String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.update(TP_CAT_USUARIO,values,condition,args);
        db.close();
    }

    public void deleteUsuario(String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.delete(TP_CAT_USUARIO, condition,args);
        db.close();
    }
}
