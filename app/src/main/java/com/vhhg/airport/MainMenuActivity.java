package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Button all = findViewById(R.id.all);
        Button dep = findViewById(R.id.departures);
        Button arr = findViewById(R.id.arrivals);
        Button favs = findViewById(R.id.favs);

        all.setOnClickListener(v -> startActivity(new Intent(this, AllActivity.class)));
        favs.setOnClickListener(v -> startActivity(new Intent(this, FavsActivity.class)));
    }
}