package com.example.vintagestore.ui.messages;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vintagestore.R;
import com.example.vintagestore.data.Repository;
import com.example.vintagestore.model.Item;
import com.example.vintagestore.model.Message;
import com.example.vintagestore.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * RecyclerView adapter for displaying chat previews in the messages list.
 */
public class ChatPreviewAdapter extends RecyclerView.Adapter<ChatPreviewAdapter.ChatPreviewViewHolder> {
    
    private final Context context;
    private List<Message> conversationPreviews;
    private final LayoutInflater inflater;
    private final SimpleDateFormat dateFormat;
    private final String currentUserId;
    
    // Cache for user and item data to avoid repeated database lookups
    private final Map<String, User> userCache = new HashMap<>();
    private final Map<String, Item> itemCache = new HashMap<>();
    
    // Repository for accessing data
    private final Repository repository;
    
    /**
     * Constructor for ChatPreviewAdapter.
     * @param context Application context
     * @param conversationPreviews List of most recent messages from each conversation
     * @param currentUserId ID of the current user
     * @param repository Repository for data access
     */
    public ChatPreviewAdapter(Context context, List<Message> conversationPreviews, 
                             String currentUserId, Repository repository) {
        this.context = context;
        this.conversationPreviews = conversationPreviews;
        this.inflater = LayoutInflater.from(context);
        this.dateFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
        this.currentUserId = currentUserId;
        this.repository = repository;
    }
    
    @NonNull
    @Override
    public ChatPreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_message, parent, false);
        return new ChatPreviewViewHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ChatPreviewViewHolder holder, int position) {
        Message message = conversationPreviews.get(position);
        
        // Get the other user ID (sender or receiver, whichever is not the current user)
        String otherUserId = message.getSenderId().equals(currentUserId) ? 
                message.getReceiverId() : message.getSenderId();
        
        // Try to get user from cache
        User otherUser = userCache.get(otherUserId);
        if (otherUser == null) {
            // If not in cache, get from database (this is a simplified version)
            // In a real app, we'd use LiveData and observe it
            repository.getUserById(otherUserId).observeForever(user -> {
                if (user != null) {
                    userCache.put(otherUserId, user);
                    holder.userNameTextView.setText(user.getName());
                    
                    // Set user profile image
                    if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                        // In a real app, we'd use an image loading library
                        holder.userImageView.setImageResource(R.drawable.placeholder_image);
                    } else {
                        holder.userImageView.setImageResource(R.drawable.ic_profile);
                    }
                }
            });
        } else {
            holder.userNameTextView.setText(otherUser.getName());
            
            // Set user profile image
            if (otherUser.getProfileImageUrl() != null && !otherUser.getProfileImageUrl().isEmpty()) {
                // In a real app, we'd use an image loading library
                holder.userImageView.setImageResource(R.drawable.placeholder_image);
            } else {
                holder.userImageView.setImageResource(R.drawable.ic_profile);
            }
        }
        
        // Set message preview text
        holder.messagePreviewTextView.setText(message.getMessageText());
        
        // Set date
        String formattedDate = dateFormat.format(new Date(message.getTimestamp()));
        holder.dateTextView.setText(formattedDate);
        
        // Check if message is unread
        if (!message.isRead() && message.getReceiverId().equals(currentUserId)) {
            holder.unreadIndicatorView.setVisibility(View.VISIBLE);
        } else {
            holder.unreadIndicatorView.setVisibility(View.GONE);
        }
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            // Navigate to chat activity
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("conversation_id", message.getConversationId());
            intent.putExtra("other_user_id", otherUserId);
            intent.putExtra("item_id", message.getItemId());
            context.startActivity(intent);
        });
    }
    
    @Override
    public int getItemCount() {
        return conversationPreviews == null ? 0 : conversationPreviews.size();
    }
    
    /**
     * Update the adapter's data set.
     * @param newConversationPreviews The new list of conversation previews
     */
    public void setConversationPreviews(List<Message> newConversationPreviews) {
        this.conversationPreviews = newConversationPreviews;
        notifyDataSetChanged();
    }
    
    /**
     * ViewHolder class for chat preview views.
     */
    public static class ChatPreviewViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userImageView;
        private final TextView userNameTextView;
        private final TextView messagePreviewTextView;
        private final TextView dateTextView;
        private final View unreadIndicatorView;
        
        public ChatPreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.user_image);
            userNameTextView = itemView.findViewById(R.id.user_name);
            messagePreviewTextView = itemView.findViewById(R.id.message_preview);
            dateTextView = itemView.findViewById(R.id.message_date);
            unreadIndicatorView = itemView.findViewById(R.id.unread_indicator);
        }
    }
}