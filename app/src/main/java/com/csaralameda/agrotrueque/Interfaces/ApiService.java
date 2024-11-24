package com.csaralameda.agrotrueque.Interfaces;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
        //AQUI VAN LAS INTERFACES DE RETROFIT PARA PDOER HACER GET POST
        @FormUrlEncoded
        @POST("insertar_user.php")
        Call<JsonObject> registrarUser(
                @Field("nombreUsuario") String nombreUsuario,
                @Field("correoUsuario") String correoUsuario,
                @Field("password") String password,
                @Field("fotoUsuario") String fotoUsuario
        );
}
