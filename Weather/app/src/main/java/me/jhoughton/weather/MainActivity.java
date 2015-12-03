package me.jhoughton.weather;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity {
    private GoogleApiClient mGoogleApiClient;
    private double lat;
    private double lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // onConnected(null);
        // getCurrentLocation();

        IntentFilter mStatusIntentFilter = new IntentFilter(
                "me.jhoughton.BROADCAST");

        WeatherBroadcastReceiver recv = new WeatherBroadcastReceiver(getApplicationContext(),this);
        LocalBroadcastManager.getInstance(this).registerReceiver(recv,mStatusIntentFilter);

        Intent serviceIntent = new Intent(this,WeatherService.class);
        startService(serviceIntent);

    }

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
class WeatherBroadcastReceiver extends BroadcastReceiver {
    private Context context;
    private Activity act;
    public WeatherBroadcastReceiver(Context c, Activity a) {
        context = c;
        act = a;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.cloud);

        double temp = intent.getDoubleExtra("temp",0);
        String weather = intent.getStringExtra("weather");

        Intent mintent = new Intent( context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, mintent, 0);
        builder.setContentIntent(pIntent);
        NotificationManager mNotificationManager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notif = builder
                .setContentText((int) (temp) + " degrees Fahrenheit | " + weather)
                .setContentTitle("Weather")
                .build();
        mNotificationManager.notify(0, notif);
    }
}
