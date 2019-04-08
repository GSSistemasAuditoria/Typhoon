package com.elektra.typhoon.notificaciones;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.elektra.typhoon.R;
import com.elektra.typhoon.carteraFolios.CarteraFolios;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 26/03/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class Notifications {

    public static void CustomNotification(Context context) {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.notification_layout);

        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(context, CarteraFolios.class);
        // Send data to NotificationView Class
        //intent.putExtra("title", strtitle);
        //intent.putExtra("text", strtext);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);//*/

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                // Set Icon
                .setSmallIcon(R.drawable.ic_action_warning)
                // Set Ticker Message
                .setTicker("Aviso de Typhoon")
                // Dismiss Notification
                .setAutoCancel(true)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Set RemoteViews into Notification
                .setVibrate(new long[] { 500, 500, 500, 500, 500 })
                .setContent(remoteViews);

        // Locate and set the Image into customnotificationtext.xml ImageViews
        //remoteViews.setImageViewResource(R.id.imagenotileft,R.drawable.ic_launcher_foreground);
        //remoteViews.setImageViewResource(R.id.imagenotiright,R.drawable.ic_launcher_foreground);

        // Locate and set the Text into customnotificationtext.xml TextViews
        remoteViews.setTextViewText(R.id.title,"Typhoon");
        remoteViews.setTextViewText(R.id.text,"Tiene n evidencias pendientes de validar");

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());

    }//*/
}
