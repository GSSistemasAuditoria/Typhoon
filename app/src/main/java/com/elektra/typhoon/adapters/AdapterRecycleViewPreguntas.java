package com.elektra.typhoon.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.MediaStore;
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
import com.elektra.typhoon.database.ChecklistDBMethods;
import com.elektra.typhoon.database.EvidenciasDBMethods;
import com.elektra.typhoon.database.HistoricoDBMethods;
import com.elektra.typhoon.database.UsuarioDBMethods;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.Historico;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.ResponseDescargaPdf;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.service.ApiInterface;
import com.elektra.typhoon.utils.Utils;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

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
public class AdapterRecycleViewPreguntas extends RecyclerView.Adapter<AdapterRecycleViewPreguntas.MyViewHolder> {

    private List<Pregunta> listPreguntas;
    private Activity activity;
    private int idRubro;
    //private int idPregunta;
    private TextView textViewCumplen;
    private TextView textViewNoCumplen;
    private AdapterExpandableChecklist adapterExpandableChecklist;
    private String fechaFolio;
    private int idBarco;
    private AdapterRecycleViewPreguntas adapterRecycleViewPreguntas;
    private final Handler handler = new Handler();
    private AdapterRecycleViewPreguntas.MyViewHolder holderTemp;

    public int getIdRubro() {
        return idRubro;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/{

        //public TextView textViewSKU;
        private LinearLayout linearLayout;
        private LinearLayout linearLayoutAddEvidencias;
        private LinearLayout linearLayoutEvidencias;
        private Button textViewAddEvidencias;
        private ImageView imageViewAddEvidencia;
        private ImageView imageViewAgregaEvidencia;
        private HorizontalScrollView horizontalScrollView;
        private RadioGroup radioGroup;
        private TextView textViewPregunta;
        private RelativeLayout relativeLayoutDescargaPdf;
        private ImageView imageViewDescargaPdf;

        public MyViewHolder(View view) {
            super(view);
            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayoutEvidencias);
            linearLayoutAddEvidencias = (LinearLayout) view.findViewById(R.id.linearLayoutAgregarEvidencia);
            linearLayoutEvidencias = (LinearLayout) view.findViewById(R.id.linearLayoutImagenesEvidencia);
            textViewAddEvidencias = (Button) view.findViewById(R.id.textViewAgregarEvidencia);
            imageViewAddEvidencia = (ImageView) view.findViewById(R.id.imageViewAddEvidencia);
            horizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.horizontalScrollView);
            radioGroup = (RadioGroup) view.findViewById(R.id.myRadioGroup);
            imageViewAgregaEvidencia = (ImageView) view.findViewById(R.id.imageViewAgregaEvidencias);
            textViewPregunta = (TextView) view.findViewById(R.id.textViewPregunta);
            relativeLayoutDescargaPdf = view.findViewById(R.id.relativeLayoutDescargaPdf);
            imageViewDescargaPdf = view.findViewById(R.id.imageViewDescargaPdf);
            //view.setOnClickListener(this);
        }

