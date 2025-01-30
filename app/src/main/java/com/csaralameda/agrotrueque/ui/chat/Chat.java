package com.csaralameda.agrotrueque.ui.chat;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.csaralameda.agrotrueque.DataService.RetrofitClient;
import com.csaralameda.agrotrueque.Interfaces.ApiService;
import com.csaralameda.agrotrueque.R;
import com.csaralameda.agrotrueque.Usuario;
import com.csaralameda.agrotrueque.UsuarioDataStore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.csaralameda.agrotrueque.ui.chat.Message;
import java.util.Date;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Chat extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private EditText inputField;
    private FloatingActionButton sendButton;
    private List<Message> messages = new ArrayList<>();
    private UsuarioDataStore usuarioDataStore;
    private Handler handler = new Handler();
    private Runnable messageRunnable;
    private TextView tvNombreuser;
    private ImageView imgview;
    private int currentUserId;
    private int otherUserId; // ID of the user you're chatting with
    private Usuario u=new Usuario();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        usuarioDataStore = UsuarioDataStore.getInstance(this);

        recyclerView = findViewById(R.id.messagesRecyclerView);
        inputField = findViewById(R.id.inputField);
        sendButton = findViewById(R.id.sendButton);
        otherUserId = getIntent().getIntExtra("OTROUSER", -1);
        cargarUsuarioAnuncio(otherUserId);
        tvNombreuser = findViewById(R.id.userNameTextView);
        imgview= findViewById(R.id.ivUsuario);

        // Get current user
        usuarioDataStore.getUser().subscribe(usuario -> {
            currentUserId = usuario.getIdUsuario();



            adapter = new MessageAdapter(messages, currentUserId);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            messageRunnable = new Runnable() {
                @Override
                public void run() {
                    retrieveMessages(0);
                    handler.postDelayed(this, 2000);
                }
            };

            handler.post(messageRunnable);
        });


        sendButton.setOnClickListener(v -> {
            String messageContent = inputField.getText().toString().trim();
            if (!messageContent.isEmpty()) {
                sendMessage(currentUserId, otherUserId, messageContent);
                inputField.setText("");
            }
        });
    }
    private void cargarUsuarioAnuncio(int idUsuario) {
        Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
        ApiService apiService = retrofit.create(ApiService.class);

        Call<JsonObject> call = apiService.selectuseranuncio(idUsuario);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RESPUESTA_BODY", response.body().toString());
                    String status = response.body().get("status").getAsString();
                    String mensaje = response.body().get("message").getAsString();

                    if ("success".equals(status)) {
                        JsonObject responseBody = response.body();
                        JsonObject anuncioObj = responseBody.getAsJsonObject("user");

                        String nombreUsuario = anuncioObj.get("nombreUsuario").getAsString();
                        String correoUsuario = anuncioObj.get("correoUsuario").getAsString();
                        String fotoruluser = anuncioObj.get("fotoUsuario").getAsString();
                        String token = anuncioObj.get("Token").getAsString();
                        int idUsuario = anuncioObj.get("idUsuario").getAsInt();

                        if (fotoruluser != null && !fotoruluser.isEmpty()) {
                            Glide.with(Chat.this)  // Usar Chat.this en lugar de getApplicationContext()
                                    .load(fotoruluser)
                                    .placeholder(R.drawable.perfil)
                                    .error(R.drawable.perfil)
                                    .into(new CustomTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable drawable,
                                                                    @Nullable Transition<? super Drawable> transition) {
                                            imgview.setImageDrawable(drawable);
                                            Log.d("IMAGE_LOADING", "Image loaded successfully");
                                        }

                                        @Override
                                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                            super.onLoadFailed(errorDrawable);
                                            Log.e("IMAGE_LOADING", "Failed to load image");
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {
                                            imgview.setImageDrawable(placeholder);
                                        }
                                    });
                        } else {
                            Log.e("IMAGE_LOADING", "Image URL is null or empty");
                        }

                        u.setIdUsuario(idUsuario);
                        u.setNombreUsuario(nombreUsuario);
                        u.setCorreoUsuario(correoUsuario);
                        tvNombreuser.setText(u.getNombreUsuario());

                    } else {
                        Toast.makeText(Chat.this, mensaje, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Chat.this,
                            "Error en la respuesta del servidor",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(Chat.this,
                        "Error en la conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sendMessage(int senderId, int receiverId, String content) {
        Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
        ApiService apiService = retrofit.create(ApiService.class);

        Call<JsonObject> call = apiService.sendMessage(senderId, receiverId, content);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject result = response.body();
                    if (result.get("status").getAsString().equals("success")) {
                        int messageId = result.get("messageId").getAsInt();
                        String timestamp = result.get("timestamp").getAsString();

                        // Create and add message to local list
                        Message sentMessage = new Message(
                                messageId,
                                senderId,
                                receiverId,
                                content,
                                parseTimestamp(timestamp),
                                false
                        );
                        messages.add(sentMessage);
                        adapter.notifyItemInserted(messages.size() - 1);
                        recyclerView.scrollToPosition(messages.size() - 1);
                    } else {
                        showErrorToast("Failed to send message");
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                showErrorToast("Network error");
            }
        });
    }

    private void retrieveMessages(int lastMessageId) {
        Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
        ApiService apiService = retrofit.create(ApiService.class);

        Call<JsonObject> call = apiService.retrieveMessages(currentUserId, otherUserId,
                lastMessageId > 0 ? lastMessageId : (messages.isEmpty() ? 0 : messages.get(messages.size() - 1).getMessageId())
        );
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject result = response.body();
                    if (result.get("status").getAsString().equals("success")) {
                        JsonArray messagesArray = result.getAsJsonArray("messages");
                        List<Message> newMessages = new ArrayList<>();

                        for (int i = 0; i < messagesArray.size(); i++) {
                            JsonObject messageObject = messagesArray.get(i).getAsJsonObject();
                            int messageId = messageObject.get("messageId").getAsInt();

                            if (messages.stream().noneMatch(m -> m.getMessageId() == messageId)) {
                                Message message = new Message(
                                        messageId,
                                        messageObject.get("senderId").getAsInt(),
                                        messageObject.get("receiverId").getAsInt(),
                                        messageObject.get("content").getAsString(),
                                        parseTimestamp(messageObject.get("timestamp").getAsString()),
                                        messageObject.get("read").getAsBoolean()
                                );
                                newMessages.add(message);
                            }
                        }

                        if (!newMessages.isEmpty()) {
                            int startPosition = messages.size();
                            messages.addAll(newMessages);

                            adapter.notifyItemRangeInserted(startPosition, newMessages.size());
                            recyclerView.scrollToPosition(messages.size() - 1);
                        }

                        markMessagesAsRead();
                    } else {
                        showErrorToast("Failed to retrieve messages");
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                showErrorToast("Network error");
            }
        });
    }

    private void markMessagesAsRead() {
        Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
        ApiService apiService = retrofit.create(ApiService.class);

        Call<JsonObject> call = apiService.markMessagesRead(currentUserId, otherUserId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject result = response.body();
                    if (result.get("status").getAsString().equals("success")) {
                        int updatedMessages = result.get("updatedMessages").getAsInt();
                        if (updatedMessages > 0) {
                            for (Message message : messages) {
                                if (message.getSenderId() == otherUserId) {
                                    message.setRead(true);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Silent failure, as this is not critical
            }
        });
    }

    private Date parseTimestamp(String timestamp) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateFormat.parse(timestamp);
        } catch (ParseException e) {
            return new Date();
        }
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}