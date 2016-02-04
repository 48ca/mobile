package me.jhoughton.multidrop;

import android.content.Context;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.ArrayList;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by 2017jhoughto on 1/11/2016.
 */
public class Route {

    ArrayList<String> list;
    public Context context;
    public String polyline;
    public ArrayList<String> namesOrdered;
    public ArrayList<String> ordered;

    public Route() {
        list = new ArrayList<>();
    }

    void addLatLng(double lat, double lon) {
        list.add(lat + "," + lon);
    }
    void addString(String str) {
        list.add(str);
    }

    boolean bestPath(String orig) {
        if(list == null) {
        } else {
            String routeString = "";
            try {
                for (String d : list) {
                    routeString += "|" + URLEncoder.encode(d, "utf-8");
                }
            }
            catch(Exception e) {
                return false;
            }
            /*
            String urlString = Uri.parse("https:/maps.googleapis.com/maps/api/directions/json")
                    .buildUpon()
                    .appendQueryParameter("waypoints","optimize:true" + routeString)
                    .appendQueryParameter("key","AIzaSyBXFjiBzV08gMKJ7ZfNcF5es-CgoAu7slQ")
                    .appendQueryParameter("origin",orig)
                    .appendQueryParameter("destination",orig)
                    .build().toString();
            */
            String urlString = "https://maps.googleapis.com/maps/api/directions/json?waypoints=optimize:true" + routeString + "&key=AIzaSyBXFjiBzV08gMKJ7ZfNcF5es-CgoAu7slQ&origin=" + orig + "&destination=" + orig;
            HttpURLConnection urlConnection = null;
            URL url = null;
            JSONObject object = null;
            InputStream inStream = null;
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                InetAddress i = InetAddress.getByName("maps.googleapis.com");
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
                return false;
            }
            try {
                url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();
                inStream = urlConnection.getInputStream();
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
                String temp, response = "";
                while ((temp = bReader.readLine()) != null) {
                    response += temp;
                }
                object = (JSONObject) new JSONTokener(response).nextValue();
            } catch (Exception e) {
                Log.d("UGH", e.toString());
                Log.d("UGH",e.getLocalizedMessage());
                Log.d("UGH",e.getMessage());
                Toast.makeText(context.getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            } finally {
                if (inStream != null) {
                    try {
                        // this will close the bReader as well
                        inStream.close();
                    } catch (IOException ignored) {
                    }
                }
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            try {
                JSONArray waypointOrder = object.getJSONArray("routes").getJSONObject(0).getJSONArray("waypoint_order");
                JSONArray legs = object.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
                ArrayList<JSONObject> locations = new ArrayList<>();
                for(int i=0;i<legs.length();i++) {
                    locations.add(legs.getJSONObject(i).getJSONObject("start_location"));
                }
                Toast.makeText(context,waypointOrder.toString(),Toast.LENGTH_LONG).show();

                namesOrdered = new ArrayList<>();
                for(int i=0;i<list.size();i++) {
                    namesOrdered.add(list.get(waypointOrder.getInt(i)));
                }

                ordered = new ArrayList<>();
                for(JSONObject l : locations) {
                    ordered.add(l.getDouble("lat") + "," + l.getDouble("lng"));
                }
                polyline = object.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");
                return ordered != null;
            }
            catch(Exception e) {
                // Toast.makeText(context.getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                Toast.makeText(context, "Check destinations and internet connection!", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return false;
    }
}
