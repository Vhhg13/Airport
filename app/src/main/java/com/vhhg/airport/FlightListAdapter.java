package com.vhhg.airport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FlightListAdapter extends RecyclerView.Adapter<FlightListAdapter.ViewHolder> {
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_flight_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mainText.setText(flights[position].getTo());
        holder.bottomText.setText(flights[position].getDepart().toString());
        holder.fav.setChecked(flights[position].isFav());
        holder.fav.setOnClickListener( v -> {

        });
    }

    @Override
    public int getItemCount() {
        return flights.length;
    }

    private final Context context;
    private final Flight[] flights;
    private LayoutInflater inflater;

    public FlightListAdapter(@NonNull Context context, Flight[] elements) {
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
