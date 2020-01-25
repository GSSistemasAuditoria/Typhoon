package com.elektra.typhoon.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.BarcoDBMethods;
import com.elektra.typhoon.database.CatalogosDBMethods;
import com.elektra.typhoon.database.ChecklistDBMethods;
import com.elektra.typhoon.database.EvidenciasDBMethods;
import com.elektra.typhoon.database.UsuarioDBMethods;
import com.elektra.typhoon.encryption.Encryption;
import com.elektra.typhoon.login.MainActivity;
import com.elektra.typhoon.objetos.response.CatalogoBarco;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.ResponseDescargaPdf;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.objetos.response.RolUsuario;
import com.elektra.typhoon.objetos.response.RubroData;
import com.elektra.typhoon.service.ApiInterface;
import com.elektra.typhoon.service.AsyncTaskGral;
import com.elektra.typhoon.service.Delegate;
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
public class AdapterExpandableChecklist extends BaseExpandableListAdapter {

    private List<RubroData> listRubros;
    private Activity activity;
    private String fechaFolio;
    private ResponseLogin.Usuario usuario;
    private CatalogoBarco mBarco;
    private SharedPreferences mSharedPreferences;
    private PopupWindow popup;
    private Button buttonCamera;
    private Button buttonFiles;

    public List<RubroData> getListRubros() {
        return listRubros;
    }

