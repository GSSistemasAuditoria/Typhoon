package com.elektra.typhoon.service;

import com.elektra.typhoon.objetos.request.RequestCartera;
import com.elektra.typhoon.objetos.request.RequestLogin;
import com.elektra.typhoon.objetos.request.SincronizacionPost;
import com.elektra.typhoon.objetos.request.ValidaDatosRequest;
import com.elektra.typhoon.objetos.response.CatalogosTyphoonResponse;
import com.elektra.typhoon.objetos.response.DatosPorValidarResponse;
import com.elektra.typhoon.objetos.response.ResponseCartera;
import com.elektra.typhoon.objetos.response.ResponseDescargaPdf;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.ResponseNotificaciones;
import com.elektra.typhoon.objetos.response.ResponseNuevoUsuario;
import com.elektra.typhoon.objetos.response.ResponseValidaUsuario;
import com.elektra.typhoon.objetos.response.SincronizacionResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    //@GET("ValidarEmpleado")
    //Call<ResponseLogin> authenticate(@Query("idUsuario") String usuario, @Query("password") String contrasena);
    @POST("ValidarEmpleado")
    Call<ResponseLogin> authenticate(@Body RequestLogin requestLogin);

    @POST("GetCarteraRevisiones")
    Call<ResponseCartera> carteraRevisiones(@Header("Authorization") String jwt, @Body RequestCartera requestCartera);

    @GET("ValidarUsuarioExterno")
    Call<ResponseValidaUsuario> validaUsuarioExterno(@Header("Authorization") String jwt,@Query("correo") String correo);

    @GET("InsertaNuevoUsuario")
    Call<ResponseNuevoUsuario> insertarNuevoUsuario(@Header("Authorization") String jwt,@Query("correo") String correo,@Query("password") String password);

    @POST("Sincronizar")
    Call<SincronizacionResponse> sincronizacion(@Header("Authorization") String jwt,@Body SincronizacionPost sincronizacionPost);

    @GET("GetCatalogosThyphoon")
    Call<CatalogosTyphoonResponse> catalogosTyphoon(@Header("Authorization") String jwt);

    @GET("DownloadInformePregunta")
    Call<ResponseDescargaPdf> descargaPDF(@Header("Authorization") String jwt,@Query("idPregunta") int idPregunta);

    @POST("GetDatosPorValidar")
    //Call<DatosPorValidarResponse> datosPorValidar(@Header("Authorization") String jwt, @Query("idRevision") int idRevision, @Query("idRol") int idRol);
    Call<DatosPorValidarResponse> datosPorValidar(@Header("Authorization") String jwt, @Body ValidaDatosRequest validaDatosRequest);

    @GET("GetTranNotificaciones")
    Call<ResponseNotificaciones> getNotificaciones(@Header("Authorization") String jwt, @Query("idRol") int idRol);
}
