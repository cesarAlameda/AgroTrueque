package com.csaralameda.agrotrueque;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.csaralameda.agrotrueque.DataService.RetrofitClient;
import com.csaralameda.agrotrueque.Interfaces.ApiService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegistroUsuario extends Activity {

    private static final int NEDITEXT = 4;
    private EditText[] EditTextArray;
    private ImageView imgviewregistro;
    private Uri urimagen;
    private String mensaje;
    Bitmap bitmap;
    String fotourl="no hay foto";
    private boolean modoEditar=false;
    private boolean esdeGoogle=false;
    private Button btnRegistro;
    private UsuarioDataStore usuarioDataStore;
    private static final int GOOGLE_PERMISO = 10; //
    private TextView tvpassregistro;
    private TextView tvpassregistroconfir;
    private boolean bitmapcambiado=false;


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        mensaje = "";
        TextView tvInicio = findViewById(R.id.tvInicio);
        btnRegistro = findViewById(R.id.btnRegistro);
        imgviewregistro = findViewById(R.id.imageView9);
        tvpassregistro=findViewById(R.id.tvpassregistro);
        tvpassregistroconfir=findViewById(R.id.tvpassregistroconfir);



        // Array de EditTexts
        EditTextArray = new EditText[NEDITEXT];
        EditTextArray[0] = findViewById(R.id.EDuser);
        EditTextArray[1] = findViewById(R.id.edEmail);
        EditTextArray[2] = findViewById(R.id.edPass);
        EditTextArray[3] = findViewById(R.id.edPassconfirmado);

        modoEditar= getIntent().getBooleanExtra("modoEditar",false);
        if(modoEditar){
            modoeditarmetodo();
        }

        imgviewregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarPermisosCamaraGaleria();

            }
        });

        btnRegistro.setOnClickListener(v -> {

            if (modoEditar) {
                usuarioDataStore = usuarioDataStore.getInstance(this);

                usuarioDataStore.getUser().subscribe(usuario -> {
                    String nombreUsuario = EditTextArray[0].getText().toString();
                    String correoUsuario = EditTextArray[1].getText().toString();
                    Log.d("bitmap",bitmapcambiado+" ");
                    if (bitmapcambiado) {
                        subirfoto(bitmap);

                    } else {
                        actualizarUsuario(usuario.getIdUsuario(), nombreUsuario, correoUsuario, null);
                    }
                });
            }else{
                if (validarCampos()) {


                    if(bitmap!=null){
                        subirfoto(bitmap);
                    }else{
                        insertarUsuario();
                    }

                }
            }

        });


        if(!modoEditar){
            tvInicio.setOnClickListener(v -> {
                Intent intent = new Intent(RegistroUsuario.this, Logueo.class);
                startActivity(intent);
                finish();
            });
        }else{
            tvInicio.setVisibility(View.GONE);
        }

    }

    private void actualizarUsuario(int idUsuario, String nombreUsuario, String correoUsuario, String fotoUsuario) {
        Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
        ApiService apiService = retrofit.create(ApiService.class);

        Call<JsonObject> call;
        if (fotoUsuario != null) {
            call = apiService.actualizarUser(idUsuario, nombreUsuario, correoUsuario, fotoUsuario);
        } else {
            call = apiService.actualizarUser(idUsuario, nombreUsuario, correoUsuario, "N");
        }

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
                        Log.d("ERROR",mensaje);
                        finish();
                    }
                } else {
                    Log.d("ERROR","Error en la respuesta del servidor");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("ERROR",t.getMessage());
                ;
            }
        });
    }



    @SuppressLint("CheckResult")
    private void modoeditarmetodo() {


        btnRegistro.setText("Guardar cambios");
        usuarioDataStore = usuarioDataStore.getInstance(this);


        usuarioDataStore.getUser().subscribe(usuario -> {
            //aqui all lo relacionado con el usuario
            esdeGoogle=usuario.getTipo().equals("G");

            if(esdeGoogle){
                //si viene de google
                EditTextArray[1].setEnabled(false); //email desactivado
                EditTextArray[2].setVisibility(View.GONE); //contraseña
                EditTextArray[3].setVisibility(View.GONE); //confirmar contraseña

            }else{
                EditTextArray[2].setVisibility(View.GONE);
                EditTextArray[3].setVisibility(View.GONE);

            }

            EditTextArray[0].setText(usuario.getNombreUsuario());
            EditTextArray[1].setText(usuario.getCorreoUsuario());
            tvpassregistro.setVisibility(View.GONE);
            tvpassregistroconfir.setVisibility(View.GONE);


            if(usuario.getFotoUsuario()!=null){
                imgviewregistro.setImageBitmap(usuario.getFotoUsuario());
            }


        });


    }


    private boolean validarCampos() {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(EditTextArray[1].getText().toString()).matches()) {
            Toast.makeText(this, "El correo electrónico no es válido", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (EditTextArray[2].getText().toString().length() < 8) {
            Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!EditTextArray[2].getText().toString().matches(".*[A-Z].*")) {
            Toast.makeText(this, "La contraseña debe contener al menos una letra mayúscula", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!EditTextArray[2].getText().toString().matches(".*\\d.*")) {
            Toast.makeText(this, "La contraseña debe contener al menos un número", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!EditTextArray[2].getText().toString().equals(EditTextArray[3].getText().toString())) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void insertarUsuario() {
        Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
        ApiService apiService = retrofit.create(ApiService.class);

        String username = EditTextArray[0].getText().toString().trim();
        String email = EditTextArray[1].getText().toString().trim();
        String password = EditTextArray[2].getText().toString().trim();
        String tipo="N";


        Call<JsonObject> call = apiService.registrarUser(username, email, password, fotourl,tipo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String status = response.body().get("status").getAsString();
                    mensaje = response.body().get("message").getAsString();

                    if ("success".equals(status)) {
                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistroUsuario.this, MainActivity.class);
                        intent.putExtra("logueado", true);
                        startActivity(intent);
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

    private void verificarPermisosCamaraGaleria() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // Solicitar permisos
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA}, 123);
            } else {
                // Si los permisos están concedidos, llamar a la función que abre la cámara
                alertElegir();
            }
        } else {
            // Para versiones anteriores a Android 13, verificamos los permisos antiguos
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // Solicitar permisos
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 123);
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            try {
                if (requestCode == 789 && data != null) {
                    urimagen = data.getData();
                }
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), urimagen);
                imgviewregistro.setImageBitmap(bitmap);
                bitmapcambiado=true;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == GOOGLE_PERMISO) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

            } catch (
                    ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("GOOGLE", "Google sign in failed", e);
                Toast.makeText(this, "Inicio de sesión de Google fallido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void subirfoto(Bitmap miBitmap) {

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
                            //SUBO LA FOTO E INSERTO AL USUARIO
                            if(modoEditar){
                                usuarioDataStore.getUser().subscribe(usuario -> {
                                    String nombreUsuario = EditTextArray[0].getText().toString();
                                    String correoUsuario = EditTextArray[1].getText().toString();
                                    Log.d("hola",usuario.getIdUsuario()+ nombreUsuario+ correoUsuario+ fotourl);
                                    actualizarUsuario(usuario.getIdUsuario(), nombreUsuario, correoUsuario, fotourl);

                                });


                            }else{
                                insertarUsuario();
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
    public static File bitmapToFile(Context context, Bitmap bitmap, String fileName) {
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 123) {
            boolean permisosConcedidos = true;
            // Verificar si todos los permisos fueron concedidos
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    permisosConcedidos = false;
                    break;
                }
            }

            if (permisosConcedidos) {
                alertElegir();
            } else {
                Toast.makeText(this, "Los permisos son necesarios para usar la cámara", Toast.LENGTH_SHORT).show();

            }
        }
    }
}