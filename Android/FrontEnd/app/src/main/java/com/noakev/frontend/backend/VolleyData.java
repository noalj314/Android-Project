package com.noakev.frontend.backend;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.noakev.frontend.signed_in.profile.ProfileFragment;
import com.noakev.frontend.signed_in.profile.Groups;

public class VolleyData {
    public void sendVolleyRequest(String route, JsonObject jsonObject) {

    }
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
/*
        try {
            obj.put("username", "myname");
            obj.put("password", "mypass");
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }

        volleyData.sendRequest("/user/login", obj, Request.Method.POST, null, (data) {
            // Gson stuff

        });
 */
