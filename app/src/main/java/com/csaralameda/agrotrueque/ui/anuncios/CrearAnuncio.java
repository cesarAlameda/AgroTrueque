package com.csaralameda.agrotrueque.ui.anuncios;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.csaralameda.agrotrueque.R;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CrearAnuncio extends AppCompatActivity {
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Button guardarAnuncio;
    private LocationManager locManager;
    private EditText etDescripcion;
    private ImageView ivFotoAnuncio;
    private String nombreCiudad;
    private LatLng currentLocation;
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

        guardarAnuncio = findViewById(R.id.guardarAnuncio);
        guardarAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombreCiudad = sacarNombreCiudad();
                Log.d("NOMBRECIUDAD", nombreCiudad);
                Anuncios.listanuncios.add(new Anuncio(1, "Tomates barato", nombreCiudad, "13:22", "hola",
                        BitmapFactory.decodeResource(v.getResources(), R.drawable.avatar), 2));
                finish();
            }
        });
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

    private String sacarNombreCiudad() {
        Location location = new Location("manual");
        location.setLatitude(latitud);
        location.setLongitude(longitud);

        Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());

        if (location != null) {
            try {
                List<Address> dir = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Address address = dir.get(0);
                return address.getLocality();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return "desconocido";
    }
}