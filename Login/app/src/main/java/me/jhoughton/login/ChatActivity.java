package me.jhoughton.login;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        String title = getIntent().getStringExtra("itemValue");
        ActionBar ab = getActionBar();
        try {
            assert ab != null;
            ab.setTitle(title);
        } catch(Exception e) {
            Log.v("ERROR:",e.getMessage());
        }

        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
        //config.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        final String username = getIntent().getStringExtra("username");
        final String password = getIntent().getStringExtra("password");
        final String room = getIntent().getStringExtra("room");
        config.setUsernameAndPassword(username, password);
        config.setServiceName("jhoughton.me");
        config.setHost("jhoughton.me");
        config.setPort(5222);
        config.setDebuggerEnabled(true);


        XMPPTCPConnection mConnection = new XMPPTCPConnection(config.build());
        mConnection.setPacketReplyTimeout(10000);

        try {
            mConnection.connect();
            mConnection.login();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }

        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(mConnection);
        MultiUserChat muc = manager.getMultiUserChat(room + "@conference.jhoughton.me");
        try {
            muc.join(username);
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }

        Button send = (Button) findViewById(R.id.send);
        EditText et = (EditText)(findViewById(R.id.message));
        final String message = et.getText().toString();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView)findViewById(R.id.textView);
                tv.setText(tv.getText()+"\n"+message);
            }
        });
        et.setText("");
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
}
