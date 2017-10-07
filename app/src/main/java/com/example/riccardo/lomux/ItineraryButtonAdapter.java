package com.example.riccardo.lomux;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Riccardo on 31/08/2017.
 */

public class ItineraryButtonAdapter extends ArrayAdapter<Itinerary> {



    private List<Itinerary> itineraries;
    private int layoutResourceId;
    private Context context;

    public ItineraryButtonAdapter(Context context, int layoutResourceId, List<Itinerary> itineraries) {
        super(context, layoutResourceId, itineraries);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.itineraries = itineraries;
    }

    @Override
    public int getCount()
    {
        return itineraries.size();
    }

    @Override
    public Itinerary getItem(int position)
    {
        return itineraries.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View v, ViewGroup vg)
    {
        if (v==null)
        {
            v= LayoutInflater.from(context).inflate(R.layout.itinerary_button_layout, null);
        }
        Itinerary i = (Itinerary) getItem(position);
        TextView txt=(TextView) v.findViewById(R.id.itinerary_button_text);
        txt.setText(i.getName());
        return v;
    }






}
