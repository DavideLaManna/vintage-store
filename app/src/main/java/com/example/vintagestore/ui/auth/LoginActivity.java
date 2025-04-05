package com.example.vintagestore.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vintagestore.MainActivity;
import com.example.vintagestore.R;
import com.example.vintagestore.data.Repository;
import com.example.vintagestore.model.User;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutPassword;
    private EditText editEmail;
    private EditText editPassword;
    private Button btnLogin;
    private TextView textRegister;
    
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Initialize views
        inputLayoutEmail = findViewById(R.id.input_layout_email);
        inputLayoutPassword = findViewById(R.id.input_layout_password);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        btnLogin = findViewById(R.id.btn_login);
        textRegister = findViewById(R.id.text_register);
        
        // Initialize repository
        repository = new Repository(getApplication());
        
        // Set up login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        
        // Set up register text
        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    
    private void loginUser() {
        // Clear previous errors
        inputLayoutEmail.setError(null);
        inputLayoutPassword.setError(null);
        
        // Get input values
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        
        // Validate inputs
        boolean isValid = true;
        
        if (TextUtils.isEmpty(email)) {
            inputLayoutEmail.setError("Email cannot be empty");
            isValid = false;
        }
        
        if (TextUtils.isEmpty(password)) {
            inputLayoutPassword.setError("Password cannot be empty");
            isValid = false;
        }
        
        if (!isValid) {
            return;
        }
        
        // Attempt login
        User user = repository.login(email, password);
        
        if (user != null) {
            // Save login status
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putLong("currentUserId", user.getId());
            editor.apply();
            
            // Navigate to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}
