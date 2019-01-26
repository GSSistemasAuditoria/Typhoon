package com.elektra.typhoon.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.checklist.ChecklistBarcos;
import com.elektra.typhoon.objetos.response.FolioRevision;
import com.elektra.typhoon.utils.Utils;

import java.util.List;

/**
 * Proyecto: TYPHOON
 * Autor: Francis Susana Carreto Espinoza
 * Fecha: 10/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class AdapterRecyclerViewCartera extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private static Activity activity;
    //private ArrayList<Folio> folios;
    private static List<FolioRevision> folios;
    private static final int header = 0;
    private static final int item = 1;

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView textViewFolio;
        public TextView textViewFecha;
        public TextView textViewDescripcion;

        public ItemViewHolder(View v) {
            super(v);
            textViewFolio = v.findViewById(R.id.textViewFolio);
            textViewFecha = v.findViewById(R.id.textViewFecha);
            textViewDescripcion = v.findViewById(R.id.textViewDescripcion);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            FolioRevision folioRevision = folios.get(getAdapterPosition());
            Intent intent = new Intent(activity, ChecklistBarcos.class);
            intent.putExtra("folio",folioRevision.getIdRevision());
            intent.putExtra("fechaInicio",folioRevision.getFechaInicio());
            intent.putExtra("fechaFin",folioRevision.getFechaFin());
            activity.startActivity(intent);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        public TextView headerTitle;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerTitle = (TextView)itemView.findViewById(R.id.textViewAno);
        }
    }

    //public AdapterRecyclerViewCartera(Context context, ArrayList<Folio> folios) {
    public AdapterRecyclerViewCartera(Activity activity, Context context, List<FolioRevision> folios) {
        this.context = context;
        this.folios = folios;
        this.activity = activity;
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
            //((ItemViewHolder)holder).textViewFolio.setText("Folio:" + folios.get(position).getFolio());
            //((ItemViewHolder)holder).textViewFecha.setText(folios.get(position).getFecha());
            //((ItemViewHolder)holder).textViewDescripcion.setText(folios.get(position).getDescripcion());
            ((ItemViewHolder)holder).textViewFolio.setText("" + folios.get(position).getIdRevision());
            ((ItemViewHolder)holder).textViewFecha.setText(Utils.getDateMonth(folios.get(position).getFechaInicio()));
            ((ItemViewHolder)holder).textViewDescripcion.setText(folios.get(position).getNombre());
        }else if(holder instanceof HeaderViewHolder){
            //((HeaderViewHolder) holder).headerTitle.setText(folios.get(position).getFecha());
            ((HeaderViewHolder) holder).headerTitle.setText(folios.get(position).getFechaInicio());
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
        //if(folios.get(position).getFolio() == null){
        if(folios.get(position).getIdUsuario() == null){
            return true;
        }
        return false;
    }
}
