package com.example.firstapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.example.firstapp.R;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Default fragment when opening
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new com.example.myapp.FragmentOne())
                .commit();

        // Handle fragment switch buttons
        Button btn1 = findViewById(R.id.btnFragment1);
        Button btn2 = findViewById(R.id.btnFragment2);
        Button btn3 = findViewById(R.id.btnFragment3);

        btn1.setOnClickListener(v -> getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new com.example.myapp.FragmentOne())
                .commit());

        btn2.setOnClickListener(v -> getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new com.example.myapp.FragmentTwo())
                .commit());

        btn3.setOnClickListener(v -> getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new com.example.myapp.FragmentThree())
                .commit());
    }
}
