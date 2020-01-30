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
import android.support.annotation.NonNull;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.adapters.AdapterExpandableChecklist;
import com.elektra.typhoon.adapters.AdapterRecycleViewPreguntas;
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
import com.elektra.typhoon.objetos.response.Rubro;
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
    private List<CatalogoBarco> listCatalogoBarcos;
    private ExpandableListView expandableListView;
    private AdapterExpandableChecklist adapterExpandableChecklist;

    private int folio;
    private String fechaInicio;
    private int estatus;
    private TextView textViewValorTotal;
    private TextView textViewCumplenValor;
    private TextView textViewNoCumplenValor;
    private TextView tvPorCargarValor;
    private TextView tvPorValidarValor;
    private Spinner spinnerBarco;
    private TextView textViewTituloChecklist;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private double latitude;
    private double longitude;
    private int countToGetLocation;
    private Encryption mEncryption;
    private CatalogoBarco mBarcoSelected;
    private ResponseLogin.Usuario mUsuario;

    private List<RubroData> lstRubros;
    private ChecklistDBMethods checklistDBMethods;
    int groupExpand;

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
                        Log.d(TAG, String.format(Locale.US, "%s -- %s", latitude, longitude));
                    }
                }
            };

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonSincronizarChecklist:
                    sincronizacionDialog(ChecklistBarcos.this, folio);
                    break;
                case R.id.buttonAnexos:
                    Intent intent = new Intent(ChecklistBarcos.this, AnexosActivity.class);
                    intent.putExtra(Constants.INTENT_FOLIO_TAG, mEncryption.encryptAES(String.valueOf(folio)));
                    intent.putExtra(Constants.INTENT_FECHA_INICIO_TAG, mEncryption.encryptAES(fechaInicio));
                    intent.putExtra(Constants.INTENT_ESTATUS_TAG, mEncryption.encryptAES(String.valueOf(estatus)));
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist_layout);
        initLocationGPS();
        lstRubros = new ArrayList<>();
        listCatalogoBarcos = new BarcoDBMethods(this).readBarcos();
        mEncryption = new Encryption();
        folio = Integer.parseInt(mEncryption.decryptAES(getIntent().getStringExtra(Constants.INTENT_FOLIO_TAG)));
        fechaInicio = mEncryption.decryptAES(Normalizer.normalize(getIntent().getStringExtra(Constants.INTENT_FECHA_INICIO_TAG), Normalizer.Form.NFD));
        spinnerBarco = findViewById(R.id.spinnerBarcos);
        textViewNombreBarco = findViewById(R.id.textViewNombreBarco);
        estatus = Integer.parseInt(mEncryption.decryptAES(getIntent().getStringExtra(Constants.INTENT_ESTATUS_TAG)));
        mBarcoSelected = new CatalogoBarco();

        textViewValorTotal = findViewById(R.id.textViewValorTotal);
        textViewCumplenValor = findViewById(R.id.textViewCumplenValor);
        textViewNoCumplenValor = findViewById(R.id.textViewNoCumplenValor);
        tvPorValidarValor = findViewById(R.id.tvPorValidarValor);
        tvPorCargarValor = findViewById(R.id.tvPorCargarValor);

        ((TextView) findViewById(R.id.textViewFolio)).setText(String.valueOf(folio));
        ((TextView) findViewById(R.id.textViewFechaInicio)).setText(Utils.getDateMonth(fechaInicio));

        CatalogosDBMethods catalogosDBMethods = new CatalogosDBMethods(this);
        String descripcion = catalogosDBMethods.readEstatusDescription(estatus);
        if (descripcion != null) {
            ((TextView) findViewById(R.id.textViewFechaFin)).setText(descripcion);
        }

        textViewTituloChecklist = findViewById(R.id.textViewTituloChecklist);

        UsuarioDBMethods usuarioDBMethods = new UsuarioDBMethods(this);
        mUsuario = usuarioDBMethods.readUsuario();
        if (mUsuario != null) {
            ((TextView) findViewById(R.id.textViewNombreUsuario)).setText(mUsuario.getNombre());
            ((TextView) findViewById(R.id.textViewRol)).setText(catalogosDBMethods.readRolUsuario(mUsuario.getIdrol()));
        }
        findViewById(R.id.buttonSincronizarChecklist).setOnClickListener(mOnClickListener);
        findViewById(R.id.buttonAnexos).setOnClickListener(mOnClickListener);

        checklistDBMethods = new ChecklistDBMethods(this);
        spinnerBarco.setAdapter(new ArrayAdapter<CatalogoBarco>(this, R.layout.item_spinner_layout, listCatalogoBarcos) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createItemView(position, convertView, parent);
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createItemView(position, convertView, parent);
            }

            private View createItemView(int position, View convertView, ViewGroup parent) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner_layout, parent, false);
                ((TextView) convertView.findViewById(R.id.textViewSpinnerItem)).setText(getItem(position).getNombre());
                return convertView;
            }
        });
        spinnerBarco.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //new CargaDatosChecklistTask(ChecklistBarcos.this, i).execute();
                expandableListView.collapseGroup(groupExpand);
                new AsyncTaskGral(ChecklistBarcos.this, new Delegate() {
                    @Override
                    public void getDelegate(String result) {
                    }

                    @Override
                    public String executeInBackground() {
                        loadBarco();
                        return null;
                    }
                }, "Cargando Checklist...").execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        expandableListView = findViewById(R.id.expandableListViewChecklist);
        adapterExpandableChecklist = new AdapterExpandableChecklist(lstRubros, ChecklistBarcos.this,
                fechaInicio, mBarcoSelected);
        expandableListView.setAdapter(adapterExpandableChecklist);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupExpand != groupPosition) {
                    expandableListView.collapseGroup(groupExpand);
                    groupExpand = groupPosition;
                    expandableListView.setSelectionFromTop(groupPosition, 0);
                }
            }
        });
    }

    public void loadBarco(){
        List<ChecklistData> listChecklist = checklistDBMethods.readChecklists(
                "SELECT ID_REVISION,ID_CHECKLIST,ID_ESTATUS,ID_LOGO,ID_TIPO_REVISION,NOMBRE,PONDERACION FROM " + ChecklistDBMethods.TP_CAT_CHEKLIST + " WHERE ID_REVISION = ?",
                new String[]{String.valueOf(folio)});

        if (listChecklist.size() != 0) {
            ChecklistData checklistData = listChecklist.get(0);
            textViewTituloChecklist.setText(checklistData.getNombre());

            checklistDBMethods.updateRubros(
                    "SELECT ID_REVISION,ID_CHECKLIST,ID_RUBRO,ESTATUS,NOMBRE FROM " + ChecklistDBMethods.TP_CAT_CL_RUBRO + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ?",
                    new String[]{String.valueOf(checklistData.getIdRevision()), String.valueOf(checklistData.getIdChecklist())}, lstRubros);
            for (RubroData rubroData : lstRubros) {
                String query;
                //rubroData.setSeleccionado(true);
                if (mUsuario.getIdrol() == 3) {
                    query = "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_TIPO_RESPUESTA,ID_RUBRO,ESTATUS,DESCRIPCION,IS_TIERRA,SELECCIONADO FROM " +
                            ChecklistDBMethods.TP_CAT_CL_PREGUNTA + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ?";
                } else {
                    query = "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_TIPO_RESPUESTA,ID_RUBRO,ESTATUS,DESCRIPCION,IS_TIERRA,SELECCIONADO FROM " +
                            ChecklistDBMethods.TP_CAT_CL_PREGUNTA + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND IS_TIERRA = 0";
                }
                List<Pregunta> listPreguntas = checklistDBMethods.readPregunta(query,
                        new String[]{String.valueOf(rubroData.getIdRevision()), String.valueOf(rubroData.getIdChecklist()),
                                String.valueOf(rubroData.getIdRubro())});

                rubroData.setListPreguntasTemp(listPreguntas);

                List<RespuestaData> listRespuestas = checklistDBMethods.readRespuesta(
                        "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_RUBRO,ID_ESTATUS,ID_BARCO,ID_REGISTRO,ID_RESPUESTA,SINCRONIZADO FROM " + ChecklistDBMethods.TP_TRAN_CL_RESPUESTA + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_BARCO = ?"
                        , new String[]{String.valueOf(rubroData.getIdRevision()), String.valueOf(rubroData.getIdChecklist()),
                                String.valueOf(rubroData.getIdRubro()), String.valueOf(((CatalogoBarco) spinnerBarco.getSelectedItem()).getIdBarco())});

                rubroData.setListRespuestas(listRespuestas);


                for (Pregunta pregunta : rubroData.getListPreguntasTemp()) {
                    try {
                        List<Evidencia> listEvidencias = new EvidenciasDBMethods(ChecklistBarcos.this).readEvidencias("SELECT ID_EVIDENCIA,NOMBRE,CONTENIDO_PREVIEW,ID_ESTATUS,ID_ETAPA,ID_REVISION,ID_CHECKLIST," +
                                        "ID_RUBRO,ID_PREGUNTA,ID_REGISTRO,ID_BARCO,CONTENIDO,LATITUDE,LONGITUDE,AGREGADO_COORDINADOR,NUEVO,FECHA_MOD," +
                                        "LOCATION,ID_ROL,ID_USUARIO,AGREGADO_LIDER FROM " + EvidenciasDBMethods.TP_TRAN_CL_EVIDENCIA +
                                        " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_BARCO = ?" +
                                        " AND ID_ESTATUS != 2",
                                new String[]{String.valueOf(pregunta.getIdRevision()), String.valueOf(pregunta.getIdChecklist()),
                                        String.valueOf(pregunta.getIdRubro()), String.valueOf(pregunta.getIdPregunta()),
                                        String.valueOf(((CatalogoBarco) spinnerBarco.getSelectedItem()).getIdBarco())}, false);
                        pregunta.setListEvidencias(listEvidencias);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            actualizarValores();
            mBarcoSelected.updateBarco((CatalogoBarco) spinnerBarco.getSelectedItem());
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapterExpandableChecklist.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            new GuardandoEvidenciasTask(ChecklistBarcos.this, data, requestCode).execute();
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

    public void actualizarValores() {
        ResponseLogin.Usuario usuario = new UsuarioDBMethods(getApplicationContext()).readUsuario();
        int cumple = 0;
        int noCumple = 0;
        int porValidar = 0;
        int porCargar = 0;

        if (lstRubros != null) {
            for (RubroData mRubroData : lstRubros)
                if (mRubroData.getListPreguntasTemp() != null)
                    for (Pregunta mPregunta : mRubroData.getListPreguntasTemp()) {
                        if ((usuario.getIdrol() == 3 || !mPregunta.isTierra())) {
                            /*List<Evidencia> listEvidencias = null;
                            listEvidencias = evidenciasDBMethods.readEvidencias("SELECT ID_ESTATUS,ID_ETAPA FROM " + evidenciasDBMethods.TP_TRAN_CL_EVIDENCIA +
                                            " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_BARCO = ?" +
                                            " AND ID_ESTATUS != 2",
                                    new String[]{String.valueOf(mPregunta.getIdRevision()), String.valueOf(mPregunta.getIdChecklist()),
                                            String.valueOf(mPregunta.getIdRubro()), String.valueOf(mPregunta.getIdPregunta()),
                                            String.valueOf(listCatalogoBarcos.get(spinnerBarco.getSelectedItemPosition()).getIdBarco())});
                            mPregunta.setListEvidencias(listEvidencias);
                            */
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
                            //mPregunta.setListEvidencias(null);
                        }
                    }
        }

        final int finalPorValidar = porValidar;
        final int finalPorCargar = porCargar;
        final int finalNoCumple = noCumple;
        final int finalCumple = cumple;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewCumplenValor.setText(String.valueOf(finalCumple));
                textViewNoCumplenValor.setText(String.valueOf(finalNoCumple));
                tvPorCargarValor.setText(String.valueOf(finalPorCargar));
                tvPorValidarValor.setText(String.valueOf(finalPorValidar));
            }
        });

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
            return guardarEvidencia();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.equals("OK")) {
                    Utils.message(context, result);
                } else {
                    Utils.message(context, "Evidencia guardada");
                }
                statusDialog.dismiss();
            } catch (OutOfMemoryError e) {
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
                    try {
                        //File file = new File(Constants.PATH + "tempPhotos/evidencia.jpg");
                        File file = new File(Constants.PATH);
                        Uri capturedImageUri = Uri.fromFile(file);
                        Bitmap bitmap = Utils.getBitmap(getApplicationContext(), capturedImageUri);
                        bitmap = Utils.rotateImageIfRequired(getApplicationContext(), bitmap, capturedImageUri);
                        final Bitmap scaledBitmap = Utils.resizeImageBitmap(bitmap);
                        bitmap = Utils.resizeImageBitmap(bitmap, 768, 1024);
                        String base64;
                        int rubro = adapterExpandableChecklist.getRubroPosition();
                        //final int idrubro = lstRubros.get(rubro).getIdRubro();
                        try {
                            base64 = Utils.bitmapToBase64(bitmap, "jpg");
                            bitmap.recycle();
                            RespuestaData datosRespuesta = null;
                            for (RespuestaData respuestaData : adapterExpandableChecklist.getListRubros().get(rubro).getListRespuestas()) {
                                if (respuestaData.getIdPregunta() == adapterExpandableChecklist.getListRubros().get(rubro).getListPreguntasTemp().get(requestCode).getIdPregunta()) {
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


                                final EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                                evidenciasDBMethods.createEvidencia(evidencia);
                                updateRespuesta(evidencia, 3);

                                evidencia.setContenido(null);

                                if (adapterExpandableChecklist.getListRubros().get(rubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {

                                    evidencia.setSmallBitmap(scaledBitmap);
                                    adapterExpandableChecklist.getListRubros().get(rubro).getListPreguntasTemp().get(requestCode).
                                            getListEvidencias().add(evidencia);
                                    //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                } else {
                                    List<Evidencia> listEvidencias = new ArrayList<>();
                                    //listEvidencias.add(new Evidencia(scaledBitmap,bitmap,1));
                                    evidencia.setSmallBitmap(scaledBitmap);
                                    listEvidencias.add(evidencia);
                                    adapterExpandableChecklist.getListRubros().get(rubro).getListPreguntasTemp().get(requestCode).setListEvidencias(listEvidencias);
                                    //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                }//*/

                                //FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(ChecklistBarcos.this);

                                if (ActivityCompat.checkSelfPermission(ChecklistBarcos.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ChecklistBarcos.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                }

                                new GetLocationTask(evidencia, ChecklistBarcos.this).execute();

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
                    int rubro = adapterExpandableChecklist.getRubroPosition();
                    if (path.contains("pdf")) {
                        try {
                            String base64 = Utils.fileToBase64(ChecklistBarcos.this, uri);
                            RespuestaData datosRespuesta = null;
                            for (RespuestaData respuestaData : adapterExpandableChecklist.getListRubros().get(rubro).getListRespuestas()) {
                                if (respuestaData.getIdPregunta() == adapterExpandableChecklist.getListRubros().get(rubro).getListPreguntasTemp().get(requestCode).getIdPregunta()) {
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

                                final EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                                evidenciasDBMethods.createEvidencia(evidencia);
                                updateRespuesta(evidencia, 3);

                                evidencia.setContenido(null);

                                if (adapterExpandableChecklist.getListRubros().get(rubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {

                                    //evidencia.setSmallBitmap(scaledBitmap);
                                    adapterExpandableChecklist.getListRubros().get(rubro).getListPreguntasTemp().get(requestCode).
                                            getListEvidencias().add(evidencia);
                                    //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                } else {
                                    List<Evidencia> listEvidencias = new ArrayList<>();
                                    //listEvidencias.add(new Evidencia(scaledBitmap,bitmap,1));
                                    //evidencia.setSmallBitmap(scaledBitmap);
                                    listEvidencias.add(evidencia);
                                    adapterExpandableChecklist.getListRubros().get(rubro).getListPreguntasTemp().get(requestCode).setListEvidencias(listEvidencias);
                                    //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                }

                                //FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(ChecklistBarcos.this);

                                if (ActivityCompat.checkSelfPermission(ChecklistBarcos.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ChecklistBarcos.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                }

                                new GetLocationTask(evidencia, ChecklistBarcos.this).execute();
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
                            for (RespuestaData respuestaData : adapterExpandableChecklist.getListRubros().get(rubro).getListRespuestas()) {
                                if (respuestaData.getIdPregunta() == adapterExpandableChecklist.getListRubros().get(rubro).getListPreguntasTemp().get(requestCode).getIdPregunta()) {
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

                                if (adapterExpandableChecklist.getListRubros().get(rubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {

                                    evidencia.setSmallBitmap(scaledBitmap);
                                    adapterExpandableChecklist.getListRubros().get(rubro).getListPreguntasTemp().get(requestCode).
                                            getListEvidencias().add(evidencia);
                                    //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                                } else {
                                    List<Evidencia> listEvidencias = new ArrayList<>();
                                    //listEvidencias.add(new Evidencia(scaledBitmap,bitmap,1));
                                    evidencia.setSmallBitmap(scaledBitmap);
                                    listEvidencias.add(evidencia);
                                    adapterExpandableChecklist.getListRubros().get(rubro).getListPreguntasTemp().get(requestCode).setListEvidencias(listEvidencias);
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
                    //final int idrubro = lstRubros.get(rubro).getIdRubro();
                    try {
                        base64 = Utils.bitmapToBase64(bitmap, "jpg");
                        bitmap.recycle();
                        base64Preview = Utils.bitmapToBase64(scaledBitmap);
                        RespuestaData datosRespuesta = null;
                        for (RespuestaData respuestaData : adapterExpandableChecklist.getListRubros().get(rubro).getListRespuestas()) {
                            if (respuestaData.getIdPregunta() == adapterExpandableChecklist.getListRubros().get(rubro).getListPreguntasTemp().get(requestCode).getIdPregunta()) {
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

                            final EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                            evidenciasDBMethods.createEvidencia(evidencia);
                            updateRespuesta(evidencia, 3);

                            evidencia.setContenido(null);

                            if (adapterExpandableChecklist.getListRubros().get(rubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {

                                evidencia.setSmallBitmap(scaledBitmap);
                                adapterExpandableChecklist.getListRubros().get(rubro).getListPreguntasTemp().get(requestCode).
                                        getListEvidencias().add(evidencia);
                                //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                            } else {
                                List<Evidencia> listEvidencias = new ArrayList<>();
                                //listEvidencias.add(new Evidencia(scaledBitmap,bitmap,1));
                                evidencia.setSmallBitmap(scaledBitmap);
                                listEvidencias.add(evidencia);
                                adapterExpandableChecklist.getListRubros().get(rubro).getListPreguntasTemp().get(requestCode).setListEvidencias(listEvidencias);
                                //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                            }//*/

                            new GetLocationTask(evidencia, ChecklistBarcos.this).execute();

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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapterExpandableChecklist.getAdapterPreguntas().notifyItemChanged(requestCode);
                }
            });
            return "OK";
        }
    }

    private void sincronizacionDialog(final ChecklistBarcos activity, final int idRevision) {
        LayoutInflater li = LayoutInflater.from(activity);
        LinearLayout layoutDialog = (LinearLayout) li.inflate(R.layout.dialog_sincronizacion_layout, null);

        TextView textViewCancelar = layoutDialog.findViewById(R.id.buttonCancelar);
        TextView textViewSincronizar = layoutDialog.findViewById(R.id.buttonSincronizar);
        LinearLayout linearLayoutCancelar = layoutDialog.findViewById(R.id.linearLayoutCancelar);
        LinearLayout linearLayoutSincronizar = layoutDialog.findViewById(R.id.linearLayoutSincronizar);

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
                dialog.dismiss();
            }
        });

        linearLayoutSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SincronizacionRequestService(activity, activity, idRevision).execute();
                dialog.dismiss();
            }
        });
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
