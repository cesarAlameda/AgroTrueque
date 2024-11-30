package com.csaralameda.agrotrueque.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.csaralameda.agrotrueque.MainActivity;
import com.csaralameda.agrotrueque.R;
import com.csaralameda.agrotrueque.Usuario;
import com.csaralameda.agrotrueque.databinding.FragmentPerfilBinding;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel perfilViewModel;
    private TextView[] TextViewArray;
    private static final int NPARAMUSARIO = 5;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        perfilViewModel = new ViewModelProvider(requireActivity()).get(PerfilViewModel.class);

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Usuario user = perfilViewModel.getUser();
        if (user != null) {
            TextViewArray = new TextView[NPARAMUSARIO];
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