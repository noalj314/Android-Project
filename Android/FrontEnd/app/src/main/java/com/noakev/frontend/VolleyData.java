package com.noakev.frontend;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

public class VolleyData {
    public void getDataVolley(String url, ProfileFragment.DataFetchedCallback callback) {
        // Instantiate the RequestQueue.
        //RequestQueue queue = Volley.newRequestQueue(getContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Display the response string.
                    Gson gson = new Gson();
                    Groups groups = gson.fromJson(response, Groups.class);
                    callback.onDataFetched(groups);
                },
                error -> Log.e("Network", error.getMessage())
        );
       // queue.add(stringRequest);
    }
}
