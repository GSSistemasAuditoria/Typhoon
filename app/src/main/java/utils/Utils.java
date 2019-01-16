package utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.elektra.typhoon.constants.Constants;
import com.elektra.typhoon.service.ApiInterface;

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
}
