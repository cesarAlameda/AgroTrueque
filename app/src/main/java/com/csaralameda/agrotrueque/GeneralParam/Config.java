package com.csaralameda.agrotrueque.GeneralParam;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.csaralameda.agrotrueque.Logueo;
import com.csaralameda.agrotrueque.MainActivity;
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
                            Intent intent = new Intent(Config.this, Logueo.class);
                            intent.putExtra("logueado", false);
                            startActivity(intent);
                            finish();

                            break;
                        case 3:
                            // Acción para ELIMINAR CUENTA
                            Log.d("ELIMINAR_CUENTA", "ELIMINAR CUENTA");

                            alertdialogborrar();



                            break;
                    }
                }
            });
        }








    }

    private void alertdialogborrar() {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(Html.fromHtml("Escriba <i>'Eliminar cuenta'</i> para eliminar la cuenta", Html.FROM_HTML_MODE_COMPACT));

            // Crear un EditText para entrada numérica
            final EditText input = new EditText(this);
            //input.setHint();


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