package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

public class LogActivity extends AppCompatActivity {

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
            Server.get().sendEncryptedAsync(String.format("register %s %s", username, password), response -> {
                if(response.charAt(0) == 'e'){
                    startActivity(new Intent(this, AllActivity.class));
                }else{
                    Snackbar.make(this, v, response, Snackbar.LENGTH_LONG).show();
                }
            });
        });
    }
}