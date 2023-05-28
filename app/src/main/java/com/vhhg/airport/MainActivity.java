package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Xml;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;

import keys.AirportKeys;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.send);
        EditText et = findViewById(R.id.input);
        TextView tw = findViewById(R.id.tw);
        PublicKey[] publicKey = new PublicKey[1];
        btn.setOnClickListener(v -> {
            publicKey[0] = AirportKeys.readPublicKey(Server.get().send("getkey"));
            String message = et.getText().toString();
            byte[] encrypted = AirportKeys.cipher(publicKey[0], AirportKeys.ENCRYPT, message);
            String answer = Server.get().send(new String(Base64.getEncoder().encode(encrypted)));
            XmlPullParser parser = Xml.newPullParser();
            try {
                parser.setInput(new StringReader(answer));
                StringBuilder sb = new StringBuilder();
                while(parser.getEventType()!=XmlPullParser.END_DOCUMENT){
                    if(parser.getEventType() == XmlPullParser.START_TAG
                            && parser.getName().equalsIgnoreCase("item")){
                        sb.append(parser.getAttributeValue(0));
                    }
                    if(parser.getEventType() == XmlPullParser.TEXT){
                        sb.append(parser.getText());
                    }
                    parser.next();
                }
                tw.setText(sb.toString());
            } catch (XmlPullParserException | IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        });
    }
}