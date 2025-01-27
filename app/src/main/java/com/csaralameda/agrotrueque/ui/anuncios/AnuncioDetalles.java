package com.csaralameda.agrotrueque.ui.anuncios;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.csaralameda.agrotrueque.DataService.RetrofitClient;
import com.csaralameda.agrotrueque.Interfaces.ApiService;
import com.csaralameda.agrotrueque.Logueo;
import com.csaralameda.agrotrueque.MainActivity;
import com.csaralameda.agrotrueque.R;
import com.csaralameda.agrotrueque.Usuario;
import com.csaralameda.agrotrueque.UsuarioDataStore;
import com.csaralameda.agrotrueque.ui.chat.Chat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
public class AnuncioDetalles extends AppCompatActivity implements OnMapReadyCallback {
    private Anuncio anuncio;
    private ImageView ivFotoAnuncio;
    private TextView tvDescripcionContenido;
    private TextView tvHora;
    private ImageButton ibChat;
    private GoogleMap mMap;
    private String localizacion;
    private boolean mapaListo = false;
    private boolean anuncioCargado = false;
    private UsuarioDataStore usuarioDataStore;
    private Usuario u=new Usuario();
    private TextView textoEtiquetaChat;
    private Button botonEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_anuncio_detalles);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        usuarioDataStore = usuarioDataStore.getInstance(this);



        int idanuncio = getIntent().getIntExtra("anuncio", 0);
        Log.d("anuncio", idanuncio + " ");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cargarAnuncio(idanuncio);


        ivFotoAnuncio = findViewById(R.id.ivFotoAnuncio);
        tvDescripcionContenido = findViewById(R.id.tvDescripcionContenido);
        tvHora = findViewById(R.id.tvHora);
        ibChat = findViewById(R.id.ibChat);
        textoEtiquetaChat = findViewById(R.id.textoEtiquetaChat);
        botonEmail = findViewById(R.id.botonEmail);


        botonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{u.getCorreoUsuario()});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Anuncio Agrotrueque");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Te contacto por el anuncio: " + anuncio.getDescripcion());

                startActivity(Intent.createChooser(emailIntent, "Enviar por correo"));
            }
        });

        ibChat.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                usuarioDataStore.getUser().subscribe(usuario -> {

                    if(anuncio.getIdUsuario()==usuario.getIdUsuario()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AnuncioDetalles.this, "No puedes crear un chat en tu anuncio", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{

                        mandarnotificacion();

                        Intent intent = new Intent(AnuncioDetalles.this, Chat.class);
                        intent.putExtra("OTROUSER",anuncio.getIdUsuario());
                        startActivity(intent);


                    }


                });
            }
            private void mandarnotificacion() {
                Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
                ApiService apiService = retrofit.create(ApiService.class);
                Call<JsonObject> call = apiService.enviarnotificacionnuevochat(anuncio.getIdUsuario());

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            Log.d("notienviada", response.body().toString());
                            finish();
                        } else {
                            // Manejar errores
                            Log.e("notinoenviada", "Error: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e("NetworkError", t.getMessage(), t);
                    }
                });

            }
        });
    }

    private void cargarUsuarioAnuncio(int idUsuario) {
        Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
        ApiService apiService = retrofit.create(ApiService.class);

        Call<JsonObject> call = apiService.selectuseranuncio(idUsuario);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RESPUESTA_BODY", response.body().toString());
                    String status = response.body().get("status").getAsString();
                    String mensaje = response.body().get("message").getAsString();

                    if ("success".equals(status)) {
                        JsonObject responseBody = response.body();
                        JsonObject anuncioObj = responseBody.getAsJsonObject("user");


                        String nombreUsuario = anuncioObj.get("nombreUsuario").getAsString();
                        String correoUsuario = anuncioObj.get("correoUsuario").getAsString();
                        String token = anuncioObj.get("Token").getAsString();
                        int idUsuario = anuncioObj.get("idUsuario").getAsInt();

                        u.setIdUsuario(idUsuario);
                        u.setNombreUsuario(nombreUsuario);
                        u.setCorreoUsuario(correoUsuario);
                        textoEtiquetaChat.setText(u.getNombreUsuario());



                    } else {
                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error en la respuesta del servidor",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(),
                        "Error en la conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });






    }

    private void cargarAnuncio(int idanuncio) {
        Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
        ApiService apiService = retrofit.create(ApiService.class);

        Call<JsonObject> call = apiService.selectanuncioid(idanuncio);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RESPUESTA_BODY", response.body().toString());
                    String status = response.body().get("status").getAsString();
                    String mensaje = response.body().get("message").getAsString();

                    if ("success".equals(status)) {
                        JsonObject responseBody = response.body();
                        JsonObject anuncioObj = responseBody.getAsJsonObject("anuncio");

                        int idAnuncio = anuncioObj.get("idAnuncio").getAsInt();
                        String descripcion = anuncioObj.get("descripcion").getAsString();
                        localizacion = anuncioObj.get("localizacion").getAsString();
                        String hora = anuncioObj.get("hora").getAsString();
                        String estado = anuncioObj.get("estado").getAsString();
                        String categoria= anuncioObj.get("categoria").getAsString();
                        String urlfoto = anuncioObj.get("fotoAnuncio").getAsString();
                        int idUsuario = anuncioObj.get("idUsuario").getAsInt();
                        cargarUsuarioAnuncio(idUsuario);
                        cargarfoto(idAnuncio, urlfoto, descripcion, localizacion, hora, estado,categoria, idUsuario);
                    } else {
                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error en la respuesta del servidor",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(),
                        "Error en la conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarfoto(int idanuncio, String base64Image, String descripcion, String localizacion,
                            String hora, String estado, String categoria, int idUsuario) {
        try {
            String base64Data = base64Image.substring(base64Image.indexOf(",") + 1);
            byte[] decodedString = Base64.decode(base64Data, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            if (bitmap != null) {
                anuncio = new Anuncio(idanuncio, descripcion, localizacion, hora, estado,categoria, bitmap, idUsuario);
            } else {
                Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
                anuncio = new Anuncio(idanuncio, descripcion, localizacion, hora, estado,categoria, defaultBitmap, idUsuario);
            }

            ivFotoAnuncio.setImageBitmap(anuncio.getFotoAnuncio());
            tvDescripcionContenido.setText(descripcion);
            tvHora.setText(gethoraTiempo(hora));

            anuncioCargado = true;
            actualizarMapa();

        } catch (Exception e) {
            Log.e("ImagenError", "Error decodificando imagen base64", e);
            Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
            anuncio = new Anuncio(idanuncio, descripcion, localizacion, hora, estado,categoria, defaultBitmap, idUsuario);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mapaListo = true;
        actualizarMapa();
    }

    private void actualizarMapa() {
        if (mapaListo && anuncioCargado && anuncio != null && anuncio.getLocalizacion() != null) {
            showLocationOnMap(anuncio.getLocalizacion());
        }
    }

    public static String gethoraTiempo(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date past = sdf.parse(dateString);
            Date now = new Date();

            long timeInMillis = now.getTime() - past.getTime();

            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis);
            long hours = TimeUnit.MILLISECONDS.toHours(timeInMillis);
            long days = TimeUnit.MILLISECONDS.toDays(timeInMillis);
            long months = days / 30;


            if (minutes < 1) {
                return "Hace un momento";
            } else if (minutes == 1) {
                return "Hace 1 minuto";
            } else if (hours < 1) {
                return "Hace " + minutes + " minutos";
            } else if (hours == 1) {
                return "Hace 1 hora";
            } else if (days < 1) {
                return "Hace " + hours + " horas";
            } else if (days == 1) {
                return "Hace 1 día";
            } else if (months < 1) {
                return "Hace " + days + " días";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString; // Retorna el string original si hay error

        }
        return "";
    }

    private void showLocationOnMap(String cityName) {
        if (mMap == null) return;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(cityName, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(cityName));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f));
            } else {
                Toast.makeText(this, "No se encontró la ubicación: " + cityName, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e("MapError", "Error al localizar la ciudad", e);
            Toast.makeText(this, "Error al localizar la ciudad", Toast.LENGTH_SHORT).show();
        }
    }
}