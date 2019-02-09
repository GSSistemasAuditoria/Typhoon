package com.elektra.typhoon.checklist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.adapters.AdapterExpandableChecklist;
import com.elektra.typhoon.adapters.SpinnerBarcosAdapter;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.BarcoDBMethods;
import com.elektra.typhoon.database.ChecklistDBMethods;
import com.elektra.typhoon.database.EvidenciasDBMethods;
import com.elektra.typhoon.database.FoliosDBMethods;
import com.elektra.typhoon.objetos.response.Barco;
import com.elektra.typhoon.objetos.response.CatalogoBarco;
import com.elektra.typhoon.objetos.response.ChecklistData;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.PreguntaData;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.objetos.response.Rubro;
import com.elektra.typhoon.objetos.response.RubroData;
import com.elektra.typhoon.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 17/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class ChecklistBarcos extends AppCompatActivity{

    private TextView textViewNombreBarco;
    //private List<Barco> listBarcos;
    private List<CatalogoBarco> listCatalogoBarcos;
    private ExpandableListView expandableListView;
    private AdapterExpandableChecklist adapterExpandableChecklist;
    private int folio;
    private String fechaInicio;
    private String fechaFin;
    private TextView textViewValorTotal;
    private TextView textViewCumplenValor;
    private TextView textViewNoCumplenValor;
    private Spinner spinnerBarco;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist_layout);

        folio = getIntent().getIntExtra(Constants.INTENT_FOLIO_TAG, 0);
        fechaInicio = getIntent().getStringExtra(Constants.INTENT_FECHA_INICIO_TAG);
        fechaFin = getIntent().getStringExtra(Constants.INTENT_FECHA_FIN_TAG);

        spinnerBarco = (Spinner) findViewById(R.id.spinnerBarcos);
        textViewNombreBarco = (TextView) findViewById(R.id.textViewNombreBarco);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListViewChecklist);
        textViewValorTotal = findViewById(R.id.textViewValorTotal);
        textViewCumplenValor = findViewById(R.id.textViewCumplenValor);
        textViewNoCumplenValor = findViewById(R.id.textViewNoCumplenValor);

        TextView textViewFolio = findViewById(R.id.textViewFolio);
        TextView textViewFechaInicio = findViewById(R.id.textViewFechaInicio);
        TextView textViewFechaFin = findViewById(R.id.textViewFechaFin);

        textViewFolio.setText("" + folio);
        textViewFechaInicio.setText(fechaSinHoras(fechaInicio));
        textViewFechaFin.setText(fechaSinHoras(fechaFin));//*/

        listCatalogoBarcos = new BarcoDBMethods(this).readBarcos(null, null);
        ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(this);
        EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(this);

        List<ChecklistData> listChecklist = checklistDBMethods.readChecklists("WHERE ID_REVISION = ?", new String[]{String.valueOf(folio)});

        if (listChecklist.size() != 0) {
            ChecklistData checklistData = listChecklist.get(0);
            for (CatalogoBarco catalogoBarco : listCatalogoBarcos) {
                List<RubroData> listRubros = checklistDBMethods.readRubro("WHERE ID_REVISION = ? AND ID_CHECKLIST = ?",
                        new String[]{String.valueOf(checklistData.getIdRevision()), String.valueOf(checklistData.getIdChecklist())});
                for (RubroData rubroData : listRubros) {
                    List<Pregunta> listPreguntas = checklistDBMethods.readPregunta("WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ?",
                            new String[]{String.valueOf(rubroData.getIdRevision()), String.valueOf(rubroData.getIdChecklist()),
                                    String.valueOf(rubroData.getIdRubro())});

                    rubroData.setListPreguntasTemp(listPreguntas);

                    List<RespuestaData> listRespuestas = checklistDBMethods.readRespuesta("WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_BARCO = ?"
                            , new String[]{String.valueOf(rubroData.getIdRevision()), String.valueOf(rubroData.getIdChecklist()),
                                    String.valueOf(rubroData.getIdRubro()), String.valueOf(catalogoBarco.getIdBarco())});

                    rubroData.setListRespuestas(listRespuestas);

                    try {
                        for (Pregunta pregunta : listPreguntas) {
                            List<Evidencia> listEvidencias = evidenciasDBMethods.readEvidencias("" +
                                            "WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_BARCO = ?",
                                    new String[]{String.valueOf(pregunta.getIdRevision()), String.valueOf(pregunta.getIdChecklist()),
                                            String.valueOf(pregunta.getIdRubro()), String.valueOf(pregunta.getIdPregunta()),
                                            String.valueOf(catalogoBarco.getIdBarco())},false);
                            pregunta.setListEvidencias(listEvidencias);
                            pregunta.setIdBarco(catalogoBarco.getIdBarco());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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

        //actualizarValores(spinnerBarco.getSelectedItemPosition());
    }

    private String fechaSinHoras(String fecha){
        if(fecha.contains(" ")){
            String[] temp = fecha.split(" ");
            return temp[0];
        }else{
            return fecha;
        }
    }

    public List<Pregunta> getPreguntas(){
        List<Pregunta> listPreguntas = new ArrayList<>();
        listPreguntas.add(new Pregunta());
        listPreguntas.add(new Pregunta());
        listPreguntas.add(new Pregunta());
        return listPreguntas;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if(extras != null) {
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

                        EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                        evidenciasDBMethods.createEvidencia(evidencia);

                        evidencia.setContenido(null);

                        if (adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {
                        /*adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).
                                getListEvidencias().add(new Evidencia(scaledBitmap,bitmap,getNewIdEvidencia(
                                adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().
                                        get(requestCode).getListEvidencias())));//*/
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
            }else{
                Uri uri = data.getData();
                String path = Utils.getRealPathFromURI(ChecklistBarcos.this,uri);
                //String path2 = Utils.getPathFromUri(ChecklistBarcos.this,uri);
                int idrubro = adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().getIdRubro();
                if(path.contains("pdf")) {
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

                            EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                            evidenciasDBMethods.createEvidencia(evidencia);

                            evidencia.setContenido(null);

                            if (adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {
                        /*adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).
                                getListEvidencias().add(new Evidencia(scaledBitmap,bitmap,getNewIdEvidencia(
                                adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().
                                        get(requestCode).getListEvidencias())));//*/
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
                }else{
                    Bitmap bitmap = null;
                    try {
                        bitmap = Utils.getBitmap(getApplicationContext(),uri);
                        Bitmap scaledBitmap = Utils.resizeImageBitmap(bitmap);
                        String base64 = null;
                        String base64Preview = null;
                        base64 = Utils.bitmapToBase64(bitmap,path);
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
                            //evidencia.setIdEstatus();
                            evidencia.setIdEtapa(1);
                            evidencia.setContenido(base64);
                            evidencia.setContenidoPreview(base64Preview);
                            evidencia.setNombre(path);
                            evidencia.setIdEvidencia(UUID.randomUUID().toString());
                            CatalogoBarco barco = (CatalogoBarco) spinnerBarco.getSelectedItem();
                            evidencia.setIdBarco(barco.getIdBarco());

                            EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                            evidenciasDBMethods.createEvidencia(evidencia);

                            evidencia.setContenido(null);

                            if (adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {
                        /*adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).
                                getListEvidencias().add(new Evidencia(scaledBitmap,bitmap,getNewIdEvidencia(
                                adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().
                                        get(requestCode).getListEvidencias())));//*/
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
            //bitmap.recycle();
            //scaledBitmap.recycle();
        }
    }

    /*private int getNewIdEvidencia(List<Evidencia> listEvidencias){
        int id = 0;
        for(Evidencia evidencia:listEvidencias){
            if(evidencia.getIdEvidencia() > id){
                id = evidencia.getIdEvidencia();
            }
        }
        return id + 1;
    }//*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    class CargaDatosChecklistTask extends AsyncTask<String,String,String> {

        private ProgressDialog statusDialog;
        private Context context;
        private int position;

        public CargaDatosChecklistTask(Context context,int position){
            this.context = context;
            this.position = position;
        }

        protected void onPreExecute() {
            statusDialog = Utils.typhoonLoader(context,"Cargando datos...");
        }

        @Override
        protected String doInBackground(String... strings) {
            //publishProgress("Cargando datos");

            /*try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }//*/

            return "OK";
        }

        @Override
        protected void onPostExecute(String result) {
            //statusDialog.dismiss();
            if(!result.equals("OK")){
                Utils.message(context,result);
            }else{
                try{
                    if(listCatalogoBarcos.size() != 0) {
                        CatalogoBarco barco = listCatalogoBarcos.get(position);
                        String nombre = barco.getNombre();
                        if (nombre != null) {
                            textViewNombreBarco.setText(nombre);
                        }

                        if(barco.getListRubros() != null) {
                            int numeroPreguntas = 0;
                            for (RubroData rubro : barco.getListRubros()) {
                                if (rubro.getListPreguntasTemp() != null) {
                                    numeroPreguntas += rubro.getListPreguntasTemp().size();
                                }
                            }

                            textViewValorTotal.setText(String.valueOf(numeroPreguntas));
                            textViewNoCumplenValor.setText(String.valueOf(numeroPreguntas));

                            /*int cumple = 0;
                            int noCumple = 0;
                            for (RubroData rubro : listCatalogoBarcos.get(position).getListRubros()) {
                                for (Pregunta pregunta : rubro.getListPreguntasTemp()) {
                                    if (pregunta.isCumple()) {
                                        cumple++;
                                    } else {
                                        noCumple++;
                                    }
                                }
                            }
                            textViewCumplenValor.setText(String.valueOf(cumple));
                            textViewNoCumplenValor.setText(String.valueOf(noCumple));//*/

                            actualizarValores(position);

                            adapterExpandableChecklist = new AdapterExpandableChecklist(barco.getListRubros(), ChecklistBarcos.this,
                                    textViewCumplenValor, textViewNoCumplenValor);
                            expandableListView.setAdapter(adapterExpandableChecklist);
                            statusDialog.dismiss();
                        }else{
                            statusDialog.dismiss();
                            Utils.message(context,"No se ha descargado el checklist");
                        }
                    }else{
                        statusDialog.dismiss();
                        Utils.message(context,"No se ha descargado el catálogo de barcos");
                    }
                }catch (Exception e){
                    statusDialog.dismiss();
                    Utils.message(context,"No se pudieron cargar los datos: " + e.getMessage());
                }
            }
        }
    }

    /*private void actualizarValores(int position){
        int cumple = 0;
        int noCumple = 0;
        if(listCatalogoBarcos.size() != 0) {
            for (RubroData rubro : listCatalogoBarcos.get(position).getListRubros()) {
                for (Pregunta pregunta : rubro.getListPreguntasTemp()) {
                    if (pregunta.isCumple()) {
                        cumple++;
                    } else {
                        noCumple++;
                    }
                }
            }
            textViewCumplenValor.setText(String.valueOf(cumple));
            textViewNoCumplenValor.setText(String.valueOf(noCumple));
        }
    }//*/

    private void actualizarValores(int position){
        int cumple = 0;
        int noCumple = 0;
        if(listCatalogoBarcos.size() != 0) {
            for (RubroData rubro : listCatalogoBarcos.get(position).getListRubros()) {
                for (Pregunta pregunta : rubro.getListPreguntasTemp()) {
                    //if (pregunta.isCumple()) {
                    if (aplicaPregunta(pregunta.getListEvidencias())) {
                        cumple++;
                    } else {
                        noCumple++;
                    }
                }
            }
            textViewCumplenValor.setText(String.valueOf(cumple));
            textViewNoCumplenValor.setText(String.valueOf(noCumple));
        }
    }//*/

    private boolean aplicaPregunta(List<Evidencia> evidencias){
        if(evidencias.size() != 0) {
            for (Evidencia evidencia : evidencias) {
                if ((evidencia.getIdEtapa() == 1 && evidencia.getIdEstatus() == 2) || (evidencia.getIdEtapa() == 1 && evidencia.getIdEstatus() == 1)) {
                    return false;
                }
            }
            return true;
        }else{
            return false;
        }
    }
}
