package com.elektra.typhoon.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.TyphoonDataBase;
import com.elektra.typhoon.login.MainActivity;
import com.elektra.typhoon.utils.Utils;

//import org.apache.commons.io.FileUtils;

import java.io.File;


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
        //try {
            SharedPreferences sharedPrefs = activity.getSharedPreferences(Constants.SP_NAME,
                    activity.MODE_PRIVATE);
            sharedPrefs.edit().clear().commit();
            new TyphoonDataBase(activity).deleteAll();
            //FileUtils.cleanDirectory(new File(ScanConstants.PATH));
        /*} catch (NullPointerException e) {
            e.printStackTrace();
            return "Error al realizar la nueva instalación: " + e.getMessage();
        }//*/
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
