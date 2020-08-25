package com.markstart.rainforest.client;

import android.util.Log;

import com.markstart.rainforest.model.Point;
import com.markstart.rainforest.model.Track;

import java.util.ArrayList;
import java.util.List;

import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class TrackClient {


    private static String url = "http://markstart.duckdns.org:8000/tracks";


    public static void sendTrackingDataToServer(ArrayList<Track> tracks) throws JSONException {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        for (Track track: tracks ) {
            ArrayList<Point> points = (ArrayList<Point>) track.getPoints();
            for ( Point point: points ) {
                params.put(point.getTrack_id().toString(), point.getJSONObject());
            }
        }

        client.get(url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
               Log.d("connection ?"," no");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("connection ?"," yes");
            }
        });



        client.put(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("data sent to server :", " SUCCESS");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("data not sent :", " NO ");
            }
        });

    }

}








