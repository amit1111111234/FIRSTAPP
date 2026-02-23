package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore; // Added

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText nameEdit, emailEdit, passEdit, confirmEdit;
    Button btnSignUp;
    TextView loginLink;
    FirebaseAuth auth;
    FirebaseFirestore db; // Added

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Added

        nameEdit = findViewById(R.id.editTextName);
        emailEdit = findViewById(R.id.editTextEmail);
        passEdit = findViewById(R.id.editTextPassword);
        confirmEdit = findViewById(R.id.editTextConfirmPassword);
        btnSignUp = findViewById(R.id.buttonSignUp);
        loginLink = findViewById(R.id.textViewLoginLink);

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MainActivity2.class));
        });

        btnSignUp.setOnClickListener(v -> {
            String name = nameEdit.getText().toString().trim();
            String email = emailEdit.getText().toString().trim();
            String pass = passEdit.getText().toString().trim();
            String confirm = confirmEdit.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // SAVE NAME TO FIRESTORE
                            String uid = auth.getCurrentUser().getUid();
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("name", name);

                            db.collection("Users").document(uid).set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(MainActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MainActivity.this, ActivityHome.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(MainActivity.this, "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(MainActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}