package com.elektra.typhoon.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.checklist.ChecklistBarcos;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.CatalogosDBMethods;
import com.elektra.typhoon.encryption.Encryption;
import com.elektra.typhoon.objetos.response.EstatusRevision;
import com.elektra.typhoon.objetos.response.FolioRevision;
import com.elektra.typhoon.service.SincronizacionRequestService;
import com.elektra.typhoon.utils.Utils;

import java.io.IOException;
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
        private TextView textViewFolio;
        private TextView textViewFecha;
        private TextView textViewDescripcion;
        private ImageView imageViewSincronizar;
        private RelativeLayout relativeLayoutSincronizar;
        private ImageView imageViewEstatus;

        public ItemViewHolder(View v) {
            super(v);
            textViewFolio = v.findViewById(R.id.textViewFolio);
            textViewFecha = v.findViewById(R.id.textViewFecha);
            textViewDescripcion = v.findViewById(R.id.textViewDescripcion);
            imageViewSincronizar = v.findViewById(R.id.imageViewSincronizar);
            relativeLayoutSincronizar = v.findViewById(R.id.relativeLayoutSincronizar);
            imageViewEstatus = v.findViewById(R.id.imageViewEstatus);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            FolioRevision folioRevision = folios.get(getAdapterPosition());
            Encryption encryption = new Encryption();
            Intent intent = new Intent(activity, ChecklistBarcos.class);
            /*intent.putExtra(Constants.INTENT_FOLIO_TAG,folioRevision.getIdRevision());
            intent.putExtra(Constants.INTENT_FECHA_INICIO_TAG,folioRevision.getFechaInicio());
            intent.putExtra(Constants.INTENT_FECHA_FIN_TAG,folioRevision.getFechaFin());
            intent.putExtra(Constants.INTENT_ESTATUS_TAG,folioRevision.getEstatus());//*/

            intent.putExtra(Constants.INTENT_FOLIO_TAG,encryption.encryptAES(String.valueOf(folioRevision.getIdRevision())));
            intent.putExtra(Constants.INTENT_FECHA_INICIO_TAG,encryption.encryptAES(folioRevision.getFechaInicio()));
            //intent.putExtra(Constants.INTENT_FECHA_FIN_TAG,encryption.encryptAES(folioRevision.getFechaFin()));
            intent.putExtra(Constants.INTENT_ESTATUS_TAG,encryption.encryptAES(String.valueOf(folioRevision.getEstatus())));

            activity.startActivity(intent);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        private TextView headerTitle;
        private HeaderViewHolder(View itemView) {
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

    private Drawable getEstatusImagen(int estatus){
        CatalogosDBMethods catalogosDBMethods = new CatalogosDBMethods(activity);
        List<EstatusRevision> listEstatusRevision = catalogosDBMethods.readEstatusRevision(
                "SELECT ID_ESTATUS,DESCRIPCION,SRC FROM " + catalogosDBMethods.TP_CAT_ESTATUS_REVISION + " WHERE ID_ESTATUS = ?",
                new String[]{String.valueOf(estatus)});
        if(listEstatusRevision.size() != 0){
            String imagen = listEstatusRevision.get(0).getImagen();
            if(imagen.contains("status-1")){
                return activity.getResources().getDrawable(R.drawable.status_1);
            }else if(imagen.contains("status-2")){
                return activity.getResources().getDrawable(R.drawable.status_2);
            }else if(imagen.contains("status-3")){
                return activity.getResources().getDrawable(R.drawable.status_3);
            }else if(imagen.contains("status-4")){
                return activity.getResources().getDrawable(R.drawable.status_4);
            }else if(imagen.contains("status-5")){
                return activity.getResources().getDrawable(R.drawable.status_5);
            }else if(imagen.contains("status-6")){
                return activity.getResources().getDrawable(R.drawable.status_6);
            }else{
                return activity.getResources().getDrawable(R.drawable.status_1);
            }
        }else{
            return activity.getResources().getDrawable(R.drawable.status_1);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            //((ItemViewHolder)holder).textViewFolio.setText("Folio:" + folios.get(position).getFolio());
            //((ItemViewHolder)holder).textViewFecha.setText(folios.get(position).getFecha());
            //((ItemViewHolder)holder).textViewDescripcion.setText(folios.get(position).getDescripcion());

            ((ItemViewHolder)holder).imageViewEstatus.setImageDrawable(getEstatusImagen(folios.get(position).getEstatus()));

            ((ItemViewHolder)holder).textViewFolio.setText("" + folios.get(position).getIdRevision());
            ((ItemViewHolder)holder).textViewFecha.setText(Utils.getDateMonth(folios.get(position).getFechaInicio()));
            ((ItemViewHolder)holder).textViewDescripcion.setText(folios.get(position).getNombre());
            ((ItemViewHolder)holder).imageViewSincronizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Utils.message(context,"Sincronizar");
                    try {
                        sincronizacion(folios.get(position).getIdRevision());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Utils.message(context,"Error al sincronizar: " + e.getMessage());
                    }
                }
            });
            ((ItemViewHolder)holder).relativeLayoutSincronizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Utils.message(context,"Sincronizar");
                    try {
                        sincronizacion(folios.get(position).getIdRevision());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Utils.message(context,"Error al sincronizar: " + e.getMessage());
                    }
                }
            });
        }else if(holder instanceof HeaderViewHolder){
            //((HeaderViewHolder) holder).headerTitle.setText(folios.get(position).getFecha());
            ((HeaderViewHolder) holder).headerTitle.setText(folios.get(position).getFechaInicio());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return header;
        }
        return item;
    }

    @Override
    public int getItemCount() {
        return folios.size();
    }

    private boolean isPositionHeader(int position) {
        //if(folios.get(position).getFolio() == null){
        /*if(folios.get(position).getIdUsuario() == null){
            return true;
        }//*/
        return false;
    }

    private void sincronizacion(int idRevision) throws IOException {

        sincronizacionDialog(activity,idRevision);

        /*final ProgressDialog progressDialog = Utils.typhoonLoader(context,"Sincronizando...");

        ApiInterface mApiService = Utils.getInterfaceService();

        //SincronizacionData sincronizacionData = new SincronizacionData();
        //sincronizacionData.setIdRevision(idRevision);

        SincronizacionData sincronizacionData = new SincronizacionJSON().generateRequestData(context,idRevision);

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
                                        for (Rubro rubroData : checklistData.getListRubros()) {
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
                    }
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
        });//*/
    }

    private void sincronizacionDialog(final Activity activity,final int idRevision){
        LayoutInflater li = LayoutInflater.from(activity);
        LinearLayout layoutDialog = (LinearLayout) li.inflate(R.layout.dialog_sincronizacion_layout, null);

        TextView textViewCancelar = layoutDialog.findViewById(R.id.buttonCancelar);
        TextView textViewSincronizar = layoutDialog.findViewById(R.id.buttonSincronizar);
        LinearLayout linearLayoutCancelar = layoutDialog.findViewById(R.id.linearLayoutCancelar);
        LinearLayout linearLayoutSincronizar = layoutDialog.findViewById(R.id.linearLayoutSincronizar);

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(layoutDialog)
                .show();

        textViewCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        linearLayoutCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        textViewSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SincronizacionRequestService(activity,activity,idRevision).execute();
                dialog.dismiss();
            }
        });

        linearLayoutSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SincronizacionRequestService(activity,activity,idRevision).execute();
                dialog.dismiss();
            }
        });
    }

    private void getSincronizacionData(){

    }
}
