package com.example.vintagestore.ui.messages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.vintagestore.R;
import com.example.vintagestore.data.Repository;
import com.example.vintagestore.model.Message;
import com.example.vintagestore.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textEmpty;
    private Repository repository;
    private long currentUserId;
    private Map<Long, ChatPreviewAdapter.ChatPreview> chatPreviews = new HashMap<>();

    private ChatPreviewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_messages);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        textEmpty = view.findViewById(R.id.text_empty);
        
        // Initialize repository
        repository = new Repository(requireActivity().getApplication());
        
        // Get current user ID from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        currentUserId = sharedPreferences.getLong("currentUserId", -1);
        
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatPreviewAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);
        
        // Set up chat click listener
        adapter.setOnChatClickListener(new ChatPreviewAdapter.OnChatClickListener() {
            @Override
            public void onChatClick(ChatPreviewAdapter.ChatPreview chatPreview) {
                openChat(chatPreview.getUserId());
            }
        });
        
        // Set up swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadChats();
            }
        });
        
        // Load chats
        loadChats();
        
        return view;
    }
    
    private void loadChats() {
        chatPreviews.clear();
        
        // Get all users the current user has conversations with
        repository.getConversationPartners(currentUserId).observe(getViewLifecycleOwner(), new Observer<List<Long>>() {
            @Override
            public void onChanged(List<Long> userIds) {
                if (userIds != null && !userIds.isEmpty()) {
                    for (Long userId : userIds) {
                        loadChatPreview(userId);
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    textEmpty.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }
    
    private void loadChatPreview(long userId) {
        // Get user information
        repository.getUserById(userId).observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    ChatPreviewAdapter.ChatPreview preview = new ChatPreviewAdapter.ChatPreview();
                    preview.setUserId(user.getId());
                    preview.setUsername(user.getUsername());
                    preview.setProfileImageUri(user.getProfileImageUri());
                    
                    chatPreviews.put(user.getId(), preview);
                    
                    // Get last message
                    loadLastMessage(user.getId());
                }
            }
        });
        
        // Get unread message count
        repository.getUnreadMessagesCount(userId).observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer count) {
                if (chatPreviews.containsKey(userId)) {
                    ChatPreviewAdapter.ChatPreview preview = chatPreviews.get(userId);
                    preview.setUnreadCount(count);
                    updateAdapter();
                }
            }
        });
    }
    
    private void loadLastMessage(long userId) {
        // Get the conversation between current user and this user
        repository.getConversation(currentUserId, userId).observe(getViewLifecycleOwner(), new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                if (messages != null && !messages.isEmpty()) {
                    // Get the most recent message
                    Message lastMessage = messages.get(messages.size() - 1);
                    
                    if (chatPreviews.containsKey(userId)) {
                        ChatPreviewAdapter.ChatPreview preview = chatPreviews.get(userId);
                        preview.setLastMessage(lastMessage.getContent());
                        preview.setTimestamp(lastMessage.getTimestamp());
                        updateAdapter();
                    }
                }
            }
        });
    }
    
    private void updateAdapter() {
        List<ChatPreviewAdapter.ChatPreview> previewList = new ArrayList<>(chatPreviews.values());
        
        // Sort by timestamp (most recent first)
        previewList.sort((o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));
        
        adapter.updateData(previewList);
        
        if (!previewList.isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            textEmpty.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            textEmpty.setVisibility(View.VISIBLE);
        }
        
        swipeRefreshLayout.setRefreshing(false);
    }
    
    private void openChat(long userId) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Reload chats when fragment becomes visible again
        loadChats();
    }
    
    // Inner adapter class for chat previews
    public static class ChatPreviewAdapter extends RecyclerView.Adapter<ChatPreviewAdapter.ChatViewHolder> {
        
        private Context context;
        private List<ChatPreview> chatList;
        private OnChatClickListener listener;
        
        public interface OnChatClickListener {
            void onChatClick(ChatPreview chatPreview);
        }
        
        public ChatPreviewAdapter(Context context, List<ChatPreview> chatList) {
            this.context = context;
            this.chatList = chatList;
        }
        
        public void setOnChatClickListener(OnChatClickListener listener) {
            this.listener = listener;
        }
        
        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
            return new ChatViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            ChatPreview chat = chatList.get(position);
            
            holder.textUsername.setText(chat.getUsername());
            holder.textLastMessage.setText(chat.getLastMessage());
            
            // Format and display time
            SimpleDateFormat dateFormat;
            Date messageDate = new Date(chat.getTimestamp());
            Date now = new Date();
            
            if (DateUtils.isToday(chat.getTimestamp())) {
                // Today, show time
                dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            } else if (now.getTime() - chat.getTimestamp() < 7 * 24 * 60 * 60 * 1000) {
                // Within last week, show day of week
                dateFormat = new SimpleDateFormat("EEE", Locale.getDefault());
            } else {
                // Older, show date
                dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
            }
            
            holder.textTime.setText(dateFormat.format(messageDate));
            
            // Show unread count if any
            if (chat.getUnreadCount() > 0) {
                holder.textUnreadCount.setVisibility(View.VISIBLE);
                holder.textUnreadCount.setText(String.valueOf(chat.getUnreadCount()));
            } else {
                holder.textUnreadCount.setVisibility(View.GONE);
            }
            
            // Load profile image
            if (chat.getProfileImageUri() != null && !chat.getProfileImageUri().isEmpty()) {
                Glide.with(context)
                        .load(chat.getProfileImageUri())
                        .placeholder(R.drawable.ic_profile)
                        .circleCrop()
                        .into(holder.imgProfile);
            } else {
                holder.imgProfile.setImageResource(R.drawable.ic_profile);
            }
            
            // Handle item click
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onChatClick(chat);
                    }
                }
            });
        }
        
        @Override
        public int getItemCount() {
            return chatList.size();
        }
        
        public void updateData(List<ChatPreview> newChats) {
            this.chatList = newChats;
            notifyDataSetChanged();
        }
        
        static class ChatViewHolder extends RecyclerView.ViewHolder {
            ImageView imgProfile;
            TextView textUsername;
            TextView textLastMessage;
            TextView textTime;
            TextView textUnreadCount;
            
            ChatViewHolder(@NonNull View itemView) {
                super(itemView);
                imgProfile = itemView.findViewById(R.id.img_profile);
                textUsername = itemView.findViewById(R.id.text_username);
                textLastMessage = itemView.findViewById(R.id.text_last_message);
                textTime = itemView.findViewById(R.id.text_time);
                textUnreadCount = itemView.findViewById(R.id.text_unread_count);
            }
        }
        
        public static class ChatPreview {
            private long userId;
            private String username;
            private String profileImageUri;
            private String lastMessage;
            private long timestamp;
            private int unreadCount;
            
            public ChatPreview() {
                this.timestamp = System.currentTimeMillis();
                this.unreadCount = 0;
            }
            
            // Getters and Setters
            public long getUserId() {
                return userId;
            }
            
            public void setUserId(long userId) {
                this.userId = userId;
            }
            
            public String getUsername() {
                return username;
            }
            
            public void setUsername(String username) {
                this.username = username;
            }
            
            public String getProfileImageUri() {
                return profileImageUri;
            }
            
            public void setProfileImageUri(String profileImageUri) {
                this.profileImageUri = profileImageUri;
            }
            
            public String getLastMessage() {
                return lastMessage;
            }
            
            public void setLastMessage(String lastMessage) {
                this.lastMessage = lastMessage;
            }
            
            public long getTimestamp() {
                return timestamp;
            }
            
            public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }
            
            public int getUnreadCount() {
                return unreadCount;
            }
            
            public void setUnreadCount(int unreadCount) {
                this.unreadCount = unreadCount;
            }
        }
    }
}
