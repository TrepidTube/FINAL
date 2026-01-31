package com.example.misLugares.casos_uso;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RutasHelper {
    private static final String DIRECTIONS_API_KEY = "AIzaSyBD5Lj82jqqYX0bSuhXPTLUs_fS7PY2ylQ";
    
    public static void trazarRuta(Context context, LatLng origen, LatLng destino, GoogleMap mapa) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origen.latitude + "," + origen.longitude +
                "&destination=" + destino.latitude + "," + destino.longitude +
                "&key=" + DIRECTIONS_API_KEY;
        
        new ObtenerRutaTask(mapa).execute(url);
    }
    
    static class ObtenerRutaTask extends AsyncTask<String, Void, List<LatLng>> {
        private GoogleMap mapa;
        
        ObtenerRutaTask(GoogleMap mapa) {
            this.mapa = mapa;
        }
        
        @Override
        protected List<LatLng> doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) json.append(line);
                
                JSONObject jsonObj = new JSONObject(json.toString());
                JSONArray routes = jsonObj.getJSONArray("routes");
                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONObject poly = route.getJSONObject("overview_polyline");
                    String polyline = poly.getString("points");
                    return decodePoly(polyline);
                }
            } catch (Exception e) {
                Log.e("RutasHelper", "Error: " + e.getMessage());
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(List<LatLng> puntos) {
            if (puntos != null && mapa != null) {
                PolylineOptions lineOptions = new PolylineOptions()
                        .addAll(puntos)
                        .width(10)
                        .color(Color.BLUE);
                mapa.addPolyline(lineOptions);
            }
        }
        
        private List<LatLng> decodePoly(String encoded) {
            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;
            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;
                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;
                poly.add(new LatLng(lat / 1E5, lng / 1E5));
            }
            return poly;
        }
    }
}
