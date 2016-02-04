package me.jhoughton.multidrop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
        route.context = getApplicationContext();
        // route.addLatLng(loc[0],loc[1]);
        final TextView directionsText = (TextView) findViewById(R.id.directionsText);
        final DirectionsAdapter adapter = new DirectionsAdapter(this, route.list);
        final Button add = (Button) findViewById(R.id.addButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.add(directionsText.getText().toString());
                directionsText.setText("");
            }
        });
        final Button order = (Button) findViewById(R.id.order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Ordering...",Toast.LENGTH_SHORT).show();
                route.list = adapter.destinations;
                if(route.bestPath(loc[0] + "," + loc[1])) {
                    adapter.destinations = new ArrayList<>();
                    for(String n : route.namesOrdered) {
                        adapter.add(n);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        final Button done = (Button) findViewById(R.id.doneButton);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                i.putExtra("locations",route.ordered);
                i.putExtra("polyline", route.polyline);
                i.putExtra("js",false);
                startActivity(i);
            }
        });
        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_listview,route.list);
        final ListView listView = (ListView) findViewById(R.id.directionsList);
        listView.setAdapter(adapter);
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