    public AdapterExpandableChecklist(List<RubroData> listRubros, Activity activity, String fechaFolio, CatalogoBarco mBarco) {
        this.listRubros = listRubros;
        this.activity = activity;
        this.fechaFolio = fechaFolio;
        this.mBarco = mBarco;
        usuario = new UsuarioDBMethods(activity).readUsuario();
        mEncryption = new Encryption();
        mSharedPreferences = activity.getSharedPreferences(Constants.SP_NAME, Activity.MODE_PRIVATE);
        popup = new PopupWindow(activity);
        View layout = activity.getLayoutInflater().inflate(R.layout.popup_evidencias_layout, null);
        popup.setContentView(layout);
        buttonCamera = layout.findViewById(R.id.buttonCamera);
        buttonFiles = layout.findViewById(R.id.buttonFiles);
        buttonCamera.setOnClickListener(mOnClickListener);
        buttonFiles.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getGroupCount() {
        return listRubros.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return listRubros.get(i).getListPreguntasTemp().size();
    }

    @Override
    public Object getGroup(int i) {
        return listRubros.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listRubros.get(i).getListPreguntasTemp().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }


    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int position, boolean b, View view, ViewGroup viewGroup) {
        final RubroData rubro = listRubros.get(position);
        if (view == null) {
            //LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_layout, viewGroup, false);
        }
        TextView textViewTituloEncabezado = view.findViewById(R.id.textViewTituloHeader);
        ImageView imageView = view.findViewById(R.id.imageViewIconoGrupo);
        ImageView imageViewSelect = view.findViewById(R.id.imageViewSelect);
        textViewTituloEncabezado.setText(rubro.getNombre());
        imageView.setImageResource(b ? R.mipmap.ic_group_close : R.mipmap.ic_group_open);
        imageViewSelect.setEnabled(false);
        imageView.setTag(position);
        //imageViewSelect.setOnClickListener(mOnClickListener);
        imageViewSelect.setImageDrawable(activity.getResources().getDrawable(
                rubro.isSeleccionado() ? R.mipmap.ic_check_white : R.mipmap.ic_uncheck_white));

        return view;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = 0;
            MyViewHolder mHolder = null;
            if (v.getTag() instanceof MyViewHolder) {
                mHolder = (MyViewHolder) v.getTag();
            }
            switch (v.getId()) {
                case R.id.imageViewSelect:
                    position = (int) v.getTag();
                    RubroData rubro = listRubros.get(position);
                    if (rubro.isSeleccionado()) {
                        rubro.setSeleccionado(false);
                        for (Pregunta pregunta : rubro.getListPreguntasTemp()) {
                            pregunta.setSeleccionado(false);
                            Utils.updatePregunta(activity, String.valueOf(pregunta.getIdRevision()),
                                    String.valueOf(pregunta.getIdChecklist()), String.valueOf(pregunta.getIdPregunta()),
                                    String.valueOf(pregunta.getIdRubro()), 0);//*/
                        }
                    } else {
                        rubro.setSeleccionado(true);
                        for (Pregunta pregunta : rubro.getListPreguntasTemp()) {
                            pregunta.setSeleccionado(true);
                            Utils.updatePregunta(activity, String.valueOf(pregunta.getIdRevision()),
                                    String.valueOf(pregunta.getIdChecklist()), String.valueOf(pregunta.getIdPregunta()),
                                    String.valueOf(pregunta.getIdRubro()), 1);//*/
                        }
                    }
                    notifyDataSetChanged();
                    break;
                case R.id.imageViewDescargaPdf:
                    if (usuario.getIdrol() == 3) {
                        descargaPDF(mHolder.mPregunta.getIdRevision(), mHolder.mPregunta.getIdPregunta());
                    } else {
                        Utils.message(activity, "No cuenta con el permiso para visualizar el documento");
                    }
                    break;
                case R.id.textViewAgregarEvidencia:
                    if (Utils.checkPermission(activity)) {
                        if (Utils.validaConfiguracionApp(activity)) {
                            List<RolUsuario> listRoles = new CatalogosDBMethods(activity).readRolesUsuario(
                                    "SELECT ID_ROL,DESCRIPCION,IS_GEOCERCA FROM " + CatalogosDBMethods.TP_CAT_ROLES_USUARIO + " WHERE ID_ROL = ?",
                                    new String[]{String.valueOf(usuario.getIdrol())});
                            //String flagGPS = new Encryption().decryptAES(activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE).getString(Constants.SP_GPS_FLAG, ""));
                            //if (flagGPS.equals("true")) {
                            /*if (Utils.validaGeocerca(activity)) {
                                agregarEvidencias(pregunta.getListEvidencias(), holder.textViewAddEvidencias, position);
                            } else {
                                Utils.message(activity, "No se encuentra dentro de la zona");
                            }//*/
                            if (listRoles.get(0).isGeocerca()) {
                                if (Utils.requesTurnOnGps(activity)) {
                                    validaGeocerca(activity, position, mHolder.mPregunta.getListEvidencias(), mHolder.textViewAddEvidencias);
                                }
                            } else {
                                agregarEvidencias(mHolder.mPregunta.getListEvidencias(), mHolder.textViewAddEvidencias, position);
                            }
                        }
                    }
                    break;
                case R.id.imageViewAgregaEvidencias:
                    if (Utils.checkPermission(activity)) {
                        if (Utils.validaConfiguracionApp(activity)) {
                            //String flagGPS = new Encryption().decryptAES(activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE).getString(Constants.SP_GPS_FLAG, ""));
                            //if (flagGPS.equals("true")) {
                            /*if (Utils.validaGeocerca(activity)) {
                                agregarEvidencias(pregunta.getListEvidencias(), holder.imageViewAgregaEvidencia, position);
                            } else {
                                Utils.message(activity, "No se encuentra dentro de la zona");
                            }//*/
                            List<RolUsuario> listRoles = new CatalogosDBMethods(activity).readRolesUsuario(
                                    "SELECT ID_ROL,DESCRIPCION,IS_GEOCERCA FROM " + CatalogosDBMethods.TP_CAT_ROLES_USUARIO + " WHERE ID_ROL = ?",
                                    new String[]{String.valueOf(usuario.getIdrol())});
                            if (listRoles.get(0).isGeocerca()) {
                                if (Utils.requesTurnOnGps(activity)) {
                                    validaGeocerca(activity, position, mHolder.mPregunta.getListEvidencias(), mHolder.imageViewAgregaEvidencia);
                                }
                            } else {
                                agregarEvidencias(mHolder.mPregunta.getListEvidencias(), mHolder.imageViewAgregaEvidencia, position);
                            }
                        }
                    }
                    break;
                case R.id.imageViewAddEvidencia:
                    if (Utils.checkPermission(activity)) {
                        if (Utils.validaConfiguracionApp(activity)) {
                            //String flagGPS = new Encryption().decryptAES(activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE).getString(Constants.SP_GPS_FLAG, ""));
                            //if (flagGPS.equals("true")) {
                            /*if (Utils.validaGeocerca(activity)) {
                                agregarEvidencias(pregunta.getListEvidencias(), holder.imageViewAddEvidencia, position);
                            } else {
                                Utils.message(activity, "No se pueden agregar evidencias fuera de la zona de operación");
                            }//*/
                            List<RolUsuario> listRoles = new CatalogosDBMethods(activity).readRolesUsuario(
                                    "SELECT ID_ROL,DESCRIPCION,IS_GEOCERCA FROM " + CatalogosDBMethods.TP_CAT_ROLES_USUARIO + " WHERE ID_ROL = ?",
                                    new String[]{String.valueOf(usuario.getIdrol())});
                            if (listRoles.get(0).isGeocerca()) {
                                if (Utils.requesTurnOnGps(activity)) {
                                    validaGeocerca(activity, position, mHolder.mPregunta.getListEvidencias(), mHolder.imageViewAddEvidencia);
                                }
                            } else {
                                agregarEvidencias(mHolder.mPregunta.getListEvidencias(), mHolder.imageViewAddEvidencia, position);
                            }
                        }
                    }
                    break;
                case R.id.buttonCamera:
                    position = (int) v.getTag();
                    String flagValida = "false";
                    if (mSharedPreferences.contains(Constants.SP_VALIDA_FECHA)) {
                        flagValida = mEncryption.decryptAES(mSharedPreferences.getString(Constants.SP_VALIDA_FECHA, "false"));
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
                    break;
                case R.id.buttonFiles:
                    position = (int) v.getTag();
                    String valida = "false";
                    if (mSharedPreferences.contains(Constants.SP_VALIDA_FECHA)) {
                        valida = mEncryption.decryptAES(mSharedPreferences.getString(Constants.SP_VALIDA_FECHA, "false"));
                    }
                    if (valida.equals("true")) {
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
                    break;
                case R.id.imageViewPreview:
                    try {
                        Evidencia mEvidencia = (Evidencia) v.getTag();
                        //int identificador = view.getId();
                        //int numeroPregunta = Integer.parseInt(view.getContentDescription().toString());
                        String[] temp = view.getContentDescription().toString().split(",");
                        String identificador = temp[0];
                        int numeroPregunta = Integer.parseInt(temp[1]);
                        //Bitmap bitmap = listPreguntas.get(numeroPregunta).getListEvidencias().get(identificador - 1).getOriginalBitmap();
                        //Bitmap bitmap = null;descomentar si se requiere
                        Pregunta pregunta = listPreguntas.get(numeroPregunta);
                        EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(activity);
                        Evidencia evidencia = evidenciasDBMethods.readEvidencia(
                                "SELECT ID_EVIDENCIA,NOMBRE,CONTENIDO,ID_ESTATUS,ID_ETAPA,ID_REVISION,ID_CHECKLIST," +
                                        "ID_RUBRO,ID_PREGUNTA,ID_REGISTRO,ID_BARCO,LATITUDE,LONGITUDE,AGREGADO_COORDINADOR,NUEVO,FECHA_MOD," +
                                        "LOCATION,ID_ROL,ID_USUARIO,AGREGADO_LIDER FROM " + EvidenciasDBMethods.TP_TRAN_CL_EVIDENCIA + " WHERE ID_EVIDENCIA = ? AND ID_REVISION = ? " +
                                        "AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_BARCO = ?",
                                new String[]{String.valueOf(identificador), String.valueOf(pregunta.getIdRevision()),
                                        String.valueOf(pregunta.getIdChecklist()), String.valueOf(pregunta.getIdRubro()),
                                        String.valueOf(pregunta.getIdPregunta()), String.valueOf(pregunta.getIdBarco())});
                    /*for(Evidencia evidencia:listPreguntas.get(numeroPregunta).getListEvidencias()){
                        if(evidencia.getIdEvidencia() == identificador){
                            bitmap = evidencia.getOriginalBitmap();
                        }
                    }//*/
                        if (evidencia != null) {
                            //bitmap = evidencia.getOriginalBitmap();descomentar si se requiere
                            //mostrarDocumento(view, identificador, numeroPregunta, activity, bitmap,evidencia.getContenido());descomentar si se requiere
                            mostrarDocumento(view, identificador, numeroPregunta, activity, evidencia.getOriginalBitmap(), evidencia.getContenido(), evidencia.getLocation());
                        } else {
                            Utils.message(activity, "No se pudo cargar la imagen");
                        }
                    } catch (IOException e) {
                        Utils.message(activity, "Error al cargar imagen");
                        e.printStackTrace();
                    } catch (NumberFormatException e) {
                        Utils.message(activity, "Error al cargar imagen");
                        e.printStackTrace();
                    }
                    break;
                case R.id.imageViewSelect:
                    if (mHolder.mPregunta.isSeleccionado()) {
                        mHolder.mPregunta.setSeleccionado(false);
                        mHolder.mPregunta.setSeleccionado(false);
                        Utils.updatePregunta(activity, String.valueOf(mHolder.mPregunta.getIdRevision()),
                                String.valueOf(mHolder.mPregunta.getIdChecklist()), String.valueOf(mHolder.mPregunta.getIdPregunta()),
                                String.valueOf(mHolder.mPregunta.getIdRubro()), 0);//*/

                    } else {
                        pregunta.setSeleccionado(true);
                        Utils.updatePregunta(activity, String.valueOf(pregunta.getIdRevision()),
                                String.valueOf(pregunta.getIdChecklist()), String.valueOf(pregunta.getIdPregunta()),
                                String.valueOf(pregunta.getIdRubro()), 1);//*/
                    }
                    notifyDataSetChanged();
                    break;
                default:
                    Utils.message(activity, "Opción inválida");
                    break;
            }
        }
    };

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
        private Pregunta mPregunta;

        public MyViewHolder(View view) {
            super(view);
            linearLayout = view.findViewById(R.id.linearLayoutEvidencias);
            linearLayoutAddEvidencias = view.findViewById(R.id.linearLayoutAgregarEvidencia);
            linearLayoutEvidencias = view.findViewById(R.id.linearLayoutImagenesEvidencia);
            textViewAddEvidencias = view.findViewById(R.id.textViewAgregarEvidencia);
            imageViewAddEvidencia = view.findViewById(R.id.imageViewAddEvidencia);
            horizontalScrollView = view.findViewById(R.id.horizontalScrollView);
            radioGroup = view.findViewById(R.id.myRadioGroup);
            imageViewAgregaEvidencia = view.findViewById(R.id.imageViewAgregaEvidencias);
            textViewPregunta = view.findViewById(R.id.textViewPregunta);
            relativeLayoutDescargaPdf = view.findViewById(R.id.relativeLayoutDescargaPdf);
            imageViewDescargaPdf = view.findViewById(R.id.imageViewDescargaPdf);
            imageViewSeleccionado = view.findViewById(R.id.imageViewSelect);
            imageViewSincronizadoFlag = view.findViewById(R.id.imageViewSincronizadoFlag);
        }
    }

