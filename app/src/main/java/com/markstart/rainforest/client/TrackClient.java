package com.markstart.rainforest.client;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.markstart.rainforest.MainActivity;
import com.markstart.rainforest.model.Point;

import java.io.UnsupportedEncodingException;

import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class TrackClient {

    private static String url = "http://markstart.duckdns.org:8000/tracks";


    public static void sendJsonPoint( final Context context, final Point point) {

        Log.d("begining", "sending");

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject json = new JSONObject();
        StringEntity se = null;

        try {
            json.put("point_id", point.getPoint_id());
            json.put("track_id", point.getTrack_id());
            json.put("gps_latitude", point.getGps_latitude());
            json.put("gps_longitude", point.getGps_longitude());
            json.put("point_timestamp", point.getJsonTimestamp());
            json.put("point_humidity", point.getPoint_humidity());
            json.put("point_temperature", point.getPoint_temperature());

            se = new StringEntity(json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));


        client.put(null, url, se, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                Toast.makeText(context, " Files Sent ", Toast.LENGTH_LONG).show();
                MainActivity.messageHandler(true);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Toast.makeText(context, " Could not send files ! ", Toast.LENGTH_LONG).show();
                MainActivity.messageHandler(false);
            }
        });



    }

}









