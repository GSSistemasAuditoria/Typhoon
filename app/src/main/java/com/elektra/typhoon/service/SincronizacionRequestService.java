package com.elektra.typhoon.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.elektra.typhoon.adapters.AdapterExpandableChecklist;
import com.elektra.typhoon.checklist.ChecklistBarcos;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.AnexosDBMethods;
import com.elektra.typhoon.database.ChecklistDBMethods;
import com.elektra.typhoon.database.EvidenciasDBMethods;
import com.elektra.typhoon.database.FoliosDBMethods;
import com.elektra.typhoon.database.HistoricoDBMethods;
import com.elektra.typhoon.encryption.Encryption;
import com.elektra.typhoon.gps.GPSTracker;
import com.elektra.typhoon.json.SincronizacionJSON;
import com.elektra.typhoon.objetos.Folio;
import com.elektra.typhoon.objetos.request.SincronizacionData;
import com.elektra.typhoon.objetos.request.SincronizacionPost;
import com.elektra.typhoon.objetos.request.SubAnexo;
import com.elektra.typhoon.objetos.response.Anexo;
import com.elektra.typhoon.objetos.response.CatalogoBarco;
import com.elektra.typhoon.objetos.response.ChecklistData;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.FolioRevision;
import com.elektra.typhoon.objetos.response.Historico;
import com.elektra.typhoon.objetos.response.HistoricoAnexo;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.PreguntaData;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.objetos.response.Rubro;
import com.elektra.typhoon.objetos.response.RubroData;
import com.elektra.typhoon.objetos.response.SincronizacionResponse;
import com.elektra.typhoon.utils.Utils;

