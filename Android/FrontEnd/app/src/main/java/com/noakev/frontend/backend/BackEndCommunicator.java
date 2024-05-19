package com.noakev.frontend.backend;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.noakev.frontend.GlobalUser;

import java.util.HashMap;
import java.util.Map;

public class BackEndCommunicator {
    private final String URL = "http://10.0.2.2:5000";
    public void sendRequest(int requestMethod, String route, byte[] jsonObject, Context context, ResponseListener listener) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        Gson gson = new Gson();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(requestMethod, URL +route,
                response -> {
                    APIObject apiObject = gson.fromJson(response, APIObject.class);
                    Log.v("RESPONSE", response.toString());
                    listener.onSucces(apiObject);
                },
                error -> {
                    APIObject apiObject = gson.fromJson(String.valueOf(error), APIObject.class);
                    Log.e("RESPONSE", error.toString());
                    listener.onError(apiObject);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + GlobalUser.getToken());
                return params;
            }

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
