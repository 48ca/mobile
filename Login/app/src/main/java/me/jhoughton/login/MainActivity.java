package me.jhoughton.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
        //config.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        final String username = getIntent().getStringExtra("username");
        final String password = getIntent().getStringExtra("password");
        config.setUsernameAndPassword("test", "password");
        config.setServiceName("jhoughton.me");
        config.setHost("jhoughton.me");
        config.setPort(5222);
        config.setDebuggerEnabled(true);


        XMPPTCPConnection mConnection = new XMPPTCPConnection(config.build());
        mConnection.setPacketReplyTimeout(10000);

        try {
            mConnection.connect();
            mConnection.login();


            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(mConnection);
            List<HostedRoom> rooms = null;
            try {
                rooms = manager.getHostedRooms("conference.jhoughton.me");
                Log.v("rooms",rooms.toString());
            } catch (SmackException.NoResponseException e) {
                Toast.makeText(getApplicationContext(), "Failed to list rooms",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


            final ListView listView = (ListView) findViewById(R.id.listview);
            assert rooms != null;
            String[] values;
            Object[] roomArray = rooms.toArray();
            int length = roomArray.length;
            values = new String[length];
            int cnt=0;
            for( Object o : roomArray) {
                values[cnt++] = o.toString();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.activity_listview, values);
            listView.setAdapter(adapter);

            final Intent i = new Intent(this, ChatActivity.class);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String itemValue = (String)listView.getItemAtPosition(position);
                    i.putExtra("itemValue",itemValue);
                    i.putExtra("username",username);
                    i.putExtra("password",password);
                    startActivity(i);
                }
            });
        } catch (XMPPException | IOException | SmackException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Failed to connect", Toast.LENGTH_LONG).show();
        }
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
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
