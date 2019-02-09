package com.elektra.typhoon.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.objetos.ItemCatalogo;
import com.elektra.typhoon.objetos.response.Barco;

import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 17/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class SpinnerAdapter extends ArrayAdapter<ItemCatalogo> {

    private Context context;
    private List<ItemCatalogo> listItems;
    private int idResource;
    private LayoutInflater inflater;

    public SpinnerAdapter(@NonNull Context context, int resource, List<ItemCatalogo> listItems) {
        super(context, resource, listItems);
        this.context = context;
        this.listItems = listItems;
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

        ItemCatalogo itemCatalogo = listItems.get(position);
        textViewItem.setText(itemCatalogo.getDescripcion());
        //textViewItem.setText(String.valueOf(itemCatalogo.getId()));

        return view;
    }
}