import java.io.IOException;
import java.text.Normalizer;
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
            FoliosDBMethods foliosDBMethods = new FoliosDBMethods(context);
            FolioRevision folio = foliosDBMethods.readFolio(
                    "SELECT ID_REVISION,NOMBRE,ID_TIPO_REVISION,ID_USUARIO,FECHA_INICIO,FECHA_FIN,ESTATUS FROM " + foliosDBMethods.TP_TRAN_REVISION + " WHERE ID_REVISION = ?",
                    new String[]{String.valueOf(idRevision)});
            sincronizacionData = new SincronizacionJSON().generateRequestData(activity,context,idRevision);
            SincronizacionPost sincronizacionPost = new SincronizacionPost();
            sincronizacionPost.setSincronizacionData(sincronizacionData);

            SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE);
            Call<SincronizacionResponse> mService = mApiService.sincronizacion(Normalizer.normalize(sharedPreferences.getString(Constants.SP_JWT_TAG,""), Normalizer.Form.NFD),sincronizacionPost);
            Response<SincronizacionResponse> response = mService.execute();
            if(response != null) {
                if (response.body() != null) {
                    if (response.body().getSincronizacion().getExito()) {
                        //try {
                            if (response.body().getSincronizacion().getSincronizacionResponseData().getListChecklist() != null) {
                                ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(context);
                                EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(context);
                                HistoricoDBMethods historicoDBMethods = new HistoricoDBMethods(context);
                                AnexosDBMethods anexosDBMethods = new AnexosDBMethods(context);
                                for (ChecklistData checklistData : response.body().getSincronizacion().getSincronizacionResponseData().getListChecklist()) {
                                    evidenciasDBMethods.deleteEvidencia("ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_ESTATUS != 1 AND ID_ETAPA != 1",
                                            new String[]{String.valueOf(idRevision), String.valueOf(checklistData.getIdChecklist())});
                                    historicoDBMethods.deleteHistorico("ID_REVISION = ? AND ID_CHECKLIST = ?",
                                            new String[]{String.valueOf(idRevision),String.valueOf(checklistData.getIdChecklist())});
                                    historicoDBMethods.deleteHistoricoAnexo("ID_REVISION = ?",new String[]{String.valueOf(idRevision)});
                                    anexosDBMethods.deleteAnexo("ID_REVISION = ?",new String[]{String.valueOf(idRevision)});
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
                                if (response.body().getSincronizacion().getSincronizacionResponseData().getListAnexos() != null) {
                                    for (Anexo anexo : response.body().getSincronizacion().getSincronizacionResponseData().getListAnexos()) {
                                        anexosDBMethods.createCatalogoAnexo(anexo);
                                        anexosDBMethods.createRelacionRevisionAnexo(anexo.getIdAnexo(),folio.getIdRevision());
                                        if(anexo.getListSubAnexos() != null){
                                            for(Anexo subAnexo:anexo.getListSubAnexos()){
                                                anexosDBMethods.createCatalogoAnexo(subAnexo);
                                            }
                                        }
                                    }
                                }
                                if (response.body().getSincronizacion().getSincronizacionResponseData().getListSubAnexos() != null) {
                                    for (SubAnexo subAnexo : response.body().getSincronizacion().getSincronizacionResponseData().getListSubAnexos()) {
                                        Anexo anexo = new Anexo();
                                        anexo.setIdRevision(subAnexo.getIdRevision());
                                        anexo.setIdSubAnexo(subAnexo.getIdSubAnexo());
                                        anexo.setNombreArchivo(subAnexo.getFileName());
                                        anexo.setBase64(subAnexo.getContenido());
                                        anexo.setIdEtapa(subAnexo.getIdEtapa());
                                        anexo.setFechaSinc(subAnexo.getFechaSincronizacion());
                                        new AnexosDBMethods(activity).createAnexo(anexo);
                                        if(subAnexo.getListHistorico() != null){
                                            for(HistoricoAnexo historicoAnexo:subAnexo.getListHistorico()){
                                                historicoDBMethods.createHistoricoAnexo(historicoAnexo);
                                            }
                                        }
                                    }
                                }
                            }
                            progressDialog.dismiss();
                            if(checklistBarcos == null) {
                                Intent intent = new Intent(activity, ChecklistBarcos.class);
                                /*intent.putExtra(Constants.INTENT_FOLIO_TAG, folio.getIdRevision());
                                intent.putExtra(Constants.INTENT_FECHA_INICIO_TAG, folio.getFechaInicio());
                                intent.putExtra(Constants.INTENT_FECHA_FIN_TAG, folio.getFechaFin());
                                intent.putExtra(Constants.INTENT_ESTATUS_TAG, folio.getEstatus());//*/

                                Encryption encryption = new Encryption();

                                intent.putExtra(Constants.INTENT_FOLIO_TAG,encryption.encryptAES(String.valueOf(folio.getIdRevision())));
                                intent.putExtra(Constants.INTENT_FECHA_INICIO_TAG,encryption.encryptAES(folio.getFechaInicio()));
                                /*if(folio.getFechaFin() != null){
                                    intent.putExtra(Constants.INTENT_FECHA_FIN_TAG,encryption.encryptAES(folio.getFechaFin()));
                                }//*/
                                intent.putExtra(Constants.INTENT_ESTATUS_TAG,encryption.encryptAES(String.valueOf(folio.getEstatus())));

                                activity.startActivity(intent);
                            }
                            return "Sincronizado correctamente";
                        /*} catch (NullPointerException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            return "Error al guardar datos: " + e.getMessage();
                        }//*/
                    } else {
                        progressDialog.dismiss();
                        return response.body().getSincronizacion().getError();
                    }//*/
                } else {
                    progressDialog.dismiss();
                    if(response.errorBody() != null){
                        return "No se pudo sincronizar: " + response.errorBody().string();
                    }else {
                        return "No se pudo sincronizar";
                    }
                }
            }else{
                progressDialog.dismiss();
                return "No se pudo sincronizar";
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return "Error al sincronizar: " + e.getMessage();
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
        if(checklistBarcos != null){
            checklistBarcos.reloadData();
        }
        progressDialog.dismiss();
    }
}
