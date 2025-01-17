package com.csaralameda.agrotrueque.ui.perfil;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.csaralameda.agrotrueque.RegistroUsuario;
import com.csaralameda.agrotrueque.Usuario;
import com.csaralameda.agrotrueque.databinding.FragmentPerfilBinding;
import com.csaralameda.agrotrueque.GeneralParam.*;
import com.csaralameda.agrotrueque.ui.anuncios.misanuncios.MisAnuncios;


public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel perfilViewModel;
    private TextView[] TextViewArray;
    private LinearLayout[] LinearLayoutArray;
    private static final int NPARAMUSARIO = 5;
    private static final int NLAYOUT = 4;
    private ImageView imUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        perfilViewModel = new ViewModelProvider(requireActivity()).get(PerfilViewModel.class);

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Usuario user = perfilViewModel.getUser();
        if (user != null) {
            imUser = binding.imUser;
            imUser.setImageBitmap(user.getFotoUsuario());
            TextViewArray = new TextView[NPARAMUSARIO];
            LinearLayoutArray=new LinearLayout[NLAYOUT];
            LinearLayout linear2=binding.linearLayout2;
            linear2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), MisAnuncios.class);
                    startActivity(intent);


                }
            });


            LinearLayoutArray[0] = binding.lyEditarPerfil;
            LinearLayoutArray[1] = binding.lyConfig;
            LinearLayoutArray[2] = binding.lyPrivacidad;
            LinearLayoutArray[3] = binding.lyAyuda;

            for (int i = 0; i < NLAYOUT; i++) {
                final int index = i;
                LinearLayoutArray[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (index) {
                            case 0:
                                // Acción para lyEditarPerfil
                                Log.d("EDITARPERFIL", "EDITAR PERFIL");

                                    //si el usurario no es de google podrá editar también el correo electrónico
                                    Log.d("PERFILFRAGMENTLOGINEDITAR", "SE HA LOGUEADO NORMAL");

                                    Intent intentRegistro = new Intent(v.getContext(), RegistroUsuario.class);
                                    intentRegistro.putExtra("modoEditar",true);
                                    startActivity(intentRegistro);

                                break;
                            case 1:
                                // Acción para lyConfig
                                Log.d("CONFIGURAR", "CONFIGURAR");

                                Intent intentConfig = new Intent(v.getContext(), Config.class);
                                startActivity(intentConfig);



                                break;
                            case 2:
                                // Acción para lyPrivacidad
                                Log.d("PRIVACIDAD", "PRIVACIDAD");
                                Intent intentPriv = new Intent(v.getContext(), PDFvista.class);
                                startActivity(intentPriv);

                                break;
                            case 3:
                                // Acción para lyAyuda
                                Log.d("AYUDA", "AYUDA");
                               // https://es.stackoverflow.com/questions/11542/c%C3%B3mo-enviar-correo-directamente-desde-android
                                Toast.makeText(v.getContext(), "Contáctanos", Toast.LENGTH_SHORT).show();
                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "soporteagrotrueque@gmail.com", null));
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Android APP - ");
                                startActivity(Intent.createChooser(emailIntent, "Enviar por correo"));


                                break;
                        }
                    }



                });
            }
            TextViewArray[0] = binding.tvNombreuser;
            TextViewArray[1] = binding.tvCorreo;
            /**
            TextViewArray[2] = binding.tvNtrueques;
            TextViewArray[3] = binding.tvValoracion;
            TextViewArray[4] = binding.tvNanuncios;**/

            TextViewArray[0].setText(user.getNombreUsuario());
            TextViewArray[1].setText(user.getCorreoUsuario());
            /**
            TextViewArray[2].setText(String.valueOf(user.getnIntercambios()));
            TextViewArray[3].setText("   " + user.getValoracion() + "⭐");
            TextViewArray[4].setText(String.valueOf(user.getnAnuncios()));**/
        }

        return root;
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}