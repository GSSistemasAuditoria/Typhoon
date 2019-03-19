package com.elektra.typhoon.json;

import android.app.Activity;
import android.content.Context;

import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.ChecklistDBMethods;
import com.elektra.typhoon.database.EvidenciasDBMethods;
import com.elektra.typhoon.database.FoliosDBMethods;
import com.elektra.typhoon.database.HistoricoDBMethods;
import com.elektra.typhoon.database.UsuarioDBMethods;
import com.elektra.typhoon.gps.GPSTracker;
import com.elektra.typhoon.objetos.Folio;
import com.elektra.typhoon.objetos.request.EvidenciaData;
import com.elektra.typhoon.objetos.request.SincronizacionData;
import com.elektra.typhoon.objetos.response.CatalogoBarco;
import com.elektra.typhoon.objetos.response.ChecklistData;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.FolioRevision;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.PreguntaData;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.objetos.response.Rubro;
import com.elektra.typhoon.objetos.response.RubroData;
import com.elektra.typhoon.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 06/02/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class SincronizacionJSON {

    public SincronizacionData generateRequestData(Activity activity, Context context, int folio) throws IOException {
        SincronizacionData sincronizacionData = new SincronizacionData();
        ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(context);
        EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(context);
        UsuarioDBMethods usuarioDBMethods = new UsuarioDBMethods(context);
        ResponseLogin.Usuario usuario = usuarioDBMethods.readUsuario();
        FoliosDBMethods foliosDBMethods = new FoliosDBMethods(context);
        FolioRevision folioRevision = foliosDBMethods.readFolio(
                "SELECT ID_REVISION,NOMBRE,ID_TIPO_REVISION,ID_USUARIO,FECHA_INICIO,FECHA_FIN,ESTATUS FROM " + foliosDBMethods.TP_TRAN_REVISION + " WHERE ID_REVISION = ?",
                new String[]{String.valueOf(folio)});
        List<ChecklistData> listChecklist = checklistDBMethods.readChecklists(
                "SELECT ID_REVISION,ID_CHECKLIST,ID_ESTATUS,ID_LOGO,ID_TIPO_REVISION,NOMBRE,PONDERACION FROM " + checklistDBMethods.TP_CAT_CHEKLIST + " WHERE ID_REVISION = ?",
                new String[]{String.valueOf(folio)});

        if (listChecklist.size() != 0) {
            ChecklistData checklistData = listChecklist.get(0);
            List<RubroData> listRubros = checklistDBMethods.readRubro(
                    "SELECT ID_REVISION,ID_CHECKLIST,ID_RUBRO,ESTATUS,NOMBRE FROM " + checklistDBMethods.TP_CAT_CL_RUBRO + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ?",
                    new String[]{String.valueOf(checklistData.getIdRevision()), String.valueOf(checklistData.getIdChecklist())});
            List<Rubro> listRubroPost = new ArrayList<>();
            for(RubroData rubroData:listRubros){
                Rubro rubro = new Rubro();
                rubro.setEstatus(rubroData.getEstatus());
                rubro.setIdRubro(rubroData.getIdRubro());
                rubro.setNombre(rubroData.getNombre());
                listRubroPost.add(rubro);
            }
            checklistData.setListRubros(listRubroPost);
            for(Rubro rubro:listRubroPost){
                List<Pregunta> listPreguntas = checklistDBMethods.readPregunta(
                        "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_TIPO_RESPUESTA,ID_RUBRO,ESTATUS,DESCRIPCION,IS_TIERRA FROM " +
                                checklistDBMethods.TP_CAT_CL_PREGUNTA + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ?",
                        new String[]{String.valueOf(checklistData.getIdRevision()), String.valueOf(checklistData.getIdChecklist()),
                                String.valueOf(rubro.getIdRubro())});
                List<PreguntaData> listPreguntasPost = new ArrayList<>();
                for(Pregunta pregunta:listPreguntas){
                    PreguntaData preguntaData = new PreguntaData();
                    preguntaData.setDescripcion(pregunta.getDescripcion());
                    preguntaData.setEstatus(pregunta.getEstatus());
                    preguntaData.setIdPregunta(pregunta.getIdPregunta());
                    preguntaData.setIdRubro(pregunta.getIdRubro());
                    preguntaData.setIdTipoRespuesta(pregunta.getIdTipoRespuesta());
                    //String idRol = "2";
                    List<Evidencia> listEvidencias = null;
                    if(usuario.getIdrol() == 1){
                        //idRol = "2";
                        listEvidencias = evidenciasDBMethods.readEvidencias("" +
                                        "SELECT ID_EVIDENCIA,NOMBRE,CONTENIDO_PREVIEW,ID_ESTATUS,ID_ETAPA,ID_REVISION,ID_CHECKLIST," +
                                        "ID_RUBRO,ID_PREGUNTA,ID_REGISTRO,ID_BARCO,CONTENIDO,LATITUDE,LONGITUDE,AGREGADO_COORDINADOR FROM " + evidenciasDBMethods.TP_TRAN_CL_EVIDENCIA +
                                        " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND (ID_ETAPA = 2) OR (ID_ETAPA = 1 AND ID_ESTATUS = 2)",
                                new String[]{String.valueOf(pregunta.getIdRevision()), String.valueOf(pregunta.getIdChecklist()),
                                        String.valueOf(pregunta.getIdRubro()), String.valueOf(pregunta.getIdPregunta())},true);
                    //}if(usuario.getIdrol() == 2){
                    }else {
                        String idRol = String.valueOf(usuario.getIdrol() + 1);
                        listEvidencias = evidenciasDBMethods.readEvidencias("" +
                                        "SELECT ID_EVIDENCIA,NOMBRE,CONTENIDO_PREVIEW,ID_ESTATUS,ID_ETAPA,ID_REVISION,ID_CHECKLIST," +
                                        "ID_RUBRO,ID_PREGUNTA,ID_REGISTRO,ID_BARCO,CONTENIDO,LATITUDE,LONGITUDE,AGREGADO_COORDINADOR FROM " + evidenciasDBMethods.TP_TRAN_CL_EVIDENCIA +
                                        " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND (ID_ETAPA = 1 OR ID_ETAPA = ?)",
                                new String[]{String.valueOf(pregunta.getIdRevision()), String.valueOf(pregunta.getIdChecklist()),
                                        String.valueOf(pregunta.getIdRubro()), String.valueOf(pregunta.getIdPregunta()),idRol},true);
                    }
                    /*listEvidencias = evidenciasDBMethods.readEvidencias("" +
                                    "WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_ETAPA = ?" +
                                    " AND ID_ESTATUS != 2",
                            new String[]{String.valueOf(pregunta.getIdRevision()), String.valueOf(pregunta.getIdChecklist()),
                                    String.valueOf(pregunta.getIdRubro()), String.valueOf(pregunta.getIdPregunta()),idRol},true);//*/

                    HistoricoDBMethods historicoDBMethods = new HistoricoDBMethods(activity);
                    for(Evidencia evidencia:listEvidencias){
                        evidencia.setListHistorico(historicoDBMethods.readHistorico(
                                "SELECT ID_EVIDENCIA,ID_ETAPA,ID_USUARIO,MOTIVO,CONSEC,ID_REVISION,ID_CHECKLIST,FECHA_MOD FROM " + historicoDBMethods.TP_TRAN_HISTORIAL_EVIDENCIA + " WHERE ID_EVIDENCIA = ?",
                                new String[]{evidencia.getIdEvidencia()}));
                    }

                    preguntaData.setListEvidencias(listEvidencias);
                    listPreguntasPost.add(preguntaData);
                }
                rubro.setListPreguntas(listPreguntasPost);
            }

            //List<RespuestaData> listRespuestas = checklistDBMethods.readRespuesta("WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RESPUESTA != 0"
            List<RespuestaData> listRespuestas = checklistDBMethods.readRespuesta(
                    "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_RUBRO,ID_ESTATUS,ID_BARCO,ID_REGISTRO,ID_RESPUESTA FROM " + checklistDBMethods.TP_TRAN_CL_RESPUESTA + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ?"
                , new String[]{String.valueOf(checklistData.getIdRevision()), String.valueOf(checklistData.getIdChecklist())});

            sincronizacionData.setIdRevision(folio);
            sincronizacionData.setEstatus(folioRevision.getEstatus());
            sincronizacionData.setEsMovil(true);
            List<ChecklistData> listChecklists = new ArrayList<>();
            listChecklists.add(checklistData);
            sincronizacionData.setListChecklist(listChecklists);
            sincronizacionData.setListRespuestas(listRespuestas);
            sincronizacionData.setRevisionFechaMod(Utils.getDate(Constants.DATE_FORMAT_FULL));
        }else{
            sincronizacionData.setIdRevision(folio);
        }
        return sincronizacionData;
    }
}
