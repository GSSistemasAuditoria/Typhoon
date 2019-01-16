package com.elektra.typhoon.service;

import com.elektra.typhoon.objetos.request.RequestCartera;
import com.elektra.typhoon.objetos.response.ResponseCartera;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.ResponseNuevoUsuario;
import com.elektra.typhoon.objetos.response.ResponseValidaUsuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("ValidarEmpleado")
    Call<ResponseLogin> authenticate(@Query("idUsuario") String usuario, @Query("password") String contrasena);

    @POST("GetCarteraRevisiones")
    Call<ResponseCartera> carteraRevisiones(@Body RequestCartera requestCartera);

    @GET("ValidarUsuarioExterno")
    Call<ResponseValidaUsuario> validaUsuarioExterno(@Query("correo") String correo);

    @GET("InsertaNuevoUsuario")
    Call<ResponseNuevoUsuario> insertarNuevoUsuario(@Query("correo") String correo,@Query("password") String password);
}
