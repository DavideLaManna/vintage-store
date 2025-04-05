package com.example.vintagestore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vintagestore.R;
import com.example.vintagestore.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView adapter for displaying messages in a chat.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    
    private final Context context;
    private List<Message> messages;
    private final LayoutInflater inflater;
    private final String currentUserId;
    private final SimpleDateFormat timeFormat;
    
    /**
     * Constructor for MessageAdapter.
     * @param context Application context
     * @param messages List of messages to display
     * @param currentUserId ID of the current user
     */
    public MessageAdapter(Context context, List<Message> messages, String currentUserId) {
        this.context = context;
        this.messages = messages;
        this.inflater = LayoutInflater.from(context);
        this.currentUserId = currentUserId;
        this.timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
    }
    
    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }
    
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View messageView = inflater.inflate(R.layout.item_chat, parent, false);
        return new MessageViewHolder(messageView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message currentMessage = messages.get(position);
        
        // Set message text
        holder.messageTextView.setText(currentMessage.getMessageText());
        
        // Set message time
        String formattedTime = timeFormat.format(new Date(currentMessage.getTimestamp()));
        holder.timeTextView.setText(formattedTime);
        
        // Set image if message has one
        if (currentMessage.getImageUrl() != null && !currentMessage.getImageUrl().isEmpty()) {
            holder.messageImageView.setVisibility(View.VISIBLE);
            // In a real app, we'd use an image loading library like Glide or Picasso
            // For now, we'll just set a placeholder image
            holder.messageImageView.setImageResource(R.drawable.placeholder_image);
        } else {
            holder.messageImageView.setVisibility(View.GONE);
        }
        
        // Set layout based on message type (sent or received)
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.messageContainer.getLayoutParams();
        
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            // Align to the right for sent messages
            params.horizontalBias = 1.0f;
            holder.messageContainer.setBackgroundResource(R.drawable.bg_rounded_corner);
            holder.messageContainer.setPadding(16, 8, 16, 8);
        } else {
            // Align to the left for received messages
            params.horizontalBias = 0.0f;
            holder.messageContainer.setBackgroundResource(R.drawable.bg_rounded_corner);
            holder.messageContainer.setPadding(16, 8, 16, 8);
        }
        
        holder.messageContainer.setLayoutParams(params);
    }
    
    @Override
    public int getItemCount() {
        return messages == null ? 0 : messages.size();
    }
    
    /**
     * Update the adapter's data set.
     * @param newMessages The new list of messages
     */
    public void setMessages(List<Message> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }
    
    /**
     * ViewHolder class for message views.
     */
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final View messageContainer;
        private final TextView messageTextView;
        private final TextView timeTextView;
        private final ImageView messageImageView;
        
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContainer = itemView.findViewById(R.id.message_container);
            messageTextView = itemView.findViewById(R.id.message_text);
            timeTextView = itemView.findViewById(R.id.message_time);
            messageImageView = itemView.findViewById(R.id.message_image);
        }
    }
}