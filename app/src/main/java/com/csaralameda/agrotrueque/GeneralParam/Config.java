package com.csaralameda.agrotrueque.GeneralParam;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewKt;


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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Config extends AppCompatActivity {
    private static final int NLAYOUT = 4;
    private LinearLayout[] LinearLayoutArray;
    private GoogleSignInClient mGoogleSignInClient;
    private UsuarioDataStore usuarioDataStore;
    private String pass;
    private String nuevapass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_config);
        nuevapass="";
        //CARGO LOS DATOS ALMACENADOS EN EL OBJETO
        usuarioDataStore = usuarioDataStore.getInstance(this);



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        LinearLayoutArray=new LinearLayout[NLAYOUT];

        LinearLayoutArray[0] = findViewById(R.id.cambiarPassword);
        LinearLayoutArray[1] = findViewById(R.id.notificaciones);
        LinearLayoutArray[2] = findViewById(R.id.cerrarsesion);
        LinearLayoutArray[3] = findViewById(R.id.eliminarcuenta);


        for (int i = 0; i < NLAYOUT; i++) {
            final int index = i;
            LinearLayoutArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (index) {
                        case 0:
                            // Acción para cambiar la contraseña


                            //AL SER EL SUBSCRIBE ASÍNCRONO ALL LO RELATIVO AL USUARIO TENDRÁ QUE SEGUIR ESTE ESQUEMA PUESTO
                            //QUE SI NO DARÁ UN PUNTERO NULO YA QUE EL USUARIO NO HABRÁ ACABADO DE CARGAR
                            usuarioDataStore.getUser()
                                    .subscribe(usuario -> {

                                        if(!usuario.getTipo().equals("G")){


                                            runOnUiThread(() -> {

                                                View dialogView= LayoutInflater.from(Config.this).inflate(R.layout.dialog_cambiar_password,null);
                                                Log.d("CAMBIARPASSWORD", "CONTRASEÑA");
                                                EditText etPassword=dialogView.findViewById(R.id.etPassword);
                                                android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(Config.this);
                                                builder.setView(dialogView);
                                                obtenerPass(usuario.getIdUsuario());

                                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if(!etPassword.getText().toString().equals(pass)){
                                                            Log.d("TEXTOYPASS",etPassword.getText().toString()+" "+pass);
                                                            Toast.makeText(Config.this, "contraseña incorrecta", Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Toast.makeText(Config.this, "contraseña correcta", Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();

                                                            View dialogView2= LayoutInflater.from(Config.this).inflate(R.layout.dialog_cambiar_password,null);
                                                            EditText etPassword2=dialogView2.findViewById(R.id.etPassword);
                                                            TextView tvPass=dialogView2.findViewById(R.id.tvPass);
                                                            tvPass.setText("Introduce tu nueva contraseña:");
                                                            android.app.AlertDialog.Builder builder2=new android.app.AlertDialog.Builder(Config.this);

                                                            builder2.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    nuevapass=etPassword2.getText().toString();
                                                                    //php prueba para cambiar al contraseña ya esta hecho,
                                                                    // antes de cambiar la contraseña hay que comprobar si cumple con los requisitos
                                                                    if(!validarcampos(nuevapass)){
                                                                        Toast.makeText(Config.this, "La contraseña que has introducido no cumple con los requisitos", Toast.LENGTH_SHORT).show();

                                                                    }else{

                                                                        actualizarpass();


                                                                    }

                                                                }

                                                                private void actualizarpass() {
                                                                    Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
                                                                    ApiService apiService = retrofit.create(ApiService.class);

                                                                    Call<JsonObject> call = apiService.updatePass(
                                                                            usuario.getIdUsuario(),
                                                                            nuevapass

                                                                    );

                                                                    call.enqueue(new Callback<JsonObject>() {
                                                                        @Override
                                                                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                                                            String mensaje="";

                                                                            if (response.isSuccessful() && response.body() != null) {
                                                                                Log.d("RESPUESTA_BODY", response.body().toString());
                                                                                String status = response.body().get("status").getAsString();
                                                                                mensaje = response.body().get("message").getAsString();


                                                                                if ("success".equals(status)) {
                                                                                    Toast.makeText(Config.this, "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show();


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

                                                                private boolean validarcampos(String pass) {
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




                                                            });
                                                            builder2.setView(dialogView2);
                                                            final android.app.AlertDialog dialog2= builder2.create();
                                                            dialog2.show();

                                                        }


                                                    }
                                                });

                                                final android.app.AlertDialog dialog= builder.create();
                                                dialog.show();






                                            });




                                        }else{
                                            //UN TOAST TIENE QUE IR EN EL HILO DE LA USER INTERFACE PORQUE SI NO DARÁ ERROR
                                            runOnUiThread(() -> {
                                                Toast.makeText(Config.this, "No puedes cambiar la contraseña de tu cuenta de google desde aquí", Toast.LENGTH_SHORT).show();
                                            });

                                        }


                                    });







                            break;
                        case 1:
                            // Acción para notificaciones
                            Log.d("NOTIFICACIONES", "NOTIFICACIONES");




                            break;
                        case 2:
                            // Acción para CERRAR SESIÓN
                            Log.d("CERRAR_SESIÓN", "CERRAR SESION");
                            cerrarSesiondegoogleogeneral();
                            break;

                        case 3:
                            // Acción para ELIMINAR CUENTA
                            Log.d("ELIMINAR_CUENTA", "ELIMINAR CUENTA");

                            alertdialogborrar();



                            break;
                    }
                }




                private void obtenerPass(int idUsuario) {

                    Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
                    ApiService apiService = retrofit.create(ApiService.class);

                    Call<JsonObject> call = apiService.selectpass(
                            idUsuario
                    );

                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            String mensaje="";

                            if (response.isSuccessful() && response.body() != null) {
                                Log.d("RESPUESTA_BODY", response.body().toString());
                                String status = response.body().get("status").getAsString();
                                mensaje = response.body().get("message").getAsString();


                                if ("success".equals(status)) {
                                     pass = response.body().get("password").getAsString();


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



                private void cerrarSesiondegoogleogeneral() {

                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Config.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(Config.this, Logueo.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Config.this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });
        }








    }

    private void alertdialogborrar() {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(Html.fromHtml("Escriba <i>'Eliminar Cuenta'</i> para eliminar la cuenta", Html.FROM_HTML_MODE_COMPACT));

            final EditText input = new EditText(this);


            builder.setView(input);

            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String pass = input.getText().toString();
                    if(pass.equals("Eliminar Cuenta")){
                        Toast.makeText(Config.this, "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Config.this, "Texto incorrecto", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                }
            });

            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }



}