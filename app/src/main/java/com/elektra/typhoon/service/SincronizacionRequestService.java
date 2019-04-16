package com.elektra.typhoon.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.elektra.typhoon.R;
import com.elektra.typhoon.adapters.AdapterExpandableChecklist;
import com.elektra.typhoon.checklist.ChecklistBarcos;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.AnexosDBMethods;
import com.elektra.typhoon.database.BarcoDBMethods;
import com.elektra.typhoon.database.ChecklistDBMethods;
import com.elektra.typhoon.database.EvidenciasDBMethods;
import com.elektra.typhoon.database.FoliosDBMethods;
import com.elektra.typhoon.database.HistoricoDBMethods;
import com.elektra.typhoon.database.UsuarioDBMethods;
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
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.objetos.response.Rubro;
import com.elektra.typhoon.objetos.response.RubroData;
import com.elektra.typhoon.objetos.response.SincronizacionResponse;
import com.elektra.typhoon.utils.Utils;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
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
    private ApiInterface mApiService;
    private SharedPreferences sharedPreferences;
    private FolioRevision folio;
    private List<Anexo> listAnexos;
    private List<CatalogoBarco> listBarcos;
    private TextView textViewDialog;

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
        //progressDialog = Utils.typhoonLoader(activity,"Sincronizando...");
        LayoutInflater li = LayoutInflater.from(context);
        View layoutDialog = li.inflate(R.layout.typhoon_loader_layout, null);

        ImageView imageViewLoader = layoutDialog.findViewById(R.id.imageViewLoader);
        Glide.with(context).load(R.raw.loader3).into(imageViewLoader);

        textViewDialog = layoutDialog.findViewById(R.id.textViewLoader);
        String textoDialog = "Sincronizando...";
        /*if(listBarcos == null && listAnexos != null){
            textoDialog = textoDialog + " anexos...";
        }else if(listBarcos != null && listAnexos == null){
            textoDialog = textoDialog + " evidencias...";
        }else{
            textoDialog = textoDialog + "...";
        }//*/
        textViewDialog.setText(textoDialog);

        progressDialog = new ProgressDialog(context,R.style.ThemeTranslucent);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(layoutDialog);
    }

    @Override
    protected String doInBackground(String... params) {
        mApiService = Utils.getInterfaceService();
        sharedPreferences = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE);

        /*SincronizacionData sincronizacionData = new SincronizacionData();
        sincronizacionData.setIdRevision(idRevision);//*/

        ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(activity);
        List<ChecklistData> listChecklist = checklistDBMethods.readChecklists(
                "SELECT ID_REVISION,ID_CHECKLIST,ID_ESTATUS,ID_LOGO,ID_TIPO_REVISION,NOMBRE,PONDERACION FROM " + checklistDBMethods.TP_CAT_CHEKLIST + " WHERE ID_REVISION = ?",
                new String[]{String.valueOf(idRevision)});

        if (listChecklist.size() == 0) {
            SincronizacionData sincronizacionData = null;
            try {
                FoliosDBMethods foliosDBMethods = new FoliosDBMethods(context);
                folio = foliosDBMethods.readFolio(
                        "SELECT ID_REVISION,NOMBRE,ID_TIPO_REVISION,ID_USUARIO,FECHA_INICIO,FECHA_FIN,ESTATUS FROM " + foliosDBMethods.TP_TRAN_REVISION + " WHERE ID_REVISION = ?",
                        new String[]{String.valueOf(idRevision)});
                sincronizacionData = new SincronizacionJSON().generateRequestData(activity, context, idRevision);
                SincronizacionPost sincronizacionPost = new SincronizacionPost();
                sincronizacionPost.setSincronizacionData(sincronizacionData);

                Call<SincronizacionResponse> mService = mApiService.sincronizacion(Normalizer.normalize(sharedPreferences.getString(Constants.SP_JWT_TAG, ""), Normalizer.Form.NFD), sincronizacionPost);
                Response<SincronizacionResponse> response = mService.execute();
                if (response != null) {
                    if (response.body() != null) {
                        if (response.body().getSincronizacion().getExito()) {
                            //try {
                            if (response.body().getSincronizacion().getSincronizacionResponseData().getListChecklist() != null) {
                                //ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(context);
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

                            updateDialogText("Checklist descargado");
                            loadDataSincronizacion();
                            String result = executeSincronizacionCompleta();
                            //progressDialog.dismiss();

                            /*if (checklistBarcos == null) {descomentar
                                Intent intent = new Intent(activity, ChecklistBarcos.class);

                                Encryption encryption = new Encryption();

                                intent.putExtra(Constants.INTENT_FOLIO_TAG, encryption.encryptAES(String.valueOf(folio.getIdRevision())));
                                intent.putExtra(Constants.INTENT_FECHA_INICIO_TAG, encryption.encryptAES(folio.getFechaInicio()));
                                intent.putExtra(Constants.INTENT_ESTATUS_TAG, encryption.encryptAES(String.valueOf(folio.getEstatus())));

                                activity.startActivity(intent);
                            }//*/
                            return result;
                            //return "Checklist descargado";
                        /*} catch (NullPointerException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            return "Error al guardar datos: " + e.getMessage();
                        }//*/
                        } else {
                            //progressDialog.dismiss();
                            return response.body().getSincronizacion().getError();
                        }//*/
                    } else {
                        //progressDialog.dismiss();
                        if (response.errorBody() != null) {
                            return "No se pudo sincronizar: " + response.errorBody().string();
                        } else {
                            return "No se pudo sincronizar";
                        }
                    }
                } else {
                    //progressDialog.dismiss();
                    return "No se pudo sincronizar";
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return "Error al sincronizar: " + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error al sincronizar: " + e.getMessage();
            }
        }else{
            loadDataSincronizacion();
            return executeSincronizacionCompleta();
        }
        //return "OK";
    }

    private String executeSincronizacionCompleta(){
        SincronizacionData sincronizacionData = null;
        try {
            int totalActualizar = 0;
            int contador = 0;
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
                    updateDialogText("Sincronizando evidencias...");
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
                totalActualizar = 0;
                contador = 0;
                for(Anexo anexo:listAnexos) {
                    for(Anexo subanexo:anexo.getListSubAnexos()) {
                        if (subanexo.isSeleccionado()) {
                            totalActualizar++;
                        }
                    }
                }

                if(totalActualizar != 0) {
                    updateDialogText("Sincronizando anexos...");
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
        return "Sincronizado correctamente";
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
        }else{
            Intent intent = new Intent(activity, ChecklistBarcos.class);

            Encryption encryption = new Encryption();

            intent.putExtra(Constants.INTENT_FOLIO_TAG, encryption.encryptAES(String.valueOf(folio.getIdRevision())));
            intent.putExtra(Constants.INTENT_FECHA_INICIO_TAG, encryption.encryptAES(folio.getFechaInicio()));
            intent.putExtra(Constants.INTENT_ESTATUS_TAG, encryption.encryptAES(String.valueOf(folio.getEstatus())));

            activity.startActivity(intent);
        }
        progressDialog.dismiss();
    }

    private String sincronizaDatos(SincronizacionPost sincronizacionPost,int opcion){
        Call<SincronizacionResponse> mService = mApiService.sincronizacion(Normalizer.normalize(sharedPreferences.getString(Constants.SP_JWT_TAG, ""), Normalizer.Form.NFD), sincronizacionPost);
        try {
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
                        updateDialogText("No se pudo sincronizar: " + response.errorBody().string());
                        return "No se pudo sincronizar: " + response.errorBody().string();
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

    private void loadDataSincronizacion(){

        //******************************************************************************************
        //Revisiones
        listBarcos = new BarcoDBMethods(activity).readBarcos();
        ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(activity);
        EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(activity);
        UsuarioDBMethods usuarioDBMethods = new UsuarioDBMethods(activity);
        ResponseLogin.Usuario usuario = usuarioDBMethods.readUsuario();

        List<ChecklistData> listChecklist = checklistDBMethods.readChecklists(
                "SELECT ID_REVISION,ID_CHECKLIST,ID_ESTATUS,ID_LOGO,ID_TIPO_REVISION,NOMBRE,PONDERACION FROM " + checklistDBMethods.TP_CAT_CHEKLIST + " WHERE ID_REVISION = ?",
                new String[]{String.valueOf(idRevision)});

        if (listChecklist.size() != 0) {
            ChecklistData checklistData = listChecklist.get(0);
            for (CatalogoBarco catalogoBarco : listBarcos) {
                List<RubroData> listRubros = checklistDBMethods.readRubro(
                        "SELECT ID_REVISION,ID_CHECKLIST,ID_RUBRO,ESTATUS,NOMBRE FROM " + checklistDBMethods.TP_CAT_CL_RUBRO + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ?",
                        new String[]{String.valueOf(checklistData.getIdRevision()), String.valueOf(checklistData.getIdChecklist())});
                for (RubroData rubroData : listRubros) {
                    String query = null;
                    //rubroData.setSeleccionado(true);
                    if(usuario.getIdrol() == 3){
                        query = "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_TIPO_RESPUESTA,ID_RUBRO,ESTATUS,DESCRIPCION,IS_TIERRA,SELECCIONADO FROM " +
                                checklistDBMethods.TP_CAT_CL_PREGUNTA + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ?";
                    }else{
                        query = "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_TIPO_RESPUESTA,ID_RUBRO,ESTATUS,DESCRIPCION,IS_TIERRA,SELECCIONADO FROM " +
                                checklistDBMethods.TP_CAT_CL_PREGUNTA + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND IS_TIERRA = 0";
                    }
                    List<Pregunta> listPreguntas = checklistDBMethods.readPregunta(query,
                            new String[]{String.valueOf(rubroData.getIdRevision()), String.valueOf(rubroData.getIdChecklist()),
                                    String.valueOf(rubroData.getIdRubro())});

                    rubroData.setListPreguntasTemp(listPreguntas);

                    List<RespuestaData> listRespuestas = checklistDBMethods.readRespuesta(
                            "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_RUBRO,ID_ESTATUS,ID_BARCO,ID_REGISTRO,ID_RESPUESTA,SINCRONIZADO FROM " + checklistDBMethods.TP_TRAN_CL_RESPUESTA + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_BARCO = ?"
                            , new String[]{String.valueOf(rubroData.getIdRevision()), String.valueOf(rubroData.getIdChecklist()),
                                    String.valueOf(rubroData.getIdRubro()), String.valueOf(catalogoBarco.getIdBarco())});

                    rubroData.setListRespuestas(listRespuestas);

                    //try {
                    for (Pregunta pregunta : listPreguntas) {
                        pregunta.setSeleccionado(true);
                            /*List<Evidencia> listEvidencias = evidenciasDBMethods.readEvidencias("" +
                                            "SELECT ID_EVIDENCIA,NOMBRE,CONTENIDO_PREVIEW,ID_ESTATUS,ID_ETAPA,ID_REVISION,ID_CHECKLIST," +
                                            "ID_RUBRO,ID_PREGUNTA,ID_REGISTRO,ID_BARCO,CONTENIDO,LATITUDE,LONGITUDE,AGREGADO_COORDINADOR FROM " + evidenciasDBMethods.TP_TRAN_CL_EVIDENCIA +
                                            " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_BARCO = ?" +
                                            " AND ID_ESTATUS != 2",
                                    new String[]{String.valueOf(pregunta.getIdRevision()), String.valueOf(pregunta.getIdChecklist()),
                                            String.valueOf(pregunta.getIdRubro()), String.valueOf(pregunta.getIdPregunta()),
                                            String.valueOf(catalogoBarco.getIdBarco())},false);
                            pregunta.setListEvidencias(listEvidencias);//*/
                        pregunta.setIdBarco(catalogoBarco.getIdBarco());
                    }
                    /*} catch (IOException e) {
                        e.printStackTrace();
                    }//*/
                }
                for(int i=0;i<listRubros.size();i++){
                    if(listRubros.get(i).getListPreguntasTemp() != null){
                        if(listRubros.get(i).getListPreguntasTemp().size() == 0){
                            listRubros.remove(i);
                        }
                    }else{
                        listRubros.remove(i);
                    }
                }
                catalogoBarco.setListRubros(listRubros);
            }
            System.out.println();
        }

        //******************************************************************************************
        //Anexos
        AnexosDBMethods anexosDBMethods = new AnexosDBMethods(activity);
        List<Integer> listRelaciones = anexosDBMethods.readRelacionRevisionAnexo(idRevision);

        listAnexos = new ArrayList<>();

        for(int idAnexo:listRelaciones) {
            List<Anexo> tempListAnexos = anexosDBMethods.readCatalogoAnexos("SELECT ID_ANEXO,ID_SUBANEXO,DESCRIPCION FROM " + anexosDBMethods.TP_CAT_ANEXOS + " WHERE " +
                    "ID_SUBANEXO = 0 AND ID_ANEXO = ?", new String[]{String.valueOf(idAnexo)});
            for(Anexo anexo:tempListAnexos){
                listAnexos.add(anexo);
            }
        }

        for(Anexo anexo:listAnexos){
            List<Anexo> listSubAnexos = anexosDBMethods.readCatalogoAnexos("SELECT ID_ANEXO,ID_SUBANEXO,DESCRIPCION FROM " + anexosDBMethods.TP_CAT_ANEXOS + " WHERE " +
                    "ID_SUBANEXO != 0 AND ID_ANEXO = ?",new String[]{String.valueOf(anexo.getIdAnexo())});
            anexo.setListSubAnexos(listSubAnexos);
            for(Anexo subanexo:listSubAnexos){
                subanexo.setSeleccionado(true);
                /*List<Anexo> listDatosAnexos = anexosDBMethods.readAnexos("SELECT ID_REVISION,ID_ANEXO,ID_SUBANEXO,ID_DOCUMENTO,ID_ETAPA,DOCUMENTO,NOMBRE " +
                                "FROM " + anexosDBMethods.TP_TRAN_ANEXOS + " WHERE ID_REVISION = ? AND ID_ANEXO = ? AND ID_SUBANEXO = ?"
                        , new String[]{String.valueOf(folio), String.valueOf(subanexo.getIdAnexo()), String.valueOf(subanexo.getIdSubAnexo())});//*/

                List<Anexo> listDatosAnexos = anexosDBMethods.readAnexos("SELECT ID_REVISION,ID_ANEXO,ID_SUBANEXO,ID_DOCUMENTO,ID_ETAPA,DOCUMENTO,NOMBRE,SUBANEXO_FCH_SINC,SELECCIONADO " +
                                "FROM " + anexosDBMethods.TP_TRAN_ANEXOS + " WHERE ID_REVISION = ? AND ID_SUBANEXO = ?"
                        , new String[]{String.valueOf(idRevision), String.valueOf(subanexo.getIdSubAnexo())});

                if(listDatosAnexos.size() != 0){
                    //subanexo.setIdDocumento(listDatosAnexos.get(0).getIdDocumento());
                    subanexo.setNombreArchivo(listDatosAnexos.get(0).getNombreArchivo());
                    subanexo.setIdEtapa(listDatosAnexos.get(0).getIdEtapa());
                    subanexo.setIdRevision(idRevision);
                }
            }
        }
    }
}
