package com.example.vintagestore;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.vintagestore.ui.favorites.FavoritesFragment;
import com.example.vintagestore.ui.home.HomeFragment;
import com.example.vintagestore.ui.messages.MessagesFragment;
import com.example.vintagestore.ui.profile.ProfileFragment;
import com.example.vintagestore.ui.search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.vintagestore.ui.item.AddItemActivity;
import android.content.Intent;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Load default fragment (Home)
        loadFragment(new HomeFragment());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.navigation_home) {
            fragment = new HomeFragment();
        } else if (itemId == R.id.navigation_search) {
            fragment = new SearchFragment();
        } else if (itemId == R.id.navigation_add) {
            // Launch Add Item Activity instead of loading a fragment
            startActivity(new Intent(this, AddItemActivity.class));
            return true;
        } else if (itemId == R.id.navigation_favorites) {
            fragment = new FavoritesFragment();
        } else if (itemId == R.id.navigation_profile) {
            fragment = new ProfileFragment();
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}