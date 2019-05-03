package com.elektra.typhoon.firebase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elektra.typhoon.R;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.encryption.Encryption;
import com.elektra.typhoon.notificaciones.TyphoonNotificationActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 22/04/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        //displayMessage("From: " + remoteMessage.getFrom());

        Encryption encryption = new Encryption();

        Context context01 = getApplicationContext();
        Context context02 = getApplication();
        Context context03 = getBaseContext();

        if(remoteMessage.getNotification() != null){
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            if(notification.getBody() != null){
                //displayMessage("Titulo: " + notification.getTitle() + " Mensaje: " + notification.getBody());
                //notificationDialog(NotificationService.this.getBaseContext(),notification.getTitle(),notification.getBody());
                Intent intent = new Intent(NotificationService.this, TyphoonNotificationActivity.class);
                intent.putExtra("title",encryption.encryptAES(notification.getTitle()));
                intent.putExtra("message",encryption.encryptAES(notification.getBody()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        //displayMessage("Notification Message Body: " + remoteMessage.getNotification().getBody());
    }

    @Override
    public void onNewToken(String token) {
        System.out.println("Token dispositivo: " + token);

        Encryption encryption = new Encryption();

        SharedPreferences sharedPrefs = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
        if (!sharedPrefs.contains(Constants.SP_FIREBASE_TOKEN)) {
            SharedPreferences.Editor ed;
            ed = sharedPrefs.edit();
            ed.putString(Constants.SP_FIREBASE_TOKEN, encryption.encryptAES(token));
            ed.commit();
        }

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }

    private void displayMessage(final String text){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(NotificationService.this.getApplicationContext(),text,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void notificationDialog(final Context activity, final String title, final String text){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                LayoutInflater li = LayoutInflater.from(activity);
                LinearLayout layoutDialog = (LinearLayout) li.inflate(R.layout.dialog_notification_layout, null);

                TextView textViewCancelar = (TextView) layoutDialog.findViewById(R.id.buttonCancelar);
                TextView textViewAceptar = (TextView) layoutDialog.findViewById(R.id.buttonAceptar);
                TextView textViewDialogTitulo = layoutDialog.findViewById(R.id.textViewDialogTitulo);
                TextView textViewDialogMessage = layoutDialog.findViewById(R.id.textViewDialogMessage);
                LinearLayout linearLayoutCancelar = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutCancelar);
                LinearLayout linearLayoutAceptar = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutAceptar);

                textViewDialogTitulo.setText(title);
                textViewDialogMessage.setText(text);

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

                textViewAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                linearLayoutAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }
}