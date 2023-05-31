package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class FavsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favs);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        try {
            Flight[] flights = Flight.arrayFrom(Server.get().getFavs());
            FlightListAdapter adapter = new FlightListAdapter(this, flights);
            recyclerView.setAdapter(adapter);
        }catch(XmlPullParserException | IOException e){
            Toast.makeText(this, "Error parsing Exception FA", Toast.LENGTH_SHORT).show();
        }
    }
}