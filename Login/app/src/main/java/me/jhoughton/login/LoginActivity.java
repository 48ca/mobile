package me.jhoughton.login;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
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

    String nick;

    public String getNick() {
        return nick;
    }
    public static JabberReceiveService jrs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        MainActivity.parentActivity = this;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /*
        if(JabberReceiveService.instance == null) {
            Toast.makeText(getApplicationContext(),"JRS NULL!",Toast.LENGTH_SHORT).show();
            new JabberBroadcastReceiver().onStartCommand();
        }
        */

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

        startService(new Intent(getApplicationContext(), JabberReceiveService.class));

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
                     if(JabberReceiveService.instance == null) {
                         Toast.makeText(getApplicationContext(),"Jabber service is still starting...",Toast.LENGTH_SHORT).show();
                     } else {
                         JabberReceiveService.instance.start(config, username, password);
                         if (JabberReceiveService.connected) {
                             startActivity(switchView);
                         }
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

                JabberReceiveService.build(config.build());
                XMPPTCPConnection mConnection = JabberReceiveService.getXMPPConnection();
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
