package com.elektra.typhoon.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.elektra.typhoon.database.ChecklistDBMethods;
import com.elektra.typhoon.database.EvidenciasDBMethods;
import com.elektra.typhoon.json.SincronizacionJSON;
import com.elektra.typhoon.objetos.request.SincronizacionData;
import com.elektra.typhoon.objetos.request.SincronizacionPost;
import com.elektra.typhoon.objetos.response.ChecklistData;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.PreguntaData;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.objetos.response.Rubro;
import com.elektra.typhoon.objetos.response.SincronizacionResponse;
import com.elektra.typhoon.utils.Utils;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Proyecto:
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

    public SincronizacionRequestService(Activity activity,Context context,int idRevision){
        this.activity = activity;
        this.context = context;
        this.idRevision = idRevision;
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
            sincronizacionData = new SincronizacionJSON().generateRequestData(context,idRevision);
            SincronizacionPost sincronizacionPost = new SincronizacionPost();
            sincronizacionPost.setSincronizacionData(sincronizacionData);

            Call<SincronizacionResponse> mService = mApiService.sincronizacion(sincronizacionPost);
            Response<SincronizacionResponse> response = mService.execute();
            if(response.body() != null) {
                if(response.body().getSincronizacion().getExito()){
                    try {
                        if (response.body().getSincronizacion().getSincronizacionResponseData().getListChecklist() != null) {
                            ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(context);
                            EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(context);
                            for (ChecklistData checklistData : response.body().getSincronizacion().getSincronizacionResponseData().getListChecklist()) {
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
                                                if(preguntaData.getListEvidencias() != null){
                                                    for(Evidencia evidencia:preguntaData.getListEvidencias()){
                                                        evidencia.setIdRevision(preguntaData.getIdRevision());
                                                        evidencia.setIdChecklist(preguntaData.getIdChecklist());
                                                        evidencia.setIdRubro(preguntaData.getIdRubro());
                                                        evidencia.setIdPregunta(preguntaData.getIdPregunta());
                                                        evidencia.setIdBarco(getIdBarco(response.body().getSincronizacion().
                                                                getSincronizacionResponseData().getListRespuestas(),evidencia.getIdRegistro()));
                                                        evidenciasDBMethods.createEvidencia(evidencia);
                                                        System.out.println();
                                                    }
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
                        return "Sincronizado correctamente";
                    }catch (Exception e){
                        progressDialog.dismiss();
                        return "Error al guardar datos: " + e.getMessage();
                    }
                }else{
                    progressDialog.dismiss();
                    return response.body().getSincronizacion().getError();
                }//*/
            }else{
                progressDialog.dismiss();
                return "Error al sincronizar";
            }
        } catch (IOException e) {
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
        progressDialog.dismiss();
    }
}