package com.csaralameda.agrotrueque;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.csaralameda.agrotrueque.DataService.RetrofitClient;
import com.csaralameda.agrotrueque.Interfaces.ApiService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Logueo extends AppCompatActivity {
    private String mensaje="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_logueo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //declaracion de variables
        Button btnInicio=findViewById(R.id.btnInicio);
        TextView tvRegistro=findViewById(R.id.TVregistro);
        EditText edMail=findViewById(R.id.edMailLog);
        EditText edPass=findViewById(R.id.edPassLog);

        //DATOS PROVISIONALES:
        edMail.setText("twitt29@gmail.com");
        edPass.setText("Monodejungla23");





        //ALL LO RELACIONADO CON EL TEMA DE DARLE AL BOTON DE INICIO DE SESIÓN

        tvRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Logueo.this, RegistroUsuario.class);
                startActivity(intent);
                finish();
            }
        });



        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validarCampos(edMail.getText().toString(), edPass.getText().toString())){


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            usuariologin(edMail.getText().toString(), edPass.getText().toString());
                        }
                    }).start();

                }


            }



            private void usuariologin(String correo, String pass) {

                Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
                ApiService apiService = retrofit.create(ApiService.class);


                Call<JsonObject> call = apiService.selectuser(
                        correo.trim(),
                        pass.trim()
                );


                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            Log.d("RESPUESTA_BODY", response.body().toString());
                            String status = response.body().get("status").getAsString();
                            mensaje = response.body().get("message").getAsString();
                            Usuario user=new Usuario();
                            if ("success".equals(status)) {
                               // int idUsuario, String password, Float valoracion, int nAnuncios,
                               // Bitmap fotoUsuario, String nombreUsuario, String correoUsuario, int nIntercambios
                                /**   "user": {
                                    "idUsuario": 10,
                                            "fotoUsuario": "",
                                            "nombreUsuario": "twitt",
                                            "correoUsuario": "twitt29@gmail.com",
                                            "nIntercambios": 0,
                                            "nAnuncios": 0,
                                            "valoracion": 0**/
                                JsonObject responseBody = response.body();
                                JsonObject usuario = responseBody.getAsJsonObject("user");
                                user.setIdUsuario(usuario.get("idUsuario").getAsInt());
                                user.setValoracion(usuario.get("valoracion").getAsFloat());
                                user.setCorreoUsuario(usuario.get("correoUsuario").getAsString());
                                user.setnIntercambios(usuario.get("nIntercambios").getAsInt());
                                user.setnAnuncios(usuario.get("nAnuncios").getAsInt());
                                user.setNombreUsuario(usuario.get("nombreUsuario").getAsString());

                                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Logueo.this, MainActivity.class);
                                intent.putExtra("logueado", true);
                                intent.putExtra("user", (Parcelable) user);
                                startActivity(intent);
                                finish();
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
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        Log.e("ERROR", "Error: " + t.getMessage());
                    }
                });
            }
        });


    }

            private boolean validarCampos(String correo, String pass) {

                // Validar correo electrónico
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                    Toast.makeText(getApplicationContext(), "El correo electrónico no es válido", Toast.LENGTH_SHORT).show();
                    return false;
                }

                // Validar contraseña
                if (pass.length() < 8) {
                    Toast.makeText(getApplicationContext(), "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (!pass.matches(".*[A-Z].*")) {
                    Toast.makeText(getApplicationContext(), "La contraseña debe contener al menos una letra mayúscula", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (!pass.matches(".*\\d.*")) {
                    Toast.makeText(getApplicationContext(), "La contraseña debe contener al menos un número", Toast.LENGTH_SHORT).show();
                    return false;
                }

                return true;
            }
}