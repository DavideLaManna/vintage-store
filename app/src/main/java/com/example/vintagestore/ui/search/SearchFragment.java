package com.example.vintagestore.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vintagestore.R;
import com.example.vintagestore.adapter.ItemAdapter;
import com.example.vintagestore.data.Repository;
import com.example.vintagestore.model.Item;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment {

    private EditText editSearch;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private TextView textEmpty;
    private Spinner spinnerSize;
    private RangeSlider sliderPrice;
    private TextView textPriceRange;
    private Repository repository;
    
    private String currentQuery = "";
    private String currentSize = "All Sizes";
    private double minPrice = 0;
    private double maxPrice = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        
        // Initialize views
        editSearch = view.findViewById(R.id.edit_search);
        recyclerView = view.findViewById(R.id.recycler_search_results);
        textEmpty = view.findViewById(R.id.text_empty);
        spinnerSize = view.findViewById(R.id.spinner_size);
        sliderPrice = view.findViewById(R.id.slider_price);
        textPriceRange = view.findViewById(R.id.text_price_range);
        
        // Initialize repository
        repository = new Repository(requireActivity().getApplication());

        // Set up RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ItemAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);
        
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

        // Set up search input
        setupSearchInput();
        
        // Set up size spinner
        setupSizeSpinner();
        
        // Set up price range slider
        setupPriceRangeSlider();
        
        // Load initial items
        performSearch();

        return view;
    }

    private void setupSearchInput() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                currentQuery = s.toString().trim();
                performSearch();
            }
        });
    }

    private void setupSizeSpinner() {
        // Create sizes list with "All" as first option
        String[] sizes = {"All Sizes", "XS", "S", "M", "L", "XL", "XXL", "3XL", "4XL", "5XL"};
        
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                sizes
        );
        
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSize.setAdapter(spinnerAdapter);
        
        spinnerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSize = sizes[position];
                performSearch();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentSize = "All Sizes";
                performSearch();
            }
        });
    }

    private void setupPriceRangeSlider() {
        sliderPrice.setValueFrom(0);
        sliderPrice.setValueTo(200);
        sliderPrice.setValues(0f, 100f);
        
        updatePriceRangeText(0, 100);
        
        sliderPrice.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                List<Float> values = slider.getValues();
                minPrice = values.get(0);
                maxPrice = values.get(1);
                
                updatePriceRangeText(minPrice, maxPrice);
                
                if (fromUser) {
                    performSearch();
                }
            }
        });
    }
    
    private void updatePriceRangeText(double min, double max) {
        textPriceRange.setText(String.format(Locale.US, "$%.0f - $%.0f", min, max));
    }

    private void performSearch() {
        if (currentQuery.isEmpty()) {
            // Just apply the filters
            applyFilters();
        } else {
            // Search with query and apply filters
            repository.searchItems(currentQuery).observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
                @Override
                public void onChanged(List<Item> items) {
                    if (items != null && !items.isEmpty()) {
                        // Apply additional filters to search results
                        List<Item> filteredItems = filterItems(items);
                        
                        if (!filteredItems.isEmpty()) {
                            adapter.updateData(filteredItems);
                            recyclerView.setVisibility(View.VISIBLE);
                            textEmpty.setVisibility(View.GONE);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            textEmpty.setText("No items match your filters");
                            textEmpty.setVisibility(View.VISIBLE);
                        }
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        textEmpty.setText("No items found for \"" + currentQuery + "\"");
                        textEmpty.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
    
    private void applyFilters() {
        if (currentSize.equals("All Sizes")) {
            // Filter by price only
            repository.filterByPrice(minPrice, maxPrice).observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
                @Override
                public void onChanged(List<Item> items) {
                    updateUI(items);
                }
            });
        } else {
            // Filter by size and price
            repository.filterBySize(currentSize).observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
                @Override
                public void onChanged(List<Item> items) {
                    // Apply price filter to size-filtered items
                    List<Item> priceFiltered = new ArrayList<>();
                    for (Item item : items) {
                        if (item.getPrice() >= minPrice && item.getPrice() <= maxPrice) {
                            priceFiltered.add(item);
                        }
                    }
                    updateUI(priceFiltered);
                }
            });
        }
    }
    
    private List<Item> filterItems(List<Item> items) {
        List<Item> filteredItems = new ArrayList<>();
        
        for (Item item : items) {
            boolean passesFilter = true;
            
            // Apply size filter
            if (!currentSize.equals("All Sizes") && !item.getSize().equals(currentSize)) {
                passesFilter = false;
            }
            
            // Apply price filter
            if (item.getPrice() < minPrice || item.getPrice() > maxPrice) {
                passesFilter = false;
            }
            
            if (passesFilter) {
                filteredItems.add(item);
            }
        }
        
        return filteredItems;
    }
    
    private void updateUI(List<Item> items) {
        if (items != null && !items.isEmpty()) {
            adapter.updateData(items);
            recyclerView.setVisibility(View.VISIBLE);
            textEmpty.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            textEmpty.setText("No items match your filters");
            textEmpty.setVisibility(View.VISIBLE);
        }
    }
}
