package com.elektra.typhoon.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.objetos.response.Barco;
import com.elektra.typhoon.objetos.response.CatalogoBarco;

import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 17/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class SpinnerBarcosAdapter extends ArrayAdapter<CatalogoBarco> {

    private Context context;
    private List<CatalogoBarco> listItems;
    private int idResource;
    private LayoutInflater inflater;

    public SpinnerBarcosAdapter(@NonNull Context context, int resource, List<CatalogoBarco> barcos) {
        super(context, resource, barcos);
        this.context = context;
        this.listItems = barcos;
        this.idResource = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        View view = null;
        view = inflater.inflate(idResource, parent, false);
        TextView textViewItem = (TextView) view.findViewById(R.id.textViewSpinnerItem);

        CatalogoBarco barco = listItems.get(position);
        textViewItem.setText(barco.getNombre());

        return view;
    }
}
