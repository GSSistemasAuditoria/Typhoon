package com.elektra.typhoon.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.elektra.typhoon.database.UsuarioDBMethods;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.utils.Utils;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.Calendar;
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
    private String fechaFolio;

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
        public TextView textViewPregunta;

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
            //view.setOnClickListener(this);
        }

        /*@Override
        public void onClick(View v) {

        }//*/
    }

    public AdapterRecycleViewPreguntas(List<Pregunta> listPreguntas,Activity activity,int idRubro,TextView textViewCumplen,
                                       TextView textViewNoCumplen,AdapterExpandableChecklist adapterExpandableChecklist,String fechaFolio){
        this.activity = activity;
        this.listPreguntas = listPreguntas;
        this.idRubro = idRubro;
        this.textViewCumplen = textViewCumplen;
        this.textViewNoCumplen = textViewNoCumplen;
        this.adapterExpandableChecklist = adapterExpandableChecklist;
        this.fechaFolio = fechaFolio;
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
        ResponseLogin.Usuario usuario = new UsuarioDBMethods(activity).readUsuario(null,null);
        holder.linearLayout.setVisibility(View.GONE);
        holder.radioGroup.setEnabled(false);
        pregunta.setRadioGroup(holder.radioGroup);
        holder.textViewPregunta.setText(pregunta.getDescripcion());

        if(usuario.getIdrol() == 1){
            holder.imageViewAddEvidencia.setVisibility(View.VISIBLE);
            holder.imageViewAgregaEvidencia.setVisibility(View.VISIBLE);
            holder.textViewAddEvidencias.setVisibility(View.VISIBLE);
        }else{
            holder.imageViewAddEvidencia.setVisibility(View.GONE);
            holder.imageViewAgregaEvidencia.setVisibility(View.GONE);
            holder.textViewAddEvidencias.setVisibility(View.GONE);
        }

        holder.textViewAddEvidencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarPopupEvidencias(holder.textViewAddEvidencias,position);
            }
        });

        holder.imageViewAgregaEvidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarPopupEvidencias(holder.imageViewAgregaEvidencia,position);
            }
        });

        holder.imageViewAddEvidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarPopupEvidencias(holder.imageViewAddEvidencia,position);
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
        reiniciaRadioGroup(holder.radioGroup,pregunta.getListEvidencias());
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
                if ((evidenciaTemp.getIdEtapa() == 2 || evidenciaTemp.getIdEtapa() == 3) && evidenciaTemp.getIdEstatus() == 1) {
                    relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.verde_chk));
                } else if (evidenciaTemp.getIdEtapa() == 1 && evidenciaTemp.getIdEstatus() == 3) {
                    relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.rojo_chk));
                } else {
                    relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.gris));
                }
            }else if(usuario.getIdrol() == 2) {
                if (evidenciaTemp.getIdEtapa() == 3 && evidenciaTemp.getIdEstatus() == 1) {
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
        Button buttonBorrar = (Button) dialogLayout.findViewById(R.id.buttonBorrar);
        final TextView textViewNombreDocumento = (TextView) dialogLayout.findViewById(R.id.textViewNombreDocumento);

        if(usuario != null){
            if(usuario.getIdrol() == 1){
                buttonNoCumple.setVisibility(View.GONE);
            }
        }

        for(Evidencia evidencia:listPreguntas.get(numeroPregunta).getListEvidencias()) {
            if(evidencia.getIdEvidencia().equals(identificador)) {
                //if(evidencia.getIdEstatus() == 1){
                if(usuario.getIdrol() == 1) {
                    if (evidencia.getIdEtapa() == 2 && evidencia.getIdEstatus() == 1) {
                        buttonCumple.setBackground(drawableCumpleVerde);
                        buttonNoCumple.setBackground(drawableNoCumpleGris);
                    } else if (evidencia.getIdEtapa() == 1 && evidencia.getIdEstatus() == 3) {
                        buttonCumple.setBackground(drawableCumpleGris);
                        buttonNoCumple.setBackground(drawableNoCumpleRojo);
                    } else {
                        buttonCumple.setBackground(drawableCumpleGris);
                        buttonNoCumple.setBackground(drawableNoCumpleGris);
                    }
                }else if(usuario.getIdrol() == 2) {
                    if (evidencia.getIdEtapa() == 3 && evidencia.getIdEstatus() == 1) {
                        buttonCumple.setBackground(drawableCumpleVerde);
                        buttonNoCumple.setBackground(drawableNoCumpleGris);
                    } else if (evidencia.getIdEtapa() == 1 && evidencia.getIdEstatus() == 3) {
                        buttonCumple.setBackground(drawableCumpleGris);
                        buttonNoCumple.setBackground(drawableNoCumpleRojo);
                    } else {
                        buttonCumple.setBackground(drawableCumpleGris);
                        buttonNoCumple.setBackground(drawableNoCumpleGris);
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
                        }else if(usuario.getIdrol() == 2) {
                            evidencia.setIdEtapa(3);
                        }
                        try {
                            ContentValues contentValues = new ContentValues();
                            //contentValues.put("ID_ETAPA", getEtapaValidado(evidencia.getIdEtapa()));
                            if(usuario.getIdrol() == 1) {
                                contentValues.put("ID_ETAPA", 2);
                            }else if(usuario.getIdrol() == 2) {
                                contentValues.put("ID_ETAPA", 3);
                            }
                            contentValues.put("ID_ESTATUS",1);
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
                Utils.message(activity,"Validada");
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
                buttonCumple.setBackground(drawableCumpleVerde);
                buttonNoCumple.setBackground(drawableNoCumpleGris);
                alertDialog.dismiss();
                notifyDataSetChanged();
            }
        });

        buttonNoCumple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                notifyDataSetChanged();
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
                                new EvidenciasDBMethods(activity).deleteEvidencia("ID_EVIDENCIA = ? AND ID_REVISION = ? AND " +
                                        "ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_BARCO = ?", new String[]{
                                        identificador,
                                        String.valueOf(evidencia.getIdRevision()),
                                        String.valueOf(evidencia.getIdChecklist()),
                                        String.valueOf(evidencia.getIdRubro()),
                                        String.valueOf(evidencia.getIdPregunta()),
                                        String.valueOf(evidencia.getIdBarco())
                                });
                            }else{
                                //borrado lógico
                                evidencia.setIdEstatus(2);
                                evidencia.setIdEtapa(usuario.getIdrol());
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("ID_ETAPA",usuario.getIdrol());
                                contentValues.put("ID_ESTATUS",2);
                                String base64 = null;
                                contentValues.put("CONTENIDO",base64);
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
                            }
                            listPreguntas.get(numeroPregunta).getListEvidencias().remove(evidencia);
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
                            adapterExpandableChecklist.contarPreguntasCumplen();
                        } else {
                            listPreguntas.get(numeroPregunta).getRadioGroup().clearCheck();
                            updateRespuesta(evidenciaTemp,3);
                            listPreguntas.get(numeroPregunta).setCumple(false);
                            adapterExpandableChecklist.contarPreguntasCumplen();
                        }
                    } else {
                        listPreguntas.get(numeroPregunta).getRadioGroup().clearCheck();
                        updateRespuesta(evidenciaTemp,3);
                        listPreguntas.get(numeroPregunta).setCumple(false);
                        adapterExpandableChecklist.contarPreguntasCumplen();
                    }
                    notifyDataSetChanged();
                    alertDialog.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateRespuesta(Evidencia evidencia,int idRespuesta){
        if(evidencia != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID_RESPUESTA", idRespuesta);
            new ChecklistDBMethods(activity).updateRespuesta(contentValues, "ID_REVISION = ? AND " +
                            "ID_CHECKLIST = ? AND ID_PREGUNTA = ? AND ID_RUBRO = ? AND ID_BARCO = ? AND ID_REGISTRO = ?",
                    new String[]{String.valueOf(evidencia.getIdRevision()), String.valueOf(evidencia.getIdChecklist()), String.valueOf(evidencia.getIdPregunta()),
                            String.valueOf(evidencia.getIdRubro()), String.valueOf(evidencia.getIdBarco()), String.valueOf(evidencia.getIdRegistro())});
        }
    }

    private int getEtapaValidado(int etapa){
        switch (etapa){
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            default:
                return 4;
        }
    }

    public boolean validaEvidencias(List<Evidencia> listEvidencias){
        ResponseLogin.Usuario usuario = new UsuarioDBMethods(activity).readUsuario(null,null);
        for(Evidencia ev:listEvidencias){
            //if(ev.getIdEstatus() == 2){
            if(usuario.getIdrol() == 1) {
                if (ev.getIdEtapa() == 2 && ev.getIdEstatus() == 1) {

                }else{
                    return false;
                }
            }else if(usuario.getIdrol() == 2) {
                if (ev.getIdEtapa() == 3 && ev.getIdEstatus() == 1) {

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
        if(usuario.getIdrol() == 2) {
            for (Evidencia ev : listEvidencias) {
                if (ev.getIdEtapa() == 2 && ev.getIdEstatus() == 1) {

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
        popup.showAsDropDown(anchorView);
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
}
