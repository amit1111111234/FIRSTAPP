package com.example.firstapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    Button button;
    Button openSecondActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FireBase.initilizeApp(this);

        // link UI elements
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        openSecondActivityButton = findViewById(R.id.openSecondActivityButton);

        // handle text-changing button click
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userText = editText.getText().toString();
                button.setText(userText);
            }
        });

        // handle open second activity button click
        openSecondActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });
    }
}
