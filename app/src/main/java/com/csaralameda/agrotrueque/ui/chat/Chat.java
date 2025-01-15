package com.csaralameda.agrotrueque.ui.chat;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.csaralameda.agrotrueque.R;



import android.view.View;
import android.widget.EditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private EditText inputField;
    private FloatingActionButton sendButton;
    private List<Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        // Inicializar vistas
        recyclerView = findViewById(R.id.messagesRecyclerView);
        inputField = findViewById(R.id.inputField);
        sendButton = findViewById(R.id.sendButton);

        // Configurar RecyclerView
        adapter = new MessageAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Configurar listener para enviar mensajes
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }


}