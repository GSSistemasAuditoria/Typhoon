package com.elektra.typhoon.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.Rubro;

import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 18/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class AdapterExpandableChecklist extends BaseExpandableListAdapter{

    private List<Rubro> listRubros;
    private Activity activity;
    private int rubroPosition;
    private AdapterRecycleViewPreguntas adapterRecycleViewPreguntasTemp;
    private TextView textViewCumplen;
    private TextView textViewNoCumplen;

    public AdapterRecycleViewPreguntas getAdapterRecycleViewPreguntasTemp() {
        return adapterRecycleViewPreguntasTemp;
    }

    public List<Rubro> getListRubros() {
        return listRubros;
    }

    public int getRubroPosition() {
        return rubroPosition;
    }

    public AdapterExpandableChecklist(List<Rubro> listRubros, Activity activity,TextView textViewCumplen,TextView textViewNoCumplen){
        this.listRubros = listRubros;
        this.activity = activity;
        this.textViewCumplen = textViewCumplen;
        this.textViewNoCumplen = textViewNoCumplen;
    }

    @Override
    public int getGroupCount() {
        return listRubros.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return listRubros.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listRubros.get(i).getListPreguntas().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        Rubro rubro = listRubros.get(i);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.header_layout, null);
        }

        TextView textViewTituloEncabezado = (TextView) view.findViewById(R.id.textViewTituloHeader);
        textViewTituloEncabezado.setText("Rubro " + i);

        rubroPosition = i;

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        Rubro rubro = listRubros.get(i);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.child_layout, null);
        }

        //TextView textViewTituloRubro = (TextView) view.findViewById(R.id.textViewTituloRubro);
        TextView textViewCumplenValor = (TextView) view.findViewById(R.id.textViewCumplenValor);
        TextView textViewNoCumpleValor = (TextView) view.findViewById(R.id.textViewNoCumplenValor);
        TextView textViewValorTotal = (TextView) view.findViewById(R.id.textViewValorTotal);
        RecyclerView recyclerViewPreguntas = (RecyclerView) view.findViewById(R.id.recyclerViewPreguntas);

        //textViewTituloRubro.setText("Rubro " + i);
        AdapterRecycleViewPreguntas adapterRecycleViewPreguntas = new AdapterRecycleViewPreguntas(rubro.getListPreguntas(),activity,i,
                textViewCumplen,textViewNoCumplen,this);
        recyclerViewPreguntas.setAdapter(adapterRecycleViewPreguntas);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerViewPreguntas.setLayoutManager(layoutManager);
        recyclerViewPreguntas.setNestedScrollingEnabled(false);
        adapterRecycleViewPreguntasTemp = adapterRecycleViewPreguntas;
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    public void contarPreguntasCumplen(){
        int cumple = 0;
        int noCumple = 0;
        for(Rubro rubro:listRubros){
            for(Pregunta pregunta:rubro.getListPreguntas()){
                if(pregunta.isCumple()){
                    cumple++;
                }else{
                    noCumple++;
                }
            }
        }
        textViewCumplen.setText(String.valueOf(cumple));
        textViewNoCumplen.setText(String.valueOf(noCumple));
    }
}
