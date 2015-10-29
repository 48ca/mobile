package me.jhoughton.login;

import android.app.ActionBar;
import android.app.Activity;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private MultiUserChatManager manager;
    private MultiUserChat muc;
    private ChatAdapter ca;

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
        muc = manager.getMultiUserChat(room);
        DiscussionHistory history = new DiscussionHistory();
        Date date = new Date();
        date.setTime(date.getTime() - 1000 * 60  * 30);
        history.setSince(date);
        try {
            muc.join(nick,null,history, SmackConfiguration.getDefaultPacketReplyTimeout());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }

        List<ChatMessage> past = new ArrayList<ChatMessage>();
        ca = new ChatAdapter(this, past);

        muc.addMessageListener(new MessageListener() {
            @Override
            public void processMessage(Message message) {
                ca.add(new ChatMessage(message.getFrom().split("/")[1],message.getBody(),nick));
            }
        });

        Button send = (Button) findViewById(R.id.chatSendButton);
        final EditText et = (EditText)(findViewById(R.id.messageEdit));
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = et.getText().toString();
                // Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                try {
                    muc.sendMessage(message);
                    et.setText("");
                    // ca.add(new ChatMessage(nick, message));
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        });
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
        /*
        try {
            muc.leave();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        */
        this.finish();
    }
}
