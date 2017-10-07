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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LomuxMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, RecyclerAdapter.OnItemClickListener {

    private Integer current_textview_id = -1;

    private static final int ITINERARY_DETAIL_REQUEST = 1;
    private int standard_image_resource_id;

    private GoogleMap mMap;
    private LinearLayout layout;

    private TextView shownTextView = null;
    private PinInfoFragment pinInfoFragment = null;
    private Boolean shownFragment = false;

    private String selected_itinerary = "All Pins";
    private int selected_pin = -1;

    private HashMap<Marker, Pin> markerPinHashMap = new HashMap<Marker, Pin>();
    private HashMap<Integer, Pin> pinSet = null;
    private TextView pinStuff = null;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private RecyclerAdapter mAdapter;


    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    private LinkedHashMap<Integer, Itinerary> itineraries = null;


    private LinkedHashMap<Integer, Itinerary> itineraryReader() {
        LinkedHashMap<Integer, Itinerary> itineraries = new LinkedHashMap<Integer, Itinerary>();
        InputStream inputStream = getResources().openRawResource(R.raw.lomux_itineraries);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Boolean firstRow = true;

        Itinerary allpinsitinerary = new Itinerary(0, "All Pins", "All pins of current city", null);
        allpinsitinerary.setImage_reference(getApplicationContext().getResources().getIdentifier("it0square", "drawable", getApplicationContext().getPackageName()));
        allpinsitinerary.setImage_circle_reference(getApplicationContext().getResources().getIdentifier("it0circle", "drawable", getApplicationContext().getPackageName()));
        itineraries.put(0, allpinsitinerary);



        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {

                if (firstRow) {
                    firstRow = false;
                    continue;
                }

                String[] row = csvLine.split(";");

                int number = Integer.parseInt(row[0]);
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
                        int current_pin = Integer.parseInt(s);
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

    private HashMap<Integer, Pin> pinReader() {
        HashMap<Integer, Pin> pinSet = new HashMap<Integer, Pin>();
        InputStream inputStream = getResources().openRawResource(R.raw.lomux_data);
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

                int number = Integer.parseInt(row[0]);
                double lat = Double.parseDouble(row[2].replaceAll(",","."));
                double lng = Double.parseDouble(row[3].replaceAll(",","."));


                for (String s:row) {
                    Log.d("Elements", s);
                }
                Log.d("Pin", String.valueOf(row[11]));
                Pin currentPin = new Pin(number,                              //number
                        pinType,                                                                //pinType
                        lat,
                        lng,
                        //new LatLng(lat, lng),     //lat and long in a new latlong object
                        row[4],     //name
                        row[5],     //subtitle
                        row[6],     //address
                        row[7],     //zipcode
                        row[8],     //city
                        row[9],     //country
                        row[10],     //info
                        row[12],    //sourceName
                        row[13],    //source
                        //null,       //TODO implement stuff for loading image in this case
                        row[14],    //artist name
                        row[15],    //Song title
                        row[16]     //lyrics
                        );

                String photos_present = row[13];

                if (photos_present.compareTo("yes") == 0) {

                    String field_pin_image = new String("pin" + currentPin.getId());

                    int resourceIdPin = getApplicationContext().getResources().getIdentifier(field_pin_image, "drawable", getApplicationContext().getPackageName());

                    currentPin.setImage_reference(resourceIdPin);

                }


                else {
                    currentPin.setImage_reference(standard_image_resource_id);
                }



                //TODO implement stuff for loading images in the Pin
                String[] itinerary_string = row[10].split(",");

                pinSet.put(number, currentPin);

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


        return pinSet;

    }

    private void placePin(int id) {
        Pin p = pinSet.get(id);
        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(p.getLat(), p.getLng())).title(p.getName()));
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
        //p.setMarker(marker);
        p.setVisible();
    }

    private void removePin(int id) {

        Marker toRemove = null;

        for (Map.Entry<Marker, Pin> m: markerPinHashMap.entrySet()) {
            if (m.getValue().getId() == id) {
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

        Marker toRemove = null;

        for (Map.Entry<Marker, Pin> m: markerPinHashMap.entrySet()) {
            toRemove = m.getKey();
            toRemove.remove();
        }

        markerPinHashMap.clear();

    }

    private void placeItineraryPins(int id) {
        Itinerary currentItinerary = itineraries.get(id);
        HashMap<Integer, Pin> itineraryPins = currentItinerary.getPins();
        removeAllPins();
        for (HashMap.Entry<Integer, Pin> p: itineraryPins.entrySet()) {
            placePin(p.getKey());
        }
    }

    private void placeAllPins() {
        removeAllPins();
        for (Pin p: pinSet.values()) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(p.getLat(), p.getLng())).title(p.getName()));
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
           // p.setMarker(marker);
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

            if (itinerary.getID() == 0) {
                placeAllPins();
                selected_itinerary = itinerary.getName();
            } else {
                placeItineraryPins(itinerary.getID());
                selected_itinerary = itinerary.getName();
            }
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


        standard_image_resource_id = getApplicationContext().getResources().getIdentifier("it0square", "drawable", getApplicationContext().getPackageName());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        layout = (LinearLayout) findViewById(R.id.linear_layout_map);



        pinSet = pinReader();
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

        placeAllPins();

        //final RecyclerAdapter.ItineraryHolder holder = (RecyclerAdapter.ItineraryHolder) mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(0));
        //holder.showItinerary();


    }


    public void placeOnMarker(final Pin pin) {

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(pin.getLat(), pin.getLng())));

        selected_pin = pin.getId();

        if (pinInfoFragment == null) {



            Bundle args = new Bundle();
            args.putString(PinInfoFragment.ARG_NAME, pin.getName());
            args.putString(PinInfoFragment.ARG_ADDRESS, pin.getAddress());
            args.putString(PinInfoFragment.ARG_ARTISTS, pin.getArtist_name());
            args.putString(PinInfoFragment.ARG_INFO, pin.getInfo());
            args.putString(PinInfoFragment.ARG_SOURCE, pin.getSource());
            args.putString(PinInfoFragment.ARG_SUBTITLE, pin.getSubtitle());
            args.putInt(PinInfoFragment.ARG_IMAGE, pin.getImage_reference());
            args.putString(PinInfoFragment.ARG_TYPE, pin.getPintype().toString());

            Log.d("stringpin", String.valueOf(pin.getImage_reference()));

            args.putDouble(PinInfoFragment.ARG_LNG, pin.getLng());
            args.putDouble(PinInfoFragment.ARG_LAT, pin.getLat());

            pinInfoFragment = new PinInfoFragment();
            pinInfoFragment.setArguments(args);




            getSupportFragmentManager().beginTransaction()
                    .add(R.id.lomux_map_fragment_frame, pinInfoFragment).commit();

            FrameLayout fragment_frame = (FrameLayout) findViewById(R.id.lomux_map_fragment_frame);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            lp.weight=1.0f;
            fragment_frame.setLayoutParams(lp);

            shownFragment = true;

        }

        else  if (!shownFragment) {

            FrameLayout fragment_frame = (FrameLayout) findViewById(R.id.lomux_map_fragment_frame);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            lp.weight=1.0f;
            fragment_frame.setLayoutParams(lp);


            pinInfoFragment.updatePinView(pin.getName(), pin.getSubtitle(), pin.getAddress(), pin.getArtist_name(), pin.getInfo(), pin.getSource(), pin.getImage_reference(), pin.getLng(), pin.getLat(), pin.getPintype());

            shownFragment = true;
        }

        else {
            pinInfoFragment.updatePinView(pin.getName(), pin.getSubtitle(), pin.getAddress(), pin.getArtist_name(), pin.getInfo(), pin.getSource(), pin.getImage_reference(), pin.getLng(), pin.getLat(), pin.getPintype());

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

        selected_pin = -1;

    }


}
