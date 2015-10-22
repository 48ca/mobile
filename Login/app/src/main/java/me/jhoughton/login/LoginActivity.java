package me.jhoughton.login;

import android.content.Intent;
import android.os.StrictMode;
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

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.android.AndroidSmackInitializer;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.dns.HostAddress;

import java.io.IOException;
import java.util.Map;

import javax.net.ssl.SSLContext;

import static com.firebase.client.Firebase.setAndroidContext;

public class LoginActivity extends AppCompatActivity {

    XMPPTCPConnection mConnection;

    public synchronized XMPPTCPConnection getXMPPConnection() {
        return mConnection;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAndroidContext(this);
        final Firebase fb = new Firebase("https://day-5.firebaseio.com");
        setContentView(R.layout.login_activity);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final Intent switchView;
        switchView = new Intent(this, MainActivity.class);
        MainActivity.parentActivity = this;
        Button login = (Button) findViewById(R.id.login);
        /*
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText utv = (EditText) findViewById(R.id.uname);
                EditText ptv = (EditText) findViewById(R.id.passwd);
                final String username = utv.getText().toString();
                final String password = ptv.getText().toString();
                fb.authWithPassword(username, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {

                        switchView.putExtra("username",username);
                        switchView.putExtra("password",password);

                        startActivity(switchView);
                    }
                    @Override
                    public void onAuthenticationError(FirebaseError fbe) {
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        */
        login.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
                     //config.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
                     // final String username = getIntent().getStringExtra("username");
                     // final String password = getIntent().getStringExtra("password");
                     config.setUsernameAndPassword("test", "password");
                     config.setServiceName("jhoughton.me");
                     config.setHost("jhoughton.me");
                     config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                     config.setDebuggerEnabled(true);

                     mConnection = new XMPPTCPConnection(config.build());

                     mConnection.addConnectionListener(new ConnectionListener() {
                         @Override
                         public void connected(XMPPConnection connection) {
                             Toast.makeText(getApplicationContext(), "Connected.", Toast.LENGTH_SHORT).show();
                         }

                         @Override
                         public void authenticated(XMPPConnection connection, boolean resumed) {
                             Toast.makeText(getApplicationContext(), "Authenticated.", Toast.LENGTH_SHORT).show();
                         }

                         @Override
                         public void connectionClosed() {
                             Toast.makeText(getApplicationContext(), "Connection closed.", Toast.LENGTH_SHORT).show();
                         }

                         @Override
                         public void connectionClosedOnError(Exception e) {
                             Toast.makeText(getApplicationContext(), "Connection closed.", Toast.LENGTH_SHORT).show();
                         }

                         @Override
                         public void reconnectionSuccessful() {

                         }

                         @Override
                         public void reconnectingIn(int seconds) {

                         }

                         @Override
                         public void reconnectionFailed(Exception e) {

                         }
                     });

                     mConnection.setPacketReplyTimeout(10000);

                     try {
                         mConnection.connect();
                         mConnection.login("test", "password");

                         startActivity(switchView);

                     } catch (XMPPException | IOException | SmackException e) {
                         e.printStackTrace();
                         Toast.makeText(getApplicationContext(), "Failed to connect", Toast.LENGTH_LONG).show();
                     }
                 }
             }
        );
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
