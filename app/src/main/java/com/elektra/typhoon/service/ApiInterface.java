package com.elektra.typhoon.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("api/{usuario}/{contrasena}")
    Call<ResponseLogin> authenticate(@Path("usuario") String usuario, @Path("contrasena") String contrasena);

}