    @Override
    public View getChildView(final int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final RubroData rubro = listRubros.get(i);
        final MyViewHolder mHolder;
        if (view == null) {
            //LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_checklist_layout, viewGroup, false);
            mHolder = new MyViewHolder(view);
            view.setTag(mHolder);
        } else {
            mHolder = (MyViewHolder) view.getTag();
        }

        Pregunta pregunta = listRubros.get(i).getListPreguntasTemp().get(i1);
        mHolder.mPregunta = pregunta;
        mHolder.linearLayout.setVisibility(View.GONE);
        mHolder.radioGroup.setEnabled(false);
        pregunta.setRadioGroup(mHolder.radioGroup);
        mHolder.textViewPregunta.setText(pregunta.getDescripcion());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mHolder.textViewPregunta.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }
        //holderTemp = holder;

        mHolder.imageViewSincronizadoFlag.setVisibility(View.GONE);

        //ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(activity);
        /*List<RespuestaData> listRespuestas = checklistDBMethods.readRespuesta(
                "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_RUBRO,ID_ESTATUS,ID_BARCO,ID_REGISTRO,ID_RESPUESTA,SINCRONIZADO FROM " + checklistDBMethods.TP_TRAN_CL_RESPUESTA + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? " +
                        "AND ID_PREGUNTA = ? AND ID_RUBRO = ? AND ID_BARCO = ?", new String[]{String.valueOf(pregunta.getIdRevision()),
                        String.valueOf(pregunta.getIdChecklist()), String.valueOf(pregunta.getIdPregunta()), String.valueOf(pregunta.getIdRubro()),
                        String.valueOf(idBarco)});*/

