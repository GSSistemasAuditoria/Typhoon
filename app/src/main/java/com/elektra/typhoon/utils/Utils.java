package com.elektra.typhoon.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.elektra.typhoon.R;
import com.elektra.typhoon.carteraFolios.CarteraFolios;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.BarcoDBMethods;
import com.elektra.typhoon.database.CatalogosDBMethods;
import com.elektra.typhoon.database.EvidenciasDBMethods;
import com.elektra.typhoon.database.UsuarioDBMethods;
import com.elektra.typhoon.encryption.Encryption;
import com.elektra.typhoon.gps.GPSTracker;
import com.elektra.typhoon.login.MainActivity;
import com.elektra.typhoon.objetos.response.Barco;
import com.elektra.typhoon.objetos.response.CatalogosTyphoonResponse;
import com.elektra.typhoon.objetos.response.Configuracion;
import com.elektra.typhoon.objetos.response.EstatusEvidencia;
import com.elektra.typhoon.objetos.response.EstatusRevision;
import com.elektra.typhoon.objetos.response.EtapaEvidencia;
import com.elektra.typhoon.objetos.response.EtapaSubAnexo;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.LatLng;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.RolUsuario;
import com.elektra.typhoon.objetos.response.TipoRespuesta;
import com.elektra.typhoon.service.ApiInterface;
import com.elektra.typhoon.service.NuevaInstalacion;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.location.Geofence;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_PUBLIC)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        return mInterfaceService;
    }

    /**
     * Método para mostrar texto en pantalla
     * @param context
     * @param text
     */
    /*public static void message(Context context,String text){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }//*/

    public static void message(Context context,String text){
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.toast_layout, null);

        TextView textViewToast = (TextView) view.findViewById(R.id.textViewToast);
        textViewToast.setText(text);

        toast.setView(view);
        toast.show();
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

    public static void openCamera(Activity activity,int requestCode){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //File file = new File(Constants.PATH + "tempPhotos/evidencia.jpg");
        File file = new File(Constants.PATH);
        //Uri capturedImageUri = Uri.fromFile(file);
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
        activity.startActivityForResult(cameraIntent, requestCode);
    }

    public static String getDate(String format) {
        DateFormat df = new SimpleDateFormat(format);
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

    public static Bitmap resizeImageBitmap(Bitmap imageBitmap) {
        //Bitmap scaled = Bitmap.createScaledBitmap(imageBitmap, 512, 512, true);
        //return scaled;
        return Bitmap.createScaledBitmap(imageBitmap, 512, 512, true);
    }

    public static Bitmap resizeImageBitmap(Bitmap imageBitmap,int ancho,int alto) {
        //Bitmap scaled = Bitmap.createScaledBitmap(imageBitmap, ancho, alto, true);
        //return scaled;
        return Bitmap.createScaledBitmap(imageBitmap, ancho, alto, true);
    }

    public static String getDateMonth(String fecha){
        String mes = "";
        try {
            String[] temp = fecha.split("-");
            mes = getMonth(Integer.parseInt(temp[1]));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return mes;
    }

    private static String getMonth(int mes){
        switch (mes){
            case 1:
                return "Enero";
            case 2:
                return "Febrero";
            case 3:
                return "Marzo";
            case 4:
                return "Abril";
            case 5:
                return "Mayo";
            case 6:
                return "Junio";
            case 7:
                return "Julio";
            case 8:
                return "Agosto";
            case 9:
                return "Septiembre";
            case 10:
                return "Octubre";
            case 11:
                return "Noviembre";
            case 12:
                return "Diciembre";
            default:
                return "";
        }
    }

    /**
     * Loader para ejecución de tareas
     * @param context
     * @param texto
     * @return
     */
    public static ProgressDialog typhoonLoader(Context context,String texto){
        LayoutInflater li = LayoutInflater.from(context);
        View layoutDialog = li.inflate(R.layout.typhoon_loader_layout, null);

        ImageView imageViewLoader = layoutDialog.findViewById(R.id.imageViewLoader);
        Glide.with(context).load(R.raw.loader3).into(imageViewLoader);

        TextView textView = (TextView) layoutDialog.findViewById(R.id.textViewLoader);
        textView.setText(texto);

        final ProgressDialog progressDialog = new ProgressDialog(context,R.style.ThemeTranslucent);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(layoutDialog);
        return progressDialog;
    }

    /**
     * Método para convertir imagen bitmap a base64
     * @param bitmap
     * @return
     * @throws IOException
     */
    public static String bitmapToBase64(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        byteArrayOutputStream.close();
        return encoded;
    }

    /**
     * Método para convertir una imagen bitmap a base64
     * @param bitmap
     * @param extension
     * @return
     * @throws IOException
     */
    public static String bitmapToBase64(Bitmap bitmap,String extension) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if(extension.contains("png")) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        }else{
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        }
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        byteArrayOutputStream.close();
        return encoded;
    }

    /**
     * Método para convertir imagen base64 en imagen bitmap
     * @param base64
     * @return
     * @throws IOException
     */
    public static Bitmap base64ToBitmap(String base64) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageBytes = baos.toByteArray();
        //decode base64 string to image
        imageBytes = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        baos.close();
        return decodedImage;
    }

    /**
     *
     * @param activity
     * @param opcion
     */
    public static void descargaCatalogos(final Activity activity, final int opcion){

        String titulo = "";
        if(opcion == 1){
            titulo = "Descargando catálogos...";
        }else{
            titulo = "Actualizando catálogos...";
        }

        final ProgressDialog progressDialog = Utils.typhoonLoader(activity,titulo);

        final Encryption encryption = new Encryption();

        //try {
            ApiInterface mApiService = getInterfaceService();
            SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE);
            Call<CatalogosTyphoonResponse> mService = mApiService.catalogosTyphoon(sharedPreferences.getString(Constants.SP_JWT_TAG, ""));
            mService.enqueue(new Callback<CatalogosTyphoonResponse>() {
                @Override
                public void onResponse(Call<CatalogosTyphoonResponse> call, Response<CatalogosTyphoonResponse> response) {
                    if(response != null) {
                        if (response.body() != null) {
                            if (response.body().getCatalogos().getExito()) {
                                //try {
                                BarcoDBMethods barcoDBMethods = new BarcoDBMethods(activity);
                                CatalogosDBMethods catalogosDBMethods = new CatalogosDBMethods(activity);
                                if (response.body().getCatalogos().getCatalogosData().getListBarcos() != null) {
                                    barcoDBMethods.deleteBarco();
                                    for (Barco catalogoBarco : response.body().getCatalogos().getCatalogosData().getListBarcos()) {
                                        barcoDBMethods.createBarco(catalogoBarco);
                                    }
                                }
                                if (response.body().getCatalogos().getCatalogosData().getListEstatusEvidencia() != null) {
                                    catalogosDBMethods.deleteEstatusEvidencia();
                                    for (EstatusEvidencia estatusEvidencia : response.body().getCatalogos().getCatalogosData().getListEstatusEvidencia()) {
                                        catalogosDBMethods.createEstatusEvidencia(estatusEvidencia);
                                    }
                                }
                                if (response.body().getCatalogos().getCatalogosData().getListEtapasEvidencia() != null) {
                                    catalogosDBMethods.deleteEtapaEvidencia();
                                    for (EtapaEvidencia etapaEvidencia : response.body().getCatalogos().getCatalogosData().getListEtapasEvidencia()) {
                                        catalogosDBMethods.createEtapaEvidencia(etapaEvidencia);
                                    }
                                }
                                if (response.body().getCatalogos().getCatalogosData().getListTiposRespuesta() != null) {
                                    catalogosDBMethods.deleteTipoRespuesta();
                                    for (TipoRespuesta tipoRespuesta : response.body().getCatalogos().getCatalogosData().getListTiposRespuesta()) {
                                        catalogosDBMethods.createTipoRespuesta(tipoRespuesta);
                                    }
                                }
                                if (response.body().getCatalogos().getCatalogosData().getListEstatusRevision() != null) {
                                    catalogosDBMethods.deleteEstatusRevision();
                                    for (EstatusRevision estatusRevision : response.body().getCatalogos().getCatalogosData().getListEstatusRevision()) {
                                        catalogosDBMethods.createEstatusRevision(estatusRevision);
                                    }
                                }
                                if (response.body().getCatalogos().getCatalogosData().getListRolesUsuario() != null) {
                                    catalogosDBMethods.deleteRolesUsuario();
                                    for (RolUsuario rolUsuario : response.body().getCatalogos().getCatalogosData().getListRolesUsuario()) {
                                        catalogosDBMethods.createRolUsuario(rolUsuario);
                                    }
                                }
                                if (response.body().getCatalogos().getCatalogosData().getListConfiguracion() != null) {
                                    //catalogosDBMethods.deleteRolesUsuario();
                                    SharedPreferences sharedPrefs = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE);
                                    SharedPreferences.Editor ed;
                                    ed = sharedPrefs.edit();
                                    for (Configuracion configuracion : response.body().getCatalogos().getCatalogosData().getListConfiguracion()) {
                                        if (configuracion.getConfiguracion().equals("LimiteEvidencias")) {
                                            ed.putString(Constants.SP_LIMITE_EVIDENCIAS, encryption.encryptAES(configuracion.getArgumento()));
                                            ed.apply();
                                        }
                                        if (configuracion.getConfiguracion().equals("Gps")) {
                                            ed.putString(Constants.SP_GPS_FLAG, encryption.encryptAES(configuracion.getArgumento()));
                                            ed.apply();
                                        }
                                        if (configuracion.getConfiguracion().equals("GpsConfig")) {
                                            ed.putString(Constants.SP_GPS_GEOCERCA, encryption.encryptAES(configuracion.getArgumento()));
                                            ed.apply();
                                        }
                                        if (configuracion.getConfiguracion().equals("ValidaFechaEvidencias")) {
                                            ed.putString(Constants.SP_VALIDA_FECHA, encryption.encryptAES(configuracion.getArgumento()));
                                            ed.apply();
                                        }
                                    }
                                }
                                if (response.body().getCatalogos().getCatalogosData().getListEtapasSubAnexo() != null) {
                                    catalogosDBMethods.deleteEtapaSubAnexo();
                                    for (EtapaSubAnexo etapaSubAnexo: response.body().getCatalogos().getCatalogosData().getListEtapasSubAnexo()) {
                                        catalogosDBMethods.createEtapaSubAnexo(etapaSubAnexo);
                                    }
                                }
                                progressDialog.dismiss();
                                Utils.message(activity, "Catálogos descargados");
                                if (opcion == 1) {
                                    Intent intent = new Intent(activity, CarteraFolios.class);
                                    activity.startActivity(intent);
                                    activity.finish();
                                }
                            /*} catch (NullPointerException e) {
                                progressDialog.dismiss();
                                Utils.message(activity, "Error al guardar los catálogos: " + e.getMessage());
                                e.printStackTrace();
                            }//*/
                            } else {
                                progressDialog.dismiss();
                                Utils.message(activity, response.body().getCatalogos().getError());
                            }
                        } else {
                            progressDialog.dismiss();
                            if (response.errorBody() != null) {
                                try {
                                    Utils.message(activity, "Error al descargar catálogos: " + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Utils.message(activity, "Error al descargar catálogos: " + e.getMessage());
                                }
                            } else {
                                Utils.message(activity, "Error al descargar catálogos");
                            }
                        }
                    }else{
                        Utils.message(activity, "Error al descargar catálogos");
                    }
                }

                @Override
                public void onFailure(Call<CatalogosTyphoonResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Utils.message(activity, Constants.MSG_ERR_CONN);
                }
            });
        /*}catch (NullPointerException e){
            e.printStackTrace();
            progressDialog.dismiss();
            Utils.message(activity, "Error al descargar catálogos: " + e.getMessage());
        }//*/
    }

    /**
     *
     * @param activity
     * @param contentUri
     * @return
     */
    public static String getRealPathFromURI(Activity activity, Uri contentUri) {
        Cursor cursor = null;
        String displayName = null;
        try {
            cursor = activity.getContentResolver().query(contentUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        } finally {
            cursor.close();
        }
        return displayName;
    }

    /**
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getPathFromUri(Context context,Uri uri){
        final String id = DocumentsContract.getDocumentId(uri);
        final Uri contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

        return getDataColumn(context, contentUri, null, null);
    }

    /**
     *
     * @param context
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    public static String getDataColumn(Context context, Uri uri, String selection,String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     *
     * @param context
     * @param uri
     * @return
     * @throws IOException
     */
    public static String fileToBase64(Context context, Uri uri) throws IOException {
        InputStream iStream =   context.getContentResolver().openInputStream(uri);
        //byte[] inputData = getBytes(iStream);
        byte[] inputData = IOUtils.toByteArray(iStream);
        String encodedImage = Base64.encodeToString(inputData, Base64.DEFAULT);
        return encodedImage;
    }

    /**
     *
     * @param data
     * @return
     */
    public static byte[] base64ToFile(String data){
        //File file = null;
        byte[] fileAsBytes = Base64.decode(data, 0);
        /*FileOutputStream os;
        file = new File(filename);
        os = new FileOutputStream(file, false);
        os.write(fileAsBytes);
        os.flush();
        os.close();
        return file;//*/
        return fileAsBytes;
    }

    /*public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }//*/

    /**
     *
     * @param context
     * @param uri
     * @return
     * @throws IOException
     */
    public static Bitmap getBitmap(Context context, Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        return bitmap;
    }

    public static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23) {
            ei = new ExifInterface(input);
        }else{
            ei = new ExifInterface(selectedImage.getPath());
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270); default: return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    /**
     *
     * @param evidencias
     * @return
     */
    public static boolean aplicaPregunta(Context context,List<Evidencia> evidencias){
        ResponseLogin.Usuario usuario = new UsuarioDBMethods(context).readUsuario();
        if(evidencias.size() != 0) {
            for (Evidencia evidencia : evidencias) {
                if(usuario.getIdrol() == 1){
                    if(evidencia.getIdEtapa() != 1 && evidencia.getIdEstatus() == 1){

                    }else{
                        return false;
                    }
                    //}else if(usuario.getIdrol() == 2){
                }else {
                    if(evidencia.getIdEtapa() > usuario.getIdrol() && evidencia.getIdEstatus() == 1){

                    }else{
                        return false;
                    }
                }
                /*if ((evidencia.getIdEtapa() == 1 && evidencia.getIdEstatus() == 2) || (evidencia.getIdEtapa() == 1 && evidencia.getIdEstatus() == 1)) {
                    return false;
                }//*/
            }
            return true;
        }else{
            return false;
        }
    }

    public static void validaFechaCreacion(){

    }

    /**
     *
     * @param activity
     * @param manifestPerm
     * @param permission
     * @return
     */
    public static boolean checkPermission(Activity activity, String manifestPerm, int permission) {
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            int storagePermission = activity.checkSelfPermission(manifestPerm);
            if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{manifestPerm}, permission);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static boolean checkPermission(final Activity activity){
        if(/*ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                +*/ ContextCompat.checkSelfPermission(
                activity,Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(
                activity,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            // Do something, when permissions not granted
            /*if(ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,Manifest.permission.CAMERA)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,Manifest.permission.WRITE_EXTERNAL_STORAGE)){//*/
            // If we should give explanation of requested permissions

            // Show an alert dialog here with request explanation

            LayoutInflater li = LayoutInflater.from(activity);
            LinearLayout layoutDialog = (LinearLayout) li.inflate(R.layout.permission_layout, null);

            Button buttonAceptar = (Button) layoutDialog.findViewById(R.id.buttonAceptar);
            LinearLayout linearLayoutCamera = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutCameraPermission);
            LinearLayout linearLayoutLocation = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutLocationPermission);
            LinearLayout linearLayoutStorage = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutStoragePermission);

            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                linearLayoutStorage.setVisibility(View.GONE);
            }

            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                linearLayoutCamera.setVisibility(View.GONE);
            }

            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                linearLayoutLocation.setVisibility(View.GONE);
            }

            final AlertDialog builder = new AlertDialog.Builder(activity)
                    .setView(layoutDialog)
                    .show();

            buttonAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(
                            activity,
                            new String[]{
                                    //Manifest.permission.CAMERA,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },
                            200
                    );
                    builder.dismiss();
                }
            });
            /*}else{
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        200
                );
            }//*/
        }else {
            // Do something, when permissions are already granted
            return true;
        }
        return false;
    }

    public static Calendar getCalendarDate(String inputDate){
        SimpleDateFormat format1=new SimpleDateFormat(Constants.DATE_FORMAT_FULL);
        Calendar c = null;
        try {
            Date dt1 = format1.parse(inputDate);
            c = Calendar.getInstance();
            c.setTime(dt1);
            //int dia = c.get(Calendar.DAY_OF_MONTH);
            //int mes = c.get(Calendar.MONTH) + 1;
            //int anio = c.get(Calendar.YEAR);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return c;
    }

    public static void nuevaInstalacionDialog(final Activity activity){
        LayoutInflater li = LayoutInflater.from(activity);
        LinearLayout layoutDialog = (LinearLayout) li.inflate(R.layout.dialog_nueva_instalacion_layout, null);

        TextView textViewCancelar = (TextView) layoutDialog.findViewById(R.id.buttonCancelar);
        TextView textViewAceptar = (TextView) layoutDialog.findViewById(R.id.buttonAceptar);
        LinearLayout linearLayoutCancelar = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutCancelar);
        LinearLayout linearLayoutAceptar = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutAceptar);

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
                new NuevaInstalacion(activity).execute();
                dialog.dismiss();
            }
        });

        linearLayoutAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NuevaInstalacion(activity).execute();
                dialog.dismiss();
            }
        });
    }

    public static String getEtapa(Context context,int etapa){
        CatalogosDBMethods catalogosDBMethods = new CatalogosDBMethods(context);
        List<EtapaEvidencia> listEtapa = catalogosDBMethods.readEtapaEvidencia(
                "SELECT ID_ETAPA,ID_USUARIO,DESCRIPCION FROM " + catalogosDBMethods.TP_CAT_CL_ETAPA_EVIDENCIA + " WHERE ID_ETAPA = ?",
                new String[]{String.valueOf(etapa)});
        if(listEtapa.size() != 0){
            return listEtapa.get(0).getDescripcion();
        }else{
            return "";
        }
    }

    public static String getRol(Context context,int idRol){
        CatalogosDBMethods catalogosDBMethods = new CatalogosDBMethods(context);
        List<RolUsuario> listRoles = catalogosDBMethods.readRolesUsuario(
                "SELECT ID_ROL,DESCRIPCION FROM " + catalogosDBMethods.TP_CAT_ROLES_USUARIO + " WHERE ID_ROL = ?",
                new String[]{String.valueOf(idRol)});
        if(listRoles.size() != 0){
            return listRoles.get(0).getDescripcion();
        }else{
            return "";
        }
    }

    public static String getEstatusEvidencia(Context context,int idEstatus){
        CatalogosDBMethods catalogosDBMethods = new CatalogosDBMethods(context);
        List<EstatusEvidencia> listEstatusEvidencia = catalogosDBMethods.readEstatusEvidencia(
                "SELECT ID_ESTATUS,DESCRIPCION FROM " + catalogosDBMethods.TP_CAT_CL_ESTATUS_EVIDENCIA + " WHERE ID_ESTATUS = ?",
                new String[]{String.valueOf(idEstatus)});
        if(listEstatusEvidencia.size() != 0){
            return listEstatusEvidencia.get(0).getDescripcion();
        }else{
            return "";
        }
    }

    /*public static String getEtapaEvidencia(Context context,int idEtapa){
        List<EtapaEvidencia> listEtapa = new CatalogosDBMethods(context).readEtapaEvidencia("WHERE ID_ETAPA = ?",new String[]{String.valueOf(idEtapa)});
        if(listEtapa.size() != 0){
            return listEtapa.get(0).getDescripcion();
        }else{
            return "";
        }
    }//*/

    public static boolean isPointInPolygon(LatLng tap) {

        ArrayList<LatLng> geocerca = new ArrayList<>();
        geocerca.add(new LatLng(19.304805241165596,-99.20415345810665));
        geocerca.add(new LatLng(19.30430908658883,-99.20403544090999));
        geocerca.add(new LatLng(19.30449134762865,-99.20354191445125));
        geocerca.add(new LatLng(19.3044407195824,-99.20318786286128));
        geocerca.add(new LatLng(19.304703985251763,-99.20313421868099));
        geocerca.add(new LatLng(19.30481536675354,-99.20348827027095));
        geocerca.add(new LatLng(19.304876120267995,-99.20381013535274));

        int intersectCount = 0;
        for (int j = 0; j < geocerca.size() - 1; j++) {
            if (rayCastIntersect(tap, geocerca.get(j), geocerca.get(j + 1))) {
                intersectCount++;
            }
        }

        return ((intersectCount % 2) == 1); // odd = inside, even = outside;
    }

    private static boolean rayCastIntersect(LatLng tap, LatLng vertA, LatLng vertB) {

        double aY = vertA.getLatitude();
        double bY = vertB.getLatitude();
        double aX = vertA.getLongitude();
        double bX = vertB.getLongitude();
        double pY = tap.getLatitude();
        double pX = tap.getLongitude();

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY)
                || (aX < pX && bX < pX)) {
            return false; // a and b can't both be above or below pt.y, and a or
            // b must be east of pt.x
        }

        double m = (aY - bY) / (aX - bX); // Rise over run
        double bee = (-aX) * m + aY; // y = mx + b
        double x = (pY - bee) / m; // algebra is neat!

        return x > pX;
    }

    public static boolean validaGeocerca(Activity activity){
        GPSTracker gps = new GPSTracker(activity,1);
        LatLng miPosicion = null;
        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            miPosicion = new LatLng(latitude,longitude);
            gps.stopUsingGPS();
        }

        if(miPosicion != null) {
            /*boolean enZona = Utils.isPointInPolygon(miPosicion);//dentro
            if (enZona) {
                Utils.message(this, "Dentro de la geocerca");
            } else {
                Utils.message(this, "Fuera de la geocerca");
            }//*/

            float[] disResultado = new float[2];
            SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE);
            if(sharedPreferences.contains(Constants.SP_GPS_GEOCERCA)){
                String geocerca = new Encryption().decryptAES(sharedPreferences.getString(Constants.SP_GPS_GEOCERCA,""));
                String[] temp = geocerca.split("\\|");
                double latitudeTyphoon = Double.parseDouble(temp[1].replace("Lat:",""));
                double longitudeTyphoon = Double.parseDouble(temp[0].replace("Lon:",""));
                float radioTyphoon = Float.parseFloat(temp[2].replace("Rad:",""));

                Location.distanceBetween(latitudeTyphoon,longitudeTyphoon,miPosicion.getLatitude(),miPosicion.getLongitude(),disResultado);
                //Location.distanceBetween(19.3046277,-99.2037863,miPosicion.getLatitude(),miPosicion.getLongitude(),disResultado);
                //Location.distanceBetween(19.3046277,-99.2037863,19.304980, -99.204047,disResultado);

                if(disResultado[0] > radioTyphoon){
                    //Utils.message(this,"Fuera de la geocerca");
                } else {
                    //Utils.message(this,"Dentro de la geocerca");
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean validaConfiguracionApp(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SP_NAME, context.MODE_PRIVATE);
        if(sharedPreferences.contains(Constants.SP_LIMITE_EVIDENCIAS) && sharedPreferences.contains(Constants.SP_GPS_GEOCERCA) &&
                sharedPreferences.contains(Constants.SP_GPS_FLAG)){
            return true;
        }else{
            Utils.message(context,"No se descargo correctamente la configuración");
            return false;
        }
    }

    @SuppressLint("NewApi")
    public static void deviceLockVerification(Context context){
        KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
        if(keyguardManager.isDeviceSecure()){

        }else{

        }
    }

    public static boolean installerVerification(Context context){
        String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());
        if(installer != null) {
            if (installer.startsWith("com.android.vending")) {
                return true;
            } else {
                return false;
            }
        }else{
            return false;
        }
    }

    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()){ return true;}
        }
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) {return true;}
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (process != null) {process.destroy();}
        }
    }

    public static boolean validaFechaRevision(Context context,String fechaFolio){
        Calendar calendarActual = Utils.getCalendarDate(Utils.getDate(Constants.DATE_FORMAT_FULL));
        Calendar calendarFolio = Utils.getCalendarDate(fechaFolio);
        if(calendarActual != null && calendarFolio != null) {
            int mesActual = calendarActual.get(Calendar.MONTH) + 1;
            int anioActual = calendarActual.get(Calendar.YEAR);
            int mesFolio = calendarFolio.get(Calendar.MONTH) + 1;
            int anioFolio = calendarFolio.get(Calendar.YEAR);
            if ((mesActual == mesFolio) && (anioActual == anioFolio)) {
                return true;
            }
        }else{
            message(context,"No se pudo validar la fecha de revisión");
        }
        return false;
    }
}
