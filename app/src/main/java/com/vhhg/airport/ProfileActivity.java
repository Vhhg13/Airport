package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ImageView picture = findViewById(R.id.profile_pic);


        TextView lastName = findViewById(R.id.lastname);
        TextView firstName = findViewById(R.id.firstname);
        TextView thirdName = findViewById(R.id.thirdname);

        Button edit = findViewById(R.id.edit);
        Button signOut = findViewById(R.id.exit);
        try {
            Server.get(this).getUserInfo(response -> {
                try {
                    User user = User.listFrom(response.getString()).get(0);
                    Log.d("USERVHHG", user.toString());
                    lastName.setText(user.getLastName());
                    firstName.setText(user.getFirstName());
                    thirdName.setText(user.getThirdName());
                    String address = "http://vid16.online/userPictures/" + user.getProfilePic();
                    Log.d("USERVHHG", address);
                    Picasso.get().load(
                            address
                    ).into(picture);
                } catch (XmlPullParserException | IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).get();
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}