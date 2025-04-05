package com.example.vintagestore.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Patterns;
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

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout inputLayoutUsername;
    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutPassword;
    private TextInputLayout inputLayoutConfirmPassword;
    private TextInputLayout inputLayoutFullName;
    private EditText editUsername;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private EditText editFullName;
    private Button btnRegister;
    private TextView textLogin;
    
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        // Initialize views
        inputLayoutUsername = findViewById(R.id.input_layout_username);
        inputLayoutEmail = findViewById(R.id.input_layout_email);
        inputLayoutPassword = findViewById(R.id.input_layout_password);
        inputLayoutConfirmPassword = findViewById(R.id.input_layout_confirm_password);
        inputLayoutFullName = findViewById(R.id.input_layout_full_name);
        editUsername = findViewById(R.id.edit_username);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editConfirmPassword = findViewById(R.id.edit_confirm_password);
        editFullName = findViewById(R.id.edit_full_name);
        btnRegister = findViewById(R.id.btn_register);
        textLogin = findViewById(R.id.text_login);
        
        // Initialize repository
        repository = new Repository(getApplication());
        
        // Set up register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        
        // Set up login text
        textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to login activity
            }
        });
    }
    
    private void registerUser() {
        // Clear previous errors
        inputLayoutUsername.setError(null);
        inputLayoutEmail.setError(null);
        inputLayoutPassword.setError(null);
        inputLayoutConfirmPassword.setError(null);
        inputLayoutFullName.setError(null);
        
        // Get input values
        String username = editUsername.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();
        String fullName = editFullName.getText().toString().trim();
        
        // Validate inputs
        boolean isValid = true;
        
        if (TextUtils.isEmpty(username)) {
            inputLayoutUsername.setError("Username cannot be empty");
            isValid = false;
        } else if (username.length() < 4) {
            inputLayoutUsername.setError("Username must be at least 4 characters");
            isValid = false;
        }
        
        if (TextUtils.isEmpty(email)) {
            inputLayoutEmail.setError("Email cannot be empty");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputLayoutEmail.setError("Invalid email format");
            isValid = false;
        }
        
        if (TextUtils.isEmpty(password)) {
            inputLayoutPassword.setError("Password cannot be empty");
            isValid = false;
        } else if (password.length() < 6) {
            inputLayoutPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }
        
        if (TextUtils.isEmpty(confirmPassword)) {
            inputLayoutConfirmPassword.setError("Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            inputLayoutConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }
        
        if (TextUtils.isEmpty(fullName)) {
            inputLayoutFullName.setError("Full name cannot be empty");
            isValid = false;
        }
        
        if (!isValid) {
            return;
        }
        
        // Check if username or email already exists
        if (repository.isUsernameTaken(username)) {
            inputLayoutUsername.setError("Username already taken");
            return;
        }
        
        if (repository.isEmailTaken(email)) {
            inputLayoutEmail.setError("Email already registered");
            return;
        }
        
        // Create user
        User user = new User(username, email, password, fullName);
        
        // Save to database
        long userId = repository.insertUser(user);
        
        if (userId > 0) {
            // Save login status
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putLong("currentUserId", userId);
            editor.apply();
            
            // Show success message
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
            
            // Navigate to main activity
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }
}
