/*package com.example.securitymap;

import android.util.Log;
import org.json.*; //SHOULD RLY LOOK INTO ONLY IMPORTING WHAT WE ACTUALLY NEED

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GettingDirections {

    //instantiating variables needed for the Directions API request
    public String originLong,
            originLat,
            destLong,
            destLat;

    //methods to set the value to these vbles that will then be concatenated to the URL
    public void setOriginLong(String originLong) {
        this.originLong = originLong;
    }
    public void setOriginLat(String originLat) {
        this.originLat = originLat;
    }
    public void setDestLat(String destLat) {
        this.destLat = destLat;
    }
    public void setDestLong(String destLong) {
        this.destLong = destLong;
    }

    public String makeURL(){

        String urlStr = "http://maps.googleapis.com/maps/api/directions/json?";

        urlStr = urlStr+"origin="+originLat+","+originLong //set the origin point with coords
                +"&destination="+destLat+","+destLong //set the destination point with coords
                +"&key=" //specify the API key for our app (NOTE: normally this would NEVER be in the app, but rather on a server, in order to protect the key)
                +"&mode=walking"; //specify the transportation mode, in our case it will always be walking

        Log.d("Directions", "full url is "+urlStr);
        return url;
    }

    public void getJSON(String urlStr){

        // this next piece of code is NOT SUPPOSED TO BE THERE. Gotta put in in the non=depricated version of the Asynctask functions
        URL url = null;
        try {
            url = new URL(urlStr);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(); //establishes the bridge to the data source

            InputStream inputStream = httpURLConnection.getInputStream(); //lets us have the data flow in so that we can read it

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream)); //gonna setup a way to read the file




        } catch (MalformedURLException e) {
            e.printStackTrace(); //not sure what these do, they are basically mandatory when attempting to get stuff from the web
        } catch (IOException e) {
            e.printStackTrace();
        }


        result = json.load(urllib.request.urlopen(urlStr));


    }


}



//These are the necessary bits of information in the URL for the Directions API request
//   http://maps.googleapis.com/maps/api/directions/json?
//   origin=originLatValue,originLongValue
//   &destination=destLatValue,destLongValue
//   &key=apiKeyValue
//   &mode=walking

*/