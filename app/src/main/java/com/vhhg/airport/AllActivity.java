package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
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
        RecyclerView.Adapter adapter = FlightListAdapterFactory.createAdapterFor(Server.get(this).isRoot(), this, flights);

//        if(getIntent().getStringExtra(LogActivity.ISROOT) != null && getIntent().getStringExtra(LogActivity.ISROOT).equals("NO"))
//            adapter = new RootFlightListAdapter(this, flights);
//        else
//            adapter = new FlightListAdapter(this, flights);

        recycler.setAdapter(adapter);
        try {
            Server.get(this).getall(response -> {
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