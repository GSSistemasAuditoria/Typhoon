package com.elektra.typhoon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.objetos.response.Historico;

import java.util.ArrayList;
import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 22/02/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class HistoricoDBMethods {

    private Context context;

    public HistoricoDBMethods(Context context){
        this.context = context;
    }

    public static final String TP_TRAN_HISTORIAL_EVIDENCIA = "TP_TRAN_HISTORIAL_EVIDENCIA";

    public static String QUERY_CREATE_TABLE_TP_TRAN_HISTORIAL_EVIDENCIA = "CREATE TABLE " + TP_TRAN_HISTORIAL_EVIDENCIA + " (" +
            "ID_EVIDENCIA TEXT, " +
            "ID_ETAPA INTEGER, " +
            "ID_USUARIO TEXT, " +
            "MOTIVO TEXT, " +
            "CONSEC INTEGER, " +
            "ID_REVISION INTEGER, " +
            "ID_CHECKLIST INTEGER, " +
            "FECHA_MOD TEXT, " +
            "PRIMARY KEY (ID_EVIDENCIA,CONSEC,ID_ETAPA))";

    public void createHistorico(Historico historico){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_EVIDENCIA",historico.getIdEvidencia());
        values.put("ID_ETAPA",historico.getIdEtapa());
        values.put("ID_USUARIO",historico.getIdUsuario());
        values.put("MOTIVO",historico.getMotivo());
        values.put("CONSEC",historico.getConsec());
        values.put("ID_REVISION",historico.getIdRevision());
        values.put("ID_CHECKLIST",historico.getIdChecklist());
        values.put("FECHA_MOD",historico.getFechaMod());
        db.insertWithOnConflict(TP_TRAN_HISTORIAL_EVIDENCIA, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<Historico> readHistorico(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<Historico> listHistorico = new ArrayList<>();
        /*StringBuilder query = new StringBuilder();
        query.append("SELECT ID_EVIDENCIA,ID_ETAPA,ID_USUARIO,MOTIVO,CONSEC,ID_REVISION,ID_CHECKLIST,FECHA_MOD FROM ").append(TP_TRAN_HISTORIAL_EVIDENCIA);
        if(condition != null){
            query.append(" ").append(condition);
        }
        Cursor cursor = db.rawQuery(query.toString(),args);//*/
        Cursor cursor = db.rawQuery(condition,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    Historico historico = new Historico();
                    historico.setIdEvidencia(cursor.getString(0));
                    historico.setIdEtapa(cursor.getInt(1));
                    historico.setIdUsuario(cursor.getString(2));
                    historico.setMotivo(cursor.getString(3));
                    historico.setConsec(cursor.getInt(4));
                    historico.setIdRevision(cursor.getInt(5));
                    historico.setIdChecklist(cursor.getInt(6));
                    historico.setFechaMod(cursor.getString(7));
                    listHistorico.add(historico);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listHistorico;
    }

    public void deleteHistorico(String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.delete(TP_TRAN_HISTORIAL_EVIDENCIA, condition,args);
        db.close();
    }
}
