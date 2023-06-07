package com.vhhg.airport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class RootMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_menu);
        Button all = findViewById(R.id.all);
        Button find = findViewById(R.id.find_flight);
        Button add = findViewById(R.id.add_flight);
        Button users = findViewById(R.id.users);

        all.setOnClickListener(v -> {
            startActivity(new Intent(this, AllActivity.class));
        });

        find.setOnClickListener(v ->{
            startActivity(new Intent(this, FindFlightActivity.class));
        });
        add.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateFlightActivity.class));
        });
        users.setOnClickListener(v -> startActivity(new Intent(this, UsersActivity.class)));

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.root_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.profile)
            startActivity(new Intent(this, ProfileActivity.class));
        else if(item.getItemId() == R.id.sign_out){
            Server.get(this).logout();
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}