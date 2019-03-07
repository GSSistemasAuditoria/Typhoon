package com.elektra.typhoon.login;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.carteraFolios.CarteraFolios;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.BarcoDBMethods;
import com.elektra.typhoon.database.CatalogosDBMethods;
import com.elektra.typhoon.database.TyphoonDataBase;
import com.elektra.typhoon.database.UsuarioDBMethods;
import com.elektra.typhoon.encryption.Encryption;
import com.elektra.typhoon.gps.GPSTracker;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            }else{
                Utils.checkPermission(this);
            }
        } else {
            SharedPreferences.Editor ed;
            ed = sharedPrefs.edit();
            ed.putBoolean(Constants.SP_LOGIN_TAG, false);
            ed.commit();
            Utils.checkPermission(this);
        }//*/

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResponseLogin.Usuario usuarioDB = new UsuarioDBMethods(getApplicationContext()).readUsuario(null,null);
                usuario = editTextUsuario.getText().toString();
                contrasena = editTextContrasena.getText().toString();
                if(!usuario.equals("")){
                    if(usuarioDB == null){
                        if (!contrasena.equals("")) {
                            iniciarSesion(usuario, contrasena);
                        } else {
                            Utils.message(getApplicationContext(), "Debe introducir la contraseña");
                        }
                    }else {
                        if (usuarioDB.getCorreo().equals(usuario) || usuarioDB.getIdUsuario().equals(usuario)) {
                            if (!contrasena.equals("")) {
                                iniciarSesion(usuario, contrasena);
                            } else {
                                Utils.message(getApplicationContext(), "Debe introducir la contraseña");
                            }
                        } else {
                            nuevaInstalacionDialog(usuarioDB.getNombre());
                        }
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

                RelativeLayout relativeLayoutRegistrarse = (RelativeLayout) layoutDialog.findViewById(R.id.relativeLayoutRegistrarse);
                RelativeLayout relativeLayoutRecuperar = (RelativeLayout) layoutDialog.findViewById(R.id.relativeLayoutRecuperar);

                relativeLayoutRegistrarse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, NuevoRegistro.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                registrarse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, NuevoRegistro.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                relativeLayoutRecuperar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, RestablecerContrasena.class);
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

        /*if(Utils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,101)){

        }

        if(Utils.checkPermission(this, Manifest.permission.CAMERA,102)){

        }

        if(Utils.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION,103)){

        }//*/
        //checkAndRequestPermissions();
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
        Call<ResponseLogin> mService = mApiService.authenticate(usuario, new Encryption().encryptAES(contrasena));
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
                    if(response.errorBody() != null){
                        try {
                            Utils.message(getApplicationContext(), "Error al iniciar sesión: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Utils.message(getApplicationContext(), "Error al iniciar sesión: " + e.getMessage());
                        }
                    }else {
                        Utils.message(getApplicationContext(), "Error al iniciar sesión");
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                progressDialog.dismiss();
                Utils.message(MainActivity.this, Constants.MSG_ERR_CONN);
            }
        });
    }

    private  boolean checkAndRequestPermissions() {
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),200);
            return false;
        }
        return true;
    }

    /*public void checkPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(
                this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            // Do something, when permissions not granted
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    this,Manifest.permission.CAMERA)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation

                LayoutInflater li = LayoutInflater.from(this);
                LinearLayout layoutDialog = (LinearLayout) li.inflate(R.layout.permission_layout, null);

                Button buttonAceptar = (Button) layoutDialog.findViewById(R.id.buttonAceptar);
                LinearLayout linearLayoutCamera = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutCameraPermission);
                LinearLayout linearLayoutLocation = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutLocationPermission);
                LinearLayout linearLayoutStorage = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutStoragePermission);

                if(Utils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,101)){
                    linearLayoutStorage.setVisibility(View.GONE);
                }

                if(Utils.checkPermission(this, Manifest.permission.CAMERA,102)){
                    linearLayoutCamera.setVisibility(View.GONE);
                }

                if(Utils.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION,103)){
                    linearLayoutLocation.setVisibility(View.GONE);
                }

                final AlertDialog builder = new AlertDialog.Builder(this)
                .setView(layoutDialog)
                .show();

                buttonAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                },
                                200
                        );
                        builder.dismiss();
                    }
                });
            }else{
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        200
                );
            }
        }else {
            // Do something, when permissions are already granted

        }
    }//*/

    private void nuevaInstalacionDialog(String usuario){
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        LinearLayout layoutDialog = (LinearLayout) li.inflate(R.layout.dialog_layout, null);

        TextView textViewCancelar = (TextView) layoutDialog.findViewById(R.id.buttonCancelar);
        TextView textViewAceptar = (TextView) layoutDialog.findViewById(R.id.buttonAceptar);
        TextView textViewTitulo = (TextView) layoutDialog.findViewById(R.id.textViewDialogTitulo);
        LinearLayout linearLayoutCancelar = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutCancelar);
        LinearLayout linearLayoutAceptar = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutAceptar);

        textViewTitulo.setText("La sesión iniciada corresponde a: " + usuario + ", desea iniciar sesión con otro usuario?");

        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setView(layoutDialog)
                .show();

        textViewCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        linearLayoutCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        textViewAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new NuevaInstalacion(CarteraFolios.this).execute();
                Utils.nuevaInstalacionDialog(MainActivity.this);
                dialog.dismiss();
            }
        });

        linearLayoutAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new NuevaInstalacion(CarteraFolios.this).execute();
                Utils.nuevaInstalacionDialog(MainActivity.this);
                dialog.dismiss();
            }
        });
    }
}
