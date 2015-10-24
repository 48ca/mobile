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

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    XMPPTCPConnection mConnection;
    String nick;

    public synchronized XMPPTCPConnection getXMPPConnection() {
        return mConnection;
    }

    public String getNick() {
        return nick;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        MainActivity.parentActivity = this;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final Intent switchView;
        switchView = new Intent(this, MainActivity.class);
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


        final XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();

        config.setServiceName("jhoughton.me");
        config.setHost("jhoughton.me");
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setDebuggerEnabled(true);

        login.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     //config.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
                     EditText utv = (EditText) findViewById(R.id.uname);
                     EditText ptv = (EditText) findViewById(R.id.passwd);
                     String uget = utv.getText().toString();
                     final String password = ptv.getText().toString();

                     if(uget.equals("")) uget = "anonymous";
                     final String username = uget;

                     mConnection = new XMPPTCPConnection(config.build());

                     mConnection.addConnectionListener(new ConnectionListener() {
                         @Override
                         public void connected(XMPPConnection connection) {
                             // Toast.makeText(getApplicationContext(), "Connecting as " + username, Toast.LENGTH_SHORT).show();
                         }

                         @Override
                         public void authenticated(XMPPConnection connection, boolean resumed) {
                             // Toast.makeText(getApplicationContext(), "Authenticated.", Toast.LENGTH_SHORT).show();
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

                     mConnection.setPacketReplyTimeout(1000);

                     try {
                         mConnection.connect();
                         mConnection.login(username, password);

                         nick = username;

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

                mConnection = new XMPPTCPConnection(config.build());
                mConnection.setPacketReplyTimeout(1000);
                try {
                    mConnection.connect();
                } catch (SmackException | IOException | XMPPException e) {
                    e.printStackTrace();
                }

                AccountManager am = AccountManager.getInstance(mConnection);
                try {
                    am.createAccount(username,password);
                    Toast.makeText(getApplicationContext(),"Created account successfully!",Toast.LENGTH_SHORT).show();
                } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
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