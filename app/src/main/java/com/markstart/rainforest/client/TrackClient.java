package com.markstart.rainforest.client;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.markstart.rainforest.MainActivity;
import com.markstart.rainforest.dataStorage.DataStorageEngine;
import com.markstart.rainforest.model.Point;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.loopj.android.http.*;
import com.markstart.rainforest.model.Track;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class TrackClient {

    private static String url = "http://markstart.duckdns.org:8000/tracks";
    private static Context contextClient;
    private DataStorageEngine dseClient;


    public TrackClient ( Context context, DataStorageEngine dse ) {

        contextClient = context;
        dseClient = dse;

    }


    public void sendAllFiles() {

        MainActivity.messageHandler("Connecting ...");
        Toast.makeText(contextClient, " Connecting to server ... ... ", Toast.LENGTH_LONG).show();

        ArrayList<Track> tracks = dseClient.getAllTracksFromDisk();

        for (Track track : tracks) {
            for (Point point : track.getTrackPoints()) {
                sendJsonPoint(point);
            }
        }

    }


    private static void sendJsonPoint( Point point) {


        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject json = new JSONObject();
        StringEntity se = null;

        try {
            json.put("point_id", point.getPoint_id());
            json.put("track_id", point.getTrack_id());
            json.put("gps_latitude", point.getGps_latitude());
            json.put("gps_longitude", point.getGps_longitude());
            json.put("point_timestamp", point.createJsonTimestamp());
            json.put("point_humidity", point.getPoint_humidity());
            json.put("point_temperature", point.getPoint_temperature());

            se = new StringEntity(json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (NullPointerException e ) {
            Log.d("TrackClient", "se.null pointer Exception" );
        }

        client.put(null, url, se, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                Toast.makeText(contextClient, " Files Sent ", Toast.LENGTH_LONG).show();
                MainActivity.messageHandler( " Files Sent. ");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Toast.makeText(contextClient, " Could not send files ! ", Toast.LENGTH_LONG).show();
                MainActivity.messageHandler( " Files not Sent !" );
            }
        });


    }





}


