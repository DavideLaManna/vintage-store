package com.example.vintagestore.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vintagestore.R;
import com.example.vintagestore.adapter.ItemAdapter;
import com.example.vintagestore.data.Repository;
import com.example.vintagestore.model.Item;

import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private TextView emptyView;
    private Repository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        
        // Set up the RecyclerView with a grid layout
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        
        // Initialize repository
        repository = new Repository(getContext());
        
        // Load and display items
        loadItems();
        
        return view;
    }

    private void loadItems() {
        List<Item> items = repository.getAllItems();
        
        if (items != null && !items.isEmpty()) {
            adapter = new ItemAdapter(getContext(), items);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the items list when the fragment becomes visible
        loadItems();
    }
}