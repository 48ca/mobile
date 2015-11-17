package me.jhoughton.spotify;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "c58fdd9c2e2e4bcdaa8111c74f6864e9";
    private static final String REDIRECT_URI = "spotify://jhoughton.me";

    private static final int REQUEST_CODE = 5678;
    private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b = (Button) findViewById(R.id.button);
        final Activity activity = this;
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                        AuthenticationResponse.Type.TOKEN,
                        REDIRECT_URI);
                builder.setScopes(new String[]{"user-read-private"});
                AuthenticationRequest request = builder.build();

                AuthenticationClient.openLoginActivity(activity, REQUEST_CODE, request);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer.addConnectionStateCallback(new ConnectionStateCallback() {
                            @Override
                            public void onLoggedIn() {
                                Toast.makeText(getApplicationContext(), "Logged in!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onLoggedOut() {
                                Toast.makeText(getApplicationContext(), "Logged out!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onLoginFailed(Throwable throwable) {
                                Toast.makeText(getApplicationContext(), "Logged failed!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onTemporaryError() {
                                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onConnectionMessage(String s) {
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                            }
                        });
                        mPlayer.addPlayerNotificationCallback(new PlayerNotificationCallback() {
                            @Override
                            public void onPlaybackEvent(EventType eventType, PlayerState playerState) {

                            }

                            @Override
                            public void onPlaybackError(ErrorType errorType, String s) {

                            }
                        });
                        mPlayer.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }
}
