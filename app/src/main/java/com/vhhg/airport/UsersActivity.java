package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {
    private UserListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);
        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        LinkedList<User> users = new LinkedList<>();
        adapter = new UserListAdapter(this, users);
        recycler.setAdapter(adapter);
        Server.get(this).getUsers(res -> {
            try {
                User.listFrom(res.getString(), users);
                adapter.notifyDataSetChanged();
            }catch(XmlPullParserException | IOException e){
                Toast.makeText(this, "Не удалось запарсить XML", Toast.LENGTH_SHORT).show();
            }
        }).join();

    }
}