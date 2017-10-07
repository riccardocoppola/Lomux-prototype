package com.example.riccardo.lomux;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Riccardo on 31/08/2017.
 */

public class PinInfoFragment extends Fragment {

    final static String ARG_NAME = "name";
    final static String ARG_ADDRESS = "firstline";
    final static String ARG_ARTISTS = "secondline";
    final static String ARG_INFO = "info";
    final static String ARG_SOURCE_NAME = "sourcename";
    final static String ARG_SOURCE = "source";
    final static String ARG_IMAGE = "image_ref";
    final static String ARG_LNG = "longitude";
    final static String ARG_LAT = "latitude";
    final static String ARG_SUBTITLE = "subtitle";
    final static String ARG_TYPE = "type";

    View rootView;

    private TextView name;
    private TextView address_textview;
    private TextView artists_textview;
    private TextView info;
    private TextView formore;
    private TextView subtitle_textview;
    private TextView source_label;
    private ImageView pin_fragment_image;
    private int image_reference;

    private ImageButton arrow_button;

    public class ArrowClickListener implements View.OnClickListener
    {

        private double lng;
        private double lat;

        public ArrowClickListener(double lng, double lat) {
            this.lng = lng;
            this.lat = lat;
            Log.d("Directions", "created listener");
        }

        @Override
        public void onClick(View v)
        {
            Log.d("Directions", String.valueOf(lng) + " - " + String.valueOf(lat));
            Uri gmmIntentUri = Uri.parse("google.navigation:q="+String.valueOf(lat)+","+String.valueOf(lng));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);

        }

    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_pin_layout, container, false);
        updatePinView();

        name = (TextView) rootView.findViewById(R.id.pin_fragment_layout_title);
        address_textview = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_address);
        artists_textview = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_artists);
        info = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_info);
        formore = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_formore);
        pin_fragment_image = (ImageView) rootView.findViewById(R.id.pin_fragment_layout_imageview);
        subtitle_textview = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_subtitle);
        arrow_button = (ImageButton) rootView.findViewById(R.id.imagebutton_arrow_directions);
        source_label = (TextView) rootView.findViewById(R.id.pin_fragment_layout_source_label);




        return rootView;


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void updatePinView() {

        LinearLayout source_layout = (LinearLayout) rootView.findViewById(R.id.pin_fragment_layout_linearlayout_for_source);

        name = (TextView) rootView.findViewById(R.id.pin_fragment_layout_title);
        address_textview = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_address);
        artists_textview = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_artists);
        info = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_info);
        formore = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_formore);
        pin_fragment_image = (ImageView) rootView.findViewById(R.id.pin_fragment_layout_imageview);
        subtitle_textview = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_subtitle);
        arrow_button = (ImageButton) rootView.findViewById(R.id.imagebutton_arrow_directions);
        source_label = (TextView) rootView.findViewById(R.id.pin_fragment_layout_source_label);


        Bundle args = getArguments();


        name.setText(args.getString(PinInfoFragment.ARG_NAME));
        address_textview.setText(args.getString(PinInfoFragment.ARG_ADDRESS));
        artists_textview.setText(args.getString(PinInfoFragment.ARG_ARTISTS));
        info.setText(args.getString(PinInfoFragment.ARG_INFO));

        if (args.getString(PinInfoFragment.ARG_SOURCE).compareTo("-") == 0 || args.getString(PinInfoFragment.ARG_SOURCE).trim().compareTo("") == 0) {
            source_label.setText("");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            lp.weight=0.0f;
            Log.d("sourcehide", "hide" + " " + args.getString(PinInfoFragment.ARG_SOURCE));
            source_layout.setLayoutParams(lp);
        }
        else {
            source_label.setText("Source: ");
            formore.setText(args.getString(PinInfoFragment.ARG_SOURCE));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            lp.weight=0.1f;
            Log.d("sourcehide", "not hide" + " " + args.getString(PinInfoFragment.ARG_SOURCE));
            source_layout.setLayoutParams(lp);
        }

        formore.setText(args.getString(PinInfoFragment.ARG_SOURCE));
        subtitle_textview.setText(args.getString(PinInfoFragment.ARG_SUBTITLE));

        String pin_type = args.getString(PinInfoFragment.ARG_TYPE);
        Log.d("pintypeforcolor", pin_type);

        LinearLayout layout_name = (LinearLayout) rootView.findViewById(R.id.pin_fragment_layout_linear_layout_for_title);

        ImageView layout_image = (ImageView) rootView.findViewById(R.id.pin_fragment_layout_title_image);


        if (pin_type.compareTo("WORK") == 0) {
            layout_name.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorWork));
            layout_image.setImageResource(R.drawable.title_layout_w);
        }
        else if (pin_type.compareTo("PRIVATE") == 0) {
            layout_name.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorPrivate));
            layout_image.setImageResource(R.drawable.title_layout_p);
        }
        else if (pin_type.compareTo("STUDIO") == 0) {
            layout_name.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorStudio));
            layout_image.setImageResource(R.drawable.title_layout_r);
        }
        else if (pin_type.compareTo("MONUMENT") == 0) {
            layout_name.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorMonument));
            layout_image.setImageResource(R.drawable.title_layout_m);
        }
        else if (pin_type.compareTo("VENUE") == 0) {
            layout_name.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorVenue));
            layout_image.setImageResource(R.drawable.title_layout_v);
        }
        else {
            layout_name.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorLOTM));
            layout_image.setImageResource(R.drawable.title_layout_l);
        }


        image_reference = args.getInt(PinInfoFragment.ARG_IMAGE);


        if (image_reference != -1 ) {
            pin_fragment_image.setImageResource(image_reference);
        }
        else {
            pin_fragment_image.setImageResource(R.drawable.info_pin_placeholder);
        }




        Log.d("Directions", "should create listener");
        arrow_button.setOnClickListener(new ArrowClickListener(args.getDouble(PinInfoFragment.ARG_LNG), args.getDouble(PinInfoFragment.ARG_LAT)));


    }

    public void updatePinView(String arg_name, String arg_subtitle, String arg_firstrow, String arg_secondrow, String arg_info, String arg_formore, int arg_imageid, double arg_lng, double arg_lat, PinType arg_type) {


        LinearLayout source_layout = (LinearLayout) rootView.findViewById(R.id.pin_fragment_layout_linearlayout_for_source);

        name = (TextView) rootView.findViewById(R.id.pin_fragment_layout_title);
        address_textview = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_address);
        artists_textview = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_artists);
        info = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_info);
        formore = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_formore);
        pin_fragment_image = (ImageView) rootView.findViewById(R.id.pin_fragment_layout_imageview);
        subtitle_textview = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_subtitle);
        arrow_button = (ImageButton) rootView.findViewById(R.id.imagebutton_arrow_directions);
        source_label = (TextView) rootView.findViewById(R.id.pin_fragment_layout_source_label);



        //TODO fare funzionare colore



        arrow_button.setOnClickListener(new ArrowClickListener(arg_lng, arg_lat));


        name.setText(arg_name);
        address_textview.setText(arg_firstrow);
        artists_textview.setText(arg_secondrow);
        info.setText(arg_info);
        subtitle_textview.setText(arg_subtitle);

        if (arg_formore.compareTo("-") == 0 || arg_formore.trim().compareTo("") == 0) {

            source_label.setText("");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            lp.weight=0.0f;
            Log.d("sourcehide", "hide" + arg_formore);
            source_layout.setLayoutParams(lp);
        }
        else {
            source_label.setText("Source: ");
            formore.setText(arg_formore);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            lp.weight=0.1f;
            Log.d("sourcehide", "not hide" + " " + arg_formore);

            source_layout.setLayoutParams(lp);
        }

        LinearLayout layout_name = (LinearLayout) rootView.findViewById(R.id.pin_fragment_layout_linear_layout_for_title);
        ImageView layout_image = (ImageView) rootView.findViewById(R.id.pin_fragment_layout_title_image);

        switch(arg_type) {
            case PRIVATE:
                layout_name.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorPrivate));
                layout_image.setImageResource(R.drawable.title_layout_p);
                break;
            case WORK:
                layout_name.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorWork));
                layout_image.setImageResource(R.drawable.title_layout_w);
                break;
            case STUDIO:
                layout_name.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorStudio));
                layout_image.setImageResource(R.drawable.title_layout_r);
                break;
            case MONUMENT:
                layout_name.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorMonument));
                layout_image.setImageResource(R.drawable.title_layout_m);
                break;
            case VENUE:
                layout_name.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorVenue));
                layout_image.setImageResource(R.drawable.title_layout_v);
                break;
            case LOTM:
                layout_name.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorLOTM));
                layout_image.setImageResource(R.drawable.title_layout_l);
                break;
            default:
                layout_name.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorPrimary));
                layout_name.setBackgroundResource(R.color.colorPrimary);
                Log.d("colorz", String.valueOf(R.color.colorPrimary));
                break;
        }

        //TODO aggiungere switch sul tipo e linearlayout con una piccola immagine


        if (image_reference != -1 ) {
            pin_fragment_image.setImageResource(arg_imageid);
        }
        else {
            pin_fragment_image.setImageResource(R.drawable.info_pin_placeholder);
        }


    }


}
