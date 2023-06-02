package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Xml;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;

import keys.AirportKeys;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.send);
        EditText et = findViewById(R.id.input);
        RecyclerView recyclerView = findViewById(R.id.recycler);
//        btn.setOnClickListener(v -> {
//            //String answer = Server.get().sendWithJWT(et.getText().toString());
//            String answer = "";
//            try {
//                Flight[] flights = Flight.arrayFrom(answer);
//                FlightListAdapter adapter = new FlightListAdapter(this, flights);
//                recyclerView.setAdapter(adapter);
//                recyclerView.setLayoutManager(new LinearLayoutManager(this));
//            }catch(XmlPullParserException | IOException e){
//                Toast.makeText(this, "Error parsing xml", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}