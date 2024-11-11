package com.csaralameda.agrotrueque;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Logueo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logueo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //declaracion de variables
        Button btnInicio=findViewById(R.id.btnInicio);
        TextView tvRegistro=findViewById(R.id.TVregistro);




        //ALL LO RELACIONADO CON EL TEMA DE DARLE AL BOTON DE INICIO DE SESIÓN

        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent para cuando le doy al botn de inicio de sesion iniciando la main activity (login correcto)
                Intent intent = new Intent(Logueo.this, MainActivity.class);
                intent.putExtra("logueado", true);
                startActivity(intent);
                finish();
            }
        });


        //ALL LO RELACIONADO CON EL REGISTRARSE
        tvRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Logueo.this, RegistroUsuario.class);
                startActivity(intent);
                finish();
            }
        });




    }
}