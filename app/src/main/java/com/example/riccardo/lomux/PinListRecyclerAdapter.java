package com.example.riccardo.lomux;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Riccardo on 08/09/2017.
 */

public class PinListRecyclerAdapter extends RecyclerView.Adapter<PinListRecyclerAdapter.PinListHolder> {


    public interface PinListClickListener{
        void onShowPinClick(Pin pin);
    }

    private LinkedList<Pin> pins;
    private PinListClickListener listener;

    @Override
    public int getItemCount() {
        return pins.size();
    }


    public PinListRecyclerAdapter(LinkedList<Pin> pins, PinListClickListener listener) {
        this.pins = pins;
        this.listener = listener;
    }

    @Override
    public PinListRecyclerAdapter.PinListHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itinerary_details_pin, parent, false);
        return new PinListHolder(inflatedView);
    }


    @Override
    public void onBindViewHolder(PinListRecyclerAdapter.PinListHolder holder, int position) {
        Pin itemPin = pins.get(position);
        holder.bindPin(itemPin, listener);



    }




    public static class PinListHolder extends RecyclerView.ViewHolder {


        private PinListClickListener listener;

        private TextView holderTextName;
        private TextView holderTextPlace;
        private TextView holderTextAdditional; // to become subtitle
        private TextView holderTextArtists;
        private TextView holderTextSubtitle;
        private TextView holderTextInfo;
        private TextView holderTextForMore;
        private ImageView holderImage;
        private Button placeInMapButton;
        View v;

        private Pin pin;

        public PinListHolder (View v){
            super(v);

            holderTextName = (TextView) v.findViewById(R.id.itinerary_details_pin_layout_title);
            holderTextPlace = (TextView) v.findViewById(R.id.itinerary_details_pin_layout_textview_place);
            holderTextSubtitle = (TextView) v.findViewById(R.id.itinerary_details_pin_layout_textview_subtitle);
            holderTextArtists = (TextView) v.findViewById(R.id.itinerary_details_pin_layout_textview_artists);


            holderImage = (ImageView) v.findViewById(R.id.itinerary_details_pin_layout_imageview);

            placeInMapButton = (Button) v.findViewById(R.id.itinerary_details_pin_placeinmapbutton);
           this.v=v;

        }


        public void bindPin(final Pin pin, final PinListClickListener listener){

            this.pin = pin;
            this.listener = listener;

            if (pin.getImage_reference() != -1) {
                holderImage.setImageResource(pin.getImage_reference());
            }

            holderTextName.setText(pin.getName());
            holderTextPlace.setText(pin.getAddress());
            holderTextArtists.setText(pin.getArtist_name());
            holderTextSubtitle.setText(pin.getSubtitle());


            //holderTextAdditional.setText(pin.getArtist_name());
            //holderTextInfo.setText(pin.getInfo());
            //holderTextForMore.setText(pin.getSource());

            switch(pin.getPintype()) {
                case PRIVATE:
                    holderTextName.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPrivate));
                    break;
                case WORK:
                    holderTextName.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorWork));
                    break;
                case STUDIO:
                    holderTextName.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorStudio));
                    break;
                case MONUMENT:
                    holderTextName.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorMonument));
                    break;
                case VENUE:
                    holderTextName.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorVenue));
                    break;
                case LOTM:
                    holderTextName.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorLOTM));
                    break;
                default:
                    holderTextName.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary));
                    break;
            }


            if (pin.getImage_reference() != -1 ) {
                //holderImage.setImageResource(pin.getImage_reference());
                Picasso.with(v.getContext()).load(pin.getImageUrl()).into(holderImage);
            }

            placeInMapButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    listener.onShowPinClick(pin);
                }

            });


        }

    }








}
