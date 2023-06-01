package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class FlightInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_info);
        TextView dep = findViewById(R.id.departure);
        TextView arr = findViewById(R.id.arrival);
        TextView price = findViewById(R.id.price);
        Flight flight = (Flight)getIntent().getSerializableExtra(FlightListAdapter.FLIGHTINFO);
        dep.setText(flight.getDepart().toString());
        arr.setText(flight.getArrive().toString());
        price.setText(String.valueOf(flight.getPrice()));
    }
}