package com.elektra.typhoon.notificaciones;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elektra.typhoon.R;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 23/04/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class TyphoonNotificationActivity extends AppCompatActivity {

    private String title;
    private String message;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_notification_layout);

        title = getIntent().getStringExtra("title");
        message = getIntent().getStringExtra("message");

        TextView textViewCancelar = findViewById(R.id.buttonCancelar);
        TextView textViewAceptar = findViewById(R.id.buttonAceptar);
        TextView textViewDialogTitulo = findViewById(R.id.textViewDialogTitulo);
        TextView textViewDialogMessage = findViewById(R.id.textViewDialogMessage);
        LinearLayout linearLayoutCancelar = findViewById(R.id.linearLayoutCancelar);
        LinearLayout linearLayoutAceptar = findViewById(R.id.linearLayoutAceptar);

        textViewDialogTitulo.setText(title);
        textViewDialogMessage.setText(message);

        linearLayoutCancelar.setVisibility(View.GONE);
        textViewCancelar.setVisibility(View.GONE);

        textViewCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        linearLayoutCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        textViewAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        linearLayoutAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
