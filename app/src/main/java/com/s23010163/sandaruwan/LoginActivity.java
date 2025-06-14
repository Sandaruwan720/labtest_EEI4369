package com.s23010163.sandaruwan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginButton;

    DatabaseHelper databaseHelper;  // DB Helper instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.btnLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();

                if (!emailText.isEmpty() && !passwordText.isEmpty()) {
                    boolean isInserted = databaseHelper.insertData(emailText, passwordText);
                    if (isInserted) {
                        Toast.makeText(LoginActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();

                        // Proceed to next activity (e.g. MapActivity)
                        Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Data saving failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
