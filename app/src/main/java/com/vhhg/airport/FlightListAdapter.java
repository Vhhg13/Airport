package com.vhhg.airport;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FlightListAdapter extends RecyclerView.Adapter<FlightListAdapter.ViewHolder> {
    public static final String FLIGHTINFO = "com.vhhg.FlightListAdapter.FlightInfo";
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_flight_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mainText.setText(flights.get(position).getTo());
        holder.bottomText.setText(flights.get(position).getDepart().toString());
        holder.fav.setChecked(flights.get(position).isFav());
        holder.info.setOnClickListener(v -> {
            Intent intent = new Intent(context, FlightInfoActivity.class);
            intent.putExtra(FLIGHTINFO, flights.get(position));
            context.startActivity(intent);
        });
        holder.fav.setOnClickListener( v -> {
            Flight flight = flights.get(position);
            flight.setFav(!flight.isFav());
            holder.fav.setChecked(flight.isFav());
            Server.get().mark(flights.get(position).getID(), res -> {});
        });
    }

    @Override
    public int getItemCount() {
        return flights.size();
    }

    private final Context context;
    private final List<Flight> flights;
    private LayoutInflater inflater;

    public FlightListAdapter(@NonNull Context context, List<Flight> elements) {
        this.context = context;
        this.flights = elements;
        inflater = LayoutInflater.from(context);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mainText = itemView.findViewById(R.id.destpoint);
        TextView bottomText = itemView.findViewById(R.id.status);
        ImageButton info = itemView.findViewById(R.id.info);
        ToggleButton fav = itemView.findViewById(R.id.fav);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
