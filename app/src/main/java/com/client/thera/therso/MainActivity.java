package com.client.thera.therso;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends Activity {

    private WebSocketClient mWebSocketClient;

    private UUID deviceID= UUID.fromString("f14a5fee-59e3-4673-9ed0-46c56bec1f2e");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if ("google_sdk".equals( Build.PRODUCT )) {
            // ... disable IPv6
            java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
            java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        }

        createWebSocket();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    private void createWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://10.0.2.2:9000/api/connectWS/"+deviceID);
            //uri = new URI("ws://echo.websocket.org");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = (TextView)findViewById(R.id.messages);
                        textView.setText(textView.getText() + "\n Received: " + message);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {

                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };

        mWebSocketClient.connect();
        int i =0;
    }

    public void connectWS(View view){


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = (TextView) findViewById(R.id.messages);
                textView.setText(textView.getText() + "\n" + new Date() + ": Connected");

                Button connectWSButton = (Button) findViewById(R.id.connectWS);
                Button disconnectWSButton = (Button) findViewById(R.id.disconnectWS);
                Button sendMessageButton = (Button) findViewById(R.id.sendMessageWS);

                connectWSButton.setVisibility(View.GONE);
                disconnectWSButton.setVisibility(View.VISIBLE);
                sendMessageButton.setVisibility(View.VISIBLE);
            }
        });
    }

    public void disconnectWS(View view){

        mWebSocketClient.close();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = (TextView) findViewById(R.id.messages);
                textView.setText(textView.getText() + "\n" + new Date() + ": Disconnected");

                Button connectWSButton = (Button) findViewById(R.id.connectWS);
                Button disconnectWSButton = (Button) findViewById(R.id.disconnectWS);
                Button sendMessageButton = (Button) findViewById(R.id.sendMessageWS);

                connectWSButton.setVisibility(View.VISIBLE);
                disconnectWSButton.setVisibility(View.GONE);
                sendMessageButton.setVisibility(View.GONE);
            }
        });
    }

    public void sendMessage(View view) {
        String hola = "Hola desde Android";
        mWebSocketClient.send(hola);
        TextView textView = (TextView) findViewById(R.id.messages);
        textView.setText(textView.getText() + "\n Sent: "+hola);
    }
}