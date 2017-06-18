/*
package com.example.android.harvesthand;

*/
/**
 * Created by franz on 15.06.2017.
 *//*


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Location {
        public static void main(String[] args) {
            try {
                URL url = new URL("http://api.wunderground.com/api/28182cd556dbb993/geolookup/q/autoip.json");
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                String strTemp = "";
                while (null != (strTemp = br.readLine())) {
                    System.out.println(strTemp.toString());
                }
        } catch (Exception ex) {
            ex.printStackTrace();
        }}
    private void sendData(String ip) {
        try {
            JSONObject json = new JSONObject();

            URL url = new URL("http://localhost:3001/wetter");
            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            httpcon.setDoOutput(true);
            httpcon.setRequestMethod("POST");
            httpcon.setRequestProperty("Accept", "application/json");
            httpcon.setRequestProperty("Content-Type", "application/json");
            httpcon.setRequestProperty("Accept", "application/json");

            OutputStreamWriter output = new OutputStreamWriter(httpcon.getOutputStream());
            System.out.println(json);
            output.write(json.toString());
            httpcon.connect();

            String output1 = httpcon.getResponseMessage();
            System.out.println(output1);

        } catch (Exception e) {

        }

    }}

*/
