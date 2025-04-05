package com.example.vintagestore.ui.item;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.example.vintagestore.R;
import com.example.vintagestore.data.Repository;
import com.example.vintagestore.model.Item;
import com.example.vintagestore.model.User;
import com.example.vintagestore.ui.checkout.CheckoutActivity;
import com.example.vintagestore.ui.messages.ChatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ItemDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imgItem;
    private TextView textTitle;
    private TextView textPrice;
    private TextView textSize;
    private TextView textBrand;
    private TextView textCondition;
    private TextView textDescription;
    private TextView textSellerName;
    private TextView textListingDate;
    private Button btnBuyNow;
    private Button btnMessage;
    private FloatingActionButton fabFavorite;
    
    private Repository repository;
    private long itemId;
    private Item currentItem;
    private long currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        
        // Get item ID from intent
        itemId = getIntent().getLongExtra("ITEM_ID", -1);
        if (itemId == -1) {
            finish();
            return;
        }
        
        // Get current user ID from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserId = sharedPreferences.getLong("currentUserId", -1);
        
        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        imgItem = findViewById(R.id.img_item);
        textTitle = findViewById(R.id.text_title);
        textPrice = findViewById(R.id.text_price);
        textSize = findViewById(R.id.text_size);
        textBrand = findViewById(R.id.text_brand);
        textCondition = findViewById(R.id.text_condition);
        textDescription = findViewById(R.id.text_description);
        textSellerName = findViewById(R.id.text_seller_name);
        textListingDate = findViewById(R.id.text_listing_date);
        btnBuyNow = findViewById(R.id.btn_buy_now);
        btnMessage = findViewById(R.id.btn_message);
        fabFavorite = findViewById(R.id.fab_favorite);
        
        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        
        // Initialize repository
        repository = new Repository(getApplication());
        
        // Load item data
        loadItem();
        
        // Set up favorite button
        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite();
            }
        });
        
        // Set up buy now button
        btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentItem != null) {
                    Intent intent = new Intent(ItemDetailActivity.this, CheckoutActivity.class);
                    intent.putExtra("ITEM_ID", currentItem.getId());
                    startActivity(intent);
                }
            }
        });
        
        // Set up message button
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentItem != null) {
                    Intent intent = new Intent(ItemDetailActivity.this, ChatActivity.class);
                    intent.putExtra("userId", currentItem.getUserId());
                    startActivity(intent);
                }
            }
        });
    }
    
    private void loadItem() {
        repository.getItemById(itemId).observe(this, new Observer<Item>() {
            @Override
            public void onChanged(Item item) {
                if (item != null) {
                    currentItem = item;
                    updateUI(item);
                    loadSeller(item.getUserId());
                    
                    // Hide message button if current user is the seller
                    if (item.getUserId() == currentUserId) {
                        btnMessage.setVisibility(View.GONE);
                    } else {
                        btnMessage.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
    
    private void loadSeller(long sellerId) {
        repository.getUserById(sellerId).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    textSellerName.setText(user.getUsername());
                }
            }
        });
    }
    
    private void updateUI(Item item) {
        textTitle.setText(item.getTitle());
        textPrice.setText("$" + String.format("%.2f", item.getPrice()));
        textSize.setText("Size: " + item.getSize());
        textBrand.setText("Brand: " + item.getBrand());
        textCondition.setText("Condition: " + item.getCondition());
        textDescription.setText(item.getDescription());
        
        // Format and display listing date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        textListingDate.setText("Listed on " + dateFormat.format(new Date(item.getTimestamp())));
        
        // Update favorite button
        updateFavoriteButton(item.isFavorite());
        
        // Load item image if available
        if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
            Glide.with(this)
                    .load(item.getImageUri())
                    .placeholder(R.drawable.placeholder_image)
                    .into(imgItem);
        } else {
            imgItem.setImageResource(R.drawable.placeholder_image);
        }
        
        // Update Buy Now button if item is sold
        if (item.isSold()) {
            btnBuyNow.setText("SOLD OUT");
            btnBuyNow.setEnabled(false);
        } else {
            btnBuyNow.setText("BUY NOW");
            btnBuyNow.setEnabled(true);
        }
    }
    
    private void toggleFavorite() {
        if (currentItem != null) {
            boolean newFavoriteStatus = !currentItem.isFavorite();
            repository.updateFavoriteStatus(currentItem.getId(), newFavoriteStatus);
            updateFavoriteButton(newFavoriteStatus);
            
            if (newFavoriteStatus) {
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void updateFavoriteButton(boolean isFavorite) {
        if (isFavorite) {
            fabFavorite.setImageResource(R.drawable.ic_favorites);
        } else {
            fabFavorite.setImageResource(R.drawable.ic_favorites);
            // Add alpha to show it's not favorite
            fabFavorite.setAlpha(0.5f);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
