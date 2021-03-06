package com.elektra.typhoon.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.checklist.ChecklistBarcos;
import com.elektra.typhoon.database.ChecklistDBMethods;
import com.elektra.typhoon.database.EvidenciasDBMethods;
import com.elektra.typhoon.database.UsuarioDBMethods;
import com.elektra.typhoon.objetos.response.CatalogoBarco;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.objetos.response.RubroData;
import com.elektra.typhoon.service.AsyncTaskGral;
import com.elektra.typhoon.service.Delegate;
import com.elektra.typhoon.utils.Utils;

import java.io.IOException;
import java.util.List;

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
    private int rubroPosition;
    private AdapterRecycleViewPreguntas adapterRecycleViewPreguntasTemp;
    private TextView textViewCumplen;
    private TextView textViewNoCumplen;
    private TextView tvPorValidarValor;
    private TextView tvPorCargarValor;
    private String fechaFolio;
    private int idBarco;
    private ProgressDialog progressDialog;

    public AdapterRecycleViewPreguntas getAdapterRecycleViewPreguntasTemp() {
        return adapterRecycleViewPreguntasTemp;
    }

    public List<RubroData> getListRubros() {
        return listRubros;
    }

    public int getRubroPosition() {
        return rubroPosition;
    }

    public AdapterExpandableChecklist(List<RubroData> listRubros, Activity activity, TextView textViewCumplen, TextView textViewNoCumplen,
                                      TextView tvPorCargarValor, TextView tvPorValidarValor, String fechaFolio, int idBarco) {
        this.listRubros = listRubros;
        this.activity = activity;
        this.textViewCumplen = textViewCumplen;
        this.textViewNoCumplen = textViewNoCumplen;
        this.tvPorCargarValor = tvPorCargarValor;
        this.tvPorValidarValor = tvPorValidarValor;
        this.fechaFolio = fechaFolio;
        this.idBarco = idBarco;
    }

    @Override
    public int getGroupCount() {
        return listRubros.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return listRubros.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listRubros.get(i).getListPreguntas().get(i1);
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
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        final RubroData rubro = listRubros.get(i);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.header_layout, null);
        }

        TextView textViewTituloEncabezado = view.findViewById(R.id.textViewTituloHeader);
        ImageView imageView = view.findViewById(R.id.imageViewIconoGrupo);
        ImageView imageViewSelect = view.findViewById(R.id.imageViewSelect);
        textViewTituloEncabezado.setText(rubro.getNombre());

        rubroPosition = i;

        if (b) {
            imageView.setImageResource(R.mipmap.ic_group_close);
        } else {
            imageView.setImageResource(R.mipmap.ic_group_open);
        }

        imageViewSelect.setEnabled(false);

        imageViewSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        if (rubro.isSeleccionado()) {
            imageViewSelect.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_check_white));
        } else {
            imageViewSelect.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_uncheck_white));
        }

        /*if(b) {
            progressDialog = Utils.typhoonLoader(activity, "Cargando datos...");
        }//*/

        return view;
    }

    private boolean cuentaPreguntasDeshabilitadas(List<Pregunta> preguntas) {
        for (Pregunta pregunta : preguntas) {
            if (pregunta.isSeleccionado()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public View getChildView(final int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final RubroData rubro = listRubros.get(i);
        //if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.child_layout, null);
        //}

        //TextView textViewTituloRubro = (TextView) view.findViewById(R.id.textViewTituloRubro);
        TextView textViewCumplenValor = view.findViewById(R.id.textViewCumplenValor);
        TextView textViewNoCumpleValor = view.findViewById(R.id.textViewNoCumplenValor);
        TextView textViewValorTotal = view.findViewById(R.id.textViewValorTotal);
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
                        List<Evidencia> listEvidencias = evidenciasDBMethods.readEvidenciasWithOutContenido("SELECT ID_EVIDENCIA,NOMBRE,CONTENIDO_PREVIEW,ID_ESTATUS,ID_ETAPA,ID_REVISION,ID_CHECKLIST," +
                                        "ID_RUBRO,ID_PREGUNTA,ID_REGISTRO,ID_BARCO,LATITUDE,LONGITUDE,AGREGADO_COORDINADOR,NUEVO,FECHA_MOD," +
                                        "LOCATION,ID_ROL,ID_USUARIO,AGREGADO_LIDER FROM " + evidenciasDBMethods.TP_TRAN_CL_EVIDENCIA +
                                        " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_BARCO = ?" +
                                        " AND ID_ESTATUS != 2",
                                new String[]{String.valueOf(pregunta.getIdRevision()), String.valueOf(pregunta.getIdChecklist()),
                                        String.valueOf(pregunta.getIdRubro()), String.valueOf(pregunta.getIdPregunta()),
                                        String.valueOf(idBarco)});
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

    public void contarPreguntasCumplen(int idBarco) {
        /*int cumple = 0;
        int noCumple = 0;
        int numeroPreguntas = 0;//*/
        int folio = 0;
        int checklist = 0;
        for (RubroData rubro : listRubros) {
            folio = rubro.getIdRevision();
            checklist = rubro.getIdChecklist();
            /*for(Pregunta pregunta:rubro.getListPreguntasTemp()){
                if (Utils.aplicaPregunta(activity,pregunta.getListEvidencias())) {
                    cumple++;
                } else {
                    noCumple++;
                }
                numeroPreguntas++;
            }//*/
        }

        /*ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(activity);

        ResponseLogin.Usuario usuario = new UsuarioDBMethods(activity).readUsuario();

        String query = "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_TIPO_RESPUESTA,ID_RUBRO,ESTATUS,DESCRIPCION,IS_TIERRA,SELECCIONADO FROM " +
                checklistDBMethods.TP_CAT_CL_PREGUNTA + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ?";

        List<Pregunta> listPreguntas = checklistDBMethods.readPregunta(query,
                new String[]{String.valueOf(folio),String.valueOf(checklist)});

        List<RespuestaData> listRespuestas = checklistDBMethods.readRespuesta(
                "SELECT ID_REVISION,ID_CHECKLIST,ID_PREGUNTA,ID_RUBRO,ID_ESTATUS,ID_BARCO,ID_REGISTRO,ID_RESPUESTA,SINCRONIZADO FROM " + checklistDBMethods.TP_TRAN_CL_RESPUESTA + " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_BARCO = ?",
                new String[]{String.valueOf(folio),String.valueOf(checklist),String.valueOf(idBarco)});
        int cumple = 0;
        int noCumple = 0;
        for(RespuestaData respuestaData:listRespuestas){
            if(usuario.getIdrol() != 3) {
                if (!Utils.respuestaIsTierra(listPreguntas, respuestaData)) {
                    if (respuestaData.getIdRespuesta() != null) {
                        if (respuestaData.getIdRespuesta() == 2) {
                            cumple++;
                        }
                        if (respuestaData.getIdRespuesta() == 3) {
                            noCumple++;
                        }
                    } else {
                        //noCumple++;
                    }
                }
            }else{
                if (respuestaData.getIdRespuesta() != null) {
                    if (respuestaData.getIdRespuesta() == 2) {
                        cumple++;
                    }
                    if (respuestaData.getIdRespuesta() == 3) {
                        noCumple++;
                    }
                }
            }
        }

        textViewCumplen.setText(String.valueOf(cumple));
        textViewNoCumplen.setText(String.valueOf(noCumple));*/


        int cumple = 0;
        int noCumple = 0;
        int porValidar = 0;
        int porCargar = 0;

        EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(activity);
        ResponseLogin.Usuario usuario = new UsuarioDBMethods(activity).readUsuario();

        for (RubroData mRubroData : listRubros)
            for (Pregunta mPregunta : mRubroData.getListPreguntasTemp()) {
                if ((usuario.getIdrol() == 3 || !mPregunta.isTierra())) {
                    if (mPregunta.getListEvidencias() == null) {
                        List<Evidencia> listEvidencias;
                        listEvidencias = evidenciasDBMethods.readEvidencias("SELECT ID_ESTATUS,ID_ETAPA FROM " + evidenciasDBMethods.TP_TRAN_CL_EVIDENCIA +
                                        " WHERE ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_RUBRO = ? AND ID_PREGUNTA = ? AND ID_BARCO = ?" +
                                        " AND ID_ESTATUS != 2",
                                new String[]{String.valueOf(mPregunta.getIdRevision()), String.valueOf(mPregunta.getIdChecklist()),
                                        String.valueOf(mPregunta.getIdRubro()), String.valueOf(mPregunta.getIdPregunta()),
                                        String.valueOf(idBarco)});
                        mPregunta.setListEvidencias(listEvidencias);
                    }
                    if (mPregunta.getListEvidencias() == null || mPregunta.getListEvidencias().size() == 0) {
                        porCargar++;
                    } else {
                        int valida = 0;
                        int cump = 0;
                        int noCump = 0;
                        for (Evidencia mEvidencia : mPregunta.getListEvidencias()) {
                            if ((usuario.getIdrol() == 1 && mEvidencia.getIdEtapa() != 1 && mEvidencia.getIdEstatus() == 1) ||
                                    (mEvidencia.getIdEtapa() > usuario.getIdrol() && mEvidencia.getIdEstatus() == 1) ||
                                    (mEvidencia.getIdEtapa() == 2 && mEvidencia.getIdEstatus() == 1 && usuario.getIdrol() == 3) ||
                                    ((mEvidencia.getIdEtapa() == 3 || mEvidencia.getIdEtapa() == 2) && (mEvidencia.getIdEstatus() == 2 || mEvidencia.getIdEstatus() == 1) && usuario.getIdrol() == 4)) {
                                cump++;
                            } else if ((usuario.getIdrol() == 1 && (mEvidencia.getIdEtapa() == 1 || mEvidencia.getIdEtapa() == 2) && mEvidencia.getIdEstatus() == 3) ||
                                    ((mEvidencia.getIdEtapa() == 1 || mEvidencia.getIdEtapa() == 2) && mEvidencia.getIdEstatus() == 3)) {
                                noCump++;
                            } else {
                                valida++;
                            }
                        }
                        if (noCump > 0) {
                            noCumple++;
                        } else {
                            if (valida > 0) {
                                porValidar++;
                            } else {
                                if (cump > 0) {
                                    cumple++;
                                }
                            }
                        }
                    }
                    if (mPregunta.getListEvidencias() != null && mPregunta.getListEvidencias().size() > 0 && mPregunta.getListEvidencias().get(0).getIdEvidencia() == null)
                        mPregunta.setListEvidencias(null);
                }
            }


        textViewCumplen.setText(String.valueOf(cumple));
        textViewNoCumplen.setText(String.valueOf(noCumple));
        tvPorCargarValor.setText(String.valueOf(porCargar));
        tvPorValidarValor.setText(String.valueOf(porValidar));



        /*ChecklistDBMethods checklistDBMethods = new ChecklistDBMethods(activity);
        ChecklistData checklist = checklistDBMethods.readChecklist("WHERE ID_REVISION = ?",new String[]{String.valueOf(folio)});
        List<Pregunta> listPreguntas = checklistDBMethods.readPregunta("WHERE ID_REVISION = ? AND ID_CHECKLIST = ?",new String[]{String.valueOf(checklist.getIdRevision())
        ,String.valueOf(checklist.getIdChecklist())});

        int numeroPreguntasVisualizadas = listPreguntas.size();
        int diferenciaPreguntas = numeroPreguntasVisualizadas - numeroPreguntas;

        textViewCumplen.setText(String.valueOf(cumple));
        textViewNoCumplen.setText(String.valueOf(noCumple+diferenciaPreguntas));//*/
    }
}
