package com.elektra.typhoon.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.objetos.Folio;

import java.util.ArrayList;
/**
 * Proyecto: TYPHOON
 * Autor: Francis Susana Carreto Espinoza
 * Fecha: 10/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class AdapterReciclerViewCartera extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Folio> folios;
    private static final int header = 0;
    private static final int item = 1;

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewFolio;
        public TextView textViewFecha;
        public TextView textViewDescripcion;

        public ItemViewHolder(View v) {
            super(v);
            textViewFolio = v.findViewById(R.id.textViewFolio);
            textViewFecha = v.findViewById(R.id.textViewFecha);
            textViewDescripcion = v.findViewById(R.id.textViewDescripcion);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        public TextView headerTitle;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerTitle = (TextView)itemView.findViewById(R.id.textViewAno);
        }
    }

    public AdapterReciclerViewCartera(Context context, ArrayList<Folio> folios) {
        this.context = context;
        this.folios = folios;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == item) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recycler_view_cartera, parent, false);
            return new ItemViewHolder(v);
        }else if(viewType == header){
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_header_recycler_view, parent, false);
            return new HeaderViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder)holder).textViewFolio.setText("Folio:" + folios.get(position).getFolio());
            ((ItemViewHolder)holder).textViewFecha.setText(folios.get(position).getFecha());
            ((ItemViewHolder)holder).textViewDescripcion.setText(folios.get(position).getDescripcion());
        }else if(holder instanceof HeaderViewHolder){
            ((HeaderViewHolder) holder).headerTitle.setText(folios.get(position).getFecha());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return header;
        return item;
    }

    @Override
    public int getItemCount() {
        return folios.size();
    }

    private boolean isPositionHeader(int position) {
        if(folios.get(position).getFolio() == null){
            return true;
        }
        return false;
    }
}
