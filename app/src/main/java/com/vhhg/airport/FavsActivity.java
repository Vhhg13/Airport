package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

public class FavsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favs);
        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        LinkedList<Flight> flights = new LinkedList<>();
        FlightListAdapter adapter = new FlightListAdapter(this, flights);
        recycler.setAdapter(adapter);
        try {
            Server.get().getFavs(response -> {
                try {
                    Flight.listFrom(response.getString(), flights);
                    adapter.notifyDataSetChanged();
                } catch (XmlPullParserException | IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).get();
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}