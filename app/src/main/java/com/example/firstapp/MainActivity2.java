package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity2 extends AppCompatActivity {
    EditText emailEdit, passEdit;
    Button btnSignIn;
    TextView signUpLink;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        auth = FirebaseAuth.getInstance();

        // Initialize Views
        emailEdit = findViewById(R.id.editTextEmail);
        passEdit = findViewById(R.id.editTextPassword);
        btnSignIn = findViewById(R.id.buttonSignIn);
        signUpLink = findViewById(R.id.textView2);

        // Navigate back to Sign Up
        signUpLink.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity2.this, MainActivity.class));
        });

        // Login Logic
        btnSignIn.setOnClickListener(v -> {
            String email = emailEdit.getText().toString().trim();
            String pass = passEdit.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity2.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity2.this, ActivityHome.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity2.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}