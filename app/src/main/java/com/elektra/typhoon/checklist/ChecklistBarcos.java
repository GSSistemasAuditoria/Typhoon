package com.elektra.typhoon.checklist;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.adapters.AdapterExpandableChecklist;
import com.elektra.typhoon.adapters.SpinnerBarcosAdapter;
import com.elektra.typhoon.anexos.AnexosActivity;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.BarcoDBMethods;
import com.elektra.typhoon.database.CatalogosDBMethods;
import com.elektra.typhoon.database.ChecklistDBMethods;
import com.elektra.typhoon.database.EvidenciasDBMethods;
import com.elektra.typhoon.database.UsuarioDBMethods;
import com.elektra.typhoon.encryption.Encryption;
import com.elektra.typhoon.gps.GPSTracker;
import com.elektra.typhoon.objetos.response.CatalogoBarco;
import com.elektra.typhoon.objetos.response.Checklist;
import com.elektra.typhoon.objetos.response.ChecklistData;
import com.elektra.typhoon.objetos.response.EstatusRevision;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.objetos.response.RubroData;
import com.elektra.typhoon.service.AsyncTaskGral;
import com.elektra.typhoon.service.Delegate;
import com.elektra.typhoon.service.SincronizacionRequestService;
import com.elektra.typhoon.utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 17/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class ChecklistBarcos extends AppCompatActivity {
    private final static String TAG = ChecklistBarcos.class.getName();

    private TextView textViewNombreBarco;
    //private List<Barco> listBarcos;
    private List<CatalogoBarco> listCatalogoBarcos;
    private ExpandableListView expandableListView;
    private AdapterExpandableChecklist adapterExpandableChecklist;
    private int folio;
    private String fechaInicio;
    private String fechaFin;
    private int estatus;
    private TextView textViewValorTotal;
    private TextView textViewCumplenValor;
    private TextView textViewNoCumplenValor;
    private TextView tvPorCargarValor;
    private TextView tvPorValidarValor;
    private Spinner spinnerBarco;
    private TextView textViewTituloChecklist;
    private Button buttonSincronizar;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private double latitude;
    private double longitude;
    private int countToGetLocation;

    @Override
    protected void onPause() {
        super.onPause();
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLocationGPS();
    }

    private void initLocationGPS() {
        if (mFusedLocationClient != null)
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        else {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5000);
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        double latitudeLocation = Double.parseDouble(new DecimalFormat("#.000").format(location.getLatitude()));
                        double longitudeLocation = Double.parseDouble(new DecimalFormat("#.000").format(location.getLongitude()));
                        if (latitude == latitudeLocation && longitude == longitudeLocation && countToGetLocation > 10)
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        latitude = latitudeLocation;
                        countToGetLocation++;
                        longitude = longitudeLocation;
                        Log.e(TAG, String.format(Locale.US, "%s -- %s", latitude, longitude));
                    }
                }
            };

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist_layout);
        initLocationGPS();
        final Encryption encryption = new Encryption();

        /*folio = getIntent().getIntExtra(Constants.INTENT_FOLIO_TAG, 0);
        fechaInicio = getIntent().getStringExtra(Constants.INTENT_FECHA_INICIO_TAG);
        fechaFin = getIntent().getStringExtra(Constants.INTENT_FECHA_FIN_TAG);
        estatus = getIntent().getIntExtra(Constants.INTENT_ESTATUS_TAG,0);//*/

        folio = Integer.parseInt(encryption.decryptAES(getIntent().getStringExtra(Constants.INTENT_FOLIO_TAG)));
        fechaInicio = encryption.decryptAES(Normalizer.normalize(getIntent().getStringExtra(Constants.INTENT_FECHA_INICIO_TAG), Normalizer.Form.NFD));
        /*if(getIntent().hasExtra(Constants.INTENT_FECHA_FIN_TAG)){
            fechaFin = encryption.decryptAES(Normalizer.normalize(getIntent().getStringExtra(Constants.INTENT_FECHA_FIN_TAG), Normalizer.Form.NFD));
        }else{
            fechaFin = "";
        }//*/
        estatus = Integer.parseInt(encryption.decryptAES(getIntent().getStringExtra(Constants.INTENT_ESTATUS_TAG)));

        spinnerBarco = (Spinner) findViewById(R.id.spinnerBarcos);
        textViewNombreBarco = (TextView) findViewById(R.id.textViewNombreBarco);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListViewChecklist);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            private int previousItem = -1;

            @Override
            public void onGroupExpand(final int groupPosition) {
                new AsyncTaskGral(ChecklistBarcos.this, new Delegate() {
                    @Override
                    public void getDelegate(String result) {
                    }

                    @Override
                    public String executeInBackground() {
                        if (groupPosition != previousItem) {
                            expandableListView.collapseGroup(previousItem);
                            if (previousItem != -1) {
                                RubroData rubroData = listCatalogoBarcos.get(spinnerBarco.getSelectedItemPosition()).getListRubros().get(previousItem);
                                if (rubroData.getListPreguntasTemp() != null) {
                                    for (Pregunta pregunta : rubroData.getListPreguntasTemp()) {
                                        if (pregunta.getListEvidencias() != null) {
                                            for (Evidencia evidencia : pregunta.getListEvidencias()) {
                                                if (evidencia.getSmallBitmap() != null) {
                                                    evidencia.getSmallBitmap().recycle();
                                                }
                                            }
                                            pregunta.getListEvidencias().clear();
                                            pregunta.setListEvidencias(null);
                                        }
                                    }
                                }
                                System.out.println();
                            }
                        }
                        previousItem = groupPosition;
                        //timerDelayRunForScroll(100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                expandableListView.setSelectionFromTop(groupPosition, 0);
                            }
                        });
                        return null;
                    }
                }, null).execute();
            }
        });

        textViewValorTotal = findViewById(R.id.textViewValorTotal);
        textViewCumplenValor = findViewById(R.id.textViewCumplenValor);
        textViewNoCumplenValor = findViewById(R.id.textViewNoCumplenValor);
        tvPorValidarValor = findViewById(R.id.tvPorValidarValor);
        tvPorCargarValor = findViewById(R.id.tvPorCargarValor);

        TextView textViewFolio = findViewById(R.id.textViewFolio);
        TextView textViewFechaInicio = findViewById(R.id.textViewFechaInicio);
        TextView textViewFechaFin = findViewById(R.id.textViewFechaFin);
        textViewTituloChecklist = findViewById(R.id.textViewTituloChecklist);

        TextView textViewNombreUsuario = (TextView) findViewById(R.id.textViewNombreUsuario);
        TextView textViewRol = findViewById(R.id.textViewRol);

        UsuarioDBMethods usuarioDBMethods = new UsuarioDBMethods(this);
        ResponseLogin.Usuario usuario = usuarioDBMethods.readUsuario();
        if (usuario != null) {
            textViewNombreUsuario.setText(usuario.getNombre());
            textViewRol.setText(Utils.getRol(this, usuario.getIdrol()));
        }

        buttonSincronizar = (Button) findViewById(R.id.buttonSincronizarChecklist);

        buttonSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sincronizacionDialog(ChecklistBarcos.this, folio);
            }
        });

        //buttonSincronizar.setVisibility(View.GONE);

        final Button buttonAnexos = findViewById(R.id.buttonAnexos);
        buttonAnexos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChecklistBarcos.this, AnexosActivity.class);
                intent.putExtra(Constants.INTENT_FOLIO_TAG, encryption.encryptAES(String.valueOf(folio)));
                intent.putExtra(Constants.INTENT_FECHA_INICIO_TAG, encryption.encryptAES(fechaInicio));
                intent.putExtra(Constants.INTENT_ESTATUS_TAG, encryption.encryptAES(String.valueOf(estatus)));
                startActivity(intent);
            }
        });
        //buttonAnexos.setVisibility(View.GONE);

        CatalogosDBMethods catalogosDBMethods = new CatalogosDBMethods(this);
        List<EstatusRevision> listEstatusRevision = catalogosDBMethods.readEstatusRevision(
                "SELECT ID_ESTATUS,DESCRIPCION,SRC FROM " + catalogosDBMethods.TP_CAT_ESTATUS_REVISION + " WHERE ID_ESTATUS = ?", new String[]{String.valueOf(estatus)});

        textViewFolio.setText("" + folio);
        textViewFechaInicio.setText(Utils.getDateMonth(fechaInicio));
        if (listEstatusRevision.size() != 0) {
            textViewFechaFin.setText(listEstatusRevision.get(0).getDescripcion());
        }

        listCatalogoBarcos = new BarcoDBMethods(this).readBarcos();
        ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(this);
        EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(this);
        //UsuarioDBMethods usuarioDBMethods = new UsuarioDBMethods(this);

        //ResponseLogin.Usuario usuario = usuarioDBMethods.readUsuario(null,null);

        List<ChecklistData> listChecklist = checklistDBMethods.readChecklists(
                "SELECT ID_REVISION,ID_CHECKLIST,ID_ESTATUS,ID_LOGO,ID_TIPO_REVISION,NOMBRE,PONDERACION FROM " + checklistDBMethods.TP_CAT_CHEKLIST + " WHERE ID_REVISION = ?",
                new String[]{String.valueOf(folio)});

        if (listChecklist.size() != 0) {
            ChecklistData checklistData = listChecklist.get(0);
            textViewTituloChecklist.setText(checklistData.getNombre());
            for (CatalogoBarco catalogoBarco : listCatalogoBarcos) {
                List<RubroData> listRubros = checklistDBMethods.readRubro(
                        "SELECT ID_REVISION,ID_CHECKLIST,ID_RUBRO,ESTATUS,NOMBRE FROM " + checklistDBMethods.TP_CAT_CL_RUBRO + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ?",
                        new String[]{String.valueOf(checklistData.getIdRevision()), String.valueOf(checklistData.getIdChecklist())});
                for (RubroData rubroData : listRubros) {
                    String query = null;
                    //rubroData.setSeleccionado(true);
                    if (usuario.getIdrol() == 3) {
                        query = "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_TIPO_RESPUESTA,ID_RUBRO,ESTATUS,DESCRIPCION,IS_TIERRA,SELECCIONADO FROM " +
                                checklistDBMethods.TP_CAT_CL_PREGUNTA + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ?";
                    } else {
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
                        pregunta.setSeleccionado(Utils.isPreguntaSeleccionada(listRespuestas, pregunta));
                    }
                    /*} catch (IOException e) {
                        e.printStackTrace();
                    }//*/
                }
                for (int i = 0; i < listRubros.size(); i++) {
                    if (listRubros.get(i).getListPreguntasTemp() != null) {
                        if (listRubros.get(i).getListPreguntasTemp().size() == 0) {
                            listRubros.remove(i);
                        }
                    } else {
                        listRubros.remove(i);
                    }
                }
                catalogoBarco.setListRubros(listRubros);
            }
            System.out.println();
        }

        SpinnerBarcosAdapter spinnerBarcosAdapter = new SpinnerBarcosAdapter(ChecklistBarcos.this, R.layout.item_spinner_layout, listCatalogoBarcos);
        spinnerBarco.setAdapter(spinnerBarcosAdapter);

        spinnerBarco.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                new CargaDatosChecklistTask(ChecklistBarcos.this, i).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /*private void timerDelayRunForScroll(long time) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    expandableListView.smoothScrollToPosition(0);
                } catch (Exception e) {}
            }
        }, time);
    }//*/

    public void reloadData() {
        loadData();
        new CargaDatosChecklistTask(ChecklistBarcos.this, spinnerBarco.getSelectedItemPosition()).execute();
    }

    private String fechaSinHoras(String fecha) {
        if (fecha.contains(" ")) {
            String[] temp = fecha.split(" ");
            return temp[0];
        } else {
            return fecha;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            new GuardandoEvidenciasTask(ChecklistBarcos.this, data, requestCode).execute();

            /*ProgressDialog progressDialog = Utils.typhoonLoader(ChecklistBarcos.this,"Guardando evidencia...");
            if(data != null) {
                Bundle extras = data.getExtras();
                //Imágen desde cámara
                if (extras != null) {
                    Bitmap bitmap = extras.getParcelable("data");
                    Bitmap scaledBitmap = Utils.resizeImageBitmap(bitmap);
                    String base64 = null;
                    String base64Preview = null;
                    int rubro = adapterExpandableChecklist.getRubroPosition();
                    int idrubro = adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().getIdRubro();
                    try {
                        base64 = Utils.bitmapToBase64(bitmap);
                        base64Preview = Utils.bitmapToBase64(scaledBitmap);
                        RespuestaData datosRespuesta = null;
                        for (RespuestaData respuestaData : adapterExpandableChecklist.getListRubros().get(idrubro).getListRespuestas()) {
                            if (respuestaData.getIdPregunta() == adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getIdPregunta()) {
                                datosRespuesta = respuestaData;
                                break;
                            }
                        }

                        Evidencia evidencia = new Evidencia();

                        if (datosRespuesta != null) {
                            evidencia.setIdRegistro(datosRespuesta.getIdRegistro());
                            evidencia.setIdPregunta(datosRespuesta.getIdPregunta());
                            evidencia.setIdRubro(datosRespuesta.getIdRubro());
                            evidencia.setIdChecklist(datosRespuesta.getIdChecklist());
                            evidencia.setIdRevision(datosRespuesta.getIdRevision());
                            evidencia.setIdEstatus(1);
                            evidencia.setIdEtapa(1);
                            evidencia.setContenido(base64);
                            evidencia.setContenidoPreview(base64Preview);
                            evidencia.setNombre(Utils.getDate("yyyyMMddHHmmss") + ".png");
                            evidencia.setIdEvidencia(UUID.randomUUID().toString());
                            CatalogoBarco barco = (CatalogoBarco) spinnerBarco.getSelectedItem();
                            evidencia.setIdBarco(barco.getIdBarco());

                            GPSTracker gps = new GPSTracker(ChecklistBarcos.this);
                            if(gps.canGetLocation()) {
                                double latitude = gps.getLatitude();
                                double longitude = gps.getLongitude();
                                evidencia.setLatitude(latitude);
                                evidencia.setLongitude(longitude);
                                gps.stopUsingGPS();
                            }

                            EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                            evidenciasDBMethods.createEvidencia(evidencia);

                            evidencia.setContenido(null);

                            if (adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {
                                evidencia.setSmallBitmap(scaledBitmap);
                                adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).
                                        getListEvidencias().add(evidencia);
                                adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                            } else {
                                List<Evidencia> listEvidencias = new ArrayList<>();
                                //listEvidencias.add(new Evidencia(scaledBitmap,bitmap,1));
                                evidencia.setSmallBitmap(scaledBitmap);
                                listEvidencias.add(evidencia);
                                adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).setListEvidencias(listEvidencias);
                                adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                            }
                            System.out.println();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.message(getApplicationContext(), "No se pudo guardar la imagen: " + e.getMessage());
                    }
                } else {
                    //Imágen desde galería
                    Uri uri = data.getData();
                    String path = Utils.getRealPathFromURI(ChecklistBarcos.this, uri);
                    //String path2 = Utils.getPathFromUri(ChecklistBarcos.this,uri);
                    int idrubro = adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().getIdRubro();
                    if (path.contains("pdf")) {
                        try {
                            String base64 = Utils.fileToBase64(ChecklistBarcos.this, uri);
                            RespuestaData datosRespuesta = null;
                            for (RespuestaData respuestaData : adapterExpandableChecklist.getListRubros().get(idrubro).getListRespuestas()) {
                                if (respuestaData.getIdPregunta() == adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getIdPregunta()) {
                                    datosRespuesta = respuestaData;
                                    break;
                                }
                            }

                            Evidencia evidencia = new Evidencia();

                            if (datosRespuesta != null) {
                                evidencia.setIdRegistro(datosRespuesta.getIdRegistro());
                                evidencia.setIdPregunta(datosRespuesta.getIdPregunta());
                                evidencia.setIdRubro(datosRespuesta.getIdRubro());
                                evidencia.setIdChecklist(datosRespuesta.getIdChecklist());
                                evidencia.setIdRevision(datosRespuesta.getIdRevision());
                                evidencia.setIdEstatus(1);
                                evidencia.setIdEtapa(1);
                                evidencia.setContenido(base64);
                                //evidencia.setContenidoPreview(base64Preview);
                                //evidencia.setNombre(Utils.getDate("yyyyMMddHHmmss") + ".png");
                                evidencia.setNombre(path);
                                evidencia.setIdEvidencia(UUID.randomUUID().toString());
                                CatalogoBarco barco = (CatalogoBarco) spinnerBarco.getSelectedItem();
                                evidencia.setIdBarco(barco.getIdBarco());

                                GPSTracker gps = new GPSTracker(ChecklistBarcos.this);
                                if(gps.canGetLocation()) {
                                    double latitude = gps.getLatitude();
                                    double longitude = gps.getLongitude();
                                    evidencia.setLatitude(latitude);
                                    evidencia.setLongitude(longitude);
                                    gps.stopUsingGPS();
                                }

                                EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                                evidenciasDBMethods.createEvidencia(evidencia);

                                evidencia.setContenido(null);

                                if (adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {
                                    //evidencia.setSmallBitmap(scaledBitmap);
                                    adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).
                                            getListEvidencias().add(evidencia);
                                    adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                } else {
                                    List<Evidencia> listEvidencias = new ArrayList<>();
                                    //listEvidencias.add(new Evidencia(scaledBitmap,bitmap,1));
                                    //evidencia.setSmallBitmap(scaledBitmap);
                                    listEvidencias.add(evidencia);
                                    adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).setListEvidencias(listEvidencias);
                                    adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                }
                                System.out.println();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Bitmap bitmap = null;
                        try {
                            bitmap = Utils.getBitmap(getApplicationContext(), uri);
                            bitmap = Utils.resizeImageBitmap(bitmap,768,1024);
                            Bitmap scaledBitmap = Utils.resizeImageBitmap(bitmap);
                            String base64 = null;
                            String base64Preview = null;
                            base64 = Utils.bitmapToBase64(bitmap, path);
                            base64Preview = Utils.bitmapToBase64(scaledBitmap);
                            RespuestaData datosRespuesta = null;
                            for (RespuestaData respuestaData : adapterExpandableChecklist.getListRubros().get(idrubro).getListRespuestas()) {
                                if (respuestaData.getIdPregunta() == adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getIdPregunta()) {
                                    datosRespuesta = respuestaData;
                                    break;
                                }
                            }

                            Evidencia evidencia = new Evidencia();

                            if (datosRespuesta != null) {
                                evidencia.setIdRegistro(datosRespuesta.getIdRegistro());
                                evidencia.setIdPregunta(datosRespuesta.getIdPregunta());
                                evidencia.setIdRubro(datosRespuesta.getIdRubro());
                                evidencia.setIdChecklist(datosRespuesta.getIdChecklist());
                                evidencia.setIdRevision(datosRespuesta.getIdRevision());
                                evidencia.setIdEstatus(1);
                                evidencia.setIdEtapa(1);
                                evidencia.setContenido(base64);
                                evidencia.setContenidoPreview(base64Preview);
                                evidencia.setNombre(path);
                                evidencia.setIdEvidencia(UUID.randomUUID().toString());
                                CatalogoBarco barco = (CatalogoBarco) spinnerBarco.getSelectedItem();
                                evidencia.setIdBarco(barco.getIdBarco());

                                GPSTracker gps = new GPSTracker(ChecklistBarcos.this);
                                if(gps.canGetLocation()) {
                                    double latitude = gps.getLatitude();
                                    double longitude = gps.getLongitude();
                                    evidencia.setLatitude(latitude);
                                    evidencia.setLongitude(longitude);
                                    gps.stopUsingGPS();
                                }

                                EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                                evidenciasDBMethods.createEvidencia(evidencia);

                                evidencia.setContenido(null);

                                if (adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {
                                    evidencia.setSmallBitmap(scaledBitmap);
                                    adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).
                                            getListEvidencias().add(evidencia);
                                    adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                } else {
                                    List<Evidencia> listEvidencias = new ArrayList<>();
                                    //listEvidencias.add(new Evidencia(scaledBitmap,bitmap,1));
                                    evidencia.setSmallBitmap(scaledBitmap);
                                    listEvidencias.add(evidencia);
                                    adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).setListEvidencias(listEvidencias);
                                    adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                }
                                System.out.println();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Utils.message(getApplicationContext(), "No se pudo guardar la imagen: " + e.getMessage());
                        }
                    }

                    System.out.println();
                }
            }else{
                try {
                    File file = new File(Constants.PATH + "tempPhotos/evidencia.jpg");
                    Uri capturedImageUri = Uri.fromFile(file);
                    Bitmap bitmap = Utils.getBitmap(getApplicationContext(),capturedImageUri);
                    bitmap = Utils.rotateImageIfRequired(getApplicationContext(),bitmap,capturedImageUri);
                    Bitmap scaledBitmap = Utils.resizeImageBitmap(bitmap);
                    bitmap = Utils.resizeImageBitmap(bitmap,768,1024);
                    String base64 = null;
                    String base64Preview = null;
                    int rubro = adapterExpandableChecklist.getRubroPosition();
                    int idrubro = adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().getIdRubro();
                    try {
                        base64 = Utils.bitmapToBase64(bitmap,"jpg");
                        base64Preview = Utils.bitmapToBase64(scaledBitmap);
                        RespuestaData datosRespuesta = null;
                        for (RespuestaData respuestaData : adapterExpandableChecklist.getListRubros().get(idrubro).getListRespuestas()) {
                            if (respuestaData.getIdPregunta() == adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getIdPregunta()) {
                                datosRespuesta = respuestaData;
                                break;
                            }
                        }

                        Evidencia evidencia = new Evidencia();

                        if (datosRespuesta != null) {
                            evidencia.setIdRegistro(datosRespuesta.getIdRegistro());
                            evidencia.setIdPregunta(datosRespuesta.getIdPregunta());
                            evidencia.setIdRubro(datosRespuesta.getIdRubro());
                            evidencia.setIdChecklist(datosRespuesta.getIdChecklist());
                            evidencia.setIdRevision(datosRespuesta.getIdRevision());
                            evidencia.setIdEstatus(1);
                            evidencia.setIdEtapa(1);
                            evidencia.setContenido(base64);
                            evidencia.setContenidoPreview(base64Preview);
                            evidencia.setNombre(Utils.getDate("yyyyMMddHHmmss") + ".jpg");
                            evidencia.setIdEvidencia(UUID.randomUUID().toString());
                            CatalogoBarco barco = (CatalogoBarco) spinnerBarco.getSelectedItem();
                            evidencia.setIdBarco(barco.getIdBarco());

                            GPSTracker gps = new GPSTracker(ChecklistBarcos.this);
                            if(gps.canGetLocation()) {
                                double latitude = gps.getLatitude();
                                double longitude = gps.getLongitude();
                                evidencia.setLatitude(latitude);
                                evidencia.setLongitude(longitude);
                                gps.stopUsingGPS();
                            }

                            EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                            evidenciasDBMethods.createEvidencia(evidencia);

                            evidencia.setContenido(null);

                            if (adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {
                                evidencia.setSmallBitmap(scaledBitmap);
                                adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).
                                        getListEvidencias().add(evidencia);
                                adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                            } else {
                                List<Evidencia> listEvidencias = new ArrayList<>();
                                //listEvidencias.add(new Evidencia(scaledBitmap,bitmap,1));
                                evidencia.setSmallBitmap(scaledBitmap);
                                listEvidencias.add(evidencia);
                                adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).setListEvidencias(listEvidencias);
                                adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                            }
                            System.out.println();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.message(getApplicationContext(), "No se pudo guardar la imagen: " + e.getMessage());
                    }
                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.message(getApplicationContext(), "No se pudo guardar la imagen: " + e.getMessage());
                }
            }
            progressDialog.dismiss();//*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.itemSincronizacion) {
            sincronizacionDialog(ChecklistBarcos.this, folio);
        }
        return super.onOptionsItemSelected(item);
    }

    class CargaDatosChecklistTask extends AsyncTask<String, String, String> {

        private ProgressDialog statusDialog;
        private Activity context;
        private int position;

        public CargaDatosChecklistTask(Activity context, int position) {
            this.context = context;
            this.position = position;
        }

        protected void onPreExecute() {
            statusDialog = Utils.typhoonLoader(context, "Cargando datos...");
        }

        @Override
        protected String doInBackground(String... strings) {
            //publishProgress("Cargando datos");

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }//*/

            return "OK";
        }

        @Override
        protected void onPostExecute(String result) {
            //statusDialog.dismiss();
            if (!result.equals("OK")) {
                Utils.message(context, result);
            } else {
                //try{
                if (listCatalogoBarcos.size() != 0) {
                    CatalogoBarco barco = listCatalogoBarcos.get(position);
                    String nombre = barco.getNombre();
                    if (nombre != null) {
                        textViewNombreBarco.setText(nombre);
                    }

                    if (barco.getListRubros() != null) {
                        int numeroPreguntas = 0;
                        int numeroPreguntasVisualizadas = 0;
                        for (RubroData rubro : barco.getListRubros()) {
                            if (rubro.getListPreguntasTemp() != null) {
                                numeroPreguntasVisualizadas += rubro.getListPreguntasTemp().size();
                            }
                        }//*/

                        ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(getApplicationContext());
                        ChecklistData checklist = checklistDBMethods.readChecklist(
                                "SELECT ID_REVISION,ID_CHECKLIST,ID_ESTATUS,ID_LOGO,ID_TIPO_REVISION,NOMBRE,PONDERACION FROM " + checklistDBMethods.TP_CAT_CHEKLIST + " WHERE ID_REVISION = ?",
                                new String[]{String.valueOf(folio)});
                        List<RespuestaData> listRespuestas = checklistDBMethods.readRespuesta(
                                "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_RUBRO,ID_ESTATUS,ID_BARCO,ID_REGISTRO,ID_RESPUESTA,SINCRONIZADO FROM " + checklistDBMethods.TP_TRAN_CL_RESPUESTA + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_BARCO = ?", new String[]{
                                        String.valueOf(folio), String.valueOf(checklist.getIdChecklist()), String.valueOf(barco.getIdBarco())
                                });

                        numeroPreguntas = listRespuestas.size();
                        int diferenciaPreguntas = numeroPreguntas - numeroPreguntasVisualizadas;

                        //textViewValorTotal.setText(String.valueOf(numeroPreguntas));
                        textViewValorTotal.setText(String.valueOf(numeroPreguntasVisualizadas));
                        textViewNoCumplenValor.setText(String.valueOf(numeroPreguntas));

                        actualizarValores(position, diferenciaPreguntas);

                        adapterExpandableChecklist = new AdapterExpandableChecklist(barco.getListRubros(), ChecklistBarcos.this,
                                textViewCumplenValor, textViewNoCumplenValor, tvPorCargarValor, tvPorValidarValor, fechaInicio, listCatalogoBarcos.get(spinnerBarco.getSelectedItemPosition()).getIdBarco());
                        expandableListView.setAdapter(adapterExpandableChecklist);
                        //((AdapterExpandableChecklist)expandableListView.getAdapter()).notifyDataSetChanged();
                        statusDialog.dismiss();
                        //buttonSincronizar.setVisibility(View.VISIBLE);
                    } else {
                        statusDialog.dismiss();
                        Utils.message(context, "No se ha descargado el checklist, sincronice su revisión");
                    }
                } else {
                    statusDialog.dismiss();
                    Utils.message(context, "No se ha descargado el catálogo de barcos");
                }
                /*}catch (NullPointerException e){
                    statusDialog.dismiss();
                    Utils.message(context,"No se pudieron cargar los datos: " + e.getMessage());
                }//*/
            }
        }
    }

    private void actualizarValores(int position, int diferencia) {
        /*int cumple = 0;
        int noCumple = 0;
        if(listCatalogoBarcos.size() != 0) {
            for (RubroData rubro : listCatalogoBarcos.get(position).getListRubros()) {
                for (Pregunta pregunta : rubro.getListPreguntasTemp()) {
                    //if (pregunta.isCumple()) {
                    if (Utils.aplicaPregunta(this,pregunta.getListEvidencias())) {
                        cumple++;
                    } else {
                        noCumple++;
                    }
                }
            }
            textViewCumplenValor.setText(String.valueOf(cumple));
            textViewNoCumplenValor.setText(String.valueOf(noCumple+diferencia));
        }//*/

        /*ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(getApplicationContext());
        ChecklistData checklistData = checklistDBMethods.readChecklist(
                "SELECT ID_REVISION,ID_CHECKLIST,ID_ESTATUS,ID_LOGO,ID_TIPO_REVISION,NOMBRE,PONDERACION FROM " + checklistDBMethods.TP_CAT_CHEKLIST + " WHERE ID_REVISION = ?",
                new String[]{String.valueOf(folio)});

        ResponseLogin.Usuario usuario = new UsuarioDBMethods(getApplicationContext()).readUsuario();

        String query = "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_TIPO_RESPUESTA,ID_RUBRO,ESTATUS,DESCRIPCION,IS_TIERRA,SELECCIONADO FROM " +
                checklistDBMethods.TP_CAT_CL_PREGUNTA + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ?";

        List<Pregunta> listPreguntas = checklistDBMethods.readPregunta(query,
                new String[]{String.valueOf(folio), String.valueOf(checklistData.getIdChecklist())});

        List<RespuestaData> listRespuestas = checklistDBMethods.readRespuesta(
                "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_RUBRO,ID_ESTATUS,ID_BARCO,ID_REGISTRO,ID_RESPUESTA,SINCRONIZADO FROM " + checklistDBMethods.TP_TRAN_CL_RESPUESTA + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_BARCO = ?",
                new String[]{String.valueOf(folio), String.valueOf(checklistData.getIdChecklist()), String.valueOf(position + 1)});


        int cumple = 0;
        int noCumple = 0;
        int porValidar = 0;
        int porCargar = 0;

        for (RespuestaData respuestaData : listRespuestas) {
            if (usuario.getIdrol() != 3) {
                if (!Utils.respuestaIsTierra(listPreguntas, respuestaData)) {
                    if (respuestaData.getIdRespuesta() != null) {
                        if (respuestaData.getIdRespuesta() == 2) { //no aplica
                            cumple++;
                        }
                        if (respuestaData.getIdRespuesta() == 3) { //
                            noCumple++;
                        }
                    } else {
                        //noCumple++;
                    }
                }
            } else {
                if (respuestaData.getIdRespuesta() != null) {
                    if (respuestaData.getIdRespuesta() == 2) {
                        cumple++;
                    }
                    if (respuestaData.getIdRespuesta() == 3) {
                        noCumple++;
                    }
                }
            }
        }*/

        ResponseLogin.Usuario usuario = new UsuarioDBMethods(getApplicationContext()).readUsuario();
        int cumple = 0;
        int noCumple = 0;
        int porValidar = 0;
        int porCargar = 0;
        CatalogoBarco barco = listCatalogoBarcos.get(position);
        EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(ChecklistBarcos.this);


        for (RubroData mRubroData : barco.getListRubros())
            for (Pregunta mPregunta : mRubroData.getListPreguntasTemp()) {
                if ((usuario.getIdrol() == 3 || !mPregunta.isTierra())) {
                    List<Evidencia> listEvidencias = null;
                    listEvidencias = evidenciasDBMethods.readEvidencias("SELECT ID_ESTATUS,ID_ETAPA FROM " + evidenciasDBMethods.TP_TRAN_CL_EVIDENCIA +
                                    " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_BARCO = ?" +
                                    " AND ID_ESTATUS != 2",
                            new String[]{String.valueOf(mPregunta.getIdRevision()), String.valueOf(mPregunta.getIdChecklist()),
                                    String.valueOf(mPregunta.getIdRubro()), String.valueOf(mPregunta.getIdPregunta()),
                                    String.valueOf(listCatalogoBarcos.get(spinnerBarco.getSelectedItemPosition()).getIdBarco())});
                    mPregunta.setListEvidencias(listEvidencias);
                    if (mPregunta.getListEvidencias() == null || mPregunta.getListEvidencias().size() == 0)
                        porCargar++;
                    else {
                        int valida = 0;
                        int cump = 0;
                        int noCump = 0;
                        for (Evidencia mEvidencia : mPregunta.getListEvidencias()) {
                            if ((usuario.getIdrol() == 1 && mEvidencia.getIdEtapa() != 1 && mEvidencia.getIdEstatus() == 1) ||
                                    (mEvidencia.getIdEtapa() > usuario.getIdrol() && mEvidencia.getIdEstatus() == 1) ||
                                    (mEvidencia.getIdEtapa() == 2 && mEvidencia.getIdEstatus() == 1 && usuario.getIdrol() == 3) ||
                                    ((mEvidencia.getIdEtapa() == 3 || mEvidencia.getIdEtapa() == 2) && (mEvidencia.getIdEstatus() == 2 || mEvidencia.getIdEstatus() == 1) && usuario.getIdrol() == 4)) {
                                cump++;
                            } else if ((usuario.getIdrol() == 1 && (mEvidencia.getIdEtapa() == 1 || mEvidencia.getIdEtapa() == 2) && mEvidencia.getIdEstatus() == 3) ||
                                    ((mEvidencia.getIdEtapa() == 1 || mEvidencia.getIdEtapa() == 2) && mEvidencia.getIdEstatus() == 3)) {
                                noCump++;
                            } else {
                                valida++;
                            }
                        }
                        if (noCump > 0)
                            noCumple++;
                        else if (valida > 0)
                            porValidar++;
                        else if (cump > 0)
                            cumple++;
                    }
                    mPregunta.setListEvidencias(null);
                }
            }


        textViewCumplenValor.setText(String.valueOf(cumple));
        textViewNoCumplenValor.setText(String.valueOf(noCumple));
        tvPorCargarValor.setText(String.valueOf(porCargar));
        tvPorValidarValor.setText(String.valueOf(porValidar));


    }//*/

    class GuardandoEvidenciasTask extends AsyncTask<String, String, String> {

        private ProgressDialog statusDialog;
        private Activity context;
        private Intent data;
        private int requestCode;

        public GuardandoEvidenciasTask(Activity context, Intent data, int requestCode) {
            this.context = context;
            this.data = data;
            this.requestCode = requestCode;
        }

        protected void onPreExecute() {
            statusDialog = Utils.typhoonLoader(context, "Guardando evidencia...");
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /*if(data != null) {
                Bundle extras = data.getExtras();
                //Imágen desde cámara
                if (extras != null) {
                    Bitmap bitmap = extras.getParcelable("data");
                    Bitmap scaledBitmap = Utils.resizeImageBitmap(bitmap);
                    String base64 = null;
                    String base64Preview = null;
                    int rubro = adapterExpandableChecklist.getRubroPosition();
                    int idrubro = adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().getIdRubro();
                    try {
                        base64 = Utils.bitmapToBase64(bitmap);
                        base64Preview = Utils.bitmapToBase64(scaledBitmap);
                        RespuestaData datosRespuesta = null;
                        for (RespuestaData respuestaData : adapterExpandableChecklist.getListRubros().get(idrubro).getListRespuestas()) {
                            if (respuestaData.getIdPregunta() == adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getIdPregunta()) {
                                datosRespuesta = respuestaData;
                                break;
                            }
                        }

                        Evidencia evidencia = new Evidencia();

                        if (datosRespuesta != null) {
                            evidencia.setIdRegistro(datosRespuesta.getIdRegistro());
                            evidencia.setIdPregunta(datosRespuesta.getIdPregunta());
                            evidencia.setIdRubro(datosRespuesta.getIdRubro());
                            evidencia.setIdChecklist(datosRespuesta.getIdChecklist());
                            evidencia.setIdRevision(datosRespuesta.getIdRevision());
                            evidencia.setIdEstatus(1);
                            evidencia.setIdEtapa(1);
                            evidencia.setContenido(base64);
                            evidencia.setContenidoPreview(base64Preview);
                            evidencia.setNombre(Utils.getDate("yyyyMMddHHmmss") + ".png");
                            evidencia.setIdEvidencia(UUID.randomUUID().toString());
                            CatalogoBarco barco = (CatalogoBarco) spinnerBarco.getSelectedItem();
                            evidencia.setIdBarco(barco.getIdBarco());

                            GPSTracker gps = new GPSTracker(ChecklistBarcos.this);
                            if(gps.canGetLocation()) {
                                double latitude = gps.getLatitude();
                                double longitude = gps.getLongitude();
                                evidencia.setLatitude(latitude);
                                evidencia.setLongitude(longitude);
                                gps.stopUsingGPS();
                            }

                            EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                            evidenciasDBMethods.createEvidencia(evidencia);

                            evidencia.setContenido(null);

                            if (adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {
                                evidencia.setSmallBitmap(scaledBitmap);
                                adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).
                                        getListEvidencias().add(evidencia);
                                //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                            } else {
                                List<Evidencia> listEvidencias = new ArrayList<>();
                                //listEvidencias.add(new Evidencia(scaledBitmap,bitmap,1));
                                evidencia.setSmallBitmap(scaledBitmap);
                                listEvidencias.add(evidencia);
                                adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).setListEvidencias(listEvidencias);
                                //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                            }
                            System.out.println();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.message(getApplicationContext(), "No se pudo guardar la imagen: " + e.getMessage());
                    }
                } else {
                    //Imágen desde galería
                    Uri uri = data.getData();
                    String path = Utils.getRealPathFromURI(ChecklistBarcos.this, uri);
                    //String path2 = Utils.getPathFromUri(ChecklistBarcos.this,uri);
                    int idrubro = adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().getIdRubro();
                    if (path.contains("pdf")) {
                        try {
                            String base64 = Utils.fileToBase64(ChecklistBarcos.this, uri);
                            RespuestaData datosRespuesta = null;
                            for (RespuestaData respuestaData : adapterExpandableChecklist.getListRubros().get(idrubro).getListRespuestas()) {
                                if (respuestaData.getIdPregunta() == adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getIdPregunta()) {
                                    datosRespuesta = respuestaData;
                                    break;
                                }
                            }

                            Evidencia evidencia = new Evidencia();

                            if (datosRespuesta != null) {
                                evidencia.setIdRegistro(datosRespuesta.getIdRegistro());
                                evidencia.setIdPregunta(datosRespuesta.getIdPregunta());
                                evidencia.setIdRubro(datosRespuesta.getIdRubro());
                                evidencia.setIdChecklist(datosRespuesta.getIdChecklist());
                                evidencia.setIdRevision(datosRespuesta.getIdRevision());
                                evidencia.setIdEstatus(1);
                                evidencia.setIdEtapa(1);
                                evidencia.setContenido(base64);
                                //evidencia.setContenidoPreview(base64Preview);
                                //evidencia.setNombre(Utils.getDate("yyyyMMddHHmmss") + ".png");
                                evidencia.setNombre(path);
                                evidencia.setIdEvidencia(UUID.randomUUID().toString());
                                CatalogoBarco barco = (CatalogoBarco) spinnerBarco.getSelectedItem();
                                evidencia.setIdBarco(barco.getIdBarco());

                                GPSTracker gps = new GPSTracker(ChecklistBarcos.this);
                                if(gps.canGetLocation()) {
                                    double latitude = gps.getLatitude();
                                    double longitude = gps.getLongitude();
                                    evidencia.setLatitude(latitude);
                                    evidencia.setLongitude(longitude);
                                    gps.stopUsingGPS();
                                }

                                EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                                evidenciasDBMethods.createEvidencia(evidencia);

                                evidencia.setContenido(null);

                                if (adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {
                                    //evidencia.setSmallBitmap(scaledBitmap);
                                    adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).
                                            getListEvidencias().add(evidencia);
                                    //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                } else {
                                    List<Evidencia> listEvidencias = new ArrayList<>();
                                    //listEvidencias.add(new Evidencia(scaledBitmap,bitmap,1));
                                    //evidencia.setSmallBitmap(scaledBitmap);
                                    listEvidencias.add(evidencia);
                                    adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).setListEvidencias(listEvidencias);
                                    //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return "No se pudo guardar la imagen: " + e.getMessage();
                        }
                    } else {
                        Bitmap bitmap = null;
                        try {
                            bitmap = Utils.getBitmap(getApplicationContext(), uri);
                            bitmap = Utils.resizeImageBitmap(bitmap,768,1024);
                            Bitmap scaledBitmap = Utils.resizeImageBitmap(bitmap);
                            String base64 = null;
                            String base64Preview = null;
                            base64 = Utils.bitmapToBase64(bitmap, path);
                            base64Preview = Utils.bitmapToBase64(scaledBitmap);
                            RespuestaData datosRespuesta = null;
                            for (RespuestaData respuestaData : adapterExpandableChecklist.getListRubros().get(idrubro).getListRespuestas()) {
                                if (respuestaData.getIdPregunta() == adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getIdPregunta()) {
                                    datosRespuesta = respuestaData;
                                    break;
                                }
                            }

                            Evidencia evidencia = new Evidencia();

                            if (datosRespuesta != null) {
                                evidencia.setIdRegistro(datosRespuesta.getIdRegistro());
                                evidencia.setIdPregunta(datosRespuesta.getIdPregunta());
                                evidencia.setIdRubro(datosRespuesta.getIdRubro());
                                evidencia.setIdChecklist(datosRespuesta.getIdChecklist());
                                evidencia.setIdRevision(datosRespuesta.getIdRevision());
                                evidencia.setIdEstatus(1);
                                evidencia.setIdEtapa(1);
                                evidencia.setContenido(base64);
                                evidencia.setContenidoPreview(base64Preview);
                                evidencia.setNombre(path);
                                evidencia.setIdEvidencia(UUID.randomUUID().toString());
                                CatalogoBarco barco = (CatalogoBarco) spinnerBarco.getSelectedItem();
                                evidencia.setIdBarco(barco.getIdBarco());

                                GPSTracker gps = new GPSTracker(ChecklistBarcos.this);
                                if(gps.canGetLocation()) {
                                    double latitude = gps.getLatitude();
                                    double longitude = gps.getLongitude();
                                    evidencia.setLatitude(latitude);
                                    evidencia.setLongitude(longitude);
                                    gps.stopUsingGPS();
                                }

                                EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                                evidenciasDBMethods.createEvidencia(evidencia);

                                evidencia.setContenido(null);

                                if (adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {

                                    evidencia.setSmallBitmap(scaledBitmap);
                                    adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).
                                            getListEvidencias().add(evidencia);
                                    //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                } else {
                                    List<Evidencia> listEvidencias = new ArrayList<>();
                                    //listEvidencias.add(new Evidencia(scaledBitmap,bitmap,1));
                                    evidencia.setSmallBitmap(scaledBitmap);
                                    listEvidencias.add(evidencia);
                                    adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).setListEvidencias(listEvidencias);
                                    //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //Utils.message(getApplicationContext(), "No se pudo guardar la imagen: " + e.getMessage());
                            return "No se pudo guardar la imagen: " + e.getMessage();
                        }
                    }
                }
            }else{
                try {
                    File file = new File(Constants.PATH + "tempPhotos/evidencia.jpg");
                    Uri capturedImageUri = Uri.fromFile(file);
                    Bitmap bitmap = Utils.getBitmap(getApplicationContext(),capturedImageUri);
                    bitmap = Utils.rotateImageIfRequired(getApplicationContext(),bitmap,capturedImageUri);
                    Bitmap scaledBitmap = Utils.resizeImageBitmap(bitmap);
                    bitmap = Utils.resizeImageBitmap(bitmap,768,1024);
                    String base64 = null;
                    String base64Preview = null;
                    int rubro = adapterExpandableChecklist.getRubroPosition();
                    int idrubro = adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().getIdRubro();
                    try {
                        base64 = Utils.bitmapToBase64(bitmap,"jpg");
                        base64Preview = Utils.bitmapToBase64(scaledBitmap);
                        RespuestaData datosRespuesta = null;
                        for (RespuestaData respuestaData : adapterExpandableChecklist.getListRubros().get(idrubro).getListRespuestas()) {
                            if (respuestaData.getIdPregunta() == adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getIdPregunta()) {
                                datosRespuesta = respuestaData;
                                break;
                            }
                        }

                        Evidencia evidencia = new Evidencia();

                        if (datosRespuesta != null) {
                            evidencia.setIdRegistro(datosRespuesta.getIdRegistro());
                            evidencia.setIdPregunta(datosRespuesta.getIdPregunta());
                            evidencia.setIdRubro(datosRespuesta.getIdRubro());
                            evidencia.setIdChecklist(datosRespuesta.getIdChecklist());
                            evidencia.setIdRevision(datosRespuesta.getIdRevision());
                            evidencia.setIdEstatus(1);
                            evidencia.setIdEtapa(1);
                            evidencia.setContenido(base64);
                            evidencia.setContenidoPreview(base64Preview);
                            evidencia.setNombre(Utils.getDate("yyyyMMddHHmmss") + ".jpg");
                            evidencia.setIdEvidencia(UUID.randomUUID().toString());
                            CatalogoBarco barco = (CatalogoBarco) spinnerBarco.getSelectedItem();
                            evidencia.setIdBarco(barco.getIdBarco());

                            GPSTracker gps = new GPSTracker(ChecklistBarcos.this);
                            if(gps.canGetLocation()) {
                                double latitude = gps.getLatitude();
                                double longitude = gps.getLongitude();
                                evidencia.setLatitude(latitude);
                                evidencia.setLongitude(longitude);
                                gps.stopUsingGPS();
                            }

                            EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                            evidenciasDBMethods.createEvidencia(evidencia);

                            evidencia.setContenido(null);

                            if (adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {

                                evidencia.setSmallBitmap(scaledBitmap);
                                adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).
                                        getListEvidencias().add(evidencia);
                                //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                            } else {
                                List<Evidencia> listEvidencias = new ArrayList<>();
                                //listEvidencias.add(new Evidencia(scaledBitmap,bitmap,1));
                                evidencia.setSmallBitmap(scaledBitmap);
                                listEvidencias.add(evidencia);
                                adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).setListEvidencias(listEvidencias);
                                //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //Utils.message(getApplicationContext(), "No se pudo guardar la imagen: " + e.getMessage());
                        return "No se pudo guardar la imagen: " + e.getMessage();
                    }
                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                    //Utils.message(getApplicationContext(), "No se pudo guardar la imagen: " + e.getMessage());
                    return "No se pudo guardar la imagen: " + e.getMessage();
                }
            }//*/
            return "OK";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                String respuesta = guardarEvidencia();
                statusDialog.dismiss();
                //if(!result.equals("OK")){
                if (!respuesta.equals("OK")) {
                    //Utils.message(context,result);
                    Utils.message(context, respuesta);
                } else {
                    adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                    Utils.message(context, "Evidencia guardada");
                }
            } catch (OutOfMemoryError e) {
                adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                statusDialog.dismiss();
                Utils.message(context, "Error de memoria: " + e.getMessage());
            }
        }

        private String guardarEvidencia() {
            ResponseLogin.Usuario usuario = new UsuarioDBMethods(getApplicationContext()).readUsuario();
            if (data != null) {
                Bundle extras = data.getExtras();
                //Imágen desde cámara
                if (extras != null) {
/*                    Bitmap bitmap = extras.getParcelable("data");
                    final Bitmap scaledBitmap = Utils.resizeImageBitmap(bitmap);
                    String base64 = null;
                    String base64Preview = null;
                    int rubro = adapterExpandableChecklist.getRubroPosition();
                    final int idrubro = adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().getIdRubro();*/
                    try {
                        //File file = new File(Constants.PATH + "tempPhotos/evidencia.jpg");
                        File file = new File(Constants.PATH);
                        Uri capturedImageUri = Uri.fromFile(file);
                        Bitmap bitmap = Utils.getBitmap(getApplicationContext(), capturedImageUri);
                        bitmap = Utils.rotateImageIfRequired(getApplicationContext(), bitmap, capturedImageUri);
                        final Bitmap scaledBitmap = Utils.resizeImageBitmap(bitmap);
                        bitmap = Utils.resizeImageBitmap(bitmap, 768, 1024);
                        String base64 = null;
                        String base64Preview = null;
                        int rubro = adapterExpandableChecklist.getRubroPosition();
                        final int idrubro = adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().getIdRubro();
                        try {
                            base64 = Utils.bitmapToBase64(bitmap, "jpg");
                            bitmap.recycle();
                            base64Preview = Utils.bitmapToBase64(scaledBitmap);
                            RespuestaData datosRespuesta = null;
                            for (RespuestaData respuestaData : adapterExpandableChecklist.getListRubros().get(idrubro).getListRespuestas()) {
                                if (respuestaData.getIdPregunta() == adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getIdPregunta()) {
                                    datosRespuesta = respuestaData;
                                    break;
                                }
                            }

                            final Evidencia evidencia = new Evidencia();

                            if (datosRespuesta != null) {
                                evidencia.setIdRegistro(datosRespuesta.getIdRegistro());
                                evidencia.setIdPregunta(datosRespuesta.getIdPregunta());
                                evidencia.setIdRubro(datosRespuesta.getIdRubro());
                                evidencia.setIdChecklist(datosRespuesta.getIdChecklist());
                                evidencia.setIdRevision(datosRespuesta.getIdRevision());
                                evidencia.setIdEstatus(1);
                                //evidencia.setIdEtapa(1);
                                evidencia.setIdEtapa(usuario.getIdrol());
                                if (usuario.getIdrol() == 3) {
                                    evidencia.setAgregadoCoordinador(1);
                                }
                                if (usuario.getIdrol() == 2) {
                                    evidencia.setAgregadoLider(1);
                                }
                                evidencia.setContenido(base64);
                                //evidencia.setContenidoPreview(base64Preview);
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(Utils.getDate("yyyyMMddHHmmss")).append(".jpg");
                                evidencia.setNombre(stringBuilder.toString());
                                //evidencia.setNombre(Utils.getDate("yyyyMMddHHmmss") + ".jpg");
                                evidencia.setIdEvidencia(UUID.randomUUID().toString());
                                CatalogoBarco barco = (CatalogoBarco) spinnerBarco.getSelectedItem();
                                evidencia.setIdBarco(barco.getIdBarco());
                                evidencia.setIdRol(usuario.getIdrol());
                                evidencia.setIdUsuario(usuario.getIdUsuario());

                            /*GPSTracker gps = new GPSTracker(ChecklistBarcos.this,2);
                            if(gps.canGetLocation()) {
                                double latitude = gps.getLatitude();
                                double longitude = gps.getLongitude();
                                evidencia.setLatitude(latitude);
                                evidencia.setLongitude(longitude);
                                gps.stopUsingGPS();
                            }//*/

                                final EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                                evidenciasDBMethods.createEvidencia(evidencia);
                                updateRespuesta(evidencia, 3);

                                evidencia.setContenido(null);

                                if (adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {

                                    evidencia.setSmallBitmap(scaledBitmap);
                                    adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).
                                            getListEvidencias().add(evidencia);
                                    //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                } else {
                                    List<Evidencia> listEvidencias = new ArrayList<>();
                                    //listEvidencias.add(new Evidencia(scaledBitmap,bitmap,1));
                                    evidencia.setSmallBitmap(scaledBitmap);
                                    listEvidencias.add(evidencia);
                                    adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).setListEvidencias(listEvidencias);
                                    //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                }//*/

                                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(ChecklistBarcos.this);

                                if (ActivityCompat.checkSelfPermission(ChecklistBarcos.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ChecklistBarcos.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                }

                                new GetLocationTask(evidencia, ChecklistBarcos.this).execute();

                            /*fusedLocationClient.getLastLocation()
                                    .addOnSuccessListener(ChecklistBarcos.this, new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            // Got last known location. In some rare situations this can be null.
                                            if (location != null) {
                                                // Logic to handle location object
                                                System.out.println("Pruebas: Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
                                                double latitude = location.getLatitude();
                                                double longitude = location.getLongitude();
                                                evidencia.setLatitude(latitude);
                                                evidencia.setLongitude(longitude);
                                                List<Address> addresses;
                                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                                                try {
                                                    addresses = geocoder.getFromLocation(latitude, longitude, 3);
                                                    if(addresses != null){
                                                        if(addresses.size() != 0){
                                                            if(addresses.get(0).getAddressLine(0).length() != 0) {
                                                                evidencia.setLocation(addresses.get(0).getAddressLine(0));
                                                            }
                                                        }
                                                    }
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                ContentValues contentValues = new ContentValues();
                                                contentValues.put("LATITUDE",evidencia.getLatitude());
                                                contentValues.put("LONGITUDE",evidencia.getLongitude());
                                                contentValues.put("LOCATION",evidencia.getLocation());
                                                evidenciasDBMethods.updateEvidencia(contentValues,"ID_EVIDENCIA = ?",
                                                        new String[]{evidencia.getIdEvidencia()});
                                            }
                                        }
                                    });//*/
                                //scaledBitmap.recycle();
                            }
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                            //Utils.message(getApplicationContext(), "No se pudo guardar la imagen: " + e.getMessage());
                            return "No se pudo guardar la imagen: " + e.getMessage();
                        }
                        file.delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "No se pudo guardar la imagen: " + e.getMessage();
                    }
                } else {
                    //Imágen desde galería
                    Uri uri = data.getData();
                    String path = Utils.getRealPathFromURI(ChecklistBarcos.this, uri);
                    //String path2 = Utils.getPathFromUri(ChecklistBarcos.this,uri);
                    int idrubro = adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().getIdRubro();
                    if (path.toLowerCase().contains("pdf")) {
                        try {
                            String base64 = Utils.fileToBase64(ChecklistBarcos.this, uri);
                            RespuestaData datosRespuesta = null;
                            for (RespuestaData respuestaData : adapterExpandableChecklist.getListRubros().get(idrubro).getListRespuestas()) {
                                if (respuestaData.getIdPregunta() == adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getIdPregunta()) {
                                    datosRespuesta = respuestaData;
                                    break;
                                }
                            }

                            final Evidencia evidencia = new Evidencia();

                            if (datosRespuesta != null) {
                                evidencia.setIdRegistro(datosRespuesta.getIdRegistro());
                                evidencia.setIdPregunta(datosRespuesta.getIdPregunta());
                                evidencia.setIdRubro(datosRespuesta.getIdRubro());
                                evidencia.setIdChecklist(datosRespuesta.getIdChecklist());
                                evidencia.setIdRevision(datosRespuesta.getIdRevision());
                                evidencia.setIdEstatus(1);
                                //evidencia.setIdEtapa(1);
                                evidencia.setIdEtapa(usuario.getIdrol());
                                if (usuario.getIdrol() == 3) {
                                    evidencia.setAgregadoCoordinador(1);
                                }
                                if (usuario.getIdrol() == 2) {
                                    evidencia.setAgregadoLider(1);
                                }
                                evidencia.setContenido(base64);
                                //evidencia.setContenidoPreview(base64Preview);
                                //evidencia.setNombre(Utils.getDate("yyyyMMddHHmmss") + ".png");
                                evidencia.setNombre(path);
                                evidencia.setIdEvidencia(UUID.randomUUID().toString());
                                CatalogoBarco barco = (CatalogoBarco) spinnerBarco.getSelectedItem();
                                evidencia.setIdBarco(barco.getIdBarco());
                                evidencia.setIdRol(usuario.getIdrol());
                                evidencia.setIdUsuario(usuario.getIdUsuario());

                                /*GPSTracker gps = new GPSTracker(ChecklistBarcos.this,2);
                                if(gps.canGetLocation()) {
                                    double latitude = gps.getLatitude();
                                    double longitude = gps.getLongitude();
                                    evidencia.setLatitude(latitude);
                                    evidencia.setLongitude(longitude);

                                    List<Address> addresses;
                                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                                    try {
                                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                        if(addresses != null){
                                            if(addresses.size() != 0){
                                                if(addresses.get(0).getAddressLine(0).length() != 0) {
                                                    evidencia.setLocation(addresses.get(0).getAddressLine(0));
                                                }
                                            }
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    gps.stopUsingGPS();
                                }//*/

                                final EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                                evidenciasDBMethods.createEvidencia(evidencia);
                                updateRespuesta(evidencia, 3);

                                evidencia.setContenido(null);

                                if (adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {

                                    //evidencia.setSmallBitmap(scaledBitmap);
                                    adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).
                                            getListEvidencias().add(evidencia);
                                    //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                } else {
                                    List<Evidencia> listEvidencias = new ArrayList<>();
                                    //listEvidencias.add(new Evidencia(scaledBitmap,bitmap,1));
                                    //evidencia.setSmallBitmap(scaledBitmap);
                                    listEvidencias.add(evidencia);
                                    adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).setListEvidencias(listEvidencias);
                                    //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                }

                                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(ChecklistBarcos.this);

                                if (ActivityCompat.checkSelfPermission(ChecklistBarcos.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ChecklistBarcos.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                }

                                new GetLocationTask(evidencia, ChecklistBarcos.this).execute();

                                /*fusedLocationClient.getLastLocation()
                                        .addOnSuccessListener(ChecklistBarcos.this, new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {
                                                // Got last known location. In some rare situations this can be null.
                                                if (location != null) {
                                                    // Logic to handle location object
                                                    System.out.println("Pruebas: Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
                                                    double latitude = location.getLatitude();
                                                    double longitude = location.getLongitude();
                                                    evidencia.setLatitude(latitude);
                                                    evidencia.setLongitude(longitude);
                                                    List<Address> addresses;
                                                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                                                    try {
                                                        addresses = geocoder.getFromLocation(latitude, longitude, 3);
                                                        if(addresses != null){
                                                            if(addresses.size() != 0){
                                                                if(addresses.get(0).getAddressLine(0).length() != 0) {
                                                                    evidencia.setLocation(addresses.get(0).getAddressLine(0));
                                                                }
                                                            }
                                                        }
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                    ContentValues contentValues = new ContentValues();
                                                    contentValues.put("LATITUDE",evidencia.getLatitude());
                                                    contentValues.put("LONGITUDE",evidencia.getLongitude());
                                                    contentValues.put("LOCATION",evidencia.getLocation());
                                                    evidenciasDBMethods.updateEvidencia(contentValues,"ID_EVIDENCIA = ?",
                                                            new String[]{evidencia.getIdEvidencia()});
                                                }
                                            }
                                        });//*/
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            return "No se pudo guardar la imagen: " + e.getMessage();
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                            return "No se pudo guardar la imagen: " + e.getMessage();
                        }
                    } else {
                        Bitmap bitmap = null;
                        try {
                            bitmap = Utils.getBitmap(getApplicationContext(), uri);
                            bitmap = Utils.resizeImageBitmap(bitmap, 768, 1024);
                            Bitmap scaledBitmap = Utils.resizeImageBitmap(bitmap);
                            String base64 = null;
                            String base64Preview = null;
                            base64 = Utils.bitmapToBase64(bitmap, path);
                            bitmap.recycle();
                            base64Preview = Utils.bitmapToBase64(scaledBitmap);
                            RespuestaData datosRespuesta = null;
                            for (RespuestaData respuestaData : adapterExpandableChecklist.getListRubros().get(idrubro).getListRespuestas()) {
                                if (respuestaData.getIdPregunta() == adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getIdPregunta()) {
                                    datosRespuesta = respuestaData;
                                    break;
                                }
                            }

                            Evidencia evidencia = new Evidencia();

                            if (datosRespuesta != null) {
                                evidencia.setIdRegistro(datosRespuesta.getIdRegistro());
                                evidencia.setIdPregunta(datosRespuesta.getIdPregunta());
                                evidencia.setIdRubro(datosRespuesta.getIdRubro());
                                evidencia.setIdChecklist(datosRespuesta.getIdChecklist());
                                evidencia.setIdRevision(datosRespuesta.getIdRevision());
                                evidencia.setIdEstatus(1);
                                //evidencia.setIdEtapa(1);
                                evidencia.setIdEtapa(usuario.getIdrol());
                                if (usuario.getIdrol() == 3) {
                                    evidencia.setAgregadoCoordinador(1);
                                }
                                evidencia.setContenido(base64);
                                //evidencia.setContenidoPreview(base64Preview);
                                evidencia.setNombre(path);
                                evidencia.setIdEvidencia(UUID.randomUUID().toString());
                                CatalogoBarco barco = (CatalogoBarco) spinnerBarco.getSelectedItem();
                                evidencia.setIdBarco(barco.getIdBarco());

                                GPSTracker gps = new GPSTracker(ChecklistBarcos.this, 2);
                                if (gps.canGetLocation()) {
                                    double latitude = gps.getLatitude();
                                    double longitude = gps.getLongitude();
                                    evidencia.setLatitude(latitude);
                                    evidencia.setLongitude(longitude);
                                    gps.stopUsingGPS();
                                }

                                EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                                evidenciasDBMethods.createEvidencia(evidencia);
                                updateRespuesta(evidencia, 3);

                                evidencia.setContenido(null);

                                if (adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {

                                    evidencia.setSmallBitmap(scaledBitmap);
                                    adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).
                                            getListEvidencias().add(evidencia);
                                    //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                } else {
                                    List<Evidencia> listEvidencias = new ArrayList<>();
                                    //listEvidencias.add(new Evidencia(scaledBitmap,bitmap,1));
                                    evidencia.setSmallBitmap(scaledBitmap);
                                    listEvidencias.add(evidencia);
                                    adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).setListEvidencias(listEvidencias);
                                    //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                }
                                //scaledBitmap.recycle();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            return "No se pudo guardar la imagen: " + e.getMessage();
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                            return "No se pudo guardar la imagen: " + e.getMessage();
                        }
                    }
                }
            } else {
                try {
                    //File file = new File(Constants.PATH + "tempPhotos/evidencia.jpg");
                    File file = new File(Constants.PATH);
                    Uri capturedImageUri = Uri.fromFile(file);
                    Bitmap bitmap = Utils.getBitmap(getApplicationContext(), capturedImageUri);
                    bitmap = Utils.rotateImageIfRequired(getApplicationContext(), bitmap, capturedImageUri);
                    final Bitmap scaledBitmap = Utils.resizeImageBitmap(bitmap);
                    bitmap = Utils.resizeImageBitmap(bitmap, 768, 1024);
                    String base64 = null;
                    String base64Preview = null;
                    int rubro = adapterExpandableChecklist.getRubroPosition();
                    final int idrubro = adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().getIdRubro();
                    try {
                        base64 = Utils.bitmapToBase64(bitmap, "jpg");
                        bitmap.recycle();
                        base64Preview = Utils.bitmapToBase64(scaledBitmap);
                        RespuestaData datosRespuesta = null;
                        for (RespuestaData respuestaData : adapterExpandableChecklist.getListRubros().get(idrubro).getListRespuestas()) {
                            if (respuestaData.getIdPregunta() == adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getIdPregunta()) {
                                datosRespuesta = respuestaData;
                                break;
                            }
                        }

                        final Evidencia evidencia = new Evidencia();

                        if (datosRespuesta != null) {
                            evidencia.setIdRegistro(datosRespuesta.getIdRegistro());
                            evidencia.setIdPregunta(datosRespuesta.getIdPregunta());
                            evidencia.setIdRubro(datosRespuesta.getIdRubro());
                            evidencia.setIdChecklist(datosRespuesta.getIdChecklist());
                            evidencia.setIdRevision(datosRespuesta.getIdRevision());
                            evidencia.setIdEstatus(1);
                            //evidencia.setIdEtapa(1);
                            evidencia.setIdEtapa(usuario.getIdrol());
                            if (usuario.getIdrol() == 3) {
                                evidencia.setAgregadoCoordinador(1);
                            }
                            if (usuario.getIdrol() == 2) {
                                evidencia.setAgregadoLider(1);
                            }
                            evidencia.setContenido(base64);
                            //evidencia.setContenidoPreview(base64Preview);
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(Utils.getDate("yyyyMMddHHmmss")).append(".jpg");
                            evidencia.setNombre(stringBuilder.toString());
                            //evidencia.setNombre(Utils.getDate("yyyyMMddHHmmss") + ".jpg");
                            evidencia.setIdEvidencia(UUID.randomUUID().toString());
                            CatalogoBarco barco = (CatalogoBarco) spinnerBarco.getSelectedItem();
                            evidencia.setIdBarco(barco.getIdBarco());
                            evidencia.setIdRol(usuario.getIdrol());
                            evidencia.setIdUsuario(usuario.getIdUsuario());

                            /*GPSTracker gps = new GPSTracker(ChecklistBarcos.this,2);
                            if(gps.canGetLocation()) {
                                double latitude = gps.getLatitude();
                                double longitude = gps.getLongitude();
                                evidencia.setLatitude(latitude);
                                evidencia.setLongitude(longitude);
                                gps.stopUsingGPS();
                            }//*/

                            final EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                            evidenciasDBMethods.createEvidencia(evidencia);
                            updateRespuesta(evidencia, 3);

                            evidencia.setContenido(null);

                            if (adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {

                                evidencia.setSmallBitmap(scaledBitmap);
                                adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).
                                        getListEvidencias().add(evidencia);
                                //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                            } else {
                                List<Evidencia> listEvidencias = new ArrayList<>();
                                //listEvidencias.add(new Evidencia(scaledBitmap,bitmap,1));
                                evidencia.setSmallBitmap(scaledBitmap);
                                listEvidencias.add(evidencia);
                                adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).setListEvidencias(listEvidencias);
                                //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                            }//*/

                            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(ChecklistBarcos.this);

                            if (ActivityCompat.checkSelfPermission(ChecklistBarcos.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ChecklistBarcos.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            }

                            new GetLocationTask(evidencia, ChecklistBarcos.this).execute();

                            /*fusedLocationClient.getLastLocation()
                                    .addOnSuccessListener(ChecklistBarcos.this, new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            // Got last known location. In some rare situations this can be null.
                                            if (location != null) {
                                                // Logic to handle location object
                                                System.out.println("Pruebas: Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
                                                double latitude = location.getLatitude();
                                                double longitude = location.getLongitude();
                                                evidencia.setLatitude(latitude);
                                                evidencia.setLongitude(longitude);
                                                List<Address> addresses;
                                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                                                try {
                                                    addresses = geocoder.getFromLocation(latitude, longitude, 3);
                                                    if(addresses != null){
                                                        if(addresses.size() != 0){
                                                            if(addresses.get(0).getAddressLine(0).length() != 0) {
                                                                evidencia.setLocation(addresses.get(0).getAddressLine(0));
                                                            }
                                                        }
                                                    }
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                ContentValues contentValues = new ContentValues();
                                                contentValues.put("LATITUDE",evidencia.getLatitude());
                                                contentValues.put("LONGITUDE",evidencia.getLongitude());
                                                contentValues.put("LOCATION",evidencia.getLocation());
                                                evidenciasDBMethods.updateEvidencia(contentValues,"ID_EVIDENCIA = ?",
                                                        new String[]{evidencia.getIdEvidencia()});
                                            }
                                        }
                                    });//*/
                            //scaledBitmap.recycle();
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                        //Utils.message(getApplicationContext(), "No se pudo guardar la imagen: " + e.getMessage());
                        return "No se pudo guardar la imagen: " + e.getMessage();
                    }
                    file.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                    return "No se pudo guardar la imagen: " + e.getMessage();
                }
            }
            return "OK";
        }
    }

    private void sincronizacionDialog(final ChecklistBarcos activity, final int idRevision) {
        LayoutInflater li = LayoutInflater.from(activity);
        LinearLayout layoutDialog = (LinearLayout) li.inflate(R.layout.dialog_sincronizacion_layout, null);

        TextView textViewCancelar = (TextView) layoutDialog.findViewById(R.id.buttonCancelar);
        TextView textViewSincronizar = (TextView) layoutDialog.findViewById(R.id.buttonSincronizar);
        TextView textViewTituloDialogoSincronizacion = layoutDialog.findViewById(R.id.textViewTituloDialogoSincronizacion);
        LinearLayout linearLayoutCancelar = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutCancelar);
        LinearLayout linearLayoutSincronizar = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutSincronizar);

        //textViewTituloDialogoSincronizacion.setText("¿Deseas sincronizar tus evidencias?");

        final AlertDialog dialog = new AlertDialog.Builder(activity)
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

        textViewSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SincronizacionRequestService(activity, activity, idRevision).execute();
                //new SincronizacionIndividualRequestService(activity,activity,idRevision,listCatalogoBarcos,null,activity,null).execute();
                dialog.dismiss();
            }
        });

        linearLayoutSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SincronizacionRequestService(activity, activity, idRevision).execute();
                //new SincronizacionIndividualRequestService(activity,activity,idRevision,listCatalogoBarcos,null,activity,null).execute();
                dialog.dismiss();
            }
        });
    }

    private void loadData() {
        listCatalogoBarcos = new BarcoDBMethods(this).readBarcos();
        ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(this);
        EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(this);
        List<ChecklistData> listChecklist = checklistDBMethods.readChecklists(
                "SELECT ID_REVISION,ID_CHECKLIST,ID_ESTATUS,ID_LOGO,ID_TIPO_REVISION,NOMBRE,PONDERACION FROM " + checklistDBMethods.TP_CAT_CHEKLIST + " WHERE ID_REVISION = ?",
                new String[]{String.valueOf(folio)});
        ResponseLogin.Usuario usuario = new UsuarioDBMethods(getApplicationContext()).readUsuario();

        if (listChecklist.size() != 0) {
            ChecklistData checklistData = listChecklist.get(0);
            textViewTituloChecklist.setText(checklistData.getNombre());
            for (CatalogoBarco catalogoBarco : listCatalogoBarcos) {
                List<RubroData> listRubros = checklistDBMethods.readRubro(
                        "SELECT ID_REVISION,ID_CHECKLIST,ID_RUBRO,ESTATUS,NOMBRE FROM " + checklistDBMethods.TP_CAT_CL_RUBRO + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ?",
                        new String[]{String.valueOf(checklistData.getIdRevision()), String.valueOf(checklistData.getIdChecklist())});
                for (RubroData rubroData : listRubros) {
                    String query = null;
                    if (usuario.getIdrol() == 3) {
                        query = "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_TIPO_RESPUESTA,ID_RUBRO,ESTATUS,DESCRIPCION,IS_TIERRA,SELECCIONADO FROM " +
                                checklistDBMethods.TP_CAT_CL_PREGUNTA + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ?";
                    } else {
                        query = "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_TIPO_RESPUESTA,ID_RUBRO,ESTATUS,DESCRIPCION,IS_TIERRA,SELECCIONADO FROM " +
                                checklistDBMethods.TP_CAT_CL_PREGUNTA + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND IS_TIERRA = 0";
                    }
                    List<Pregunta> listPreguntas = checklistDBMethods.readPregunta(query,
                            new String[]{String.valueOf(rubroData.getIdRevision()), String.valueOf(rubroData.getIdChecklist()),
                                    String.valueOf(rubroData.getIdRubro())});

                    /*List<Pregunta> listPreguntas = checklistDBMethods.readPregunta("WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ?",
                            new String[]{String.valueOf(rubroData.getIdRevision()), String.valueOf(rubroData.getIdChecklist()),
                                    String.valueOf(rubroData.getIdRubro())});//*/

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
                        pregunta.setSeleccionado(Utils.isPreguntaSeleccionada(listRespuestas, pregunta));
                    }
                    /*} catch (IOException e) {
                        e.printStackTrace();
                    }//*/
                }
                for (int i = 0; i < listRubros.size(); i++) {
                    if (listRubros.get(i).getListPreguntasTemp() != null) {
                        if (listRubros.get(i).getListPreguntasTemp().size() == 0) {
                            listRubros.remove(i);
                        }
                    } else {
                        listRubros.remove(i);
                    }
                }
                catalogoBarco.setListRubros(listRubros);
            }
            System.out.println();
        }
    }

    private void updateRespuesta(Evidencia evidencia, Integer idRespuesta) {
        if (evidencia != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID_RESPUESTA", idRespuesta);
            contentValues.put("SINCRONIZADO", 0);
            new ChecklistDBMethods(ChecklistBarcos.this).updateRespuesta(contentValues, "ID_REVISION = ? AND " +
                            "ID_CHECKLIST = ? AND ID_PREGUNTA = ? AND ID_RUBRO = ? AND ID_BARCO = ? AND ID_REGISTRO = ?",
                    new String[]{String.valueOf(evidencia.getIdRevision()), String.valueOf(evidencia.getIdChecklist()), String.valueOf(evidencia.getIdPregunta()),
                            String.valueOf(evidencia.getIdRubro()), String.valueOf(evidencia.getIdBarco()), String.valueOf(evidencia.getIdRegistro())});
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
        if (sharedPreferences.contains(Constants.SP_LOGIN_TAG)) {
            if (!sharedPreferences.getBoolean(Constants.SP_LOGIN_TAG, false)) {
                finish();
            }
        }
    }

    class GetLocationTask extends AsyncTask<String, String, String> {

        private Evidencia evidencia;
        private Activity activity;
        private EvidenciasDBMethods evidenciasDBMethods;

        public GetLocationTask(Evidencia evidencia, Activity activity) {
            this.evidencia = evidencia;
            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... strings) {

            evidenciasDBMethods = new EvidenciasDBMethods(activity);

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(ChecklistBarcos.this);

            if (ActivityCompat.checkSelfPermission(ChecklistBarcos.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ChecklistBarcos.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(ChecklistBarcos.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                Log.e(TAG, "Pruebas: Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                //latitude = Double.parseDouble("19.308507"); // Se hardcodea para probar que ubicacion se colaca en las evidencias
                                //longitude = Double.parseDouble("-99.186068");
                                evidencia.setLatitude(latitude);
                                evidencia.setLongitude(longitude);
                                List<Address> addresses;
                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                                try {
                                    addresses = geocoder.getFromLocation(latitude, longitude, 3);
                                    if (addresses != null) {
                                        if (addresses.size() != 0) {
                                            if (addresses.get(0).getAddressLine(0).length() != 0) {
                                                evidencia.setLocation(addresses.get(0).getAddressLine(0));
                                            }
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ContentValues contentValues = new ContentValues();
                                contentValues.put("LATITUDE", evidencia.getLatitude());
                                contentValues.put("LONGITUDE", evidencia.getLongitude());
                                contentValues.put("LOCATION", evidencia.getLocation());
                                evidenciasDBMethods.updateEvidencia(contentValues, "ID_EVIDENCIA = ?",
                                        new String[]{evidencia.getIdEvidencia()});

                            }
                        }
                    });

            return "OK";
        }
    }
}
