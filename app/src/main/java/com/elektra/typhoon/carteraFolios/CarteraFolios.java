package com.elektra.typhoon.carteraFolios;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.adapters.AdapterRecyclerViewCartera;
import com.elektra.typhoon.adapters.SpinnerAdapter;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.BarcoDBMethods;
import com.elektra.typhoon.database.CatalogosDBMethods;
import com.elektra.typhoon.database.ChecklistDBMethods;
import com.elektra.typhoon.database.FoliosDBMethods;
import com.elektra.typhoon.database.NotificacionesDBMethods;
import com.elektra.typhoon.database.UsuarioDBMethods;
import com.elektra.typhoon.encryption.Encryption;
import com.elektra.typhoon.gps.GPSTracker;
import com.elektra.typhoon.json.SincronizacionJSON;
import com.elektra.typhoon.login.MainActivity;
import com.elektra.typhoon.notificaciones.Notifications;
import com.elektra.typhoon.objetos.ItemCatalogo;
import com.elektra.typhoon.objetos.request.SincronizacionData;
import com.elektra.typhoon.objetos.request.SincronizacionPost;
import com.elektra.typhoon.objetos.response.Barco;
import com.elektra.typhoon.objetos.response.CatalogosTyphoonResponse;
import com.elektra.typhoon.objetos.response.Configuracion;
import com.elektra.typhoon.objetos.response.DatosPorValidarResponse;
import com.elektra.typhoon.objetos.response.EstatusEvidencia;
import com.elektra.typhoon.objetos.response.EstatusRevision;
import com.elektra.typhoon.objetos.response.EtapaEvidencia;
import com.elektra.typhoon.objetos.response.EtapaSubAnexo;
import com.elektra.typhoon.objetos.response.FolioRevision;
import com.elektra.typhoon.objetos.response.LatLng;
import com.elektra.typhoon.objetos.response.Notificacion;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.ResponseNotificaciones;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.objetos.response.RolUsuario;
import com.elektra.typhoon.objetos.response.SincronizacionResponse;
import com.elektra.typhoon.objetos.response.TipoRespuesta;
import com.elektra.typhoon.service.ApiInterface;
import com.elektra.typhoon.objetos.request.CarteraData;
import com.elektra.typhoon.objetos.request.RequestCartera;
import com.elektra.typhoon.objetos.response.ResponseCartera;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.elektra.typhoon.service.NuevaInstalacion;
import com.elektra.typhoon.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Proyecto: TYPHOON
 * Autor: Francis Susana Carreto Espinoza
 * Fecha: 10/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class CarteraFolios extends AppCompatActivity {

    private RecyclerView recyclerView;
    //private ArrayList<Folio> folios;
    private AdapterRecyclerViewCartera adapterRecyclerViewCartera;
    private TextView textViewNotificaciones;
    private ResponseLogin.Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartera_folios);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewFolios);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CarteraFolios.this);
        recyclerView.setLayoutManager(layoutManager);

        final Spinner spinnerAnio = (Spinner) findViewById(R.id.spinnerAnio);
        final Spinner spinnerMes = (Spinner) findViewById(R.id.spinnerMes);

        Button buttonBuscar = (Button) findViewById(R.id.buttonBuscar);
        Button buttonLimpiarFiltro = (Button) findViewById(R.id.buttonLimpiarFiltro);
        final EditText editTextBuscar = (EditText) findViewById(R.id.editTextBuscar);
        TextView textViewNombreUsuario = (TextView) findViewById(R.id.textViewNombreUsuario);
        TextView textViewRol = findViewById(R.id.textViewRol);
        textViewNotificaciones = findViewById(R.id.textViewNotificaciones);

        UsuarioDBMethods usuarioDBMethods = new UsuarioDBMethods(this);
        usuario = usuarioDBMethods.readUsuario();
        if(usuario != null){
            textViewNombreUsuario.setText(usuario.getNombre());
            textViewRol.setText(Utils.getRol(this,usuario.getIdrol()));
        }

        //loadNotificaciones(usuario.getIdrol());

        final ImageView imageViewMenuCartera = (ImageView) findViewById(R.id.imageViewMenuCartera);

        List<ItemCatalogo> listItemsAnio = new ArrayList<>();
        List<ItemCatalogo> listItemsMes = new ArrayList<>();

        List<Integer> listAnios = new CatalogosDBMethods(this).readAnios();

        listItemsAnio.add(new ItemCatalogo(-1,"Año"));
        for(Integer integer:listAnios){
            listItemsAnio.add(new ItemCatalogo(integer,String.valueOf(integer)));
        }
        //listItemsAnio.add(new ItemCatalogo(2019,"2019"));
        //listItemsAnio.add(new ItemCatalogo(2018,"2018"));
        //listItemsAnio.add(new ItemCatalogo(2017,"2017"));
        //listItemsAnio.add(new ItemCatalogo(2016,"2016"));

        listItemsMes.add(new ItemCatalogo(-1,"Mes"));
        listItemsMes.add(new ItemCatalogo(1,"1"));
        listItemsMes.add(new ItemCatalogo(2,"2"));
        listItemsMes.add(new ItemCatalogo(3,"3"));
        listItemsMes.add(new ItemCatalogo(4,"4"));
        listItemsMes.add(new ItemCatalogo(5,"5"));
        listItemsMes.add(new ItemCatalogo(6,"6"));
        listItemsMes.add(new ItemCatalogo(7,"7"));
        listItemsMes.add(new ItemCatalogo(8,"8"));
        listItemsMes.add(new ItemCatalogo(9,"9"));
        listItemsMes.add(new ItemCatalogo(10,"10"));
        listItemsMes.add(new ItemCatalogo(11,"11"));
        listItemsMes.add(new ItemCatalogo(12,"12"));

        final SpinnerAdapter spinnerAdapterAnio = new SpinnerAdapter(CarteraFolios.this,R.layout.item_spinner_layout,listItemsAnio);
        spinnerAnio.setAdapter(spinnerAdapterAnio);

        SpinnerAdapter spinnerAdapterMes = new SpinnerAdapter(CarteraFolios.this,R.layout.item_spinner_layout,listItemsMes);
        spinnerMes.setAdapter(spinnerAdapterMes);

        buttonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemCatalogo itemCatalogoMes = (ItemCatalogo)spinnerMes.getSelectedItem();
                ItemCatalogo itemCatalogoAnio = (ItemCatalogo)spinnerAnio.getSelectedItem();
                int mes = -1;
                int anio = -1;
                int revision = -1;
                if(itemCatalogoAnio != null){
                    anio = itemCatalogoAnio.getId();
                }
                if(itemCatalogoMes != null){
                    mes = itemCatalogoMes.getId();
                }
                if(mes != -1 && anio == -1){
                    Utils.message(getApplicationContext(),"Debe seleccionar el año");
                }else {
                    try {
                        if (!editTextBuscar.getText().toString().equals("")) {
                            revision = Integer.parseInt(editTextBuscar.getText().toString());
                        }
                        obtenerFolios(revision, anio, mes);
                    } catch (NumberFormatException e) {
                        Utils.message(getApplicationContext(), "Folio no válido");
                    }
                }
            }
        });

        buttonLimpiarFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<FolioRevision> listFolios = new FoliosDBMethods(getApplicationContext()).readFolios();
                if(listFolios.size() == 0) {
                    obtenerFolios(-1, -1, -1);
                }else{
                    adapterRecyclerViewCartera = new AdapterRecyclerViewCartera(CarteraFolios.this,CarteraFolios.this,listFolios);
                    recyclerView.setAdapter(adapterRecyclerViewCartera);
                }
                editTextBuscar.setText("");
                spinnerAnio.setSelection(0);
                spinnerMes.setSelection(0);
            }
        });

        List<FolioRevision> listFolios = new FoliosDBMethods(getApplicationContext()).readFolios();
        if(listFolios.size() == 0) {
            obtenerFolios(-1, -1, -1);
        }else{
            adapterRecyclerViewCartera = new AdapterRecyclerViewCartera(CarteraFolios.this,CarteraFolios.this,listFolios);
            recyclerView.setAdapter(adapterRecyclerViewCartera);
        }

        imageViewMenuCartera.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                @SuppressLint("RestrictedApi")
                MenuBuilder menuBuilder = new MenuBuilder(CarteraFolios.this);
                MenuInflater inflater = new MenuInflater(CarteraFolios.this);
                inflater.inflate(R.menu.menu_cartera, menuBuilder);
                Context wrapper = new ContextThemeWrapper(CarteraFolios.this, R.style.PopupTheme);
                @SuppressLint("RestrictedApi")
                MenuPopupHelper optionsMenu = new MenuPopupHelper(wrapper, menuBuilder, imageViewMenuCartera);
                optionsMenu.setForceShowIcon(true);
                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {

                        if (item.getItemId() == R.id.cerrarSesion) {
                            SharedPreferences sharedPrefs = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
                            sharedPrefs.edit().putBoolean(Constants.SP_LOGIN_TAG, false).apply();
                            Intent intent = new Intent(CarteraFolios.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else if(item.getItemId() == R.id.actualizarCatalogos){
                            //Utils.descargaCatalogos(CarteraFolios.this,2);
                            descargaCatalogos(2);
                        }else if(item.getItemId() == R.id.nuevaInstalacion){
                            Utils.nuevaInstalacionDialog(CarteraFolios.this);
                        }
                        return true;
                    }

                    @Override
                    public void onMenuModeChange(MenuBuilder menu) {
                    }
                });
                optionsMenu.show();
            }
        });

        //sincronizacionDialog();

        //Notifications.CustomNotification(this);

        //boolean enZona = Utils.isPointInPolygon(new LatLng(19.30511913410018,-99.20381013535274));//fuera

        //obtenerDatosPorValidar(5,1);
        //obtenerNotificaciones(usuario.getIdrol());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void obtenerFolios(int idRevision,int anio,int mes){

        final ProgressDialog progressDialog = Utils.typhoonLoader(CarteraFolios.this,"Descargando folios...");

        ApiInterface mApiService = Utils.getInterfaceService();

        RequestCartera requestCartera = new RequestCartera();
        CarteraData carteraData = new CarteraData();
        carteraData.setIdRevision(idRevision);
        carteraData.setAnio(anio);
        carteraData.setMes(mes);
        requestCartera.setCarteraData(carteraData);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);

        //Call<ResponseCartera> mService = mApiService.carteraRevisiones(requestCartera);
        Call<ResponseCartera> mService = mApiService.carteraRevisiones(sharedPreferences.getString(Constants.SP_JWT_TAG,""),requestCartera);
        mService.enqueue(new Callback<ResponseCartera>() {

            @Override
            public void onResponse(Call<ResponseCartera> call, Response<ResponseCartera> response) {
                progressDialog.dismiss();
                if(response != null) {
                    if (response.body() != null) {
                        if (response.body().getCarteraRevisiones().getExito()) {
                            FoliosDBMethods foliosDBMethods = new FoliosDBMethods(getApplicationContext());
                            for (FolioRevision folioRevision : response.body().getCarteraRevisiones().getFolioRevision()) {
                                foliosDBMethods.createFolio(folioRevision);
                            }
                            adapterRecyclerViewCartera = new AdapterRecyclerViewCartera(CarteraFolios.this, CarteraFolios.this, response.body().getCarteraRevisiones().getFolioRevision());
                            recyclerView.setAdapter(adapterRecyclerViewCartera);
                            if (response.body().getCarteraRevisiones().getFolioRevision().size() == 0) {
                                Utils.message(getApplicationContext(), "No se encontraron folios");
                            }
                        } else {
                            Utils.message(getApplicationContext(), response.body().getCarteraRevisiones().getError());
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                Utils.message(getApplicationContext(), "Error al descargar folios: " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                                Utils.message(getApplicationContext(), "Error al descargar folios: " + e.getMessage());
                            }
                        } else {
                            Utils.message(getApplicationContext(), "Error al descargar folios");
                        }
                    }
                }else{
                    Utils.message(getApplicationContext(), "Error al descargar folios");
                }
            }

            @Override
            public void onFailure(Call<ResponseCartera> call, Throwable t) {
                progressDialog.dismiss();
                Utils.message(CarteraFolios.this, Constants.MSG_ERR_CONN);
            }
        });
    }

    private void setRadioGroup(int idBarco,int idRevision,int idChecklist){
        ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(getApplicationContext());
        List<RespuestaData> listRespuestas = checklistDBMethods.readRespuesta(
                "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_RUBRO,ID_ESTATUS,ID_BARCO,ID_REGISTRO,ID_RESPUESTA,SINCRONIZADO FROM " + checklistDBMethods.TP_TRAN_CL_RESPUESTA + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_BARCO = ?",
                new String[]{String.valueOf(idRevision),String.valueOf(idChecklist),String.valueOf(idBarco)});
        int cumple = 0;
        int noCumple = 0;
        for(RespuestaData respuestaData:listRespuestas){
            if(respuestaData.getIdRespuesta() != null) {
                if (respuestaData.getIdRespuesta() == 2) {
                    cumple++;
                }
                if (respuestaData.getIdRespuesta() == 3) {
                    noCumple++;
                }
            }else{
                noCumple++;
            }
        }
    }

    private void descargaCatalogos(final int opcion){

        String titulo = "";
        if(opcion == 1){
            titulo = "Descargando catálogos...";
        }else{
            titulo = "Actualizando catálogos...";
        }

        final ProgressDialog progressDialog = Utils.typhoonLoader(CarteraFolios.this,titulo);

        final Encryption encryption = new Encryption();

        ApiInterface mApiService = Utils.getInterfaceService();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
        Call<CatalogosTyphoonResponse> mService = mApiService.catalogosTyphoon(sharedPreferences.getString(Constants.SP_JWT_TAG, ""));
        mService.enqueue(new Callback<CatalogosTyphoonResponse>() {
            @Override
            public void onResponse(Call<CatalogosTyphoonResponse> call, Response<CatalogosTyphoonResponse> response) {
                if(response != null) {
                    if (response.body() != null) {
                        if (response.body().getCatalogos().getExito()) {
                            //try {
                            BarcoDBMethods barcoDBMethods = new BarcoDBMethods(CarteraFolios.this);
                            CatalogosDBMethods catalogosDBMethods = new CatalogosDBMethods(CarteraFolios.this);
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
                                }
                            }
                            if (response.body().getCatalogos().getCatalogosData().getListEtapasSubAnexo() != null) {
                                catalogosDBMethods.deleteEtapaSubAnexo();
                                for (EtapaSubAnexo etapaSubAnexo: response.body().getCatalogos().getCatalogosData().getListEtapasSubAnexo()) {
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
                            Utils.message(CarteraFolios.this, "Catálogos descargados");
                            if (opcion == 1) {
                                Intent intent = new Intent(CarteraFolios.this, CarteraFolios.class);
                                startActivity(intent);
                                finish();
                            }
                            /*} catch (NullPointerException e) {
                                progressDialog.dismiss();
                                Utils.message(activity, "Error al guardar los catálogos: " + e.getMessage());
                                e.printStackTrace();
                            }//*/
                        } else {
                            progressDialog.dismiss();
                            Utils.message(CarteraFolios.this, response.body().getCatalogos().getError());
                        }
                    } else {
                        progressDialog.dismiss();
                        if (response.errorBody() != null) {
                            try {
                                Utils.message(CarteraFolios.this, "Error al descargar catálogos: " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                                Utils.message(CarteraFolios.this, "Error al descargar catálogos: " + e.getMessage());
                            }
                        } else {
                            Utils.message(CarteraFolios.this, "Error al descargar catálogos");
                        }
                    }
                }else{
                    Utils.message(CarteraFolios.this, "Error al descargar catálogos");
                }
            }

            @Override
            public void onFailure(Call<CatalogosTyphoonResponse> call, Throwable t) {
                progressDialog.dismiss();
                Utils.message(CarteraFolios.this, Constants.MSG_ERR_CONN);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        List<FolioRevision> listFolios = new FoliosDBMethods(getApplicationContext()).readFolios();
        if(listFolios.size() == 0) {
            obtenerFolios(-1, -1, -1);
        }else{
            adapterRecyclerViewCartera = new AdapterRecyclerViewCartera(CarteraFolios.this,CarteraFolios.this,listFolios);
            recyclerView.setAdapter(adapterRecyclerViewCartera);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private void obtenerNotificaciones(int idRol){

        final ProgressDialog progressDialog = Utils.typhoonLoader(CarteraFolios.this,"Descargando notificaciones...");

        ApiInterface mApiService = Utils.getInterfaceService();

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);

        final NotificacionesDBMethods notificacionesDBMethods = new NotificacionesDBMethods(CarteraFolios.this);

        //Call<ResponseCartera> mService = mApiService.carteraRevisiones(requestCartera);
        Call<ResponseNotificaciones> mService = mApiService.getNotificaciones(sharedPreferences.getString(Constants.SP_JWT_TAG,""),idRol);
        mService.enqueue(new Callback<ResponseNotificaciones>() {

            @Override
            public void onResponse(Call<ResponseNotificaciones> call, Response<ResponseNotificaciones> response) {
                progressDialog.dismiss();
                if(response != null) {
                    if (response.body() != null) {
                        if (response.body().getNotificaciones().getExito()) {
                            if(response.body().getNotificaciones().getNotificaciones() != null){
                                for(Notificacion notificacion:response.body().getNotificaciones().getNotificaciones()){
                                    notificacionesDBMethods.createNotificacion(notificacion);
                                }
                                //loadNotificaciones(usuario.getIdrol());
                            }
                        } else {
                            Utils.message(getApplicationContext(), response.body().getNotificaciones().getError());
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                Utils.message(getApplicationContext(), "Error al descargar notificaciones: " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                                Utils.message(getApplicationContext(), "Error al descargar notificaciones: " + e.getMessage());
                            }
                        } else {
                            Utils.message(getApplicationContext(), "Error al descargar notificaciones");
                        }
                    }
                }else{
                    Utils.message(getApplicationContext(), "Error al descargar notificaciones");
                }
            }

            @Override
            public void onFailure(Call<ResponseNotificaciones> call, Throwable t) {
                progressDialog.dismiss();
                Utils.message(CarteraFolios.this, Constants.MSG_ERR_CONN);
            }
        });
    }

    private void loadNotificaciones(int idRol){
        List<Notificacion> notificaciones = new NotificacionesDBMethods(CarteraFolios.this).readNotificaciones(idRol);
        if(notificaciones.size() == 0){
            textViewNotificaciones.setVisibility(View.GONE);
        }else{
            textViewNotificaciones.setVisibility(View.VISIBLE);
            textViewNotificaciones.setText(String.valueOf(notificaciones.size()));
        }
    }
}
