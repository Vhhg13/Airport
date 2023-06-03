package com.vhhg.airport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Button all = findViewById(R.id.all);
        Button dep = findViewById(R.id.departures);
        Button arr = findViewById(R.id.arrivals);
        Button favs = findViewById(R.id.favs);

        all.setOnClickListener(v -> startActivity(new Intent(this, AllActivity.class)));
        favs.setOnClickListener(v -> startActivity(new Intent(this, FavsActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.favs)
            startActivity(new Intent(this, FavsActivity.class));
        else if(item.getItemId() == R.id.profile)
            startActivity(new Intent(this, ProfileActivity.class));
        else if(item.getItemId() == R.id.sign_out){
            Server.get(this).logout();
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}