package me.jhoughton.weather;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;
    private double lat;
    private double lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                // .addOnConnectionFailedListener(this)
                .build();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // onConnected(null);
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int a) {

    }
    public void getCurrentLocation() {
        LocationManager locationManager;
        String context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(context);
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
            Toast.makeText(getApplicationContext(), "Failed to get permission",Toast.LENGTH_SHORT).show();
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
                                    TextView tv = (TextView) findViewById(R.id.tv);
                                    String weather = jsono.getJSONArray("weather").getJSONObject(0).get("main").toString();
                                    tv.setText(weather);
                                    Context context = getApplicationContext();
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                            .setSmallIcon(R.mipmap.ic_launcher);

                                    Intent intent = new Intent( context, MainActivity.class);
                                    PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
                                    builder.setContentIntent(pIntent);
                                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                    Notification notif = builder
                                            .setContentText((int)((32 + (9 / 5) * (jsono.getJSONObject("main").getDouble("temp") - 273.15))) + " degrees Fahrenheit | "+weather)
                                            .setContentTitle("Weather")
                                            .build();
                                    mNotificationManager.notify(0, notif);
                                } catch (IOException |JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }

                });
    }
    @Override
    public void onConnected(Bundle connectionHint) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();
        }
        // Toast.makeText(getApplicationContext(),"SET LAT AND LONG",Toast.LENGTH_LONG).show();
    }

    public String callWebService() throws IOException{
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://api.openweathermap.org/data/2.5/weather?" +
            String.format("lat=%f&lon=%f",lat,lon) +
            "&appid=2de143494c0b295cca9337e1e96b00e0"
        );
        ResponseHandler<String> handler = new BasicResponseHandler();
        HttpResponse result = null;
        try {
            result = httpclient.execute(request);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        httpclient.getConnectionManager().shutdown();
        HttpEntity is = result.getEntity();
        String data = EntityUtils.toString(is);
        // Toast.makeText(getApplicationContext(),result.toString(),Toast.LENGTH_LONG).show();
        return data;
    } // end callWebService()

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
