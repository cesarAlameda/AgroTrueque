package com.csaralameda.agrotrueque.ui.anuncios;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.csaralameda.agrotrueque.DataService.RetrofitClient;
import com.csaralameda.agrotrueque.Interfaces.ApiService;
import com.csaralameda.agrotrueque.Logueo;
import com.csaralameda.agrotrueque.MainActivity;
import com.csaralameda.agrotrueque.R;
import com.csaralameda.agrotrueque.databinding.FragmentAnunciosBinding;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AnunciosFragment extends Fragment {

    private FragmentAnunciosBinding binding;
    private Button btnCrearAnuncio;
    private AnuncioFragment af;
    private Bitmap bitmap;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = FragmentAnunciosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        btnCrearAnuncio = binding.btnCrearAnuncio;

        btnCrearAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CrearAnuncio.class);
                startActivity(intent);
            }
        });
        af = (AnuncioFragment) getChildFragmentManager().findFragmentById(R.id.fragmentContainerView);
        Log.d("CARGOANUNCIOS","CARGO ANUNCIOS");
        cargarAnuncios();

        return root;
    }

    private void cargarAnuncios() {
        Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
        ApiService apiService = retrofit.create(ApiService.class);

        Call<JsonObject> call = apiService.obtenerAnuncios();
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

                            String estado = anuncioObj.get("estado").getAsString();
                            String urlfoto = anuncioObj.get("fotoAnuncio").getAsString();
                            int idUsuario = anuncioObj.get("idUsuario").getAsInt();
                            cargarfoto(idAnuncio,urlfoto,descripcion,localizacion,hora,estado,idUsuario);







                        }

                            af.actualizarAnuncios();
                    } else {
                        Toast.makeText(getContext(), "Error: " + response.body().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            private void cargarfoto(int idanuncio, String base64Image, String descripcion, String localizacion, String hora, String estado, int idUsuario) {
                try {
                    String base64Data = base64Image.substring(base64Image.indexOf(",") + 1);
                    byte[] decodedString = Base64.decode(base64Data, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    if (bitmap != null) {
                        Anuncio anuncio = new Anuncio(idanuncio, descripcion, localizacion, hora, estado, bitmap, idUsuario);
                        Anuncios.listanuncios.add(anuncio);
                    } else {
                        Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
                        Anuncio anuncio = new Anuncio(idanuncio, descripcion, localizacion, hora, estado, defaultBitmap, idUsuario);
                        Anuncios.listanuncios.add(anuncio);
                    }
                } catch (Exception e) {
                    Log.e("ImagenError", "Error decodificando imagen base64", e);
                    Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
                    Anuncio anuncio = new Anuncio(idanuncio, descripcion, localizacion, hora, estado, defaultBitmap, idUsuario);
                    Anuncios.listanuncios.add(anuncio);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarAnuncios();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}