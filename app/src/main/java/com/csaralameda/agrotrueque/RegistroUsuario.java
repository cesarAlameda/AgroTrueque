package com.csaralameda.agrotrueque;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.Manifest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.csaralameda.agrotrueque.DataService.RetrofitClient;
import com.csaralameda.agrotrueque.Interfaces.ApiService;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegistroUsuario extends AppCompatActivity {
    //TODO:OPCION DE ELEGIR UNA FOTO DE GALERÍA (ACTUALMENTE NO FUNCIONA)
    private static final int FOTO = 1;
    private static final int GALERIA=2;
    private static final int NEDITEXT=4;
    private EditText[] EditTextArray;
    ImageView imgviewregistro;
    Uri urimagen;
    private String mensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Declaro variables
        mensaje="";
        TextView tvInicio=findViewById(R.id.tvInicio);
        Button btnRegistro=findViewById(R.id.btnRegistro);
        imgviewregistro=findViewById(R.id.imageView9);

        //array de editexts
        EditTextArray = new EditText[NEDITEXT];
        EditTextArray[0] = findViewById(R.id.EDuser);
        EditTextArray[1] = findViewById(R.id.edEmail);
        EditTextArray[2] = findViewById(R.id.edPass);
        EditTextArray[3] = findViewById(R.id.edPassconfirmado);




        imgviewregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarpedirpermisosCamaraGaleria();
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(validarCampos()){
                    insertarUsuario();
                }

            }
            private void insertarUsuario() {
                Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
                ApiService apiService = retrofit.create(ApiService.class);

                // Asumiendo que los EditTextArray están en este orden:
                // 0: username, 1: email, 2: password, 3: confirmed password
                String username = EditTextArray[0].getText().toString().trim();
                String email = EditTextArray[1].getText().toString().trim();
                String password = EditTextArray[2].getText().toString().trim();
                // Por ahora enviamos una cadena vacía para la foto
                String photoUrl = "";

                Call<JsonObject> call = apiService.registrarUser(
                        username,    // nombreUsuario
                        email,       // correoUsuario
                        password,    // password
                        photoUrl     // fotoUsuario
                );

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d("RESPUESTA_BODY", response.body().toString());
                            String status = response.body().get("status").getAsString();
                            mensaje = response.body().get("message").getAsString();

                            if ("success".equals(status)) {
                                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(RegistroUsuario.this, MainActivity.class);
                                intent.putExtra("logueado",true);
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


        tvInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegistroUsuario.this, Logueo.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private boolean validarCampos() {

        // Validar correo electrónico
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(EditTextArray[1].getText().toString()).matches()) {
            Toast.makeText(this, "El correo electrónico no es válido", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar contraseña
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

        if(!EditTextArray[2].getText().toString().equals(EditTextArray[3].getText().toString())){
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }




        return true;


    }



    private void verificarpedirpermisosCamaraGaleria() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
            //abrircam()
            alertElegir();

        } else {
            //pregunto permisos (añadir galería)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, FOTO);
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
                abrircam();
                dialog.dismiss();
            }
        });
        
        lgaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirgaleria();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void abrircam() {
      //FUNCIONA EN EMULADOR
      ContentValues values =new ContentValues();
      values.put(MediaStore.Images.Media.TITLE,"Título");
      values.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion");

      urimagen=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
      Intent intent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      intent.putExtra(MediaStore.EXTRA_OUTPUT,urimagen);
      camara.launch(intent);


    }

    private ActivityResultLauncher<Intent> camara=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode() == Activity.RESULT_OK){
                        imgviewregistro.setImageURI(urimagen);
                    }else{
                        Toast.makeText(RegistroUsuario.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void abrirgaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galeria.launch(intent);
    }
    private ActivityResultLauncher<Intent> galeria = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imagenSeleccionada = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagenSeleccionada);
                            imgviewregistro.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(RegistroUsuario.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegistroUsuario.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FOTO || requestCode==GALERIA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED  ) {
                alertElegir();
            }else{
                Toast.makeText(this, "No se han otorgado permisos", Toast.LENGTH_SHORT).show();
            }
        }
    }

}