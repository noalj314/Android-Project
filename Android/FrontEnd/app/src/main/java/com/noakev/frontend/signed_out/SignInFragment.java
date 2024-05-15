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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.noakev.frontend.GlobalUser;
import com.noakev.frontend.R;
import com.noakev.frontend.backend.APIObject;
import com.noakev.frontend.databinding.FragmentSignInBinding;
import com.noakev.frontend.signed_in.post.PostFragment;
import com.noakev.frontend.signed_in.profile.Groups;
import com.noakev.frontend.signed_in.profile.ProfileFragment;

import org.json.JSONException;
import org.json.JSONObject;


public class SignInFragment extends Fragment {
    public interface DataFetchedCallbackSign {
        void onDataFetched();
    }
    private APIObject apiObject;
    private TextView username;
    private TextView password;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSignInBinding binding = FragmentSignInBinding.inflate(getLayoutInflater(), container, false);

        username = (TextView) binding.usernametext;
        password = (TextView) binding.passwordtext;

        Button loginbtn = binding.loginbtn;
        loginbtn.setOnClickListener(v -> {
            if (allFieldsAreFilled()) {
                fieldsAreCorrect();
            } else {
                Toast.makeText(getContext(), "Fill out all fields.", Toast.LENGTH_SHORT).show();
            }
        });

        Button register = binding.register;
        register.setOnClickListener(v -> {
            SignedOutActivity mainActivity = (SignedOutActivity)(getActivity());
            mainActivity.navigateToRegistration();
        });

        return binding.getRoot();
    }

    private void fieldsAreCorrect() {
        getDataVolley("http://10.0.2.2:5000/user/login", () -> {
            if (apiObject.getToken().isEmpty()) {
                Toast.makeText(getContext(), "no such user!!!", Toast.LENGTH_SHORT).show();
            } else {
                GlobalUser globalUser = new GlobalUser(apiObject.getToken(), username.getText().toString());
                SignedOutActivity mainActivity = (SignedOutActivity)(getActivity());
                mainActivity.navigateHome();
                Toast.makeText(getContext(), "LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getDataVolley(String url, DataFetchedCallbackSign callback) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Gson gson = new Gson();
                    apiObject = gson.fromJson(response, APIObject.class);
                    callback.onDataFetched();
                },
                error -> Log.e("Network", "fail")
        ) {
            @Override
            public byte[] getBody() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username", username.getText().toString());
                    jsonObject.put("password", password.getText().toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                return jsonObject.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        queue.add(stringRequest);
    }


    private boolean allFieldsAreFilled() {
        if (username.getText().toString().isEmpty()) {
            return false;
        } else return !username.getText().toString().isEmpty();
    }
}