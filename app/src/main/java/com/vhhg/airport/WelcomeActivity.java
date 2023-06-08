package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Button register = findViewById(R.id.register);
        Button login = findViewById(R.id.login);

        boolean signedIn = Server.get(this).checkWhetherSignedIn();
        if(signedIn && Server.get(this).isRoot()) {
            startActivity(new Intent(this, RootMenuActivity.class));
            finish();
            return;
        } else if (signedIn) {
            startActivity(new Intent(this, MainMenuActivity.class));
            finish();
            return;
        }
        register.setOnClickListener(v -> {
            startActivity(new Intent(this, RegActivity.class));
            finish();
        });
        login.setOnClickListener(v -> {
            startActivity(new Intent(this, LogActivity.class));
            finish();
        });
    }
}