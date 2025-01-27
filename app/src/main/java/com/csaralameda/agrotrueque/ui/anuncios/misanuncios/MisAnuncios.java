package com.csaralameda.agrotrueque.ui.anuncios.misanuncios;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.csaralameda.agrotrueque.DataService.RetrofitClient;
import com.csaralameda.agrotrueque.Interfaces.ApiService;
import com.csaralameda.agrotrueque.R;
import com.csaralameda.agrotrueque.UsuarioDataStore;
import com.csaralameda.agrotrueque.ui.anuncios.Anuncio;
import com.csaralameda.agrotrueque.ui.anuncios.AnuncioFragment;
import com.csaralameda.agrotrueque.ui.anuncios.Anuncios;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MisAnuncios extends AppCompatActivity {
    private miAnuncioFragment maf;
    private UsuarioDataStore usuarioDataStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_anuncios);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        maf = (miAnuncioFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView2);
        usuarioDataStore = usuarioDataStore.getInstance(this);
        cargarMisAnuncios();







    }

    @SuppressLint("CheckResult")
    private void cargarMisAnuncios() {


        Anuncios.listanuncios.clear();
        usuarioDataStore.getUser().subscribe(usuario -> {


            Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
            ApiService apiService = retrofit.create(ApiService.class);

            Call<JsonObject> call = apiService.selectmisanuncios(usuario.getIdUsuario());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String status = response.body().get("status").getAsString();

                        if ("success".equals(status)) {
                            JsonArray anunciosArray = response.body().getAsJsonArray("anuncios");
                            Anuncios.listanuncios.clear();

                            for (int i = 0; i < anunciosArray.size(); i++) {
                                JsonObject anuncioObj = anunciosArray.get(i).getAsJsonObject();
                                int idAnuncio=anuncioObj.get("idAnuncio").getAsInt();
                                String descripcion = anuncioObj.get("descripcion").getAsString();
                                String localizacion = anuncioObj.get("localizacion").getAsString();
                                String hora = anuncioObj.get("hora").getAsString();
                                String categoria = anuncioObj.get("categoria").getAsString();

                                String estado = anuncioObj.get("estado").getAsString();
                                String urlfoto = anuncioObj.get("fotoAnuncio").getAsString();
                                int idUsuario = anuncioObj.get("idUsuario").getAsInt();
                                cargarfoto(idAnuncio,urlfoto,descripcion,localizacion,hora,estado,categoria,idUsuario);







                            }

                            maf.actualizarAnuncios();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error: " + response.body().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                private void cargarfoto(int idanuncio, String base64Image, String descripcion, String localizacion, String hora, String estado,String categoria, int idUsuario) {
                    try {
                        String base64Data = base64Image.substring(base64Image.indexOf(",") + 1);
                        byte[] decodedString = Base64.decode(base64Data, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        if (bitmap != null) {
                            Anuncio anuncio = new Anuncio(idanuncio, descripcion, localizacion, hora, estado,categoria, bitmap, idUsuario);
                            Anuncios.listanuncios.add(anuncio);
                        } else {
                            Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
                            Anuncio anuncio = new Anuncio(idanuncio, descripcion, localizacion, hora, estado,categoria, defaultBitmap, idUsuario);
                            Anuncios.listanuncios.add(anuncio);
                        }
                    } catch (Exception e) {
                        Log.e("ImagenError", "Error decodificando imagen base64", e);
                        Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
                        Anuncio anuncio = new Anuncio(idanuncio, descripcion, localizacion, hora, estado,categoria, defaultBitmap, idUsuario);
                        Anuncios.listanuncios.add(anuncio);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        });



    }





    }
