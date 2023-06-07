package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class EditProfileActivity extends AppCompatActivity {

    User[] user = new User[1];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        user[0] = (User) getIntent().getSerializableExtra(ProfileActivity.EDITPROFILE);
        ImageView picture = findViewById(R.id.profile_pic);

        EditText lastName = findViewById(R.id.lastname);
        EditText firstName = findViewById(R.id.firstname);
        EditText thirdName = findViewById(R.id.thirdname);

        Button save = findViewById(R.id.edit);
        try {
            String sh = Server.get(this).getUserInfo(res -> {}).get().getString();
            user[0] = User.listFrom(sh).get(0);
            lastName.setText(user[0].getLastName());
            firstName.setText(user[0].getFirstName());
            thirdName.setText(user[0].getThirdName());
            Picasso.get().load(
                    "http://vid16.online/userPictures/" + user[0].getProfilePic()
            ).into(picture);
        } catch (XmlPullParserException | IOException | ExecutionException | InterruptedException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        save.setOnClickListener(v -> {
            Intent result = new Intent();
            User newUser = new User(
                    user[0].getID(),
                    firstName.getText().toString(),
                    lastName.getText().toString(),
                    thirdName.getText().toString(),
                    user[0].getProfilePic()
            );
            result.putExtra(ProfileActivity.EDITPROFILE, newUser);
            setResult(RESULT_OK, result);
            finish();
        });
    }

}