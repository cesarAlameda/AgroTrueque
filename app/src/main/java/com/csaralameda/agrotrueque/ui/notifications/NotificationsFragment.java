package com.csaralameda.agrotrueque.ui.notifications;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.csaralameda.agrotrueque.DataService.RetrofitClient;
import com.csaralameda.agrotrueque.Interfaces.ApiService;
import com.csaralameda.agrotrueque.R;
import com.csaralameda.agrotrueque.UsuarioDataStore;
import com.csaralameda.agrotrueque.databinding.FragmentNotificationsBinding;
import com.csaralameda.agrotrueque.ui.chat.ChatObj;
import com.csaralameda.agrotrueque.ui.chat.ChatsObjs;
import com.csaralameda.agrotrueque.ui.chat.chatobjFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private UsuarioDataStore usuarioDataStore;
    private chatobjFragment cho;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //llenar listachats
        cho = (chatobjFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragmentContainerView3);



        usuarioDataStore = usuarioDataStore.getInstance(getContext());
        usuarioDataStore.getUser()
                .subscribe(usuario -> {        cargarChats(usuario.getIdUsuario());
                });





        return root;
    }

    private void cargarChats(int idUser) {


            Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
            ApiService apiService = retrofit.create(ApiService.class);

            Call<JsonObject> call = apiService.retrieveChats(idUser);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject result = response.body();
                        if (result.get("status").getAsString().equals("success")) {
                            JsonArray chatsArray = result.getAsJsonArray("chats");
                            Log.d("CARGO_CHATS","CARGANDO LOS CHATS");
                            ChatsObjs.listachats.clear(); // Limpia la lista antes de agregar nuevos datos

                            for (int i = 0; i < chatsArray.size(); i++) {
                                JsonObject chatObject = chatsArray.get(i).getAsJsonObject();

                                int idUsuario = chatObject.get("userId").getAsInt();
                                String nombreUser = chatObject.get("userName").getAsString();
                                String userPhotoUrl = chatObject.get("userPhoto").getAsString().trim();
                                String ultimoMensaje = chatObject.get("lastMessage").getAsString();
                                String ultimoMensajeHora = chatObject.get("lastMessageTimestamp").getAsString();

                                // Descargar la foto del usuario (si es necesario, puedes omitir este paso)
                                Bitmap fotoUser = null;

                                // Crear un objeto ChatObj y añadirlo a la lista
                                ChatObj chat = new ChatObj(idUsuario, nombreUser, fotoUser, ultimoMensaje, ultimoMensajeHora);
                                ChatsObjs.listachats.add(chat);
                            }

                                cho.updateVista();
                        } else {
                            Log.d("ERROR","Error al obtener los chats");
                        }
                    } else {
                        Log.d("ERROR","Error en la respuesta del servidor");
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.d("ERROR","Error de red: " + t.getMessage());
                }
            });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}