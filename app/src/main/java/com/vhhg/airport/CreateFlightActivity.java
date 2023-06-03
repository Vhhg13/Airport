package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.javafaker.Faker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateFlightActivity extends AppCompatActivity {

    private EditText src;
    private EditText dest;
    private EditText depTime;
    private EditText arrTime;
    private EditText price;
    private EditText depDate;
    private EditText arrDate;

    private View.OnClickListener getTimeListener(EditText view){
        return v -> {
            DialogFragment timePicker = new TimePickerFragment(view);
            timePicker.show(getSupportFragmentManager(), "timePicker");
        };
    }
    private View.OnClickListener getDateListener(EditText view){
        return v -> {
            DialogFragment newFragment = new DatePickerFragment(view);
            newFragment.show(getSupportFragmentManager(), "datePicker");
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_flight);

        src = findViewById(R.id.departure_airport);
        dest = findViewById(R.id.arrival_airport);
        price = findViewById(R.id.price);
        Button save = findViewById(R.id.save);


        depTime = findViewById(R.id.departure_time);
        depTime.setOnClickListener(getTimeListener(depTime));
        findViewById(R.id.dep_time_btn).setOnClickListener(getTimeListener(depTime));

        depDate = findViewById(R.id.departure_date);
        depDate.setOnClickListener(getDateListener(depDate));
        findViewById(R.id.dep_date_btn).setOnClickListener(getDateListener(depDate));

        arrTime = findViewById(R.id.arrival_time);
        arrTime.setOnClickListener(getTimeListener(arrTime));
        findViewById(R.id.arr_time_btn).setOnClickListener(getTimeListener(arrTime));

        arrDate = findViewById(R.id.arrival_date);
        arrDate.setOnClickListener(getDateListener(arrDate));
        findViewById(R.id.arr_date_btn).setOnClickListener(getDateListener(arrDate));


        save.setOnClickListener(v -> {
            try {
                Server.get(this).addflight(createFlight());
            }catch(ParseException e){
                Toast.makeText(this, "Проверьте верность введённых дат", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Рейс создан", Toast.LENGTH_SHORT).show();
            finish();
        });
        Button random = findViewById(R.id.random);
        random.setOnClickListener(v -> {
            src.setText(Faker.instance().address().cityPrefix());
            dest.setText(Faker.instance().address().city());
            price.setText(String.valueOf((int)(Math.random()*10000)));
            arrTime.setText(""+((int)(Math.random()*100))%12+":"+((int)(Math.random()*100))%60);
            SimpleDateFormat sdf = new SimpleDateFormat(DatePickerFragment.format, Locale.getDefault());
            arrDate.setText(sdf.format(Faker.instance().date().birthday()));
            depTime.setText(""+((int)(Math.random()*100))%12+":"+((int)(Math.random()*100))%60);
            depDate.setText(sdf.format(Faker.instance().date().birthday()));
        });
    }

        private Flight createFlight() throws ParseException {
        //    public Flight(int ID, String from, String to, Date depart, Date arrive, double price, boolean fav) {
            SimpleDateFormat sdf = new SimpleDateFormat(TimePickerFragment.format + DatePickerFragment.format, Locale.getDefault());
            Log.e("NYTAG", TimePickerFragment.format + DatePickerFragment.format);
            Date depart = sdf
                    .parse(depTime.getText().toString() + " " + depDate.getText().toString());
            Date arrive = sdf
                    .parse(arrTime.getText().toString() + " " + arrDate.getText().toString());
            Log.e("NYTAG", sdf.format(depart));
            return new Flight(
                    0,
                    src.getText().toString(),
                    dest.getText().toString(),
                    depart,
                    arrive,
                    Integer.parseInt(price.getText().toString()),
                    false
            );
    }
}