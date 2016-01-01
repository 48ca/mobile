package me.jhoughton.login;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

/**
 * Created by james on 12/24/2015.
 */
public class JabberReceiveService extends IntentService {

    static XMPPTCPConnection mConnection;

    public static synchronized XMPPTCPConnection getXMPPConnection() {
        return mConnection;
    }
    public static void setXMPPConnection(XMPPTCPConnection xmppc) {
        mConnection = xmppc;
    }

    public JabberReceiveService() {
        super("Jabber");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String user = intent.getStringExtra("user");
        String message = intent.getStringExtra("message");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(user).setContentText(message).setVibrate(new long[]{1});
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(0,mBuilder.build());
    }
}
