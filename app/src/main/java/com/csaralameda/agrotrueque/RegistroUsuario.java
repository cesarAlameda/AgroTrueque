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

import java.io.IOException;

public class RegistroUsuario extends AppCompatActivity {
    //TODO:OPCION DE ELEGIR UNA FOTO DE GALERÍA (ACTUALMENTE NO FUNCIONA)
    private static final int FOTO = 1;
    private static final int GALERIA=2;
    ImageView imgviewregistro;
    Uri urimagen;

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
        TextView tvInicio=findViewById(R.id.tvInicio);
        Button btnRegistro=findViewById(R.id.btnRegistro);
        imgviewregistro=findViewById(R.id.imageView9);





        imgviewregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarpedirpermisosCamaraGaleria();
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegistroUsuario.this, "Registrado", Toast.LENGTH_SHORT).show();
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