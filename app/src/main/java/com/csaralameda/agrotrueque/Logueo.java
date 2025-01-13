package com.csaralameda.agrotrueque;




import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.csaralameda.agrotrueque.DataService.RetrofitClient;
import com.csaralameda.agrotrueque.Interfaces.ApiService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.fido.fido2.api.common.RequestOptions;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Field;

public class
Logueo extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    private String mensaje="";
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton btnGoogle;
    private TextView tvPassForget;
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
        tvPassForget=findViewById(R.id.tvPassForget);
        btnGoogle=findViewById(R.id.googleSignInButton);
        //DATOS PROVISIONALES:
        edMail.setText("twitt29@gmail.com");
        edPass.setText("Monodejungla23");





        //ALL LO RELACIONADO CON EL TEMA DE DARLE AL BOTON DE INICIO DE SESIÓN

        tvPassForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LOGICA DE CUANDO OLVIDAS EL CORERO CON EL CODIGO POR EMAIL ETC
                View dialogView= LayoutInflater.from(Logueo.this).inflate(R.layout.dialog_cambiar_password,null);
                Log.d("CAMBIARPASSWORD", "CONTRASEÑA");
                EditText etPassword=dialogView.findViewById(R.id.etPassword);
                TextView tvPass=dialogView.findViewById(R.id.tvPass);
                tvPass.setText("Introduce tu correo:");
                etPassword.setHint("correo");
                android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(Logueo.this);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //AQUI 1 COGER EL CORREO DEL ED DESPUES PASARLO POR RETROFIT AL PHP Y DESPUES MOSTRAR
                        String correo=etPassword.getText().toString();
                        generarCodigo(correo);






                    }

                    private void generarCodigo(String correo) {

                        Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
                        ApiService apiService = retrofit.create(ApiService.class);





                        Call<JsonObject> call = apiService.crearcodigo(correo);
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    String status = response.body().get("status").getAsString();
                                    mensaje = response.body().get("message").getAsString();

                                    if ("success".equals(status)) {
                                        Toast.makeText(Logueo.this, "CORREO ENVIADO", Toast.LENGTH_SHORT).show();
                                        View dialogView= LayoutInflater.from(Logueo.this).inflate(R.layout.dialog_olvidopassycodigo,null);
                                        Log.d("CAMBIARPASSWORD", "CONTRASEÑA");
                                        EditText etCod=dialogView.findViewById(R.id.etCod);
                                        EditText etnuevapass=dialogView.findViewById(R.id.etnewpass);

                                        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(Logueo.this);
                                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                    String cod=etCod.getText().toString();
                                                    String nuevapass=etnuevapass.getText().toString();
                                                    if(!validarCampos(correo, nuevapass)){

                                                        Toast.makeText(Logueo.this, "Contraseña no válida", Toast.LENGTH_SHORT).show();

                                                    }else{
                                                        comprobarCodigo(cod,nuevapass);
                                                    }




                                            }

                                            private void comprobarCodigo(String cod, String nuevapass) {
                                              //  @Field("email") String email,
                                                //  @Field("new_password") String new_password,
                                                // @Field("code") String code
                                                Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
                                                ApiService apiService = retrofit.create(ApiService.class);


                                              

                                                Call<JsonObject> call = apiService.cambiopassconcodigo(correo, nuevapass,cod);
                                                call.enqueue(new Callback<JsonObject>() {
                                                    @Override
                                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                                        if (response.isSuccessful() && response.body() != null) {
                                                            String status = response.body().get("status").getAsString();
                                                            mensaje = response.body().get("message").getAsString();

                                                            if ("success".equals(status)) {
                                                                Toast.makeText(Logueo.this, "CONTRASEÑA CAMBIADA CON ÉXITO", Toast.LENGTH_SHORT).show();
                                                                
                                                                
                                                            } else {

                                                                Toast.makeText(Logueo.this, "ERROR EN EL CÓDIGO", Toast.LENGTH_SHORT).show();
                                                         



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
                                        });
                                        builder.setView(dialogView);
                                        final android.app.AlertDialog dialog= builder.create();
                                        dialog.show();


                                        
                                    } else {

                                        Toast.makeText(Logueo.this, "CORREO NO EXISTE", Toast.LENGTH_SHORT).show();



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
                });
                builder.setView(dialogView);
                final android.app.AlertDialog dialog= builder.create();
                dialog.show();



            }
        });


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




        });



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogle.setOnClickListener(view -> {
            Log.d("GOOGLE", "LOGIN POR GOOGLE");
            signInWithGoogle();
        });



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
                    Usuario user = new Usuario();

                    if ("success".equals(status)) {
                        JsonObject responseBody = response.body();
                        JsonObject usuario = responseBody.getAsJsonObject("user");

                        user.setIdUsuario(usuario.get("idUsuario").getAsInt());
                        user.setValoracion(usuario.get("valoracion").getAsFloat());
                        user.setCorreoUsuario(usuario.get("correoUsuario").getAsString());
                        user.setnIntercambios(usuario.get("nIntercambios").getAsInt());
                        user.setnAnuncios(usuario.get("nAnuncios").getAsInt());
                        user.setNombreUsuario(usuario.get("nombreUsuario").getAsString());
                        user.setTipo(usuario.get("tipo").getAsString());

                        String fotoUrl = usuario.get("fotoUsuario").getAsString();

                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(fotoUrl)
                                .error(R.drawable.avatar)
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                                        if (bitmap != null) {
                                            Log.d("BitmapCarga", "Bitmap cargado correctamente");
                                            Log.d("BitmapDimensions", "Width: " + bitmap.getWidth() + ", Height: " + bitmap.getHeight());
                                            user.setFotoUsuario(bitmap);

                                            Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Logueo.this, MainActivity.class);
                                            intent.putExtra("logueado", true);

                                            if (user.getFotoUsuario() != null) {
                                                String rutaFoto = guardarImagenEnCache(user.getFotoUsuario());
                                                Log.d("IMAGENLOGUEO", "IMAGENGUARDADA");
                                                intent.putExtra("rutaFoto", rutaFoto);
                                                user.setFotoUsuario(null); // Evita pasar el Bitmap
                                            }
                                            intent.putExtra("user", (Parcelable) user);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Log.e("BitmapError", "Bitmap es nulo");
                                        }
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        // Si es necesario limpiar recursos, se hace aquí
                                    }

                                    @Override
                                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                        super.onLoadFailed(errorDrawable);
                                        String fotoBase64 = usuario.get("foto").getAsString();
                                        if (!"empty".equals(fotoBase64)) {
                                            try {
                                                if (fotoBase64.startsWith("data:image/png;base64,")) {
                                                    fotoBase64 = fotoBase64.replace("data:image/png;base64,", "");
                                                }

                                                byte[] decodedBytes = Base64.decode(fotoBase64, Base64.DEFAULT);

                                                Bitmap fotoBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                                                user.setFotoUsuario(fotoBitmap);
                                            } catch (Exception e) {
                                                Log.e("ERROR_FOTO", "Error convirtiendo foto", e);
                                                user.setFotoUsuario(BitmapFactory.decodeResource(getResources(), R.drawable.avatar));
                                            }
                                        }

                                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Logueo.this, MainActivity.class);
                                        intent.putExtra("logueado", true);

                                        if (user.getFotoUsuario() != null) {
                                            String rutaFoto = guardarImagenEnCache(user.getFotoUsuario());
                                            Log.d("IMAGENLOGUEO", "IMAGENGUARDADA");
                                            intent.putExtra("rutaFoto", rutaFoto);
                                            user.setFotoUsuario(null); // Evita pasar el Bitmap
                                        }
                                        intent.putExtra("user", (Parcelable) user);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
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


    private String guardarImagenEnCache(Bitmap bitmap) {
        try {
            File cacheDir = getApplicationContext().getCacheDir();
            File file = new File(cacheDir, "foto_usuario.png");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return file.getAbsolutePath();
        } catch (
                IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                insertarUsuario(account);

            } catch (
                    ApiException e) {
                Log.w("GOOGLE", "Google sign in failed", e);
                Toast.makeText(this, "Inicio de sesión de Google fallido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void insertarUsuario(GoogleSignInAccount account) {

            Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
            ApiService apiService = retrofit.create(ApiService.class);

            String username = account.getDisplayName();
            String email = account.getEmail();
            String password = account.getId();
            String photoUrl = account.getPhotoUrl().toString();

            String tipo="G";

            Call<JsonObject> call = apiService.registrarUser(username, email, password, photoUrl,tipo);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String status = response.body().get("status").getAsString();
                        mensaje = response.body().get("message").getAsString();

                        if ("success".equals(status)) {
                            Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Logueo.this, MainActivity.class);
                            intent.putExtra("logueado", true);
                            startActivity(intent);
                            finish();
                            Log.d("INSERTARENLOGUEO1",mensaje);
                        } else {

                            Log.d("INSERTARENLOGUEO2",mensaje);
                            //si el correo existe entra aqui
                            if(mensaje.equals("El correo electrónico ya está registrado")){
                                usuariologin(account.getEmail(), account.getId());
                            }



                        }
                    } else {
                        Log.d("INSERTARENLOGUEO3",mensaje);
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

    private boolean validarCampos(String correo, String pass) {

                // Validar correo electrónico
                if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
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