package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class FlightInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_info);
        TextView dep = findViewById(R.id.departure);
        TextView arr = findViewById(R.id.arrival);
        TextView price = findViewById(R.id.price);
        Flight flight = (Flight)getIntent().getSerializableExtra(FlightListAdapter.FLIGHTINFO);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault());
        dep.setText(sdf.format(flight.getDepart()));
        arr.setText(sdf.format(flight.getArrive()));
        price.setText(String.valueOf(flight.getPrice()));
    }
}