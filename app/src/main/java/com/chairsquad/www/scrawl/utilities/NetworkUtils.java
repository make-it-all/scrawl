package com.chairsquad.www.scrawl.utilities;

import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by henry on 13/04/17.
 */

public class NetworkUtils {

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */

    public static JSONObject request(String method, URL url, RequestParams params) throws IOException {
        String response = null;

        // Append params to end of url for get request
        if (method=="GET" && params!=null) url = new URL(url.toString() + "?" + params.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod(method);
        conn.setDoInput(true);

        if (method!="GET" && params !=null) {
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(params.toString());
            writer.flush();
            writer.close();
            os.close();
        }

        try {
            InputStream in = conn.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                response =  scanner.next();
            }
        } finally {
            conn.disconnect();
        }

        if (response != null) {
            try {
                return new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static JSONObject get(URL url, RequestParams params) throws IOException {
        return request("GET", url, params);
    }

    public static JSONObject get(String url, RequestParams params) throws IOException {
        return request("GET", new URL(url), params);
    }

    public static JSONObject post(URL url, RequestParams params) throws IOException {
        return request("POST", url, params);
    }

    public static JSONObject post(String url, RequestParams params) throws IOException {
     return request("POST", new URL(url), params);
    }

    public static JSONObject put(URL url, RequestParams params) throws IOException {
        return request("PUT", url, params);
    }

    public static JSONObject put(String url, RequestParams params) throws IOException {
     return request("PUT", new URL(url), params);
    }
    public static JSONObject delete(URL url, RequestParams params) throws IOException {
        return request("DELETE", url, params);
    }

    public static JSONObject delete(String url, RequestParams params) throws IOException {
     return request("DELETE", new URL(url), params);
    }


}