        /*@Override
        public void onClick(View v) {

        }//*/
    }

    public AdapterRecycleViewPreguntas(List<Pregunta> listPreguntas,Activity activity,int idRubro,TextView textViewCumplen,
                                       TextView textViewNoCumplen,AdapterExpandableChecklist adapterExpandableChecklist,String fechaFolio,int idBarco){
        this.activity = activity;
        this.listPreguntas = listPreguntas;
        this.idRubro = idRubro;
        this.textViewCumplen = textViewCumplen;
        this.textViewNoCumplen = textViewNoCumplen;
        this.adapterExpandableChecklist = adapterExpandableChecklist;
        this.fechaFolio = fechaFolio;
        this.idBarco = idBarco;
        this.adapterRecycleViewPreguntas = this;
    }

    @NonNull
    @Override
    public AdapterRecycleViewPreguntas.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checklist_layout, parent, false);
        return new AdapterRecycleViewPreguntas.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterRecycleViewPreguntas.MyViewHolder holder, final int position) {
        //acciones
        final Pregunta pregunta = listPreguntas.get(position);
        ResponseLogin.Usuario usuario = new UsuarioDBMethods(activity).readUsuario(null,null);
        holder.linearLayout.setVisibility(View.GONE);
        holder.radioGroup.setEnabled(false);
        pregunta.setRadioGroup(holder.radioGroup);
        holder.textViewPregunta.setText(pregunta.getDescripcion());
        holderTemp = holder;

        List<RespuestaData> listRespuestas = new ChecklistDBMethods(activity).readRespuesta("WHERE ID_REVISION = ? AND ID_CHECKLIST = ? " +
                "AND ID_PREGUNTA = ? AND ID_RUBRO = ? AND ID_BARCO = ?",new String[]{String.valueOf(pregunta.getIdRevision()),
                String.valueOf(pregunta.getIdChecklist()),String.valueOf(pregunta.getIdPregunta()),String.valueOf(pregunta.getIdRubro()),
                String.valueOf(idBarco)});

        if(usuario.getIdrol() == 1){
            holder.imageViewAddEvidencia.setVisibility(View.VISIBLE);
            holder.imageViewAgregaEvidencia.setVisibility(View.VISIBLE);
            holder.textViewAddEvidencias.setVisibility(View.VISIBLE);
        }else{
            holder.imageViewAddEvidencia.setVisibility(View.GONE);
            holder.imageViewAgregaEvidencia.setVisibility(View.GONE);
            holder.textViewAddEvidencias.setVisibility(View.GONE);
            if(usuario.getIdrol() == 3 && pregunta.isTierra()){
                holder.imageViewAddEvidencia.setVisibility(View.VISIBLE);
                holder.imageViewAgregaEvidencia.setVisibility(View.VISIBLE);
                holder.textViewAddEvidencias.setVisibility(View.VISIBLE);
            }else{
                holder.imageViewAddEvidencia.setVisibility(View.GONE);
                holder.imageViewAgregaEvidencia.setVisibility(View.GONE);
                holder.textViewAddEvidencias.setVisibility(View.GONE);
            }
        }

        holder.imageViewDescargaPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                descargaPDF(pregunta.getIdPregunta());
            }
        });

        holder.textViewAddEvidencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.validaConfiguracionApp(activity)) {
                    String flagGPS = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE).getString(Constants.SP_GPS_FLAG, "");
                    if (flagGPS.equals("true")) {
                        if (Utils.validaGeocerca(activity)) {
                            agregarEvidencias(pregunta.getListEvidencias(), holder.textViewAddEvidencias, position);
                        } else {
                            Utils.message(activity, "No se encuentra dentro de la zona");
                        }
                    } else {
                        agregarEvidencias(pregunta.getListEvidencias(), holder.textViewAddEvidencias, position);
                    }
                }
                /*if(pregunta.getListEvidencias() != null) {
                    if (validaNumeroEvidencias(pregunta.getListEvidencias().size())) {
                        String noEvidencias = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE).getString(Constants.SP_LIMITE_EVIDENCIAS,"");
                        Utils.message(activity,"Sólo se permite agregar " + noEvidencias + " evidencias");
                    }else{
                        mostrarPopupEvidencias(holder.textViewAddEvidencias, position);
                    }
                }else{
                    mostrarPopupEvidencias(holder.textViewAddEvidencias, position);
                }//*/
            }
        });

        holder.imageViewAgregaEvidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.validaConfiguracionApp(activity)) {
                    String flagGPS = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE).getString(Constants.SP_GPS_FLAG, "");
                    if (flagGPS.equals("true")) {
                        if (Utils.validaGeocerca(activity)) {
                            agregarEvidencias(pregunta.getListEvidencias(), holder.imageViewAgregaEvidencia, position);
                        } else {
                            Utils.message(activity, "No se encuentra dentro de la zona");
                        }
                    } else {
                        agregarEvidencias(pregunta.getListEvidencias(), holder.imageViewAgregaEvidencia, position);
                    }
                }
                /*if(pregunta.getListEvidencias() != null) {
                    if (validaNumeroEvidencias(pregunta.getListEvidencias().size())) {
                        String noEvidencias = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE).getString(Constants.SP_LIMITE_EVIDENCIAS, "");
                        Utils.message(activity, "Sólo se permite agregar " + noEvidencias + " evidencias");
                    } else {
                        mostrarPopupEvidencias(holder.imageViewAgregaEvidencia, position);
                    }
                }else{
                    mostrarPopupEvidencias(holder.imageViewAgregaEvidencia, position);
                }//*/
            }
        });

        holder.imageViewAddEvidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.validaConfiguracionApp(activity)) {
                    String flagGPS = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE).getString(Constants.SP_GPS_FLAG, "");
                    if (flagGPS.equals("true")) {
                        if (Utils.validaGeocerca(activity)) {
                            agregarEvidencias(pregunta.getListEvidencias(), holder.imageViewAddEvidencia, position);
                        } else {
                            Utils.message(activity, "No se encuentra dentro de la zona");
                        }
                    } else {
                        agregarEvidencias(pregunta.getListEvidencias(), holder.imageViewAddEvidencia, position);
                    }
                }
                /*if(pregunta.getListEvidencias() != null) {
                    if (validaNumeroEvidencias(pregunta.getListEvidencias().size())) {
                        String noEvidencias = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE).getString(Constants.SP_LIMITE_EVIDENCIAS, "");
                        Utils.message(activity, "Sólo se permite agregar " + noEvidencias + " evidencias");
                    } else {
                        mostrarPopupEvidencias(holder.imageViewAddEvidencia, position);
                    }
                }else{
                    mostrarPopupEvidencias(holder.imageViewAddEvidencia, position);
                }//*/
            }
        });

        if(pregunta.getListEvidencias() != null){
            if(pregunta.getListEvidencias().size() != 0) {

                int childs = holder.linearLayoutEvidencias.getChildCount();
                if(childs > 1){
                    holder.linearLayoutEvidencias.removeViews(1,childs - 1);
                }

                holder.radioGroup.check(R.id.opcion2);
                holder.linearLayout.setVisibility(View.VISIBLE);
                holder.linearLayoutAddEvidencias.setVisibility(View.GONE);
                for (Evidencia evidencia : pregunta.getListEvidencias()) {
                    //if (evidencia.getSmallBitmap() != null) {
                    /*if(usuario.getIdrol() != 1){
                        if(evidencia.getIdEstatus() != 3){
                            holder.linearLayoutEvidencias.addView(insertEvidencia(evidencia.getSmallBitmap(), evidencia.getIdEvidencia(), position), 1);
                        }
                    }else{
                        holder.linearLayoutEvidencias.addView(insertEvidencia(evidencia.getSmallBitmap(), evidencia.getIdEvidencia(), position), 1);
                    }//*/
                    holder.linearLayoutEvidencias.addView(insertEvidencia(evidencia.getSmallBitmap(), evidencia.getIdEvidencia(), position), 1);
                    //}
                }
                holder.horizontalScrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.horizontalScrollView.fullScroll(ScrollView.FOCUS_LEFT);
                    }
                }, 100L);

                if(validaEvidencias(pregunta.getListEvidencias())){
                    //radioGroupTemp.check(R.id.opcion1);
                    holder.radioGroup.check(R.id.opcion1);
                    pregunta.setCumple(true);
                }else{
                    //radioGroupTemp.check(R.id.opcion2);
                    holder.radioGroup.check(R.id.opcion2);
                    pregunta.setCumple(false);
                }
                adapterExpandableChecklist.contarPreguntasCumplen(idBarco);
            }else{
                holder.linearLayout.setVisibility(View.GONE);
                holder.linearLayoutAddEvidencias.setVisibility(View.VISIBLE);
            }
        }else{
            holder.linearLayout.setVisibility(View.GONE);
            holder.linearLayoutAddEvidencias.setVisibility(View.VISIBLE);
        }

        if(pregunta.getListEvidencias() != null) {
            if(pregunta.getListEvidencias().size() != 0) {
                if (validaEvidencias(pregunta.getListEvidencias())) {
                    holder.radioGroup.check(R.id.opcion1);
                } else {
                    holder.radioGroup.check(R.id.opcion2);
                }
            }
        }

        if(listRespuestas.size() != 0){
            for(RespuestaData respuestaData:listRespuestas){
                if(respuestaData.getIdRespuesta() != null){
                    if(respuestaData.getIdRespuesta() == 2){
                        holder.radioGroup.check(R.id.opcion1);
                    }else if(respuestaData.getIdRespuesta() == 3){
                        holder.radioGroup.check(R.id.opcion2);
                    }else{
                        holder.radioGroup.clearCheck();
                    }
                }else{
                    holder.radioGroup.clearCheck();
                }

                if(respuestaData.getIdEstatus() == 2){
                    holder.relativeLayoutDescargaPdf.setVisibility(View.VISIBLE);
                    holder.linearLayout.setVisibility(View.GONE);
                    holder.linearLayoutAddEvidencias.setVisibility(View.GONE);
                }else{
                    holder.relativeLayoutDescargaPdf.setVisibility(View.GONE);
                }
            }
        }

        //reiniciaRadioGroup(holder.radioGroup,pregunta.getListEvidencias());
    }

    private void agregarEvidencias(List<Evidencia> listEvidencias,View view,int position){
        if(listEvidencias != null) {
            if (validaNumeroEvidencias(listEvidencias.size())) {
                String noEvidencias = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE).getString(Constants.SP_LIMITE_EVIDENCIAS,"");
                Utils.message(activity,"Sólo se permite agregar " + noEvidencias + " evidencias");
            }else{
                mostrarPopupEvidencias(view, position);
            }
        }else{
            mostrarPopupEvidencias(view, position);
        }
    }

    /*private ImageView insertEvidencia(Bitmap bitmap,String id,int numPregunta){
        ImageView iv = new ImageView(activity.getApplicationContext());
        if(bitmap != null) {
            iv.setImageBitmap(bitmap);
        }else{
            iv.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_pdf_image));
        }
        //iv.setId(id);
        //iv.setContentDescription("" + numPregunta);
        iv.setContentDescription(id + "," + numPregunta);
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(87,
                87);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.setMarginStart(5);
        iv.setLayoutParams(lp);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //int identificador = view.getId();
                    //int numeroPregunta = Integer.parseInt(view.getContentDescription().toString());
                    String[] temp = view.getContentDescription().toString().split(",");
                    String identificador = temp[0];
                    int numeroPregunta = Integer.parseInt(temp[1]);
                    //Bitmap bitmap = listPreguntas.get(numeroPregunta).getListEvidencias().get(identificador - 1).getOriginalBitmap();
                    Bitmap bitmap = null;
                    Pregunta pregunta = listPreguntas.get(numeroPregunta);
                    Evidencia evidencia = new EvidenciasDBMethods(activity).readEvidencia("WHERE ID_EVIDENCIA = ? AND ID_REVISION = ? " +
                            "AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_BARCO = ?",
                            new String[]{String.valueOf(identificador),String.valueOf(pregunta.getIdRevision()),
                                    String.valueOf(pregunta.getIdChecklist()),String.valueOf(pregunta.getIdRubro()),
                                    String.valueOf(pregunta.getIdPregunta()),String.valueOf(pregunta.getIdBarco())});
                    if(evidencia != null) {
                        bitmap = evidencia.getOriginalBitmap();
                        mostrarDocumento(view, identificador, numeroPregunta, activity, bitmap,evidencia.getContenido());
                    }else{
                        Utils.message(activity,"No se pudo cargar la imagen");
                    }
                }catch (Exception e){
                    Utils.message(activity,"Error al cargar imagen");
                    e.printStackTrace();
                }
            }
        });
        return iv;
    }//*/

    private boolean validaNumeroEvidencias(int evidenciasCargadas){
        try {
            SharedPreferences sharedPrefs = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE);
            if (sharedPrefs.contains(Constants.SP_LIMITE_EVIDENCIAS)) {
                String evidenciasPermitidas = sharedPrefs.getString(Constants.SP_LIMITE_EVIDENCIAS, "");
                int numeroEvidencias = Integer.parseInt(evidenciasPermitidas);
                if (numeroEvidencias == evidenciasCargadas) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (evidenciasCargadas == 5) {
                    return true;
                } else {
                    return false;
                }
            }
        }catch (Exception e){
            Utils.message(activity,"No se descargo correctamente la configuración");
            return false;
        }
    }

    private RelativeLayout insertEvidencia(Bitmap bitmap, String id, int numPregunta){

        ResponseLogin.Usuario usuario = new UsuarioDBMethods(activity).readUsuario(null,null);

        LayoutInflater inflater = LayoutInflater.from(activity);
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.image_item_layout, null, false);

        //ImageView iv = new ImageView(activity.getApplicationContext());
        ImageView iv = (ImageView) relativeLayout.findViewById(R.id.imageViewPreview);

        Pregunta pregunta = listPreguntas.get(numPregunta);
        Evidencia evidenciaTemp = null;
        for(Evidencia evidencia:listPreguntas.get(numPregunta).getListEvidencias()){
            if(evidencia.getIdEvidencia().equals(id)){
                evidenciaTemp = evidencia;
                break;
            }
        }

        if(evidenciaTemp != null){
            if(usuario.getIdrol() == 1) {
                //if ((evidenciaTemp.getIdEtapa() == 2 || evidenciaTemp.getIdEtapa() == 3) && evidenciaTemp.getIdEstatus() == 1) {
                if (evidenciaTemp.getIdEtapa() != 1 && evidenciaTemp.getIdEstatus() == 1) {
                    relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.verde_chk));
                } else if (evidenciaTemp.getIdEtapa() == 1 && evidenciaTemp.getIdEstatus() == 3) {
                    relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.rojo_chk));
                } else {
                    relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.gris));
                }
                //}else if(usuario.getIdrol() == 2) {
            }else {
                //if (evidenciaTemp.getIdEtapa() == 3 && evidenciaTemp.getIdEstatus() == 1) {
                if (evidenciaTemp.getIdEtapa() > usuario.getIdrol() && evidenciaTemp.getIdEstatus() == 1) {
                    relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.verde_chk));
                } else if (evidenciaTemp.getIdEtapa() == 1 && evidenciaTemp.getIdEstatus() == 3) {
                    relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.rojo_chk));
                } else {
                    relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.gris));
                }
            }
        }

        if(bitmap != null) {
            iv.setImageBitmap(bitmap);
        }else{
            iv.setImageDrawable(activity.getResources().getDrawable(R.drawable.pdf_icon));
        }
        //iv.setId(id);
        //iv.setContentDescription("" + numPregunta);
        iv.setContentDescription(id + "," + numPregunta);
        //iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.setMarginStart(5);
        //iv.setLayoutParams(lp);
        relativeLayout.setLayoutParams(lp);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //int identificador = view.getId();
                    //int numeroPregunta = Integer.parseInt(view.getContentDescription().toString());
                    String[] temp = view.getContentDescription().toString().split(",");
                    String identificador = temp[0];
                    int numeroPregunta = Integer.parseInt(temp[1]);
                    //Bitmap bitmap = listPreguntas.get(numeroPregunta).getListEvidencias().get(identificador - 1).getOriginalBitmap();
                    Bitmap bitmap = null;
                    Pregunta pregunta = listPreguntas.get(numeroPregunta);
                    Evidencia evidencia = new EvidenciasDBMethods(activity).readEvidencia("WHERE ID_EVIDENCIA = ? AND ID_REVISION = ? " +
                                    "AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_BARCO = ?",
                            new String[]{String.valueOf(identificador),String.valueOf(pregunta.getIdRevision()),
                                    String.valueOf(pregunta.getIdChecklist()),String.valueOf(pregunta.getIdRubro()),
                                    String.valueOf(pregunta.getIdPregunta()),String.valueOf(pregunta.getIdBarco())});
                    /*for(Evidencia evidencia:listPreguntas.get(numeroPregunta).getListEvidencias()){
                        if(evidencia.getIdEvidencia() == identificador){
                            bitmap = evidencia.getOriginalBitmap();
                        }
                    }//*/
                    if(evidencia != null) {
                        bitmap = evidencia.getOriginalBitmap();
                        mostrarDocumento(view, identificador, numeroPregunta, activity, bitmap,evidencia.getContenido());
                    }else{
                        Utils.message(activity,"No se pudo cargar la imagen");
                    }
                }catch (Exception e){
                    Utils.message(activity,"Error al cargar imagen");
                    e.printStackTrace();
                }
            }
        });
        return relativeLayout;
    }

    @Override
    public int getItemCount() {
        return listPreguntas.size();
    }

    private void mostrarDocumento(final View viewImagen, final String identificador, final int numeroPregunta, final Activity activity, final Bitmap documento, String documentoPDF){
        LayoutInflater inflater = LayoutInflater.from(activity);

        String estatusString = "";
        String etapaString = "";

        View dialogLayout = null;
        UsuarioDBMethods usuarioDBMethods = new UsuarioDBMethods(activity);
        final ResponseLogin.Usuario usuario = usuarioDBMethods.readUsuario(null,null);

        if(documento != null) {
            dialogLayout = inflater.inflate(R.layout.mostrar_documento_layout, null, false);
            final ImageView imageViewDocumento = (ImageView) dialogLayout.findViewById(R.id.imageViewDocumento);
            //imageViewDocumento.setImageBitmap(documento);
            /*Glide.with(activity).load(Utils.resizeImageBitmap(documento,
                    documento.getWidth()/2,documento.getHeight()/2)).into(imageViewDocumento);//*/
            Glide.with(activity).load(documento).into(imageViewDocumento);
        }else{
            dialogLayout = inflater.inflate(R.layout.mostrar_documento_pdf_layout, null, false);
            PDFView pdfViewDocumento = (PDFView) dialogLayout.findViewById(R.id.pdfViewDocumento);
            byte[] pdf = Utils.base64ToFile(documentoPDF);
            pdfViewDocumento.fromBytes(pdf).load();
        }

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

        if(usuario != null){
            if(usuario.getIdrol() == 1){
                buttonNoCumple.setVisibility(View.GONE);
            }else{
                if(usuario.getIdrol() == 3){
                    buttonBorrar.setVisibility(View.VISIBLE);
                }else {
                    buttonBorrar.setVisibility(View.GONE);
                }
                //buttonHistorico.setVisibility(View.GONE);
            }
        }

        buttonCumple.setBackground(drawableCumpleVerde);
        buttonNoCumple.setBackground(drawableNoCumpleRojo);

        for(Evidencia evidencia:listPreguntas.get(numeroPregunta).getListEvidencias()) {
            if(evidencia.getIdEvidencia().equals(identificador)) {
                //if(evidencia.getIdEstatus() == 1){

                etapaString = Utils.getEtapa(activity,evidencia.getIdEtapa());
                estatusString = Utils.getEstatusEvidencia(activity,evidencia.getIdEstatus());
                if(evidencia.getIdEtapa() != 1 && evidencia.getIdEstatus() == 1){
                    estatusString = estatusString.replace("Activa","Validada");
                }else{

                }

                if(usuario.getIdrol() == 1) {
                    if (evidencia.getIdEtapa() != 1 && evidencia.getIdEstatus() == 1) {
                        //buttonCumple.setBackground(drawableCumpleVerde);
                        //buttonNoCumple.setBackground(drawableNoCumpleGris);
                        buttonCumple.setVisibility(View.GONE);
                        buttonBorrar.setVisibility(View.GONE);
                    } else if (evidencia.getIdEtapa() == 1 && evidencia.getIdEstatus() == 3) {
                        //buttonCumple.setBackground(drawableCumpleGris);
                        //buttonNoCumple.setBackground(drawableNoCumpleRojo);
                        buttonCumple.setVisibility(View.GONE);
                    } else {
                        //buttonCumple.setBackground(drawableCumpleGris);
                        //buttonNoCumple.setBackground(drawableNoCumpleGris);
                    }
                    //}else if(usuario.getIdrol() == 2) {
                }else{
                    if (evidencia.getIdEtapa() > usuario.getIdrol() && evidencia.getIdEstatus() == 1) {
                        //buttonCumple.setBackground(drawableCumpleVerde);
                        //buttonNoCumple.setBackground(drawableNoCumpleGris);
                        buttonBorrar.setVisibility(View.GONE);
                        buttonCumple.setVisibility(View.GONE);
                        buttonNoCumple.setVisibility(View.GONE);
                    } else if (evidencia.getIdEtapa() == 1 && evidencia.getIdEstatus() == 3) {
                        //buttonCumple.setBackground(drawableCumpleGris);
                        //buttonNoCumple.setBackground(drawableNoCumpleRojo);
                        buttonBorrar.setVisibility(View.GONE);
                        buttonCumple.setVisibility(View.GONE);
                        buttonNoCumple.setVisibility(View.GONE);
                    } else {
                        //buttonCumple.setBackground(drawableCumpleGris);
                        //buttonNoCumple.setBackground(drawableNoCumpleGris);
                    }

                    if(evidencia.getAgregadoCoordinador() == 1){
                        //buttonBorrar.setVisibility(View.VISIBLE);
                        buttonNoCumple.setVisibility(View.GONE);
                    }else{
                        buttonBorrar.setVisibility(View.GONE);
                    }
                }
                textViewNombreDocumento.setText(evidencia.getNombre());
                break;
            }
        }

        final AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setView(dialogLayout)
                //.setPositiveButton("Cerrar", null)
                .create();

        /*alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });//*/
        alertDialog.show();

        imageViewCerrarDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        final String finalEstatusString = estatusString;
        final String finalEtapaString = etapaString;

        buttonHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(activity);
                View dialogLayout = inflater.inflate(R.layout.historico_layout, null, false);

                ListView listViewHistorico = dialogLayout.findViewById(R.id.listViewHistorico);
                Button buttonCerrar = dialogLayout.findViewById(R.id.buttonCerrar);
                TextView textViewEstatus = dialogLayout.findViewById(R.id.textViewEstatusEvidencia);
                TextView textViewEtapa = dialogLayout.findViewById(R.id.textViewEtapaEvidencia);

                textViewEstatus.setText("Estatus general: " + finalEstatusString);
                textViewEtapa.setText("Etapa actual: " + finalEtapaString);

                List<Historico> listHistorico = new HistoricoDBMethods(activity).readHistorico("WHERE ID_EVIDENCIA = ?",new String[]{identificador});
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
                Evidencia evidenciaTemp = null;
                for(Evidencia evidencia:listPreguntas.get(numeroPregunta).getListEvidencias()) {
                    if(evidencia.getIdEvidencia().equals(identificador)) {
                        evidencia.setIdEstatus(1);
                        if(usuario.getIdrol() == 1) {
                            //evidencia.setIdEtapa(getEtapaValidado(evidencia.getIdEtapa()));
                            evidencia.setIdEtapa(2);
                            //}else if(usuario.getIdrol() == 2) {
                        }else {
                            evidencia.setIdEtapa(usuario.getIdrol() + 1);
                        }
                        try {
                            ContentValues contentValues = new ContentValues();
                            //contentValues.put("ID_ETAPA", getEtapaValidado(evidencia.getIdEtapa()));
                            if(usuario.getIdrol() == 1) {
                                contentValues.put("ID_ETAPA", 2);
                                //}else if(usuario.getIdrol() == 2) {
                            }else {
                                //contentValues.put("ID_ETAPA", 3);
                                contentValues.put("ID_ETAPA", usuario.getIdrol() + 1);
                            }
                            contentValues.put("ID_ESTATUS",1);
                            new EvidenciasDBMethods(activity).updateEvidencia(contentValues,
                                    "ID_EVIDENCIA = ? AND ID_REVISION = ? AND ID_CHECKLIST = ? " +
                                            "AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_REGISTRO = ? AND ID_BARCO = ?",
                                    new String[]{evidencia.getIdEvidencia(), String.valueOf(evidencia.getIdRevision()),
                                            String.valueOf(evidencia.getIdChecklist()), String.valueOf(evidencia.getIdRubro()),
                                            String.valueOf(evidencia.getIdPregunta()), String.valueOf(evidencia.getIdRegistro()),
                                            String.valueOf(evidencia.getIdBarco())});

                            Utils.message(activity,"Validada");
                            crearHistorico(evidencia,usuario,"Validado por " + Utils.getRol(activity,usuario.getIdrol()).toLowerCase() + ": " + usuario.getNombre());

                            if(validaEvidencias(listPreguntas.get(numeroPregunta).getListEvidencias())){
                                listPreguntas.get(numeroPregunta).getRadioGroup().check(R.id.opcion1);
                                updateRespuesta(evidencia,2);
                                listPreguntas.get(numeroPregunta).setCumple(true);
                            }else{
                                listPreguntas.get(numeroPregunta).getRadioGroup().check(R.id.opcion2);
                                updateRespuesta(evidencia,3);
                                listPreguntas.get(numeroPregunta).setCumple(false);
                            }
                            adapterExpandableChecklist.contarPreguntasCumplen(idBarco);
                            buttonCumple.setBackground(drawableCumpleVerde);
                            buttonNoCumple.setBackground(drawableNoCumpleGris);
                            alertDialog.dismiss();
                            notifyDataSetChanged();
                            break;

                        }catch (Exception e){
                            e.printStackTrace();
                            Utils.message(activity,"Error al validar evidencia: " + e.getMessage());
                        }
                    }
                }
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

                /*alertDialogMotivo.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {
                        textViewAceptar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(!editTextMotivoRechazo.getText().toString().equals("")) {
                                    rechazarEvidencia(numeroPregunta, identificador, buttonCumple, buttonNoCumple, alertDialog, drawableCumpleGris, drawableNoCumpleRojo,usuario,editTextMotivoRechazo.getText().toString(),adapterRecycleViewPreguntas);
                                    alertDialogMotivo.dismiss();
                                    notifyDataSetChanged();
                                }else{
                                    Utils.message(activity,"Debe especificar el motivo de rechazo");
                                }
                            }
                        });

                        linearLayoutAceptar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(!editTextMotivoRechazo.getText().toString().equals("")) {
                                    rechazarEvidencia(numeroPregunta, identificador, buttonCumple, buttonNoCumple, alertDialog, drawableCumpleGris, drawableNoCumpleRojo,usuario,editTextMotivoRechazo.getText().toString(),adapterRecycleViewPreguntas);
                                    alertDialogMotivo.dismiss();
                                    notifyDataSetChanged();
                                }else{
                                    Utils.message(activity,"Debe especificar el motivo de rechazo");
                                }
                            }
                        });
                    }
                });
                alertDialogMotivo.show();//*/

                /*textViewAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!editTextMotivoRechazo.getText().toString().equals("")) {
                            rechazarEvidencia(numeroPregunta, identificador, buttonCumple, buttonNoCumple, alertDialog, drawableCumpleGris, drawableNoCumpleRojo,usuario,editTextMotivoRechazo.getText().toString(),adapterRecycleViewPreguntas);
                            alertDialogMotivo.dismiss();
                            notifyDataSetChanged();
                        }else{
                            Utils.message(activity,"Debe especificar el motivo de rechazo");
                        }
                    }
                });//*/

                linearLayoutAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!editTextMotivoRechazo.getText().toString().equals("")) {

                            rechazarEvidencia(numeroPregunta, identificador, buttonCumple, buttonNoCumple, alertDialog, drawableCumpleGris, drawableNoCumpleRojo,usuario,editTextMotivoRechazo.getText().toString(),adapterRecycleViewPreguntas);

                            //********************************************************************
                            /*Evidencia evidenciaTemp = null;
                            for(Evidencia evidencia:listPreguntas.get(numeroPregunta).getListEvidencias()) {
                                if(evidencia.getIdEvidencia().equals(identificador)) {
                                    evidencia.setIdEstatus(3);
                                    evidencia.setIdEtapa(1);
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("ID_ETAPA",1);
                                    contentValues.put("ID_ESTATUS",3);
                                    try {
                                        new EvidenciasDBMethods(activity).updateEvidencia(contentValues,
                                                "ID_EVIDENCIA = ? AND ID_REVISION = ? AND ID_CHECKLIST = ? " +
                                                        "AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_REGISTRO = ? AND ID_BARCO = ?",
                                                new String[]{evidencia.getIdEvidencia(), String.valueOf(evidencia.getIdRevision()),
                                                        String.valueOf(evidencia.getIdChecklist()), String.valueOf(evidencia.getIdRubro()),
                                                        String.valueOf(evidencia.getIdPregunta()), String.valueOf(evidencia.getIdRegistro()),
                                                        String.valueOf(evidencia.getIdBarco())});

                                        Utils.message(activity,"Rechazada");
                                        crearHistorico(evidencia,usuario,"Rechazado por " + Utils.getRol(activity,usuario.getIdrol()).toLowerCase() + ": " + usuario.getNombre() + "\nMotivo: " +
                                                editTextMotivoRechazo.getText().toString());

                                        if(validaEvidencias(listPreguntas.get(numeroPregunta).getListEvidencias())){
                                            listPreguntas.get(numeroPregunta).getRadioGroup().check(R.id.opcion1);
                                            updateRespuesta(evidencia,2);
                                            listPreguntas.get(numeroPregunta).setCumple(true);
                                        }else{
                                            listPreguntas.get(numeroPregunta).getRadioGroup().check(R.id.opcion2);
                                            updateRespuesta(evidencia,3);
                                            listPreguntas.get(numeroPregunta).setCumple(false);
                                        }
                                        adapterExpandableChecklist.contarPreguntasCumplen(idBarco);
                                        buttonCumple.setBackground(drawableCumpleGris);
                                        buttonNoCumple.setBackground(drawableNoCumpleRojo);
                                        alertDialog.dismiss();
                                        //notifyDataSetChanged();
                                        //adapterRecycleViewPreguntas.notifyDataSetChanged();
                                        //evidenciaTemp = evidencia;
                                        break;
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        Utils.message(activity,"Error al rechazar evidencia: " + e.getMessage());
                                    }
                                }
                            }//*/
                            //********************************************************************

                            adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                            alertDialogMotivo.dismiss();

                        }else{
                            Utils.message(activity,"Debe especificar el motivo de rechazo");
                        }
                    }
                });//*/

                /*Evidencia evidenciaTemp = null;
                for(Evidencia evidencia:listPreguntas.get(numeroPregunta).getListEvidencias()) {
                    if(evidencia.getIdEvidencia().equals(identificador)) {
                        evidencia.setIdEstatus(3);
                        evidencia.setIdEtapa(1);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("ID_ETAPA",1);
                        contentValues.put("ID_ESTATUS",3);
                        try {
                            new EvidenciasDBMethods(activity).updateEvidencia(contentValues,
                                    "ID_EVIDENCIA = ? AND ID_REVISION = ? AND ID_CHECKLIST = ? " +
                                            "AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_REGISTRO = ? AND ID_BARCO = ?",
                                    new String[]{evidencia.getIdEvidencia(), String.valueOf(evidencia.getIdRevision()),
                                            String.valueOf(evidencia.getIdChecklist()), String.valueOf(evidencia.getIdRubro()),
                                            String.valueOf(evidencia.getIdPregunta()), String.valueOf(evidencia.getIdRegistro()),
                                            String.valueOf(evidencia.getIdBarco())});
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        evidenciaTemp = evidencia;
                        break;
                    }
                }
                Utils.message(activity,"Rechazada");
                if(validaEvidencias(listPreguntas.get(numeroPregunta).getListEvidencias())){
                    listPreguntas.get(numeroPregunta).getRadioGroup().check(R.id.opcion1);
                    updateRespuesta(evidenciaTemp,2);
                    listPreguntas.get(numeroPregunta).setCumple(true);
                }else{
                    listPreguntas.get(numeroPregunta).getRadioGroup().check(R.id.opcion2);
                    updateRespuesta(evidenciaTemp,3);
                    listPreguntas.get(numeroPregunta).setCumple(false);
                }
                adapterExpandableChecklist.contarPreguntasCumplen();
                buttonCumple.setBackground(drawableCumpleGris);
                buttonNoCumple.setBackground(drawableNoCumpleRojo);
                alertDialog.dismiss();
                notifyDataSetChanged();//*/
            }
        });

        buttonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Evidencia evidenciaTemp = null;
                    for (Evidencia evidencia : listPreguntas.get(numeroPregunta).getListEvidencias()) {
                        if (evidencia.getIdEvidencia().equals(identificador)) {
                            evidenciaTemp = evidencia;
                            if(usuario.getIdrol()==1) {
                                //si esta rechazada la evidencia
                                if(evidencia.getIdEstatus() == 3){
                                    //borrado lógico
                                    evidencia.setIdEstatus(2);
                                    evidencia.setIdEtapa(usuario.getIdrol());
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("ID_ETAPA",usuario.getIdrol());
                                    contentValues.put("ID_ESTATUS",2);
                                    String base64 = null;
                                    contentValues.put("CONTENIDO",base64);

                                    new EvidenciasDBMethods(activity).updateEvidencia(contentValues,
                                            "ID_EVIDENCIA = ? AND ID_REVISION = ? AND ID_CHECKLIST = ? " +
                                                    "AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_REGISTRO = ? AND ID_BARCO = ?",
                                            new String[]{evidencia.getIdEvidencia(), String.valueOf(evidencia.getIdRevision()),
                                                    String.valueOf(evidencia.getIdChecklist()), String.valueOf(evidencia.getIdRubro()),
                                                    String.valueOf(evidencia.getIdPregunta()), String.valueOf(evidencia.getIdRegistro()),
                                                    String.valueOf(evidencia.getIdBarco())});

                                    crearHistorico(evidencia,usuario,"Borrado por " + Utils.getRol(activity,usuario.getIdrol()).toLowerCase() + ": " + usuario.getNombre());
                                }else{
                                    //borrado físico
                                    new EvidenciasDBMethods(activity).deleteEvidencia("ID_EVIDENCIA = ? AND ID_REVISION = ? AND " +
                                            "ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_BARCO = ?", new String[]{
                                            identificador,
                                            String.valueOf(evidencia.getIdRevision()),
                                            String.valueOf(evidencia.getIdChecklist()),
                                            String.valueOf(evidencia.getIdRubro()),
                                            String.valueOf(evidencia.getIdPregunta()),
                                            String.valueOf(evidencia.getIdBarco())
                                    });
                                }
                                listPreguntas.get(numeroPregunta).getListEvidencias().remove(evidencia);
                            }else if(usuario.getIdrol()==3) {
                                //borrado físico
                                new EvidenciasDBMethods(activity).deleteEvidencia("ID_EVIDENCIA = ? AND ID_REVISION = ? AND " +
                                        "ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_BARCO = ?", new String[]{
                                        identificador,
                                        String.valueOf(evidencia.getIdRevision()),
                                        String.valueOf(evidencia.getIdChecklist()),
                                        String.valueOf(evidencia.getIdRubro()),
                                        String.valueOf(evidencia.getIdPregunta()),
                                        String.valueOf(evidencia.getIdBarco())
                                });
                                listPreguntas.get(numeroPregunta).getListEvidencias().remove(evidencia);
                            }
                            break;
                        }
                    }
                    LinearLayout linearLayoutParent = (LinearLayout) viewImagen.getParent().getParent();
                    //linearLayoutParent.removeView(viewImagen.getParent());
                    Utils.message(activity, "Evidencia borrada");
                    if (listPreguntas.get(numeroPregunta).getListEvidencias() != null) {
                        if (listPreguntas.get(numeroPregunta).getListEvidencias().size() != 0) {
                            if (validaEvidencias(listPreguntas.get(numeroPregunta).getListEvidencias())) {
                                listPreguntas.get(numeroPregunta).getRadioGroup().check(R.id.opcion1);
                                updateRespuesta(evidenciaTemp,2);
                                listPreguntas.get(numeroPregunta).setCumple(true);
                            } else {
                                listPreguntas.get(numeroPregunta).getRadioGroup().check(R.id.opcion2);
                                updateRespuesta(evidenciaTemp,3);
                                listPreguntas.get(numeroPregunta).setCumple(false);
                            }
                            adapterExpandableChecklist.contarPreguntasCumplen(idBarco);
                        } else {
                            listPreguntas.get(numeroPregunta).getRadioGroup().clearCheck();
                            //updateRespuesta(evidenciaTemp,3);
                            updateRespuesta(evidenciaTemp,null);
                            listPreguntas.get(numeroPregunta).setCumple(false);
                            adapterExpandableChecklist.contarPreguntasCumplen(idBarco);
                        }
                    } else {
                        listPreguntas.get(numeroPregunta).getRadioGroup().clearCheck();
                        //updateRespuesta(evidenciaTemp,3);
                        updateRespuesta(evidenciaTemp,null);
                        listPreguntas.get(numeroPregunta).setCumple(false);
                        adapterExpandableChecklist.contarPreguntasCumplen(idBarco);
                    }
                    notifyDataSetChanged();
                    alertDialog.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                    Utils.message(activity,"Error al borrar evidencia: " + e.getMessage());
                }
            }
        });
    }

    private void refresh()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //movies.add(0,movies.get(new Random().nextInt(movies.size())));

                AdapterRecycleViewPreguntas.this.notifyDataSetChanged();

                //swiper.setRefreshing(false);
            }
        },3000);
    }

    private void crearHistorico(Evidencia evidencia, ResponseLogin.Usuario usuario,String motivo){
        HistoricoDBMethods historicoDBMethods = new HistoricoDBMethods(activity);
        List<Historico> listHistorico = historicoDBMethods.readHistorico("WHERE ID_EVIDENCIA = ?",new String[]{evidencia.getIdEvidencia()});
        int consecutivo = 1;
        /*if(listHistorico.size() != 0){
            for(Historico historico:listHistorico){
                if(historico.getConsec() > consecutivo){
                    consecutivo = historico.getConsec();
                }
            }
        }//*/
        Historico historico = new Historico();
        historico.setIdEvidencia(evidencia.getIdEvidencia());
        historico.setIdEtapa(evidencia.getIdEtapa());
        historico.setIdUsuario(usuario.getIdUsuario());
        historico.setMotivo(motivo);
        historico.setConsec(consecutivo);
        historico.setIdRevision(evidencia.getIdRevision());
        historico.setIdChecklist(evidencia.getIdChecklist());
        historico.setFechaMod(Utils.getDate(Constants.DATE_FORMAT_FULL));
        historicoDBMethods.createHistorico(historico);
    }

    private void rechazarEvidencia(int numeroPregunta, String identificador, Button buttonCumple, Button buttonNoCumple, AlertDialog alertDialog,
                                   Drawable drawableCumpleGris, Drawable drawableNoCumpleRojo, ResponseLogin.Usuario usuario,String motivoRechazo,
                                   AdapterRecycleViewPreguntas adapterRecycleViewPreguntas){
        Evidencia evidenciaTemp = null;
        for(Evidencia evidencia:listPreguntas.get(numeroPregunta).getListEvidencias()) {
            if(evidencia.getIdEvidencia().equals(identificador)) {
                evidencia.setIdEstatus(3);
                evidencia.setIdEtapa(1);
                ContentValues contentValues = new ContentValues();
                contentValues.put("ID_ETAPA",1);
                contentValues.put("ID_ESTATUS",3);
                try {
                    new EvidenciasDBMethods(activity).updateEvidencia(contentValues,
                            "ID_EVIDENCIA = ? AND ID_REVISION = ? AND ID_CHECKLIST = ? " +
                                    "AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_REGISTRO = ? AND ID_BARCO = ?",
                            new String[]{evidencia.getIdEvidencia(), String.valueOf(evidencia.getIdRevision()),
                                    String.valueOf(evidencia.getIdChecklist()), String.valueOf(evidencia.getIdRubro()),
                                    String.valueOf(evidencia.getIdPregunta()), String.valueOf(evidencia.getIdRegistro()),
                                    String.valueOf(evidencia.getIdBarco())});

                    Utils.message(activity,"Rechazada");
                    crearHistorico(evidencia,usuario,"Rechazado por " + Utils.getRol(activity,usuario.getIdrol()).toLowerCase() + ": " + usuario.getNombre() + "\nMotivo: " +
                            motivoRechazo);

                    if(validaEvidencias(listPreguntas.get(numeroPregunta).getListEvidencias())){
                        listPreguntas.get(numeroPregunta).getRadioGroup().check(R.id.opcion1);
                        updateRespuesta(evidencia,2);
                        listPreguntas.get(numeroPregunta).setCumple(true);
                    }else{
                        listPreguntas.get(numeroPregunta).getRadioGroup().check(R.id.opcion2);
                        updateRespuesta(evidencia,3);
                        listPreguntas.get(numeroPregunta).setCumple(false);
                    }
                    adapterExpandableChecklist.contarPreguntasCumplen(idBarco);
                    buttonCumple.setBackground(drawableCumpleGris);
                    buttonNoCumple.setBackground(drawableNoCumpleRojo);
                    alertDialog.dismiss();
                    //notifyDataSetChanged();
                    //adapterRecycleViewPreguntas.notifyDataSetChanged();
                    //evidenciaTemp = evidencia;
                    break;
                }catch (Exception e){
                    e.printStackTrace();
                    Utils.message(activity,"Error al rechazar evidencia: " + e.getMessage());
                }
            }
        }
    }

    private void updateRespuesta(Evidencia evidencia,Integer idRespuesta){
        if(evidencia != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID_RESPUESTA", idRespuesta);
            new ChecklistDBMethods(activity).updateRespuesta(contentValues, "ID_REVISION = ? AND " +
                            "ID_CHECKLIST = ? AND ID_PREGUNTA = ? AND ID_RUBRO = ? AND ID_BARCO = ? AND ID_REGISTRO = ?",
                    new String[]{String.valueOf(evidencia.getIdRevision()), String.valueOf(evidencia.getIdChecklist()), String.valueOf(evidencia.getIdPregunta()),
                            String.valueOf(evidencia.getIdRubro()), String.valueOf(evidencia.getIdBarco()), String.valueOf(evidencia.getIdRegistro())});
        }
    }

    public boolean validaEvidencias(List<Evidencia> listEvidencias){
        ResponseLogin.Usuario usuario = new UsuarioDBMethods(activity).readUsuario(null,null);
        for(Evidencia ev:listEvidencias){
            //if(ev.getIdEstatus() == 2){
            if(usuario.getIdrol() == 1) {
                if (ev.getIdEtapa() != 1 && ev.getIdEstatus() == 1) {

                }else{
                    return false;
                }
                //}else if(usuario.getIdrol() == 2) {
            }else {
                if (ev.getIdEtapa() != 1 && ev.getIdEstatus() == 1) {
                    //if (ev.getIdEtapa() > usuario.getIdrol() && ev.getIdEstatus() == 1) {

                }else{
                    return false;
                }
            }
        }
        return true;
    }

    public void reiniciaRadioGroup(RadioGroup radioGroup,List<Evidencia> listEvidencias){
        boolean flag = true;
        ResponseLogin.Usuario usuario = new UsuarioDBMethods(activity).readUsuario(null,null);
        //if(usuario.getIdrol() == 2) {
        if(usuario.getIdrol() != 1) {
            for (Evidencia ev : listEvidencias) {
                //if (ev.getIdEtapa() == 2 && ev.getIdEstatus() == 1) {
                if (ev.getIdEtapa() == usuario.getIdrol() && ev.getIdEstatus() == 1) {

                }else{
                    flag = false;
                }
            }
            if (flag) {
                radioGroup.clearCheck();
            }

            boolean flag2 = true;
            for (Evidencia ev : listEvidencias) {
                if (ev.getIdEtapa() == 1 && ev.getIdEstatus() == 1) {

                }else{
                    flag2 = false;
                }
            }
            if (flag2) {
                radioGroup.clearCheck();
            }
        }
    }

    private void mostrarPopupEvidencias(View anchorView,final int position) {
        final PopupWindow popup = new PopupWindow(activity);
        View layout = activity.getLayoutInflater().inflate(R.layout.popup_evidencias_layout, null);
        popup.setContentView(layout);
        // Set content width and height

        Button buttonCamera = (Button) layout.findViewById(R.id.buttonCamera);
        Button buttonFiles = (Button) layout.findViewById(R.id.buttonFiles);

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activity.startActivityForResult(captureIntent, position);//*/

                /*if(Utils.checkPermission(activity, Manifest.permission.CAMERA,102)){
                    if(Utils.checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE,101)) {
                        if(Utils.checkPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION,103)) {
                            Utils.openCamera(activity, position);
                        }else{
                            Utils.message(activity,"Se requiere permiso para acceso a la ubicación");
                        }
                    }else{
                        Utils.message(activity,"Se requiere permiso para lectura de archivos");
                    }
                }else{
                    Utils.message(activity,"Se requiere permiso para utilizar la cámara");
                }//*/

                /*Calendar calendarActual = Utils.getCalendarDate(Utils.getDate(Constants.DATE_FORMAT_FULL));
                Calendar calendarFolio = Utils.getCalendarDate(fechaFolio);
                if(calendarActual != null && calendarFolio != null){
                    int mesActual = calendarActual.get(Calendar.MONTH) + 1;
                    int anioActual = calendarActual.get(Calendar.YEAR);
                    int mesFolio = calendarFolio.get(Calendar.MONTH) + 1;
                    int anioFolio = calendarFolio.get(Calendar.YEAR);
                    if((mesActual == mesFolio) && (anioActual == anioFolio)){
                        if(Utils.checkPermission(activity)){
                            Utils.openCamera(activity, position);
                        }
                    }else{
                        Utils.message(activity,"No se pueden agregar evidencias ya que la fecha de revisión no corresponde al mes actual");
                    }
                }//*/

                if(Utils.checkPermission(activity)){
                    Utils.openCamera(activity, position);
                }

                popup.dismiss();
            }
        });

        buttonFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(Utils.checkPermission(activity, Manifest.permission.CAMERA,102)){
                    if(Utils.checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE,101)) {
                        if(Utils.checkPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION,103)) {
                            seleccionarArchivo(position);
                        }else{
                            Utils.message(activity,"Se requiere permiso para acceso a la ubicación");
                        }
                    }else{
                        Utils.message(activity,"Se requiere permiso para lectura de archivos");
                    }
                }else{
                    Utils.message(activity,"Se requiere permiso para utilizar la cámara");
                }//*/

                if(Utils.checkPermission(activity)){
                    seleccionarArchivo(position);
                }

                popup.dismiss();
            }
        });

        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.drawable_editext));
        // Show anchored to button
        //popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(anchorView,0,Math.round(anchorView.getY())-dpToPx(100));
    }

    public int dpToPx(int dp) {
        float density = activity.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    public void seleccionarArchivo(int position) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        String[] mimetypes={"image/png","image/jpg","image/jpeg", "application/pdf"};//*/
        //String[] mimetypes={"application/pdf"}; //pdf*/
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimetypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activity.startActivityForResult(intent.createChooser(intent, "Selecciona el archivo"),position);
        //activity.startActivity(intent.createChooser(intent, "Selecciona el archivo"));
    }

    private void descargaPDF(int idPregunta){

        final ProgressDialog progressDialog = Utils.typhoonLoader(activity,"Descargando informe...");

        try {

            SharedPreferences sharedPrefs = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE);

            ApiInterface mApiService = Utils.getInterfaceService();
            Call<ResponseDescargaPdf> mService = mApiService.descargaPDF(sharedPrefs.getString(Constants.SP_JWT_TAG, ""), idPregunta);
            mService.enqueue(new Callback<ResponseDescargaPdf>() {
                @Override
                public void onResponse(Call<ResponseDescargaPdf> call, Response<ResponseDescargaPdf> response) {
                    progressDialog.dismiss();
                    if (response.body() != null) {
                        if (response.body().getDescargaPDF().getExito()) {
                            try {
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
                            } catch (Exception e) {
                                Utils.message(activity, "No se pudo descargar el informe: " + e.getMessage());
                                e.printStackTrace();
                            }
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
                }

                @Override
                public void onFailure(Call<ResponseDescargaPdf> call, Throwable t) {
                    progressDialog.dismiss();
                    Utils.message(activity, Constants.MSG_ERR_CONN);
                }
            });
        }catch (Exception e){
            progressDialog.dismiss();
            e.printStackTrace();
            Utils.message(activity, "Error al descargar informe: " + e.getMessage());
        }
    }
}
