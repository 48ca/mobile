package me.jhoughton.login;

import android.app.Activity;
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
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static LoginActivity parentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Chatrooms");

        XMPPTCPConnection mConnection = JabberReceiveService.getXMPPConnection();

        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(mConnection);
        List<HostedRoom> rooms = null;
        try {
            rooms = manager.getHostedRooms("conference.jhoughton.me");
            Log.v("rooms", rooms.toString());
        } catch (SmackException.NoResponseException e) {
            Toast.makeText(getApplicationContext(), "Failed to list rooms", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }


        final ListView listView = (ListView) findViewById(R.id.listview);
        assert rooms != null;
        String[] values;
        int length = rooms.size();
        values = new String[length];
        final HashMap<String, String> jn = new HashMap<>();
        int cnt = 0;
        for (HostedRoom o : rooms) {
            String name = o.getName();
            values[cnt++] = name;
            jn.put(name,o.getJid());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.activity_listview, values);
        listView.setAdapter(adapter);

        final Intent i = new Intent(this, ChatActivity.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) listView.getItemAtPosition(position);
                i.putExtra("itemValue", jn.get(itemValue));
                startActivity(i);
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
            JabberReceiveService.getXMPPConnection().disconnect();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        JabberReceiveService.getXMPPConnection().disconnect();
        this.finish();
    }
}
