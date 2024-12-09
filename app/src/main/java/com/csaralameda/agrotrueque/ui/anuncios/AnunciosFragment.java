package com.csaralameda.agrotrueque.ui.anuncios;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.csaralameda.agrotrueque.databinding.FragmentAnunciosBinding;

public class AnunciosFragment extends Fragment {

    private FragmentAnunciosBinding binding;
    private Button btnCrearAnuncio;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        AnunciosViewModel anunciosViewModel =
                new ViewModelProvider(this).get(AnunciosViewModel.class);

        binding = FragmentAnunciosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        for (int i = 0; i < 20; i++) {
            Anuncio a=new Anuncio(1,"hola","ny","10:20","cerrado","a",2);
            Anuncios.listanuncios.add(a);
        }

        btnCrearAnuncio=binding.btnCrearAnuncio;

        btnCrearAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(v.getContext(), CrearAnuncio.class);
                startActivity(intent);




            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}