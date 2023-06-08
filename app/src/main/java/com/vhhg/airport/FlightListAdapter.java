package com.vhhg.airport;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FlightListAdapter extends RecyclerView.Adapter<FlightListAdapter.ViewHolder> {
    public static final String FLIGHTINFO = "com.vhhg.FlightListAdapter.FlightInfo";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd MMM yyyy", Locale.getDefault());
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_flight_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StringBuilder txt = new StringBuilder();
        if(from && !to) txt.append("Из ");
        if(!from && to) txt.append("В ");
        if(from) txt.append(flights.get(position).getFrom());
        if(from && to) txt.append(" -> ");
        if(to) txt.append(flights.get(position).getTo());
        holder.mainText.setText(txt.toString());

        StringBuilder txt2 = new StringBuilder();
        Date date0 = flights.get(position).getDepart();
        Date date1 = flights.get(position).getArrive();
        Date now = new Date();
        holder.bottomText.setTextColor(context.getResources().getColor(R.color.gray, context.getTheme()));
        if(now.after(date1))
            txt2.append("Приземлился в ").append(sdf.format(date1));
        else if(now.after(date0))
            txt2.append("В пути");
        else if(date0.getTime() - now.getTime() > 15*60){
            txt2.append("Идет посадка до ").append(sdf.format(flights.get(position).getDepart()));
            holder.bottomText.setTextColor(context.getResources().getColor(R.color.red, context.getTheme()));
        }else
            txt2.append("Вылетает в ").append(sdf.format(date0));
        holder.bottomText.setText(txt2.toString());
        holder.fav.setChecked(flights.get(position).isFav());
        holder.info.setOnClickListener(v -> {
            Intent intent = new Intent(context, FlightInfoActivity.class);
            intent.putExtra(FLIGHTINFO, flights.get(position));
            context.startActivity(intent);
        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FlightInfoActivity.class);
            intent.putExtra(FLIGHTINFO, flights.get(position));
            context.startActivity(intent);
        });
        holder.fav.setOnClickListener( v -> {
            Flight flight = flights.get(position);
            flight.setFav(!flight.isFav());
            holder.fav.setChecked(flight.isFav());
            Server.get(context).mark(flights.get(position).getID(), res -> {});
        });
    }

    @Override
    public int getItemCount() {
        return flights.size();
    }

    private final Context context;
    private final List<Flight> flights;
    private LayoutInflater inflater;
    private boolean from, to;

    public FlightListAdapter(@NonNull Context context, List<Flight> elements, boolean from, boolean to) {
        this.context = context;
        this.flights = elements;
        inflater = LayoutInflater.from(context);
        this.from = from;
        this.to = to;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mainText = itemView.findViewById(R.id.destpoint);
        TextView bottomText = itemView.findViewById(R.id.status);
        Button info = itemView.findViewById(R.id.info);
        ToggleButton fav = itemView.findViewById(R.id.fav);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
