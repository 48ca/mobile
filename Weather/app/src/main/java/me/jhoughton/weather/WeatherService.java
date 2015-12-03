package me.jhoughton.weather;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by 2017jhoughto on 12/3/2015.
 */
public class WeatherService extends IntentService {
    private double lat;
    private double lon;

    public WeatherService() {
        super("Weather");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        // while(true) {
            getCurrentLocation();
        // }
    }

    public void getCurrentLocation() {
        LocationManager locationManager;
        String context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(context);

        final WeatherService svc = this;

        /*
        Criteria crta = new Criteria();
        crta.setAccuracy(Criteria.ACCURACY_FINE);
        crta.setAltitudeRequired(false);
        crta.setBearingRequired(false);
        crta.setCostAllowed(true);
        crta.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(crta, true);
        */
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            Toast.makeText(getApplicationContext(), "Failed to get permission", Toast.LENGTH_SHORT).show();
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, //(provider, 1000, 0,
                new LocationListener() {
                    @Override
                    public void onStatusChanged(String provider, int status,
                                                Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }

                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            if (lat != 0.0 && lon != 0.0) {
                                /*
                                System.out.println("WE GOT THE LOCATION");
                                System.out.println(lat);
                                System.out.println(lon);
                                */

                                Toast.makeText(getApplicationContext(),""+lat,Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(),""+lon,Toast.LENGTH_SHORT).show();

                                try {
                                    String json = callWebService();
                                    JSONObject jsono = new JSONObject(json);
                                    String weather = jsono.getJSONArray("weather").getJSONObject(0).get("main").toString();
                                    double temp = (32 + (9 / 5) * jsono.getJSONObject("main").getDouble("temp") - 273.15);
                                    Intent sendIntent = new Intent("me.jhoughton.BROADCAST").putExtra("weather",weather).putExtra("temp",temp);
                                    LocalBroadcastManager.getInstance(svc).sendBroadcast(sendIntent);
                                    /*
                                    Context context = getApplicationContext();
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                            .setSmallIcon(R.drawable.cloud);

                                    Intent intent = new Intent( context, MainActivity.class);
                                    PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
                                    builder.setContentIntent(pIntent);
                                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                    Notification notif = builder
                                            .setContentText((int) (temp) + " degrees Fahrenheit | " + weather)
                                            .setContentTitle("Weather")
                                            .build();
                                    mNotificationManager.notify(0, notif);
                                    */

                                } catch (IOException |JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }

                });
    }


    public String callWebService() throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://api.openweathermap.org/data/2.5/weather?" +
                String.format("lat=%f&lon=%f",lat,lon) +
                "&appid=2de143494c0b295cca9337e1e96b00e0"
        );
        // ResponseHandler<String> handler = new BasicResponseHandler();
        HttpResponse result = null;
        try {
            result = httpclient.execute(request);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        httpclient.getConnectionManager().shutdown();
        HttpEntity is = result.getEntity();
        String data = EntityUtils.toString(is);
        Toast.makeText(getApplicationContext(),result.toString(),Toast.LENGTH_LONG).show();
        return data;
    } // end callWebService()
}
