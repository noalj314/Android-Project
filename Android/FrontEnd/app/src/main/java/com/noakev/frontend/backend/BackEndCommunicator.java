package com.noakev.frontend.backend;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

public class BackEndCommunicator {
    private String url = "http://10.0.2.2:5000";
    public void sendRequest(int requestMethod, String route, byte[] jsonObject, Context context, ResponseListener listener) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        Gson gson = new Gson();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(requestMethod, url+route,
                response -> {
                    // Display the response string.
                    APIObject apiObject = gson.fromJson(response, APIObject.class);
                    listener.onSucces(apiObject);
                },
                error -> {
                    APIObject apiObject = gson.fromJson(String.valueOf(error), APIObject.class);
                    Log.v("fail", String.valueOf(error));
                    listener.onError(apiObject);
                }
        ) {
            @Override
            public byte[] getBody() {
                return jsonObject;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
       queue.add(stringRequest);
    }
}
