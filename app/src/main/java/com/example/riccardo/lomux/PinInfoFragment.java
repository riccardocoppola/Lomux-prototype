package com.example.riccardo.lomux;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.view.*;
import java.util.ArrayList;

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
    final static String ARG_MEDIALIST = "media_list";

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
    private Link sourceLink = null;
    private ArrayList<Link> mediaLinks;


    private ImageButton share_button;
    private ImageButton arrow_button;
    private ImageButton media_button;
    private ImageButton youtube_button;
    private ImageButton spotify_button;
    private ImageButton back_button;


    private LinearLayout buttons_layout;

    private OnYoutubeClickListener youtubeListener;

    public interface OnYoutubeClickListener {
        public void onYoutubeClick();
    }

    public void reset_buttons() {


        LinearLayout.LayoutParams lp_visible = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        lp_visible.weight=1.0f;

        LinearLayout.LayoutParams lp_hidden = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        lp_hidden.weight=0.0f;

        arrow_button.setLayoutParams(lp_visible);
        share_button.setLayoutParams(lp_visible);
        media_button.setLayoutParams(lp_visible);
        youtube_button.setLayoutParams(lp_hidden);
        spotify_button.setLayoutParams(lp_hidden);
        back_button.setLayoutParams(lp_hidden);

    }

    public class MediaButtonClickListener implements View.OnClickListener {

        private LinearLayout linear_layout;
        private boolean hide;
        private boolean hasyoutubelink;
        private boolean hasspotifylink;

        public MediaButtonClickListener(LinearLayout linear_layout, boolean hide, boolean hasyoutubelink, boolean hasspotifylink) {
            this.linear_layout = linear_layout;
            this.hide = hide;
            this.hasyoutubelink = hasyoutubelink;
            this.hasspotifylink = hasspotifylink;
        }

        @Override
        public void onClick(View v) {
            ImageButton arrow_button = (ImageButton) linear_layout.findViewById(R.id.imagebutton_arrow_directions);
            ImageButton share_button = (ImageButton) linear_layout.findViewById(R.id.imagebutton_share);
            ImageButton media_button = (ImageButton) linear_layout.findViewById(R.id.imagebutton_play);
            ImageButton youtube_button = (ImageButton) linear_layout.findViewById(R.id.imagebutton_youtube);
            ImageButton spotify_button = (ImageButton) linear_layout.findViewById(R.id.imagebutton_spotify);
            ImageButton back_button = (ImageButton) linear_layout.findViewById(R.id.imagebutton_back);

            LinearLayout.LayoutParams lp_visible = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            lp_visible.weight=1.0f;

            LinearLayout.LayoutParams lp_hidden = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            lp_hidden.weight=0.0f;

            if (hide) {
                arrow_button.setLayoutParams(lp_hidden);
                share_button.setLayoutParams(lp_hidden);
                media_button.setLayoutParams(lp_hidden);
                if (hasyoutubelink)
                    youtube_button.setLayoutParams(lp_visible);
                else
                    youtube_button.setLayoutParams(lp_hidden);
                if (hasspotifylink)
                    spotify_button.setLayoutParams(lp_visible);
                else
                    spotify_button.setLayoutParams(lp_hidden);
                back_button.setLayoutParams(lp_visible);
            }
            else {
                reset_buttons();
            }


        }

    }

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

    public class ShareListener implements View.OnClickListener
    {

        private double lng;
        private double lat;
        private PackageManager pm;

        public ShareListener(double lng, double lat, PackageManager pm) {
            this.lng = lng;
            this.lat = lat;
            this.pm = pm;
        }

        @Override
        public void onClick(View v)
        {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);

            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "TEXT TO SEND");
            startActivity(Intent.createChooser(sharingIntent, "Send message via:"));

        }
    };



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            youtubeListener = (OnYoutubeClickListener) context;
        }
        catch(ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

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
        share_button = (ImageButton) rootView.findViewById(R.id.imagebutton_share);
        media_button = (ImageButton) rootView.findViewById(R.id.imagebutton_play);
        back_button = (ImageButton) rootView.findViewById(R.id.imagebutton_back);
        spotify_button = (ImageButton) rootView.findViewById(R.id.imagebutton_spotify);
        youtube_button = (ImageButton) rootView.findViewById(R.id.imagebutton_youtube);
        source_label = (TextView) rootView.findViewById(R.id.pin_fragment_layout_source_label);
        buttons_layout = (LinearLayout) rootView.findViewById(R.id.pin_fragment_layout_button_horizontal_layout);


        return rootView;


    }

    @Override
    public void onStart() {
        super.onStart();
    }




    //TODO updatepinview

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
        share_button = (ImageButton) rootView.findViewById(R.id.imagebutton_share);
        source_label = (TextView) rootView.findViewById(R.id.pin_fragment_layout_source_label);
        media_button = (ImageButton) rootView.findViewById(R.id.imagebutton_play);
        back_button = (ImageButton) rootView.findViewById(R.id.imagebutton_back);
        youtube_button = (ImageButton) rootView.findViewById(R.id.imagebutton_youtube);
        spotify_button= (ImageButton) rootView.findViewById(R.id.imagebutton_spotify);

        Bundle args = getArguments();


        mediaLinks = (ArrayList<Link>) args.getSerializable(PinInfoFragment.ARG_MEDIALIST);


        name.setText(args.getString(PinInfoFragment.ARG_NAME));
        address_textview.setText(args.getString(PinInfoFragment.ARG_ADDRESS));
        artists_textview.setText(args.getString(PinInfoFragment.ARG_ARTISTS));
        info.setText(args.getString(PinInfoFragment.ARG_INFO));

        if (args.getString(PinInfoFragment.ARG_SOURCE_NAME).compareTo("-") == 0 || args.getString(PinInfoFragment.ARG_SOURCE_NAME).trim().compareTo("") == 0) {
            source_label.setText("");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            lp.weight=0.0f;
            Log.d("sourcehide", "hide" + " " + args.getString(PinInfoFragment.ARG_SOURCE_NAME));
            source_layout.setLayoutParams(lp);
        }
        else {
            source_label.setText("Source: ");
            formore.setText(args.getString(PinInfoFragment.ARG_SOURCE_NAME));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            lp.weight=0.1f;
            Log.d("sourcehide", "not hide" + " " + args.getString(PinInfoFragment.ARG_SOURCE_NAME));
            source_layout.setLayoutParams(lp);
            sourceLink = new Link(args.getString(PinInfoFragment.ARG_SOURCE_NAME), args.getString(PinInfoFragment.ARG_SOURCE));
        }

        if (sourceLink != null) {
            formore.setOnClickListener(new URIClickListener(sourceLink.getUri()));
        }
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


        LinearLayout buttons_layout = (LinearLayout) rootView.findViewById(R.id.pin_fragment_layout_button_horizontal_layout);


        Log.d("Directions", "should create listener");
        arrow_button.setOnClickListener(new ArrowClickListener(args.getDouble(PinInfoFragment.ARG_LNG), args.getDouble(PinInfoFragment.ARG_LAT)));
        share_button.setOnClickListener(new ShareListener(args.getDouble(PinInfoFragment.ARG_LNG), args.getDouble(PinInfoFragment.ARG_LAT), getContext().getPackageManager()));

        if (mediaLinks != null) {
            for (Link l:mediaLinks) {
                Log.d("medialinks", l.getText());
            }
        }


        boolean hasyoutubelink = false;
        boolean hasspotifylink = false;
        if (mediaLinks != null && mediaLinks.size() != 0) {

            for(Link l: mediaLinks) {

                Log.d("checklinks", l.getText());

                if (l.getText().toLowerCase().equals("youtube")) {
                    Log.d("checklinks", "has youtube");
                    hasyoutubelink = true;
                }
                else if (l.getText().toLowerCase().equals("spotify")) {
                    Log.d("checklinks", "has spotify");
                    hasspotifylink = true;
                }

            }
        }


        if (!hasyoutubelink && !hasspotifylink) {
            media_button.setAlpha(0.2f);
            media_button.setEnabled(false);
        }
        else {
            media_button.setAlpha(1.0f);
            media_button.setEnabled(true);
        }

        if (hasyoutubelink) {
            youtube_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    youtubeListener.onYoutubeClick();
                }
            });
        }
            if (hasspotifylink) {

                spotify_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(isPackageInstalled("com.spotify.music",getContext().getPackageManager()))
                        {
                            String spotifyUri = "spotify:album:7rt7AxYexFTtdEqaJPekvX";
                            for (Link l : mediaLinks)
                            {
                                if (l.getText().toLowerCase().equals("spotify"))
                                {
                                    spotifyUri = l.getUri();
                                }
                            }
                            Log.d("spotify", spotifyUri);
                            Intent spotifyIntent = new Intent(Intent.ACTION_VIEW);
                            spotifyIntent.setPackage("com.spotify.music");
                            spotifyIntent.setData(Uri.parse(spotifyUri));
                             startActivity(spotifyIntent);
                        }
                        else{
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.spotify.music"));

                            startActivity(intent);
                        }
                    }
                });
        }

        media_button.setOnClickListener(new MediaButtonClickListener(buttons_layout, true, hasyoutubelink, hasspotifylink));
        back_button.setOnClickListener(new MediaButtonClickListener(buttons_layout, false, false, false));

    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    public void updatePinView(String arg_name, String arg_subtitle, String arg_firstrow, String arg_secondrow, String arg_info, String sourceName, String arg_formore, int arg_imageid, double arg_lng, double arg_lat, PinType arg_type, ArrayList<Link> media_list) {


        LinearLayout source_layout = (LinearLayout) rootView.findViewById(R.id.pin_fragment_layout_linearlayout_for_source);

        name = (TextView) rootView.findViewById(R.id.pin_fragment_layout_title);
        address_textview = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_address);
        artists_textview = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_artists);
        info = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_info);
        formore = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_formore);
        pin_fragment_image = (ImageView) rootView.findViewById(R.id.pin_fragment_layout_imageview);
        subtitle_textview = (TextView) rootView.findViewById(R.id.pin_fragment_layout_textview_subtitle);
        arrow_button = (ImageButton) rootView.findViewById(R.id.imagebutton_arrow_directions);
        share_button = (ImageButton) rootView.findViewById(R.id.imagebutton_share);
        media_button = (ImageButton) rootView.findViewById(R.id.imagebutton_play);
        back_button = (ImageButton) rootView.findViewById(R.id.imagebutton_back);
        youtube_button = (ImageButton) rootView.findViewById(R.id.imagebutton_youtube);



        source_label = (TextView) rootView.findViewById(R.id.pin_fragment_layout_source_label);
        mediaLinks = media_list;


        //TODO fare funzionare colore

        // per committ a caso

        arrow_button.setOnClickListener(new ArrowClickListener(arg_lng, arg_lat));
        share_button.setOnClickListener(new ShareListener(arg_lng, arg_lat, getContext().getPackageManager()));


        name.setText(arg_name);
        address_textview.setText(arg_firstrow);
        artists_textview.setText(arg_secondrow);
        info.setText(arg_info);
        subtitle_textview.setText(arg_subtitle);

        if (sourceName.compareTo("-") == 0 || arg_formore.trim().compareTo("") == 0) {

            source_label.setText("");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            lp.weight=0.0f;
            Log.d("sourcehide", "hide" + arg_formore);
            source_layout.setLayoutParams(lp);
        }
        else {
            source_label.setText("Source: ");
            formore.setText(sourceName);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            lp.weight=0.1f;
            Log.d("sourcehide", "not hide" + " " + arg_formore);

            source_layout.setLayoutParams(lp);
            sourceLink = new Link(sourceName, arg_formore);

        }

        if (sourceLink != null) {
            formore.setOnClickListener(new URIClickListener(sourceLink.getUri()));
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


        boolean hasyoutubelink = false;
        boolean hasspotifylink = false;
        if (mediaLinks != null && mediaLinks.size() != 0) {

            for(Link l: mediaLinks) {
                Log.d("checklinks", l.getText());

                if (l.getText().toLowerCase().equals("youtube")) {
                    Log.d("checklinks", "hasyoutubelink");

                    hasyoutubelink = true;
                }
                else if (l.getText().toLowerCase().equals("spotify")) {
                    Log.d("checklinks", "hasspotifylink");

                    hasspotifylink = true;
                }

            }
        }
        LinearLayout buttons_layout = (LinearLayout) rootView.findViewById(R.id.pin_fragment_layout_button_horizontal_layout);
        media_button.setOnClickListener(new MediaButtonClickListener(buttons_layout, true, hasyoutubelink, hasspotifylink));
        back_button.setOnClickListener(new MediaButtonClickListener(buttons_layout, false, false, false));


        if (!hasyoutubelink && !hasspotifylink) {
            media_button.setAlpha(0.2f);
            media_button.setEnabled(false);
        }
        else {
            media_button.setAlpha(1.0f);
            media_button.setEnabled(true);
        }

        if (hasyoutubelink) {
            youtube_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    youtubeListener.onYoutubeClick();
                }
            });
        }

        if (image_reference != -1 ) {
            pin_fragment_image.setImageResource(arg_imageid);
        }
        else {
            pin_fragment_image.setImageResource(R.drawable.info_pin_placeholder);
        }


    }


}
