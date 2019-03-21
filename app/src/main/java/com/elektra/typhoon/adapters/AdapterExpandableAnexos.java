package com.elektra.typhoon.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.database.ChecklistDBMethods;
import com.elektra.typhoon.objetos.response.Anexo;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.objetos.response.RubroData;

import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 18/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class AdapterExpandableAnexos extends BaseExpandableListAdapter{

    private List<Anexo> listAnexosHeader;
    private Activity activity;
    private int header;
    private AdapterRecycleViewItemsAnexos adapterRecycleViewItemsAnexosTemp;

    public AdapterExpandableAnexos(List<Anexo> listAnexosHeader, Activity activity){
        this.listAnexosHeader = listAnexosHeader;
        this.activity = activity;
    }

    public List<Anexo> getListAnexosHeader() {
        return listAnexosHeader;
    }

    public int getHeader() {
        return header;
    }

    public AdapterRecycleViewItemsAnexos getAdapterRecycleViewItemsAnexosTemp() {
        return adapterRecycleViewItemsAnexosTemp;
    }

    @Override
    public int getGroupCount() {
        return listAnexosHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return listAnexosHeader.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listAnexosHeader.get(i).getListAnexos().get(i1);
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
        Anexo anexoHeader = listAnexosHeader.get(i);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.header_layout, null);
        }

        TextView textViewTituloEncabezado = view.findViewById(R.id.textViewTituloHeader);
        ImageView imageView =  view.findViewById(R.id.imageViewIconoGrupo);
        textViewTituloEncabezado.setText(anexoHeader.getTitulo());

        header = i;

        if (b) {
            imageView.setImageResource(R.mipmap.ic_group_close);
        } else {
            imageView.setImageResource(R.mipmap.ic_group_open);
        }

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        Anexo anexoHeader = listAnexosHeader.get(i);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.child_layout, null);
        }

        RecyclerView recyclerViewItemsAnexos = view.findViewById(R.id.recyclerViewPreguntas);
        AdapterRecycleViewItemsAnexos adapterRecycleViewItemsAnexos = new AdapterRecycleViewItemsAnexos(anexoHeader.getListAnexos(),activity,i);
        recyclerViewItemsAnexos.setAdapter(adapterRecycleViewItemsAnexos);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerViewItemsAnexos.setLayoutManager(layoutManager);
        recyclerViewItemsAnexos.setNestedScrollingEnabled(false);
        adapterRecycleViewItemsAnexosTemp = adapterRecycleViewItemsAnexos;

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
