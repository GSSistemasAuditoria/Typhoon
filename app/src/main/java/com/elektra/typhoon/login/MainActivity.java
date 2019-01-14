package com.elektra.typhoon.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elektra.typhoon.R;
import com.elektra.typhoon.carteraFolios.CarteraFolios;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.service.ResponseLogin;
import com.elektra.typhoon.registro.NuevoRegistro;
import com.elektra.typhoon.registro.RestablecerContrasena;
import com.elektra.typhoon.service.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        setContentView(R.layout.activity_main);

        editTextUsuario = (EditText)findViewById(R.id.editTextUsuario);
        editTextContrasena = (EditText)findViewById(R.id.editTextContrasena);
        entrar = (Button) findViewById(R.id.buttonEntrar);
        registro = (Button)findViewById(R.id.buttonRegistro);

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario = editTextUsuario.getText().toString();
                contrasena = editTextContrasena.getText().toString();
                if(!usuario.equals("")){
                    if(!contrasena.equals("")){
                        iniciarSesion(usuario,contrasena);
                    }
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
    }
    private ApiInterface getInterfaceService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_PUBLIC + "ValidarEmpleado")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        return mInterfaceService;
    }

    private void iniciarSesion(final String usuario, String contrasena){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Iniciando...");
        progressDialog.show();

        ApiInterface mApiService = this.getInterfaceService();
        Call<ResponseLogin> mService = mApiService.authenticate(usuario, contrasena);
        mService.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                progressDialog.dismiss();
                if(response.body().getExito()) {
                    Toast.makeText(getApplicationContext(), "Inicio exitoso", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }
}
