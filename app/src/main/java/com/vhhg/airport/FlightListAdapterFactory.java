package com.vhhg.airport;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public interface FlightListAdapterFactory {
    static RecyclerView.Adapter createAdapterFor(boolean isRoot, Context context, List<Flight> flightList){
        if(isRoot)
            return new RootFlightListAdapter(context, flightList);
        else
            return new FlightListAdapter(context, flightList);
    }
}
