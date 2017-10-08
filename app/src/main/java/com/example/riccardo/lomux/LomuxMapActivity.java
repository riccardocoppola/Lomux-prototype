package com.example.riccardo.lomux;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LomuxMapActivity extends AppCompatActivity implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<Pin>, ClusterManager.OnClusterItemClickListener<Pin>, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, RecyclerAdapter.OnItemClickListener, PinInfoFragment.OnYoutubeClickListener, YoutubeFragment.OnYoutubeBackListener {

    private Integer current_textview_id = -1;

    private static final int ITINERARY_DETAIL_REQUEST = 1;
    private int standard_image_resource_id;

    private GoogleMap mMap;
    private LinearLayout layout;

    private TextView shownTextView = null;
    private PinInfoFragment pinInfoFragment = null;
    private Boolean shownFragment = false;

    private String selected_itinerary = "All Pins";
    private String selected_pin = "";
    private Pin current_pin = null;

    private HashMap<Marker, Pin> markerPinHashMap = new HashMap<Marker, Pin>();
    private HashMap<String, Pin> pinSet = null;
    private TextView pinStuff = null;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private RecyclerAdapter mAdapter;

    private ClusterManager<Pin> mClusterManager;

    private boolean youtube_over = false;
    String json_string;
    String json_itinerary;

    JSONObject jsonObject;
    JSONArray jsonArray;



    @Override
    public void onYoutubeClick() {





        YoutubeFragment  yf = new YoutubeFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.lomux_map_fragment_frame, yf, "youtube").commit();

        youtube_over = true;

        ArrayList<Link> current_medialist = current_pin.getMediaList();

        String video_id = "GW3enefjwY0";

        for (Link l:current_medialist) {
            if (l.getText().toLowerCase().equals("youtube")) {
                String[] fields = l.getUri().split("=");
                video_id = fields[fields.length-1];
            }
        }

        getSupportFragmentManager().executePendingTransactions();

        yf.setVideoID(video_id);

    }

    @Override
    public void onYoutubeBack() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.lomux_map_fragment_frame, pinInfoFragment, "info").commit();

        youtube_over = false;
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    private LinkedHashMap<String, Itinerary> itineraries = null;


    private LinkedHashMap<String, Itinerary> itineraryReader() {
        LinkedHashMap<String, Itinerary> itineraries = new LinkedHashMap<String, Itinerary>();
        InputStream inputStream = getResources().openRawResource(R.raw.lomux_itineraries);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Boolean firstRow = true;

        Itinerary allpinsitinerary = new Itinerary("0", "All Pins", "All pins of current city", null);
        allpinsitinerary.setImage_reference(getApplicationContext().getResources().getIdentifier("it0square", "drawable", getApplicationContext().getPackageName()));
        allpinsitinerary.setImage_circle_reference(getApplicationContext().getResources().getIdentifier("it0circle", "drawable", getApplicationContext().getPackageName()));
        itineraries.put("0", allpinsitinerary);

        try {
            jsonObject = new JSONObject(json_itinerary);
            jsonArray = jsonObject.getJSONArray("result");
            String number,name,info;

            int count;
            for(count=0; count<jsonArray.length();count++)
            {
                JSONObject JO=jsonArray.getJSONObject(count);
                number = JO.getString("Number");
                name = JO.getString("Name");
                info = JO.getString("Info");
                Itinerary currentItinerary = new Itinerary(number, name, info, null);

                String photos_present = JO.getString("Image");

                if (photos_present.compareTo("yes") == 0) {

                    String field_square_image = new String("it" + currentItinerary.getID() + "square");
                    String field_circle_image = new String("it" + currentItinerary.getID() + "circle");

                    Log.d("string square", field_square_image);

                    int resourceIdsquare = getApplicationContext().getResources().getIdentifier(field_square_image, "drawable", getApplicationContext().getPackageName());

                    int resourceIdcircle = getApplicationContext().getResources().getIdentifier(field_circle_image, "drawable", getApplicationContext().getPackageName());
                    Log.d("square", String.valueOf(resourceIdcircle));

                    currentItinerary.setImage_reference(resourceIdsquare);
                    currentItinerary.setImage_circle_reference(resourceIdcircle);

                }

                Log.d("JSON",jsonArray.toString());
                String[] pins_string = JO.getString("Pins").split(",");
                if (pins_string[0].compareTo("-") != 0) {
                    for (String s:pins_string) {
                        String current_pin = s;
                        currentItinerary.addPin(pinSet.get(current_pin));
                        pinSet.get(current_pin).addItinerary(currentItinerary);
                    }
                }


                itineraries.put(number, currentItinerary);

            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }


        return itineraries;

    }

/*
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {

                if (firstRow) {
                    firstRow = false;
                    continue;
                }

                String[] row = csvLine.split(";");

                String number = row[0];
                String name = row[1];
                String info = row[2];

                Itinerary currentItinerary = new Itinerary(number, name, info, null);

                String photos_present = row[3];

                if (photos_present.compareTo("yes") == 0) {

                    String field_square_image = new String("it" + currentItinerary.getID() + "square");
                    String field_circle_image = new String("it" + currentItinerary.getID() + "circle");

                    Log.d("string square", field_square_image);

                    int resourceIdsquare = getApplicationContext().getResources().getIdentifier(field_square_image, "drawable", getApplicationContext().getPackageName());

                    int resourceIdcircle = getApplicationContext().getResources().getIdentifier(field_circle_image, "drawable", getApplicationContext().getPackageName());
                    Log.d("square", String.valueOf(resourceIdcircle));

                    currentItinerary.setImage_reference(resourceIdsquare);
                    currentItinerary.setImage_circle_reference(resourceIdcircle);

                }


                String[] pins_string = row[4].split(",");
                if (pins_string[0].compareTo("-") != 0) {
                    for (String s:pins_string) {
                        String current_pin = s;
                        currentItinerary.addPin(pinSet.get(current_pin));
                        pinSet.get(current_pin).addItinerary(currentItinerary);
                    }
                }


                itineraries.put(number, currentItinerary);

            }
        }
        catch (IOException ex){
            throw new RuntimeException("Error in reading CSV file");
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error in reading CSV file");
            }
        }
        return itineraries;
    }
*/
    private HashMap<String, Pin> pinReader() throws JSONException {
        HashMap<String, Pin> pinSet = new HashMap<String, Pin>();
        /* inputStream = getResources().openRawResource(R.raw.lomux_data);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Boolean firstRow = true;


        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(";");

                if (firstRow) {
                    firstRow = false;
                    continue;
                }

                PinType pinType;
                if (row[1].compareTo("V") == 0) pinType = PinType.VENUE;
                else if (row[1].compareTo("R") == 0) pinType = PinType.STUDIO;
                else if (row[1].compareTo("W") == 0) pinType = PinType.WORK;
                else if (row[1].compareTo("P") == 0) pinType = PinType.PRIVATE;
                else if (row[1].compareTo("M") == 0) pinType = PinType.MONUMENT;
                else pinType = PinType.LOTM;
*/
        PinType pinType;
        jsonObject = new JSONObject(json_string);
        jsonArray = jsonObject.getJSONArray("result");
        String type;
        Log.d("JSON", json_string);
        int count;
        for(count=0; count<jsonArray.length();count++)
        {
            JSONObject JO=jsonArray.getJSONObject(count);
            type = JO.getString("Type");
        if (type.compareTo("V") == 0) pinType = PinType.VENUE;
        else if (type.compareTo("R") == 0) pinType = PinType.STUDIO;
        else if (type.compareTo("W") == 0) pinType = PinType.WORK;
        else if (type.compareTo("P") == 0) pinType = PinType.PRIVATE;
        else if (type.compareTo("M") == 0) pinType = PinType.MONUMENT;
        else pinType = PinType.LOTM;

            String number = JO.getString("ID_STRING");
            double lat = Double.parseDouble(JO.getString("Lat").replaceAll(",","."));
            double lng = Double.parseDouble(JO.getString("Lon").replaceAll(",","."));



                Pin currentPin = new Pin(number,                              //number
                        pinType,                                                                //pinType
                        lat,
                        lng,
                        //new LatLng(lat, lng),     //lat and long in a new latlong object
                        JO.getString("Title"),     //name
                        JO.getString("Subtitle"),     //subtitle
                        JO.getString("Address"),     //address
                        JO.getString("ZIP"),     //zipcode
                        JO.getString("City"),     //city
                        JO.getString("Country"),     //country
                        JO.getString("Info"),     //info
                        JO.getString("SourceName"),    //sourceName
                        JO.getString("Source"),    //source
                        //null,       //TODO implement stuff for loading image in this case
                        JO.getString("Artist"),    //artist name
                        JO.getString("SongTitle"),    //Song title
                        JO.getString("Lyrics")     //lyrics
                        );
                // now on row[18] and row[19] we have media type (Youtube, Spotify for now) and their URI
                // we extract them here
                try {
                    Log.d("mediaload", JO.getString("MediaType"));
                    String[] medias = JO.getString("MediaType").split(",");
                    String[] mediaUri = JO.getString("MediaURL").split(",");
                    Log.d("mediaload", String.valueOf(medias.length));


                    for (int ii = 0; ii < medias.length; (ii)++) {
                        currentPin.addMedia(medias[ii], mediaUri[ii]);
                        Log.d("mediaload", currentPin.getName() + medias[ii]);

                    }
                } catch (ArrayIndexOutOfBoundsException ex )
                {
                    // no media sources found
                    Log.d("mediaload", "No media sources available");
                }



                String photos_present = JO.getString("Image");

                if (photos_present.compareTo("yes") == 0) {

                    String field_pin_image = new String("pin" + currentPin.getId());

                    int resourceIdPin = getApplicationContext().getResources().getIdentifier(field_pin_image, "drawable", getApplicationContext().getPackageName());

                    currentPin.setImage_reference(resourceIdPin);

                }


                else {
                    currentPin.setImage_reference(standard_image_resource_id);
                }



                //TODO implement stuff for loading images in the Pin
                String[] itinerary_string = JO.getString("Itinerary").split(",");

                pinSet.put(number, currentPin);

            }


/*        catch (IOException ex){
            throw new RuntimeException("Error in reading CSV file");
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error in reading CSV file");
            }
        }

*/
        return pinSet;

    }

    private void placePin(String id) {
        Pin p = pinSet.get(id);
        mClusterManager.addItem(p);
        //p.setMarker(marker);
        p.setVisible();
    }

    private void removePin(String id) {

        Marker toRemove = null;

        for (Map.Entry<Marker, Pin> m: markerPinHashMap.entrySet()) {
            if (m.getValue().getId().equals(id)) {
                Log.d("Found pin", m.getValue().getName());
                toRemove = m.getKey();
                toRemove.remove();
            }
        }

        if (toRemove != null) {
            markerPinHashMap.remove(toRemove);
        }
    }

    private void removeAllPins() {
       mClusterManager.clearItems();
    }

    private void placeItineraryPins(String id) {
        Itinerary currentItinerary = itineraries.get(id);
        HashMap<String, Pin> itineraryPins = currentItinerary.getPins();
        removeAllPins();
        for (HashMap.Entry<String, Pin> p: itineraryPins.entrySet()) {
            placePin(p.getKey());
        }
    }

    private void placeAllPins() {
        removeAllPins();
        ArrayList<PinType> icons = new ArrayList<>();
        for (Pin p: pinSet.values()) {
           // Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(p.getLat(), p.getLng())).title(p.getName()));
            mClusterManager.addItem(p);
            icons.add(p.getPintype());
            /*
            Log.d("PinName", p.getName());
            Log.d("PinType", p.getPintype().toString());
            switch (p.getPintype()) {
                case VENUE:
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_v));
                    break;
                case STUDIO:
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_r));
                    break;
                case WORK:
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_w));
                    break;
                case PRIVATE:
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_p));
                    break;
                case MONUMENT:
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_m));
                    break;
                default:
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_l));
            }
            markerPinHashMap.put(marker, p);
           // p.setMarker(marker);*/
            p.setVisible();
        }
    }


    @Override
    public void onItemClick(Itinerary itinerary) {

        if (!itinerary.getName().equals(selected_itinerary)) {
            for (int childCount = mRecyclerView.getChildCount(), i = 0; i < childCount; ++i) {
                final RecyclerAdapter.ItineraryHolder holder = (RecyclerAdapter.ItineraryHolder) mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(i));
                holder.hideItinerary();
            }
            Log.d("itinerary", "Itenerary ID: " + itinerary.getID());
            if (itinerary.getID().equals("0")) {
                placeAllPins();
                selected_itinerary = itinerary.getName();
            } else {
                placeItineraryPins(itinerary.getID());
                selected_itinerary = itinerary.getName();
            }
            mClusterManager.cluster();
            closePinFragment();

        }

    }

    @Override
    public void onItemLongClick(Itinerary itinerary){

        Log.d("longclicked", itinerary.getName());

        Intent intent = new Intent(this, ItineraryDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("itinerary", itinerary);
        intent.putExtras(bundle);

        closePinFragment();

        startActivityForResult(intent, ITINERARY_DETAIL_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ITINERARY_DETAIL_REQUEST) {
            if (resultCode == RESULT_OK) {

                Bundle resultBundle = data.getExtras();
                Pin resultPin = (Pin) resultBundle.get("pin");
                placeOnMarker(resultPin);

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lomux_map);

        json_string= getIntent().getExtras().getString("json_data");
        json_itinerary= getIntent().getExtras().getString("json_itinerary");

        standard_image_resource_id = getApplicationContext().getResources().getIdentifier("it0square", "drawable", getApplicationContext().getPackageName());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        layout = (LinearLayout) findViewById(R.id.linear_layout_map);


        try {
            pinSet = pinReader();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        itineraries = itineraryReader();


        mAdapter = new RecyclerAdapter(new ArrayList<Itinerary>(itineraries.values()), this);
        mRecyclerView.setAdapter(mAdapter);



        mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                ImageButton i = (ImageButton) view.findViewById(R.id.itinerary_button_image);
                TextView t = (TextView) view.findViewById(R.id.itinerary_button_text);
                if (!t.getText().equals(selected_itinerary)) {
                    i.setAlpha(0.5f);
                    t.setAlpha(0.5f);
                }
                else {
                    i.setAlpha(1f);
                    t.setAlpha(1f);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        });


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        float camerazoom=(float)18.0;

        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);

        // Add a marker in Sydney and move the camera
        LatLng london_center = new LatLng(51.509865, -0.118092);



        mMap.moveCamera(CameraUpdateFactory.newLatLng(london_center));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(camerazoom));

        mClusterManager = new ClusterManager<Pin>(this, mMap);
        mClusterManager.setRenderer(new PinRenderer(this.getApplicationContext(), mMap, mClusterManager));
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);

        placeAllPins();

        //final RecyclerAdapter.ItineraryHolder holder = (RecyclerAdapter.ItineraryHolder) mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(0));
        //holder.showItinerary();


    }


    public void placeOnMarker(final Pin pin) {

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(pin.getLat(), pin.getLng())));

        selected_pin = pin.getId();
        current_pin = pin;

        if (pinInfoFragment == null) {

            Bundle args = new Bundle();
            args.putString(PinInfoFragment.ARG_NAME, pin.getName());
            args.putString(PinInfoFragment.ARG_ADDRESS, pin.getAddress());
            args.putString(PinInfoFragment.ARG_ARTISTS, pin.getArtist_name());
            args.putString(PinInfoFragment.ARG_INFO, pin.getInfo());
            args.putString(PinInfoFragment.ARG_SOURCE_NAME, pin.getSource().getText());
            args.putString(PinInfoFragment.ARG_SOURCE, pin.getSource().getUri());
            args.putString(PinInfoFragment.ARG_SUBTITLE, pin.getSubtitle());
            args.putInt(PinInfoFragment.ARG_IMAGE, pin.getImage_reference());
            args.putString(PinInfoFragment.ARG_TYPE, pin.getPintype().toString());
            args.putSerializable(PinInfoFragment.ARG_MEDIALIST, pin.getMediaList());


            Log.d("stringpin", String.valueOf(pin.getImage_reference()));

            args.putDouble(PinInfoFragment.ARG_LNG, pin.getLng());
            args.putDouble(PinInfoFragment.ARG_LAT, pin.getLat());

            pinInfoFragment = new PinInfoFragment();
            pinInfoFragment.setArguments(args);


            getSupportFragmentManager().beginTransaction()
                    .add(R.id.lomux_map_fragment_frame, pinInfoFragment, "info").commit();

            FrameLayout fragment_frame = (FrameLayout) findViewById(R.id.lomux_map_fragment_frame);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            lp.weight=1.0f;
            fragment_frame.setLayoutParams(lp);

            shownFragment = true;

        }

        else  if (!shownFragment) {

            if (youtube_over) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.lomux_map_fragment_frame, pinInfoFragment, "info").commit();
                youtube_over = false;
            }

            FrameLayout fragment_frame = (FrameLayout) findViewById(R.id.lomux_map_fragment_frame);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            lp.weight=1.0f;
            fragment_frame.setLayoutParams(lp);


            pinInfoFragment.updatePinView(pin.getName(), pin.getSubtitle(), pin.getAddress(), pin.getArtist_name(), pin.getInfo(), pin.getSource().getText(), pin.getSource().getUri(), pin.getImage_reference(), pin.getLng(), pin.getLat(), pin.getPintype(), pin.getMediaList());

            shownFragment = true;
            pinInfoFragment.reset_buttons();

        }

        else {

            if (youtube_over) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.lomux_map_fragment_frame, pinInfoFragment, "info").commit();

                youtube_over = false;
            }

            getSupportFragmentManager().executePendingTransactions();

            pinInfoFragment.updatePinView(pin.getName(), pin.getSubtitle(), pin.getAddress(), pin.getArtist_name(), pin.getInfo(), pin.getSource().getText(), pin.getSource().getUri(), pin.getImage_reference(), pin.getLng(), pin.getLat(), pin.getPintype(), pin.getMediaList());
            pinInfoFragment.reset_buttons();

        }


    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        Pin pin = markerPinHashMap.get(marker);

        if (pin == null) return false;

        placeOnMarker(pin);

        return true;
    }

    public void closePinFragment() {

        if (shownFragment) {

            FrameLayout fragment_frame = (FrameLayout) findViewById(R.id.lomux_map_fragment_frame);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            lp.weight=0.0f;
            fragment_frame.setLayoutParams(lp);

            shownFragment = false;
        }

    }

    @Override
    public void onMapClick(LatLng point) {

        closePinFragment();

        if (youtube_over) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.lomux_map_fragment_frame, pinInfoFragment, "info").commit();
          youtube_over = false;
          }

        selected_pin = "";

    }


    @Override
    public boolean onClusterItemClick(Pin pin) {
        Log.d("marker", "marker pressed");
        placeOnMarker(pin);
        return true;
    }

    @Override
    public boolean onClusterClick(Cluster<Pin> cluster) {
        Log.d("marker", "marker cluster pressed");
        return true; //TODO or false?
    }
}
