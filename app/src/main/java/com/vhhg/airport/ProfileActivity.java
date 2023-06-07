package com.vhhg.airport;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ProfileActivity extends AppCompatActivity {

    public static final String EDITPROFILE = "com.vhhg.airport.EDITPROFILE";

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
        User[] user = new User[1];
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

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), res -> {
            if(res.getData() == null) return;
            user[0] = (User) res.getData().getSerializableExtra(EDITPROFILE);
            if(user[0] == null) return;
            lastName.setText(user[0].getLastName());
            firstName.setText(user[0].getFirstName());
            thirdName.setText(user[0].getThirdName());
            Picasso.get().load(
                    "http://vid16.online/userPictures/" + user[0].getProfilePic()
            ).into(picture);
            Server.get(this).setUserInfo(user[0]);
        });

        edit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            intent.putExtra(EDITPROFILE, user[0]);
            launcher.launch(intent);
        });

        signOut.setOnClickListener(v -> {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        });
    }
}