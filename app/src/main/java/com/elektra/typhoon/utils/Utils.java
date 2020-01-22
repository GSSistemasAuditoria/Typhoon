package com.elektra.typhoon.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
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
import com.elektra.typhoon.database.AnexosDBMethods;
import com.elektra.typhoon.database.BarcoDBMethods;
import com.elektra.typhoon.database.CatalogosDBMethods;
import com.elektra.typhoon.database.ChecklistDBMethods;
import com.elektra.typhoon.database.TyphoonDataBase;
import com.elektra.typhoon.database.UsuarioDBMethods;
import com.elektra.typhoon.encryption.Encryption;
import com.elektra.typhoon.gps.GPSTracker;
import com.elektra.typhoon.login.MainActivity;
import com.elektra.typhoon.objetos.response.Barco;
import com.elektra.typhoon.objetos.response.CatalogoBarco;
import com.elektra.typhoon.objetos.response.CatalogosTyphoonResponse;
import com.elektra.typhoon.objetos.response.CerrarSesionResponse;
import com.elektra.typhoon.objetos.response.Configuracion;
import com.elektra.typhoon.objetos.response.EstatusEvidencia;
import com.elektra.typhoon.objetos.response.EstatusRevision;
import com.elektra.typhoon.objetos.response.EtapaEvidencia;
import com.elektra.typhoon.objetos.response.EtapaSubAnexo;
import com.elektra.typhoon.objetos.response.Evidencia;
import com.elektra.typhoon.objetos.response.LatLng;
import com.elektra.typhoon.objetos.response.Pregunta;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.RespuestaData;
import com.elektra.typhoon.objetos.response.RolUsuario;
import com.elektra.typhoon.objetos.response.TipoRespuesta;
import com.elektra.typhoon.service.ApiInterface;
import com.google.android.gms.common.util.IOUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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

