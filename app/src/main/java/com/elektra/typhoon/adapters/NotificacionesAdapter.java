package com.elektra.typhoon.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.objetos.response.Notificacion;

import java.util.List;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 03/05/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class NotificacionesAdapter extends BaseAdapter {

    private List<Notificacion> listnotificaciones;
    private Activity activity;

    public NotificacionesAdapter(List<Notificacion> listnotificaciones,Activity activity){
        this.listnotificaciones = listnotificaciones;
        this.activity = activity;
    }

    public List<Notificacion> getListnotificaciones() {
        return listnotificaciones;
    }

    @Override
    public int getCount() {
        return listnotificaciones.size();
    }

    @Override
    public Object getItem(int i) {
        return listnotificaciones.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Notificacion notificacion = listnotificaciones.get(i);

        View item = new View(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        item = inflater.inflate(R.layout.item_notificacion_layout, viewGroup, false);

        TextView textViewTitulo = item.findViewById(R.id.textViewNotificacionTitulo);
        TextView textViewTexto = item.findViewById(R.id.textViewNotificacionTexto);

        textViewTitulo.setText(notificacion.getTitle());
        textViewTexto.setText(notificacion.getBody());

        return item;
    }
}
