package com.csaralameda.agrotrueque;

import android.content.Intent;
import android.os.Bundle;

import com.csaralameda.agrotrueque.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.csaralameda.agrotrueque.MainActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    //TODO:QUE AL INICIAR LA APP POR PRIMERA VEZ ME CARGE EL REGISTRARSE O LOGUEARSE
    //TODO: SI TE REGISTRAS QUE CREE UN NUEVO USUARIO CON NOMBRE DE USUARIO, CORREO, INTERCAMBIOS, NANUNCIOS, VALORACION, PASSWORD
    boolean logueado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        logueado = getIntent().getBooleanExtra("logueado", false);

        if (!logueado) {
            Intent intent = new Intent(MainActivity.this, Logueo.class);
            startActivity(intent);
            finish();
        }




        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);






    }

}