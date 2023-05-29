package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class RegActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        EditText login = findViewById(R.id.login);
        EditText pwd = findViewById(R.id.pwd);
        EditText pwdAgain = findViewById(R.id.pwdagain);
        CheckBox checkBox = findViewById(R.id.consent);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            if(checkBox.isChecked()){
                String username = login.getText().toString();
                String password = pwd.getText().toString();
                String pwd2 = pwdAgain.getText().toString();
                if(password.equals(pwd2)){
                    Server.get().register(username, password);
                    Server.get().login(username, password);
                    startActivity(new Intent(this, MainActivity.class));
                }else
                    Snackbar.make(this, v, getResources().getString(R.string.passwords_dont_match), Snackbar.LENGTH_SHORT).show();

            }else
                Snackbar.make(this, v, getResources().getString(R.string.have_to_consent), Snackbar.LENGTH_SHORT).show();
        });
    }
}