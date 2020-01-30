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
import android.widget.PopupWindow;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.checklist.ChecklistBarcos;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.CatalogosDBMethods;
import com.elektra.typhoon.database.ChecklistDBMethods;
import com.elektra.typhoon.database.EvidenciasDBMethods;
import com.elektra.typhoon.database.UsuarioDBMethods;
import com.elektra.typhoon.encryption.Encryption;
import com.elektra.typhoon.objetos.response.CatalogoBarco;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.objetos.response.RolUsuario;
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
    private ChecklistBarcos activity;
    private int rubroPosition;
    private String fechaFolio;
    private CatalogoBarco mBarco;
    private AdapterRecycleViewPreguntas adapterRecycleViewPreguntas;
    private View  viewPreguntas;
    private RecyclerView rvPreguntas;

    public List<RubroData> getListRubros() {
        return listRubros;
    }

    public AdapterRecycleViewPreguntas getAdapterPreguntas(){
        return adapterRecycleViewPreguntas;
    }

    public int getRubroPosition() {
        return rubroPosition;
    }

    public AdapterExpandableChecklist(List<RubroData> listRubros, ChecklistBarcos activity, String fechaFolio, CatalogoBarco mBarco) {
        this.listRubros = listRubros;
        this.activity = activity;
        this.fechaFolio = fechaFolio;
        this.mBarco = mBarco;
        adapterRecycleViewPreguntas = new AdapterRecycleViewPreguntas(new RubroData(), activity,
                AdapterExpandableChecklist.this, fechaFolio, mBarco);
        LayoutInflater layoutInflater = (LayoutInflater) activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewPreguntas = layoutInflater.inflate(R.layout.child_layout, null);
        rvPreguntas = viewPreguntas.findViewById(R.id.recyclerViewPreguntas);
        rvPreguntas.setAdapter(adapterRecycleViewPreguntas);
        rvPreguntas.setLayoutManager(new LinearLayoutManager(activity));
        rvPreguntas.setNestedScrollingEnabled(false);

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
        imageViewSelect.setOnClickListener(mOnClickListener);
        imageViewSelect.setImageDrawable(activity.getResources().getDrawable(
                rubro.isSeleccionado() ? R.mipmap.ic_check_white : R.mipmap.ic_uncheck_white));

        return view;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = 0;
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
                default:
                    Utils.message(activity, "Opción inválida");
                    break;
            }
        }
    };

    @Override
    public View getChildView(final int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        adapterRecycleViewPreguntas.setmRubro(listRubros.get(i));
        rubroPosition = i;
        adapterRecycleViewPreguntas.notifyDataSetChanged();
        return viewPreguntas;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    public void contarPreguntasCumplen() {
        activity.actualizarValores();
    }
}
