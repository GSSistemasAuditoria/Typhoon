package com.elektra.typhoon.checklist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import com.elektra.typhoon.database.FoliosDBMethods;
import com.elektra.typhoon.objetos.response.Barco;
import com.elektra.typhoon.objetos.response.CatalogoBarco;
import com.elektra.typhoon.objetos.response.ChecklistData;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.Rubro;
import com.elektra.typhoon.objetos.response.RubroData;
import com.elektra.typhoon.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 17/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class ChecklistBarcos extends AppCompatActivity{

    private TextView textViewNombreBarco;
    private List<Barco> listBarcos;
    private ExpandableListView expandableListView;
    private AdapterExpandableChecklist adapterExpandableChecklist;
    private int folio;
    private String fechaInicio;
    private String fechaFin;
    private TextView textViewValorTotal;
    private TextView textViewCumplenValor;
    private TextView textViewNoCumplenValor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist_layout);

        folio = getIntent().getIntExtra(Constants.INTENT_FOLIO_TAG,0);
        fechaInicio = getIntent().getStringExtra(Constants.INTENT_FECHA_INICIO_TAG);
        fechaFin = getIntent().getStringExtra(Constants.INTENT_FECHA_FIN_TAG);

        Spinner spinnerBarco = (Spinner) findViewById(R.id.spinnerBarcos);
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

        List<CatalogoBarco> listCatalogoBarcos = new BarcoDBMethods(this).readBarcos(null,null);
        ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(this);

        List<ChecklistData> listChecklist = checklistDBMethods.readChecklists("WHERE ID_REVISION = ?",new String[]{String.valueOf(folio)});

        if(listChecklist.size() != 0) {
            ChecklistData checklistData = listChecklist.get(0);
            List<RubroData> listRubros = checklistDBMethods.readRubro("WHERE ID_REVISION = ? AND ID_CHECKLIST = ?",
                    new String[]{String.valueOf(checklistData.getIdRevision()),String.valueOf(checklistData.getIdChecklist())});
            System.out.println();
        }

        listBarcos = new ArrayList<>();
        listBarcos.add(new Barco(1,"Far Sentinel"));
        listBarcos.add(new Barco(2,"Barco 1"));
        listBarcos.add(new Barco(3,"Barco 2"));

        /*CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);//*/

        for(Barco barco:listBarcos){
            List<Rubro> listRubros2 = new ArrayList<>();
            listRubros2.add(new Rubro());
            listRubros2.add(new Rubro());
            listRubros2.add(new Rubro());
            listRubros2.add(new Rubro());

            for(Rubro rubro:listRubros2){
                rubro.setListPreguntas(getPreguntas());
            }
            barco.setListRubros(listRubros2);
        }

        /*List<Rubro> listRubros2 = new ArrayList<>();
        listRubros2.add(new Rubro());
        listRubros2.add(new Rubro());
        listRubros2.add(new Rubro());
        listRubros2.add(new Rubro());

        for(Rubro rubro:listRubros2){
            rubro.setListPreguntas(getPreguntas());
        }

        int numeroPreguntas = 0;
        for(Rubro rubro:listRubros2){
            if(rubro.getListPreguntas() != null) {
                numeroPreguntas += rubro.getListPreguntas().size();
            }
        }

        textViewValorTotal.setText(String.valueOf(numeroPreguntas));
        textViewNoCumplenValor.setText(String.valueOf(numeroPreguntas));

        adapterExpandableChecklist = new AdapterExpandableChecklist(listRubros2,ChecklistBarcos.this,
                textViewCumplenValor,textViewNoCumplenValor);
        expandableListView.setAdapter(adapterExpandableChecklist);//*/

        SpinnerBarcosAdapter spinnerBarcosAdapter = new SpinnerBarcosAdapter(ChecklistBarcos.this,R.layout.item_spinner_layout,listBarcos);
        spinnerBarco.setAdapter(spinnerBarcosAdapter);

        spinnerBarco.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                new CargaDatosChecklistTask(ChecklistBarcos.this,i).execute();

                /*if(listBarcos.size() != 0) {
                    Barco barco = listBarcos.get(i);
                    String nombre = barco.getNombre();
                    if (nombre != null) {
                        textViewNombreBarco.setText(nombre);
                    }

                    int numeroPreguntas = 0;
                    for(Rubro rubro:barco.getListRubros()){
                        if(rubro.getListPreguntas() != null) {
                            numeroPreguntas += rubro.getListPreguntas().size();
                        }
                    }

                    textViewValorTotal.setText(String.valueOf(numeroPreguntas));
                    textViewNoCumplenValor.setText(String.valueOf(numeroPreguntas));

                    int cumple = 0;
                    int noCumple = 0;
                    for(Rubro rubro:listBarcos.get(i).getListRubros()){
                        for(Pregunta pregunta:rubro.getListPreguntas()){
                            if(pregunta.isCumple()){
                                cumple++;
                            }else{
                                noCumple++;
                            }
                        }
                    }
                    textViewCumplenValor.setText(String.valueOf(cumple));
                    textViewNoCumplenValor.setText(String.valueOf(noCumple));

                    adapterExpandableChecklist = new AdapterExpandableChecklist(barco.getListRubros(),ChecklistBarcos.this,
                            textViewCumplenValor,textViewNoCumplenValor);
                    expandableListView.setAdapter(adapterExpandableChecklist);
                }//*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
            Bitmap bitmap = extras.getParcelable("data");
            Bitmap scaledBitmap = Utils.resizeImageBitmap(bitmap);
            int rubro = adapterExpandableChecklist.getRubroPosition();
            int idrubro = adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().getIdRubro();
            if(adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntas().get(requestCode).getListEvidencias() != null) {
                adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntas().get(requestCode).
                        getListEvidencias().add(new Evidencia(scaledBitmap,bitmap,getNewIdEvidencia(
                        adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntas().
                                get(requestCode).getListEvidencias())));
                adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
            }else{
                List<Evidencia> listEvidencias = new ArrayList<>();
                listEvidencias.add(new Evidencia(scaledBitmap,bitmap,1));
                adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntas().get(requestCode).setListEvidencias(listEvidencias);
                adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
            }
            System.out.println();
        }
    }

    private int getNewIdEvidencia(List<Evidencia> listEvidencias){
        int id = 0;
        for(Evidencia evidencia:listEvidencias){
            if(evidencia.getIdEvidencia() > id){
                id = evidencia.getIdEvidencia();
            }
        }
        return id + 1;
    }

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
            /*statusDialog.setMessage("cargando datos");
            statusDialog.setCancelable(false);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP ||
                    Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
                statusDialog.setIndeterminate(true);
                Drawable drawable = new ProgressBar(context).getIndeterminateDrawable().mutate();
                drawable.setColorFilter(ContextCompat.getColor(context, R.color.verde),
                        PorterDuff.Mode.SRC_IN);
                statusDialog.setIndeterminateDrawable(drawable);
            }else{
                statusDialog.setIndeterminate(false);
            }
            statusDialog.show();//*/
        }

        @Override
        protected String doInBackground(String... strings) {
            //publishProgress("Cargando datos");

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return "OK";
        }

        @Override
        protected void onPostExecute(String result) {
            statusDialog.dismiss();
            if(!result.equals("OK")){
                Utils.message(context,result);
            }else{
                try{
                    if(listBarcos.size() != 0) {
                        Barco barco = listBarcos.get(position);
                        String nombre = barco.getNombre();
                        if (nombre != null) {
                            textViewNombreBarco.setText(nombre);
                        }

                        int numeroPreguntas = 0;
                        for(Rubro rubro:barco.getListRubros()){
                            if(rubro.getListPreguntas() != null) {
                                numeroPreguntas += rubro.getListPreguntas().size();
                            }
                        }

                        textViewValorTotal.setText(String.valueOf(numeroPreguntas));
                        textViewNoCumplenValor.setText(String.valueOf(numeroPreguntas));

                        int cumple = 0;
                        int noCumple = 0;
                        for(Rubro rubro:listBarcos.get(position).getListRubros()){
                            for(Pregunta pregunta:rubro.getListPreguntas()){
                                if(pregunta.isCumple()){
                                    cumple++;
                                }else{
                                    noCumple++;
                                }
                            }
                        }
                        textViewCumplenValor.setText(String.valueOf(cumple));
                        textViewNoCumplenValor.setText(String.valueOf(noCumple));

                        adapterExpandableChecklist = new AdapterExpandableChecklist(barco.getListRubros(),ChecklistBarcos.this,
                                textViewCumplenValor,textViewNoCumplenValor);
                        expandableListView.setAdapter(adapterExpandableChecklist);
                    }
                }catch (Exception e){
                    Utils.message(context,"No se pudieron cargar los datos: " + e.getMessage());
                }
            }
        }
    }
}
