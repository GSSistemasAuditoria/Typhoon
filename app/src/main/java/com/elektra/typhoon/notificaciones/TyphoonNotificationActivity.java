package com.elektra.typhoon.notificaciones;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elektra.typhoon.R;
import com.elektra.typhoon.database.NotificacionesDBMethods;
import com.elektra.typhoon.encryption.Encryption;
import com.elektra.typhoon.objetos.response.Notificacion;

import java.util.List;

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

        Encryption encryption = new Encryption();

        if(getIntent().getStringExtra("title") != null){
            title = encryption.decryptAES(getIntent().getStringExtra("title"));
        }
        if(getIntent().getStringExtra("message") != null){
            message = encryption.decryptAES(getIntent().getStringExtra("message"));
        }

        NotificacionesDBMethods notificacionesDBMethods = new NotificacionesDBMethods(this);
        List<Notificacion> listNotificaciones = notificacionesDBMethods.readNotificaciones(1);
        Notificacion notificacion = new Notificacion();
        notificacion.setTitle(title);
        notificacion.setBody(message);
        notificacion.setIdNotificacion(listNotificaciones.size() + 1);
        notificacionesDBMethods.createNotificacion(notificacion);

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
