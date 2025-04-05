package com.example.vintagestore.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vintagestore.R;
import com.example.vintagestore.adapter.ItemAdapter;
import com.example.vintagestore.data.Repository;
import com.example.vintagestore.model.Item;
import com.example.vintagestore.model.User;
import com.example.vintagestore.ui.auth.LoginActivity;
import com.example.vintagestore.ui.item.AddItemActivity;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private ImageView imgProfile;
    private TextView textUsername;
    private TextView textFullName;
    private TextView textJoinDate;
    private TextView textBio;
    private RatingBar ratingBar;
    private TextView textRatingCount;
    private RecyclerView recyclerMyItems;
    private Button btnEditProfile;
    private Button btnLogout;
    private Button btnSettings;
    private Button btnAddItem;
    private TextView textEmpty;
    private ProgressBar progressBar;
    private TabLayout profileTabs;
    private TextView textListingsCount;
    private TextView textSoldCount;
    private TextView textPurchasedCount;
    
    private ItemAdapter adapter;
    private Repository repository;
    private long currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        // Initialize views
        imgProfile = view.findViewById(R.id.img_profile);
        textUsername = view.findViewById(R.id.text_username);
        textFullName = view.findViewById(R.id.text_full_name);
        textJoinDate = view.findViewById(R.id.text_join_date);
        textBio = view.findViewById(R.id.text_bio);
        ratingBar = view.findViewById(R.id.rating_bar);
        textRatingCount = view.findViewById(R.id.text_rating_count);
        recyclerMyItems = view.findViewById(R.id.recycler_my_items);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnSettings = view.findViewById(R.id.btn_settings);
        btnAddItem = view.findViewById(R.id.btn_add_item);
        textEmpty = view.findViewById(R.id.text_empty);
        progressBar = view.findViewById(R.id.progress_bar);
        profileTabs = view.findViewById(R.id.profile_tabs);
        textListingsCount = view.findViewById(R.id.text_listings_count);
        textSoldCount = view.findViewById(R.id.text_sold_count);
        textPurchasedCount = view.findViewById(R.id.text_purchased_count);
        
        // Initialize repository
        repository = new Repository(requireContext());
        
        // Get current user ID from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        currentUserId = sharedPreferences.getLong("currentUserId", -1);
        
        if (currentUserId == -1) {
            // Not logged in, redirect to login
            startActivity(new Intent(getActivity(), LoginActivity.class));
            if (getActivity() != null) {
                getActivity().finish();
            }
            return view;
        }
        
        // Setup RecyclerView with a grid layout (2 columns)
        recyclerMyItems.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ItemAdapter(getContext(), new ArrayList<>());
        recyclerMyItems.setAdapter(adapter);
        
        // Set initial stats
        textListingsCount.setText("0");
        textSoldCount.setText("0");
        textPurchasedCount.setText("0");
        
        // Set up item click and favorite listeners
        adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                // Item click is handled by the adapter (opens ItemDetailActivity)
            }

            @Override
            public void onFavoriteClick(Item item, int position) {
                // Toggle favorite status
                repository.updateFavoriteStatus(item.getId(), !item.isFavorite());
            }
        });
        
        // Set up tabs change listener
        profileTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabContent(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed
            }
        });
        
        // Load user data
        loadUserData();
        
        // Load user's items (default tab)
        loadUserItems();
        
        // Setup button click listeners
        setupButtons();
        
        return view;
    }
    
    private void updateTabContent(int position) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerMyItems.setVisibility(View.GONE);
        textEmpty.setVisibility(View.GONE);
        
        switch (position) {
            case 0: // My Items
                loadUserItems();
                break;
            case 1: // Sold
                loadSoldItems();
                break;
            case 2: // Purchased
                loadPurchasedItems();
                break;
            case 3: // Reviews
                loadReviews();
                break;
        }
    }
    
    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);
        
        repository.getUserById(currentUserId).observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                progressBar.setVisibility(View.GONE);
                if (user != null) {
                    updateUI(user);
                }
            }
        });
    }
    
    private void updateUI(User user) {
        // Set username from email (since we don't have a separate username field)
        String username = user.getEmail().split("@")[0];
        textUsername.setText("@" + username);
        
        textFullName.setText(user.getFullName());
        
        // Format and display join date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String joinDateText = "Member since " + dateFormat.format(user.getCreatedAt());
        textJoinDate.setText(joinDateText);
        
        // Set bio from user object
        if (user.getBio() != null && !user.getBio().isEmpty()) {
            textBio.setText(user.getBio());
        } else {
            textBio.setText("No bio available");
        }
        
        // Set rating
        ratingBar.setRating(user.getRating());
        textRatingCount.setText("(" + user.getReviewCount() + ")");
        
        // Load profile image if available
        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            Glide.with(this)
                    .load(user.getProfileImage())
                    .circleCrop()
                    .into(imgProfile);
        } else {
            // Set a placeholder image
            Glide.with(this)
                    .load(R.drawable.placeholder_image)
                    .circleCrop()
                    .into(imgProfile);
        }
        
        // Query the item counts asynchronously
        countUserItems();
    }
    
    private void countUserItems() {
        // This would be implemented with actual queries to get counts of items
        // For now we'll update the UI with sample data
        repository.getItemsByUserId(currentUserId).observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                int totalItems = items.size();
                int soldItems = 0;
                
                // Count sold items
                for (Item item : items) {
                    if (item.isSold()) {
                        soldItems++;
                    }
                }
                
                textListingsCount.setText(String.valueOf(totalItems));
                textSoldCount.setText(String.valueOf(soldItems));
                textPurchasedCount.setText("0"); // Purchased items would be tracked separately
            }
        });
    }
    
    private void loadUserItems() {
        repository.getItemsByUserId(currentUserId).observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                progressBar.setVisibility(View.GONE);
                if (items != null && !items.isEmpty()) {
                    // Filter to show only unsold items
                    List<Item> activeItems = new ArrayList<>();
                    for (Item item : items) {
                        if (!item.isSold()) {
                            activeItems.add(item);
                        }
                    }
                    
                    if (!activeItems.isEmpty()) {
                        adapter.updateData(activeItems);
                        recyclerMyItems.setVisibility(View.VISIBLE);
                        textEmpty.setVisibility(View.GONE);
                    } else {
                        recyclerMyItems.setVisibility(View.GONE);
                        textEmpty.setText("You haven't listed any active items yet");
                        textEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerMyItems.setVisibility(View.GONE);
                    textEmpty.setText("You haven't listed any items yet");
                    textEmpty.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    
    private void loadSoldItems() {
        repository.getItemsByUserId(currentUserId).observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                progressBar.setVisibility(View.GONE);
                if (items != null && !items.isEmpty()) {
                    // Filter to show only sold items
                    List<Item> soldItems = new ArrayList<>();
                    for (Item item : items) {
                        if (item.isSold()) {
                            soldItems.add(item);
                        }
                    }
                    
                    if (!soldItems.isEmpty()) {
                        adapter.updateData(soldItems);
                        recyclerMyItems.setVisibility(View.VISIBLE);
                        textEmpty.setVisibility(View.GONE);
                    } else {
                        recyclerMyItems.setVisibility(View.GONE);
                        textEmpty.setText("You haven't sold any items yet");
                        textEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerMyItems.setVisibility(View.GONE);
                    textEmpty.setText("You haven't listed any items yet");
                    textEmpty.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    
    private void loadPurchasedItems() {
        // In a real app, we would load purchased items from the repository
        // For demo purposes, we'll show the empty view
        progressBar.setVisibility(View.GONE);
        recyclerMyItems.setVisibility(View.GONE);
        textEmpty.setText("You haven't purchased any items yet");
        textEmpty.setVisibility(View.VISIBLE);
    }
    
    private void loadReviews() {
        // In a real app, we would load reviews from the repository
        // For demo purposes, we'll show the empty view
        progressBar.setVisibility(View.GONE);
        recyclerMyItems.setVisibility(View.GONE);
        textEmpty.setText("You don't have any reviews yet");
        textEmpty.setVisibility(View.VISIBLE);
    }
    
    private void setupButtons() {
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open edit profile screen (not implemented in this MVP)
                Toast.makeText(getContext(), "Edit profile feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open add item activity
                Intent intent = new Intent(getActivity(), AddItemActivity.class);
                startActivity(intent);
            }
        });
        
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open settings screen (not implemented in this MVP)
                Toast.makeText(getContext(), "Settings feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear login status
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.putLong("currentUserId", -1);
                editor.apply();
                
                // Navigate to login screen
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
    }
}
