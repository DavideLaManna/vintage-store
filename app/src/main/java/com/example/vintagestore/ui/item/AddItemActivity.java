package com.example.vintagestore.ui.item;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.vintagestore.R;
import com.example.vintagestore.data.Repository;
import com.example.vintagestore.model.Item;
import com.example.vintagestore.util.ImageUtils;

import java.io.File;
import java.io.IOException;

public class AddItemActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY_IMAGE = 2;

    private Toolbar toolbar;
    private ImageView imgItem;
    private EditText editTitle;
    private EditText editDescription;
    private EditText editPrice;
    private Spinner spinnerSize;
    private Spinner spinnerCategory;
    private Spinner spinnerCondition;
    private EditText editBrand;
    private EditText editColor;
    private Button btnTakePhoto;
    private Button btnSelectPhoto;
    private Button btnSubmit;

    private Repository repository;
    private long currentUserId;
    private Uri imageUri;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Get current user ID from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserId = sharedPreferences.getLong("currentUserId", -1);
        if (currentUserId == -1) {
            finish();
            return;
        }

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        imgItem = findViewById(R.id.img_item);
        editTitle = findViewById(R.id.edit_title);
        editDescription = findViewById(R.id.edit_description);
        editPrice = findViewById(R.id.edit_price);
        spinnerSize = findViewById(R.id.spinner_size);
        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerCondition = findViewById(R.id.spinner_condition);
        editBrand = findViewById(R.id.edit_brand);
        editColor = findViewById(R.id.edit_color);
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnSelectPhoto = findViewById(R.id.btn_select_photo);
        btnSubmit = findViewById(R.id.btn_submit);

        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Item");

        // Initialize repository
        repository = new Repository(getApplication());

        // Set up spinners
        setupSpinners();

        // Set up button listeners
        setupButtonListeners();
    }

    private void setupSpinners() {
        // Size spinner
        String[] sizes = {"XS", "S", "M", "L", "XL", "XXL", "3XL", "4XL", "5XL"};
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, sizes);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSize.setAdapter(sizeAdapter);

        // Category spinner
        String[] categories = {"T-Shirt", "Shirt", "Jeans", "Pants", "Shorts", "Dress", 
                "Skirt", "Jacket", "Coat", "Sweater", "Hoodie", "Shoes", "Accessories"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Condition spinner
        String[] conditions = {"New with tags", "Like new", "Very good", "Good", "Satisfactory"};
        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, conditions);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCondition.setAdapter(conditionAdapter);
    }

    private void setupButtonListeners() {
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = ImageUtils.createImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "com.example.vintagestore.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_GALLERY_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Image captured from camera
                if (imageUri != null) {
                    loadImageIntoView(imageUri);
                }
            } else if (requestCode == REQUEST_GALLERY_IMAGE && data != null) {
                // Image selected from gallery
                imageUri = data.getData();
                loadImageIntoView(imageUri);
            }
        }
    }

    private void loadImageIntoView(Uri uri) {
        Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.placeholder_image)
                .into(imgItem);
        imgItem.setVisibility(View.VISIBLE);
    }

    private void saveItem() {
        // Validate inputs
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String priceStr = editPrice.getText().toString().trim();
        String brand = editBrand.getText().toString().trim();
        String color = editColor.getText().toString().trim();
        String size = spinnerSize.getSelectedItem().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String condition = spinnerCondition.getSelectedItem().toString();

        if (title.isEmpty()) {
            editTitle.setError("Title is required");
            return;
        }

        if (description.isEmpty()) {
            editDescription.setError("Description is required");
            return;
        }

        if (priceStr.isEmpty()) {
            editPrice.setError("Price is required");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            editPrice.setError("Invalid price");
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please add an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create item object
        Item item = new Item(
                currentUserId,
                title,
                description,
                price,
                size,
                brand,
                condition,
                category,
                color,
                imageUri.toString());

        // Save to database
        long itemId = repository.insertItem(item);
        
        if (itemId > 0) {
            Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
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
