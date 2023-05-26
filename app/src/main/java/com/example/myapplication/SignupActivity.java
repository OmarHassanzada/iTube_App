package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText fullNameET;
    private EditText usernameET;
    private EditText passwordET;
    private EditText confirmET;
    private Button createAccountB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        fullNameET = findViewById(R.id.editTextFullName);
        usernameET = findViewById(R.id.editTextUsername);
        passwordET = findViewById(R.id.editTextPassword);
        confirmET = findViewById(R.id.editTextConfirmPassword);
        createAccountB = findViewById(R.id.buttonCreateAccount);

        // Set click listener for create account button
        createAccountB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddingAccount();
            }
        });
    }

    private void AddingAccount() {
        String fullName = fullNameET.getText().toString().trim();
        String username = usernameET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String confirm = confirmET.getText().toString().trim();

        // Validating all inputs
        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // password and confirm passwords match
        if (!password.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        if (databaseHelper.isUsernameExists(username)) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        // passing acc to details to database helper
        long userId = databaseHelper.addUser(username, password, fullName);
        databaseHelper.close();

        //checks if account is valid
        if (userId != -1) {
            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to create account", Toast.LENGTH_SHORT).show();
        }
    }
}
