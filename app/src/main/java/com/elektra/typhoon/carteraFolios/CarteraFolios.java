package com.elektra.typhoon.carteraFolios;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.elektra.typhoon.R;
import com.elektra.typhoon.adapters.AdapterReciclerViewCartera;
import com.elektra.typhoon.objetos.Folio;

import java.util.ArrayList;

/**
 * Proyecto: TYPHOON
 * Autor: Francis Susana Carreto Espinoza
 * Fecha: 10/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class CarteraFolios extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Folio> folios;
    private AdapterReciclerViewCartera adapterReciclerViewCartera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartera_folios);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewFolios);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CarteraFolios.this);
        recyclerView.setLayoutManager(layoutManager);

        folios = new ArrayList<>();
        folios.add(new Folio("2019"));
        folios.add(new Folio("1001","Enero","Set transparent background of an imageview on Android"));
        folios.add(new Folio("1002","Febrero","Set transparent background of an imageview on Android"));
        folios.add(new Folio("1003","Marzo","Set transparent background of an imageview on Android"));
        folios.add(new Folio("1004","Abril","Set transparent background of an imageview on Android"));
        folios.add(new Folio("2018"));
        folios.add(new Folio("0001","Enero","Lorem Ipsum is simply dummy text of the printing and typesetting industry"));
        folios.add(new Folio("0002","Febrero","Lorem Ipsum is simply dummy text of the printing and typesetting industry"));
        folios.add(new Folio("0003","Marzo","Lorem Ipsum is simply dummy text of the printing and typesetting industry"));
        folios.add(new Folio("0004","Abril","Lorem Ipsum is simply dummy text of the printing and typesetting industry"));

        adapterReciclerViewCartera = new AdapterReciclerViewCartera(CarteraFolios.this,folios);
        recyclerView.setAdapter(adapterReciclerViewCartera);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
