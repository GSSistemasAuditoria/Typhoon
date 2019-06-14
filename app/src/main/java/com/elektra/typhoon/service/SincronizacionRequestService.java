package com.elektra.typhoon.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import com.elektra.typhoon.login.MainActivity;
import com.elektra.typhoon.objetos.Folio;
import com.elektra.typhoon.objetos.request.DatosRequest;
import com.elektra.typhoon.objetos.request.NotificationSync;
import com.elektra.typhoon.objetos.request.SincronizacionData;
import com.elektra.typhoon.objetos.request.SincronizacionPost;
import com.elektra.typhoon.objetos.request.SubAnexo;
import com.elektra.typhoon.objetos.request.ValidaDatosRequest;
import com.elektra.typhoon.objetos.response.Anexo;
import com.elektra.typhoon.objetos.response.CatalogoBarco;
import com.elektra.typhoon.objetos.response.ChecklistData;
import com.elektra.typhoon.objetos.response.DatosPorValidarResponse;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.FolioRevision;
import com.elektra.typhoon.objetos.response.Historico;
import com.elektra.typhoon.objetos.response.HistoricoAnexo;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.PreguntaData;
import com.elektra.typhoon.objetos.response.PreguntasPorValidar;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.objetos.response.Rubro;
import com.elektra.typhoon.objetos.response.RubroData;
import com.elektra.typhoon.objetos.response.SincronizacionResponse;
import com.elektra.typhoon.objetos.response.SincronizacionResponseData;
import com.elektra.typhoon.utils.Utils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
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
    private String sincronizacionMensaje;
    private String jwt;
    private boolean flagCambios;
    private boolean flagRechazos;
    private boolean flagValidar;
    private Set<Integer> listRechazados;
    private Set<Integer> listValidados;
    private ResponseLogin.Usuario usuario;

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
        jwt = Normalizer.normalize(sharedPreferences.getString(Constants.SP_JWT_TAG, ""), Normalizer.Form.NFD);
        /*SincronizacionData sincronizacionData = new SincronizacionData();
        sincronizacionData.setIdRevision(idRevision);//*/

        usuario = new UsuarioDBMethods(activity).readUsuario();

        ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(activity);
        List<ChecklistData> listChecklist = checklistDBMethods.readChecklists(
                "SELECT ID_REVISION,ID_CHECKLIST,ID_ESTATUS,ID_LOGO,ID_TIPO_REVISION,NOMBRE,PONDERACION FROM " + checklistDBMethods.TP_CAT_CHEKLIST + " WHERE ID_REVISION = ?",
                new String[]{String.valueOf(idRevision)});

        listRechazados = new HashSet<>();
        listValidados = new HashSet<>();

        //Primer sincronización
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

                Call<SincronizacionResponse> mService = mApiService.sincronizacion(jwt, sincronizacionPost);
                Response<SincronizacionResponse> response = mService.execute();
                if (response != null) {
                    if (response.body() != null) {
                        if (response.body().getSincronizacion().getExito()) {
                            //try {

                            String jwt = response.headers().get("Authorization");
                            sharedPreferences.edit().putString(Constants.SP_JWT_TAG, jwt).apply();

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
                            //**********************************************************************************************************
                            ResponseLogin.Usuario usuario = new UsuarioDBMethods(activity).readUsuario();

                            DatosRequest datosRequest = new DatosRequest();

                            datosRequest.setIdRevision(idRevision);
                            EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(activity);
                            List<Evidencia> listaEvidencias = evidenciasDBMethods.readEvidenciasSinDocumento("SELECT ID_EVIDENCIA,FECHA_MOD FROM " + evidenciasDBMethods.TP_TRAN_CL_EVIDENCIA
                                    + " WHERE ID_REVISION = ?",new String[]{String.valueOf(idRevision)});
                            datosRequest.setLocalEvidencias(listaEvidencias);

                            AnexosDBMethods anexosDBMethods = new AnexosDBMethods(activity);
                            List<Anexo> listAnexos = anexosDBMethods.readAnexosSinDocumento("SELECT ID_REVISION,ID_ANEXO,ID_SUBANEXO,ID_DOCUMENTO,ID_ETAPA,NOMBRE,SUBANEXO_FCH_SINC,SELECCIONADO,SUBANEXO_FCH_MOD,ID_ROL,ID_USUARIO " +
                                            "FROM " + anexosDBMethods.TP_TRAN_ANEXOS + " WHERE ID_REVISION = ?"
                                    , new String[]{String.valueOf(idRevision)});
                            List<SubAnexo> listSubanexo = new ArrayList<>();
                            for(Anexo anexo:listAnexos){
                                SubAnexo subAnexo = new SubAnexo();
                                subAnexo.setIdRevision(anexo.getIdRevision());
                                subAnexo.setIdSubAnexo(anexo.getIdSubAnexo());
                                subAnexo.setFechaMod(anexo.getFechaMod());
                                subAnexo.setFechaSincronizacion(anexo.getFechaSinc());
                                listSubanexo.add(subAnexo);
                            }
                            datosRequest.setLocalSubanexo(listSubanexo);
                            ValidaDatosRequest validaDatosRequest = new ValidaDatosRequest();
                            validaDatosRequest.setDatosRequest(datosRequest);

                            Call<DatosPorValidarResponse> mServiceValidar = mApiService.datosPorValidar(jwt,validaDatosRequest);
                            try {
                                Response<DatosPorValidarResponse> responseValidar = mServiceValidar.execute();
                                if(responseValidar != null) {
                                    if (responseValidar.body() != null) {
                                        if (responseValidar.body().getDatos() != null) {
                                            if (responseValidar.body().getDatos().getExito()) {

                                                String jwt2 = response.headers().get("Authorization");
                                                sharedPreferences.edit().putString(Constants.SP_JWT_TAG, jwt2).apply();

                                                loadDataSincronizacion();
                                                return executeSincronizacionCompleta(responseValidar,false);
                                                //return "Sincronizado correctamente";
                                            } else {
                                                //Utils.message(activity, responseValidar.body().getDatos().getError());
                                                return responseValidar.body().getDatos().getError();
                                            }
                                        } else {
                                            //Utils.message(activity, "Error al descargar datos por validar");
                                            return "Error al descargar datos por validar";
                                        }
                                    } else {
                                        if (responseValidar.errorBody() != null) {
                                            try {
                                                String mensaje = "" + responseValidar.errorBody().string();
                                                //int code = responseValidar.code();
                                                int code = responseValidar.code();
                                                //if(!mensaje.contains("No tiene permiso para ver")) {
                                                if(code != 401) {
                                                    ///Utils.message(activity, "Error al descargar folios: " + responseValidar.errorBody().string());
                                                    return "Error al descargar datos por validar: " + responseValidar.errorBody().string();
                                                }else{
                                                    sharedPreferences.edit().putBoolean(Constants.SP_LOGIN_TAG, false).apply();
                                                    //Utils.message(getApplicationContext(), "La sesión ha expirado");
                                                    Intent intent = new Intent(activity,MainActivity.class);
                                                    activity.startActivity(intent);
                                                    //finish();
                                                    return "La sesión ha expirado";
                                                }
                                                //Utils.message(activity, "Error al descargar datos por validar: " + responseValidar.errorBody().string());
                                                //return "Error al descargar datos por validar: " + responseValidar.errorBody().string();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                //Utils.message(activity, "Error al descargar datos por validar: " + e.getMessage());
                                                return "Error al descargar datos por validar: " + e.getMessage();
                                            }
                                        } else {
                                            //Utils.message(activity, "Error al descargar datos por validar");
                                            return "Error al descargar datos por validar";
                                        }
                                    }
                                }else{
                                    //Utils.message(activity, "Error al descargar datos por validar");
                                    return "Error al descargar datos por validar";
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                return "Error al descargar datos por validar: " + e.getMessage();
                            }
                            //*********************************************************************************************************
                            //return "Sincronizado correctamente";
                        } else {
                            //progressDialog.dismiss();
                            return response.body().getSincronizacion().getError();
                        }//*/
                    } else {
                        //progressDialog.dismiss();
                        if (response.errorBody() != null) {
                            String mensaje = "" + response.errorBody().string();
                            int code = response.code();
                            //if(!mensaje.contains("No tiene permiso para ver")) {
                            if(code != 401) {
                                //Utils.message(getApplicationContext(), "Error al descargar folios: " + response.errorBody().string());
                                return "No se pudo sincronizar: " + response.errorBody().string();
                            }else{
                                sharedPreferences.edit().putBoolean(Constants.SP_LOGIN_TAG, false).apply();
                                //Utils.message(activity, "La sesión ha expirado");
                                Intent intent = new Intent(activity,MainActivity.class);
                                activity.startActivity(intent);
                                //finish();
                                return "La sesión ha expirado";
                            }
                            //return "No se pudo sincronizar: " + response.errorBody().string();
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
            ResponseLogin.Usuario usuario = new UsuarioDBMethods(activity).readUsuario();

            try {

                DatosRequest datosRequest = new DatosRequest();

                datosRequest.setIdRevision(idRevision);
                EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(activity);
                List<Evidencia> listaEvidencias = evidenciasDBMethods.readEvidenciasSinDocumento("SELECT ID_EVIDENCIA,FECHA_MOD FROM " + evidenciasDBMethods.TP_TRAN_CL_EVIDENCIA
                        + " WHERE ID_REVISION = ?",new String[]{String.valueOf(idRevision)});
                datosRequest.setLocalEvidencias(listaEvidencias);

                AnexosDBMethods anexosDBMethods = new AnexosDBMethods(activity);
                List<Anexo> listAnexos = anexosDBMethods.readAnexosSinDocumento("SELECT ID_REVISION,ID_ANEXO,ID_SUBANEXO,ID_DOCUMENTO,ID_ETAPA,NOMBRE,SUBANEXO_FCH_SINC,SELECCIONADO,SUBANEXO_FCH_MOD,ID_ROL,ID_USUARIO " +
                                "FROM " + anexosDBMethods.TP_TRAN_ANEXOS + " WHERE ID_REVISION = ?"
                        , new String[]{String.valueOf(idRevision)});
                List<SubAnexo> listSubanexo = new ArrayList<>();
                for(Anexo anexo:listAnexos){
                    SubAnexo subAnexo = new SubAnexo();
                    subAnexo.setIdRevision(anexo.getIdRevision());
                    subAnexo.setIdSubAnexo(anexo.getIdSubAnexo());
                    subAnexo.setFechaMod(anexo.getFechaMod());
                    listSubanexo.add(subAnexo);
                }
                datosRequest.setLocalSubanexo(listSubanexo);
                ValidaDatosRequest validaDatosRequest = new ValidaDatosRequest();
                validaDatosRequest.setDatosRequest(datosRequest);

                Call<DatosPorValidarResponse> mService = mApiService.datosPorValidar(jwt,validaDatosRequest);
                try {
                    Response<DatosPorValidarResponse> response = mService.execute();
                    if(response != null) {
                        if (response.body() != null) {
                            if (response.body().getDatos() != null) {
                                if (response.body().getDatos().getExito()) {

                                    String jwt = response.headers().get("Authorization");
                                    sharedPreferences.edit().putString(Constants.SP_JWT_TAG, jwt).apply();

                                    loadDataSincronizacion();
                                    return executeSincronizacionCompleta(response,true);
                                    //return "Sincronizado correctamente";
                                } else {
                                    //Utils.message(activity, response.body().getDatos().getError());
                                    return response.body().getDatos().getError();
                                }
                            } else {
                                //Utils.message(activity, "Error al descargar datos por validar");
                                return "Error al descargar datos por validar";
                            }
                        } else {
                            if (response.errorBody() != null) {
                                try {
                                    String mensaje = "" + response.errorBody().string();
                                    int code = response.code();
                                    //if(!mensaje.contains("No tiene permiso para ver")) {
                                    if(code != 401) {
                                        //Utils.message(getApplicationContext(), "Error al descargar folios: " + response.errorBody().string());
                                        return "Error al descargar datos por validar: " + response.errorBody().string();
                                    }else{
                                        sharedPreferences.edit().putBoolean(Constants.SP_LOGIN_TAG, false).apply();
                                        //Utils.message(getApplicationContext(), "La sesión ha expirado");
                                        Intent intent = new Intent(activity,MainActivity.class);
                                        activity.startActivity(intent);
                                        //finish();
                                        return "La sesión ha expirado";
                                    }
                                    //Utils.message(activity, "Error al descargar datos por validar: " + response.errorBody().string());
                                    //return "Error al descargar datos por validar: " + response.errorBody().string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    //Utils.message(activity, "Error al descargar datos por validar: " + e.getMessage());
                                    return "Error al descargar datos por validar: " + e.getMessage();
                                }
                            } else {
                                //Utils.message(activity, "Error al descargar datos por validar");
                                return "Error al descargar datos por validar";
                            }
                        }
                    }else{
                        //Utils.message(activity, "Error al descargar datos por validar");
                        return "Error al descargar datos por validar";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Error al descargar datos por validar: " + e.getMessage();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error al descargar datos por validar: " + e.getMessage();
            }
            //loadDataSincronizacion();
            //return executeSincronizacionCompleta();
        }
        //return "OK";
    }

    private boolean preguntaPorDescargar(Response<DatosPorValidarResponse> responseData,int idPregunta,int idBarco){
        for(PreguntasPorValidar preguntasPorValidar:responseData.body().getDatos().getDatosPorValidar().getListPreguntas()){
            if(preguntasPorValidar.getIdPregunta() == idPregunta && existeId(preguntasPorValidar.getListIdBarco(),idBarco)){
                return true;
            }
        }
        return false;
    }

    private boolean existeId(List<Integer> listId,int idValidar){
        if(listId != null) {
            for (int id : listId) {
                if (id == idValidar) {
                    return true;
                }
            }
        }
        return false;
    }

    private int condicionNotificaciones(Response<DatosPorValidarResponse> responseData){
        int condicion = 0;
        int totalActualizar = 0;
        int totalActualizarAnexos = 0;
        if(listBarcos != null) {

            for (CatalogoBarco catalogoBarco : listBarcos) {
                for (RubroData rubroDataTemp : catalogoBarco.getListRubros()) {
                    for (Pregunta preguntaTemp : rubroDataTemp.getListPreguntasTemp()) {
                        if (preguntaTemp.isSeleccionado() || preguntaPorDescargar(responseData, preguntaTemp.getIdPregunta(), catalogoBarco.getIdBarco())) {
                            totalActualizar++;
                        }
                        if(preguntaTemp.isSeleccionado()){
                            flagCambios = true;
                        }
                    }
                }
            }
        }

        if(listAnexos != null) {
            for (Anexo anexo : listAnexos) {
                for (Anexo subanexo : anexo.getListSubAnexos()) {
                    if (subanexo.isSeleccionado() || existeId(responseData.body().getDatos().getDatosPorValidar().getListIdSubanexos(), subanexo.getIdSubAnexo())) {
                        totalActualizarAnexos++;
                    }
                    if(subanexo.isSeleccionado()){
                        flagCambios = true;
                    }
                }
            }
        }

        //si solo se envian anexos
        if (totalActualizar == 0 && totalActualizarAnexos != 0) {
            condicion = 1;
            //si solo se envian evidencias
        }else if (totalActualizar != 0 && totalActualizarAnexos == 0) {
            condicion = 2;
            //si se envían evidencias y anexos
        }else if (totalActualizar != 0 && totalActualizarAnexos != 0) {
            condicion = 3;
        }

        return condicion;
    }

    private String executeSincronizacionCompleta(Response<DatosPorValidarResponse> responseData,boolean notificar){
        SincronizacionData sincronizacionData = null;
        try {
            int totalActualizar = 0;
            int contador = 0;
            int contadorEvidenciasProcesadas = 0;
            int flagNotificaciones = condicionNotificaciones(responseData);
            FoliosDBMethods foliosDBMethods = new FoliosDBMethods(context);
            folio = foliosDBMethods.readFolio(
                    "SELECT ID_REVISION,NOMBRE,ID_TIPO_REVISION,ID_USUARIO,FECHA_INICIO,FECHA_FIN,ESTATUS FROM " + foliosDBMethods.TP_TRAN_REVISION + " WHERE ID_REVISION = ?",
                    new String[]{String.valueOf(idRevision)});
            if(listBarcos != null) {

                for (CatalogoBarco catalogoBarco : listBarcos) {
                    for (RubroData rubroDataTemp : catalogoBarco.getListRubros()) {
                        for (Pregunta preguntaTemp : rubroDataTemp.getListPreguntasTemp()) {
                            if (preguntaTemp.isSeleccionado() || preguntaPorDescargar(responseData,preguntaTemp.getIdPregunta(),catalogoBarco.getIdBarco())) {
                                totalActualizar++;
                            }
                        }
                    }
                }

                if(totalActualizar != 0) {
                    int fallidos = 0;
                    updateDialogText("Sincronizando evidencias...");
                    updateDialogText("Evidencias\nSincronizado: " + (totalActualizar - (totalActualizar - contador)) + " de: " + totalActualizar
                            + "\nFallidos: " + fallidos + " de: " + totalActualizar);
                    for (CatalogoBarco catalogoBarco : listBarcos) {
                        for (RubroData rubroDataTemp : catalogoBarco.getListRubros()) {
                            //for (Pregunta preguntaTemp : rubroDataTemp.getListPreguntasTemp()) {
                            for (int i=0;i<rubroDataTemp.getListPreguntasTemp().size();i++) {
                                Pregunta preguntaTemp = rubroDataTemp.getListPreguntasTemp().get(i);
                                if (preguntaTemp.isSeleccionado() || preguntaPorDescargar(responseData,preguntaTemp.getIdPregunta(),catalogoBarco.getIdBarco())) {
                                    SincronizacionPost sincronizacionPost = new SincronizacionPost();
                                    sincronizacionData = new SincronizacionJSON().generateRequestDataIndividual(activity, context, idRevision, preguntaTemp.getIdRubro()
                                            , preguntaTemp.getIdPregunta(), 0, preguntaTemp.getIdBarco());
                                    sincronizacionPost.setSincronizacionData(sincronizacionData);
                                    for(ChecklistData checklistData:sincronizacionData.getListChecklist()){
                                        for(Rubro rubro:checklistData.getListRubros()){
                                            for(PreguntaData preguntaData:rubro.getListPreguntas()){
                                                for(Evidencia evidencia:preguntaData.getListEvidencias()){
                                                    if(evidencia.getIdEstatus() == 3){
                                                        if(usuario.getIdrol() > evidencia.getIdEtapa()) {
                                                            listRechazados.add(evidencia.getIdEtapa());
                                                            flagRechazos = true;
                                                        }
                                                    }else if(evidencia.getIdEstatus() == 1){
                                                        if(evidencia.getIdEtapa() == (usuario.getIdrol() + 1)) {
                                                            listValidados.add(evidencia.getIdEtapa());
                                                            flagValidar = true;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if(notificar) {
                                        if(flagCambios) {
                                            if (contadorEvidenciasProcesadas == totalActualizar - 1) {
                                                if (flagNotificaciones == 2) {
                                                    //sincronizacionData.setUltimaSincronizacion(true);
                                                    NotificationSync notificationSync = new NotificationSync();
                                                    notificationSync.setNotification(true);
                                                    if(flagValidar) {
                                                        notificationSync.setRolesNotificacionValidadas(setToList(listValidados));
                                                    }
                                                    if(flagRechazos){
                                                        notificationSync.setRolesNotificacionRechazadas(setToList(listRechazados));
                                                    }
                                                    if(notificationSync.getRolesNotificacionRechazadas() == null){
                                                        notificationSync.setRolesNotificacionRechazadas(new ArrayList<Integer>());
                                                    }
                                                    if(notificationSync.getRolesNotificacionValidadas() == null){
                                                        notificationSync.setRolesNotificacionValidadas(new ArrayList<Integer>());
                                                    }
                                                    sincronizacionData.setNotificationSync(notificationSync);
                                                }
                                            }
                                        }
                                    }

                                    if(sincronizacionData.getNotificationSync() == null){
                                        NotificationSync notificationSync = new NotificationSync();
                                        notificationSync.setNotification(false);
                                        notificationSync.setRolesNotificacionRechazadas(new ArrayList<Integer>());
                                        notificationSync.setRolesNotificacionValidadas(new ArrayList<Integer>());
                                        sincronizacionData.setNotificationSync(notificationSync);
                                    }

                                    String response = sincronizaDatos(sincronizacionPost, 1);
                                    if (response.equals("Sincronizado correctamente")) {
                                        contador++;
                                        //contadorEvidenciasProcesadas++;
                                        System.out.println("Rubro: " + preguntaTemp.getIdRubro() + " Pregunta: " + preguntaTemp.getIdPregunta() + " Barco: " + preguntaTemp.getIdBarco());
                                    }else{
                                        fallidos++;
                                    }
                                    if(response.equals("La sesión ha expirado")){
                                        return "La sesión ha expirado";
                                    }
                                    contadorEvidenciasProcesadas++;
                                    updateDialogText("Evidencias\nSincronizado: " + (totalActualizar - (totalActualizar - contador)) + " de: " + totalActualizar
                                            + "\nFallidos: " + fallidos + " de: " + totalActualizar);
                                }
                            }
                        }
                    }
                }else{
                    //return "No hay preguntas para sincronizar";
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
                int contadorAnexos = 0;
                for(Anexo anexo:listAnexos) {
                    for(Anexo subanexo:anexo.getListSubAnexos()) {
                        if (subanexo.isSeleccionado() || existeId(responseData.body().getDatos().getDatosPorValidar().getListIdSubanexos(),subanexo.getIdSubAnexo())) {
                            totalActualizar++;
                        }
                    }
                }

                if(totalActualizar != 0) {
                    int fallidosAnexos = 0;
                    updateDialogText("Sincronizando anexos...");
                    updateDialogText("Anexos\nSincronizado: " + (totalActualizar - (totalActualizar - contador)) + " de: " + totalActualizar
                            + "\nFallidos: " + fallidosAnexos + " de: " + totalActualizar);
                    for (Anexo anexo : listAnexos) {
                        //for (Anexo subanexo : anexo.getListSubAnexos()) {
                        for (int i=0;i<anexo.getListSubAnexos().size();i++) {
                            Anexo subanexo = anexo.getListSubAnexos().get(i);
                            if (subanexo.isSeleccionado() || existeId(responseData.body().getDatos().getDatosPorValidar().getListIdSubanexos(),subanexo.getIdSubAnexo())) {
                                SincronizacionPost sincronizacionPost = new SincronizacionPost();
                                sincronizacionData = new SincronizacionJSON().generateRequestDataIndividual(activity, context, idRevision, 0, 0, subanexo.getIdSubAnexo(), 0);
                                sincronizacionData.setSyncMode(1);
                                sincronizacionPost.setSincronizacionData(sincronizacionData);
                                for(SubAnexo subAnexo:sincronizacionData.getListSubAnexos()){
                                    if(subAnexo.getIdEtapa() == -1){
                                        listRechazados.add(subAnexo.getIdRol());
                                        flagRechazos = true;
                                    }else{
                                        listValidados.add(usuario.getIdrol() + 1);
                                        flagValidar = true;
                                    }
                                }
                                if(notificar) {
                                    if(flagCambios) {
                                        if (contadorAnexos == totalActualizar - 1) {
                                            if (flagNotificaciones == 1 || flagNotificaciones == 3) {
                                                //sincronizacionData.setUltimaSincronizacion(true);
                                                NotificationSync notificationSync = new NotificationSync();
                                                notificationSync.setNotification(true);
                                                if(flagValidar) {
                                                    notificationSync.setRolesNotificacionValidadas(setToList(listValidados));
                                                }
                                                if(flagRechazos){
                                                    notificationSync.setRolesNotificacionRechazadas(setToList(listRechazados));
                                                }
                                                if(notificationSync.getRolesNotificacionRechazadas() == null){
                                                    notificationSync.setRolesNotificacionRechazadas(new ArrayList<Integer>());
                                                }
                                                if(notificationSync.getRolesNotificacionValidadas() == null){
                                                    notificationSync.setRolesNotificacionValidadas(new ArrayList<Integer>());
                                                }
                                                sincronizacionData.setNotificationSync(notificationSync);
                                            }
                                        }
                                    }
                                }

                                if(sincronizacionData.getNotificationSync() == null){
                                    NotificationSync notificationSync = new NotificationSync();
                                    notificationSync.setNotification(false);
                                    notificationSync.setRolesNotificacionRechazadas(new ArrayList<Integer>());
                                    notificationSync.setRolesNotificacionValidadas(new ArrayList<Integer>());
                                    sincronizacionData.setNotificationSync(notificationSync);
                                }

                                String response = sincronizaDatos(sincronizacionPost,2);
                                if(response.equals("Sincronizado correctamente")) {
                                    contador++;
                                    //contadorAnexos++;
                                    System.out.println("Anexo: " + anexo.getIdAnexo() + " Subanexo: " + subanexo.getIdSubAnexo());
                                }else{
                                    fallidosAnexos++;
                                }
                                contadorAnexos++;
                                if(response.equals("La sesión ha expirado")){
                                    return "La sesión ha expirado";
                                }
                                updateDialogText("Anexos\nSincronizado: " + (totalActualizar - (totalActualizar - contador)) + " de: " + totalActualizar
                                        + "\nFallidos: " + fallidosAnexos + " de: " + totalActualizar);
                            }
                        }
                    }
                }else{
                    //return "No hay anexos para sincronizar";
                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return "Error al sincronizar: " + e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error al sincronizar: " + e.getMessage();
        }//*/
        return "Termina la sincronización";
    }

    private List<Integer> setToList(Set<Integer> listSet){
        List<Integer> listInteger = new ArrayList<>();
        for(int valor:listSet){
            listInteger.add(valor);
        }
        return listInteger;
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

        if(sincronizacionMensaje != null){
            if(!sincronizacionMensaje.equals("")){
                Utils.message(activity,sincronizacionMensaje);
            }
        }

        if(checklistBarcos != null){
            checklistBarcos.reloadData();
        }else{
            Intent intent = new Intent(activity, ChecklistBarcos.class);

            Encryption encryption = new Encryption();
            FoliosDBMethods foliosDBMethods = new FoliosDBMethods(context);
            FolioRevision folioIntent = foliosDBMethods.readFolio(
                    "SELECT ID_REVISION,NOMBRE,ID_TIPO_REVISION,ID_USUARIO,FECHA_INICIO,FECHA_FIN,ESTATUS FROM " + foliosDBMethods.TP_TRAN_REVISION + " WHERE ID_REVISION = ?",
                    new String[]{String.valueOf(idRevision)});

            intent.putExtra(Constants.INTENT_FOLIO_TAG, encryption.encryptAES(String.valueOf(folioIntent.getIdRevision())));
            intent.putExtra(Constants.INTENT_FECHA_INICIO_TAG, encryption.encryptAES(folioIntent.getFechaInicio()));
            intent.putExtra(Constants.INTENT_ESTATUS_TAG, encryption.encryptAES(String.valueOf(folioIntent.getEstatus())));

            activity.startActivity(intent);
            if(!activity.getComponentName().toString().contains("Cartera")) {
                activity.finish();
            }
        }
        progressDialog.dismiss();
    }

    private String sincronizaDatos(SincronizacionPost sincronizacionPost,int opcion){
        String jwtSinc = Normalizer.normalize(sharedPreferences.getString(Constants.SP_JWT_TAG, ""), Normalizer.Form.NFD);
        Call<SincronizacionResponse> mService = mApiService.sincronizacion(jwtSinc, sincronizacionPost);
        try {
            Response<SincronizacionResponse> response = mService.execute();
            if (response != null) {
                if (response.body() != null) {
                    if (response.body().getSincronizacion().getExito()) {

                        String jwt = response.headers().get("Authorization");
                        sharedPreferences.edit().putString(Constants.SP_JWT_TAG, jwt).apply();

                        HistoricoDBMethods historicoDBMethods = new HistoricoDBMethods(context);
                        //try {
                        if(opcion == 1){
                            if (response.body().getSincronizacion().getSincronizacionResponseData().getListChecklist() != null) {
                                ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(context);
                                EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(context);


                                if(response.body().getSincronizacion().getError() != null) {
                                    sincronizacionMensaje = response.body().getSincronizacion().getError();
                                }

                                //Borrado de evidencias y su histórico
                                for(ChecklistData checklistData:sincronizacionPost.getSincronizacionData().getListChecklist()){
                                    for(Rubro rubro:checklistData.getListRubros()){
                                        for(PreguntaData preguntaData:rubro.getListPreguntas()){
                                            for(Evidencia evidencia:preguntaData.getListEvidencias()){
                                                /*evidenciasDBMethods.deleteEvidencia("ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_EVIDENCIA = ? AND ID_PREGUNTA = ? AND ID_REGISTRO = ?",
                                                        new String[]{String.valueOf(idRevision), String.valueOf(checklistData.getIdChecklist())
                                                                , String.valueOf(evidencia.getIdEvidencia()), String.valueOf(evidencia.getIdPregunta()), String.valueOf(evidencia.getIdRegistro())});//*/
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
                                                                if(evidencia.getContenido() != null) {
                                                                    evidenciasDBMethods.createEvidencia(evidencia);
                                                                }else{
                                                                    ContentValues contentValues = new ContentValues();
                                                                    //contentValues.put("NUEVO",0);
                                                                    contentValues.put("FECHA_MOD",evidencia.getFechaMod());
                                                                    contentValues.put("ID_ETAPA",evidencia.getIdEtapa());
                                                                    contentValues.put("ID_ESTATUS",evidencia.getIdEstatus());
                                                                    evidenciasDBMethods.updateEvidencia(contentValues,"ID_EVIDENCIA = ?",
                                                                            new String[]{evidencia.getIdEvidencia()});
                                                                }
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
                                                //respuestaData.setSincronizado(1);
                                                checklistDBMethods.createRespuesta(respuestaData);
                                            }
                                        }else{
                                            checklistDBMethods.createRespuesta(respuestaData);
                                        }//*/
                                    }
                                }

                            }
                        }else{
                            //Borrado de anexos
                            AnexosDBMethods anexosDBMethods = new AnexosDBMethods(context);

                            for(SubAnexo subAnexo:sincronizacionPost.getSincronizacionData().getListSubAnexos()){
                                //anexosDBMethods.deleteAnexo("ID_REVISION = ? AND ID_SUBANEXO = ?", new String[]{String.valueOf(idRevision),String.valueOf(subAnexo.getIdSubAnexo())});
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
                                    anexo.setFechaMod(subAnexo.getFechaMod());
                                    anexo.setIdRol(subAnexo.getIdRol());
                                    anexo.setIdUsuario(subAnexo.getIdUsuario());

                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("ID_ETAPA",subAnexo.getIdEtapa());
                                    contentValues.put("SUBANEXO_FCH_SINC",subAnexo.getFechaSincronizacion());
                                    contentValues.put("NOMBRE",subAnexo.getFileName());
                                    contentValues.put("SUBANEXO_FCH_MOD",subAnexo.getFechaMod());
                                    //contentValues.put("DOCUMENTO",subAnexo.getContenido());

                                    if(subAnexo.getContenido() != null){
                                        new AnexosDBMethods(activity).createAnexo(anexo);
                                    }else {
                                        new AnexosDBMethods(activity).updateAnexo(contentValues, "ID_REVISION = ? AND ID_SUBANEXO = ?",
                                                new String[]{String.valueOf(subAnexo.getIdRevision()), String.valueOf(subAnexo.getIdSubAnexo())});
                                    }

                                    //new AnexosDBMethods(activity).createAnexo(anexo);
                                    if (subAnexo.getListHistorico() != null) {
                                        for (HistoricoAnexo historicoAnexo : subAnexo.getListHistorico()) {
                                            historicoDBMethods.createHistoricoAnexo(historicoAnexo);
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

                        SincronizacionResponseData sincronizacionResponseData = response.body().getSincronizacion().getSincronizacionResponseData();

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("ID_REVISION",sincronizacionResponseData.getIdRevision());
                        contentValues.put("NOMBRE",sincronizacionResponseData.getNombre());
                        contentValues.put("ID_TIPO_REVISION",sincronizacionResponseData.getIdTipoRevision());
                        contentValues.put("ID_USUARIO",sincronizacionResponseData.getIdUsuario());
                        contentValues.put("FECHA_INICIO",sincronizacionResponseData.getFechaInicio());
                        contentValues.put("FECHA_FIN",sincronizacionResponseData.getFechaFin());
                        contentValues.put("ESTATUS",sincronizacionResponseData.getEstatus());

                        new FoliosDBMethods(activity).updateFolio(contentValues,"ID_REVISION = ?",new String[]{String.valueOf(sincronizacionResponseData.getIdRevision())});

                        //updateDialogText("Sincronizado correctamente");
                        return "Sincronizado correctamente";
                    } else {
                        //progressDialog.dismiss();
                        //updateDialogText(response.body().getSincronizacion().getError());
                        return response.body().getSincronizacion().getError();
                    }
                } else {
                    //progressDialog.dismiss();
                    if (response.errorBody() != null) {
                        String mensaje = "" + response.errorBody().string();
                        int code = response.code();
                        //if(!mensaje.contains("No tiene permiso para ver")) {
                        if(code != 401) {
                            //Utils.message(getApplicationContext(), "Error al descargar folios: " + response.errorBody().string());
                            return "No se pudo sincronizar: " + response.errorBody().string();
                        }else{
                            sharedPreferences.edit().putBoolean(Constants.SP_LOGIN_TAG, false).apply();
                            //Utils.message(activity, "La sesión ha expirado");
                            Intent intent = new Intent(activity,MainActivity.class);
                            activity.startActivity(intent);
                            //finish();
                            return "La sesión ha expirado";
                        }
                        //updateDialogText("No se pudo sincronizar: " + response.errorBody().string());
                        //return "No se pudo sincronizar: " + response.errorBody().string();
                    } else {
                        //updateDialogText("No se pudo sincronizar");
                        return "No se pudo sincronizar";
                    }
                }
            } else {
                //progressDialog.dismiss();
                //updateDialogText("No se pudo sincronizar");
                return "No se pudo sincronizar";
            }
        } catch (IOException e) {
            //e.printStackTrace();
            //updateDialogText("Error al sincronizar: " + e.getMessage());
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

    private boolean isPreguntaSeleccionada(List<RespuestaData> listRespuestas,Pregunta pregunta){
        for(RespuestaData respuestaData:listRespuestas){
            if(respuestaData.getIdRevision() == pregunta.getIdRevision() &&
                    respuestaData.getIdChecklist() == pregunta.getIdChecklist() &&
                    respuestaData.getIdRubro() == pregunta.getIdRubro() &&
                    respuestaData.getIdPregunta() == pregunta.getIdPregunta() &&
                    respuestaData.getIdBarco() == pregunta.getIdBarco()){
                if(respuestaData.getSincronizado() == 1){
                    return true;
                }else{
                    return false;
                }
            }
        }
        return false;
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
                        //pregunta.setSeleccionado(true);
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
                        pregunta.setSeleccionado(Utils.isPreguntaSeleccionada(listRespuestas,pregunta));
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
                //subanexo.setSeleccionado(true);
                /*List<Anexo> listDatosAnexos = anexosDBMethods.readAnexos("SELECT ID_REVISION,ID_ANEXO,ID_SUBANEXO,ID_DOCUMENTO,ID_ETAPA,DOCUMENTO,NOMBRE " +
                                "FROM " + anexosDBMethods.TP_TRAN_ANEXOS + " WHERE ID_REVISION = ? AND ID_ANEXO = ? AND ID_SUBANEXO = ?"
                        , new String[]{String.valueOf(folio), String.valueOf(subanexo.getIdAnexo()), String.valueOf(subanexo.getIdSubAnexo())});//*/

                List<Anexo> listDatosAnexos = anexosDBMethods.readAnexos("SELECT ID_REVISION,ID_ANEXO,ID_SUBANEXO,ID_DOCUMENTO,ID_ETAPA,DOCUMENTO,NOMBRE,SUBANEXO_FCH_SINC,SELECCIONADO,SUBANEXO_FCH_MOD,ID_ROL,ID_USUARIO " +
                                "FROM " + anexosDBMethods.TP_TRAN_ANEXOS + " WHERE ID_REVISION = ? AND ID_SUBANEXO = ?"
                        , new String[]{String.valueOf(idRevision), String.valueOf(subanexo.getIdSubAnexo())});

                if(listDatosAnexos.size() != 0){
                    //subanexo.setIdDocumento(listDatosAnexos.get(0).getIdDocumento());
                    subanexo.setNombreArchivo(listDatosAnexos.get(0).getNombreArchivo());
                    subanexo.setIdEtapa(listDatosAnexos.get(0).getIdEtapa());
                    subanexo.setIdRevision(idRevision);
                    subanexo.setSeleccionado(listDatosAnexos.get(0).isSeleccionado());
                }
            }
        }
    }
}
