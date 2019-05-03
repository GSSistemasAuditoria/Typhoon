package com.elektra.typhoon.firebase;

import android.content.SharedPreferences;

import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.encryption.Encryption;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Proyecto:
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 22/04/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class NotificationIdService extends FirebaseInstanceIdService {
    private static final String TAG = "NotificationIdService";



    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        Encryption encryption = new Encryption();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println("Token dispositivo: " + refreshedToken);
        SharedPreferences sharedPrefs = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
        if (!sharedPrefs.contains(Constants.SP_FIREBASE_TOKEN)) {
            SharedPreferences.Editor ed;
            ed = sharedPrefs.edit();
            ed.putString(Constants.SP_FIREBASE_TOKEN, encryption.encryptAES(refreshedToken));
            ed.commit();
        }
        //Toast.makeText(this,"Refreshed token: " + refreshedToken,Toast.LENGTH_SHORT).show();

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }
}