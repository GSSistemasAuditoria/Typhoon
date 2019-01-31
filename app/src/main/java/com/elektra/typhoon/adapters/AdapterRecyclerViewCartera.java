package com.elektra.typhoon.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.elektra.typhoon.R;
import com.elektra.typhoon.checklist.ChecklistBarcos;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.ChecklistDBMethods;
import com.elektra.typhoon.objetos.request.SincronizacionData;
import com.elektra.typhoon.objetos.request.SincronizacionPost;
import com.elektra.typhoon.objetos.response.ChecklistData;
import com.elektra.typhoon.objetos.response.FolioRevision;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.PreguntaData;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.objetos.response.RubroData;
import com.elektra.typhoon.objetos.response.SincronizacionResponse;
import com.elektra.typhoon.service.ApiInterface;
import com.elektra.typhoon.utils.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        public ImageView imageViewSincronizar;

        public ItemViewHolder(View v) {
            super(v);
            textViewFolio = v.findViewById(R.id.textViewFolio);
            textViewFecha = v.findViewById(R.id.textViewFecha);
            textViewDescripcion = v.findViewById(R.id.textViewDescripcion);
            imageViewSincronizar = v.findViewById(R.id.imageViewSincronizar);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            FolioRevision folioRevision = folios.get(getAdapterPosition());
            Intent intent = new Intent(activity, ChecklistBarcos.class);
            intent.putExtra(Constants.INTENT_FOLIO_TAG,folioRevision.getIdRevision());
            intent.putExtra(Constants.INTENT_FECHA_INICIO_TAG,folioRevision.getFechaInicio());
            intent.putExtra(Constants.INTENT_FECHA_FIN_TAG,folioRevision.getFechaFin());
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            //((ItemViewHolder)holder).textViewFolio.setText("Folio:" + folios.get(position).getFolio());
            //((ItemViewHolder)holder).textViewFecha.setText(folios.get(position).getFecha());
            //((ItemViewHolder)holder).textViewDescripcion.setText(folios.get(position).getDescripcion());
            ((ItemViewHolder)holder).textViewFolio.setText("" + folios.get(position).getIdRevision());
            ((ItemViewHolder)holder).textViewFecha.setText(Utils.getDateMonth(folios.get(position).getFechaInicio()));
            ((ItemViewHolder)holder).textViewDescripcion.setText(folios.get(position).getNombre());
            ((ItemViewHolder)holder).imageViewSincronizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Utils.message(context,"Sincronizar");
                    sincronizacion(folios.get(position).getIdRevision());
                }
            });
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

    private void sincronizacion(final int idRevision){

        final ProgressDialog progressDialog = Utils.typhoonLoader(context,"Sincronizando...");

        ApiInterface mApiService = Utils.getInterfaceService();

        SincronizacionData sincronizacionData = new SincronizacionData();
        sincronizacionData.setIdRevision(idRevision);

        SincronizacionPost sincronizacionPost = new SincronizacionPost();
        sincronizacionPost.setSincronizacionData(sincronizacionData);

        Call<SincronizacionResponse> mService = mApiService.sincronizacion(sincronizacionPost);
        mService.enqueue(new Callback<SincronizacionResponse>() {

            @Override
            public void onResponse(Call<SincronizacionResponse> call, Response<SincronizacionResponse> response) {
                if(response.body() != null) {
                    if(response.body().getSincronizacion().getExito()){
                        try {
                            if (response.body().getSincronizacion().getSincronizacionResponseData().getListChecklist() != null) {
                                ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(context);
                                for (ChecklistData checklistData : response.body().getSincronizacion().getSincronizacionResponseData().getListChecklist()) {
                                    checklistData.setIdRevision(idRevision);
                                    checklistDBMethods.createChecklist(checklistData);
                                    if (checklistData.getListRubros() != null) {
                                        for (RubroData rubroData : checklistData.getListRubros()) {
                                            rubroData.setIdRevision(idRevision);
                                            rubroData.setIdChecklist(checklistData.getIdChecklist());
                                            checklistDBMethods.createRubro(rubroData);
                                            if (rubroData.getListPreguntas() != null) {
                                                for (PreguntaData preguntaData : rubroData.getListPreguntas()) {
                                                    preguntaData.setIdRevision(idRevision);
                                                    preguntaData.setIdChecklist(checklistData.getIdChecklist());
                                                    checklistDBMethods.createPregunta(preguntaData);
                                                }
                                            }
                                        }
                                    }
                                }
                                if (response.body().getSincronizacion().getSincronizacionResponseData().getListRespuestas() != null) {
                                    for (RespuestaData respuestaData : response.body().getSincronizacion().getSincronizacionResponseData().getListRespuestas()) {
                                        checklistDBMethods.createRespuesta(respuestaData);
                                    }
                                }
                            }
                            progressDialog.dismiss();
                            Utils.message(context,"Sincronizado correctamente");
                        }catch (Exception e){
                            progressDialog.dismiss();
                            Utils.message(context,"Error al guardar datos: " + e.getMessage());
                        }
                    }else{
                        progressDialog.dismiss();
                        Utils.message(context, response.body().getSincronizacion().getError());
                    }//*/
                }else{
                    progressDialog.dismiss();
                    Utils.message(context,"Error al sincronizar");
                }
            }

            @Override
            public void onFailure(Call<SincronizacionResponse> call, Throwable t) {
                progressDialog.dismiss();
                Utils.message(context, Constants.MSG_ERR_CONN);
            }
        });
    }
}
