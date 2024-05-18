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
import com.noakev.frontend.backend.BackEndCommunicator;
import com.noakev.frontend.backend.ResponseListener;
import com.noakev.frontend.databinding.FragmentCreateUserBinding;
import com.noakev.frontend.databinding.FragmentSignInBinding;
import com.noakev.frontend.signed_in.profile.Groups;
import com.noakev.frontend.signed_in.profile.ProfileFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CreateUserFragment extends Fragment {
    private TextView username;
    private TextView password;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentCreateUserBinding binding = FragmentCreateUserBinding.inflate(getLayoutInflater(), container, false);

        username = binding.usernametext;
        password = binding.passwordtext;

        Button register = binding.registerbtn;
        register.setOnClickListener(v -> {
            if (allFieldsAreFilled()) {
                Toast.makeText(getContext(), "Creating user...", Toast.LENGTH_SHORT).show();
                saveUser();
                SignedOutActivity mainActivity = (SignedOutActivity)(getActivity());
                mainActivity.navigateToSignIn();
            } else {
                Toast.makeText(getContext(), "Fill out all fields.", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    private void saveUser() {
        BackEndCommunicator communicator = new BackEndCommunicator();
        communicator.sendRequest(1, "/user/create", createBody(), getContext(), new ResponseListener() {
            @Override
            public void onSucces(APIObject apiObject) {}
            @Override
            public void onError(APIObject apiObject) {
            }
        });
    }
    public byte[] createBody() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username.getText().toString());
            jsonObject.put("password", password.getText().toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Log.v("JSON", jsonObject.toString());
        return jsonObject.toString().getBytes();
    }

    private boolean allFieldsAreFilled() {
        if (username.getText().toString().isEmpty()) {
            return false;
        } else return !username.getText().toString().isEmpty();
    }
}