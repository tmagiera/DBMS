package com.example.tmagiera.dbms;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tmagiera on 2015-03-26.
 */
public class NetworkLibrary {
    public String download(String location) {
        try {
            Log.d(this.getClass().getSimpleName(), "Requesting: " + location);

            URL url = new URL(location);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(30000 /* milliseconds */);
            conn.setConnectTimeout(60000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int response = conn.getResponseCode();
            Log.d(this.getClass().getSimpleName(), "The response code is: " + response);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String read;

            try {
                while ((read = br.readLine()) != null) {
                    sb.append(read);
                }

                Log.d(this.getClass().getSimpleName(), "Type: text Bytes: " + sb.toString().length() + " Data: " + sb.toString());
            } catch (Exception e) {
                Log.d(this.getClass().getSimpleName(), "Error");
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
