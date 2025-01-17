package com.csaralameda.agrotrueque.Interfaces;


import com.google.gson.JsonObject;

import java.io.File;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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

        @FormUrlEncoded
        @POST("select_pass.php")
        Call<JsonObject> selectpass(
                @Field("idUsuario") int idUsuario

        );
        @FormUrlEncoded
        @POST("update_pass.php")
        Call<JsonObject> updatePass(
                @Field("idUsuario") int idUsuario,
                @Field("password") String password

        );

        @FormUrlEncoded
        @POST("creacodigos.php")
        Call<JsonObject> crearcodigo(
                @Field("email") String email
        );

        @FormUrlEncoded
        @POST("cambiopassconcodigo.php")
        Call<JsonObject> cambiopassconcodigo(
                @Field("email") String email,
                @Field("new_password") String new_password,
                @Field("code") String code
                );
        @FormUrlEncoded
        @POST("actualizar_usuario.php")
        Call<JsonObject> actualizarUser(
                @Field("idUsuario") int idUsuario,
                @Field("nombreUsuario") String nombreUsuario,
                @Field("correoUsuario") String correoUsuario,
                @Field("fotoUsuario") String fotoUsuario
        );

        @FormUrlEncoded
        @POST("insertar_anuncio.php")
        Call<JsonObject> insertarAnuncio(
                @Field("descripcion") String descripcion,
                @Field("localizacion") String localizacion,
                @Field("estado") String estado,
                @Field("fotoAnuncio") String fotoAnuncio,
                @Field("idUsuario") int idUsuario
        );

        @GET("select_anuncios.php")
        Call<JsonObject> obtenerAnuncios();

        @FormUrlEncoded
        @POST("select_anuncioid.php")
        Call<JsonObject> selectanuncioid(
                @Field("idAnuncio") int idAnuncio

        );

        @FormUrlEncoded
        @POST("selectmisanuncios.php")
        Call<JsonObject> selectmisanuncios(
                @Field("idUsuario") int idUsuario

        );

        @FormUrlEncoded
        @POST("deleteanuncio.php")
        Call<JsonObject> deleteanuncio(
                @Field("idAnuncio") int idAnuncio

        );
        @FormUrlEncoded
        @POST("actualizar_anuncio.php")
        Call<JsonObject> actualizarAnuncio(
                @Field("idAnuncio") int idAnuncio,
                @Field("descripcion") String descripcion,
                @Field("localizacion") String localizacion,
                @Field("fotoAnuncio") String fotoAnuncio
        );

        @FormUrlEncoded
        @POST("actualizar_token.php")
        Call<JsonObject> actualizarToken(
                @Field("idUsuario") int idUsuario,
                @Field("token") String token

        );

        @FormUrlEncoded
        @POST("enviarnotificacionnuevochat.php")
        Call<JsonObject> enviarnotificacionnuevochat(
                @Field("idUsuario") int idUsuario

        );


}
