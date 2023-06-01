package com.vhhg.airport;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RootFlightListAdapter extends RecyclerView.Adapter<RootFlightListAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView mainText = itemView.findViewById(R.id.destpoint);
        TextView bottomText = itemView.findViewById(R.id.status);
        Button edit = itemView.findViewById(R.id.edit);
        Button info = itemView.findViewById(R.id.info);
        Button remove = itemView.findViewById(R.id.del);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_root_flight_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mainText.setText(flights.get(position).getTo());
        holder.bottomText.setText(flights.get(position).getDepart().toString());
        holder.info.setOnClickListener(v -> {
            Intent intent = new Intent(context, FlightInfoActivity.class);
            intent.putExtra(FlightListAdapter.FLIGHTINFO, flights.get(position));
            context.startActivity(intent);
        });
        holder.edit.setOnClickListener(v -> {

        });
        holder.remove.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return flights.size();
    }
    private List<Flight> flights;
    private Context context;
    private LayoutInflater inflater;

    public RootFlightListAdapter(Context context, List<Flight> flights) {
        this.context = context;
        this.flights = flights;
        inflater = LayoutInflater.from(context);
    }
}
