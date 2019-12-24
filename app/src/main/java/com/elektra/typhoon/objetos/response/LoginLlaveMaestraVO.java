package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginLlaveMaestraVO {
    @SerializedName("LoginModel")
    @Expose
    private DataLoginMasterKey datosRequest;

    public DataLoginMasterKey getDatosRequest() {
        return datosRequest;
    }

    public void setDatosRequest(DataLoginMasterKey datosRequest) {
        this.datosRequest = datosRequest;
    }

    public class DataLoginMasterKey{
        @SerializedName("OAUTHTOKEN")
        @Expose
        private String OAUTHTOKEN;

        @SerializedName("IS_MOVIL")
        @Expose
        private boolean IS_MOVIL;

        @SerializedName("IP_CLIENT")
        @Expose
        private String IP_CLIENT;

        @SerializedName("FB_TOKEN")
        @Expose
        private String FB_TOKEN;

        @SerializedName("USER_NAME")
        @Expose
        private String USER_NAME;

        @SerializedName("LLAVE_MAESTRA")
        @Expose
        private String LLAVE_MAESTRA;

        public String getOAUTHTOKEN() {
            return OAUTHTOKEN;
        }

        public void setOAUTHTOKEN(String OAUTHTOKEN) {
            this.OAUTHTOKEN = OAUTHTOKEN;
        }

        public boolean isIS_MOVIL() {
            return IS_MOVIL;
        }

        public void setIS_MOVIL(boolean IS_MOVIL) {
            this.IS_MOVIL = IS_MOVIL;
        }

        public String getIP_CLIENT() {
            return IP_CLIENT;
        }

        public void setIP_CLIENT(String IP_CLIENT) {
            this.IP_CLIENT = IP_CLIENT;
        }

        public String getFB_TOKEN() {
            return FB_TOKEN;
        }

        public void setFB_TOKEN(String FB_TOKEN) {
            this.FB_TOKEN = FB_TOKEN;
        }

        public String getUSER_NAME() {
            return USER_NAME;
        }

        public void setUSER_NAME(String USER_NAME) {
            this.USER_NAME = USER_NAME;
        }

        public String getLLAVE_MAESTRA() {
            return LLAVE_MAESTRA;
        }

        public void setLLAVE_MAESTRA(String LLAVE_MAESTRA) {
            this.LLAVE_MAESTRA = LLAVE_MAESTRA;
        }
    }
}
