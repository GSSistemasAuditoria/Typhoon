package com.elektra.typhoon.checklist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.adapters.AdapterExpandableChecklist;
import com.elektra.typhoon.adapters.SpinnerBarcosAdapter;
import com.elektra.typhoon.objetos.response.Barco;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.Rubro;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist_layout);

        Spinner spinnerBarco = (Spinner) findViewById(R.id.spinnerBarcos);
        textViewNombreBarco = (TextView) findViewById(R.id.textViewNombreBarco);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListViewChecklist);
        TextView textViewValorTotal = findViewById(R.id.textViewValorTotal);
        TextView textViewCumplenValor = findViewById(R.id.textViewCumplenValor);
        TextView textViewNoCumplenValor = findViewById(R.id.textViewNoCumplenValor);

        listBarcos = new ArrayList<>();
        listBarcos.add(new Barco(1,"Far Sentinel"));
        listBarcos.add(new Barco(2,"Barco 1"));
        listBarcos.add(new Barco(3,"Barco 2"));

        /*CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);//*/

        List<Rubro> listRubros2 = new ArrayList<>();
        List<Pregunta> listPreguntas = new ArrayList<>();
        listPreguntas.add(new Pregunta());
        listPreguntas.add(new Pregunta());
        listPreguntas.add(new Pregunta());
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
        expandableListView.setAdapter(adapterExpandableChecklist);

        SpinnerBarcosAdapter spinnerBarcosAdapter = new SpinnerBarcosAdapter(ChecklistBarcos.this,R.layout.item_spinner_layout,listBarcos);
        spinnerBarco.setAdapter(spinnerBarcosAdapter);

        spinnerBarco.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(listBarcos.size() != 0) {
                    String nombre = listBarcos.get(i).getNombre();
                    if (nombre != null) {
                        textViewNombreBarco.setText(nombre);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
}
