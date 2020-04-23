package com.elektra.typhoon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.elektra.typhoon.database.TyphoonDataBase;
import com.elektra.typhoon.utils.Utils;

import java.io.IOException;
import java.util.Arrays;

public class GlobalHandlerErrorActivity extends AppCompatActivity {

    private Thread paramThread;
    private Throwable paramThrowable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Thread.UncaughtExceptionHandler oldHandler =
                Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread paramThread, final Throwable paramThrowable) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Utils.getInterfaceServiceLog().setLogMovil(new com.elektra.typhoon.objetos.request.Log("TyphoonApp", 2,
                                    paramThrowable.getMessage() + " : " + paramThrowable.getCause().getMessage() + " : " + Arrays.toString(paramThrowable.getStackTrace()))).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //TyphoonDataBase.createError(GlobalHandlerErrorActivity.this, paramThrowable.getMessage() + " : " + paramThrowable.getCause().getMessage() + " : " + paramThrowable.toString());
                    }
                }).start();

                Log.e("Alert", "Lets See if it Works !!!");
                if (oldHandler != null)
                    oldHandler.uncaughtException(
                            paramThread,
                            paramThrowable
                    ); //Delegates to Android's error handling
                else
                    System.exit(2); //Prevents the service/app from freezing
            }
        });
    }
}
