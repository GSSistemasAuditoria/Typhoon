package com.elektra.typhoon.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.elektra.typhoon.adapters.AdapterExpandableChecklist;
import com.elektra.typhoon.checklist.ChecklistBarcos;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.ChecklistDBMethods;
import com.elektra.typhoon.database.EvidenciasDBMethods;
import com.elektra.typhoon.database.FoliosDBMethods;
import com.elektra.typhoon.database.HistoricoDBMethods;
import com.elektra.typhoon.gps.GPSTracker;
import com.elektra.typhoon.json.SincronizacionJSON;
import com.elektra.typhoon.objetos.Folio;
import com.elektra.typhoon.objetos.request.SincronizacionData;
import com.elektra.typhoon.objetos.request.SincronizacionPost;
import com.elektra.typhoon.objetos.response.CatalogoBarco;
import com.elektra.typhoon.objetos.response.ChecklistData;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.FolioRevision;
import com.elektra.typhoon.objetos.response.Historico;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.PreguntaData;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.objetos.response.Rubro;
import com.elektra.typhoon.objetos.response.RubroData;
import com.elektra.typhoon.objetos.response.SincronizacionResponse;
import com.elektra.typhoon.utils.Utils;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 07/02/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class SincronizacionRequestService extends AsyncTask<String,String,String> {

    private Activity activity;
    private Context context;
    private int idRevision;
    private ProgressDialog progressDialog;
    private ChecklistBarcos checklistBarcos;

    public SincronizacionRequestService(Activity activity,Context context,int idRevision){
        this.activity = activity;
        this.context = context;
        this.idRevision = idRevision;
    }

    public SincronizacionRequestService(ChecklistBarcos activity,Context context,int idRevision){
        this.activity = activity;
        this.context = context;
        this.idRevision = idRevision;
        this.checklistBarcos = activity;
    }

    protected void onPreExecute() {
        progressDialog = Utils.typhoonLoader(activity,"Sincronizando...");
    }

    @Override
    protected String doInBackground(String... params) {
        ApiInterface mApiService = Utils.getInterfaceService();

        /*SincronizacionData sincronizacionData = new SincronizacionData();
        sincronizacionData.setIdRevision(idRevision);//*/

        SincronizacionData sincronizacionData = null;
        try {
            FolioRevision folio = new FoliosDBMethods(context).readFolio("WHERE ID_REVISION = ?",new String[]{String.valueOf(idRevision)});
            sincronizacionData = new SincronizacionJSON().generateRequestData(activity,context,idRevision);
            SincronizacionPost sincronizacionPost = new SincronizacionPost();
            sincronizacionPost.setSincronizacionData(sincronizacionData);

            Call<SincronizacionResponse> mService = mApiService.sincronizacion(sincronizacionPost);
            Response<SincronizacionResponse> response = mService.execute();
            if(response != null) {
                if (response.body() != null) {
                    if (response.body().getSincronizacion().getExito()) {
                        try {
                            if (response.body().getSincronizacion().getSincronizacionResponseData().getListChecklist() != null) {
                                ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(context);
                                EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(context);
                                HistoricoDBMethods historicoDBMethods = new HistoricoDBMethods(context);
                                for (ChecklistData checklistData : response.body().getSincronizacion().getSincronizacionResponseData().getListChecklist()) {
                                    evidenciasDBMethods.deleteEvidencia("ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_ESTATUS != 1 AND ID_ETAPA != 1",
                                            new String[]{String.valueOf(idRevision), String.valueOf(checklistData.getIdChecklist())});
                                    historicoDBMethods.deleteHistorico(null, null);//TODO: cambiar para borrar por folio y checklist
                                    checklistData.setIdRevision(idRevision);
                                    checklistDBMethods.createChecklist(checklistData);
                                    if (checklistData.getListRubros() != null) {
                                        for (Rubro rubroData : checklistData.getListRubros()) {
                                            rubroData.setIdRevision(idRevision);
                                            rubroData.setIdChecklist(checklistData.getIdChecklist());
                                            checklistDBMethods.createRubro(rubroData);
                                            if (rubroData.getListPreguntas() != null) {
                                                for (PreguntaData preguntaData : rubroData.getListPreguntas()) {
                                                    preguntaData.setIdRevision(idRevision);
                                                    preguntaData.setIdChecklist(checklistData.getIdChecklist());
                                                    checklistDBMethods.createPregunta(preguntaData);
                                                    if (preguntaData.getListEvidencias() != null) {
                                                        for (Evidencia evidencia : preguntaData.getListEvidencias()) {
                                                            evidencia.setIdRevision(preguntaData.getIdRevision());
                                                            evidencia.setIdChecklist(preguntaData.getIdChecklist());
                                                            evidencia.setIdRubro(preguntaData.getIdRubro());
                                                            evidencia.setIdPregunta(preguntaData.getIdPregunta());
                                                            evidencia.setIdBarco(getIdBarco(response.body().getSincronizacion().
                                                                    getSincronizacionResponseData().getListRespuestas(), evidencia.getIdRegistro()));
                                                            evidenciasDBMethods.createEvidencia(evidencia);
                                                            if (evidencia.getListHistorico() != null) {
                                                                for (Historico historico : evidencia.getListHistorico()) {
                                                                    historicoDBMethods.createHistorico(historico);
                                                                }
                                                            }
                                                        }
                                                    } else {

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (response.body().getSincronizacion().getSincronizacionResponseData().getListRespuestas() != null) {
                                    for (RespuestaData respuestaData : response.body().getSincronizacion().getSincronizacionResponseData().getListRespuestas()) {
                                        checklistDBMethods.createRespuesta(respuestaData);
                                    }
                                }
                            }
                            progressDialog.dismiss();
                            if(checklistBarcos == null) {
                                Intent intent = new Intent(activity, ChecklistBarcos.class);
                                intent.putExtra(Constants.INTENT_FOLIO_TAG, folio.getIdRevision());
                                intent.putExtra(Constants.INTENT_FECHA_INICIO_TAG, folio.getFechaInicio());
                                intent.putExtra(Constants.INTENT_FECHA_FIN_TAG, folio.getFechaFin());
                                intent.putExtra(Constants.INTENT_ESTATUS_TAG, folio.getEstatus());
                                activity.startActivity(intent);
                            }
                            return "Sincronizado correctamente";
                        } catch (Exception e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            return "Error al guardar datos: " + e.getMessage();
                        }
                    } else {
                        progressDialog.dismiss();
                        return response.body().getSincronizacion().getError();
                    }//*/
                } else {
                    progressDialog.dismiss();
                    return "No se pudo sincronizar";
                }
            }else{
                progressDialog.dismiss();
                return "No se pudo sincronizar";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al sincronizar: " + e.getMessage();
        }
        //return "OK";
    }

    private int getIdBarco(List<RespuestaData> lista,int idRegistro){
        for(RespuestaData respuestaData:lista){
            if(respuestaData.getIdRegistro() == idRegistro){
                return respuestaData.getIdBarco();
            }
        }
        return 0;
    }

    @Override
    protected void onPostExecute(String result) {
        //ocultar dialogo
        Utils.message(activity,result);
        if(checklistBarcos != null){
            checklistBarcos.reloadData();
        }
        progressDialog.dismiss();
    }
}
