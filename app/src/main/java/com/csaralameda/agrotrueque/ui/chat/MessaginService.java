package com.csaralameda.agrotrueque.ui.chat;

import android.util.Log;

import androidx.annotation.NonNull;

import com.csaralameda.agrotrueque.DataService.RetrofitClient;
import com.csaralameda.agrotrueque.Interfaces.ApiService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MessaginService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "Token: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d("FCM", "Message: " + message.getNotification().getBody());
    }

    public String getToken(int idUser) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    updateToken(idUser,token);
                    Log.d("FCM", "Token: " + token);
                });
        return null;
    }

    private void updateToken(int idUsuario, String token) {
        Log.d("iduser",idUsuario+"        "+token);
        Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
        ApiService apiService = retrofit.create(ApiService.class);
        Call<JsonObject> call = apiService.actualizarToken(idUsuario,token);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.d("UpdateSuccess", response.body().toString());

                } else {
                    // Manejar errores
                    Log.e("UpdateError", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("NetworkError", t.getMessage(), t);
            }
        });
    }
}
