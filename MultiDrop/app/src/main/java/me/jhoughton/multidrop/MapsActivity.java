package me.jhoughton.multidrop;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        Button b = (Button) findViewById(R.id.zoom);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomOnCoords();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /*
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            Marker mMarker = mMap.addMarker(new MarkerOptions().position(loc));
            if(mMap != null){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }
        }
    };
    */

    private GoogleMap.OnMyLocationButtonClickListener myLocationButtonListener = new GoogleMap.OnMyLocationButtonClickListener() {
        @Override
        public boolean onMyLocationButtonClick() {
            try {
                Location ploc = mMap.getMyLocation();
                LatLng loc = new LatLng(ploc.getLatitude(),ploc.getLongitude());
                Marker mMarker = mMap.addMarker(new MarkerOptions().position(loc));
                if(mMap != null){
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                }
                return true;

            } catch(Exception e) {
                return false;
            }
        }
    };

    private void zoomOnCoords() {
        Intent i = getIntent();
        ArrayList<CharSequence> al = i.getCharSequenceArrayListExtra("locations");
        double lat, lon;
        int in;
        String str;
        LatLngBounds bounds = null;
        for(CharSequence cs: al) {
            str = cs.toString();
            in = str.indexOf(',');
            lat = Double.parseDouble(str.substring(0, in));
            lon = Double.parseDouble(str.substring(in + 1));
            LatLng loc = new LatLng(lat,lon);
            bounds = bounds == null ? new LatLngBounds(loc,loc): bounds.including(loc);
            // Toast.makeText(getApplicationContext(),lat+" "+lon,Toast.LENGTH_SHORT).show();
            mMap.addMarker(new MarkerOptions().position(loc));
        }
        if(bounds != null)
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,50));
        // mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng((maxlat + minlat) / 2.0, (minlon + maxlon) / 2.0)));
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setTrafficEnabled(true);
        mMap.setMyLocationEnabled(true);
        // mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        mMap.setOnMyLocationButtonClickListener(myLocationButtonListener);
    }
}
