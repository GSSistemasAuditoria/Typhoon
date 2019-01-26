package com.elektra.typhoon.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.utils.Utils;

import java.util.List;

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

    public int getIdRubro() {
        return idRubro;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/{

        //public TextView textViewSKU;
        public LinearLayout linearLayout;
        public LinearLayout linearLayoutAddEvidencias;
        public LinearLayout linearLayoutEvidencias;
        public Button textViewAddEvidencias;
        public ImageView imageViewAddEvidencia;
        public ImageView imageViewAgregaEvidencia;
        public HorizontalScrollView horizontalScrollView;
        public RadioGroup radioGroup;

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
            //view.setOnClickListener(this);
        }

        /*@Override
        public void onClick(View v) {

        }//*/
    }

    public AdapterRecycleViewPreguntas(List<Pregunta> listPreguntas,Activity activity,int idRubro,TextView textViewCumplen,
                                       TextView textViewNoCumplen,AdapterExpandableChecklist adapterExpandableChecklist){
        this.activity = activity;
        this.listPreguntas = listPreguntas;
        this.idRubro = idRubro;
        this.textViewCumplen = textViewCumplen;
        this.textViewNoCumplen = textViewNoCumplen;
        this.adapterExpandableChecklist = adapterExpandableChecklist;
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
        Pregunta pregunta = listPreguntas.get(position);
        holder.linearLayout.setVisibility(View.GONE);
        holder.radioGroup.setEnabled(false);
        pregunta.setRadioGroup(holder.radioGroup);
        holder.textViewAddEvidencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //holder.linearLayout.setVisibility(View.VISIBLE);

                //Utils.openCamera(activity);

                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activity.startActivityForResult(captureIntent, position);//*/
                //idPregunta = position;
                /*CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(activity);//*/
            }
        });

        holder.imageViewAgregaEvidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //holder.linearLayout.setVisibility(View.VISIBLE);

                //Utils.openCamera(activity);

                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activity.startActivityForResult(captureIntent, position);//*/
                //idPregunta = position;
                /*CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(activity);//*/
            }
        });

        holder.imageViewAddEvidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //holder.linearLayout.setVisibility(View.VISIBLE);

                //Utils.openCamera(activity);

                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activity.startActivityForResult(captureIntent, position);//*/
                //idPregunta = position;

                /*CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(activity);//*/
            }
        });

        if(pregunta.getListEvidencias() != null){
            if(pregunta.getListEvidencias().size() != 0) {
                holder.radioGroup.check(R.id.opcion2);
                holder.linearLayout.setVisibility(View.VISIBLE);
                holder.linearLayoutAddEvidencias.setVisibility(View.GONE);
                for (Evidencia evidencia : pregunta.getListEvidencias()) {
                    if (evidencia.getSmallBitmap() != null) {
                        holder.linearLayoutEvidencias.addView(insertEvidencia(evidencia.getSmallBitmap(), evidencia.getIdEvidencia(), position), 1);
                    }
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
                adapterExpandableChecklist.contarPreguntasCumplen();
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
    }

    private ImageView insertEvidencia(Bitmap bitmap,int id,int numPregunta){
        ImageView iv = new ImageView(activity.getApplicationContext());
        iv.setImageBitmap(bitmap);
        iv.setId(id);
        iv.setContentDescription("" + numPregunta);
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(87,
                87);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.setMarginStart(5);
        iv.setLayoutParams(lp);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Utils.message(activity,"Abrir imagen" + view.getId());
                try {
                    int identificador = view.getId();
                    int numeroPregunta = Integer.parseInt(view.getContentDescription().toString());
                    //Bitmap bitmap = listPreguntas.get(numeroPregunta).getListEvidencias().get(identificador - 1).getOriginalBitmap();
                    Bitmap bitmap = null;
                    for(Evidencia evidencia:listPreguntas.get(numeroPregunta).getListEvidencias()){
                        if(evidencia.getIdEvidencia() == identificador){
                            bitmap = evidencia.getOriginalBitmap();
                        }
                    }
                    mostrarDocumento(view,identificador,numeroPregunta,activity, bitmap);
                }catch (Exception e){
                    Utils.message(activity,"Error al cargar imagen");
                    e.printStackTrace();
                }
            }
        });
        return iv;
    }

    @Override
    public int getItemCount() {
        return listPreguntas.size();
    }

    private void mostrarDocumento(final View viewImagen, final int identificador, final int numeroPregunta, final Activity activity, Bitmap documento){
        LayoutInflater inflater = LayoutInflater.from(activity);
        View dialogLayout = inflater.inflate(R.layout.mostrar_documento_layout, null, false);

        ImageView imageViewDocumento = (ImageView) dialogLayout.findViewById(R.id.imageViewDocumento);
        imageViewDocumento.setImageBitmap(documento);

        final Drawable drawableCumpleVerde = activity.getDrawable(R.mipmap.ic_cumple_verde);
        final Drawable drawableNoCumpleRojo = activity.getDrawable(R.mipmap.ic_nocumple_rojo);
        final Drawable drawableCumpleGris = activity.getDrawable(R.mipmap.ic_cumple_gris);
        final Drawable drawableNoCumpleGris = activity.getDrawable(R.mipmap.ic_nocumple_gris);

        final Button buttonCumple = (Button) dialogLayout.findViewById(R.id.buttonCumple);
        final Button buttonNoCumple = (Button) dialogLayout.findViewById(R.id.buttonNoCumple);
        Button buttonBorrar = (Button) dialogLayout.findViewById(R.id.buttonBorrar);

        for(Evidencia evidencia:listPreguntas.get(numeroPregunta).getListEvidencias()) {
            if(evidencia.getIdEvidencia() == identificador) {
                if(evidencia.getIdEstatus() == 1){
                    buttonCumple.setBackground(drawableCumpleVerde);
                    buttonNoCumple.setBackground(drawableNoCumpleGris);
                }else{
                    buttonCumple.setBackground(drawableCumpleGris);
                    buttonNoCumple.setBackground(drawableNoCumpleRojo);
                }
            }
        }

        final AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setView(dialogLayout)
                .setPositiveButton("Cerrar", null)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

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
        });
        alertDialog.show();

        buttonCumple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(Evidencia evidencia:listPreguntas.get(numeroPregunta).getListEvidencias()) {
                    if(evidencia.getIdEvidencia() == identificador) {
                        evidencia.setIdEstatus(1);
                    }
                }
                Utils.message(activity,"Validada");
                if(validaEvidencias(listPreguntas.get(numeroPregunta).getListEvidencias())){
                    listPreguntas.get(numeroPregunta).getRadioGroup().check(R.id.opcion1);
                    listPreguntas.get(numeroPregunta).setCumple(true);
                }else{
                    listPreguntas.get(numeroPregunta).getRadioGroup().check(R.id.opcion2);
                    listPreguntas.get(numeroPregunta).setCumple(false);
                }
                adapterExpandableChecklist.contarPreguntasCumplen();
                buttonCumple.setBackground(drawableCumpleVerde);
                buttonNoCumple.setBackground(drawableNoCumpleGris);
                alertDialog.dismiss();
            }
        });

        buttonNoCumple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(Evidencia evidencia:listPreguntas.get(numeroPregunta).getListEvidencias()) {
                    if(evidencia.getIdEvidencia() == identificador) {
                        evidencia.setIdEstatus(0);
                    }
                }
                Utils.message(activity,"Rechazada");
                if(validaEvidencias(listPreguntas.get(numeroPregunta).getListEvidencias())){
                    listPreguntas.get(numeroPregunta).getRadioGroup().check(R.id.opcion1);
                    listPreguntas.get(numeroPregunta).setCumple(true);
                }else{
                    listPreguntas.get(numeroPregunta).getRadioGroup().check(R.id.opcion2);
                    listPreguntas.get(numeroPregunta).setCumple(false);
                }
                adapterExpandableChecklist.contarPreguntasCumplen();
                buttonCumple.setBackground(drawableCumpleGris);
                buttonNoCumple.setBackground(drawableNoCumpleRojo);
                alertDialog.dismiss();
            }
        });

        buttonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(Evidencia evidencia:listPreguntas.get(numeroPregunta).getListEvidencias()){
                    if(evidencia.getIdEvidencia() == (identificador)){
                        listPreguntas.get(numeroPregunta).getListEvidencias().remove(evidencia);
                        break;
                    }
                }
                LinearLayout linearLayoutParent = (LinearLayout) viewImagen.getParent();
                linearLayoutParent.removeView(viewImagen);
                Utils.message(activity,"Evidencia borrada");
                if(listPreguntas.get(numeroPregunta).getListEvidencias() != null) {
                    if(listPreguntas.get(numeroPregunta).getListEvidencias().size() != 0) {
                        if (validaEvidencias(listPreguntas.get(numeroPregunta).getListEvidencias())) {
                            listPreguntas.get(numeroPregunta).getRadioGroup().check(R.id.opcion1);
                            listPreguntas.get(numeroPregunta).setCumple(true);
                        } else {
                            listPreguntas.get(numeroPregunta).getRadioGroup().check(R.id.opcion2);
                            listPreguntas.get(numeroPregunta).setCumple(false);
                        }
                        adapterExpandableChecklist.contarPreguntasCumplen();
                    }else{
                        listPreguntas.get(numeroPregunta).getRadioGroup().clearCheck();
                        listPreguntas.get(numeroPregunta).setCumple(false);
                        adapterExpandableChecklist.contarPreguntasCumplen();
                    }
                }else{
                    listPreguntas.get(numeroPregunta).getRadioGroup().clearCheck();
                    listPreguntas.get(numeroPregunta).setCumple(false);
                    adapterExpandableChecklist.contarPreguntasCumplen();
                }
                alertDialog.dismiss();
            }
        });
    }

    public boolean validaEvidencias(List<Evidencia> listEvidencias){
        for(Evidencia ev:listEvidencias){
            if(ev.getIdEstatus() == 0){
                return false;
            }
        }
        return true;
    }
}
