package com.elektra.typhoon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.objetos.response.Historico;
import com.elektra.typhoon.objetos.response.HistoricoAnexo;
import com.elektra.typhoon.utils.Utils;

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
    public static final String TP_TRAN_HISTORIAL_SUBANEXO = "TP_TRAN_HISTORIAL_SUBANEXO";

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
        values.put("MOTIVO", Utils.removeSpecialCharacters(historico.getMotivo()));
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

    //**********************************************************************************************

    public static String QUERY_CREATE_TABLE_TP_TRAN_HISTORIAL_SUBANEXO = "CREATE TABLE " + TP_TRAN_HISTORIAL_SUBANEXO + " (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "ID_SUBANEXO INTEGER, " +
            "ID_REVISION INTEGER, " +
            "ID_ETAPA INTEGER, " +
            "ID_USUARIO TEXT, " +
            "NOMBRE TEXT, " +
            "MOTIVO_RECHAZO TEXT, " +
            "GUID TEXT, " +
            "SUBANEXO_FCH_SINC DATE, " +
            "ID_ROL INTEGER, " +
            "FECHA_MOD DATE)";
            //"PRIMARY KEY (ID,ID_SUBANEXO,ID_REVISION))";

    public void createHistoricoAnexo(HistoricoAnexo historicoAnexo){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        ContentValues values = new ContentValues();
        values.put("ID_SUBANEXO",historicoAnexo.getIdSubAnexo());
        values.put("ID_REVISION",historicoAnexo.getIdRevision());
        values.put("ID_ETAPA",historicoAnexo.getIdEtapa());
        values.put("ID_USUARIO",historicoAnexo.getIdUsuario());
        values.put("NOMBRE",historicoAnexo.getNombre());
        values.put("MOTIVO_RECHAZO", Utils.removeSpecialCharacters(historicoAnexo.getMotivoRechazo()));
        values.put("FECHA_MOD",historicoAnexo.getFechaMod());
        values.put("GUID",historicoAnexo.getGuid());
        values.put("SUBANEXO_FCH_SINC",historicoAnexo.getFechaSincronizacion());
        values.put("ID_ROL",historicoAnexo.getIdRol());
        db.insertWithOnConflict(TP_TRAN_HISTORIAL_SUBANEXO, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<HistoricoAnexo> readHistoricoAnexo(String condition, String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        List<HistoricoAnexo> listHistorico = new ArrayList<>();
        Cursor cursor = db.rawQuery(condition,args);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    HistoricoAnexo historico = new HistoricoAnexo();
                    historico.setIdSubAnexo(cursor.getInt(0));
                    historico.setIdRevision(cursor.getInt(1));
                    historico.setIdEtapa(cursor.getInt(2));
                    historico.setIdUsuario(cursor.getString(3));
                    historico.setNombre(cursor.getString(4));
                    historico.setMotivoRechazo(cursor.getString(5));
                    historico.setFechaMod(cursor.getString(6));
                    historico.setGuid(cursor.getString(7));
                    historico.setFechaSincronizacion(cursor.getString(8));
                    historico.setIdRol(cursor.getInt(9));
                    listHistorico.add(historico);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listHistorico;
    }

    public void deleteHistoricoAnexo(String condition,String[] args){
        SQLiteDatabase db = context.openOrCreateDatabase(Constants.DB_NAME,context.MODE_PRIVATE,null);
        db.delete(TP_TRAN_HISTORIAL_SUBANEXO, condition,args);
        db.close();
    }
}
