package com.elektra.typhoon.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.elektra.typhoon.R;
import com.elektra.typhoon.anexos.AnexosActivity;
import com.elektra.typhoon.checklist.ChecklistBarcos;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.AnexosDBMethods;
import com.elektra.typhoon.database.ChecklistDBMethods;
import com.elektra.typhoon.database.EvidenciasDBMethods;
import com.elektra.typhoon.database.FoliosDBMethods;
import com.elektra.typhoon.database.HistoricoDBMethods;
import com.elektra.typhoon.encryption.Encryption;
import com.elektra.typhoon.json.SincronizacionJSON;
import com.elektra.typhoon.login.MainActivity;
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
public class SincronizacionIndividualRequestService extends AsyncTask<String,String,String> {

    private Activity activity;
    private Context context;
    private int idRevision;
    private ProgressDialog progressDialog;
    private ChecklistBarcos checklistBarcos;
    private AnexosActivity anexosActivity;
    private List<CatalogoBarco> listBarcos;
    private TextView textViewDialog;
    private int totalActualizar;
    private List<Anexo> listAnexos;
    private ApiInterface mApiService;
    private SharedPreferences sharedPreferences;
    private FolioRevision folio;
    private Encryption encryption;

    public SincronizacionIndividualRequestService(Activity activity, Context context, int idRevision){
        this.activity = activity;
        this.context = context;
        this.idRevision = idRevision;
        encryption = new Encryption();
    }

    public SincronizacionIndividualRequestService(ChecklistBarcos activity, Context context, int idRevision){
        this.activity = activity;
        this.context = context;
        this.idRevision = idRevision;
        this.checklistBarcos = activity;
        encryption = new Encryption();
    }

    public SincronizacionIndividualRequestService(Activity activity, Context context, int idRevision,List<CatalogoBarco> listBarcos,List<Anexo> listAnexos,ChecklistBarcos checklistBarcos,AnexosActivity anexosActivity){
        this.activity = activity;
        this.context = context;
        this.idRevision = idRevision;
        this.listBarcos = listBarcos;
        this.listAnexos = listAnexos;
        this.checklistBarcos = checklistBarcos;
        this.anexosActivity = anexosActivity;
        encryption = new Encryption();
    }

    protected void onPreExecute() {
        //progressDialog = Utils.typhoonLoader(activity,"Sincronizando...");
        LayoutInflater li = LayoutInflater.from(context);
        View layoutDialog = li.inflate(R.layout.typhoon_loader_layout, null);

        ImageView imageViewLoader = layoutDialog.findViewById(R.id.imageViewLoader);
        Glide.with(context).load(R.raw.loader3).into(imageViewLoader);

        textViewDialog = layoutDialog.findViewById(R.id.textViewLoader);
        String textoDialog = "Sincronizando";
        if(listBarcos == null && listAnexos != null){
            textoDialog = textoDialog + " anexos...";
        }else if(listBarcos != null && listAnexos == null){
            textoDialog = textoDialog + " evidencias...";
        }else{
            textoDialog = textoDialog + "...";
        }
        textViewDialog.setText(textoDialog);

        progressDialog = new ProgressDialog(context,R.style.ThemeTranslucent);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(layoutDialog);
    }

