package com.vhhg.airport;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

public class AllActivity extends AppCompatActivity {
    public static final String FROMTO = "com.vhhg.airport.FROMTO";

    LinkedList<Flight> flights = new LinkedList<>();
    RecyclerView.Adapter adapter;
    RecyclerView recycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);
        recycler = findViewById(R.id.recycler);


        recycler.setLayoutManager(new LinearLayoutManager(this));
        boolean[] fromto;
        fromto = getIntent().getBooleanArrayExtra(FROMTO);
        if(fromto == null) fromto = new boolean[]{false, false};

        adapter = FlightListAdapterFactory.createAdapterFor(Server.get(this).isRoot(), this, flights, fromto[0], fromto[1]);

        recycler.setAdapter(adapter);
        displayFlights();
        recycler.invalidate();
    }

    private void displayFlights(){
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.all_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        flights.clear();
//        displayFlights();
//        recycler.invalidate();
//        return super.onOptionsItemSelected(item);
//    }
}