        if (usuario.getIdrol() == 1 || usuario.getIdrol() == 2) {
            mHolder.imageViewAddEvidencia.setVisibility(View.VISIBLE);
            mHolder.imageViewAgregaEvidencia.setVisibility(View.VISIBLE);
            mHolder.textViewAddEvidencias.setVisibility(View.VISIBLE);
        } else {
            mHolder.imageViewAddEvidencia.setVisibility(View.GONE);
            mHolder.imageViewAgregaEvidencia.setVisibility(View.GONE);
            mHolder.textViewAddEvidencias.setVisibility(View.GONE);
            if (usuario.getIdrol() == 3 && pregunta.isTierra()) {
                mHolder.imageViewAddEvidencia.setVisibility(View.VISIBLE);
                mHolder.imageViewAgregaEvidencia.setVisibility(View.VISIBLE);
                mHolder.textViewAddEvidencias.setVisibility(View.VISIBLE);
            } else {
                mHolder.imageViewAddEvidencia.setVisibility(View.GONE);
                mHolder.imageViewAgregaEvidencia.setVisibility(View.GONE);
                mHolder.textViewAddEvidencias.setVisibility(View.GONE);
            }
        }
        mHolder.imageViewDescargaPdf.setTag(mHolder);
        mHolder.textViewAddEvidencias.setTag(mHolder);
        mHolder.imageViewAgregaEvidencia.setTag(mHolder);
        mHolder.imageViewAddEvidencia.setTag(mHolder);

