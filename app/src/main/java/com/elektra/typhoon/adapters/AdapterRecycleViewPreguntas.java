package com.elektra.typhoon.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
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
import com.elektra.typhoon.database.BarcoDBMethods;
import com.elektra.typhoon.database.CatalogosDBMethods;
import com.elektra.typhoon.database.ChecklistDBMethods;
import com.elektra.typhoon.database.EvidenciasDBMethods;
import com.elektra.typhoon.database.HistoricoDBMethods;
import com.elektra.typhoon.database.UsuarioDBMethods;
import com.elektra.typhoon.encryption.Encryption;
import com.elektra.typhoon.login.MainActivity;
import com.elektra.typhoon.objetos.response.CatalogoBarco;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.Historico;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.ResponseDescargaPdf;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.objetos.response.RolUsuario;
import com.elektra.typhoon.objetos.response.RubroData;
import com.elektra.typhoon.service.ApiInterface;
import com.elektra.typhoon.utils.Utils;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

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
    private AdapterExpandableChecklist adapterExpandableChecklist;
    private String fechaFolio;
    private CatalogoBarco mBarco;
    private AdapterRecycleViewPreguntas adapterRecycleViewPreguntas;
    private Encryption encryption;
    private ResponseLogin.Usuario mUsuario;
    private List<RolUsuario> lstRoles;
    private RubroData mRubro;
    private ChecklistDBMethods mChecklistDBMethods;

    public class MyViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

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
        private ImageView imageViewSeleccionado;
        private ImageView imageViewSincronizadoFlag;

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
            imageViewSeleccionado = view.findViewById(R.id.imageViewSelect);
            imageViewSincronizadoFlag = view.findViewById(R.id.imageViewSincronizadoFlag);
            //view.setOnClickListener(this);
        }

        /*@Override
        public void onClick(View v) {

        }//*/
    }

    public AdapterRecycleViewPreguntas(RubroData mRubro, Activity activity, AdapterExpandableChecklist adapterExpandableChecklist,
                                       String fechaFolio, CatalogoBarco mBarco) {
        this.activity = activity;
        //this.listPreguntas = mRubro.getListPreguntasTemp();
        this.adapterExpandableChecklist = adapterExpandableChecklist;
        this.fechaFolio = fechaFolio;
        this.mBarco = mBarco;
        this.adapterRecycleViewPreguntas = this;
        this.mRubro = mRubro;
        mUsuario = new UsuarioDBMethods(activity).readUsuario();
        lstRoles = new CatalogosDBMethods(activity).readRolesUsuario(
                "SELECT ID_ROL,DESCRIPCION,IS_GEOCERCA FROM " + CatalogosDBMethods.TP_CAT_ROLES_USUARIO + " WHERE ID_ROL = ?",
                new String[]{String.valueOf(mUsuario.getIdrol())});
        //mChecklistDBMethods = new ChecklistDBMethods(activity);
    }

    public void setmRubro(RubroData mRubro) {
        this.mRubro = mRubro;
        this.listPreguntas = mRubro.getListPreguntasTemp();
    }

    public RubroData getmRubro() {
        return mRubro;
    }

    @NonNull
    @Override
    public AdapterRecycleViewPreguntas.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checklist_layout, parent, false);
        return new AdapterRecycleViewPreguntas.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        encryption = new Encryption();
        //acciones
        final Pregunta pregunta = listPreguntas.get(position);
        final ResponseLogin.Usuario usuario = new UsuarioDBMethods(activity).readUsuario();
        holder.linearLayout.setVisibility(View.GONE);
        holder.radioGroup.setEnabled(false);
        pregunta.setRadioGroup(holder.radioGroup);
        holder.textViewPregunta.setText(pregunta.getDescripcion());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.textViewPregunta.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }

        holder.imageViewSincronizadoFlag.setVisibility(View.GONE);

        if (usuario.getIdrol() == 1 || usuario.getIdrol() == 2 ||
                (usuario.getIdrol() == 3 && pregunta.isTierra())) {
            holder.imageViewAddEvidencia.setVisibility(View.VISIBLE);
            holder.imageViewAgregaEvidencia.setVisibility(View.VISIBLE);
            holder.textViewAddEvidencias.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewAddEvidencia.setVisibility(View.GONE);
            holder.imageViewAgregaEvidencia.setVisibility(View.GONE);
            holder.textViewAddEvidencias.setVisibility(View.GONE);
        }

        holder.imageViewDescargaPdf.setTag(holder);
        holder.imageViewDescargaPdf.setOnClickListener(mOnClickListener);

        holder.textViewAddEvidencias.setTag(holder);
        holder.textViewAddEvidencias.setOnClickListener(mOnClickListener);

        holder.imageViewAgregaEvidencia.setTag(holder);
        holder.imageViewAgregaEvidencia.setOnClickListener(mOnClickListener);

        holder.imageViewAddEvidencia.setTag(holder);
        holder.imageViewAddEvidencia.setOnClickListener(mOnClickListener);

        if (pregunta.getListEvidencias() != null) {
            if (pregunta.getListEvidencias().size() != 0) {

                int childs = holder.linearLayoutEvidencias.getChildCount();
                if (childs > 1) {
                    holder.linearLayoutEvidencias.removeViews(1, childs - 1);
                }

                holder.radioGroup.check(R.id.opcion2);
                holder.linearLayout.setVisibility(View.VISIBLE);
                holder.linearLayoutAddEvidencias.setVisibility(View.GONE);
                for (Evidencia evidencia : pregunta.getListEvidencias()) {
                    holder.linearLayoutEvidencias.addView(insertEvidencia(evidencia), 1);
                }
                holder.horizontalScrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.horizontalScrollView.fullScroll(ScrollView.FOCUS_LEFT);
                    }
                }, 100L);

                if (validaEvidencias(pregunta.getListEvidencias())) {
                    holder.radioGroup.check(R.id.opcion1);
                    pregunta.setCumple(true);
                } else {
                    holder.radioGroup.check(R.id.opcion2);
                    pregunta.setCumple(false);
                }
                adapterExpandableChecklist.contarPreguntasCumplen();
            } else {
                holder.linearLayout.setVisibility(View.GONE);
                holder.linearLayoutAddEvidencias.setVisibility(View.VISIBLE);
            }
        } else {
            holder.linearLayout.setVisibility(View.GONE);
            holder.linearLayoutAddEvidencias.setVisibility(View.VISIBLE);
        }


        if (mRubro.getListRespuestas().size() != 0) {
            for (RespuestaData respuestaData : mRubro.getListRespuestas()) {
                if (respuestaData.getIdPregunta() == pregunta.getIdPregunta()) {
                    if (respuestaData.getIdRespuesta() != null) {
                        if (respuestaData.getIdRespuesta() == 2) {
                            holder.radioGroup.check(R.id.opcion1);
                        } else if (respuestaData.getIdRespuesta() == 3) {
                            holder.radioGroup.check(R.id.opcion2);
                        } else {
                            holder.radioGroup.clearCheck();
                        }
                    } else {
                        holder.radioGroup.clearCheck();
                    }
                }

                if (respuestaData.getIdEstatus() == 2) {
                    holder.relativeLayoutDescargaPdf.setVisibility(View.VISIBLE);
                    holder.linearLayout.setVisibility(View.GONE);
                    holder.linearLayoutAddEvidencias.setVisibility(View.GONE);
                } else {
                    holder.relativeLayoutDescargaPdf.setVisibility(View.GONE);
                }

                if (respuestaData.getIdRevision() == pregunta.getIdRevision() &&
                        respuestaData.getIdChecklist() == pregunta.getIdChecklist() &&
                        respuestaData.getIdRubro() == pregunta.getIdRubro() &&
                        respuestaData.getIdPregunta() == pregunta.getIdPregunta() &&
                        respuestaData.getIdBarco() == mBarco.getIdBarco() && !pregunta.isSeleccionado()) {
                    pregunta.setSeleccionado(respuestaData.getSincronizado() == 1);
                }
            }
        }

        holder.imageViewSeleccionado.setEnabled(false);
        holder.imageViewSeleccionado.setTag(holder);
        holder.imageViewSeleccionado.setOnClickListener(mOnClickListener);

        if (pregunta.isSeleccionado()) {
            holder.imageViewSeleccionado.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_check_blue));
        } else {
            holder.imageViewSeleccionado.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_uncheck_blue));
        }

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MyViewHolder mHolder = null;
            if (v.getTag() instanceof MyViewHolder)
                mHolder = (MyViewHolder) v.getTag();
            switch (v.getId()) {
                case R.id.imageViewDescargaPdf:
                    if (mUsuario.getIdrol() == 3) {
                        descargaPDF(listPreguntas.get(mHolder.getAdapterPosition()));
                    } else {
                        Utils.message(activity, "No cuenta con el permiso para visualizar el documento");
                    }
                    break;
                case R.id.imageViewAgregaEvidencias:
                case R.id.textViewAgregarEvidencia:
                case R.id.imageViewAddEvidencia:
                    if (Utils.checkPermission(activity)) {
                        if (Utils.validaConfiguracionApp(activity)) {
                            if (lstRoles.get(0).isGeocerca()) {
                                if (Utils.requesTurnOnGps(activity)) {
                                    validaGeocerca(activity, mHolder.getAdapterPosition(),
                                            listPreguntas.get(mHolder.getAdapterPosition()).getListEvidencias(),
                                            v);
                                }
                            } else {
                                agregarEvidencias(listPreguntas.get(mHolder.getAdapterPosition()).getListEvidencias(),
                                        v, mHolder.getAdapterPosition());
                            }
                        }
                    }
                    break;
                case R.id.imageViewSelect:
                    Pregunta mPregunta = listPreguntas.get(mHolder.getAdapterPosition());
                    if (mPregunta.isSeleccionado()) {
                        mPregunta.setSeleccionado(false);
                        mRubro.setSeleccionado(false);
                        Utils.updatePregunta(activity, String.valueOf(mPregunta.getIdRevision()),
                                String.valueOf(mPregunta.getIdChecklist()), String.valueOf(mPregunta.getIdPregunta()),
                                String.valueOf(mPregunta.getIdRubro()), 0);//*/
                    } else {
                        mPregunta.setSeleccionado(true);
                        Utils.updatePregunta(activity, String.valueOf(mPregunta.getIdRevision()),
                                String.valueOf(mPregunta.getIdChecklist()), String.valueOf(mPregunta.getIdPregunta()),
                                String.valueOf(mPregunta.getIdRubro()), 1);//*/
                    }
                    notifyItemChanged(mHolder.getAdapterPosition());
                    break;
                case R.id.imageViewPreview:
                    try {
                        Evidencia mEvidencia = (Evidencia) v.getTag();
                        if (mEvidencia.getContenido() == null && mEvidencia.getOriginalBitmap() == null) {
                            EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(activity);
                            evidenciasDBMethods.readJustEvidencia(
                                    new String[]{String.valueOf(mEvidencia.getIdEvidencia())}, mEvidencia);
                        }
                        mostrarDocumento(mEvidencia);
                        mEvidencia.setOriginalBitmap(null);
                        mEvidencia.setContenido(null);
                    } catch (IOException e) {
                        Utils.message(activity, "Error al cargar imagen");
                        e.printStackTrace();
                    } catch (NumberFormatException e) {
                        Utils.message(activity, "Error al cargar imagen");
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void validaGeocerca(final Activity activity, final int position, final List<Evidencia> listEvidencias, final View view) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            List<CatalogoBarco> barcos = new BarcoDBMethods(activity).readBarcos();
                            CatalogoBarco catalogoBarco = null;
                            for (CatalogoBarco catalogoBarcoTemp : barcos) {
                                if (catalogoBarcoTemp.getIdBarco() == mBarco.getIdBarco()) {
                                    catalogoBarco = catalogoBarcoTemp;
                                    break;
                                }
                            }

                            float[] disResultado = new float[2];
                            double latitudeTyphoon = catalogoBarco.getLatitud();
                            double longitudeTyphoon = catalogoBarco.getLongitud();
                            float radioTyphoon = catalogoBarco.getRadio();

                            Location.distanceBetween(latitudeTyphoon, longitudeTyphoon, location.getLatitude(), location.getLongitude(), disResultado);
                            if (disResultado[0] > radioTyphoon) {
                                Utils.message(activity, "No se pueden agregar evidencias fuera de la zona de operación");
                            } else {
                                agregarEvidencias(listEvidencias, view, position);
                            }
                            //}
                        }
                    }
                });
    }

    private void agregarEvidencias(List<Evidencia> listEvidencias, View view, int position) {
        if (listEvidencias != null) {
            if (validaNumeroEvidencias(listEvidencias.size())) {
                String noEvidencias = new Encryption().decryptAES(activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE).getString(Constants.SP_LIMITE_EVIDENCIAS, ""));
                Utils.message(activity, "Sólo se permite agregar " + noEvidencias + " evidencias");
            } else {
                mostrarPopupEvidencias(view, position);
            }
        } else {
            mostrarPopupEvidencias(view, position);
        }
    }

    private boolean validaNumeroEvidencias(int evidenciasCargadas) {
        try {
            SharedPreferences sharedPrefs = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE);
            if (sharedPrefs.contains(Constants.SP_LIMITE_EVIDENCIAS)) {
                String evidenciasPermitidas = new Encryption().decryptAES(sharedPrefs.getString(Constants.SP_LIMITE_EVIDENCIAS, ""));
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
        } catch (NumberFormatException e) {
            Utils.message(activity, "No se descargo correctamente la configuración");
            return false;
        }
    }

    private RelativeLayout insertEvidencia(Evidencia mEvidencia) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.image_item_layout, null, false);
        ImageView iv = relativeLayout.findViewById(R.id.imageViewPreview);

        if (mUsuario.getIdrol() == 1) {
            if (mEvidencia.getIdEtapa() != 1 && mEvidencia.getIdEstatus() == 1) {
                relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.verde_chk));
            } else if ((mEvidencia.getIdEtapa() == 1 || mEvidencia.getIdEtapa() == 2) && mEvidencia.getIdEstatus() == 3) {
                relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.rojo_chk));
            } else {
                relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.gris));
            }
        } else {
            if (mEvidencia.getIdEtapa() > mUsuario.getIdrol() && mEvidencia.getIdEstatus() == 1) {
                relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.verde_chk));
            } else if ((mEvidencia.getIdEtapa() == 1 || mEvidencia.getIdEtapa() == 2) && mEvidencia.getIdEstatus() == 3) {
                relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.rojo_chk));
            } else {
                relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.gris));
            }
        }

        if (mEvidencia.getSmallBitmap() != null) {
            iv.setImageBitmap(mEvidencia.getSmallBitmap());
        } else {
            iv.setImageDrawable(activity.getResources().getDrawable(R.drawable.pdf_icon));
        }
        //iv.setContentDescription(mEvidencia.getIdEvidencia() + "," + mHolder.getAdapterPosition());
        iv.setTag(mEvidencia);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.setMarginStart(5);
        //iv.setLayoutParams(lp);
        relativeLayout.setLayoutParams(lp);
        iv.setOnClickListener(mOnClickListener);
        return relativeLayout;
    }

    @Override
    public int getItemCount() {
        if (listPreguntas != null) {
            return listPreguntas.size();
        }
        return 0;
    }

    private void mostrarDocumento(final Evidencia mEvidencia) {
        //final View viewImagen, final String identificador, final int numeroPregunta, final Activity activity, final Bitmap documento, String documentoPDF, String location
        LayoutInflater inflater = LayoutInflater.from(activity);
        final Pregunta pregunta = new Pregunta();
        pregunta.setIdPregunta(mEvidencia.getIdPregunta());
        final int positionPregunta = listPreguntas.indexOf(pregunta);
        String etapaString = "";
        View dialogLayout = null;

        if (mEvidencia.getOriginalBitmap() != null) {
            dialogLayout = inflater.inflate(R.layout.mostrar_documento_layout, null, false);
            final ImageView imageViewDocumento = dialogLayout.findViewById(R.id.imageViewDocumento);
            Glide.with(activity).load(mEvidencia.getOriginalBitmap()).into(imageViewDocumento);
        } else {
            dialogLayout = inflater.inflate(R.layout.mostrar_documento_pdf_layout, null, false);
            PDFView pdfViewDocumento = dialogLayout.findViewById(R.id.pdfViewDocumento);
            byte[] pdf = Utils.base64ToFile(mEvidencia.getContenido());
            pdfViewDocumento.fromBytes(pdf).load();

            dialogLayout.setMinimumWidth((int) (activity.getResources().getDisplayMetrics().widthPixels * 0.90));
            dialogLayout.setMinimumHeight((int) (activity.getResources().getDisplayMetrics().heightPixels * 0.90));
        }

        final Drawable drawableCumpleVerde = activity.getDrawable(R.mipmap.ic_cumple_verde);
        final Drawable drawableNoCumpleRojo = activity.getDrawable(R.mipmap.ic_nocumple_rojo);
        final Drawable drawableCumpleGris = activity.getDrawable(R.mipmap.ic_cumple_gris);
        final Drawable drawableNoCumpleGris = activity.getDrawable(R.mipmap.ic_nocumple_gris);

        final Button buttonCumple = dialogLayout.findViewById(R.id.buttonCumple);
        final Button buttonNoCumple = dialogLayout.findViewById(R.id.buttonNoCumple);
        final Button buttonBorrar = dialogLayout.findViewById(R.id.buttonBorrar);
        Button buttonHistorico = dialogLayout.findViewById(R.id.buttonHistorico);
        final TextView textViewNombreDocumento = dialogLayout.findViewById(R.id.textViewNombreDocumento);
        ImageView imageViewCerrarDialog = dialogLayout.findViewById(R.id.imageViewCloseDialog);

        if (mUsuario != null) {
            if (mUsuario.getIdrol() == 1) {
                buttonNoCumple.setVisibility(View.GONE);
            } else {
                if (mUsuario.getIdrol() == 3) {
                    buttonBorrar.setVisibility(View.VISIBLE);
                } else {
                    buttonBorrar.setVisibility(View.GONE);
                }
            }
        }

        buttonCumple.setBackground(drawableCumpleVerde);
        buttonNoCumple.setBackground(drawableNoCumpleRojo);

        etapaString = Utils.getEtapa(activity, mEvidencia.getIdEtapa());

        if (mUsuario.getIdrol() == 1) {
            if (mEvidencia.getIdEtapa() != 1 && mEvidencia.getIdEstatus() == 1) {
                buttonCumple.setVisibility(View.GONE);
                buttonBorrar.setVisibility(View.GONE);
            } else if (mEvidencia.getIdEtapa() == 1 && mEvidencia.getIdEstatus() == 3) {
                buttonCumple.setVisibility(View.GONE);
            } else if (mEvidencia.getIdEtapa() == 2 && mEvidencia.getIdEstatus() == 3) {
                buttonBorrar.setVisibility(View.GONE);
                buttonCumple.setVisibility(View.GONE);
                buttonNoCumple.setVisibility(View.GONE);
            }
        } else {
            if (mEvidencia.getIdEtapa() > mUsuario.getIdrol() && mEvidencia.getIdEstatus() == 1) {
                buttonBorrar.setVisibility(View.GONE);
                buttonCumple.setVisibility(View.GONE);
                buttonNoCumple.setVisibility(View.GONE);
            } else if (mEvidencia.getIdEtapa() == 1 && mEvidencia.getIdEstatus() == 3) {
                buttonBorrar.setVisibility(View.GONE);
                buttonCumple.setVisibility(View.GONE);
                buttonNoCumple.setVisibility(View.GONE);
            } else if (mEvidencia.getIdEtapa() == 2 && mEvidencia.getIdEstatus() == 3) {
                buttonBorrar.setVisibility(View.VISIBLE);
                buttonCumple.setVisibility(View.GONE);
                buttonNoCumple.setVisibility(View.GONE);
            } else if (mEvidencia.getIdEtapa() < mUsuario.getIdrol() && mEvidencia.getIdEstatus() == 1) {
                buttonCumple.setVisibility(View.GONE);
                buttonNoCumple.setVisibility(View.GONE);
            }

            if (mUsuario.getIdrol() > 2) {
                if (mEvidencia.getAgregadoCoordinador() == 1) {
                    buttonNoCumple.setVisibility(View.GONE);
                } else {
                    buttonBorrar.setVisibility(View.GONE);
                }
            } else if (mUsuario.getIdrol() == 2) {
                if (mEvidencia.getAgregadoLider() == 1) {
                    buttonNoCumple.setVisibility(View.GONE);
                }
            }
        }
        textViewNombreDocumento.setText(mRubro.getNombre() + "\n" + mEvidencia.getNombre());


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

        final String finalEtapaString = etapaString;

        buttonHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(activity);
                View dialogLayout = inflater.inflate(R.layout.historico_layout, null, false);

                ListView listViewHistorico = dialogLayout.findViewById(R.id.listViewHistorico);
                Button buttonCerrar = dialogLayout.findViewById(R.id.buttonCerrar);
                TextView textViewEstatus = dialogLayout.findViewById(R.id.textViewEstatusEvidencia);
                TextView textViewLocalizacion = dialogLayout.findViewById(R.id.textViewLocalizacionEvidencia);
                TextView textViewEtapa = dialogLayout.findViewById(R.id.textViewEtapaEvidencia);
                TextView textViewtituloBarco = dialogLayout.findViewById(R.id.textViewtituloBarco);
                TextView textViewTituloLocalizacion = dialogLayout.findViewById(R.id.textViewTituloLocalizacionEvidencia);

                textViewEstatus.setVisibility(View.VISIBLE);
                textViewLocalizacion.setVisibility(View.VISIBLE);
                textViewtituloBarco.setVisibility(View.VISIBLE);
                textViewTituloLocalizacion.setVisibility(View.VISIBLE);
                textViewEstatus.setText(mBarco.getNombre());
                textViewLocalizacion.setText(mEvidencia.getLocation());
                textViewEtapa.setText(finalEtapaString);

                HistoricoDBMethods historicoDBMethods = new HistoricoDBMethods(activity);
                List<Historico> listHistorico = historicoDBMethods.readHistorico(
                        "SELECT ID_EVIDENCIA,ID_ETAPA,ID_USUARIO,MOTIVO,CONSEC,ID_REVISION,ID_CHECKLIST,FECHA_MOD FROM " + historicoDBMethods.TP_TRAN_HISTORIAL_EVIDENCIA + " WHERE ID_EVIDENCIA = ? ORDER BY FECHA_MOD DESC",
                        new String[]{mEvidencia.getIdEvidencia()});
                HistoricoAdapter historicoAdapter = new HistoricoAdapter(activity, listHistorico);
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
                mEvidencia.setIdEstatus(1);
                if (mUsuario.getIdrol() == 1) {
                    mEvidencia.setIdEtapa(2);
                } else {
                    mEvidencia.setIdEtapa(mUsuario.getIdrol() + 1);
                }
                ContentValues contentValues = new ContentValues();
                if (mUsuario.getIdrol() == 1) {
                    contentValues.put("ID_ETAPA", 2);
                } else {
                    contentValues.put("ID_ETAPA", mUsuario.getIdrol() + 1);
                }
                contentValues.put("ID_ESTATUS", 1);
                new EvidenciasDBMethods(activity).updateEvidencia(contentValues,
                        "ID_EVIDENCIA = ? AND ID_REVISION = ? AND ID_CHECKLIST = ? " +
                                "AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_REGISTRO = ? AND ID_BARCO = ?",
                        new String[]{mEvidencia.getIdEvidencia(), String.valueOf(mEvidencia.getIdRevision()),
                                String.valueOf(mEvidencia.getIdChecklist()), String.valueOf(mEvidencia.getIdRubro()),
                                String.valueOf(mEvidencia.getIdPregunta()), String.valueOf(mEvidencia.getIdRegistro()),
                                String.valueOf(mBarco.getIdBarco())});

                Utils.message(activity, "Validada");
                crearHistorico(mEvidencia, mUsuario, "VALIDADA POR " + Utils.getRol(activity, mUsuario.getIdrol()).toUpperCase() + ": " + mUsuario.getNombre());

                Pregunta preguntaTemp = listPreguntas.get(positionPregunta);
                preguntaTemp.setSeleccionado(true);
                Utils.updatePregunta(activity, String.valueOf(preguntaTemp.getIdRevision()),
                        String.valueOf(preguntaTemp.getIdChecklist()), String.valueOf(preguntaTemp.getIdPregunta()),
                        String.valueOf(preguntaTemp.getIdRubro()), String.valueOf(mEvidencia.getIdBarco()), 1);//*/

                if (validaEvidencias(preguntaTemp.getListEvidencias())) {
                    preguntaTemp.getRadioGroup().check(R.id.opcion1);
                    updateRespuesta(mEvidencia, 2);
                    preguntaTemp.setCumple(true);
                } else {
                    preguntaTemp.getRadioGroup().check(R.id.opcion2);
                    updateRespuesta(mEvidencia, 3);
                    preguntaTemp.setCumple(false);
                }
                adapterExpandableChecklist.contarPreguntasCumplen();
                buttonCumple.setBackground(drawableCumpleVerde);
                buttonNoCumple.setBackground(drawableNoCumpleGris);
                alertDialog.dismiss();
                adapterRecycleViewPreguntas.notifyItemChanged(positionPregunta);
            }
        });

        buttonNoCumple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater inflater = LayoutInflater.from(activity);
                View dialogLayout = inflater.inflate(R.layout.rechazo_evidencia_layout, null, false);

                final EditText editTextMotivoRechazo = dialogLayout.findViewById(R.id.editTextMotivoRechazo);
                TextView textViewCancelar = dialogLayout.findViewById(R.id.buttonCancelar);

                final LinearLayout linearLayoutAceptar = dialogLayout.findViewById(R.id.linearLayoutAceptar);
                LinearLayout linearLayoutCancelar = dialogLayout.findViewById(R.id.linearLayoutCancelar);

                final AlertDialog alertDialogMotivo = new AlertDialog.Builder(activity)
                        .setView(dialogLayout)
                        .setNegativeButton("", null)
                        .setPositiveButton("", null)
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
                        if (!editTextMotivoRechazo.getText().toString().equals("")) {

                            rechazarEvidencia(mEvidencia, positionPregunta,
                                    editTextMotivoRechazo.getText().toString());
                            buttonCumple.setBackground(drawableCumpleGris);
                            buttonNoCumple.setBackground(drawableNoCumpleRojo);
                            Pregunta mPregunta = listPreguntas.get(positionPregunta);
                            mPregunta.setSeleccionado(true);

                            Utils.updatePregunta(activity, String.valueOf(mPregunta.getIdRevision()),
                                    String.valueOf(mPregunta.getIdChecklist()), String.valueOf(mPregunta.getIdPregunta()),
                                    String.valueOf(mPregunta.getIdRubro()), String.valueOf(mBarco.getIdBarco()), 1);//*/
                            notifyItemChanged(positionPregunta);
                            alertDialogMotivo.dismiss();
                            alertDialog.dismiss();

                        } else {
                            Utils.message(activity, "Debe especificar el motivo de rechazo");
                        }
                    }
                });
            }
        });

        buttonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //try {
                if (mUsuario.getIdrol() == 1 || mUsuario.getIdrol() == 2) {
                    //si esta rechazada la evidencia
                    if (mEvidencia.getIdEstatus() == 3) {
                        //borrado lógico
                        mEvidencia.setIdEstatus(2);
                        mEvidencia.setIdEtapa(mUsuario.getIdrol());
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("ID_ETAPA", mUsuario.getIdrol());
                        contentValues.put("ID_ESTATUS", 2);
                        String base64 = null;
                        contentValues.put("CONTENIDO", base64);

                        new EvidenciasDBMethods(activity).updateEvidencia(contentValues,
                                "ID_EVIDENCIA = ? AND ID_REVISION = ? AND ID_CHECKLIST = ? " +
                                        "AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_REGISTRO = ? AND ID_BARCO = ?",
                                new String[]{mEvidencia.getIdEvidencia(), String.valueOf(mEvidencia.getIdRevision()),
                                        String.valueOf(mEvidencia.getIdChecklist()), String.valueOf(mEvidencia.getIdRubro()),
                                        String.valueOf(mEvidencia.getIdPregunta()), String.valueOf(mEvidencia.getIdRegistro()),
                                        String.valueOf(mEvidencia.getIdBarco())});

                        crearHistorico(mEvidencia, mUsuario, "BORRADO POR " + Utils.getRol(activity, mUsuario.getIdrol()).toUpperCase() + ": " + mUsuario.getNombre());
                        Pregunta mPregunta = listPreguntas.get(positionPregunta);
                        mPregunta.setSeleccionado(true);
                        Utils.updatePregunta(activity, String.valueOf(mPregunta.getIdRevision()),
                                String.valueOf(mPregunta.getIdChecklist()), String.valueOf(mPregunta.getIdPregunta()),
                                String.valueOf(mPregunta.getIdRubro()), String.valueOf(mBarco.getIdBarco()), 1);
                    } else {
                        new EvidenciasDBMethods(activity).deleteEvidencia("ID_EVIDENCIA = ? AND ID_REVISION = ? AND " +
                                "ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_BARCO = ?", new String[]{
                                mEvidencia.getIdEvidencia(),
                                String.valueOf(mEvidencia.getIdRevision()),
                                String.valueOf(mEvidencia.getIdChecklist()),
                                String.valueOf(mEvidencia.getIdRubro()),
                                String.valueOf(mEvidencia.getIdPregunta()),
                                String.valueOf(mEvidencia.getIdBarco())
                        });
                    }
                    listPreguntas.get(positionPregunta).getListEvidencias().remove(mEvidencia);
                } else if (mUsuario.getIdrol() == 3) {
                    //borrado físico
                    new EvidenciasDBMethods(activity).deleteEvidencia("ID_EVIDENCIA = ? AND ID_REVISION = ? AND " +
                            "ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_BARCO = ?", new String[]{
                            mEvidencia.getIdEvidencia(),
                            String.valueOf(mEvidencia.getIdRevision()),
                            String.valueOf(mEvidencia.getIdChecklist()),
                            String.valueOf(mEvidencia.getIdRubro()),
                            String.valueOf(mEvidencia.getIdPregunta()),
                            String.valueOf(mEvidencia.getIdBarco())
                    });
                    Pregunta mPregunta = listPreguntas.get(positionPregunta);
                    mPregunta.setSeleccionado(true);
                    mPregunta.getListEvidencias().remove(mEvidencia);
                    Utils.updatePregunta(activity, String.valueOf(mPregunta.getIdRevision()),
                            String.valueOf(mPregunta.getIdChecklist()), String.valueOf(mPregunta.getIdPregunta()),
                            String.valueOf(mPregunta.getIdRubro()), String.valueOf(mBarco.getIdBarco()), 1);
                }

                Utils.message(activity, "Evidencia borrada");

                Pregunta mPregunta = listPreguntas.get(positionPregunta);
                if (mPregunta.getListEvidencias() != null) {
                    if (mPregunta.getListEvidencias().size() != 0) {
                        if (validaEvidencias(mPregunta.getListEvidencias())) {
                            mPregunta.getRadioGroup().check(R.id.opcion1);
                            updateRespuesta(mEvidencia, 2);
                            mPregunta.setCumple(true);
                        } else {
                            mPregunta.getRadioGroup().check(R.id.opcion2);
                            updateRespuesta(mEvidencia, 3);
                            mPregunta.setCumple(false);
                        }
                        adapterExpandableChecklist.contarPreguntasCumplen();
                    } else {
                        mPregunta.getRadioGroup().clearCheck();
                        //updateRespuesta(evidenciaTemp,3);
                        updateRespuesta(mEvidencia, null);
                        mPregunta.setCumple(false);
                        adapterExpandableChecklist.contarPreguntasCumplen();
                    }
                } else {
                    mPregunta.getRadioGroup().clearCheck();
                    //updateRespuesta(evidenciaTemp,3);
                    updateRespuesta(mEvidencia, null);
                    mPregunta.setCumple(false);
                    adapterExpandableChecklist.contarPreguntasCumplen();
                }
                adapterRecycleViewPreguntas.notifyItemChanged(positionPregunta);
                alertDialog.dismiss();
                /*}catch (NullPointerException e){
                    e.printStackTrace();
                    Utils.message(activity,"Error al borrar evidencia: " + e.getMessage());
                }//*/
            }
        });
    }

    private void crearHistorico(Evidencia evidencia, ResponseLogin.Usuario usuario, String motivo) {
        HistoricoDBMethods historicoDBMethods = new HistoricoDBMethods(activity);
        List<Historico> listHistorico = historicoDBMethods.readHistorico(
                "SELECT ID_EVIDENCIA,ID_ETAPA,ID_USUARIO,MOTIVO,CONSEC,ID_REVISION,ID_CHECKLIST,FECHA_MOD FROM " + historicoDBMethods.TP_TRAN_HISTORIAL_EVIDENCIA + " WHERE ID_EVIDENCIA = ?",
                new String[]{evidencia.getIdEvidencia()});
        int consecutivo = 1;
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

    private void rechazarEvidencia(Evidencia mEvidencia, int positionPregunta, String motivoRechazo) {
        /*int numeroPregunta, String identificador, Button buttonCumple, Button buttonNoCumple, AlertDialog alertDialog,
                                   Drawable drawableCumpleGris, Drawable drawableNoCumpleRojo, ResponseLogin.Usuario usuario, String motivoRechazo,
                                   AdapterRecycleViewPreguntas adapterRecycleViewPreguntas*/
        mEvidencia.setIdEstatus(3);
        mEvidencia.setIdEtapa(mEvidencia.getIdRol());
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_ETAPA", mEvidencia.getIdRol());
        contentValues.put("ID_ESTATUS", 3);
        //try {
        new EvidenciasDBMethods(activity).updateEvidencia(contentValues,
                "ID_EVIDENCIA = ? AND ID_REVISION = ? AND ID_CHECKLIST = ? " +
                        "AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_REGISTRO = ? AND ID_BARCO = ?",
                new String[]{mEvidencia.getIdEvidencia(), String.valueOf(mEvidencia.getIdRevision()),
                        String.valueOf(mEvidencia.getIdChecklist()), String.valueOf(mEvidencia.getIdRubro()),
                        String.valueOf(mEvidencia.getIdPregunta()), String.valueOf(mEvidencia.getIdRegistro()),
                        String.valueOf(mEvidencia.getIdBarco())});

        Utils.message(activity, "Rechazada");
        crearHistorico(mEvidencia, mUsuario, "RECHAZADO POR " + Utils.getRol(activity, mUsuario.getIdrol()).toUpperCase() + ": " + mUsuario.getNombre() + "\nMotivo: " +
                motivoRechazo);

        Pregunta mPregunta = listPreguntas.get(positionPregunta);
        if (validaEvidencias(mPregunta.getListEvidencias())) {
            mPregunta.getRadioGroup().check(R.id.opcion1);
            updateRespuesta(mEvidencia, 2);
            mPregunta.setCumple(true);
        } else {
            mPregunta.getRadioGroup().check(R.id.opcion2);
            updateRespuesta(mEvidencia, 3);
            mPregunta.setCumple(false);
        }
        adapterExpandableChecklist.contarPreguntasCumplen();
    }

    private void updateRespuesta(Evidencia evidencia, Integer idRespuesta) {
        if (evidencia != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID_RESPUESTA", idRespuesta);
            new ChecklistDBMethods(activity).updateRespuesta(contentValues, "ID_REVISION = ? AND " +
                            "ID_CHECKLIST = ? AND ID_PREGUNTA = ? AND ID_RUBRO = ? AND ID_BARCO = ? AND ID_REGISTRO = ?",
                    new String[]{String.valueOf(evidencia.getIdRevision()), String.valueOf(evidencia.getIdChecklist()), String.valueOf(evidencia.getIdPregunta()),
                            String.valueOf(evidencia.getIdRubro()), String.valueOf(evidencia.getIdBarco()), String.valueOf(evidencia.getIdRegistro())});
            RespuestaData mRespuestaData = new RespuestaData();
            mRespuestaData.setIdPregunta(evidencia.getIdPregunta());
            mRubro.getListRespuestas().get(mRubro.getListRespuestas().indexOf(mRespuestaData)).setIdRespuesta(idRespuesta);
        }
    }

    public boolean validaEvidencias(List<Evidencia> listEvidencias) {
        for (Evidencia ev : listEvidencias) {
            if (ev.getIdEtapa() == 1 || ev.getIdEstatus() != 1) {
                return false;
            }
        }
        return true;
    }

    public void reiniciaRadioGroup(RadioGroup radioGroup, List<Evidencia> listEvidencias) {
        boolean flag = true;
        ResponseLogin.Usuario usuario = new UsuarioDBMethods(activity).readUsuario();
        //if(usuario.getIdrol() == 2) {
        if (usuario.getIdrol() != 1) {
            for (Evidencia ev : listEvidencias) {
                //if (ev.getIdEtapa() == 2 && ev.getIdEstatus() == 1) {
                if (ev.getIdEtapa() == usuario.getIdrol() && ev.getIdEstatus() == 1) {

                } else {
                    flag = false;
                }
            }
            if (flag) {
                radioGroup.clearCheck();
            }

            boolean flag2 = true;
            for (Evidencia ev : listEvidencias) {
                if (ev.getIdEtapa() == 1 && ev.getIdEstatus() == 1) {

                } else {
                    flag2 = false;
                }
            }
            if (flag2) {
                radioGroup.clearCheck();
            }
        }
    }

    private void mostrarPopupEvidencias(View anchorView, final int position) {
        final PopupWindow popup = new PopupWindow(activity);
        View layout = activity.getLayoutInflater().inflate(R.layout.popup_evidencias_layout, null);
        popup.setContentView(layout);
        // Set content width and height

        final SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SP_NAME, Activity.MODE_PRIVATE);
        final Encryption encryption = new Encryption();

        Button buttonCamera = layout.findViewById(R.id.buttonCamera);
        Button buttonFiles = layout.findViewById(R.id.buttonFiles);

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String flagValida = "false";
                if (sharedPreferences.contains(Constants.SP_VALIDA_FECHA)) {
                    flagValida = encryption.decryptAES(sharedPreferences.getString(Constants.SP_VALIDA_FECHA, "false"));
                }
                if (flagValida.equals("true")) {
                    if (Utils.validaFechaRevision(activity, fechaFolio)) {
                        if (Utils.checkPermission(activity)) {
                            Utils.openCamera(activity, position);
                        }
                    } else {
                        Utils.message(activity, "No se permite agregar evidencias porque la revisión no es del mes actual");
                    }
                } else {
                    if (Utils.checkPermission(activity)) {
                        Utils.openCamera(activity, position);
                    }
                }

                popup.dismiss();
            }
        });

        buttonFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String flagValida = "false";
                if (sharedPreferences.contains(Constants.SP_VALIDA_FECHA)) {
                    flagValida = encryption.decryptAES(sharedPreferences.getString(Constants.SP_VALIDA_FECHA, "false"));
                }
                if (flagValida.equals("true")) {
                    if (Utils.validaFechaRevision(activity, fechaFolio)) {
                        if (Utils.checkPermission(activity)) {
                            seleccionarArchivo(position);
                        }
                    } else {
                        Utils.message(activity, "No se permite agregar evidencias porque la revisión no es del mes actual");
                    }
                } else {
                    if (Utils.checkPermission(activity)) {
                        seleccionarArchivo(position);
                    }
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
        popup.showAsDropDown(anchorView, 0, Math.round(anchorView.getY()) - dpToPx(100));
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
        //String[] mimetypes={"image/png","image/jpg","image/jpeg", "application/pdf"};//*/
        String[] mimetypes = {"application/pdf"}; //pdf*/
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activity.startActivityForResult(Intent.createChooser(intent, "Selecciona el archivo"), position);
        //activity.startActivity(intent.createChooser(intent, "Selecciona el archivo"));
    }

    private void descargaPDF(Pregunta mPregunta) {

        final ProgressDialog progressDialog = Utils.typhoonLoader(activity, "Descargando informe...");

        //try {

        final SharedPreferences sharedPrefs = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE);

        ApiInterface mApiService = Utils.getInterfaceService();
        Call<ResponseDescargaPdf> mService = mApiService.descargaPDF(Utils.getIPAddress(),
                encryption.decryptAES(sharedPrefs.getString(Constants.SP_JWT_TAG, "")),
                mPregunta.getIdRevision(), mPregunta.getIdPregunta());
        mService.enqueue(new Callback<ResponseDescargaPdf>() {
            @Override
            public void onResponse(Call<ResponseDescargaPdf> call, Response<ResponseDescargaPdf> response) {
                progressDialog.dismiss();
                if (response != null) {
                    if (response.body() != null) {
                        if (response.body().getDescargaPDF().getExito()) {
                            //try {

                            String jwt = encryption.encryptAES(response.headers().get("Authorization"));
                            sharedPrefs.edit().putString(Constants.SP_JWT_TAG, jwt).apply();

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
                                                .create();
                                        alertDialog.show();

                                        int widthA = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.90);
                                        int heightA = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.90);
                                        alertDialog.getWindow().setLayout(widthA, heightA);

                                        ViewGroup.LayoutParams params = pdfView.getLayoutParams();
                                        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                        params.height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.75);
                                        pdfView.requestLayout();

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
                                String mensaje = "" + response.errorBody().string();
                                int code = response.code();
                                //if(!mensaje.contains("No tiene permiso para ver")) {
                                if (code != 401) {
                                    Utils.message(activity, "Error al descargar informe: " + response.errorBody().string());
                                } else {
                                    sharedPrefs.edit().putBoolean(Constants.SP_LOGIN_TAG, false).apply();
                                    Utils.message(activity, "La sesión ha expirado");
                                    Intent intent = new Intent(activity, MainActivity.class);
                                    activity.startActivity(intent);
                                    activity.finish();
                                }
                                //Utils.message(activity, "Error al descargar informe: " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                                Utils.message(activity, "Error al descargar informe: " + e.getMessage());
                            }
                        } else {
                            Utils.message(activity, "Error al descargar informe");
                        }
                    }
                } else {
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
