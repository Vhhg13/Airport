package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.function.Consumer;

public class LogActivity extends AppCompatActivity {
    public static final String ISROOT = "com.vhhg.airport.LogActivity.ISROOT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        EditText login = findViewById(R.id.login);
        EditText pwd = findViewById(R.id.pwd);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            String username = login.getText().toString();
            String password = pwd.getText().toString();
            String errorCode = Server.get(this).login(username, password, (Consumer<Server.StringHolder>) response -> {
                if(response.getString().charAt(0) == 'e') {
                    if(username.equals("root"))
                        startActivity(new Intent(this, RootMenuActivity.class));
                    else
                        startActivity(new Intent(this, MainMenuActivity.class));
                    finish();
                }
            }).join().getString();
            if(errorCode.equals("No such user"))
                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, WelcomeActivity.class));
        finish();
    }
}