package com.csaralameda.agrotrueque.Interfaces;


import com.google.gson.JsonObject;

import java.io.File;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
        //AQUI VAN LAS INTERFACES DE RETROFIT PARA PDOER HACER GET POST
        @FormUrlEncoded
        @POST("insertar_user.php")
        Call<JsonObject> registrarUser(
                @Field("nombreUsuario") String nombreUsuario,
                @Field("correoUsuario") String correoUsuario,
                @Field("password") String password,
                @Field("fotoUsuario") String fotoUsuario,
                @Field("tipo") String tipo
        );

        @FormUrlEncoded
        @POST("select_user.php")
        Call<JsonObject> selectuser(
                @Field("correoUsuario") String correoUsuario,
                @Field("password") String password
        );

        @Multipart
        @POST("subir_imagen.php")
        Call<JsonObject> subirfoto(
                @Part MultipartBody.Part file
        );





}
