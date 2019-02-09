package com.elektra.typhoon.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.elektra.typhoon.R;
import com.elektra.typhoon.carteraFolios.CarteraFolios;
import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.database.BarcoDBMethods;
import com.elektra.typhoon.database.CatalogosDBMethods;
import com.elektra.typhoon.login.MainActivity;
import com.elektra.typhoon.objetos.response.Barco;
import com.elektra.typhoon.objetos.response.CatalogosTyphoonResponse;
import com.elektra.typhoon.objetos.response.EstatusEvidencia;
import com.elektra.typhoon.objetos.response.EtapaEvidencia;
import com.elektra.typhoon.objetos.response.TipoRespuesta;
import com.elektra.typhoon.service.ApiInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public static String getDateMonth(String fecha){
        String mes = "";
        try {
            String[] temp = fecha.split("-");
            mes = getMonth(Integer.parseInt(temp[1]));
        } catch (Exception e) {
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
        Glide.with(context).load(R.raw.loader2).into(imageViewLoader);

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

    public static void descargaCatalogos(final Activity activity, final int opcion){

        String titulo = "";
        if(opcion == 1){
            titulo = "Descargando catálogos...";
        }else{
            titulo = "Actualizando catálogos...";
        }

        final ProgressDialog progressDialog = Utils.typhoonLoader(activity,titulo);

        ApiInterface mApiService = getInterfaceService();
        Call<CatalogosTyphoonResponse> mService = mApiService.catalogosTyphoon();
        mService.enqueue(new Callback<CatalogosTyphoonResponse>() {
            @Override
            public void onResponse(Call<CatalogosTyphoonResponse> call, Response<CatalogosTyphoonResponse> response) {
                if(response.body() != null) {
                    if(response.body().getCatalogos().getExito()){
                        try {
                            BarcoDBMethods barcoDBMethods = new BarcoDBMethods(activity);
                            CatalogosDBMethods catalogosDBMethods = new CatalogosDBMethods(activity);
                            if(response.body().getCatalogos().getCatalogosData().getListBarcos() != null){
                                barcoDBMethods.deleteBarco();
                                for(Barco catalogoBarco:response.body().getCatalogos().getCatalogosData().getListBarcos()){
                                    barcoDBMethods.createBarco(catalogoBarco);
                                }
                            }
                            if(response.body().getCatalogos().getCatalogosData().getListEstatusEvidencia() != null){
                                catalogosDBMethods.deleteEstatusEvidencia();
                                for(EstatusEvidencia estatusEvidencia:response.body().getCatalogos().getCatalogosData().getListEstatusEvidencia()){
                                    catalogosDBMethods.createEstatusEvidencia(estatusEvidencia);
                                }
                            }
                            if(response.body().getCatalogos().getCatalogosData().getListEtapasEvidencia() != null){
                                catalogosDBMethods.deleteEtapaEvidencia();
                                for(EtapaEvidencia etapaEvidencia:response.body().getCatalogos().getCatalogosData().getListEtapasEvidencia()){
                                    catalogosDBMethods.createEtapaEvidencia(etapaEvidencia);
                                }
                            }
                            if(response.body().getCatalogos().getCatalogosData().getListTiposRespuesta() != null){
                                catalogosDBMethods.deleteTipoRespuesta();
                                for(TipoRespuesta tipoRespuesta:response.body().getCatalogos().getCatalogosData().getListTiposRespuesta()){
                                    catalogosDBMethods.createTipoRespuesta(tipoRespuesta);
                                }
                            }
                            progressDialog.dismiss();
                            Utils.message(activity,"Catálogos descargados");
                            if(opcion == 1) {
                                Intent intent = new Intent(activity, CarteraFolios.class);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        }catch (Exception e){
                            progressDialog.dismiss();
                            Utils.message(activity,"Error al guardar los catálogos: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }else{
                        progressDialog.dismiss();
                        Utils.message(activity, response.body().getCatalogos().getError());
                    }
                }else{
                    progressDialog.dismiss();
                    Utils.message(activity,"Error al descargar catálogos");
                }
            }
            @Override
            public void onFailure(Call<CatalogosTyphoonResponse> call, Throwable t) {
                progressDialog.dismiss();
                Utils.message(activity, Constants.MSG_ERR_CONN);
            }
        });
    }

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

    public static String getPathFromUri(Context context,Uri uri){
        final String id = DocumentsContract.getDocumentId(uri);
        final Uri contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

        return getDataColumn(context, contentUri, null, null);
    }

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
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static String fileToBase64(Context context, Uri uri) throws IOException {
        InputStream iStream =   context.getContentResolver().openInputStream(uri);
        byte[] inputData = getBytes(iStream);
        String encodedImage = Base64.encodeToString(inputData, Base64.DEFAULT);
        return encodedImage;
    }

    //public static File base64ToFile(String filename,String data) throws IOException {
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

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static Bitmap getBitmap(Context context, Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        return bitmap;
    }

    public static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else ei = new ExifInterface(selectedImage.getPath());
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
}
