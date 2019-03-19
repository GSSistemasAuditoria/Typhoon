package com.elektra.typhoon.registro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.elektra.typhoon.R;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.objetos.response.ResponseNuevoUsuario;
import com.elektra.typhoon.objetos.response.ResponseValidaUsuario;
import com.elektra.typhoon.service.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.elektra.typhoon.utils.Utils;

import java.io.IOException;
import java.text.Normalizer;

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
    private LinearLayout linearLayoutContrasena;
    private LinearLayout linearLayoutConfirmarContrasena;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_registro);
        setContentView(R.layout.registro_layout);

        registrar = (Button) findViewById(R.id.buttonRegistrarse);
        Button validar = (Button) findViewById(R.id.buttonValidar);
        editTextCorreo = (EditText) findViewById(R.id.editTextCorreo);
        editTextPassword = (EditText) findViewById(R.id.editTextContrasena);
        editTextConfirmaPassword = (EditText) findViewById(R.id.editTextConfirmarContrasena);
        linearLayoutContrasena = (LinearLayout) findViewById(R.id.linearLayoutContrasena);
        linearLayoutConfirmarContrasena = (LinearLayout) findViewById(R.id.linearLayoutConfirmarContrasena);

        registrar.setVisibility(View.GONE);
        linearLayoutContrasena.setVisibility(View.GONE);
        linearLayoutConfirmarContrasena.setVisibility(View.GONE);

        editTextPassword.setLongClickable(false);
        editTextPassword.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });

        editTextConfirmaPassword.setLongClickable(false);
        editTextConfirmaPassword.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });

        validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(NuevoRegistro.this, CarteraFolios.class);
                startActivity(intent);//*/
                //if(!editTextCorreo.getText().toString().equals("")) {
                if(!Normalizer.normalize(editTextCorreo.getText().toString(), Normalizer.Form.NFD).equals("")) {
                    //if (Utils.validaExpresion(editTextCorreo.getText().toString(), Constants.EMAIL_REGEX)) {
                    if (Utils.validaExpresion(Normalizer.normalize(editTextCorreo.getText().toString(), Normalizer.Form.NFD), Constants.EMAIL_REGEX)) {
                        //validarUsuario(editTextCorreo.getText().toString());
                        validarUsuario(Normalizer.normalize(editTextCorreo.getText().toString(), Normalizer.Form.NFD));
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
                    linearLayoutContrasena.setVisibility(View.GONE);
                    linearLayoutConfirmarContrasena.setVisibility(View.GONE);
                }
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(!editTextPassword.getText().toString().equals("")){
                if(!Normalizer.normalize(editTextPassword.getText().toString(), Normalizer.Form.NFD).equals("")){
                    //if(!editTextConfirmaPassword.getText().toString().equals("")){
                    if(!Normalizer.normalize(editTextConfirmaPassword.getText().toString(), Normalizer.Form.NFD).equals("")){
                        //if(editTextPassword.getText().toString().equals(editTextConfirmaPassword.getText().toString())){
                        if(Normalizer.normalize(editTextPassword.getText().toString(), Normalizer.Form.NFD).
                                equals(Normalizer.normalize(editTextConfirmaPassword.getText().toString(), Normalizer.Form.NFD))){
                            //registrarUsuario(NuevoRegistro.this,editTextCorreo.getText().toString(), editTextPassword.getText().toString());
                            registrarUsuario(NuevoRegistro.this,Normalizer.normalize(editTextCorreo.getText().toString(), Normalizer.Form.NFD),
                                    Normalizer.normalize(editTextPassword.getText().toString(), Normalizer.Form.NFD));
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

        final ProgressDialog progressDialog = Utils.typhoonLoader(NuevoRegistro.this,"Validando usuario...");

        ApiInterface mApiService = Utils.getInterfaceService();

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
        Call<ResponseValidaUsuario> mService = mApiService.validaUsuarioExterno(sharedPreferences.getString(Constants.SP_JWT_TAG,""),correo);
        mService.enqueue(new Callback<ResponseValidaUsuario>() {

            @Override
            public void onResponse(Call<ResponseValidaUsuario> call, Response<ResponseValidaUsuario> response) {
                progressDialog.dismiss();
                if(response != null) {
                    if (response.body() != null) {
                        if (response.body().getValidaUsuario().getExito()) {
                            registrar.setVisibility(View.VISIBLE);
                            linearLayoutContrasena.setVisibility(View.VISIBLE);
                            linearLayoutConfirmarContrasena.setVisibility(View.VISIBLE);
                        } else {
                            Utils.message(getApplicationContext(), response.body().getValidaUsuario().getError());
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                Utils.message(getApplicationContext(), "Error al validar usuario: " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                                Utils.message(getApplicationContext(), "Error al validar usuario: " + e.getMessage());
                            }
                        } else {
                            Utils.message(getApplicationContext(), "Error al validar usuario");
                        }
                    }
                }else{
                    Utils.message(getApplicationContext(), "Error al validar usuario");
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

        final ProgressDialog progressDialog = Utils.typhoonLoader(NuevoRegistro.this,"Registrando usuario...");

        ApiInterface mApiService = Utils.getInterfaceService();

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
        Call<ResponseNuevoUsuario> mService = mApiService.insertarNuevoUsuario(sharedPreferences.getString(Constants.SP_JWT_TAG,""),correo,password);
        mService.enqueue(new Callback<ResponseNuevoUsuario>() {

            @Override
            public void onResponse(Call<ResponseNuevoUsuario> call, Response<ResponseNuevoUsuario> response) {
                progressDialog.dismiss();
                if(response != null) {
                    if (response.body() != null) {
                        if (response.body().getNuevoUsuario().getExito()) {
                            Utils.message(getApplicationContext(), "Registro exitoso");
                            activity.finish();
                        } else {
                            Utils.message(getApplicationContext(), response.body().getNuevoUsuario().getError());
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                Utils.message(getApplicationContext(), "Error al registrar usuario: " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                                Utils.message(getApplicationContext(), "Error al registrar usuario: " + e.getMessage());
                            }
                        } else {
                            Utils.message(getApplicationContext(), "Error al registrar usuario");
                        }
                    }
                }else{
                    Utils.message(getApplicationContext(), "Error al registrar usuario");
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
