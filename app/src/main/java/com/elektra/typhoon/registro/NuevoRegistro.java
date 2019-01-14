package com.elektra.typhoon.registro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.elektra.typhoon.R;
import com.elektra.typhoon.carteraFolios.CarteraFolios;

/**
 * Proyecto: TYPHOON
 * Autor: Francis Susana Carreto Espinoza
 * Fecha: 09/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */

public class NuevoRegistro extends AppCompatActivity {

    Button registrar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        registrar = (Button)findViewById(R.id.buttonRegistrarse);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NuevoRegistro.this, CarteraFolios.class);
                startActivity(intent);
            }
        });
    }
}
