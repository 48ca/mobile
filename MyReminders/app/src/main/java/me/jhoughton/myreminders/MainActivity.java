package me.jhoughton.myreminders;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Shader;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        final SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.editText);
                SharedPreferences.Editor editor = mSettings.edit();
                String update = mSettings.getString("tasks", "");
                update += et.getText().toString();
                editor.putString("tasks",update);
                editor.commit();
                updateTasks(mSettings);
            }
        });
        updateTasks(mSettings);
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
    public void updateTasks(SharedPreferences mSettings) {
        String update = mSettings.getString("tasks","No tasks");
        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText(update);
    }
}
