package com.elektra.typhoon.registro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.elektra.typhoon.R;
import com.elektra.typhoon.adapters.AdapterReciclerViewCartera;
import com.elektra.typhoon.carteraFolios.CarteraFolios;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.objetos.request.CarteraData;
import com.elektra.typhoon.objetos.request.RequestCartera;
import com.elektra.typhoon.objetos.response.ResponseCartera;
import com.elektra.typhoon.objetos.response.ResponseNuevoUsuario;
import com.elektra.typhoon.objetos.response.ResponseValidaUsuario;
import com.elektra.typhoon.service.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.Utils;

/**
 * Proyecto: TYPHOON
 * Autor: Francis Susana Carreto Espinoza
 * Fecha: 09/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */

public class NuevoRegistro extends AppCompatActivity {

    private Button registrar;
    private EditText editTextCorreo;
    private EditText editTextPassword;
    private EditText editTextConfirmaPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        registrar = (Button) findViewById(R.id.buttonRegistrarse);
        Button validar = (Button) findViewById(R.id.buttonValidar);
        editTextCorreo = (EditText) findViewById(R.id.editTextCorreo);
        editTextPassword = (EditText) findViewById(R.id.editTextContrasena);
        editTextConfirmaPassword = (EditText) findViewById(R.id.editTextConfirmarContrasena);

        registrar.setVisibility(View.GONE);
        editTextPassword.setVisibility(View.GONE);
        editTextConfirmaPassword.setVisibility(View.GONE);

        validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(NuevoRegistro.this, CarteraFolios.class);
                startActivity(intent);//*/
                if(!editTextCorreo.getText().toString().equals("")) {
                    if (Utils.validaExpresion(editTextCorreo.getText().toString(), Constants.EMAIL_REGEX)) {
                        validarUsuario(editTextCorreo.getText().toString());
                    } else {
                        Utils.message(getApplicationContext(), "Formato de correo no válido");
                    }
                }else{
                    Utils.message(getApplicationContext(), "Debe ingresar una cuenta de correo");
                }
            }
        });

        editTextCorreo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().equals("")){
                    registrar.setVisibility(View.GONE);
                    editTextPassword.setVisibility(View.GONE);
                    editTextConfirmaPassword.setVisibility(View.GONE);
                }
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editTextPassword.getText().toString().equals("")){
                    if(!editTextConfirmaPassword.getText().toString().equals("")){
                        if(editTextPassword.getText().toString().equals(editTextConfirmaPassword.getText().toString())){
                            registrarUsuario(NuevoRegistro.this,editTextCorreo.getText().toString(),
                                    editTextPassword.getText().toString());
                        }else{
                            Utils.message(getApplicationContext(),"Las contraseñas no coinciden");
                        }
                    }else{
                        Utils.message(getApplicationContext(),"Debe confirmar la contraseña");
                    }
                }else{
                    Utils.message(getApplicationContext(),"Debe ingresar la contraseña");
                }
            }
        });
    }

    private void validarUsuario(String correo){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Validando usuario");
        progressDialog.show();

        ApiInterface mApiService = Utils.getInterfaceService();

        Call<ResponseValidaUsuario> mService = mApiService.validaUsuarioExterno(correo);
        mService.enqueue(new Callback<ResponseValidaUsuario>() {

            @Override
            public void onResponse(Call<ResponseValidaUsuario> call, Response<ResponseValidaUsuario> response) {
                progressDialog.dismiss();
                if(response.body() != null) {
                    if(response.body().getValidaUsuario().getExito()){
                        registrar.setVisibility(View.VISIBLE);
                        editTextPassword.setVisibility(View.VISIBLE);
                        editTextConfirmaPassword.setVisibility(View.VISIBLE);
                    }else{
                        Utils.message(getApplicationContext(), response.body().getValidaUsuario().getError());
                    }
                }else{
                    Utils.message(getApplicationContext(),"Error al validar usuario");
                }
            }

            @Override
            public void onFailure(Call<ResponseValidaUsuario> call, Throwable t) {
                progressDialog.dismiss();
                Utils.message(NuevoRegistro.this, Constants.MSG_ERR_CONN);
            }
        });
    }

    private void registrarUsuario(final Activity activity, String correo, String password){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando usuario");
        progressDialog.show();

        ApiInterface mApiService = Utils.getInterfaceService();

        Call<ResponseNuevoUsuario> mService = mApiService.insertarNuevoUsuario(correo,password);
        mService.enqueue(new Callback<ResponseNuevoUsuario>() {

            @Override
            public void onResponse(Call<ResponseNuevoUsuario> call, Response<ResponseNuevoUsuario> response) {
                progressDialog.dismiss();
                if(response.body() != null) {
                    if(response.body().getNuevoUsuario().getExito()){
                        Utils.message(getApplicationContext(),"Registro exitoso");
                        activity.finish();
                    }else{
                        Utils.message(getApplicationContext(), response.body().getNuevoUsuario().getError());
                    }
                }else{
                    Utils.message(getApplicationContext(),"Error al registrar usuario");
                }
            }

            @Override
            public void onFailure(Call<ResponseNuevoUsuario> call, Throwable t) {
                progressDialog.dismiss();
                Utils.message(NuevoRegistro.this, Constants.MSG_ERR_CONN);
            }
        });
    }
}
