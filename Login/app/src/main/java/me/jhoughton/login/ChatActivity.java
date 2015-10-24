package me.jhoughton.login;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.io.IOException;

public class ChatActivity extends AppCompatActivity {

    XMPPTCPConnection mConnection;
    MultiUserChatManager manager;
    MultiUserChat muc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        String room = getIntent().getStringExtra("itemValue");
        ActionBar ab = getActionBar();
        try {
            assert ab != null;
            ab.setTitle(room);
        } catch(Exception e) {
            Log.v("ERROR:", e.getMessage());
        }

        Toast.makeText(getApplicationContext(),room,Toast.LENGTH_LONG).show();

        mConnection = MainActivity.parentActivity.getXMPPConnection();
        String nick = MainActivity.parentActivity.getNick();

        manager = MultiUserChatManager.getInstanceFor(mConnection);
        muc = manager.getMultiUserChat(room + "@conference.jhoughton.me");
        try {
            muc.join(nick);
        } catch (XMPPException.XMPPErrorException | SmackException e) {
            e.printStackTrace();
        }

        Button send = (Button) findViewById(R.id.btSend);
        final EditText et = (EditText)(findViewById(R.id.etMessage));
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = et.getText().toString();
                try {
                    muc.sendMessage(message);
                    sendChatMessage(message);
                    et.setText("");
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed to send message!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final ListView listView = (ListView) findViewById(R.id.listview);
        ChatAdapter ca = new ChatAdapter();
        listView.setAdapter(ca);
    }

    private boolean sendChatMessage(String message) {

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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

    @Override
    public void onBackPressed() {
        try {
            muc.leave();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        this.finish();
    }
}