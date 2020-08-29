package com.markstart.rainforest.client;

import android.util.Log;

import com.markstart.rainforest.model.Point;
import com.markstart.rainforest.model.Track;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class TrackClient {


    private static String url = "http://markstart.duckdns.org:8000/tracks";

    public static void sendJson(final ArrayList<Track> tracks) {

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject json = new JSONObject();
        StringEntity se = null;

        try {

            for (Track track: tracks ) {
                ArrayList<Point> points = (ArrayList<Point>) track.getTrack_points();
                for ( Point point: points ) {
                    Log.d(String.valueOf(point.getPoint_id()), String.valueOf(point.getJSONObject()));

                    try {

                        json.put("point_id", point.getPoint_id());
                        json.put("track_id", point.getTrack_id());
                        json.put("gps_latitude", point.getGps_latitude());
                        json.put("gps_longitude", point.getGps_longitude());
                        json.put("point_timestamp", point.getJsonTimestamp());
                        json.put("point_humidity", point.getPoint_humidity());
                        json.put("point_temperature", point.getPoint_temperature());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            se = new StringEntity(json.toString());



        } catch (UnsupportedEncodingException e) {
            // handle exceptions properly!
            e.printStackTrace();
        }

        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        client.put(null, url, se, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

}









