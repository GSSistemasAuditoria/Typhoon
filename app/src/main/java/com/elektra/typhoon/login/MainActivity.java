package com.elektra.typhoon.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.elektra.typhoon.R;
import com.elektra.typhoon.carteraFolios.CarteraFolios;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.BarcoDBMethods;
import com.elektra.typhoon.database.CatalogosDBMethods;
import com.elektra.typhoon.database.TyphoonDataBase;
import com.elektra.typhoon.database.UsuarioDBMethods;
import com.elektra.typhoon.objetos.response.Barco;
import com.elektra.typhoon.objetos.response.CatalogoBarco;
import com.elektra.typhoon.objetos.response.CatalogosTyphoonResponse;
import com.elektra.typhoon.objetos.response.EstatusEvidencia;
import com.elektra.typhoon.objetos.response.EtapaEvidencia;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.TipoRespuesta;
import com.elektra.typhoon.registro.NuevoRegistro;
import com.elektra.typhoon.registro.RestablecerContrasena;
import com.elektra.typhoon.service.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.elektra.typhoon.utils.Utils;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 04/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */

public class MainActivity extends AppCompatActivity {
    private EditText editTextUsuario;
    private EditText editTextContrasena;
    private Button entrar;
    private Button registro;
    private Button registrarse;
    private Button restablecerContrasena;
    private ConstraintLayout layoutDialog;
    private String usuario;
    private String contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.login_layout);

        editTextUsuario = (EditText)findViewById(R.id.editTextUsuario);
        editTextContrasena = (EditText)findViewById(R.id.editTextContrasena);
        entrar = (Button) findViewById(R.id.buttonEntrar);
        registro = (Button)findViewById(R.id.buttonRegistro);

        float scale = getResources().getConfiguration().fontScale;
        System.out.println("Escala del texto: " + scale);

        SharedPreferences sharedPrefs = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
        if (sharedPrefs.contains(Constants.SP_LOGIN_TAG)) {
            if (sharedPrefs.getBoolean(Constants.SP_LOGIN_TAG, false)) {
                Intent intent = new Intent(getApplicationContext(), CarteraFolios.class);
                startActivity(intent);
                finish();
            }
        } else {
            SharedPreferences.Editor ed;
            ed = sharedPrefs.edit();
            ed.putBoolean(Constants.SP_LOGIN_TAG, false);
            ed.commit();
        }//*/

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario = editTextUsuario.getText().toString();
                contrasena = editTextContrasena.getText().toString();
                if(!usuario.equals("")){
                    if(!contrasena.equals("")){
                        iniciarSesion(usuario,contrasena);
                    }else{
                        Utils.message(getApplicationContext(),"Debe introducir la contraseña");
                    }
                }else{
                    Utils.message(getApplicationContext(),"Debe introducir id de empleado o su correo");
                }
            }
        });

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                layoutDialog = (ConstraintLayout) li.inflate(R.layout.dialog_opciones_externo, null);
                final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setView(layoutDialog)
                        .show();

                registrarse = layoutDialog.findViewById(R.id.buttonRegistrarse);
                restablecerContrasena = layoutDialog.findViewById(R.id.buttonRestablecerContrasena);

                registrarse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, NuevoRegistro.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                restablecerContrasena.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, RestablecerContrasena.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
            }
        });

        TyphoonDataBase typhoonDataBase = new TyphoonDataBase(getApplicationContext());
    }

    private ApiInterface getInterfaceService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_PUBLIC)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        return mInterfaceService;
    }

    private void iniciarSesion(final String usuario, String contrasena){

        final ProgressDialog progressDialog = Utils.typhoonLoader(MainActivity.this,"Iniciando sesión...");

        ApiInterface mApiService = this.getInterfaceService();
        Call<ResponseLogin> mService = mApiService.authenticate(usuario, contrasena);
        mService.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                progressDialog.dismiss();
                if(response.body() != null) {
                    if(response.body().getValidarEmpleado().getExito()){
                        try {
                            new UsuarioDBMethods(getApplicationContext()).createUsuario(response.body().getValidarEmpleado().getUsuario());
                            SharedPreferences sharedPrefs = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
                            sharedPrefs.edit().putBoolean(Constants.SP_LOGIN_TAG, true).apply();
                            sharedPrefs.edit().putString(Constants.SP_JWT_TAG, response.body().getValidarEmpleado().getUsuario().getJwt()).apply();

                            BarcoDBMethods barcoDBMethods = new BarcoDBMethods(getApplicationContext());
                            if(barcoDBMethods.readBarcos(null,null).size() == 0) {
                                Utils.descargaCatalogos(MainActivity.this,1);
                            }else {
                                Intent intent = new Intent(MainActivity.this, CarteraFolios.class);
                                startActivity(intent);
                                finish();
                            }
                        }catch (Exception e){
                            Utils.message(getApplicationContext(),"No se pudieron guardar los datos del usuario: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }else{
                        Utils.message(getApplicationContext(), response.body().getValidarEmpleado().getError());
                    }
                }else{
                    Utils.message(getApplicationContext(),"Error al iniciar sesión");
                }
            }
            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                progressDialog.dismiss();
                Utils.message(MainActivity.this, Constants.MSG_ERR_CONN);
            }
        });
    }

    /*private void descargaCatalogos(){

        final ProgressDialog progressDialog = Utils.typhoonLoader(MainActivity.this,"Descargando catálogos...");

        ApiInterface mApiService = this.getInterfaceService();
        Call<CatalogosTyphoonResponse> mService = mApiService.catalogosTyphoon();
        mService.enqueue(new Callback<CatalogosTyphoonResponse>() {
            @Override
            public void onResponse(Call<CatalogosTyphoonResponse> call, Response<CatalogosTyphoonResponse> response) {
                if(response.body() != null) {
                    if(response.body().getCatalogos().getExito()){
                        try {
                            BarcoDBMethods barcoDBMethods = new BarcoDBMethods(getApplicationContext());
                            CatalogosDBMethods catalogosDBMethods = new CatalogosDBMethods(getApplicationContext());
                            if(response.body().getCatalogos().getCatalogosData().getListBarcos() != null){
                                barcoDBMethods.deleteBarco();
                                for(Barco catalogoBarco:response.body().getCatalogos().getCatalogosData().getListBarcos()){
                                    barcoDBMethods.createBarco(catalogoBarco);
                                }
                            }
                            if(response.body().getCatalogos().getCatalogosData().getListEstatusEvidencia() != null){
                                catalogosDBMethods.deleteEstatusEvidencia();
                                for(EstatusEvidencia estatusEvidencia:response.body().getCatalogos().getCatalogosData().getListEstatusEvidencia()){
                                    catalogosDBMethods.createEstatusEvidencia(estatusEvidencia);
                                }
                            }
                            if(response.body().getCatalogos().getCatalogosData().getListEtapasEvidencia() != null){
                                catalogosDBMethods.deleteEtapaEvidencia();
                                for(EtapaEvidencia etapaEvidencia:response.body().getCatalogos().getCatalogosData().getListEtapasEvidencia()){
                                    catalogosDBMethods.createEtapaEvidencia(etapaEvidencia);
                                }
                            }
                            if(response.body().getCatalogos().getCatalogosData().getListTiposRespuesta() != null){
                                catalogosDBMethods.deleteTipoRespuesta();
                                for(TipoRespuesta tipoRespuesta:response.body().getCatalogos().getCatalogosData().getListTiposRespuesta()){
                                    catalogosDBMethods.createTipoRespuesta(tipoRespuesta);
                                }
                            }
                            progressDialog.dismiss();
                            Utils.message(getApplicationContext(),"Catálogos descargados");
                            Intent intent = new Intent(MainActivity.this, CarteraFolios.class);
                            startActivity(intent);
                            finish();
                        }catch (Exception e){
                            progressDialog.dismiss();
                            Utils.message(getApplicationContext(),"Error al guardar los catálogos: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }else{
                        progressDialog.dismiss();
                        Utils.message(getApplicationContext(), response.body().getCatalogos().getError());
                    }
                }else{
                    progressDialog.dismiss();
                    Utils.message(getApplicationContext(),"Error al descargar catálogos");
                }
            }
            @Override
            public void onFailure(Call<CatalogosTyphoonResponse> call, Throwable t) {
                progressDialog.dismiss();
                Utils.message(MainActivity.this, Constants.MSG_ERR_CONN);
            }
        });
    }//*/
}
