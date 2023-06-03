package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class RootMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_menu);
        Button all = findViewById(R.id.all);
        Button find = findViewById(R.id.find_flight);
        Button add = findViewById(R.id.add_flight);
        Button users = findViewById(R.id.users);

        add.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateFlightActivity.class));
        });

    }
}