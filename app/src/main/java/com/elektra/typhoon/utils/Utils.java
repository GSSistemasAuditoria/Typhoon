package com.elektra.typhoon.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.service.ApiInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 14/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class Utils {

    /**
     * Método para crear instancia de retrofit
     * @return
     */
    public static ApiInterface getInterfaceService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_PUBLIC)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        return mInterfaceService;
    }

    /**
     * Método para mostrar texto en pantalla
     * @param context
     * @param text
     */
    public static void message(Context context,String text){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    /**
     * Método para ocultar el teclado
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * Método para validar expresión regular
     * @param cadena
     * @param regex
     * @return
     */
    public static boolean validaExpresion(String cadena,String regex){
        Pattern patron = Pattern.compile(regex);
        return patron.matcher(cadena).matches();
    }

    /**
     * Método para obtener el Uri de un bitmap
     * @param context
     * @param bitmap
     * @return
     */
    public static Uri getUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    public static void openCamera(Activity activity){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Constants.PATH + "tempPhotos/evidencia.jpg");
        boolean isDirectoryCreated = file.getParentFile().mkdirs();
        System.out.println("openCamera: isDirectoryCreated: " + isDirectoryCreated);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Uri tempFileUri = FileProvider.getUriForFile(activity,
                    "com.typhoon.provider",
                    file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri);
        } else {
            Uri tempFileUri = Uri.fromFile(file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri);
        }
        activity.startActivityForResult(cameraIntent, 1);
    }

    public static String getDate(String format) {
        DateFormat df = new SimpleDateFormat(format);
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

    public static Bitmap resizeImageBitmap(Bitmap imageBitmap) {
        //int nh = (int) (imageBitmap.getHeight() * (512.0 / imageBitmap.getWidth()));
        //Bitmap scaled = Bitmap.createScaledBitmap(imageBitmap, 512, nh, true);
        Bitmap scaled = Bitmap.createScaledBitmap(imageBitmap, 512, 512, true);
        return scaled;
    }
}
