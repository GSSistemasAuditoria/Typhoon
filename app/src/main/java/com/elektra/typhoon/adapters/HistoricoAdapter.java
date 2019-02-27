package com.elektra.typhoon.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.objetos.response.Historico;

import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 25/02/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class HistoricoAdapter extends BaseAdapter {

    private Activity activity;
    private List<Historico> listHistorico;

    public HistoricoAdapter(Activity activity,List<Historico> listHistorico){
        this.activity = activity;
        this.listHistorico = listHistorico;
    }

    @Override
    public int getCount() {
        return listHistorico.size();
    }

    @Override
    public Object getItem(int i) {
        return listHistorico.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Historico historico = listHistorico.get(i);
        View item = new View(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        item = inflater.inflate(R.layout.historico_item_layout, viewGroup, false);

        TextView textViewFecha = item.findViewById(R.id.textViewHistoricoFecha);
        TextView textViewMotivo = item.findViewById(R.id.textViewHistoricoMotivo);

        textViewFecha.setText(historico.getFechaMod());
        textViewMotivo.setText(historico.getMotivo());

        return item;
    }
}
