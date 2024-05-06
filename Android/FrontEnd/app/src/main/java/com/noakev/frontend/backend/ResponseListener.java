package com.noakev.frontend.backend;

import com.android.volley.VolleyError;

public interface ResponseListener {

    void onResponse(String response);

    void onError(VolleyError error);
}
