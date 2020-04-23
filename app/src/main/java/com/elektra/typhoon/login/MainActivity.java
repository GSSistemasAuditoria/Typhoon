package com.elektra.typhoon.login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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
import com.elektra.typhoon.database.NotificacionesDBMethods;
import com.elektra.typhoon.database.TyphoonDataBase;
import com.elektra.typhoon.database.UsuarioDBMethods;
import com.elektra.typhoon.encryption.Encryption;
import com.elektra.typhoon.objetos.response.Barco;
import com.elektra.typhoon.objetos.response.CatalogosTyphoonResponse;
import com.elektra.typhoon.objetos.response.CerrarSesionResponse;
import com.elektra.typhoon.objetos.response.Configuracion;
import com.elektra.typhoon.objetos.response.EstatusEvidencia;
import com.elektra.typhoon.objetos.response.EstatusRevision;
import com.elektra.typhoon.objetos.response.EtapaEvidencia;
import com.elektra.typhoon.objetos.response.EtapaSubAnexo;
import com.elektra.typhoon.objetos.response.GenericResponseVO;
import com.elektra.typhoon.objetos.response.LoginLlaveMaestraVO;
import com.elektra.typhoon.objetos.response.Notificacion;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.ResponseNotificaciones;
import com.elektra.typhoon.objetos.response.RolUsuario;
import com.elektra.typhoon.objetos.response.TipoRespuesta;
import com.elektra.typhoon.registro.NuevoRegistro;
import com.elektra.typhoon.registro.RestablecerContrasena;
import com.elektra.typhoon.service.ApiInterface;
import com.elektra.typhoon.utils.Utils;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
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
    public static final String TAG = MainActivity.class.getName();

    private EditText editTextUsuario;
    private EditText editTextContrasena;
    private Button entrar;
    private Button registro;
    private Button registrarse;
    private Button restablecerContrasena;
    private ConstraintLayout layoutDialog;
    private String usuario;
    private String contrasena;
    private String firebaseToken;
    private SharedPreferences sharedPrefs;

    private Button btnInterno;
    private Button btnExterno;

    private void getTokenFormUrl(String token) {
        setFirebaseToken();
        iniciarSesion(null, null, firebaseToken, token);
    }

    public void setFirebaseToken() {
        if (sharedPrefs.contains(Constants.SP_FIREBASE_TOKEN)) {
            String firebase = Normalizer.normalize(new Encryption().decryptAES(sharedPrefs.getString(Constants.SP_FIREBASE_TOKEN, "")), Normalizer.Form.NFD);
            if (!firebase.equals("")) {
                firebaseToken = firebase;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.login_layout);
        Log.e(TAG, new Encryption().encryptAES(Constants.URL_PUBLIC));
        sharedPrefs = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
        Uri data = null;
        if (getIntent() != null && getIntent().getData() != null) {
            data = getIntent().getData();
        }
        if (data != null && !TextUtils.isEmpty(data.getScheme())) {
            if ("typhoon".equals(data.getScheme())) {
                String code = null;
                for (String mValue : data.toString().split("&")) {
                    String[] values = mValue.split("=");
                    if (values[0].equals("access_token")) {
                        code = values[1];
                        break;
                    }
                }
                if (!TextUtils.isEmpty(code)) {
                    getTokenFormUrl(code);
                }
            }
        }

        Utils.deviceLockVerification(this);
        Utils.installerVerification(this);
        //Utils.isDeviceRooted();

        editTextUsuario = (EditText) findViewById(R.id.editTextUsuario);
        editTextContrasena = (EditText) findViewById(R.id.editTextContrasena);
        entrar = (Button) findViewById(R.id.buttonEntrar);
        registro = (Button) findViewById(R.id.buttonRegistro);

        btnInterno = findViewById(R.id.btnInterno);
        btnExterno = findViewById(R.id.btnExterno);
        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnInterno:
                        HttpUrl authorizeUrl = HttpUrl.parse(Constants.URL_DSI + "oauth/nam/authz")
                                .newBuilder()
                                .addQueryParameter("response_type", getString(R.string.response_type))
                                .addQueryParameter(new Encryption().decryptAES(getString(R.string.client_text)), getString(R.string.client))
                                .addQueryParameter("redirect_uri", getString(R.string.redirect_uri))
                                .addQueryParameter("scope", getString(R.string.scope))
                                .addQueryParameter("acr_values", getString(R.string.acr_values))
                                .addQueryParameter("client_id", getString(R.string.client_id))
                                .build();
                        Intent mIntent = new Intent(Intent.ACTION_VIEW);
                        mIntent.setData(Uri.parse(String.valueOf(authorizeUrl.url())));
                        startActivity(mIntent);
                        break;
                    case R.id.btnExterno:
                        /*HttpUrl authorizeUrl1 = HttpUrl.parse(Constants.URL_DSI + "jsp/logoutSuccess_latest.jsp")
                                .newBuilder()
                                .build();
                        Intent mIntent1 = new Intent(Intent.ACTION_VIEW);
                        mIntent1.setData(Uri.parse(String.valueOf(authorizeUrl1.url())));
                        startActivity(mIntent1);*/
                        findViewById(R.id.clIngresar).setVisibility(View.GONE);
                        findViewById(R.id.clExterno).setVisibility(View.VISIBLE);
                        break;
                    default:
                        Log.w(TAG, String.valueOf(v.getId()));
                        break;
                }
            }
        };
        btnInterno.setOnClickListener(mOnClickListener);
        btnExterno.setOnClickListener(mOnClickListener);
        float scale = getResources().getConfiguration().fontScale;
        System.out.println("Escala del texto: " + scale);

        if (sharedPrefs.contains(Constants.SP_LOGIN_TAG)) {
            if (sharedPrefs.getBoolean(Constants.SP_LOGIN_TAG, false)) {
                Intent intent = new Intent(getApplicationContext(), CarteraFolios.class);
                startActivity(intent);
                finish();
            } else {
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
                ResponseLogin.Usuario usuarioDB = new UsuarioDBMethods(getApplicationContext()).readUsuario();
                usuario = Normalizer.normalize(editTextUsuario.getText().toString().toLowerCase().trim(), Normalizer.Form.NFD);
                contrasena = Normalizer.normalize(editTextContrasena.getText().toString(), Normalizer.Form.NFD);
                setFirebaseToken();
                //usuario = editTextUsuario.getText().toString();
                //contrasena = editTextContrasena.getText().toString();
                if (usuario.equals("")) {
                    Utils.message(getApplicationContext(), "Debe introducir id de empleado o su correo");
                    return;
                }
                if (contrasena.equals("")) {
                    Utils.message(getApplicationContext(), "Debe introducir la contraseña");
                    return;
                }
                if (usuarioDB != null) {
                    if (!usuarioDB.getCorreo().trim().equals(usuario.trim()) && !usuarioDB.getIdUsuario().trim().equals(usuario.trim())) {
                        nuevaInstalacionDialog(usuarioDB.getNombre(), usuarioDB.getIdUsuario(),false);
                        return;
                    }
                }
                iniciarSesion(usuario, contrasena, firebaseToken, null);
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

        //Utils.checkPermissionTest(this);

        Encryption encryption = new Encryption();

        if (!sharedPrefs.contains(Constants.SP_FIREBASE_TOKEN)) {
            String newToken = FirebaseInstanceId.getInstance().getToken();
            if (newToken != null) {
                if (!newToken.equals("")) {
                    SharedPreferences.Editor ed;
                    ed = sharedPrefs.edit();
                    ed.putString(Constants.SP_FIREBASE_TOKEN, encryption.encryptAES(newToken));
                    ed.commit();
                    System.out.println("Recuperar token: " + newToken);
                }
            }
        } else {
            String firebase = Normalizer.normalize(new Encryption().decryptAES(sharedPrefs.getString(Constants.SP_FIREBASE_TOKEN, "")), Normalizer.Form.NFD);
            System.out.println("Token firebase: " + firebase);
            if (firebase != null) {
                if (firebase.equals("")) {
                    String newToken = FirebaseInstanceId.getInstance().getToken();
                    SharedPreferences.Editor ed;
                    ed = sharedPrefs.edit();
                    ed.putString(Constants.SP_FIREBASE_TOKEN, encryption.encryptAES(newToken));
                    ed.commit();
                    System.out.println("Recuperar token: " + newToken);
                }
            } else {
                String newToken = FirebaseInstanceId.getInstance().getToken();
                if (newToken != null) {
                    if (!newToken.equals("")) {
                        SharedPreferences.Editor ed;
                        ed = sharedPrefs.edit();
                        ed.putString(Constants.SP_FIREBASE_TOKEN, encryption.encryptAES(newToken));
                        ed.commit();
                    }
                }
            }
        }
        usuario = null;
        contrasena = null;
    }

    private ApiInterface getInterfaceService() {
        Retrofit retrofit = new Retrofit.Builder()
                //.baseUrl(Constants.URL_PUBLIC)
                .baseUrl(new Encryption().decryptAES(Constants.URL_PUBLIC))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        return mInterfaceService;
    }

    private void iniciarSesion(final String usuario, String contrasena, String token, String tokenDSI) {
        final ProgressDialog progressDialog = Utils.typhoonLoader(MainActivity.this, "Iniciando sesión...");
        final LoginLlaveMaestraVO mLlaveMaestraVO = new LoginLlaveMaestraVO();
        mLlaveMaestraVO.setDatosRequest(mLlaveMaestraVO.new DataLoginMasterKey());

        if (contrasena != null && !contrasena.equals("")) {
            mLlaveMaestraVO.getDatosRequest().setUSER_NAME(new Encryption().encryptAES(usuario));
            mLlaveMaestraVO.getDatosRequest().setLLAVE_MAESTRA(new Encryption().encryptAES(contrasena));
        }
        mLlaveMaestraVO.getDatosRequest().setOAUTHTOKEN(tokenDSI);
        mLlaveMaestraVO.getDatosRequest().setFB_TOKEN(token);
        mLlaveMaestraVO.getDatosRequest().setIS_MOVIL(true);
        mLlaveMaestraVO.getDatosRequest().setIP_CLIENT(Utils.getIPAddress());

        final ApiInterface mInterfaceService = getInterfaceService();
        Call<GenericResponseVO> mServiceValida = mInterfaceService.validaSesion(mLlaveMaestraVO);
        mServiceValida.enqueue(new Callback<GenericResponseVO>() {
            @Override
            public void onResponse(Call<GenericResponseVO> call, Response<GenericResponseVO> response) {
                progressDialog.dismiss();
                Log.e(TAG, response.raw().toString());
                if (response != null) {
                    if (response.body() != null) {
                        if (!response.body().getmData().isExito()) {
                            Call<ResponseLogin> mServiceLogin = mInterfaceService.loginLlaveMaestra(mLlaveMaestraVO.getDatosRequest().getIP_CLIENT(), mLlaveMaestraVO);
                            getResponseServiceLogin(mServiceLogin);
                        } else {
                            cerrarSesionDialog(response.body().getmData().getData().getID_USUARIO());
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                Utils.message(getApplicationContext(), "Error al iniciar sesión: " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                                Utils.message(getApplicationContext(), "Error al iniciar sesión: " + e.getMessage());
                            }
                        } else {
                            Utils.message(getApplicationContext(), "Error al iniciar sesión");
                        }
                    }
                } else {
                    Utils.message(getApplicationContext(), "Error al iniciar sesión");
                }
            }

            @Override
            public void onFailure(Call<GenericResponseVO> call, Throwable t) {
                progressDialog.dismiss();
                Utils.message(MainActivity.this, Constants.MSG_ERR_CONN);
            }
        });

    }

    private void getResponseServiceLogin(Call<ResponseLogin> mService) {
        final ProgressDialog progressDialog = Utils.typhoonLoader(MainActivity.this, "Iniciando sesión...");
        mService.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                progressDialog.dismiss();
                Log.e(TAG, response.raw().toString());
                if (response != null) {
                    if (response.body() != null) {
                        if (response.body().getValidarEmpleado().getExito()) {
                            //try {
                            ResponseLogin.Usuario usuarioDB = new UsuarioDBMethods(getApplicationContext()).readUsuario();
                            if (usuarioDB != null) {
                                if(usuarioDB.getInterno()){
                                    if (!usuarioDB.getIdUsuario().trim().equals(response.body().getValidarEmpleado().getUsuario().getIdUsuario())) {
                                        nuevaInstalacionDialog(usuarioDB.getNombre(), usuarioDB.getIdUsuario(),response.body().getValidarEmpleado().getUsuario().getInterno());
                                        return;
                                    }
                                }else {
                                    if (!usuarioDB.getCorreo().trim().equals(response.body().getValidarEmpleado().getUsuario().getCorreo())) {
                                        nuevaInstalacionDialog(usuarioDB.getNombre(), usuarioDB.getIdUsuario(),response.body().getValidarEmpleado().getUsuario().getInterno());
                                        return;
                                    }
                                }
                            }
                            new UsuarioDBMethods(getApplicationContext()).createUsuario(response.body().getValidarEmpleado().getUsuario());
                            SharedPreferences sharedPrefs = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
                            sharedPrefs.edit().putBoolean(Constants.SP_LOGIN_TAG, true).apply();
                            sharedPrefs.edit().putString(Constants.SP_JWT_TAG, new Encryption().encryptAES(response.body().getValidarEmpleado().getUsuario().getJwt())).apply();

                            BarcoDBMethods barcoDBMethods = new BarcoDBMethods(getApplicationContext());
                            if (barcoDBMethods.readBarcos().size() == 0) {
                                //Utils.descargaCatalogos(MainActivity.this, 1);
                                descargaCatalogos(1, response.body().getValidarEmpleado().getUsuario());
                            } else {
                                Intent intent = new Intent(MainActivity.this, CarteraFolios.class);
                                startActivity(intent);
                                finish();
                            }
                        /*}catch (NullPointerException e){
                            Utils.message(getApplicationContext(),"No se pudieron guardar los datos del usuario: " + e.getMessage());
                            e.printStackTrace();
                        }//*/
                        } else {
                            if (response.body().getValidarEmpleado().getUsuario() != null) {
                                if (response.body().getValidarEmpleado().getUsuario().getIdUsuario().equals("1")) {
                                    cerrarSesionDialog(response.body().getValidarEmpleado().getUsuario().getIdUsuario());
                                } else {
                                    Utils.message(getApplicationContext(), response.body().getValidarEmpleado().getError());
                                }
                            } else {
                                Utils.message(getApplicationContext(), response.body().getValidarEmpleado().getError());
                            }
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                Utils.message(getApplicationContext(), "Error al iniciar sesión: " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                                Utils.message(getApplicationContext(), "Error al iniciar sesión: " + e.getMessage());
                            }
                        } else {
                            Utils.message(getApplicationContext(), "Error al iniciar sesión");
                        }
                    }
                } else {
                    Utils.message(getApplicationContext(), "Error al iniciar sesión");
                }
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                progressDialog.dismiss();
                Utils.message(MainActivity.this, Constants.MSG_ERR_CONN);
            }
        });
    }

    private boolean checkAndRequestPermissions() {
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
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 200);
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

    private void nuevaInstalacionDialog(final String usuario, final String idUsuario, final boolean interno) {
        final LayoutInflater li = LayoutInflater.from(MainActivity.this);
        LinearLayout layoutDialog = (LinearLayout) li.inflate(R.layout.dialog_layout, null);
        TextView textViewCancelar = layoutDialog.findViewById(R.id.buttonCancelar);
        TextView textViewAceptar = layoutDialog.findViewById(R.id.buttonAceptar);
        TextView textViewTitulo = layoutDialog.findViewById(R.id.textViewDialogTitulo);
        LinearLayout linearLayoutCancelar = layoutDialog.findViewById(R.id.linearLayoutCancelar);
        LinearLayout linearLayoutAceptar = layoutDialog.findViewById(R.id.linearLayoutAceptar);

        textViewTitulo.setText("La sesión iniciada corresponde a: " + usuario + ", desea iniciar sesión con otro usuario?");

        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setView(layoutDialog)
                .show();

        final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.buttonAceptar || v.getId() == R.id.linearLayoutAceptar) {
                    LinearLayout layoutDialog = (LinearLayout) li.inflate(R.layout.dialog_nueva_instalacion_layout, null);
                    TextView textViewCancelar = layoutDialog.findViewById(R.id.buttonCancelar);
                    TextView textViewAceptar = layoutDialog.findViewById(R.id.buttonAceptar);
                    LinearLayout linearLayoutCancelar = layoutDialog.findViewById(R.id.linearLayoutCancelar);
                    LinearLayout linearLayoutAceptar = layoutDialog.findViewById(R.id.linearLayoutAceptar);

                    final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setView(layoutDialog)
                            .show();

                    View.OnClickListener mOnClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v.getId() == R.id.buttonAceptar || v.getId() == R.id.linearLayoutAceptar) {
                                cerrarSesionService(idUsuario, true,interno);
                            }
                            dialog.dismiss();
                        }
                    };

                    textViewCancelar.setOnClickListener(mOnClickListener);
                    linearLayoutCancelar.setOnClickListener(mOnClickListener);
                    textViewAceptar.setOnClickListener(mOnClickListener);
                    linearLayoutAceptar.setOnClickListener(mOnClickListener);
                }
                dialog.dismiss();
            }
        };

        textViewCancelar.setOnClickListener(mOnClickListener);
        linearLayoutCancelar.setOnClickListener(mOnClickListener);
        textViewAceptar.setOnClickListener(mOnClickListener);
        linearLayoutAceptar.setOnClickListener(mOnClickListener);
    }

    private void cerrarSesionDialog(final String idUsuario) {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        LinearLayout layoutDialog = (LinearLayout) li.inflate(R.layout.dialog_layout, null);

        TextView textViewCancelar = (TextView) layoutDialog.findViewById(R.id.buttonCancelar);
        TextView textViewAceptar = (TextView) layoutDialog.findViewById(R.id.buttonAceptar);
        TextView textViewTitulo = (TextView) layoutDialog.findViewById(R.id.textViewDialogTitulo);
        LinearLayout linearLayoutCancelar = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutCancelar);
        LinearLayout linearLayoutAceptar = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutAceptar);

        textViewTitulo.setText("Ya existe un usuario firmado con estas credenciales, desea cerrar la sesión?");

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
                //iniciarSesion(usuario, contrasena, firebaseToken, 1);
                cerrarSesionService(idUsuario, false,false);
                dialog.dismiss();
            }
        });
    }

    private void cerrarSesionService(final String idUsuario, final boolean borrarData,final boolean interno) {
        Call<CerrarSesionResponse> mServiceCerrarSesion = getInterfaceService().cerrarSesion(idUsuario);
        mServiceCerrarSesion.enqueue(new Callback<CerrarSesionResponse>() {
            @Override
            public void onResponse(Call<CerrarSesionResponse> call, Response<CerrarSesionResponse> response) {
                if (response != null) {
                    if (response.body() != null) {
                        if (response.body().getCerrarSesion().getExito()) {
                            if (borrarData) {
                                sharedPrefs.edit().clear().apply();
                                new TyphoonDataBase(MainActivity.this).deleteAll();
                            }
                            if(interno){
                                btnInterno.performClick();
                            }else{
                                entrar.performClick();
                            }
                        } else {
                            //progressDialog.dismiss();
                            Utils.message(MainActivity.this, response.body().getCerrarSesion().getError());
                        }//*/
                    } else {
                        //progressDialog.dismiss();
                        if (response.errorBody() != null) {
                            try {
                                String mensaje = "" + response.errorBody().string();
                                int code = response.code();
                                //if(!mensaje.contains("No tiene permiso para ver")) {
                                if (code != 401) {
                                    //Utils.message(getApplicationContext(), "Error al descargar folios: " + response.errorBody().string());
                                    Utils.message(MainActivity.this, "No se pudo realizar la nueva instalación: " + response.errorBody().string());
                                } else {
                                    Utils.message(MainActivity.this, "La sesion ha expirado");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Utils.message(MainActivity.this, "No se pudo realizar la nueva instalación");
                        }
                    }
                } else {
                    //progressDialog.dismiss();
                    Utils.message(MainActivity.this, "No se pudo realizar la nueva instalación");
                }
            }

            @Override
            public void onFailure(Call<CerrarSesionResponse> call, Throwable t) {

            }
        });
    }

    private void descargaCatalogos(final int opcion, ResponseLogin.Usuario mUsuario) {

        String titulo = "";
        if (opcion == 1) {
            titulo = "Descargando catálogos...";
        } else {
            titulo = "Actualizando catálogos...";
        }

        final ProgressDialog progressDialog = Utils.typhoonLoader(MainActivity.this, titulo);

        final Encryption encryption = new Encryption();

        try {
            ApiInterface mApiService = Utils.getInterfaceService();
            final SharedPreferences sharedPreferences = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
            Call<CatalogosTyphoonResponse> mService = mApiService.catalogosTyphoon(Utils.getIPAddress(), encryption.decryptAES(sharedPreferences.getString(Constants.SP_JWT_TAG, "")), mUsuario.getIdUsuario());
            mService.enqueue(new Callback<CatalogosTyphoonResponse>() {
                @Override
                public void onResponse(Call<CatalogosTyphoonResponse> call, Response<CatalogosTyphoonResponse> response) {
                    if (response != null) {
                        Log.e(TAG, response.raw().toString());
                        if (response.body() != null) {
                            if (response.body().getCatalogos().getExito()) {
                                //try {
                                //Log.e(MainActivity.class.getName(), response.raw().toString());
                                String jwt = encryption.encryptAES(response.headers().get("Authorization"));

                                BarcoDBMethods barcoDBMethods = new BarcoDBMethods(MainActivity.this);
                                CatalogosDBMethods catalogosDBMethods = new CatalogosDBMethods(MainActivity.this);
                                if (response.body().getCatalogos().getCatalogosData().getListBarcos() != null) {
                                    barcoDBMethods.deleteBarco();
                                    for (Barco catalogoBarco : response.body().getCatalogos().getCatalogosData().getListBarcos()) {
                                        barcoDBMethods.createBarco(catalogoBarco);
                                    }
                                }
                                if (response.body().getCatalogos().getCatalogosData().getListEstatusEvidencia() != null) {
                                    catalogosDBMethods.deleteEstatusEvidencia();
                                    for (EstatusEvidencia estatusEvidencia : response.body().getCatalogos().getCatalogosData().getListEstatusEvidencia()) {
                                        catalogosDBMethods.createEstatusEvidencia(estatusEvidencia);
                                    }
                                }
                                if (response.body().getCatalogos().getCatalogosData().getListEtapasEvidencia() != null) {
                                    catalogosDBMethods.deleteEtapaEvidencia();
                                    for (EtapaEvidencia etapaEvidencia : response.body().getCatalogos().getCatalogosData().getListEtapasEvidencia()) {
                                        catalogosDBMethods.createEtapaEvidencia(etapaEvidencia);
                                    }
                                }
                                if (response.body().getCatalogos().getCatalogosData().getListTiposRespuesta() != null) {
                                    catalogosDBMethods.deleteTipoRespuesta();
                                    for (TipoRespuesta tipoRespuesta : response.body().getCatalogos().getCatalogosData().getListTiposRespuesta()) {
                                        catalogosDBMethods.createTipoRespuesta(tipoRespuesta);
                                    }
                                }
                                if (response.body().getCatalogos().getCatalogosData().getListEstatusRevision() != null) {
                                    catalogosDBMethods.deleteEstatusRevision();
                                    for (EstatusRevision estatusRevision : response.body().getCatalogos().getCatalogosData().getListEstatusRevision()) {
                                        catalogosDBMethods.createEstatusRevision(estatusRevision);
                                    }
                                }
                                if (response.body().getCatalogos().getCatalogosData().getListRolesUsuario() != null) {
                                    catalogosDBMethods.deleteRolesUsuario();
                                    for (RolUsuario rolUsuario : response.body().getCatalogos().getCatalogosData().getListRolesUsuario()) {
                                        catalogosDBMethods.createRolUsuario(rolUsuario);
                                    }
                                }
                                if (response.body().getCatalogos().getCatalogosData().getListConfiguracion() != null) {
                                    //catalogosDBMethods.deleteRolesUsuario();
                                    SharedPreferences sharedPrefs = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
                                    SharedPreferences.Editor ed;
                                    ed = sharedPrefs.edit();
                                    for (Configuracion configuracion : response.body().getCatalogos().getCatalogosData().getListConfiguracion()) {
                                        if (configuracion.getConfiguracion().equals("LimiteEvidencias")) {
                                            ed.putString(Constants.SP_LIMITE_EVIDENCIAS, encryption.encryptAES(configuracion.getArgumento()));
                                            ed.apply();
                                        }
                                        if (configuracion.getConfiguracion().equals("Gps")) {
                                            ed.putString(Constants.SP_GPS_FLAG, encryption.encryptAES(configuracion.getArgumento()));
                                            ed.apply();
                                        }
                                        if (configuracion.getConfiguracion().equals("GpsConfig")) {
                                            ed.putString(Constants.SP_GPS_GEOCERCA, encryption.encryptAES(configuracion.getArgumento()));
                                            ed.apply();
                                        }
                                        if (configuracion.getConfiguracion().equals("ValidaFechaEvidencias")) {
                                            ed.putString(Constants.SP_VALIDA_FECHA, encryption.encryptAES(configuracion.getArgumento()));
                                            ed.apply();
                                        }
                                        if (configuracion.getConfiguracion().equals(Constants.SP_SIZE_EVIDENCIAS)){
                                            ed.putInt(Constants.SP_SIZE_EVIDENCIAS, Integer.parseInt(configuracion.getArgumento()));
                                            ed.apply();
                                        }
                                        if (configuracion.getConfiguracion().equals(Constants.WIDTH_EVIDENCIA)){
                                            ed.putInt(Constants.WIDTH_EVIDENCIA, Integer.parseInt(configuracion.getArgumento()));
                                            ed.apply();
                                        }
                                        if (configuracion.getConfiguracion().equals(Constants.HEIGTH_EVIDENCIA)){
                                            ed.putInt(Constants.HEIGTH_EVIDENCIA, Integer.parseInt(configuracion.getArgumento()));
                                            ed.apply();
                                        }
                                    }
                                }
                                if (response.body().getCatalogos().getCatalogosData().getListEtapasSubAnexo() != null) {
                                    catalogosDBMethods.deleteEtapaSubAnexo();
                                    for (EtapaSubAnexo etapaSubAnexo : response.body().getCatalogos().getCatalogosData().getListEtapasSubAnexo()) {
                                        catalogosDBMethods.createEtapaSubAnexo(etapaSubAnexo);
                                    }
                                }
                                if (response.body().getCatalogos().getCatalogosData().getCatalogoAnios() != null) {
                                    if (response.body().getCatalogos().getCatalogosData().getCatalogoAnios().getAnios() != null) {
                                        catalogosDBMethods.deleteAnios();
                                        for (Integer integer : response.body().getCatalogos().getCatalogosData().getCatalogoAnios().getAnios()) {
                                            catalogosDBMethods.createAnio(integer);
                                        }
                                    }
                                }
                                progressDialog.dismiss();
                                Utils.message(MainActivity.this, "Catálogos descargados");

                                //ResponseLogin.Usuario usuarioData = new UsuarioDBMethods(getApplicationContext()).readUsuario();
                                //obtenerNotificaciones(usuarioData.getIdrol(),opcion);

                                if (opcion == 1) {
                                    Intent intent = new Intent(MainActivity.this, CarteraFolios.class);
                                    startActivity(intent);
                                    finish();
                                }//*/

                            /*} catch (NullPointerException e) {
                                progressDialog.dismiss();
                                Utils.message(activity, "Error al guardar los catálogos: " + e.getMessage());
                                e.printStackTrace();
                            }//*/
                            } else {
                                progressDialog.dismiss();
                                Utils.message(MainActivity.this, response.body().getCatalogos().getError());
                            }
                        } else {
                            progressDialog.dismiss();
                            if (response.errorBody() != null) {
                                try {
                                    String mensaje = "" + response.errorBody().string();
                                    int code = response.code();
                                    //if(!mensaje.contains("No tiene permiso para ver")) {
                                    if (code != 401) {
                                        Utils.message(MainActivity.this, "Error al descargar catálogos: " + response.errorBody().string());
                                    } else {
                                        sharedPreferences.edit().putBoolean(Constants.SP_LOGIN_TAG, false).apply();
                                        Utils.message(getApplicationContext(), "La sesión ha expirado");
                                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    //Utils.message(MainActivity.this, "Error al descargar catálogos: " + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Utils.message(MainActivity.this, "Error al descargar catálogos: " + e.getMessage());
                                }
                            } else {
                                Utils.message(MainActivity.this, "Error al descargar catálogos");
                            }
                        }
                    } else {
                        Utils.message(MainActivity.this, "Error al descargar catálogos");
                    }
                }

                @Override
                public void onFailure(Call<CatalogosTyphoonResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Utils.message(MainActivity.this, Constants.MSG_ERR_CONN);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            Utils.message(MainActivity.this, "Error al descargar catálogos: " + e.getMessage());
        } catch (Error e) {
            progressDialog.dismiss();
            Utils.message(MainActivity.this, "Error al descargar catálogos: ");
        }//*/
    }

    private void obtenerNotificaciones(int idRol, final int opcion) {

        final ProgressDialog progressDialog = Utils.typhoonLoader(MainActivity.this, "Descargando notificaciones...");

        ApiInterface mApiService = Utils.getInterfaceService();

        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);

        final NotificacionesDBMethods notificacionesDBMethods = new NotificacionesDBMethods(MainActivity.this);

        //Call<ResponseCartera> mService = mApiService.carteraRevisiones(requestCartera);
        Call<ResponseNotificaciones> mService = mApiService.getNotificaciones(new Encryption().decryptAES(sharedPreferences.getString(Constants.SP_JWT_TAG, "")), idRol);
        mService.enqueue(new Callback<ResponseNotificaciones>() {

            @Override
            public void onResponse(Call<ResponseNotificaciones> call, Response<ResponseNotificaciones> response) {
                progressDialog.dismiss();
                if (response != null) {
                    if (response.body() != null) {
                        if (response.body().getNotificaciones().getExito()) {

                            String jwt = new Encryption().encryptAES(response.headers().get("Authorization"));
                            sharedPreferences.edit().putString(Constants.SP_JWT_TAG, jwt).apply();

                            if (response.body().getNotificaciones().getNotificaciones() != null) {
                                for (Notificacion notificacion : response.body().getNotificaciones().getNotificaciones()) {
                                    notificacionesDBMethods.createNotificacion(notificacion);
                                }
                                //loadNotificaciones(usuario.getIdrol());
                                if (opcion == 1) {
                                    Intent intent = new Intent(MainActivity.this, CarteraFolios.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        } else {
                            Utils.message(getApplicationContext(), response.body().getNotificaciones().getError());
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String mensaje = "" + response.errorBody().string();
                                int code = response.code();
                                //if(!mensaje.contains("No tiene permiso para ver")) {
                                if (code != 401) {
                                    Utils.message(getApplicationContext(), "Error al descargar notificaciones: " + response.errorBody().string());
                                } else {
                                    sharedPreferences.edit().putBoolean(Constants.SP_LOGIN_TAG, false).apply();
                                    Utils.message(getApplicationContext(), "La sesión ha expirado");
                                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                //Utils.message(getApplicationContext(), "Error al descargar notificaciones: " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                                Utils.message(getApplicationContext(), "Error al descargar notificaciones: " + e.getMessage());
                            }
                        } else {
                            Utils.message(getApplicationContext(), "Error al descargar notificaciones");
                        }
                    }
                } else {
                    Utils.message(getApplicationContext(), "Error al descargar notificaciones");
                }
            }

            @Override
            public void onFailure(Call<ResponseNotificaciones> call, Throwable t) {
                progressDialog.dismiss();
                Utils.message(MainActivity.this, Constants.MSG_ERR_CONN);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(findViewById(R.id.clExterno).getVisibility() == View.VISIBLE){
            findViewById(R.id.clIngresar).setVisibility(View.VISIBLE);
            findViewById(R.id.clExterno).setVisibility(View.GONE);
        }else{
            super.onBackPressed();
        }
    }
}
