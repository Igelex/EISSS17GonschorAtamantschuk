package com.example.android.harvesthand;

/**
 * Created by franz on 15.06.2017.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Location {
    public static void main(String[] args) {
    try {
        URL url = new URL("http://api.wunderground.com/api/28182cd556dbb993/geolookup/q/autoip.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String strTemp = "";
        while (null != (strTemp = br.readLine())) {
            System.out.printf(strTemp);
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}}

