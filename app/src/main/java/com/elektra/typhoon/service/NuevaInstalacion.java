package com.elektra.typhoon.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.TyphoonDataBase;
import com.elektra.typhoon.database.UsuarioDBMethods;
import com.elektra.typhoon.encryption.Encryption;
import com.elektra.typhoon.login.MainActivity;
import com.elektra.typhoon.objetos.response.CerrarSesionResponse;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.utils.Utils;

//import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;

import retrofit2.Call;
import retrofit2.Response;


/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 21/02/2019
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */

public class NuevaInstalacion extends AsyncTask<String,String,String> {

    private ProgressDialog statusDialog;
    private Activity activity;

    public NuevaInstalacion(Activity activity){
        this.activity = activity;
    }

    protected void onPreExecute() {
        statusDialog = Utils.typhoonLoader(activity,"Borrando datos...");
    }

    @Override
    protected String doInBackground(String... params) {

        ResponseLogin.Usuario usuario = new UsuarioDBMethods(activity).readUsuario();
        ApiInterface mApiService = Utils.getInterfaceService();
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE);
        String jwt = new Encryption().decryptAES(Normalizer.normalize(sharedPreferences.getString(Constants.SP_JWT_TAG, ""), Normalizer.Form.NFD));
        Call<CerrarSesionResponse> mService = mApiService.cerrarSesion(usuario.getIdUsuario());
        try {
            Response<CerrarSesionResponse> response = mService.execute();
            if (response != null) {
                if (response.body() != null) {
                    if (response.body().getCerrarSesion().getExito()) {
                        //try {
                        /*String jwt2 = response.headers().get("Authorization");
                        sharedPreferences.edit().putString(Constants.SP_JWT_TAG, jwt2).apply();*/

                        SharedPreferences sharedPrefs = activity.getSharedPreferences(Constants.SP_NAME,
                                Context.MODE_PRIVATE);
                        sharedPrefs.edit().clear().apply();
                        new TyphoonDataBase(activity).deleteAll();

                    } else {
                        //progressDialog.dismiss();
                        return response.body().getCerrarSesion().getError();
                    }//*/
                } else {
                    //progressDialog.dismiss();
                    if (response.errorBody() != null) {
                        String mensaje = "" + response.errorBody().string();
                        int code = response.code();
                        //if(!mensaje.contains("No tiene permiso para ver")) {
                        if(code != 401) {
                            //Utils.message(getApplicationContext(), "Error al descargar folios: " + response.errorBody().string());
                            return "No se pudo realizar la nueva instalación: " + response.errorBody().string();
                        }else{
                            sharedPreferences.edit().putBoolean(Constants.SP_LOGIN_TAG, false).apply();
                            //Utils.message(activity, "La sesión ha expirado");
                            Intent intent = new Intent(activity,MainActivity.class);
                            activity.startActivity(intent);
                            //finish();
                            return "La sesión ha expirado";
                        }
                    } else {
                        return "No se pudo realizar la nueva instalación";
                    }
                }
            } else {
                //progressDialog.dismiss();
                return "No se pudo realizar la nueva instalación";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "No se pudo realizar la nueva instalación: " + e.getMessage();
        }
        //FileUtils.cleanDirectory(new File(ScanConstants.PATH));

        return "OK";
    }

    @Override
    protected void onPostExecute(String result) {
        statusDialog.dismiss();
        /*new RequestSendLog(activity,idAuditor,this.getClass().getSimpleName(),0,
                "Nueva instalación").execute();//*/
        if(result.equals("OK")) {
            Utils.message(activity, "Datos borrados");
            Utils.message(activity, "Iniciar nueva sesión");
            Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }else{
            Utils.message(activity,result);
        }
    }

    @Override
    public void onProgressUpdate(String... values) {
        statusDialog.setMessage(values[0]);
    }
}
