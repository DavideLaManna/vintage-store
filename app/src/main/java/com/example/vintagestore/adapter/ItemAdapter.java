package com.example.vintagestore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.vintagestore.R;
import com.example.vintagestore.model.Item;

/**
 * Adapter for displaying clothing items in a RecyclerView
 * Using ListAdapter for efficient list updates with DiffUtil
 */
public class ItemAdapter extends ListAdapter<Item, ItemAdapter.ItemViewHolder> {
    
    private final OnItemClickListener listener;
    private final OnFavoriteClickListener favoriteListener;
    
    // Interface for item click
    public interface OnItemClickListener {
        void onItemClick(Item item);
    }
    
    // Interface for favorite button click
    public interface OnFavoriteClickListener {
        void onFavoriteClick(Item item, boolean isFavorite);
    }
    
    // DiffUtil implementation for efficient updates
    private static final DiffUtil.ItemCallback<Item> DIFF_CALLBACK = 
            new DiffUtil.ItemCallback<Item>() {
        @Override
        public boolean areItemsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem.getId() == newItem.getId();
        }
        
        @Override
        public boolean areContentsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                   oldItem.getPrice() == newItem.getPrice() &&
                   oldItem.isFavorite() == newItem.isFavorite() &&
                   oldItem.isSold() == newItem.isSold() &&
                   oldItem.getBrand().equals(newItem.getBrand());
        }
    };
    
    public ItemAdapter(OnItemClickListener listener, OnFavoriteClickListener favoriteListener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        this.favoriteListener = favoriteListener;
    }
    
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clothing, parent, false);
        return new ItemViewHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item currentItem = getItem(position);
        holder.bind(currentItem, listener, favoriteListener);
    }
    
    public Item getItemAt(int position) {
        return getItem(position);
    }
    
    // ViewHolder class
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView titleTextView;
        private final TextView priceTextView;
        private final TextView brandTextView;
        private final TextView sizeTextView;
        private final ImageButton favoriteButton;
        private final View soldOverlay;
        
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_item_image);
            titleTextView = itemView.findViewById(R.id.tv_item_title);
            priceTextView = itemView.findViewById(R.id.tv_item_price);
            brandTextView = itemView.findViewById(R.id.tv_item_brand);
            sizeTextView = itemView.findViewById(R.id.tv_item_size);
            favoriteButton = itemView.findViewById(R.id.btn_favorite);
            soldOverlay = itemView.findViewById(R.id.view_sold_overlay);
        }
        
        public void bind(final Item item, 
                        final OnItemClickListener listener,
                        final OnFavoriteClickListener favoriteListener) {
            Context context = itemView.getContext();
            
            // Set text fields
            titleTextView.setText(item.getTitle());
            priceTextView.setText(item.getFormattedPrice());
            brandTextView.setText(item.getBrand());
            sizeTextView.setText(item.getSize());
            
            // Load image with Glide
            String imageUrl = item.getMainImageUrl();
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop();
                    
            Glide.with(context)
                    .load(imageUrl)
                    .apply(requestOptions)
                    .into(imageView);
            
            // Set up the favorite button
            int favoriteIcon = item.isFavorite() ? 
                    android.R.drawable.btn_star_big_on : 
                    android.R.drawable.btn_star_big_off;
            favoriteButton.setImageResource(favoriteIcon);
            
            // Show sold overlay if item is sold
            soldOverlay.setVisibility(item.isSold() ? View.VISIBLE : View.GONE);
            
            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
            
            favoriteButton.setOnClickListener(v -> {
                if (favoriteListener != null) {
                    boolean newState = !item.isFavorite();
                    item.setFavorite(newState);
                    favoriteListener.onFavoriteClick(item, newState);
                    
                    // Update UI immediately for better UX
                    int newFavoriteIcon = newState ? 
                            android.R.drawable.btn_star_big_on : 
                            android.R.drawable.btn_star_big_off;
                    favoriteButton.setImageResource(newFavoriteIcon);
                }
            });
        }
    }
}
