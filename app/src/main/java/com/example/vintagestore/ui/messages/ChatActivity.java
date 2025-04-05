package com.example.vintagestore.ui.messages;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vintagestore.R;
import com.example.vintagestore.adapter.MessageAdapter;
import com.example.vintagestore.data.Repository;
import com.example.vintagestore.model.Message;
import com.example.vintagestore.model.User;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imgProfile;
    private TextView textUsername;
    private RecyclerView recyclerView;
    private EditText editMessage;
    private ImageButton btnSend;
    
    private MessageAdapter adapter;
    private Repository repository;
    private long currentUserId;
    private long otherUserId;
    private User otherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        // Get user ID from intent
        otherUserId = getIntent().getLongExtra("userId", -1);
        if (otherUserId == -1) {
            finish();
            return;
        }
        
        // Get current user ID from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserId = sharedPreferences.getLong("currentUserId", -1);
        if (currentUserId == -1) {
            finish();
            return;
        }
        
        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        imgProfile = findViewById(R.id.img_profile);
        textUsername = findViewById(R.id.text_username);
        recyclerView = findViewById(R.id.recycler_messages);
        editMessage = findViewById(R.id.edit_message);
        btnSend = findViewById(R.id.btn_send);
        
        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // Initialize repository
        repository = new Repository(getApplication());
        
        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(this, new ArrayList<>(), currentUserId);
        recyclerView.setAdapter(adapter);
        
        // Load user data
        loadUser();
        
        // Load messages
        loadMessages();
        
        // Set up send button
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }
    
    private void loadUser() {
        repository.getUserById(otherUserId).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    otherUser = user;
                    textUsername.setText(user.getUsername());
                    
                    // Load profile image if available
                    if (user.getProfileImageUri() != null && !user.getProfileImageUri().isEmpty()) {
                        Glide.with(ChatActivity.this)
                                .load(user.getProfileImageUri())
                                .placeholder(R.drawable.ic_profile)
                                .circleCrop()
                                .into(imgProfile);
                    } else {
                        imgProfile.setImageResource(R.drawable.ic_profile);
                    }
                }
            }
        });
    }
    
    private void loadMessages() {
        repository.getConversation(currentUserId, otherUserId).observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                if (messages != null) {
                    adapter.updateData(messages);
                    
                    if (!messages.isEmpty()) {
                        recyclerView.smoothScrollToPosition(messages.size() - 1);
                        
                        // Mark messages as read
                        for (Message message : messages) {
                            if (message.getReceiverId() == currentUserId && !message.isRead()) {
                                message.setRead(true);
                                repository.updateMessage(message);
                            }
                        }
                    }
                }
            }
        });
    }
    
    private void sendMessage() {
        String messageText = editMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            Message message = new Message(currentUserId, otherUserId, messageText, 0);
            repository.insertMessage(message);
            
            // Clear input field
            editMessage.setText("");
        }
    }
}
