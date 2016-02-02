package me.jhoughton.multidrop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DirectionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);
        final double[] loc = getIntent().getDoubleArrayExtra("location");
        final Route route = new Route();
        // route.addLatLng(loc[0],loc[1]);
        final TextView directionsText = (TextView) findViewById(R.id.directionsText);
        final Button add = (Button) findViewById(R.id.addButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                route.addString(directionsText.getText().toString());
                directionsText.setText("");
            }
        });
        final Button done = (Button) findViewById(R.id.doneButton);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),route.toString(),Toast.LENGTH_LONG).show();
                ArrayList<String> ordered = route.bestPath(loc[0] + "," + loc[1]);
                Toast.makeText(getApplicationContext(),ordered.toString(),Toast.LENGTH_LONG).show();

                Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_directions, menu);
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
