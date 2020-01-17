package com.elektra.typhoon.anexos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.adapters.AdapterExpandableAnexos;
import com.elektra.typhoon.checklist.ChecklistBarcos;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.AnexosDBMethods;
import com.elektra.typhoon.database.CatalogosDBMethods;
import com.elektra.typhoon.database.EvidenciasDBMethods;
import com.elektra.typhoon.database.UsuarioDBMethods;
import com.elektra.typhoon.encryption.Encryption;
import com.elektra.typhoon.objetos.response.Anexo;
import com.elektra.typhoon.objetos.response.CatalogoBarco;
import com.elektra.typhoon.objetos.response.EstatusRevision;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.RubroData;
import com.elektra.typhoon.service.SincronizacionIndividualRequestService;
import com.elektra.typhoon.service.SincronizacionRequestService;
import com.elektra.typhoon.utils.Utils;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 19/03/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class AnexosActivity extends AppCompatActivity {

    private int folio;
    private String fechaInicio;
    private int estatus;
    private ExpandableListView expandableListView;
    private AdapterExpandableAnexos adapterExpandableAnexos;
    private TextView textViewCumplen;
    private TextView textViewNoCumplen;
    private TextView tvPorCargarValor;
    private TextView tvPorValidarValor;
    private TextView textViewTotal;
    private Button buttonSincronizarAnexos;
    private List<Anexo> listAnexos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anexos_layout);

        Encryption encryption = new Encryption();

        folio = Integer.parseInt(encryption.decryptAES(getIntent().getStringExtra(Constants.INTENT_FOLIO_TAG)));
        fechaInicio = encryption.decryptAES(Normalizer.normalize(getIntent().getStringExtra(Constants.INTENT_FECHA_INICIO_TAG), Normalizer.Form.NFD));
        estatus = Integer.parseInt(encryption.decryptAES(getIntent().getStringExtra(Constants.INTENT_ESTATUS_TAG)));

        TextView textViewNombreUsuario = (TextView) findViewById(R.id.textViewNombreUsuario);
        TextView textViewRol = findViewById(R.id.textViewRol);

        textViewCumplen = findViewById(R.id.textViewCumplenValor);
        textViewNoCumplen = findViewById(R.id.textViewNoCumplenValor);
        tvPorCargarValor = findViewById(R.id.tvPorCargarValor);
        tvPorValidarValor = findViewById(R.id.tvPorValidarValor);
        textViewTotal = findViewById(R.id.textViewValorTotal);

        TextView textViewFolio = findViewById(R.id.textViewFolio);
        TextView textViewFechaInicio = findViewById(R.id.textViewFechaInicio);
        TextView textViewFechaFin = findViewById(R.id.textViewFechaFin);

        buttonSincronizarAnexos = findViewById(R.id.buttonSincronizarAnexos);

        CatalogosDBMethods catalogosDBMethods = new CatalogosDBMethods(this);
        AnexosDBMethods anexosDBMethods = new AnexosDBMethods(this);

        List<EstatusRevision> listEstatusRevision = catalogosDBMethods.readEstatusRevision(
                "SELECT ID_ESTATUS,DESCRIPCION,SRC FROM " + catalogosDBMethods.TP_CAT_ESTATUS_REVISION + " WHERE ID_ESTATUS = ?", new String[]{String.valueOf(estatus)});

        textViewFolio.setText("" + folio);
        textViewFechaInicio.setText(Utils.getDateMonth(fechaInicio));
        if (listEstatusRevision.size() != 0) {
            textViewFechaFin.setText(listEstatusRevision.get(0).getDescripcion());
        }

        expandableListView = findViewById(R.id.expandableListViewAnexos);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            private int previousItem = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousItem) {
                    expandableListView.collapseGroup(previousItem);
                }
                previousItem = groupPosition;
            }
        });

        UsuarioDBMethods usuarioDBMethods = new UsuarioDBMethods(this);
        ResponseLogin.Usuario usuario = usuarioDBMethods.readUsuario();
        if (usuario != null) {
            textViewNombreUsuario.setText(usuario.getNombre());
            textViewRol.setText(Utils.getRol(this, usuario.getIdrol()));
        }

        List<Integer> listRelaciones = anexosDBMethods.readRelacionRevisionAnexo(folio);

        listAnexos = new ArrayList<>();

        for (int idAnexo : listRelaciones) {
            List<Anexo> tempListAnexos = anexosDBMethods.readCatalogoAnexos("SELECT ID_ANEXO,ID_SUBANEXO,DESCRIPCION FROM " + anexosDBMethods.TP_CAT_ANEXOS + " WHERE " +
                    "ID_SUBANEXO = 0 AND ID_ANEXO = ?", new String[]{String.valueOf(idAnexo)});
            for (Anexo anexo : tempListAnexos) {
                listAnexos.add(anexo);
            }
        }

        for (Anexo anexo : listAnexos) {
            List<Anexo> listSubAnexos = anexosDBMethods.readCatalogoAnexos("SELECT ID_ANEXO,ID_SUBANEXO,DESCRIPCION FROM " + anexosDBMethods.TP_CAT_ANEXOS + " WHERE " +
                    "ID_SUBANEXO != 0 AND ID_ANEXO = ?", new String[]{String.valueOf(anexo.getIdAnexo())});
            anexo.setListSubAnexos(listSubAnexos);
            for (Anexo subanexo : listSubAnexos) {
                //subanexo.setSeleccionado(true);
                /*List<Anexo> listDatosAnexos = anexosDBMethods.readAnexos("SELECT ID_REVISION,ID_ANEXO,ID_SUBANEXO,ID_DOCUMENTO,ID_ETAPA,DOCUMENTO,NOMBRE " +
                                "FROM " + anexosDBMethods.TP_TRAN_ANEXOS + " WHERE ID_REVISION = ? AND ID_ANEXO = ? AND ID_SUBANEXO = ?"
                        , new String[]{String.valueOf(folio), String.valueOf(subanexo.getIdAnexo()), String.valueOf(subanexo.getIdSubAnexo())});//*/

                List<Anexo> listDatosAnexos = anexosDBMethods.readAnexosSinDocumento("SELECT ID_REVISION,ID_ANEXO,ID_SUBANEXO,ID_DOCUMENTO,ID_ETAPA,NOMBRE,SUBANEXO_FCH_SINC,SELECCIONADO,SUBANEXO_FCH_MOD,ID_ROL,ID_USUARIO " +
                                "FROM " + anexosDBMethods.TP_TRAN_ANEXOS + " WHERE ID_REVISION = ? AND ID_SUBANEXO = ?"
                        , new String[]{String.valueOf(folio), String.valueOf(subanexo.getIdSubAnexo())});

                if (listDatosAnexos.size() != 0) {
                    //subanexo.setIdDocumento(listDatosAnexos.get(0).getIdDocumento());
                    subanexo.setNombreArchivo(listDatosAnexos.get(0).getNombreArchivo());
                    subanexo.setIdEtapa(listDatosAnexos.get(0).getIdEtapa());
                    subanexo.setIdRevision(folio);
                    subanexo.setSeleccionado(listDatosAnexos.get(0).isSeleccionado());
                    subanexo.setFechaSinc(listDatosAnexos.get(0).getFechaSinc());
                    subanexo.setFechaMod(listDatosAnexos.get(0).getFechaMod());
                    subanexo.setIdRol(listDatosAnexos.get(0).getIdRol());
                    subanexo.setIdUsuario(listDatosAnexos.get(0).getIdUsuario());
                }
            }
        }

        List<Anexo> listHeader = new ArrayList<>();
        List<Anexo> listInspeccion = new ArrayList<>();
        List<Anexo> listBitacoras = new ArrayList<>();

        listInspeccion.add(new Anexo("Inspección 1"));
        listInspeccion.add(new Anexo("Inspección 2"));
        listInspeccion.add(new Anexo("Inspección 3"));
        listInspeccion.add(new Anexo("Inspección 4"));
        listInspeccion.add(new Anexo("Inspección 5"));

        listBitacoras.add(new Anexo("Bitácora 1"));
        listBitacoras.add(new Anexo("Bitácora 2"));
        listBitacoras.add(new Anexo("Bitácora 3"));
        listBitacoras.add(new Anexo("Bitácora 4"));

        listHeader.add(new Anexo("Inspecciónes", listInspeccion));
        listHeader.add(new Anexo("Bitácoras", listBitacoras));

        contarAnexosValidados(listAnexos);

        adapterExpandableAnexos = new AdapterExpandableAnexos(listAnexos, this, textViewCumplen, textViewNoCumplen, textViewTotal, fechaInicio, estatus);
        expandableListView.setAdapter(adapterExpandableAnexos);

        buttonSincronizarAnexos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new SincronizacionIndividualRequestService(AnexosActivity.this,AnexosActivity.this,folio,null,listAnexos).execute();
                sincronizacionDialog(AnexosActivity.this, folio);
            }
        });

        /*if(listAnexos != null){
            if(listAnexos.size() != 0){
                buttonSincronizarAnexos.setVisibility(View.VISIBLE);
            }else{
                buttonSincronizarAnexos.setVisibility(View.GONE);
            }
        }else{
            buttonSincronizarAnexos.setVisibility(View.GONE);
        }//*/
    }

    public void contarAnexosValidados(List<Anexo> listAnexosHeader) {
        /*ResponseLogin.Usuario usuario = new UsuarioDBMethods(this).readUsuario();
        int aplica = 0;
        int noAplica = 0;
        int total = 0;
        for(Anexo anexo:listAnexosHeader){
            for(Anexo subanexo:anexo.getListSubAnexos()){
                if(subanexo.getIdEtapa() == -1){
                    noAplica++;
                }else{
                    if(subanexo.getIdEtapa() >= usuario.getIdrol()){
                        aplica++;
                    }
                }
                total++;
            }
        }

        //noAplica = total - aplica;

        textViewCumplen.setText(String.valueOf(aplica));
        textViewNoCumplen.setText(String.valueOf(noAplica));
        textViewTotal.setText(String.valueOf(total));*/
        ResponseLogin.Usuario usuario = new UsuarioDBMethods(getApplicationContext()).readUsuario();
        int cumple = 0;
        int noCumple = 0;
        int porValidar = 0;
        int porCargar = 0;
        int total = 0;

        for (Anexo mAnexo : listAnexosHeader)
            for (Anexo subAnexo : mAnexo.getListSubAnexos()) {
                total++;
                if (subAnexo.getNombreArchivo() == null || subAnexo.getNombreArchivo().isEmpty()) {
                    porCargar++;
                } else {
                    if ((usuario.getIdrol() == 1 && subAnexo.getIdEtapa() >= 1) ||
                            (usuario.getIdrol() == 2 && subAnexo.getIdEtapa() >= 2) ||
                            (usuario.getIdrol() == 3 && (subAnexo.getIdEtapa() >= 3 || subAnexo.getIdEtapa() == 1)) ||
                            (usuario.getIdrol() == 4 && (subAnexo.getIdEtapa() == 1 || subAnexo.getIdEtapa() >= 4 || subAnexo.getIdEtapa() == 2))) {
                        cumple++;
                    } else if (subAnexo.getIdEtapa() == -1) {
                        noCumple++;
                    } else {
                        porValidar++;
                    }
                }
            }


        textViewCumplen.setText(String.valueOf(cumple));
        textViewNoCumplen.setText(String.valueOf(noCumple));
        tvPorCargarValor.setText(String.valueOf(porCargar));
        tvPorValidarValor.setText(String.valueOf(porValidar));
        textViewTotal.setText(String.valueOf(total));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            new GuardandoAnexosTask(AnexosActivity.this, data, requestCode).execute();
        }
    }

    class GuardandoAnexosTask extends AsyncTask<String, String, String> {

        private ProgressDialog statusDialog;
        private Context context;
        private Intent data;
        private int requestCode;

        public GuardandoAnexosTask(Context context, Intent data, int requestCode) {
            this.context = context;
            this.data = data;
            this.requestCode = requestCode;
        }

        protected void onPreExecute() {
            statusDialog = Utils.typhoonLoader(context, "Guardando anexo...");
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "OK";
        }

        @Override
        protected void onPostExecute(String result) {
            String respuesta = guardarEvidencia();
            statusDialog.dismiss();
            //if(!result.equals("OK")){
            if (!respuesta.equals("OK")) {
                //Utils.message(context,result);
                Utils.message(context, respuesta);
            } else {
                //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                Utils.message(context, "Evidencia guardada");
                contarAnexosValidados(listAnexos);
            }
        }

        private String guardarEvidencia() {
            boolean flagCrea = true;
            if (data != null) {
                Uri uri = data.getData();
                String nombre = Utils.getRealPathFromURI(AnexosActivity.this, uri);
                int idrubro = adapterExpandableAnexos.getAdapterRecycleViewItemsAnexosTemp().getHeader();
                if (nombre.endsWith(".pdf") || nombre.endsWith(".PDF")) {
                    try {
                        String base64 = Utils.fileToBase64(AnexosActivity.this, uri);
                        Anexo anexo = adapterExpandableAnexos.getListAnexosHeader().get(idrubro).getListSubAnexos().get(requestCode);
                        if (anexo.getNombreArchivo() != null) {
                            flagCrea = false;
                        }
                        anexo.setBase64(base64);
                        anexo.setNombreArchivo(nombre);
                        /*if(anexo.getIdDocumento() == null) {
                            anexo.setIdDocumento(UUID.randomUUID().toString());
                        }else{
                            if(anexo.getIdDocumento().equals("")) {
                                anexo.setIdDocumento(UUID.randomUUID().toString());
                            }
                        }//*/
                        ResponseLogin.Usuario usuario = new UsuarioDBMethods(getApplicationContext()).readUsuario();
                        anexo.setIdRevision(folio);
                        if (usuario != null) {
                            anexo.setIdEtapa(usuario.getIdrol() - 1);
                        } else {
                            anexo.setIdEtapa(0);
                        }
                        anexo.setIdRol(usuario.getIdrol());
                        anexo.setIdUsuario(usuario.getIdUsuario());
                        System.out.println();
                        if (flagCrea) {
                            new AnexosDBMethods(getApplicationContext()).createAnexo(anexo);
                        } else {
                            anexo.setFechaSinc(null);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("ID_ETAPA", anexo.getIdEtapa());
                            contentValues.put("DOCUMENTO", anexo.getBase64());
                            contentValues.put("NOMBRE", anexo.getNombreArchivo());
                            String valueNull = null;
                            contentValues.put("SUBANEXO_FCH_SINC", valueNull);
                            new AnexosDBMethods(getApplicationContext()).updateAnexo(contentValues,
                                    "ID_REVISION = ? AND ID_SUBANEXO = ?", new String[]{String.valueOf(anexo.getIdRevision()), String.valueOf(anexo.getIdSubAnexo())});
                        }
                        anexo.setBase64(null);
                        adapterExpandableAnexos.getAdapterRecycleViewItemsAnexosTemp().notifyDataSetChanged();
                        /*RespuestaData datosRespuesta = null;
                        for (RespuestaData respuestaData : adapterExpandableChecklist.getListRubros().get(idrubro).getListRespuestas()) {
                            if (respuestaData.getIdPregunta() == adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getIdPregunta()) {
                                datosRespuesta = respuestaData;
                                break;
                            }
                        }

                        Evidencia evidencia = new Evidencia();

                        if (datosRespuesta != null) {
                            evidencia.setIdRegistro(datosRespuesta.getIdRegistro());
                            evidencia.setIdPregunta(datosRespuesta.getIdPregunta());
                            evidencia.setIdRubro(datosRespuesta.getIdRubro());
                            evidencia.setIdChecklist(datosRespuesta.getIdChecklist());
                            evidencia.setIdRevision(datosRespuesta.getIdRevision());
                            evidencia.setIdEstatus(1);
                            //evidencia.setIdEtapa(1);
                            evidencia.setIdEtapa(usuario.getIdrol());
                            if(usuario.getIdrol() == 3){
                                evidencia.setAgregadoCoordinador(1);
                            }
                            evidencia.setContenido(base64);
                            //evidencia.setContenidoPreview(base64Preview);
                            //evidencia.setNombre(Utils.getDate("yyyyMMddHHmmss") + ".png");
                            evidencia.setNombre(path);
                            evidencia.setIdEvidencia(UUID.randomUUID().toString());
                            CatalogoBarco barco = (CatalogoBarco) spinnerBarco.getSelectedItem();
                            evidencia.setIdBarco(barco.getIdBarco());

                            GPSTracker gps = new GPSTracker(ChecklistBarcos.this,2);
                            if(gps.canGetLocation()) {
                                double latitude = gps.getLatitude();
                                double longitude = gps.getLongitude();
                                evidencia.setLatitude(latitude);
                                evidencia.setLongitude(longitude);
                                gps.stopUsingGPS();
                            }

                            EvidenciasDBMethods evidenciasDBMethods = new EvidenciasDBMethods(getApplicationContext());
                            evidenciasDBMethods.createEvidencia(evidencia);
                            updateRespuesta(evidencia,3);

                            evidencia.setContenido(null);

                            if (adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).getListEvidencias() != null) {

                                //evidencia.setSmallBitmap(scaledBitmap);
                                adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).
                                        getListEvidencias().add(evidencia);
                                //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                            } else {
                                List<Evidencia> listEvidencias = new ArrayList<>();
                                //listEvidencias.add(new Evidencia(scaledBitmap,bitmap,1));
                                //evidencia.setSmallBitmap(scaledBitmap);
                                listEvidencias.add(evidencia);
                                adapterExpandableChecklist.getListRubros().get(idrubro).getListPreguntasTemp().get(requestCode).setListEvidencias(listEvidencias);
                                //adapterExpandableChecklist.getAdapterRecycleViewPreguntasTemp().notifyDataSetChanged();
                            }
                        }//*/
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "No se pudo guardar la imagen: " + e.getMessage();
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                        return "No se pudo guardar la imagen: " + e.getMessage();
                    }
                }
            }
            return "OK";
        }
    }

    private void sincronizacionDialog(final Activity activity, final int idRevision) {
        LayoutInflater li = LayoutInflater.from(activity);
        LinearLayout layoutDialog = (LinearLayout) li.inflate(R.layout.dialog_sincronizacion_layout, null);

        TextView textViewCancelar = (TextView) layoutDialog.findViewById(R.id.buttonCancelar);
        TextView textViewSincronizar = (TextView) layoutDialog.findViewById(R.id.buttonSincronizar);
        TextView textViewTituloDialogoSincronizacion = layoutDialog.findViewById(R.id.textViewTituloDialogoSincronizacion);
        LinearLayout linearLayoutCancelar = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutCancelar);
        LinearLayout linearLayoutSincronizar = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutSincronizar);

        //textViewTituloDialogoSincronizacion.setText("¿Deseas sincronizar tus anexos?");

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
                new SincronizacionRequestService(activity, activity, idRevision).execute();
                //new SincronizacionIndividualRequestService(activity,activity,idRevision,listCatalogoBarcos,null).execute();
                //new SincronizacionIndividualRequestService(AnexosActivity.this,AnexosActivity.this,folio,null,listAnexos,null,AnexosActivity.this).execute();
                dialog.dismiss();
            }
        });

        linearLayoutSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SincronizacionRequestService(activity, activity, idRevision).execute();
                //new SincronizacionIndividualRequestService(AnexosActivity.this,AnexosActivity.this,folio,null,listAnexos,null,AnexosActivity.this).execute();
                dialog.dismiss();
            }
        });
    }

    public void loadData() {
        AnexosDBMethods anexosDBMethods = new AnexosDBMethods(this);
        List<Integer> listRelaciones = anexosDBMethods.readRelacionRevisionAnexo(folio);

        listAnexos = new ArrayList<>();

        for (int idAnexo : listRelaciones) {
            List<Anexo> tempListAnexos = anexosDBMethods.readCatalogoAnexos("SELECT ID_ANEXO,ID_SUBANEXO,DESCRIPCION FROM " + anexosDBMethods.TP_CAT_ANEXOS + " WHERE " +
                    "ID_SUBANEXO = 0 AND ID_ANEXO = ?", new String[]{String.valueOf(idAnexo)});
            for (Anexo anexo : tempListAnexos) {
                listAnexos.add(anexo);
            }
        }

        for (Anexo anexo : listAnexos) {
            List<Anexo> listSubAnexos = anexosDBMethods.readCatalogoAnexos("SELECT ID_ANEXO,ID_SUBANEXO,DESCRIPCION FROM " + anexosDBMethods.TP_CAT_ANEXOS + " WHERE " +
                    "ID_SUBANEXO != 0 AND ID_ANEXO = ?", new String[]{String.valueOf(anexo.getIdAnexo())});
            anexo.setListSubAnexos(listSubAnexos);
            for (Anexo subanexo : listSubAnexos) {
                /*List<Anexo> listDatosAnexos = anexosDBMethods.readAnexos("SELECT ID_REVISION,ID_ANEXO,ID_SUBANEXO,ID_DOCUMENTO,ID_ETAPA,DOCUMENTO,NOMBRE " +
                                "FROM " + anexosDBMethods.TP_TRAN_ANEXOS + " WHERE ID_REVISION = ? AND ID_ANEXO = ? AND ID_SUBANEXO = ?"
                        , new String[]{String.valueOf(folio), String.valueOf(subanexo.getIdAnexo()), String.valueOf(subanexo.getIdSubAnexo())});//*/

                List<Anexo> listDatosAnexos = anexosDBMethods.readAnexosSinDocumento("SELECT ID_REVISION,ID_ANEXO,ID_SUBANEXO,ID_DOCUMENTO,ID_ETAPA,NOMBRE,SUBANEXO_FCH_SINC,SELECCIONADO,SUBANEXO_FCH_MOD,ID_ROL,ID_USUARIO " +
                                "FROM " + anexosDBMethods.TP_TRAN_ANEXOS + " WHERE ID_REVISION = ? AND ID_SUBANEXO = ?"
                        , new String[]{String.valueOf(folio), String.valueOf(subanexo.getIdSubAnexo())});

                if (listDatosAnexos.size() != 0) {
                    //subanexo.setIdDocumento(listDatosAnexos.get(0).getIdDocumento());
                    subanexo.setNombreArchivo(listDatosAnexos.get(0).getNombreArchivo());
                    subanexo.setIdEtapa(listDatosAnexos.get(0).getIdEtapa());
                    subanexo.setIdRevision(folio);
                    subanexo.setSeleccionado(listDatosAnexos.get(0).isSeleccionado());
                    subanexo.setFechaSinc(listDatosAnexos.get(0).getFechaSinc());
                    subanexo.setFechaMod(listDatosAnexos.get(0).getFechaMod());
                    subanexo.setIdRol(listDatosAnexos.get(0).getIdRol());
                    subanexo.setIdUsuario(listDatosAnexos.get(0).getIdUsuario());
                }
            }
        }

        contarAnexosValidados(listAnexos);

        adapterExpandableAnexos = new AdapterExpandableAnexos(listAnexos, this, textViewCumplen, textViewNoCumplen, textViewTotal, fechaInicio, estatus);
        expandableListView.setAdapter(adapterExpandableAnexos);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
        if (sharedPreferences.contains(Constants.SP_LOGIN_TAG)) {
            if (!sharedPreferences.getBoolean(Constants.SP_LOGIN_TAG, false)) {
                finish();
            }
        }
    }
}
