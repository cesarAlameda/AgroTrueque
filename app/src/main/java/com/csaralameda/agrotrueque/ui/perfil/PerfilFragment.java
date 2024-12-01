package com.csaralameda.agrotrueque.ui.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.csaralameda.agrotrueque.MainActivity;
import com.csaralameda.agrotrueque.PDFvista;
import com.csaralameda.agrotrueque.R;
import com.csaralameda.agrotrueque.Usuario;
import com.csaralameda.agrotrueque.databinding.FragmentPerfilBinding;

import java.io.File;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel perfilViewModel;
    private TextView[] TextViewArray;
    private LinearLayout[] LinearLayoutArray;
    private static final int NPARAMUSARIO = 5;
    private static final int NLAYOUT = 4;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        perfilViewModel = new ViewModelProvider(requireActivity()).get(PerfilViewModel.class);

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Usuario user = perfilViewModel.getUser();
        if (user != null) {
            TextViewArray = new TextView[NPARAMUSARIO];
            LinearLayoutArray=new LinearLayout[NLAYOUT];

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

                                break;
                            case 1:
                                // Acción para lyConfig
                                Log.d("CONFIGURAR", "CONFIGURAR");

                                break;
                            case 2:
                                // Acción para lyPrivacidad
                                Log.d("PRIVACIDAD", "PRIVACIDAD");
                                Intent intent = new Intent(v.getContext(), PDFvista.class);
                                startActivity(intent);

                                break;
                            case 3:
                                // Acción para lyAyuda
                                Log.d("AYUDA", "AYUDA");



                                break;
                        }
                    }
                });
            }
            TextViewArray[0] = binding.tvNombreuser;
            TextViewArray[1] = binding.tvCorreo;
            TextViewArray[2] = binding.tvNtrueques;
            TextViewArray[3] = binding.tvValoracion;
            TextViewArray[4] = binding.tvNanuncios;

            TextViewArray[0].setText(user.getNombreUsuario());
            TextViewArray[1].setText(user.getCorreoUsuario());
            TextViewArray[2].setText(String.valueOf(user.getnIntercambios()));
            TextViewArray[3].setText("   " + user.getValoracion() + "⭐");
            TextViewArray[4].setText(String.valueOf(user.getnAnuncios()));
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}