import static android.content.Context.LOCATION_SERVICE;

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
     *
     * @return
     */
    public static ApiInterface getInterfaceService() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                //.baseUrl(Constants.URL_PUBLIC)
                .baseUrl(new Encryption().decryptAES(Constants.URL_PUBLIC))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        return mInterfaceService;
    }

    /**
     * Método para mostrar texto en pantalla
     *
     * @param context
     * @param text
     */
    /*public static void message(Context context,String text){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }//*/
    public static void message(Context context, String text) {
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
     *
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * Método para validar expresión regular
     *
     * @param cadena
     * @param regex
     * @return
     */
    public static boolean validaExpresion(String cadena, String regex) {
        Pattern patron = Pattern.compile(regex);
        return patron.matcher(cadena).matches();
    }

    /**
     * Método para obtener el Uri de un bitmap
     *
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

    public static void openCamera(Activity activity, int requestCode) {
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
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
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

    public static Bitmap resizeImageBitmap(Bitmap imageBitmap, int ancho, int alto) {
        //Bitmap scaled = Bitmap.createScaledBitmap(imageBitmap, ancho, alto, true);
        //return scaled;
        return Bitmap.createScaledBitmap(imageBitmap, ancho, alto, true);
    }

    public static String getDateMonth(String fecha) {
        String mes = "";
        try {
            String[] temp = fecha.split("-");
            mes = getMonth(Integer.parseInt(temp[1]));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return mes;
    }

    private static String getMonth(int mes) {
        switch (mes) {
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
     *
     * @param context
     * @param texto
     * @return
     */
    public static ProgressDialog typhoonLoader(final Activity context, String texto) {
        LayoutInflater li = LayoutInflater.from(context);
        View layoutDialog = li.inflate(R.layout.typhoon_loader_layout, null);

        final ImageView imageViewLoader = layoutDialog.findViewById(R.id.imageViewLoader);
        Glide.with(context).load(R.raw.loader3).into(imageViewLoader);
        TextView textView = (TextView) layoutDialog.findViewById(R.id.textViewLoader);
        textView.setText(texto);

        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeTranslucent);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(layoutDialog);
        return progressDialog;
    }

    /**
     * Método para convertir imagen bitmap a base64
     *
     * @param bitmap
     * @return
     * @throws IOException
     */
    public static String bitmapToBase64(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        byteArrayOutputStream.close();
        return encoded;
    }

    /**
     * Método para convertir una imagen bitmap a base64
     *
     * @param bitmap
     * @param extension
     * @return
     * @throws IOException
     */
    public static String bitmapToBase64(Bitmap bitmap, String extension) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (extension.contains("png")) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        byteArrayOutputStream.close();
        return encoded;
    }

    /**
     * Método para convertir imagen base64 en imagen bitmap
     *
     * @param base64
     * @return
     * @throws IOException
     */
    public static Bitmap base64ToBitmap(String base64) throws IOException {
        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageBytes;
        //decode base64 string to image
        if (base64 == null || base64.isEmpty())
            imageBytes = Base64.decode("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", Base64.DEFAULT);
        else
            imageBytes = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        //baos.close();
        return decodedImage;
    }

    /**
     * @param activity
     * @param opcion
     */
    public static void descargaCatalogos(final Activity activity, final int opcion) {

        String titulo = "";
        if (opcion == 1) {
            titulo = "Descargando catálogos...";
        } else {
            titulo = "Actualizando catálogos...";
        }

        final ProgressDialog progressDialog = Utils.typhoonLoader(activity, titulo);

        final Encryption encryption = new Encryption();

        try {
            ApiInterface mApiService = getInterfaceService();
            SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE);
            Call<CatalogosTyphoonResponse> mService = mApiService.catalogosTyphoon(Utils.getIPAddress(), sharedPreferences.getString(Constants.SP_JWT_TAG, ""));
            mService.enqueue(new Callback<CatalogosTyphoonResponse>() {
                @Override
                public void onResponse(Call<CatalogosTyphoonResponse> call, Response<CatalogosTyphoonResponse> response) {
                    if (response != null) {
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
                                    for (EtapaSubAnexo etapaSubAnexo : response.body().getCatalogos().getCatalogosData().getListEtapasSubAnexo()) {
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
                    } else {
                        Utils.message(activity, "Error al descargar catálogos");
                    }
                }

                @Override
                public void onFailure(Call<CatalogosTyphoonResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Utils.message(activity, Constants.MSG_ERR_CONN);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            Utils.message(activity, "Error al descargar catálogos: " + e.getMessage());
        } catch (Error e) {
            progressDialog.dismiss();
            Utils.message(activity, "Error al descargar catálogos: ");
        }//*/
    }

    /**
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
     * @param context
     * @param uri
     * @return
     */
    public static String getPathFromUri(Context context, Uri uri) {
        final String id = DocumentsContract.getDocumentId(uri);
        final Uri contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

        return getDataColumn(context, contentUri, null, null);
    }

    /**
     * @param context
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

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
     * @param context
     * @param uri
     * @return
     * @throws IOException
     */
    public static String fileToBase64(Context context, Uri uri) throws IOException {
        InputStream iStream = context.getContentResolver().openInputStream(uri);
        //byte[] inputData = getBytes(iStream);
        byte[] inputData = IOUtils.toByteArray(iStream);
        String encodedImage = Base64.encodeToString(inputData, Base64.DEFAULT);
        return encodedImage;
    }

    /**
     * @param data
     * @return
     */
    public static byte[] base64ToFile(String data) {
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
        } else {
            ei = new ExifInterface(selectedImage.getPath());
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
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
     * @param evidencias
     * @return
     */
    public static boolean aplicaPregunta(Context context, List<Evidencia> evidencias) {
        ResponseLogin.Usuario usuario = new UsuarioDBMethods(context).readUsuario();
        if (evidencias.size() != 0) {
            for (Evidencia evidencia : evidencias) {
                if (usuario.getIdrol() == 1) {
                    if (evidencia.getIdEtapa() != 1 && evidencia.getIdEstatus() == 1) {

                    } else {
                        return false;
                    }
                    //}else if(usuario.getIdrol() == 2){
                } else {
                    if (evidencia.getIdEtapa() > usuario.getIdrol() && evidencia.getIdEstatus() == 1) {

                    } else {
                        return false;
                    }
                }
                /*if ((evidencia.getIdEtapa() == 1 && evidencia.getIdEstatus() == 2) || (evidencia.getIdEtapa() == 1 && evidencia.getIdEstatus() == 1)) {
                    return false;
                }//*/
            }
            return true;
        } else {
            return false;
        }
    }

    public static void validaFechaCreacion() {

    }

    /**
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

    public static boolean checkPermission(final Activity activity) {
        if (/*ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                +*/ ContextCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(
                activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

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

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                linearLayoutStorage.setVisibility(View.GONE);
            }

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                linearLayoutCamera.setVisibility(View.GONE);
            }

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
        } else {
            // Do something, when permissions are already granted
            return true;
        }
        return false;
    }

    public static void checkPermissionTest(final Activity activity) {

        LayoutInflater li = LayoutInflater.from(activity);
        LinearLayout layoutDialog = (LinearLayout) li.inflate(R.layout.permission_layout, null);

        Button buttonAceptar = (Button) layoutDialog.findViewById(R.id.buttonAceptar);
        LinearLayout linearLayoutCamera = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutCameraPermission);
        LinearLayout linearLayoutLocation = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutLocationPermission);
        LinearLayout linearLayoutStorage = (LinearLayout) layoutDialog.findViewById(R.id.linearLayoutStoragePermission);

            /*if(ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                linearLayoutStorage.setVisibility(View.GONE);
            }

            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                linearLayoutCamera.setVisibility(View.GONE);
            }

            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                linearLayoutLocation.setVisibility(View.GONE);
            }//*/

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
    }

    public static Calendar getCalendarDate(String inputDate) {
        SimpleDateFormat format1 = new SimpleDateFormat(Constants.DATE_FORMAT_FULL);
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

    public static void nuevaInstalacionDialog(final Activity activity) {
        final ResponseLogin.Usuario usuario = new UsuarioDBMethods(activity).readUsuario();
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
                cerrarSesionService((usuario.getInterno()) ? usuario.getIdUsuario() : usuario.getCorreo(),
                        true, activity);
                dialog.dismiss();
            }
        });

        linearLayoutAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new NuevaInstalacion(activity).execute();
                cerrarSesionService((usuario.getInterno()) ? usuario.getIdUsuario() : usuario.getCorreo(),
                        true, activity);
                dialog.dismiss();
            }
        });
    }

    public static void cerrarSesionService(String idUsuario, final boolean borrarData, final Activity mActivity) {
        final SharedPreferences sharedPrefs = mActivity.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
        Call<CerrarSesionResponse> mServiceCerrarSesion = getInterfaceService().cerrarSesion(idUsuario);
        mServiceCerrarSesion.enqueue(new Callback<CerrarSesionResponse>() {
            @Override
            public void onResponse(Call<CerrarSesionResponse> call, Response<CerrarSesionResponse> response) {
                if (response != null) {
                    if (response.body() != null) {
                        if (response.body().getCerrarSesion().getExito()) {
                            if (borrarData) {
                                sharedPrefs.edit().clear().apply();
                                new TyphoonDataBase(mActivity).deleteAll();
                            }
                            sharedPrefs.edit().putBoolean(Constants.SP_LOGIN_TAG, false).apply();
                            mActivity.startActivity(new Intent(mActivity, MainActivity.class));
                            mActivity.finish();
                        } else {
                            //progressDialog.dismiss();
                            Utils.message(mActivity, response.body().getCerrarSesion().getError());
                        }//*/
                    } else {
                        //progressDialog.dismiss();
                        if (response.errorBody() != null) {
                            try {
                                String mensaje = "" + response.errorBody().string();
                                int code = response.code();
                                //if(!mensaje.contains("No tiene permiso para ver")) {
                                if (code != 401) {
                                    //Utils.message(getApplicationContext(), "Error al descargar folios: " + response.errorBody().string());
                                    Utils.message(mActivity, "No se pudo realizar la nueva instalación: " + response.errorBody().string());
                                } else {
                                    Utils.message(mActivity, "La sesion ha expirado");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Utils.message(mActivity, "No se pudo realizar la nueva instalación");
                        }
                    }
                } else {
                    //progressDialog.dismiss();
                    Utils.message(mActivity, "No se pudo realizar la nueva instalación");
                }
            }

            @Override
            public void onFailure(Call<CerrarSesionResponse> call, Throwable t) {

            }
        });
    }

    public static String getEtapa(Context context, int etapa) {
        CatalogosDBMethods catalogosDBMethods = new CatalogosDBMethods(context);
        List<EtapaEvidencia> listEtapa = catalogosDBMethods.readEtapaEvidencia(
                "SELECT ID_ETAPA,ID_USUARIO,DESCRIPCION FROM " + catalogosDBMethods.TP_CAT_CL_ETAPA_EVIDENCIA + " WHERE ID_ETAPA = ?",
                new String[]{String.valueOf(etapa)});
        if (listEtapa.size() != 0) {
            return listEtapa.get(0).getDescripcion();
        } else {
            return "";
        }
    }

    public static String getRol(Context context, int idRol) {
        CatalogosDBMethods catalogosDBMethods = new CatalogosDBMethods(context);
        List<RolUsuario> listRoles = catalogosDBMethods.readRolesUsuario(
                "SELECT ID_ROL,DESCRIPCION,IS_GEOCERCA FROM " + catalogosDBMethods.TP_CAT_ROLES_USUARIO + " WHERE ID_ROL = ?",
                new String[]{String.valueOf(idRol)});
        if (listRoles.size() != 0) {
            return listRoles.get(0).getDescripcion();
        } else {
            return "";
        }
    }

    public static String getEstatusEvidencia(Context context, int idEstatus) {
        CatalogosDBMethods catalogosDBMethods = new CatalogosDBMethods(context);
        List<EstatusEvidencia> listEstatusEvidencia = catalogosDBMethods.readEstatusEvidencia(
                "SELECT ID_ESTATUS,DESCRIPCION FROM " + catalogosDBMethods.TP_CAT_CL_ESTATUS_EVIDENCIA + " WHERE ID_ESTATUS = ?",
                new String[]{String.valueOf(idEstatus)});
        if (listEstatusEvidencia.size() != 0) {
            return listEstatusEvidencia.get(0).getDescripcion();
        } else {
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
        geocerca.add(new LatLng(19.304805241165596, -99.20415345810665));
        geocerca.add(new LatLng(19.30430908658883, -99.20403544090999));
        geocerca.add(new LatLng(19.30449134762865, -99.20354191445125));
        geocerca.add(new LatLng(19.3044407195824, -99.20318786286128));
        geocerca.add(new LatLng(19.304703985251763, -99.20313421868099));
        geocerca.add(new LatLng(19.30481536675354, -99.20348827027095));
        geocerca.add(new LatLng(19.304876120267995, -99.20381013535274));

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

    public static boolean validaGeocerca(Activity activity) {
        GPSTracker gps = new GPSTracker(activity, 1);
        LatLng miPosicion = null;
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            miPosicion = new LatLng(latitude, longitude);
            gps.stopUsingGPS();
        }

        if (miPosicion != null) {
            /*boolean enZona = Utils.isPointInPolygon(miPosicion);//dentro
            if (enZona) {
                Utils.message(this, "Dentro de la geocerca");
            } else {
                Utils.message(this, "Fuera de la geocerca");
            }//*/

            float[] disResultado = new float[2];
            SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SP_NAME, activity.MODE_PRIVATE);
            if (sharedPreferences.contains(Constants.SP_GPS_GEOCERCA)) {
                String geocerca = new Encryption().decryptAES(sharedPreferences.getString(Constants.SP_GPS_GEOCERCA, ""));
                String[] temp = geocerca.split("\\|");
                double latitudeTyphoon = Double.parseDouble(temp[1].replace("Lat:", ""));
                double longitudeTyphoon = Double.parseDouble(temp[0].replace("Lon:", ""));
                float radioTyphoon = Float.parseFloat(temp[2].replace("Rad:", ""));

                Location.distanceBetween(latitudeTyphoon, longitudeTyphoon, miPosicion.getLatitude(), miPosicion.getLongitude(), disResultado);
                //Location.distanceBetween(19.3046277,-99.2037863,miPosicion.getLatitude(),miPosicion.getLongitude(),disResultado);
                //Location.distanceBetween(19.3046277,-99.2037863,19.304980, -99.204047,disResultado);

                if (disResultado[0] > radioTyphoon) {
                    //Utils.message(this,"Fuera de la geocerca");
                } else {
                    //Utils.message(this,"Dentro de la geocerca");
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean validaConfiguracionApp(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SP_NAME, context.MODE_PRIVATE);
        if (sharedPreferences.contains(Constants.SP_LIMITE_EVIDENCIAS) && sharedPreferences.contains(Constants.SP_GPS_GEOCERCA) &&
                sharedPreferences.contains(Constants.SP_GPS_FLAG)) {
            return true;
        } else {
            Utils.message(context, "No se descargo correctamente la configuración");
            return false;
        }
    }

    @SuppressLint("NewApi")
    public static void deviceLockVerification(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        try {
            if (keyguardManager.isDeviceSecure()) {

            } else {

            }
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
    }

    public static boolean installerVerification(Context context) {
        String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());
        if (installer != null) {
            if (installer.startsWith("com.android.vending")) {
                return true;
            } else {
                return false;
            }
        } else {
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
        String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) {
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    public static boolean validaFechaRevision(Context context, String fechaFolio) {
        Calendar calendarActual = Utils.getCalendarDate(Utils.getDate(Constants.DATE_FORMAT_FULL));
        Calendar calendarFolio = Utils.getCalendarDate(fechaFolio);
        if (calendarActual != null && calendarFolio != null) {
            int mesActual = calendarActual.get(Calendar.MONTH) + 1;
            int anioActual = calendarActual.get(Calendar.YEAR);
            int mesFolio = calendarFolio.get(Calendar.MONTH) + 1;
            int anioFolio = calendarFolio.get(Calendar.YEAR);
            if ((mesActual == mesFolio) && (anioActual == anioFolio)) {
                return true;
            }
        } else {
            message(context, "No se pudo validar la fecha de revisión");
        }
        return false;
    }

    public static void updatePregunta(Activity activity, String idRevision, String idChecklist, String idPregunta, String idRubro, int seleccionado) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("SELECCIONADO", seleccionado);
        new ChecklistDBMethods(activity).updatePregunta(contentValues, "ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_PREGUNTA = ? AND ID_RUBRO = ?",
                new String[]{idRevision, idChecklist, idPregunta, idRubro});
    }

    public static void updatePregunta(Activity activity, String idRevision, String idChecklist, String idPregunta, String idRubro, String idBarco, int seleccionado) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("SINCRONIZADO", seleccionado);
        new ChecklistDBMethods(activity).updateRespuesta(contentValues, "ID_REVISION = ? AND ID_CHECKLIST = ? AND ID_PREGUNTA = ? AND ID_RUBRO = ? AND ID_BARCO = ?",
                new String[]{idRevision, idChecklist, idPregunta, idRubro, idBarco});
    }

    public static void updateAnexo(Activity activity, String idRevision, String idSubanexo, int seleccionado) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("SELECCIONADO", seleccionado);
        new AnexosDBMethods(activity).updateAnexo(contentValues, "ID_REVISION = ? AND ID_SUBANEXO = ?",
                new String[]{idRevision, idSubanexo});
    }

    public static boolean respuestaIsTierra(List<Pregunta> listPreguntas, RespuestaData respuestaData) {
        for (Pregunta pregunta : listPreguntas) {
            if (respuestaData.getIdRevision() == pregunta.getIdRevision() && respuestaData.getIdChecklist() == pregunta.getIdChecklist()
                    && respuestaData.getIdPregunta() == pregunta.getIdPregunta()) {
                if (pregunta.isTierra()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void contabilizaValoresChecklist() {

    }

    public static boolean isPreguntaSeleccionada(List<RespuestaData> listRespuestas, Pregunta pregunta) {
        for (RespuestaData respuestaData : listRespuestas) {
            if (respuestaData.getIdRevision() == pregunta.getIdRevision() &&
                    respuestaData.getIdChecklist() == pregunta.getIdChecklist() &&
                    respuestaData.getIdRubro() == pregunta.getIdRubro() &&
                    respuestaData.getIdPregunta() == pregunta.getIdPregunta() &&
                    respuestaData.getIdBarco() == pregunta.getIdBarco()) {
                if (respuestaData.getSincronizado() == 1) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static String getNombreBarco(Context context, int id) {
        BarcoDBMethods barcoDBMethods = new BarcoDBMethods(context);
        List<CatalogoBarco> barcos = barcoDBMethods.readBarcos();
        for (CatalogoBarco catalogoBarco : barcos) {
            if (catalogoBarco.getIdBarco() == id) {
                return catalogoBarco.getNombre();
            }
        }
        return "";
    }

    public static String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;
                        /*if (useIPv4) {*/
                        if (isIPv4) {
                            return sAddr;
                        }
/*                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim<0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }*/
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(Utils.class.getName(), ex.getMessage());
            ex.printStackTrace();
        } // for now eat exceptions
        return "";
    }

    public static String removeSpecialCharacters(String cadena) {
        return (cadena != null) ? Normalizer.normalize(cadena, Normalizer.Form.NFKD).replaceAll("[^a-zA-Z0-9. ]+", "") : null;
        //return cadena;
    }

    public static boolean requesTurnOnGps(final Activity mActivity) {
        LocationManager locationManager = (LocationManager) mActivity.getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertDialog.Builder(mActivity)
                    .setTitle(R.string.title_dialog_gps)
                    .setMessage(R.string.question_gps_turn_on)
                    .setPositiveButton(R.string.aceptar_tag, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            mActivity.startActivity(intent);
                        }
                    }).show();
            return false;
        }
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return false;
        } else
            return true;
    }
}
