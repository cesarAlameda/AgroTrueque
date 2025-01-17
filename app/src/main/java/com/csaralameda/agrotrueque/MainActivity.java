package com.csaralameda.agrotrueque;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.csaralameda.agrotrueque.DataService.RetrofitClient;
import com.csaralameda.agrotrueque.Interfaces.ApiService;
import com.csaralameda.agrotrueque.databinding.ActivityMainBinding;
import com.csaralameda.agrotrueque.ui.anuncios.AnunciosViewModel;
import com.csaralameda.agrotrueque.ui.chat.MessaginService;
import com.csaralameda.agrotrueque.ui.perfil.PerfilViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    boolean logueado;
    public Usuario user;
    private PerfilViewModel perfilViewModel;
    private AnunciosViewModel anunciosViewModel;
    private UsuarioDataStore usuarioDataStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //IMPORTANTE INICIALIZAR EL usuarioDataStore para que no de punteros nulos
        usuarioDataStore = usuarioDataStore.getInstance(this);
        FirebaseApp.initializeApp(this);


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

            String rutaFoto = getIntent().getStringExtra("rutaFoto");
            if (rutaFoto != null) {
                Bitmap fotoBitmap = BitmapFactory.decodeFile(rutaFoto);
                user.setFotoUsuario(fotoBitmap);
            }
            usuarioDataStore.guardarUsuario(user);


            BottomNavigationView navView = findViewById(R.id.nav_view);
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_perfil, R.id.navigation_anuncios, R.id.navigation_notifications)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
         //   NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, navController);

            perfilViewModel = new ViewModelProvider(this).get(PerfilViewModel.class);
            anunciosViewModel = new ViewModelProvider(this).get(AnunciosViewModel.class);
            anunciosViewModel.setUser(user);
            perfilViewModel.setUser(user);

            //RECARGO LA VISTA PARA QUE ESTÉ CON LOS DATOS GARGADOS
            navController.navigate(R.id.navigation_perfil);

            MessaginService messagingService = new MessaginService();
            messagingService.getToken(user.getIdUsuario());




        }
    }



}