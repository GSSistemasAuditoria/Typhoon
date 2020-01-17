package com.elektra.typhoon.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.elektra.typhoon.utils.Utils;

public class AsyncTaskGral extends AsyncTask<String, String, String> {
    public static final String TAG = AsyncTaskGral.class.getName();

    private Delegate mDelegate;
    private Context mContext;
    private ProgressDialog statusDialog;
    private String message;

    public AsyncTaskGral(Context mContext, Delegate mDelegate, String message) {
        this.mDelegate = mDelegate;
        this.mContext = mContext;
        this.message = message;
    }

    protected void onPreExecute() {
        if (message != null) {
            statusDialog = Utils.typhoonLoader(mContext, message);
        }else
            statusDialog = Utils.typhoonLoader(mContext, "Cargando");
    }

    @Override
    protected String doInBackground(String... data) {
        return mDelegate.executeInBackground();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (statusDialog != null)
            statusDialog.dismiss();
        mDelegate.getDelegate(s);
    }
}
