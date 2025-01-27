package com.csaralameda.agrotrueque.ui.anuncios;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.csaralameda.agrotrueque.DataService.RetrofitClient;
import com.csaralameda.agrotrueque.Interfaces.ApiService;
import com.csaralameda.agrotrueque.UsuarioDataStore;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.csaralameda.agrotrueque.R;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CrearAnuncio extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Button guardarAnuncio;
    private LocationManager locManager;
    private EditText etDescripcion;
    private ImageView ivFotoAnuncio;
    private String nombreCiudad;
    private LatLng currentLocation;
    private Uri urimagen;
    private Bitmap bitmap;
    private boolean bitmapcambiado=false;
    private String horaactual;
    private UsuarioDataStore usuarioDataStore;
    private String fotourl;
    private boolean editando=false;
    private int idAnuncio;
    private Anuncio anuncio;
    private String localizacion;
    private Spinner spinner;
    String[] categorias = {"Vegetales", "Animales", "Piensos", "Maquinaria", "Otros"};
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String[]  PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private double latitud, longitud;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_anuncio);

        editando=getIntent().getBooleanExtra("Editando",false);
        idAnuncio=getIntent().getIntExtra("idAnuncio",0);
        nombreCiudad = "";
        latitud = 0.0;
        longitud = 0.0;


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (!arePermissionsGranted()) {
            requestLocationPermissions();
        } else {
            initializeMap();
        }


        usuarioDataStore = usuarioDataStore.getInstance(this);
        etDescripcion= findViewById(R.id.etDescripcion);
        guardarAnuncio = findViewById(R.id.guardarAnuncio);
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        ivFotoAnuncio= findViewById(R.id.ivFotoAnuncio);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        horaactual = String.format("%02d:%02d:%02d", hour, minute, second);
        if(editando){
            guardarAnuncio.setText("Guardar Cambios");
            cargaranuncio(idAnuncio);

        }
        ivFotoAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarPermisosCamaraGaleria();
            }
        });

        guardarAnuncio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(editando){
                    //hago update del anuncio


                        if(bitmapcambiado){
                            usuarioDataStore.getUser().subscribe(usuario -> {
                                subirfoto(bitmap,usuario.getIdUsuario());
                            });
                        }else{
                            sacarNombreCiudad();
                            actualizarAnuncio(etDescripcion.getText().toString(), sacarNombreCiudad(), fotourl);
                        }

                }else{
                    //registro el anuncio
                    if(etDescripcion.getText().toString()==null || etDescripcion.getText().toString().equals("")){
                        Toast.makeText(CrearAnuncio.this, "Descripción del producto incorrecta", Toast.LENGTH_SHORT).show();
                    }

                    if(!bitmapcambiado){
                        Toast.makeText(CrearAnuncio.this, "La imagen es obligatoria", Toast.LENGTH_SHORT).show();
                    }

                    nombreCiudad = sacarNombreCiudad();
                    // Hora basada en la ubicación
                    Log.d("NOMBRECIUDAD", nombreCiudad);
                    Log.d("HORA_ACTUAL", horaactual);
                    usuarioDataStore.getUser().subscribe(usuario -> {

                        subirfoto(bitmap,usuario.getIdUsuario());



                    });
                }


            }

            private void actualizarAnuncio(String desc, String loca, String fotourl) {
                Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
                ApiService apiService = retrofit.create(ApiService.class);
                Call<JsonObject> call = apiService.actualizarAnuncio(idAnuncio, desc, loca, fotourl, spinner.getSelectedItem().toString());

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            Log.d("UpdateSuccess", response.body().toString());
                            finish();
                        } else {
                            // Manejar errores
                            Log.e("UpdateError", "Error: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e("NetworkError", t.getMessage(), t);
                    }
                });
            }


            private void insertarAnuncio(int idUser) {

                Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
                ApiService apiService = retrofit.create(ApiService.class);

                // Variables con los datos del anuncio
                String descripcion = etDescripcion.getText().toString();
                String localizacion = nombreCiudad;
                String estado = "N";

                int idUsuario = idUser; // Obtén el ID del usuario según tu lógica

                Call<JsonObject> call = apiService.insertarAnuncio(descripcion, localizacion, estado, fotourl, idUsuario,spinner.getSelectedItem().toString());
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String status = response.body().get("status").getAsString();
                            String mensaje = response.body().get("message").getAsString();

                            if ("success".equals(status)) {
                                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ERROR", "Error: " + t.getMessage());
                    }
                });

            }

            private void subirfoto(Bitmap miBitmap , int idUser) {

                File file = bitmapToFile(getApplicationContext(), miBitmap, "foto_" + System.currentTimeMillis() + ".png");


                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);


                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
                ApiService apiService = retrofit.create(ApiService.class);

                Call<JsonObject> call = apiService.subirfoto(body);

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d("RESPUESTA_BODY", response.body().toString());

                            try {
                                String status = response.body().get("success").getAsBoolean() ? "success" : "error";
                                String mensaje = response.body().get("url").getAsString();


                                if ("success".equals(status)) {
                                    // Handle successful upload
                                    fotourl=mensaje;
                                    if(editando){
                                        //descripcion, localizacion, urlfoto
                                        actualizarAnuncio(etDescripcion.getText().toString(), sacarNombreCiudad(), fotourl);
                                    }else{
                                        insertarAnuncio(idUser);
                                    }


                                    Toast.makeText(getApplicationContext(), "Foto subida exitosamente: " + mensaje, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.e("SUBIRFOTO", "Error parsing response", e);
                                Toast.makeText(getApplicationContext(), "Error parseando respuesta", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                            Log.d("SUBIRFOTO", "error servidor");
                        }

                    }





                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ERROR", "Error: " + t.getMessage());
                    }
                });

            }

            public  File bitmapToFile(Context context, Bitmap bitmap, String fileName) {
                File file = new File(context.getExternalFilesDir(null), fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (
                        IOException e) {
                    e.printStackTrace();
                }
                return file;
            }
        });
    }

    private void cargaranuncio(int idAnuncio) {
        Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
        ApiService apiService = retrofit.create(ApiService.class);

        Call<JsonObject> call = apiService.selectanuncioid(idAnuncio);

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
                        String categoria = anuncioObj.get("categoria").getAsString();
                        fotourl = anuncioObj.get("fotoAnuncio").getAsString();
                        int idUsuario = anuncioObj.get("idUsuario").getAsInt();

                        cargarfoto(idAnuncio, fotourl, descripcion, localizacion, hora, estado,categoria, idUsuario);
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
                            String hora, String estado,String categoria, int idUsuario) {
        try {
            String base64Data = base64Image.substring(base64Image.indexOf(",") + 1);
            byte[] decodedString = Base64.decode(base64Data, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            if (localizacion!=null) {
                cargarUbicacion(localizacion);
            }
            if (bitmap != null) {
                anuncio = new Anuncio(idanuncio, descripcion, localizacion, hora, estado,categoria, bitmap, idUsuario);
            } else {
                Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
                anuncio = new Anuncio(idanuncio, descripcion, localizacion, hora, estado,categoria, defaultBitmap, idUsuario);
            }

            ivFotoAnuncio.setImageBitmap(anuncio.getFotoAnuncio());
            etDescripcion.setText(descripcion);




        } catch (Exception e) {
            Log.e("ImagenError", "Error decodificando imagen base64", e);
            Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
            anuncio = new Anuncio(idanuncio, descripcion, localizacion, hora, estado,categoria, defaultBitmap, idUsuario);
        }



}

    private void verificarPermisosCamaraGaleria() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // Solicitar permisos
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA}, 123);
            } else {
                // Si los permisos están concedidos, llamar a la función que abre la cámara
                alertElegir();
            }
        } else {
            // Para versiones anteriores a Android 13, verificamos los permisos antiguos
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // Solicitar permisos
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 123);
            } else {
                // Si los permisos están concedidos, llamar a la función que abre la cámara
                alertElegir();
            }
        }
    }
    private void alertElegir() {
        View dialogView= LayoutInflater.from(this).inflate(R.layout.dialog_camarageleria_layout,null);

        LinearLayout lcamara=dialogView.findViewById(R.id.linearCam);
        LinearLayout lgaleria=dialogView.findViewById(R.id.linearGallery);


        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog dialog= builder.create();


        lcamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCamara();
                dialog.dismiss();
            }
        });

        lgaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirGaleria();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void abrirCamara() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Nueva Imagen");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Capturada con la cámara");
        urimagen = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, urimagen);
        startActivityForResult(intent, 456);
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 789);
    }


    @SuppressLint("MissingPermission")
    private void initializeMap() {
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    mMap = googleMap;
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(CrearAnuncio.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        latitud = location.getLatitude();
                                        longitud = location.getLongitude();

                                        currentLocation = new LatLng(latitud, longitud);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                                        mMap.addMarker(new MarkerOptions()
                                                .position(currentLocation)
                                                .title("Mi ubicación"));
                                    }

                                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                        @Override
                                        public void onMapClick(@NonNull LatLng coordenadas) {
                                            mMap.clear();
                                            Log.d("CLICKENELMAPA", "clickenelmapa");
                                            MarkerOptions markerOptions = new MarkerOptions()
                                                    .position(coordenadas)
                                                    .title("Localización seleccionada")
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                            mMap.addMarker(markerOptions);

                                            latitud = coordenadas.latitude;
                                            longitud = coordenadas.longitude;
                                        }
                                    });
                                }
                            });
                }
            });
        }
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private boolean arePermissionsGranted() {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (areAllPermissionsGranted(grantResults)) {
                initializeMap();
            } else {
                Log.d("PERMISOSNODADOS", "Permisos no dados");
                requestLocationPermissions();
            }
        }
    }

    private boolean areAllPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            try {
                if (requestCode == 789 && data != null) {
                    urimagen = data.getData();
                }
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), urimagen);
                ivFotoAnuncio.setImageBitmap(bitmap);
                bitmapcambiado=true;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private String sacarNombreCiudad() {
        Location location = new Location("manual");
        location.setLatitude(latitud);
        location.setLongitude(longitud);
        List<Address> dir = Collections.emptyList();
        dir.clear();
        Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());

        if (location != null) {
            try {
                dir = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Address address = dir.get(0);
                return address.getLocality();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return "desconocido";
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void cargarUbicacion(String ciudad) {
        if (mMap == null) return;
        Log.d("cargoubi","CARGAR UBICACION");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(ciudad, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng location = new LatLng(address.getLatitude(), address.getLongitude());
                latitud=address.getLatitude();
                longitud=address.getLongitude();
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(ciudad));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f));

                if (anuncio != null) {
                    anuncio.setLocalizacion(ciudad);

                }
            } else {
                Toast.makeText(this, "No se encontró la ubicación: " + ciudad, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e("MapError", "Error al localizar la ciudad", e);
            Toast.makeText(this, "Error al localizar la ciudad", Toast.LENGTH_SHORT).show();
        }
    }
}