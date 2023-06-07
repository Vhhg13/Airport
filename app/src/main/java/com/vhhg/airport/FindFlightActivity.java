package com.vhhg.airport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.slider.RangeSlider;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class FindFlightActivity extends AppCompatActivity {

    private EditText src;
    private EditText dest;
    private EditText depTime;
    private EditText arrTime;
    private EditText depDate;
    private EditText arrDate;
    private RangeSlider slider;
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
        setContentView(R.layout.activity_find_flight);
        src = findViewById(R.id.departure_airport);
        dest = findViewById(R.id.arrival_airport);
        Button save = findViewById(R.id.save);
        slider = findViewById(R.id.price);
        slider.setValueFrom(0);
        slider.setValueTo(40000);
//        slider.setLeft(1000);
//        slider.setRight(10000);
        slider.setValues(1000F, 10000F);


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
        RecyclerView recycler = findViewById(R.id.recycler);

        LinkedList<Flight> flights = new LinkedList<>();


        save.setOnClickListener(v -> {
            try {
                String flightsXML = Server.get(this).findFlight(createFlight(), res -> {}).join().getString();
                Log.d("FINDFLIGHTT", flightsXML);
                Flight.listFrom(flightsXML, flights);
                recycler.setLayoutManager(new LinearLayoutManager(this));
                RecyclerView.Adapter adapter = FlightListAdapterFactory.createAdapterFor(Server.get(this).isRoot(), this, flights, true, true);
                recycler.setAdapter(adapter);
            }catch(ParseException e){
                Toast.makeText(this, "Проверьте правильность введённых дат", Toast.LENGTH_SHORT).show();
            }catch(XmlPullParserException | IOException e){
                Toast.makeText(this, "Не удалось запарсить XML", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private Flight createFlight() throws ParseException {
        //    public Flight(int ID, String from, String to, Date depart, Date arrive, double price, boolean fav) {
        SimpleDateFormat sdf = new SimpleDateFormat(TimePickerFragment.format + DatePickerFragment.format, Locale.getDefault());
        Date depart;
        StringBuilder dateBuilder = new StringBuilder();

        String str = depTime.getText().toString();
        if(!str.isEmpty()) dateBuilder.append(str).append(" ");
        else dateBuilder.append("00:00 ");

        str = depDate.getText().toString();
        if(!str.isEmpty()) dateBuilder.append(str).append(" ");
        else dateBuilder.append("01.01.1971");
        depart = sdf
                .parse(dateBuilder.toString());


        dateBuilder = new StringBuilder();
        str = arrTime.getText().toString();
        if(!str.isEmpty()) dateBuilder.append(str).append(" ");
        else dateBuilder.append("00:00 ");

        str = arrDate.getText().toString();
        if(!str.isEmpty()) dateBuilder.append(str).append(" ");
        else dateBuilder.append("01.01.3000");
        Date arrive;
        arrive = sdf
                .parse(dateBuilder.toString());

        List<Float> values = slider.getValues();

        return new Flight(
                0,
                src.getText().toString(),
                dest.getText().toString(),
                depart,
                arrive,
                values.get(0).intValue(),
                false,
                values.get(1).intValue()
        );
    }
}