    @Override
    protected String doInBackground(String... params) {

        int contador = 0;

        /*SincronizacionData sincronizacionData = new SincronizacionData();
        sincronizacionData.setIdRevision(idRevision);//*/

        SincronizacionData sincronizacionData = null;
        mApiService = Utils.getInterfaceService();
        sharedPreferences = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE);
        try {
            FoliosDBMethods foliosDBMethods = new FoliosDBMethods(context);
            folio = foliosDBMethods.readFolio(
                    "SELECT ID_REVISION,NOMBRE,ID_TIPO_REVISION,ID_USUARIO,FECHA_INICIO,FECHA_FIN,ESTATUS FROM " + foliosDBMethods.TP_TRAN_REVISION + " WHERE ID_REVISION = ?",
                    new String[]{String.valueOf(idRevision)});
            if(listBarcos != null) {

                for (CatalogoBarco catalogoBarco : listBarcos) {
                    for (RubroData rubroDataTemp : catalogoBarco.getListRubros()) {
                        for (Pregunta preguntaTemp : rubroDataTemp.getListPreguntasTemp()) {
                            if (preguntaTemp.isSeleccionado()) {
                                totalActualizar++;
                            }
                        }
                    }
                }

                if(totalActualizar != 0) {

                    for (CatalogoBarco catalogoBarco : listBarcos) {
                        for (RubroData rubroDataTemp : catalogoBarco.getListRubros()) {
                            for (Pregunta preguntaTemp : rubroDataTemp.getListPreguntasTemp()) {
                                if (preguntaTemp.isSeleccionado()) {
                                    SincronizacionPost sincronizacionPost = new SincronizacionPost();
                                    sincronizacionData = new SincronizacionJSON().generateRequestDataIndividual(activity, context, idRevision, preguntaTemp.getIdRubro()
                                            , preguntaTemp.getIdPregunta(), 0, preguntaTemp.getIdBarco());
                                    sincronizacionPost.setSincronizacionData(sincronizacionData);
                                    String response = sincronizaDatos(sincronizacionPost, 1);
                                    if (response.equals("Sincronizado correctamente")) {
                                        contador++;
                                        System.out.println("Rubro: " + preguntaTemp.getIdRubro() + " Pregunta: " + preguntaTemp.getIdPregunta() + " Barco: " + preguntaTemp.getIdBarco());
                                    }
                                    updateDialogText("Sincronizado: " + (totalActualizar - (totalActualizar - contador)) + " de: " + totalActualizar);

                            /*Call<SincronizacionResponse> mService = mApiService.sincronizacion(Normalizer.normalize(sharedPreferences.getString(Constants.SP_JWT_TAG, ""), Normalizer.Form.NFD), sincronizacionPost);
                            Response<SincronizacionResponse> response = mService.execute();
                            if (response != null) {
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
                                                        new String[]{String.valueOf(idRevision), String.valueOf(checklistData.getIdChecklist())});
                                                historicoDBMethods.deleteHistoricoAnexo("ID_REVISION = ?", new String[]{String.valueOf(idRevision)});
                                                anexosDBMethods.deleteAnexo("ID_REVISION = ?", new String[]{String.valueOf(idRevision)});
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
                                                    anexosDBMethods.createRelacionRevisionAnexo(anexo.getIdAnexo(), folio.getIdRevision());
                                                    if (anexo.getListSubAnexos() != null) {
                                                        for (Anexo subAnexo : anexo.getListSubAnexos()) {
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
                                                    if (subAnexo.getListHistorico() != null) {
                                                        for (HistoricoAnexo historicoAnexo : subAnexo.getListHistorico()) {
                                                            historicoDBMethods.createHistoricoAnexo(historicoAnexo);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        progressDialog.dismiss();
                                        if (checklistBarcos == null) {
                                            Intent intent = new Intent(activity, ChecklistBarcos.class);
                                            Encryption encryption = new Encryption();
                                            intent.putExtra(Constants.INTENT_FOLIO_TAG, encryption.encryptAES(String.valueOf(folio.getIdRevision())));
                                            intent.putExtra(Constants.INTENT_FECHA_INICIO_TAG, encryption.encryptAES(folio.getFechaInicio()));
                                            intent.putExtra(Constants.INTENT_ESTATUS_TAG, encryption.encryptAES(String.valueOf(folio.getEstatus())));
                                            activity.startActivity(intent);
                                        }
                                        //return "Sincronizado correctamente";
                                        updateDialogText("Sincronizado correctamente");
                                    } else {
                                        //progressDialog.dismiss();
                                        //return response.body().getSincronizacion().getError();
                                        updateDialogText(response.body().getSincronizacion().getError());
                                    }
                                } else {
                                    //progressDialog.dismiss();
                                    if (response.errorBody() != null) {
                                        //return "No se pudo sincronizar: " + response.errorBody().string();
                                        updateDialogText("No se pudo sincronizar: " + response.errorBody().string());
                                    } else {
                                        //return "No se pudo sincronizar";
                                        updateDialogText("No se pudo sincronizar");
                                    }
                                }
                            } else {
                                //progressDialog.dismiss();
                                //return "No se pudo sincronizar";
                                updateDialogText("No se pudo sincronizar");
                            }//*/
                                }
                            }
                        }
                    }
                }else{
                    return "Debe seleccionar al menos una pregunta para sincronizar";
                }

                /*Intent intent = new Intent(activity, ChecklistBarcos.class);
                Encryption encryption = new Encryption();
                intent.putExtra(Constants.INTENT_FOLIO_TAG, encryption.encryptAES(String.valueOf(folio.getIdRevision())));
                intent.putExtra(Constants.INTENT_FECHA_INICIO_TAG, encryption.encryptAES(folio.getFechaInicio()));
                intent.putExtra(Constants.INTENT_ESTATUS_TAG, encryption.encryptAES(String.valueOf(folio.getEstatus())));
                activity.startActivity(intent);//*/
            }

            if(listAnexos != null){

                for(Anexo anexo:listAnexos) {
                    for(Anexo subanexo:anexo.getListSubAnexos()) {
                        if (subanexo.isSeleccionado()) {
                            totalActualizar++;
                        }
                    }
                }

                if(totalActualizar != 0) {

                    for (Anexo anexo : listAnexos) {
                        for (Anexo subanexo : anexo.getListSubAnexos()) {
                            if (subanexo.isSeleccionado()) {
                                SincronizacionPost sincronizacionPost = new SincronizacionPost();
                                sincronizacionData = new SincronizacionJSON().generateRequestDataIndividual(activity, context, idRevision, 0, 0, subanexo.getIdSubAnexo(), 0);
                                sincronizacionPost.setSincronizacionData(sincronizacionData);
                                String response = sincronizaDatos(sincronizacionPost,2);
                                if(response.equals("Sincronizado correctamente")) {
                                    contador++;
                                    System.out.println("Anexo: " + anexo.getIdAnexo() + " Subanexo: " + subanexo.getIdSubAnexo());
                                }
                                updateDialogText("Sincronizado: " + (totalActualizar - (totalActualizar - contador)) + " de: " + totalActualizar);
                            }
                        }
                    }
                }else{
                    return "Debe seleccionar al menos un anexo para sincronizar";
                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return "Error al sincronizar: " + e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error al sincronizar: " + e.getMessage();
        }
        return "Termina la sincronización";
    }

    private String sincronizaDatos(SincronizacionPost sincronizacionPost,int opcion){
        Call<SincronizacionResponse> mService = mApiService.sincronizacion(Utils.getIPAddress(),encryption.decryptAES(Normalizer.normalize(sharedPreferences.getString(Constants.SP_JWT_TAG, ""), Normalizer.Form.NFD)), sincronizacionPost);
        try {
            Response<SincronizacionResponse> response = mService.execute();
            if (response != null) {
                if (response.body() != null) {
                    if (response.body().getSincronizacion().getExito()) {
                        //try {

                        String jwt = encryption.encryptAES(response.headers().get("Authorization"));
                        sharedPreferences.edit().putString(Constants.SP_JWT_TAG, jwt).apply();

                        if (response.body().getSincronizacion().getSincronizacionResponseData().getListChecklist() != null) {
                            ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(context);
                            EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(context);
                            HistoricoDBMethods historicoDBMethods = new HistoricoDBMethods(context);
                            AnexosDBMethods anexosDBMethods = new AnexosDBMethods(context);
                            if(opcion == 1){

                                //Borrado de evidencias y su histórico
                                for(ChecklistData checklistData:sincronizacionPost.getSincronizacionData().getListChecklist()){
                                    for(Rubro rubro:checklistData.getListRubros()){
                                        for(PreguntaData preguntaData:rubro.getListPreguntas()){
                                            for(Evidencia evidencia:preguntaData.getListEvidencias()){
                                                evidenciasDBMethods.deleteEvidencia("ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_EVIDENCIA = ? AND ID_PREGUNTA = ? AND ID_REGISTRO = ?",
                                                        new String[]{String.valueOf(idRevision), String.valueOf(checklistData.getIdChecklist())
                                                                , String.valueOf(evidencia.getIdEvidencia()), String.valueOf(evidencia.getIdPregunta()), String.valueOf(evidencia.getIdRegistro())});
                                                historicoDBMethods.deleteHistorico("ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_EVIDENCIA = ?",
                                                        new String[]{String.valueOf(idRevision), String.valueOf(checklistData.getIdChecklist()),
                                                                String.valueOf(evidencia.getIdEvidencia())});
                                            }
                                        }
                                    }
                                }

                                //Guardado de evidencias y su histórico
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
                                                    if (preguntaData.getListEvidencias() != null) {
                                                        if(preguntaData.getListEvidencias().size() != 0) {
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
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                RespuestaData respuestaDataTemp = null;
                                for(RespuestaData respuestaData:sincronizacionPost.getSincronizacionData().getListRespuestas()){
                                    respuestaDataTemp = respuestaData;
                                }

                                if (response.body().getSincronizacion().getSincronizacionResponseData().getListRespuestas() != null) {
                                    for (RespuestaData respuestaData : response.body().getSincronizacion().getSincronizacionResponseData().getListRespuestas()) {
                                        if(respuestaDataTemp != null){
                                            if(respuestaData.getIdPregunta() == respuestaDataTemp.getIdPregunta() &&
                                                    respuestaData.getIdBarco() == respuestaDataTemp.getIdBarco() &&
                                                    respuestaData.getIdRevision() == respuestaDataTemp.getIdRevision() &&
                                                    respuestaData.getIdChecklist() == respuestaDataTemp.getIdChecklist() &&
                                                    respuestaData.getIdRubro() == respuestaDataTemp.getIdRubro()){
                                                respuestaData.setSincronizado(1);
                                                checklistDBMethods.createRespuesta(respuestaData);
                                            }
                                        }else{
                                            checklistDBMethods.createRespuesta(respuestaData);
                                        }//*/
                                    }
                                }
                            }else{

                                //Borrado de anexos
                                for(SubAnexo subAnexo:sincronizacionPost.getSincronizacionData().getListSubAnexos()){
                                    anexosDBMethods.deleteAnexo("ID_REVISION = ? AND ID_SUBANEXO = ?", new String[]{String.valueOf(idRevision),String.valueOf(subAnexo.getIdSubAnexo())});
                                    historicoDBMethods.deleteHistoricoAnexo("ID_REVISION = ? AND ID_SUBANEXO = ?", new String[]{String.valueOf(idRevision),String.valueOf(subAnexo.getIdSubAnexo())});
                                }

                                if (response.body().getSincronizacion().getSincronizacionResponseData().getListAnexos() != null) {
                                    for (Anexo anexo : response.body().getSincronizacion().getSincronizacionResponseData().getListAnexos()) {
                                        anexosDBMethods.createCatalogoAnexo(anexo);
                                        anexosDBMethods.createRelacionRevisionAnexo(anexo.getIdAnexo(), folio.getIdRevision());
                                        if (anexo.getListSubAnexos() != null) {
                                            for (Anexo subAnexo : anexo.getListSubAnexos()) {
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
                                        if (subAnexo.getListHistorico() != null) {
                                            for (HistoricoAnexo historicoAnexo : subAnexo.getListHistorico()) {
                                                historicoDBMethods.createHistoricoAnexo(historicoAnexo);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //progressDialog.dismiss();
                        /*if (checklistBarcos == null) {
                            Intent intent = new Intent(activity, ChecklistBarcos.class);
                            Encryption encryption = new Encryption();
                            intent.putExtra(Constants.INTENT_FOLIO_TAG, encryption.encryptAES(String.valueOf(folio.getIdRevision())));
                            intent.putExtra(Constants.INTENT_FECHA_INICIO_TAG, encryption.encryptAES(folio.getFechaInicio()));
                            intent.putExtra(Constants.INTENT_ESTATUS_TAG, encryption.encryptAES(String.valueOf(folio.getEstatus())));
                            activity.startActivity(intent);
                        }//*/
                        updateDialogText("Sincronizado correctamente");
                        return "Sincronizado correctamente";
                    } else {
                        //progressDialog.dismiss();
                        updateDialogText(response.body().getSincronizacion().getError());
                        return response.body().getSincronizacion().getError();
                    }
                } else {
                    //progressDialog.dismiss();
                    if (response.errorBody() != null) {
                        String mensaje = "" + response.errorBody().string();
                        int code = response.code();
                        //if(!mensaje.contains("No tiene permiso para ver")) {
                        if(code != 401) {
                            //updateDialogText("No se pudo sincronizar: " + response.errorBody().string());
                            return "No se pudo sincronizar: " + response.errorBody().string();
                        }else{
                            sharedPreferences.edit().putBoolean(Constants.SP_LOGIN_TAG, false).apply();
                            //Utils.message(activity, "La sesión ha expirado");
                            Intent intent = new Intent(activity,MainActivity.class);
                            activity.startActivity(intent);
                            //activity.finish();
                            return "La sesión ha expirado";
                        }
                        //updateDialogText("No se pudo sincronizar: " + response.errorBody().string());
                        //return "No se pudo sincronizar: " + response.errorBody().string();
                    } else {
                        updateDialogText("No se pudo sincronizar");
                        return "No se pudo sincronizar";
                    }
                }
            } else {
                //progressDialog.dismiss();
                updateDialogText("No se pudo sincronizar");
                return "No se pudo sincronizar";
            }
        } catch (IOException e) {
            //e.printStackTrace();
            updateDialogText("Error al sincronizar: " + e.getMessage());
            return "Error al sincronizar: " + e.getMessage();
        }
    }

    private void updateDialogText(final String texto){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewDialog.setText(texto);
            }
        });
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
        Utils.message(activity, result);
        if(checklistBarcos != null){
            checklistBarcos.loadBarco();
        }
        if(anexosActivity != null){
            anexosActivity.loadData();
        }
        progressDialog.dismiss();
    }
}
