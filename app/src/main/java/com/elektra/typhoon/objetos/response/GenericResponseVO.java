package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GenericResponseVO {
    @SerializedName("IsUserSesionResult")
    @Expose
    private DataIsUserSession mData;

    public DataIsUserSession getmData() {
        return mData;
    }

    public void setmData(DataIsUserSession mData) {
        this.mData = mData;
    }

    public class DataIsUserSession{
        @SerializedName("CodeError")
        @Expose
        private int CodeError;

        @SerializedName("Error")
        @Expose
        private String Error;

        @SerializedName("Exito")
        @Expose
        private boolean Exito;
        private DataVO Data;

        public int getCodeError() {
            return CodeError;
        }

        public void setCodeError(int codeError) {
            CodeError = codeError;
        }

        public String getError() {
            return Error;
        }

        public void setError(String error) {
            Error = error;
        }

        public boolean isExito() {
            return Exito;
        }

        public void setExito(boolean exito) {
            Exito = exito;
        }

        public DataVO getData() {
            return Data;
        }

        public void setData(DataVO data) {
            Data = data;
        }

        public class DataVO{
            @SerializedName("ID_USUARIO")
            @Expose
            private String ID_USUARIO;

            public String getID_USUARIO() {
                return ID_USUARIO;
            }

            public void setID_USUARIO(String ID_USUARIO) {
                this.ID_USUARIO = ID_USUARIO;
            }
        }
    }
}
