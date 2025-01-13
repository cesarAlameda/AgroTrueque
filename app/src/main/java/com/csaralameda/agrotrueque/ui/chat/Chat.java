package com.csaralameda.agrotrueque.ui.chat;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.csaralameda.agrotrueque.R;


import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.csaralameda.agrotrueque.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private EditText inputField;
    private FloatingActionButton sendButton;
    private List<Message> messages = new ArrayList<>();
    private FirebaseFirestore db;
    private String currentUserId;
    private String chatPartnerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Obtener el ID del usuario con quien se está chateando
        chatPartnerId = getIntent().getStringExtra("chat_partner_id");
        if (chatPartnerId == null) {
            Toast.makeText(this, "Error: No se pudo iniciar el chat", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar vistas
        recyclerView = findViewById(R.id.messagesRecyclerView);
        inputField = findViewById(R.id.inputField);
        sendButton = findViewById(R.id.sendButton);

        // Configurar RecyclerView
        adapter = new MessageAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Configurar listener para enviar mensajes
        sendButton.setOnClickListener(v -> sendMessage());

        // Escuchar mensajes nuevos
        listenForMessages();
    }

    private void sendMessage() {
        String messageText = inputField.getText().toString().trim();
        if (messageText.isEmpty()) return;

        Message message = new Message(currentUserId, chatPartnerId, messageText);

        // Guardar mensaje en Firestore
        db.collection("chats")
                .document(getChatRoomId())
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    inputField.setText("");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(Chat.this, "Error al enviar mensaje", Toast.LENGTH_SHORT).show()
                );
    }

    private void listenForMessages() {
        db.collection("chats")
                .document(getChatRoomId())
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(Chat.this, "Error al cargar mensajes", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    messages.clear();
                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Message message = doc.toObject(Message.class);
                            if (message != null) {
                                message.setMessageId(doc.getId());
                                messages.add(message);
                            }
                        }
                        adapter.updateMessages(messages);
                        recyclerView.scrollToPosition(messages.size() - 1);
                    }
                });
    }

    private String getChatRoomId() {
        // Crear un ID único para la sala de chat ordenando los IDs de usuario
        return currentUserId.compareTo(chatPartnerId) < 0
                ? currentUserId + "_" + chatPartnerId
                : chatPartnerId + "_" + currentUserId;
    }
}