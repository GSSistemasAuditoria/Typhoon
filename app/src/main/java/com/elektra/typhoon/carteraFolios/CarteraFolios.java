package com.elektra.typhoon.carteraFolios;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;

import com.elektra.typhoon.R;
import com.elektra.typhoon.adapters.AdapterRecyclerViewCartera;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.service.ApiInterface;
import com.elektra.typhoon.objetos.request.CarteraData;
import com.elektra.typhoon.objetos.request.RequestCartera;
import com.elektra.typhoon.objetos.response.ResponseCartera;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.elektra.typhoon.utils.Utils;

/**
 * Proyecto: TYPHOON
 * Autor: Francis Susana Carreto Espinoza
 * Fecha: 10/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class CarteraFolios extends AppCompatActivity {

    private RecyclerView recyclerView;
    //private ArrayList<Folio> folios;
    private AdapterRecyclerViewCartera adapterRecyclerViewCartera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartera_folios);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewFolios);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CarteraFolios.this);
        recyclerView.setLayoutManager(layoutManager);

        obtenerFolios(-1,-1,-1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void obtenerFolios(int idRevision,int anio,int mes){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Descargando folios");
        progressDialog.show();

        ApiInterface mApiService = Utils.getInterfaceService();

        RequestCartera requestCartera = new RequestCartera();
        CarteraData carteraData = new CarteraData();
        carteraData.setIdRevision(idRevision);
        carteraData.setAnio(anio);
        carteraData.setMes(mes);
        requestCartera.setCarteraData(carteraData);

        Call<ResponseCartera> mService = mApiService.carteraRevisiones(requestCartera);
        mService.enqueue(new Callback<ResponseCartera>() {

            @Override
            public void onResponse(Call<ResponseCartera> call, Response<ResponseCartera> response) {
                progressDialog.dismiss();
                if(response.body() != null) {
                    if(response.body().getCarteraRevisiones().getExito()){
                        adapterRecyclerViewCartera = new AdapterRecyclerViewCartera(CarteraFolios.this,CarteraFolios.this,response.body().getCarteraRevisiones().getFolioRevision());
                        recyclerView.setAdapter(adapterRecyclerViewCartera);
                    }else{
                        Utils.message(getApplicationContext(), response.body().getCarteraRevisiones().getError());
                    }
                }else{
                    Utils.message(getApplicationContext(),"Error al descargar folios");
                }
            }

            @Override
            public void onFailure(Call<ResponseCartera> call, Throwable t) {
                progressDialog.dismiss();
                Utils.message(CarteraFolios.this, Constants.MSG_ERR_CONN);
            }
        });
    }
}
