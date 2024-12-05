package com.csaralameda.agrotrueque.GeneralParam;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.csaralameda.agrotrueque.R;

public class Config extends AppCompatActivity {
    private static final int NLAYOUT = 4;
    private LinearLayout[] LinearLayoutArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_config);

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
                            Log.d("CAMBIARPASSWORD", "CONTRASEÑA");





                            break;
                        case 1:
                            // Acción para notificaciones
                            Log.d("NOTIFICACIONES", "NOTIFICACIONES");




                            break;
                        case 2:
                            // Acción para CERRAR SESIÓN
                            Log.d("CERRAR_SESIÓN", "CERRAR SESION");



                            break;
                        case 3:
                            // Acción para ELIMINAR CUENTA
                            Log.d("ELIMINAR_CUENTA", "ELIMINAR CUENTA");



                            break;
                    }
                }
            });
        }








    }
}