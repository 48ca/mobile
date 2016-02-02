package me.jhoughton.multidrop;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by 2017jhoughto on 1/11/2016.
 */
public class Destination extends Location {
    public int time = 0;
    public String name = "";
    public Destination(String s) {
        super(LocationManager.PASSIVE_PROVIDER);
        name = s;
    }
    public Destination(int lat, int lon) {
        super(LocationManager.PASSIVE_PROVIDER);
        this.setLatitude(lat);
        this.setLongitude(lon);
    }
    public Destination(int lat, int lon, int time) {
        super(LocationManager.PASSIVE_PROVIDER);
        this.setLatitude(lat);
        this.setLongitude(lon);
        this.time = time;
    }
    public String latLongString() {
        return getLatitude()+","+getLongitude();
    }
}
