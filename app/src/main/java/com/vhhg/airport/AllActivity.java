package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class AllActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);
        RecyclerView recycler = findViewById(R.id.recycler);
        try{
            Flight[] flights = Flight.arrayFrom(Server.get().sendWithJWT("getall"));
            FlightListAdapter adapter = new FlightListAdapter(this, flights);
            recycler.setAdapter(adapter);
            recycler.setLayoutManager(new LinearLayoutManager(this));
        }catch (XmlPullParserException| IOException e){
            Toast.makeText(this, "Error xmlling", Toast.LENGTH_SHORT).show();
        }
    }
}