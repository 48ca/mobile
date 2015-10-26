package me.jhoughton.login;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private MultiUserChatManager manager;
    private MultiUserChat muc;

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
            Log.v("ERROR:", e.getMessage());
        }

        String room = title;

        XMPPTCPConnection mConnection = MainActivity.parentActivity.getXMPPConnection();
        final String nick = MainActivity.parentActivity.getNick();

        manager = MultiUserChatManager.getInstanceFor(mConnection);
        muc = manager.getMultiUserChat(room + "@conference.jhoughton.me");
        try {
            muc.join(nick);
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }

        Button send = (Button) findViewById(R.id.chatSendButton);
        final EditText et = (EditText)(findViewById(R.id.messageEdit));
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = et.getText().toString();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                try {
                    muc.sendMessage(message);
                    et.setText("");
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        });

        List<ChatMessage> past = new ArrayList<ChatMessage>() {{
            add(new ChatMessage("james","wasd"));
        }};
        ChatAdapter ca = new ChatAdapter(this, past) {{
            add(new ChatMessage("james2","test2"));
        }};
        ListView list = (ListView) findViewById(R.id.messagesContainer);
        list.setAdapter(ca);

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
