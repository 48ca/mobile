package me.jhoughton.multidrop;

import android.net.Uri;
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
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by 2017jhoughto on 1/11/2016.
 */
public class Route {

    ArrayList<String> list;

    public Route() {
        list = new ArrayList<>();
    }

    void addLatLng(double lat, double lon) {
        list.add(lat + "," + lon);
    }
    void addString(String str) {
        list.add(str);
    }

    ArrayList<String> bestPath(String orig) {
        if(list == null) {
        } else {
            String routeString = "";
            for(String d : list) {
                routeString+="|"+d.toString();
            }
            String urlString = Uri.parse("https:/maps.googleapis.com/maps/api/directions/json")
                    .buildUpon()
                    .appendQueryParameter("waypoints","optimize:true" + routeString)
                    .appendQueryParameter("key","AIzaSyBXFjiBzV08gMKJ7ZfNcF5es-CgoAu7slQ")
                    .appendQueryParameter("origin",orig)
                    .appendQueryParameter("destination",orig)
                    .build().toString();
            HttpURLConnection urlConnection = null;
            URL url = null;
            JSONObject object = null;
            InputStream inStream = null;
            try {
                url = new URL(urlString.toString());
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
                ArrayList<String> ordered = new ArrayList<>();
                for(int i=0;i<list.size();i++) {
                    ordered.add(list.get(waypointOrder.getInt(i)));
                }
                return ordered;
            }
            catch(Exception e) {}
            return null;
        }
        return null;
    }
}
