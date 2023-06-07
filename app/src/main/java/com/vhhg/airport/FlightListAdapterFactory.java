package com.vhhg.airport;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public interface FlightListAdapterFactory {
    static RecyclerView.Adapter createAdapterFor(boolean isRoot, Context context, List<Flight> flightList, boolean from, boolean to){
        if(isRoot)
            return new RootFlightListAdapter(context, flightList);
        else
            return new FlightListAdapter(context, flightList, from, to);
    }
}
