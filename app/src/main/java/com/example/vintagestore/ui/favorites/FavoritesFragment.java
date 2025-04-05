package com.example.vintagestore.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.vintagestore.R;
import com.example.vintagestore.adapter.ItemAdapter;
import com.example.vintagestore.data.Repository;
import com.example.vintagestore.model.Item;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textEmpty;
    private Repository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_favorites);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        textEmpty = view.findViewById(R.id.text_empty);
        
        // Initialize repository
        repository = new Repository(requireActivity().getApplication());

        // Set up RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ItemAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Set up item click listener
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

        // Set up swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFavorites();
            }
        });

        // Load favorites
        loadFavorites();

        return view;
    }

    private void loadFavorites() {
        repository.getFavoriteItems().observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                if (items != null && !items.isEmpty()) {
                    adapter.updateData(items);
                    recyclerView.setVisibility(View.VISIBLE);
                    textEmpty.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    textEmpty.setVisibility(View.VISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload favorites when fragment becomes visible again
        loadFavorites();
    }
}
