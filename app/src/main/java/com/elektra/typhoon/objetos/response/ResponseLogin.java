package com.elektra.typhoon.objetos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Proyecto: TYPHOON
 * Autor: Emmanuel Rangel Reyes
 * Fecha: 14/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */
public class ResponseLogin {

    @SerializedName("ValidarEmpleadoResult")
    @Expose
    private ValidarEmpleado validarEmpleado;

    public ValidarEmpleado getValidarEmpleado() {
        return validarEmpleado;
    }

    public void setValidarEmpleado(ValidarEmpleado validarEmpleado) {
        this.validarEmpleado = validarEmpleado;
    }

    public class ValidarEmpleado {

        @SerializedName("Data")
        @Expose
        private Usuario usuario;
        @SerializedName("Error")
        @Expose
        private String error;
        @SerializedName("Exito")
        @Expose
        private Boolean exito;
        @SerializedName("HttpStatusCode")
        @Expose
        private int httpStatusCode;

        public Usuario getUsuario() {
            return usuario;
        }

        public void setUsuario(Usuario usuario) {
            this.usuario = usuario;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public Boolean getExito() {
            return exito;
        }

        public void setExito(Boolean exito) {
            this.exito = exito;
        }

        public int getHttpStatusCode() {
            return httpStatusCode;
        }

        public void setHttpStatusCode(int httpStatusCode) {
            this.httpStatusCode = httpStatusCode;
        }

    }

    public static class Usuario {

        @SerializedName("CORREO")
        @Expose
        private String correo;
        @SerializedName("ESTATUS")
        @Expose
        private Integer estatus;
        @SerializedName("ID_ROL")
        @Expose
        private Integer idrol;
        @SerializedName("ID_USUARIO")
        @Expose
        private String idUsuario;
        @SerializedName("INTERNO")
        @Expose
        private Boolean interno;
        @SerializedName("JWT")
        @Expose
        private String jwt;
        @SerializedName("NOMBRE")
        @Expose
        private String nombre;
        @SerializedName("PASSWORD")
        @Expose
        private String acceso;

        public String getCorreo() {
            return correo;
        }

        public void setCorreo(String correo) {
            this.correo = correo;
        }

        public Integer getEstatus() {
            return estatus;
        }

        public void setEstatus(Integer estatus) {
            this.estatus = estatus;
        }

        public Integer getIdrol() {
            return idrol;
        }

        public void setIdrol(Integer idrol) {
            this.idrol = idrol;
        }

        public String getIdUsuario() {
            return idUsuario;
        }

        public void setIdUsuario(String idUsuario) {
            this.idUsuario = idUsuario;
        }

        public Boolean getInterno() {
            return interno;
        }

        public void setInterno(Boolean interno) {
            this.interno = interno;
        }

        public String getJwt() {
            return jwt;
        }

        public void setJwt(String jwt) {
            this.jwt = jwt;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getAcceso() {
            return acceso;
        }

        public void setAcceso(String acceso) {
            this.acceso = acceso;
        }
    }
}
