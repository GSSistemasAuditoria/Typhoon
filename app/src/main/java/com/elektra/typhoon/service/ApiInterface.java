package com.elektra.typhoon.service;

import com.elektra.typhoon.objetos.request.RequestCartera;
import com.elektra.typhoon.objetos.request.RequestLogin;
import com.elektra.typhoon.objetos.request.SincronizacionPost;
import com.elektra.typhoon.objetos.request.ValidaDatosRequest;
import com.elektra.typhoon.objetos.response.CatalogosTyphoonResponse;
import com.elektra.typhoon.objetos.response.CerrarSesionResponse;
import com.elektra.typhoon.objetos.response.DatosPorValidarResponse;
import com.elektra.typhoon.objetos.response.GenericResponseVO;
import com.elektra.typhoon.objetos.response.LoginLlaveMaestraVO;
import com.elektra.typhoon.objetos.response.ResponseCartera;
import com.elektra.typhoon.objetos.response.ResponseDescargaPdf;
import com.elektra.typhoon.objetos.response.ResponseLogin;
import com.elektra.typhoon.objetos.response.ResponseNotificaciones;
import com.elektra.typhoon.objetos.response.ResponseNuevoUsuario;
import com.elektra.typhoon.objetos.response.ResponseValidaUsuario;
import com.elektra.typhoon.objetos.response.SincronizacionResponse;

import java.util.List;

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
    Call<ResponseLogin> authenticate(@Header("X-IP_CLIENT") String ip, @Body RequestLogin requestLogin);

    @POST("GetCarteraRevisiones")
    Call<ResponseCartera> carteraRevisiones(@Header("X-IP_CLIENT") String ip, @Header("Authorization") String jwt, @Body RequestCartera requestCartera);

    @GET("ValidarUsuarioExterno")
    Call<ResponseValidaUsuario> validaUsuarioExterno(@Header("X-IP_CLIENT") String ip, @Header("Authorization") String jwt,@Query("correo") String correo);

    @GET("InsertaNuevoUsuario")
    Call<ResponseNuevoUsuario> insertarNuevoUsuario(@Header("X-IP_CLIENT") String ip, @Header("Authorization") String jwt,@Query("correo") String correo,@Query("password") String password);

    @POST("Sincronizar")
    Call<SincronizacionResponse> sincronizacion(@Header("X-IP_CLIENT") String ip, @Header("Authorization") String jwt,@Body SincronizacionPost sincronizacionPost);

    @GET("GetCatalogosThyphoon")
    Call<CatalogosTyphoonResponse> catalogosTyphoon(@Header("X-IP_CLIENT") String ip, @Header("Authorization") String jwt);
    //@POST("GetCatalogosThyphoon")
    //Call<CatalogosTyphoonResponse> catalogosTyphoon(@Header("Authorization") String jwt, RequestCatalogos requestCatalogos);

    @GET("DownloadInformePregunta")
    Call<ResponseDescargaPdf> descargaPDF(@Header("X-IP_CLIENT") String ip, @Header("Authorization") String jwt,@Query("idRevision") int idRevision,@Query("idPregunta") int idPregunta);

    @POST("GetDatosPorValidar")
    //Call<DatosPorValidarResponse> datosPorValidar(@Header("Authorization") String jwt, @Query("idRevision") int idRevision, @Query("idRol") int idRol);
    Call<DatosPorValidarResponse> datosPorValidar(@Header("X-IP_CLIENT") String ip, @Header("Authorization") String jwt, @Body ValidaDatosRequest validaDatosRequest);

    @GET("GetTranNotificaciones")
    Call<ResponseNotificaciones> getNotificaciones(@Header("Authorization") String jwt, @Query("idRol") int idRol);

    @GET("CerrarSesion")
    Call<CerrarSesionResponse> cerrarSesion(@Query("idUsuario") String idUsuario);

    @POST("IsUserSesion")
    Call<GenericResponseVO> validaSesion(@Body LoginLlaveMaestraVO loginLlaveMaestraVO);

    @POST("LoginoAuth")
    Call<ResponseLogin> loginLlaveMaestra(@Header("X-IP_CLIENT") String ip, @Body LoginLlaveMaestraVO loginLlaveMaestraVO);
}
