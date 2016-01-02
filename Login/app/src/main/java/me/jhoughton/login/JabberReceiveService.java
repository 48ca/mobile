package me.jhoughton.login;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by james on 12/24/2015.
 */
public class JabberReceiveService extends Service {

    static XMPPTCPConnection mConnection;
    static String uname;
    private Activity retAct;
    ChatAdapter chatAdapter;
    MultiUserChat muc;
    static HashMap<ChatActivity,MultiUserChat> map;
    public static JabberReceiveService instance;
    public static Context context;
    public static boolean connected = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(),"SERVICE: "+instance==null?"NULL":"NOT NULL",Toast.LENGTH_SHORT).show();
        /*
        Intent sintent = new Intent(this, LoginActivity.class);
        sintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification noti = new Notification.Builder(getApplicationContext())
                .setContentTitle("Jabber")
                .setContentText("Service started")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1234,noti);
        // startService(sintent);
        if(LoginActivity.active)
            new LoginActivity().finishCreate();
        */
        LoginActivity.jrs = instance;
        /*
        if(LoginActivity.config != null) {
            JabberReceiveService.build(LoginActivity.config.build());
            connected = connect(LoginActivity.username, LoginActivity.password);
        }
        */
        return JabberReceiveService.START_STICKY;
    }
    public void start(XMPPTCPConnectionConfiguration.Builder config, String username, String password) {
        build(config.build());
        connected = connect(username,password);
    }
    public static synchronized XMPPTCPConnection getXMPPConnection() {
        return mConnection;
    }
    public static void setXMPPConnection(XMPPTCPConnection xmppc) {
        mConnection = xmppc;
    }
    public JabberReceiveService() {
        super();
        map = new HashMap<>();
    }
    public JabberReceiveService(Context context) {
        super();
        map = new HashMap<>();
        this.context = context;
    }
    public static void build(XMPPTCPConnectionConfiguration config) {
        mConnection = new XMPPTCPConnection(config);
    }

    public boolean connect(String username, String password) {
        mConnection.addConnectionListener(new ConnectionListener() {
            @Override
            public void connected(XMPPConnection connection) {
                // Toast.makeText(context, "Connecting as " + username, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {
                // Toast.makeText(context, "Authenticated.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void connectionClosed() {
                Toast.makeText(context, "Connection closed", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void connectionClosedOnError(Exception e) {
                Toast.makeText(context, "Connection closed", Toast.LENGTH_SHORT).show();
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
            uname = username;
            return true;
        } catch (XMPPException | IOException | SmackException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to connect", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if(instance != null)
            Toast.makeText(getApplicationContext(),"Finished init!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(context,"Jabber service destroyed!",Toast.LENGTH_SHORT).show();
        Toast.makeText(context,instance == null ? "Jabber invalidated!" : "Nothing happened",Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void notify(Intent intent) {
        String user = intent.getStringExtra("user");
        String message = intent.getStringExtra("message");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(retAct)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(user)
                .setContentText(message)
                .setContentIntent(PendingIntent.getActivity(context,0,new Intent(context,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT));
                // .setContentIntent(PendingIntent.getActivity(retAct,0,new Intent(retAct,ChatActivity.class),PendingIntent.FLAG_UPDATE_CURRENT));
        NotificationManager mNotifyMgr =
                (NotificationManager) retAct.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(0, mBuilder.build());
    }
    public MultiUserChat genMUC(String room, final String nick, final ChatActivity ca) {
        if(map.containsKey(ca)) {
            muc = map.get(ca);
        } else {
            retAct = ca;
            // Toast.makeText(ca.getApplicationContext(), "Set retAct",Toast.LENGTH_SHORT).show();
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(mConnection);
            muc = manager.getMultiUserChat(room);
            DiscussionHistory history = new DiscussionHistory();
            Date date = new Date();
            date.setTime(date.getTime() - 1000 * 60 * 30);
            history.setSince(date);
            try {
                muc.join(nick,null,history, SmackConfiguration.getDefaultPacketReplyTimeout());
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException e) {
                e.printStackTrace();
            }
            muc.addMessageListener(new MessageListener() {
                @Override
                public void processMessage(Message message) {
                    String uname = message.getFrom().split("/")[1];
                    chatAdapter.add(new ChatMessage(uname, message.getBody(), nick));
                    if (!ca.active) {
                        Intent mServiceIntent = new Intent(ca, JabberReceiveService.class);
                        mServiceIntent.putExtra("user", uname);
                        mServiceIntent.putExtra("message", message.getBody());
                        JabberReceiveService.instance.notify(mServiceIntent);
                    }
                }
            });
            map.put(ca,muc);
        }
        return muc;
    }
}
