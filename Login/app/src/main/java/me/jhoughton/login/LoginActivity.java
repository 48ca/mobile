package me.jhoughton.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        final Firebase fb = new Firebase("https://day-5.firebaseio.com");
        setContentView(R.layout.login_activity);
        final Intent switchView = new Intent(this, MainActivity.class);
        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText utv = (EditText) findViewById(R.id.uname);
                EditText ptv = (EditText) findViewById(R.id.passwd);
                String username = utv.getText().toString();
                String password = ptv.getText().toString();
                fb.authWithPassword(username, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        startActivity(switchView);
                        Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onAuthenticationError(FirebaseError fbe) {
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        Button na = (Button) findViewById(R.id.na);
        na.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText utv = (EditText) findViewById(R.id.uname);
                EditText ptv = (EditText) findViewById(R.id.passwd);
                String username = utv.getText().toString();
                String password = ptv.getText().toString();
                fb.createUser(username, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        Toast.makeText(LoginActivity.this, "Account created", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FirebaseError fbe) {
                        Toast.makeText(LoginActivity.this, "Account creation failed", Toast.LENGTH_LONG).show();
                    }
                });

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
        if (id == R.id.logout) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
