package com.example.vintagestore.ui.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.example.vintagestore.MainActivity;
import com.example.vintagestore.R;
import com.example.vintagestore.data.Repository;
import com.example.vintagestore.model.Item;
import com.google.android.material.textfield.TextInputLayout;

public class CheckoutActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imgItem;
    private TextView textTitle;
    private TextView textPrice;
    private TextView textDeliveryFee;
    private TextView textTotalPrice;
    private TextInputLayout inputLayoutName;
    private TextInputLayout inputLayoutAddress;
    private TextInputLayout inputLayoutCity;
    private TextInputLayout inputLayoutZip;
    private TextInputLayout inputLayoutCardNumber;
    private TextInputLayout inputLayoutExpiry;
    private TextInputLayout inputLayoutCvv;
    private EditText editName;
    private EditText editAddress;
    private EditText editCity;
    private EditText editZip;
    private EditText editCardNumber;
    private EditText editExpiry;
    private EditText editCvv;
    private Button btnPay;
    
    private Repository repository;
    private long itemId;
    private Item currentItem;
    private static final double DELIVERY_FEE = 4.99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        
        // Get item ID from intent
        itemId = getIntent().getLongExtra("ITEM_ID", -1);
        if (itemId == -1) {
            finish();
            return;
        }
        
        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        imgItem = findViewById(R.id.img_item);
        textTitle = findViewById(R.id.text_title);
        textPrice = findViewById(R.id.text_price);
        textDeliveryFee = findViewById(R.id.text_delivery_fee);
        textTotalPrice = findViewById(R.id.text_total_price);
        inputLayoutName = findViewById(R.id.input_layout_name);
        inputLayoutAddress = findViewById(R.id.input_layout_address);
        inputLayoutCity = findViewById(R.id.input_layout_city);
        inputLayoutZip = findViewById(R.id.input_layout_zip);
        inputLayoutCardNumber = findViewById(R.id.input_layout_card_number);
        inputLayoutExpiry = findViewById(R.id.input_layout_expiry);
        inputLayoutCvv = findViewById(R.id.input_layout_cvv);
        editName = findViewById(R.id.edit_name);
        editAddress = findViewById(R.id.edit_address);
        editCity = findViewById(R.id.edit_city);
        editZip = findViewById(R.id.edit_zip);
        editCardNumber = findViewById(R.id.edit_card_number);
        editExpiry = findViewById(R.id.edit_expiry);
        editCvv = findViewById(R.id.edit_cvv);
        btnPay = findViewById(R.id.btn_pay);
        
        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Checkout");
        
        // Initialize repository
        repository = new Repository(getApplication());
        
        // Load item data
        loadItem();
        
        // Set delivery fee and update total
        textDeliveryFee.setText("$" + String.format("%.2f", DELIVERY_FEE));
        
        // Set up pay button
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPayment();
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
                }
            }
        });
    }
    
    private void updateUI(Item item) {
        textTitle.setText(item.getTitle());
        textPrice.setText("$" + String.format("%.2f", item.getPrice()));
        
        // Calculate total price
        double totalPrice = item.getPrice() + DELIVERY_FEE;
        textTotalPrice.setText("$" + String.format("%.2f", totalPrice));
        
        // Update payment button text
        btnPay.setText("PAY $" + String.format("%.2f", totalPrice));
        
        // Load item image if available
        if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
            Glide.with(this)
                    .load(item.getImageUri())
                    .placeholder(R.drawable.placeholder_image)
                    .into(imgItem);
        } else {
            imgItem.setImageResource(R.drawable.placeholder_image);
        }
    }
    
    private void processPayment() {
        // Validate inputs
        boolean isValid = true;
        
        if (TextUtils.isEmpty(editName.getText())) {
            inputLayoutName.setError("Name is required");
            isValid = false;
        } else {
            inputLayoutName.setError(null);
        }
        
        if (TextUtils.isEmpty(editAddress.getText())) {
            inputLayoutAddress.setError("Address is required");
            isValid = false;
        } else {
            inputLayoutAddress.setError(null);
        }
        
        if (TextUtils.isEmpty(editCity.getText())) {
            inputLayoutCity.setError("City is required");
            isValid = false;
        } else {
            inputLayoutCity.setError(null);
        }
        
        if (TextUtils.isEmpty(editZip.getText())) {
            inputLayoutZip.setError("Zip code is required");
            isValid = false;
        } else {
            inputLayoutZip.setError(null);
        }
        
        if (TextUtils.isEmpty(editCardNumber.getText())) {
            inputLayoutCardNumber.setError("Card number is required");
            isValid = false;
        } else if (editCardNumber.getText().length() < 16) {
            inputLayoutCardNumber.setError("Invalid card number");
            isValid = false;
        } else {
            inputLayoutCardNumber.setError(null);
        }
        
        if (TextUtils.isEmpty(editExpiry.getText())) {
            inputLayoutExpiry.setError("Expiry date is required");
            isValid = false;
        } else {
            inputLayoutExpiry.setError(null);
        }
        
        if (TextUtils.isEmpty(editCvv.getText())) {
            inputLayoutCvv.setError("CVV is required");
            isValid = false;
        } else if (editCvv.getText().length() < 3) {
            inputLayoutCvv.setError("Invalid CVV");
            isValid = false;
        } else {
            inputLayoutCvv.setError(null);
        }
        
        if (!isValid) {
            return;
        }
        
        // This is a mock payment process - in a real app, this would connect to a payment gateway
        // Mark item as sold
        if (currentItem != null) {
            currentItem.setSold(true);
            repository.updateItem(currentItem);
            
            // Show success message
            Toast.makeText(this, "Payment successful! Item purchased.", Toast.LENGTH_LONG).show();
            
            // Navigate back to main activity
            Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
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
