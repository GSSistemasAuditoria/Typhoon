package com.elektra.typhoon.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.elektra.typhoon.R;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.AnexosDBMethods;
import com.elektra.typhoon.database.CatalogosDBMethods;
import com.elektra.typhoon.database.ChecklistDBMethods;
import com.elektra.typhoon.database.EvidenciasDBMethods;
import com.elektra.typhoon.database.HistoricoDBMethods;
import com.elektra.typhoon.database.UsuarioDBMethods;
import com.elektra.typhoon.encryption.Encryption;
import com.elektra.typhoon.objetos.response.Anexo;
import com.elektra.typhoon.objetos.response.EtapaSubAnexo;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.Historico;
import com.elektra.typhoon.objetos.response.HistoricoAnexo;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.ResponseDescargaPdf;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.service.ApiInterface;
import com.elektra.typhoon.utils.Utils;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 18/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class AdapterRecycleViewItemsAnexos extends RecyclerView.Adapter<AdapterRecycleViewItemsAnexos.MyViewHolder> {

    private List<Anexo> listAnexos;
    private Activity activity;
    private int header;
    private AdapterExpandableAnexos adapterExpandableAnexos;
    private String fechaRevision;
    private Anexo anexoHeader;
    private int estatusRevision;

    public class MyViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/{

        private TextView textViewTituloAnexo;
        private ImageView imageViewSubirArchivo;
        private ImageView imageViewDescargarArchivo;
        private ImageView imageViewEtapa;
        private ImageView imageViewSeleccionado;

        public MyViewHolder(View view) {
            super(view);
            textViewTituloAnexo = view.findViewById(R.id.textViewTituloAnexo);
            imageViewSubirArchivo = view.findViewById(R.id.imageViewSubirArchivo);
            imageViewDescargarArchivo = view.findViewById(R.id.imageViewDescargarArchivo);
            imageViewEtapa = view.findViewById(R.id.imageViewEtapa);
            imageViewSeleccionado = view.findViewById(R.id.imageViewSelect);
            //view.setOnClickListener(this);
        }

        /*@Override
        public void onClick(View v) {

        }//*/
    }

    public AdapterRecycleViewItemsAnexos(List<Anexo> listAnexos, Activity activity,int header,AdapterExpandableAnexos adapterExpandableAnexos,String fechaRevision,Anexo anexoHeader,int estatusRevision){
        this.activity = activity;
        this.listAnexos = listAnexos;
        this.header = header;
        this.adapterExpandableAnexos = adapterExpandableAnexos;
        this.fechaRevision = fechaRevision;
        this.anexoHeader = anexoHeader;
        this.estatusRevision = estatusRevision;
    }

    public int getHeader() {
        return header;
    }

    @NonNull
    @Override
    public AdapterRecycleViewItemsAnexos.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_anexo_layout, parent, false);
        return new AdapterRecycleViewItemsAnexos.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterRecycleViewItemsAnexos.MyViewHolder holder, final int position) {
        //acciones
        final Anexo anexo = listAnexos.get(position);

        final SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SP_NAME,Activity.MODE_PRIVATE);
        final Encryption encryption = new Encryption();

        holder.textViewTituloAnexo.setText(anexo.getDescripcion());
        if(anexo.getNombreArchivo() != null){
            if(!anexo.getNombreArchivo().equals("")){
                holder.imageViewSubirArchivo.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_subir_azul));
                holder.imageViewDescargarArchivo.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_show_document_green));
            }else{
                holder.imageViewSubirArchivo.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_subir_gris));
                holder.imageViewDescargarArchivo.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_show_document_gray));
            }
        }else{
            holder.imageViewSubirArchivo.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_subir_gris));
            holder.imageViewDescargarArchivo.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_show_document_gray));
        }
        holder.imageViewSubirArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String flagValida = "false";
                if(sharedPreferences.contains(Constants.SP_VALIDA_FECHA)){
                    flagValida = encryption.decryptAES(sharedPreferences.getString(Constants.SP_VALIDA_FECHA,"false"));
                }
                if(flagValida.equals("true")) {
                    if(Utils.validaFechaRevision(activity,fechaRevision)){
                        if(Utils.checkPermission(activity)){
                            seleccionarArchivo(position);
                        }
                    }else{
                        Utils.message(activity,"No se permite agregar documentos porque la revisión no es del mes actual");
                    }
                }else{
                    if(Utils.checkPermission(activity)){
                        seleccionarArchivo(position);
                    }
                }//*/

                //seleccionarArchivo(position);
            }
        });
        holder.imageViewDescargarArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(anexo.getBase64() != null) {
                    if(!anexo.getBase64().equals("")) {
                        //mostrarDocumento(activity, anexo.getNombreArchivo(), anexo.getBase64());
                    }else{
                        Utils.message(activity,"No se ha agregado documento");
                    }
                }else{
                    Utils.message(activity,"No se ha agregado documento");
                }//*/

                if(anexo.getNombreArchivo() != null) {
                    if(!anexo.getNombreArchivo().equals("")) {
                        AnexosDBMethods anexosDBMethods = new AnexosDBMethods(activity);
                        /*List<Anexo> anexoGuardado = anexosDBMethods.readAnexos("SELECT ID_REVISION,ID_ANEXO,ID_SUBANEXO,ID_DOCUMENTO,ID_ETAPA,DOCUMENTO,NOMBRE " +
                                        "FROM " + anexosDBMethods.TP_TRAN_ANEXOS + " WHERE ID_REVISION = ? AND ID_ANEXO = ? AND ID_SUBANEXO = ? AND ID_DOCUMENTO = ?"
                                , new String[]{String.valueOf(anexo.getIdRevision()), String.valueOf(anexo.getIdAnexo()), String.valueOf(anexo.getIdSubAnexo()), anexo.getIdDocumento()});//*/

                        List<Anexo> anexoGuardado = anexosDBMethods.readAnexos("SELECT ID_REVISION,ID_ANEXO,ID_SUBANEXO,ID_DOCUMENTO,ID_ETAPA,DOCUMENTO,NOMBRE,SUBANEXO_FCH_SINC,SELECCIONADO,SUBANEXO_FCH_MOD " +
                                        "FROM " + anexosDBMethods.TP_TRAN_ANEXOS + " WHERE ID_REVISION = ? AND ID_SUBANEXO = ?"
                                , new String[]{String.valueOf(anexo.getIdRevision()), String.valueOf(anexo.getIdSubAnexo())});

                        if (anexoGuardado.size() != 0) {
                            Anexo tempAnexo = anexoGuardado.get(0);
                            mostrarDocumento(activity, tempAnexo.getNombreArchivo(), tempAnexo.getBase64(),anexo);
                        } else {
                            Utils.message(activity, "No se ha agregado documento");
                        }
                    } else {
                        Utils.message(activity, "No se ha agregado documento");
                    }
                } else {
                    Utils.message(activity, "No se ha agregado documento");
                }
            }
        });

        ResponseLogin.Usuario usuario = new UsuarioDBMethods(activity).readUsuario();
        if(usuario != null){
            if(usuario.getIdrol() != 1){
                holder.imageViewSubirArchivo.setVisibility(View.GONE);
            }else{
                if(anexo.getIdEtapa() == 0 || anexo.getIdEtapa() == -1) {
                    holder.imageViewSubirArchivo.setVisibility(View.VISIBLE);
                }else{
                    holder.imageViewSubirArchivo.setVisibility(View.GONE);
                }
            }

            if(anexo.getNombreArchivo() == null){
                holder.imageViewEtapa.setImageDrawable(null);
            }else {
                if (anexo.getIdEtapa() >= usuario.getIdrol()) {
                    holder.imageViewEtapa.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_cumple_verde));
                } else if (anexo.getIdEtapa() == -1) {
                    holder.imageViewEtapa.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_nocumple_rojo));
                } else {
                    holder.imageViewEtapa.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_documento_pendiente));
                }
            }
        }

        holder.imageViewSeleccionado.setEnabled(false);

        holder.imageViewSeleccionado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(anexo.isSeleccionado()){
                    anexo.setSeleccionado(false);
                    anexoHeader.setSeleccionado(false);
                    Utils.updateAnexo(activity,String.valueOf(anexo.getIdRevision()),String.valueOf(anexo.getIdSubAnexo()),0);
                    adapterExpandableAnexos.notifyDataSetChanged();
                }else{
                    anexo.setSeleccionado(true);
                    Utils.updateAnexo(activity,String.valueOf(anexo.getIdRevision()),String.valueOf(anexo.getIdSubAnexo()),1);
                }
                notifyDataSetChanged();
            }
        });

        if(anexo.isSeleccionado()) {
            holder.imageViewSeleccionado.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_check_blue));
        }else{
            holder.imageViewSeleccionado.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_uncheck_blue));
        }

        /*if(estatusRevision == 3 || estatusRevision == 4){
            holder.imageViewSubirArchivo.setVisibility(View.GONE);
        }//*/

        if(anexo.getNombreArchivo() != null){
            if(!anexo.getNombreArchivo().equals("")) {
                holder.imageViewSubirArchivo.setVisibility(View.GONE);
            }else{
                holder.imageViewSubirArchivo.setVisibility(View.VISIBLE);
            }
        }else{
            holder.imageViewSubirArchivo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listAnexos.size();
    }

    private void mostrarDocumento(final Activity activity, String nombrePDF, String documentoPDF, final Anexo anexo){
        LayoutInflater inflater = LayoutInflater.from(activity);
        View dialogLayout = null;
        UsuarioDBMethods usuarioDBMethods = new UsuarioDBMethods(activity);
        final ResponseLogin.Usuario usuario = usuarioDBMethods.readUsuario();

        int etapaAnexo = usuario.getIdrol() - 1;

        dialogLayout = inflater.inflate(R.layout.mostrar_documento_pdf_layout, null, false);
        PDFView pdfViewDocumento = (PDFView) dialogLayout.findViewById(R.id.pdfViewDocumento);
        byte[] pdf = Utils.base64ToFile(documentoPDF);
        pdfViewDocumento.fromBytes(pdf).load();

        final Drawable drawableCumpleVerde = activity.getDrawable(R.mipmap.ic_cumple_verde);
        final Drawable drawableNoCumpleRojo = activity.getDrawable(R.mipmap.ic_nocumple_rojo);
        final Drawable drawableCumpleGris = activity.getDrawable(R.mipmap.ic_cumple_gris);
        final Drawable drawableNoCumpleGris = activity.getDrawable(R.mipmap.ic_nocumple_gris);

        final Button buttonCumple = (Button) dialogLayout.findViewById(R.id.buttonCumple);
        final Button buttonNoCumple = (Button) dialogLayout.findViewById(R.id.buttonNoCumple);
        final Button buttonBorrar = (Button) dialogLayout.findViewById(R.id.buttonBorrar);
        Button buttonHistorico = (Button) dialogLayout.findViewById(R.id.buttonHistorico);
        final TextView textViewNombreDocumento = (TextView) dialogLayout.findViewById(R.id.textViewNombreDocumento);
        ImageView imageViewCerrarDialog = dialogLayout.findViewById(R.id.imageViewCloseDialog);

        buttonCumple.setBackground(drawableCumpleVerde);
        buttonNoCumple.setBackground(drawableNoCumpleRojo);
        textViewNombreDocumento.setText(nombrePDF);

        buttonBorrar.setVisibility(View.GONE);

        if(usuario.getIdrol() == 1){
            buttonNoCumple.setVisibility(View.GONE);
            if(anexo.getIdEtapa() == -1){
                buttonCumple.setVisibility(View.GONE);
            }else{
                buttonCumple.setVisibility(View.VISIBLE);
            }

            if(anexo.getIdEtapa() > etapaAnexo){
                buttonCumple.setVisibility(View.GONE);
                buttonNoCumple.setVisibility(View.GONE);
            }
        }else{
            if(anexo.getIdEtapa() == etapaAnexo){
                buttonCumple.setVisibility(View.VISIBLE);
                if(anexo.getFechaSinc() == null){
                    buttonNoCumple.setVisibility(View.GONE);
                }else{
                    buttonNoCumple.setVisibility(View.VISIBLE);
                }
            }else if(anexo.getIdEtapa() > etapaAnexo){
                buttonCumple.setVisibility(View.GONE);
                buttonNoCumple.setVisibility(View.GONE);
            }else if(anexo.getIdEtapa() < etapaAnexo){
                buttonCumple.setVisibility(View.GONE);
                buttonNoCumple.setVisibility(View.GONE);
            }else if(anexo.getIdEtapa() == -1){
                buttonCumple.setVisibility(View.GONE);
                buttonNoCumple.setVisibility(View.GONE);
            }
        }

        /*if(anexo.getIdEtapa() == etapaAnexo){
            buttonCumple.setVisibility(View.VISIBLE);
            buttonNoCumple.setVisibility(View.VISIBLE);
        }else if(anexo.getIdEtapa() > etapaAnexo){
            buttonCumple.setVisibility(View.GONE);
            buttonNoCumple.setVisibility(View.GONE);
        }else if(anexo.getIdEtapa() < etapaAnexo){
            buttonCumple.setVisibility(View.GONE);
            buttonNoCumple.setVisibility(View.GONE);
        }//*/

        final AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setView(dialogLayout)
                .create();
        alertDialog.show();

        imageViewCerrarDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        buttonHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(activity);
                View dialogLayout = inflater.inflate(R.layout.historico_layout, null, false);

                ListView listViewHistorico = dialogLayout.findViewById(R.id.listViewHistorico);
                Button buttonCerrar = dialogLayout.findViewById(R.id.buttonCerrar);
                TextView textViewEstatus = dialogLayout.findViewById(R.id.textViewEstatusEvidencia);
                TextView textViewEtapa = dialogLayout.findViewById(R.id.textViewEtapaEvidencia);

                String finalEtapaString = "";
                CatalogosDBMethods catalogosDBMethods = new CatalogosDBMethods(activity);
                List<EtapaSubAnexo> listCatalogo = catalogosDBMethods.readEtapaSubAnexo("SELECT ID_ETAPA,ID_USUARIO,DESCRIPCION FROM " + catalogosDBMethods.TP_CAT_ETAPA_SUBANEXO +
                         " WHERE ID_ETAPA = ?",new String[]{String.valueOf(anexo.getIdEtapa())});

                if(listCatalogo.size() != 0){
                    finalEtapaString = listCatalogo.get(0).getDescripcion();
                }

                //textViewEstatus.setText("Estatus general: " + finalEstatusString);
                textViewEtapa.setText(finalEtapaString);

                HistoricoDBMethods historicoDBMethods = new HistoricoDBMethods(activity);
                List<HistoricoAnexo> listHistoricoAnexo = historicoDBMethods.readHistoricoAnexo("SELECT ID_SUBANEXO,ID_REVISION,ID_ETAPA,ID_USUARIO,NOMBRE,MOTIVO_RECHAZO,FECHA_MOD,GUID,SUBANEXO_FCH_SINC,ID_ROL FROM " +
                historicoDBMethods.TP_TRAN_HISTORIAL_SUBANEXO + " WHERE ID_SUBANEXO = ? AND ID_REVISION = ? ORDER BY FECHA_MOD DESC",new String[]{
                        String.valueOf(anexo.getIdSubAnexo()),String.valueOf(anexo.getIdRevision())});

                List<Historico> listHistorico = new ArrayList<>();

                for(HistoricoAnexo historicoAnexo:listHistoricoAnexo){
                    Historico historico = new Historico();
                    historico.setFechaMod(historicoAnexo.getFechaMod());
                    String descripcion = "";
                    if(historicoAnexo.getIdEtapa() != -1){
                        descripcion = "Validado por " + Utils.getRol(activity,historicoAnexo.getIdRol()).toLowerCase() + " " + historicoAnexo.getNombre();
                    }else{
                        descripcion = "Rechazado por " + Utils.getRol(activity,historicoAnexo.getIdRol()).toLowerCase() + " " + historicoAnexo.getNombre() + "\nMotivo: " + historicoAnexo.getMotivoRechazo();
                    }
                    historico.setMotivo(descripcion);
                    listHistorico.add(historico);
                }

                HistoricoAdapter historicoAdapter = new HistoricoAdapter(activity,listHistorico);
                listViewHistorico.setAdapter(historicoAdapter);

                final AlertDialog alertDialogMotivo = new AlertDialog.Builder(activity)
                        .setView(dialogLayout)
                        //.setNegativeButton("Cerrar",null)
                        .create();
                alertDialogMotivo.show();

                buttonCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialogMotivo.dismiss();
                    }
                });
            }
        });

        buttonCumple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idRol = usuario.getIdrol();
                ContentValues contentValues = new ContentValues();
                contentValues.put("ID_ETAPA",idRol);
                new AnexosDBMethods(activity).updateAnexo(contentValues,"ID_REVISION = ? AND ID_SUBANEXO = ?",
                        new String[]{String.valueOf(anexo.getIdRevision()),String.valueOf(anexo.getIdSubAnexo())});
                anexo.setIdEtapa(idRol);

                HistoricoAnexo historicoAnexo = new HistoricoAnexo();
                historicoAnexo.setIdSubAnexo(anexo.getIdSubAnexo());
                historicoAnexo.setIdRevision(anexo.getIdRevision());
                historicoAnexo.setIdEtapa(anexo.getIdEtapa());
                historicoAnexo.setIdUsuario(usuario.getIdUsuario());
                historicoAnexo.setNombre(usuario.getNombre());
                historicoAnexo.setFechaMod(Utils.getDate(Constants.DATE_FORMAT_FULL));
                historicoAnexo.setGuid(UUID.randomUUID().toString());
                historicoAnexo.setIdRol(usuario.getIdrol());
                new HistoricoDBMethods(activity).createHistoricoAnexo(historicoAnexo);

                Utils.message(activity,"Validado");
                Utils.updateAnexo(activity,String.valueOf(anexo.getIdRevision()),String.valueOf(anexo.getIdSubAnexo()),1);
                anexo.setSeleccionado(true);
                adapterExpandableAnexos.contarAnexosValidados();
                adapterExpandableAnexos.getAdapterRecycleViewItemsAnexosTemp().notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });

        buttonNoCumple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater inflater = LayoutInflater.from(activity);
                View dialogLayout = inflater.inflate(R.layout.rechazo_evidencia_layout, null, false);

                final EditText editTextMotivoRechazo = dialogLayout.findViewById(R.id.editTextMotivoRechazo);
                TextView textViewCancelar = dialogLayout.findViewById(R.id.buttonCancelar);
                final TextView textViewAceptar = dialogLayout.findViewById(R.id.buttonAceptar);

                final LinearLayout linearLayoutAceptar = dialogLayout.findViewById(R.id.linearLayoutAceptar);
                LinearLayout linearLayoutCancelar = dialogLayout.findViewById(R.id.linearLayoutCancelar);

                final AlertDialog alertDialogMotivo = new AlertDialog.Builder(activity)
                        .setView(dialogLayout)
                        .setNegativeButton("",null)
                        .setPositiveButton("",null)
                        .create();
                alertDialogMotivo.show();

                textViewCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialogMotivo.dismiss();
                    }
                });

                linearLayoutCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialogMotivo.dismiss();
                    }
                });

                linearLayoutAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!editTextMotivoRechazo.getText().toString().equals("")) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("ID_ETAPA",-1);
                            new AnexosDBMethods(activity).updateAnexo(contentValues,"ID_REVISION = ? AND ID_SUBANEXO = ?",
                                    new String[]{String.valueOf(anexo.getIdRevision()),String.valueOf(anexo.getIdSubAnexo())});
                            anexo.setIdEtapa(-1);
                            HistoricoAnexo historicoAnexo = new HistoricoAnexo();
                            historicoAnexo.setIdSubAnexo(anexo.getIdSubAnexo());
                            historicoAnexo.setIdRevision(anexo.getIdRevision());
                            historicoAnexo.setIdEtapa(anexo.getIdEtapa());
                            historicoAnexo.setIdUsuario(usuario.getIdUsuario());
                            historicoAnexo.setNombre(usuario.getNombre());
                            historicoAnexo.setMotivoRechazo(Normalizer.normalize(editTextMotivoRechazo.getText().toString(), Normalizer.Form.NFD));
                            historicoAnexo.setFechaMod(Utils.getDate(Constants.DATE_FORMAT_FULL));
                            historicoAnexo.setGuid(UUID.randomUUID().toString());
                            historicoAnexo.setIdRol(usuario.getIdrol());
                            new HistoricoDBMethods(activity).createHistoricoAnexo(historicoAnexo);
                            Utils.message(activity,"Rechazado");
                            Utils.updateAnexo(activity,String.valueOf(anexo.getIdRevision()),String.valueOf(anexo.getIdSubAnexo()),1);
                            anexo.setSeleccionado(true);
                            adapterExpandableAnexos.contarAnexosValidados();
                            adapterExpandableAnexos.getAdapterRecycleViewItemsAnexosTemp().notifyDataSetChanged();
                            alertDialogMotivo.dismiss();
                            alertDialog.dismiss();
                        }else{
                            Utils.message(activity,"Debe especificar el motivo de rechazo");
                        }
                    }
                });
            }
        });

        buttonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void seleccionarArchivo(int position) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        String[] mimetypes={"application/pdf"};//*/
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimetypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activity.startActivityForResult(intent.createChooser(intent, "Selecciona el archivo"),position);
    }

    private void descargaPDF(int idPregunta){

        final ProgressDialog progressDialog = Utils.typhoonLoader(activity,"Descargando informe...");

        //try {

        SharedPreferences sharedPrefs = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE);

        ApiInterface mApiService = Utils.getInterfaceService();
        Call<ResponseDescargaPdf> mService = mApiService.descargaPDF(sharedPrefs.getString(Constants.SP_JWT_TAG, ""), idPregunta);
        mService.enqueue(new Callback<ResponseDescargaPdf>() {
            @Override
            public void onResponse(Call<ResponseDescargaPdf> call, Response<ResponseDescargaPdf> response) {
                progressDialog.dismiss();
                if(response != null) {
                    if (response.body() != null) {
                        if (response.body().getDescargaPDF().getExito()) {
                            //try {
                            if (response.body().getDescargaPDF().getDocumentoPDF() != null) {
                                String base64 = response.body().getDescargaPDF().getDocumentoPDF().getBase64();
                                if (base64 != null) {
                                    if (!base64.equals("")) {
                                        String nombre = response.body().getDescargaPDF().getDocumentoPDF().getNombre();
                                        LayoutInflater inflater = LayoutInflater.from(activity);
                                        View dialogLayout = null;
                                        dialogLayout = inflater.inflate(R.layout.mostrar_descarga_pdf_layout, null, false);

                                        DisplayMetrics displayMetrics = new DisplayMetrics();
                                        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                        int height = displayMetrics.heightPixels;
                                        int width = displayMetrics.widthPixels;

                                            /*LinearLayout layout = dialogLayout.findViewById(R.id.linearLayoutMostrarDescargaPdf);
                                            ViewGroup.LayoutParams params = layout.getLayoutParams();
                                            params.height = height - 20;
                                            params.width = width - 20;
                                            layout.setLayoutParams(params);//*/

                                        PDFView pdfView = dialogLayout.findViewById(R.id.pdfViewDocumento);
                                        byte[] pdf = Utils.base64ToFile(base64);
                                        pdfView.fromBytes(pdf).load();

                                        TextView textViewNombreDocumento = (TextView) dialogLayout.findViewById(R.id.textViewNombreDocumento);
                                        ImageView imageViewCerrarDialog = dialogLayout.findViewById(R.id.imageViewCloseDialog);

                                        textViewNombreDocumento.setText(nombre);

                                        final AlertDialog alertDialog = new AlertDialog.Builder(activity)
                                                .setView(dialogLayout)
                                                //.setPositiveButton("Cerrar", null)
                                                .create();

                                        alertDialog.show();

                                        imageViewCerrarDialog.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                alertDialog.dismiss();
                                            }
                                        });
                                    } else {
                                        Utils.message(activity, "No se pudo descargar el informe");
                                    }
                                } else {
                                    Utils.message(activity, "No se pudo descargar el informe");
                                }
                            } else {
                                Utils.message(activity, "No se pudo descargar el informe");
                            }
                            /*} catch (NullPointerException e) {
                                Utils.message(activity, "No se pudo descargar el informe: " + e.getMessage());
                                e.printStackTrace();
                            }//*/
                        } else {
                            Utils.message(activity, response.body().getDescargaPDF().getError());
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                Utils.message(activity, "Error al descargar informe: " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                                Utils.message(activity, "Error al descargar informe: " + e.getMessage());
                            }
                        } else {
                            Utils.message(activity, "Error al descargar informe");
                        }
                    }
                }else{
                    Utils.message(activity, "Error al descargar informe");
                }
            }

            @Override
            public void onFailure(Call<ResponseDescargaPdf> call, Throwable t) {
                progressDialog.dismiss();
                Utils.message(activity, Constants.MSG_ERR_CONN);
            }
        });
        /*}catch (NullPointerException e){
            progressDialog.dismiss();
            e.printStackTrace();
            Utils.message(activity, "Error al descargar informe: " + e.getMessage());
        }//*/
    }
}
