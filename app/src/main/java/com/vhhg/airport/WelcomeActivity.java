package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Button register = findViewById(R.id.register);
        Button login = findViewById(R.id.login);
        Button woregister = findViewById(R.id.woregister);

        register.setOnClickListener(v -> startActivity(new Intent(this, RegActivity.class)));
        login.setOnClickListener(v -> startActivity(new Intent(this, LogActivity.class)));
        woregister.setOnClickListener(v -> startActivity(new Intent(this, AllActivity.class)));
    }
}