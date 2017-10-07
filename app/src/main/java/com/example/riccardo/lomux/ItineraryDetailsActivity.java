package com.example.riccardo.lomux;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;

public class ItineraryDetailsActivity extends AppCompatActivity implements PinListRecyclerAdapter.PinListClickListener{

    @Override
    public void onShowPinClick(Pin pin) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("pin", pin);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }


    private Itinerary itinerary;

    private TextView itinerary_name_view;
    private ImageView itinerary_image_view;
    private TextView itinerary_description_view;

    private RecyclerView itinerary_details_recyclerview;
    private LinearLayoutManager mLinearLayoutManager;
    private PinListRecyclerAdapter mPinListRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_details);


        itinerary = (Itinerary) getIntent().getExtras().getSerializable("itinerary");

       // itinerary_name_view = (TextView) findViewById(R.id.itinerary_details_layout_textview_name);
        itinerary_description_view = (TextView) findViewById(R.id.itinerary_details_layout_textview_info);
        itinerary_image_view = (ImageView) findViewById(R.id.itinerary_details_layout_imageview);

       // itinerary_name_view.setText(itinerary.getName());
        itinerary_description_view.setText(itinerary.getInfo());
        itinerary_image_view.setImageResource(itinerary.getImage_reference());

        getSupportActionBar().setTitle(itinerary.getName());

        itinerary_details_recyclerview = (RecyclerView) findViewById(R.id.itinerary_details_layout_recyclerview);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        itinerary_details_recyclerview.setLayoutManager(mLinearLayoutManager);

        mPinListRecyclerAdapter = new PinListRecyclerAdapter(new LinkedList<Pin>(itinerary.getPins().values()), this);
        itinerary_details_recyclerview.setAdapter(mPinListRecyclerAdapter);

    }
}
