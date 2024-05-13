package com.noakev.frontend.signed_out;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.noakev.frontend.R;
import com.noakev.frontend.backend.APIObject;
import com.noakev.frontend.databinding.FragmentCreateUserBinding;
import com.noakev.frontend.databinding.FragmentSignInBinding;
import com.noakev.frontend.signed_in.profile.Groups;
import com.noakev.frontend.signed_in.profile.ProfileFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateUserFragment extends Fragment {
    public interface DataFetchedCallbackCreate {
        void onDataFetched();
    }
    private TextView username;
    private TextView password;
    private JSONObject newUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentCreateUserBinding binding = FragmentCreateUserBinding.inflate(getLayoutInflater(), container, false);

        username = (TextView) binding.usernametext;
        password = (TextView) binding.passwordtext;

        Button register = binding.registerbtn;
        register.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Creating user...", Toast.LENGTH_SHORT).show();
            saveUser();
            SignedOutActivity mainActivity = (SignedOutActivity)(getActivity());
            mainActivity.navigateToSignIn();
        });

        return binding.getRoot();
    }

    private void saveUser() {
        getDataVolley("http://10.0.2.2:5000/user/create", () -> {
        });
    }
    public void getDataVolley(String url, DataFetchedCallbackCreate callback) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    System.out.println("gets into success");
                    // Create APIObject based on response data
                    Gson gson = new Gson();
                    System.out.println(response);
                    //APIObject apiObject = gson.fromJson(response, APIObject.class);

                    // Call listeners function
                    //listener.onSuccess(apiObject);
                },
                volleyError -> {

                    if (volleyError.getClass() == com.android.volley.ClientError.class) {
                        // Get byte array from response data
                        byte[] byteArray = volleyError.networkResponse.data;

                        // Convert byte array to APIObject using Gson
                        Gson gson = new Gson();
                        String jsonStringFromByteArray = new String(byteArray, StandardCharsets.UTF_8);
                        APIObject errorResponse = gson.fromJson(jsonStringFromByteArray, APIObject.class);

                        // Call listeners function
                        //listener.onError(errorResponse);
                    } else {
                        Log.e("NETWORK", "Network error.");
                        volleyError.printStackTrace();

                        //APIObject errorResponse = new APIObject("fail", "Network error. Please try again.");
                        //listener.onError(errorResponse);

                    }
                }) {
            @Override
            public byte[] getBody() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username", username.getText().toString());
                    jsonObject.put("password", password.getText().toString());
                    jsonObject.put("description", "hejsan");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(jsonObject.toString());

                return jsonObject.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        int timeout = 10000; // 10 seconds
        RetryPolicy policy = new DefaultRetryPolicy(timeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);


        queue.add(stringRequest);

    }
}

/*

                        newUser.put("username", String.valueOf(username.getText()));
                        newUser.put("password", String.valueOf(password.getText()));
                        newUser.put("description", "something");
 */