        mHolder.imageViewDescargaPdf.setOnClickListener(mOnClickListener);
        mHolder.textViewAddEvidencias.setOnClickListener(mOnClickListener);
        mHolder.imageViewAgregaEvidencia.setOnClickListener(mOnClickListener);
        mHolder.imageViewAddEvidencia.setOnClickListener(mOnClickListener);

        if (pregunta.getListEvidencias() != null) {
            if (pregunta.getListEvidencias().size() != 0) {

                int childs = mHolder.linearLayoutEvidencias.getChildCount();
                if (childs > 1) {
                    mHolder.linearLayoutEvidencias.removeViews(1, childs - 1);
                }

                mHolder.radioGroup.check(R.id.opcion2);
                mHolder.linearLayout.setVisibility(View.VISIBLE);
                mHolder.linearLayoutAddEvidencias.setVisibility(View.GONE);
                for (Evidencia evidencia : pregunta.getListEvidencias()) {
                    mHolder.linearLayoutEvidencias.addView(insertEvidencia(evidencia, pregunta), 1);
                }
                mHolder.horizontalScrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mHolder.horizontalScrollView.fullScroll(ScrollView.FOCUS_LEFT);
                    }
                }, 100L);

                if (validaEvidencias(pregunta.getListEvidencias())) {
                    //radioGroupTemp.check(R.id.opcion1);
                    mHolder.radioGroup.check(R.id.opcion1);
                    pregunta.setCumple(true);
                } else {
                    //radioGroupTemp.check(R.id.opcion2);
                    mHolder.radioGroup.check(R.id.opcion2);
                    pregunta.setCumple(false);
                }

            } else {
                mHolder.linearLayout.setVisibility(View.GONE);
                mHolder.linearLayoutAddEvidencias.setVisibility(View.VISIBLE);
            }
        } else {
            mHolder.linearLayout.setVisibility(View.GONE);
            mHolder.linearLayoutAddEvidencias.setVisibility(View.VISIBLE);
        }

        /*if (pregunta.getListEvidencias() != null) {
            if (pregunta.getListEvidencias().size() != 0) {
                if (validaEvidencias(pregunta.getListEvidencias())) {
                    holder.radioGroup.check(R.id.opcion1);
                } else {
                    holder.radioGroup.check(R.id.opcion2);
                }
            }
        }*/

        List<RespuestaData> listRespuestas = rubro.getListRespuestas();
        if (listRespuestas.size() != 0) {
            for (RespuestaData respuestaData : listRespuestas) {
                if (respuestaData.getIdRespuesta() != null) {
                    if (respuestaData.getIdRespuesta() == 2) {
                        mHolder.radioGroup.check(R.id.opcion1);
                    } else if (respuestaData.getIdRespuesta() == 3) {
                        mHolder.radioGroup.check(R.id.opcion2);
                    } else {
                        mHolder.radioGroup.clearCheck();
                    }
                } else {
                    mHolder.radioGroup.clearCheck();
                }

                if (respuestaData.getIdEstatus() == 2) {
                    mHolder.relativeLayoutDescargaPdf.setVisibility(View.VISIBLE);
                    mHolder.linearLayout.setVisibility(View.GONE);
                    mHolder.linearLayoutAddEvidencias.setVisibility(View.GONE);
                } else {
                    mHolder.relativeLayoutDescargaPdf.setVisibility(View.GONE);
                }

                /*if(respuestaData.getSincronizado() == 1){
                    holder.imageViewSincronizadoFlag.setVisibility(View.VISIBLE);
                }else{
                    holder.imageViewSincronizadoFlag.setVisibility(View.GONE);
                }//*/
            }
        }

        mHolder.imageViewSeleccionado.setEnabled(false);
        mHolder.imageViewSeleccionado.setTag(mHolder);
        mHolder.imageViewSeleccionado.setOnClickListener(mOnClickListener);

        if (pregunta.isSeleccionado()) {
            holder.imageViewSeleccionado.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_check_blue));
        } else {
            holder.imageViewSeleccionado.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_uncheck_blue));
        }

        final RecyclerView recyclerViewPreguntas = view.findViewById(R.id.recyclerViewPreguntas);
        new AsyncTaskGral(activity, new Delegate() {
            @Override
            public void getDelegate(String result) {
            }

            @Override
            public String executeInBackground() {
                EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(activity);

                for (Pregunta pregunta : rubro.getListPreguntasTemp()) {
                    try {
                        List<Evidencia> listEvidencias = evidenciasDBMethods.readEvidencias("SELECT ID_EVIDENCIA,NOMBRE,CONTENIDO_PREVIEW,ID_ESTATUS,ID_ETAPA,ID_REVISION,ID_CHECKLIST," +
                                        "ID_RUBRO,ID_PREGUNTA,ID_REGISTRO,ID_BARCO,CONTENIDO,LATITUDE,LONGITUDE,AGREGADO_COORDINADOR,NUEVO,FECHA_MOD," +
                                        "LOCATION,ID_ROL,ID_USUARIO,AGREGADO_LIDER FROM " + evidenciasDBMethods.TP_TRAN_CL_EVIDENCIA +
                                        " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_BARCO = ?" +
                                        " AND ID_ESTATUS != 2",
                                new String[]{String.valueOf(pregunta.getIdRevision()), String.valueOf(pregunta.getIdChecklist()),
                                        String.valueOf(pregunta.getIdRubro()), String.valueOf(pregunta.getIdPregunta()),
                                        String.valueOf(idBarco)}, false);
                        pregunta.setListEvidencias(listEvidencias);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

        /*if(b) {
            progressDialog.dismiss();
        }//*/

                //textViewTituloRubro.setText("Rubro " + i);
                final AdapterRecycleViewPreguntas adapterRecycleViewPreguntas = new AdapterRecycleViewPreguntas(rubro.getListPreguntasTemp(), activity, i,
                        textViewCumplen, textViewNoCumplen, AdapterExpandableChecklist.this, fechaFolio, idBarco, rubro);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerViewPreguntas.setAdapter(adapterRecycleViewPreguntas);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
                        recyclerViewPreguntas.setLayoutManager(layoutManager);
                        recyclerViewPreguntas.setNestedScrollingEnabled(false);
                        adapterRecycleViewPreguntasTemp = adapterRecycleViewPreguntas;
                    }
                });
                return null;
            }
        }, null).execute();
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    private void descargaPDF(int idRevision, int idPregunta) {

        final ProgressDialog progressDialog = Utils.typhoonLoader(activity, "Descargando informe...");

        //try {

        final SharedPreferences sharedPrefs = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE);

        ApiInterface mApiService = Utils.getInterfaceService();
        Call<ResponseDescargaPdf> mService = mApiService.descargaPDF(Utils.getIPAddress(), encryption.decryptAES(sharedPrefs.getString(Constants.SP_JWT_TAG, "")), idRevision, idPregunta);
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

    private void validaGeocerca(final Activity activity, final int position, final List<Evidencia> listEvidencias, final View view) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            float[] disResultado = new float[2];
                            double latitudeTyphoon = mBarco.getLatitud();
                            double longitudeTyphoon = mBarco.getLongitud();
                            float radioTyphoon = mBarco.getRadio();
                            Location.distanceBetween(latitudeTyphoon, longitudeTyphoon, location.getLatitude(), location.getLongitude(), disResultado);
                            //Hardcode para probar la ubicacion del dispositivo ficticia
                            //Location.distanceBetween(latitudeTyphoon, longitudeTyphoon, 19.310916, -99.183039, disResultado);
                            //Location.distanceBetween(19.3046277,-99.2037863,miPosicion.getLatitude(),miPosicion.getLongitude(),disResultado);
                            //Location.distanceBetween(19.3046277,-99.2037863,19.304980, -99.204047,disResultado);

                            if (disResultado[0] > radioTyphoon) {
                                Utils.message(activity, "No se pueden agregar evidencias fuera de la zona de operación");
                            } else {
                                agregarEvidencias(listEvidencias, view, position);
                            }
                        }
                    }
                });
    }

    Encryption mEncryption;

    private void agregarEvidencias(List<Evidencia> listEvidencias, View view, int position) {
        if (listEvidencias != null) {
            int maxEvidencias = Integer.parseInt(mEncryption.decryptAES(mSharedPreferences.getString(Constants.SP_LIMITE_EVIDENCIAS, "5")));
            if (listEvidencias.size() >= maxEvidencias) {
                Utils.message(activity, "Sólo se permite agregar " + maxEvidencias + " evidencias");
            } else {
                mostrarPopupEvidencias(view, position);
            }
        } else {
            mostrarPopupEvidencias(view, position);
        }
    }

    private void mostrarPopupEvidencias(View anchorView, final int position) {
        buttonCamera.setTag(position);
        buttonFiles.setTag(position);
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
        String[] mimetypes = {"application/pdf"}; //pdf*/
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activity.startActivityForResult(intent.createChooser(intent, "Selecciona el archivo"), position);
    }

    private RelativeLayout insertEvidencia(Evidencia mEvidencia, Pregunta mPregunta) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.image_item_layout, null, false);

        //ImageView iv = new ImageView(activity.getApplicationContext());
        ImageView iv = relativeLayout.findViewById(R.id.imageViewPreview);

        Evidencia evidenciaTemp = new Evidencia(mEvidencia);

        if (usuario.getIdrol() == 1) {
            //if ((evidenciaTemp.getIdEtapa() == 2 || evidenciaTemp.getIdEtapa() == 3) && evidenciaTemp.getIdEstatus() == 1) {
            if (evidenciaTemp.getIdEtapa() != 1 && evidenciaTemp.getIdEstatus() == 1) {
                relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.verde_chk));
            } else if ((evidenciaTemp.getIdEtapa() == 1 || evidenciaTemp.getIdEtapa() == 2) && evidenciaTemp.getIdEstatus() == 3) {
                relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.rojo_chk));
            } else {
                relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.gris));
            }
            //}else if(usuario.getIdrol() == 2) {
        } else {
            //if (evidenciaTemp.getIdEtapa() == 3 && evidenciaTemp.getIdEstatus() == 1) {
            if (evidenciaTemp.getIdEtapa() > usuario.getIdrol() && evidenciaTemp.getIdEstatus() == 1) {
                relativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.verde_chk));
            } else if ((evidenciaTemp.getIdEtapa() == 1 || evidenciaTemp.getIdEtapa() == 2) && evidenciaTemp.getIdEstatus() == 3) {
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
        //iv.setId(id);
        //iv.setContentDescription("" + numPregunta);
        iv.setTag(mEvidencia);
        //iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.setMarginStart(5);
        //iv.setLayoutParams(lp);
        relativeLayout.setLayoutParams(lp);
        iv.setOnClickListener(mOnClickListener);
        return relativeLayout;
    }

    public boolean validaEvidencias(List<Evidencia> listEvidencias) {
        for (Evidencia ev : listEvidencias) {
            if (ev.getIdEtapa() == 1 || ev.getIdEstatus() != 1) {
                return false;
            }
        }
        return true;
    }
}
