package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class EditFlightActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_flight);
        EditText depAir = findViewById(R.id.departure_airport);
        EditText arrAir = findViewById(R.id.arrival_airport);
        EditText depTime = findViewById(R.id.departure_time);
        EditText arrTime = findViewById(R.id.arrival_time);
        EditText price = findViewById(R.id.price);

    }
}