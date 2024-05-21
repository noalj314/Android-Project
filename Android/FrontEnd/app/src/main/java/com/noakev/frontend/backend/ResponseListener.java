package com.noakev.frontend.backend;

import com.android.volley.VolleyError;

public interface ResponseListener {
    void onSucces(APIObject apiObject);

    void onError(APIObject apiObject);
}
