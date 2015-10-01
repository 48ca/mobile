package me.jhoughton.day3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.textView1);
        textView.setOnClickListener(new View.OnClickListener() {
            private int clicks = 0;
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "TV1: " + ++clicks, Toast.LENGTH_SHORT).show();
                Log.i("ToastLog", "Red: " + clicks);
            }
        });
        textView = (TextView) findViewById(R.id.textView2);
        textView.setOnClickListener(new View.OnClickListener() {
            private int clicks = 0;
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "TV2: " + ++clicks, Toast.LENGTH_SHORT).show();
                Log.i("ToastLog", "Blue: " + clicks);
            }
        });
        textView = (TextView) findViewById(R.id.textView3);
        textView.setOnClickListener(new View.OnClickListener() {
            private int clicks = 0;
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "TV3: " + ++clicks, Toast.LENGTH_SHORT).show();
                Log.i("ToastLog", "Light Blue: " + clicks);
            }
        });
        textView = (TextView) findViewById(R.id.textView4);
        textView.setOnClickListener(new View.OnClickListener() {
            private int clicks = 0;
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "TV4: " + ++clicks, Toast.LENGTH_SHORT).show();
                Log.i("ToastLog", "Yellow: " + clicks);
            }
        });
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
