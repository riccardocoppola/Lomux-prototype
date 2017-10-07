package com.example.riccardo.lomux;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.clustering.*;


/**
 * Created by Franc on 07/10/2017.
 */

public class PinRenderer extends DefaultClusterRenderer<Pin> {

    public PinRenderer(Context context, GoogleMap map, ClusterManager clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(Pin curPin, MarkerOptions markerOptions) {
        // Draw a single person.
        // Set the info window to show their name.
        switch (curPin.getPintype()) {
            case VENUE:
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_v));
                break;
            case STUDIO:
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_r));
                break;
            case WORK:
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_w));
                break;
            case PRIVATE:
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_p));
                break;
            case MONUMENT:
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_m));
                break;
            default:
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_l));

        }
    }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
}
