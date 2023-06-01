package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

public class AllActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);
        RecyclerView recycler = findViewById(R.id.recycler);
        LinkedList<Flight> flights = new LinkedList<>();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        FlightListAdapter adapter = new FlightListAdapter(this, flights);
        recycler.setAdapter(adapter);
        try {
            Server.get().getall(response -> {
                try {
                    Flight.listFrom(response.getString(), flights);
                    adapter.notifyDataSetChanged();
                } catch (XmlPullParserException | IOException ignored) {
                    Toast.makeText(getApplicationContext(), response.getString(), Toast.LENGTH_LONG).show();
                }
            }).get();
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}