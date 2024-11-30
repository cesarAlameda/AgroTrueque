package com.csaralameda.agrotrueque;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.csaralameda.agrotrueque.databinding.ActivityMainBinding;
import com.csaralameda.agrotrueque.ui.perfil.PerfilViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    boolean logueado;
    public Usuario user;
    private PerfilViewModel perfilViewModel;
    private TextView[] TextViewArray;
    private static final int NPARAMUSARIO = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        logueado = getIntent().getBooleanExtra("logueado", false);
        if (!logueado) {
            Intent intent = new Intent(MainActivity.this, Logueo.class);
            startActivity(intent);
            finish();
        } else {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            user = getIntent().getParcelableExtra("user");
            if (user == null) {

                Intent intent = new Intent(MainActivity.this, Logueo.class);
                startActivity(intent);
                finish();
                return;
            }

            BottomNavigationView navView = findViewById(R.id.nav_view);
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, navController);

            perfilViewModel = new ViewModelProvider(this).get(PerfilViewModel.class);
            perfilViewModel.setUser(user);


        }
    }
}