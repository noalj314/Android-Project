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

import com.noakev.frontend.GlobalUser;
import com.noakev.frontend.backend.APIObject;
import com.noakev.frontend.backend.BackEndCommunicator;
import com.noakev.frontend.backend.ResponseListener;
import com.noakev.frontend.databinding.FragmentSignInBinding;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Our fragment for managing the sign in page.
 */
public class SignInFragment extends Fragment {
    private TextView username;
    private TextView password;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSignInBinding binding = FragmentSignInBinding.inflate(getLayoutInflater(), container, false);

        username = binding.usernametext;
        password = binding.passwordtext;

        Button loginbtn = binding.loginbtn;
        loginbtn.setOnClickListener(v -> {
            if (allFieldsAreFilled()) {
                getDataVolley("/user/login");
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

    /**
     * Check if their is a user with given username and passwords.
     */
    public void getDataVolley(String route) {
        BackEndCommunicator communicator = new BackEndCommunicator();
        communicator.sendRequest(1, route, createBody(), getContext(), new ResponseListener() {
            public void onSucces(APIObject apiObject) {
                if (apiObject.statusFail()) {
                    Toast.makeText(getContext(), "Wrong username or password", Toast.LENGTH_SHORT).show();
                } else {
                    GlobalUser.setToken(apiObject.getToken());
                    GlobalUser.setUsername(username.getText().toString());
                    SignedOutActivity mainActivity = (SignedOutActivity)(getActivity());
                    mainActivity.navigateHome();
                    Toast.makeText(getContext(), "LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show();
                }
            }
            public void onError(APIObject apiObject) { Log.e("Network", "fail"); }
        });
    }

    /**
     * Create the JSON Object to send to back-end.
     */
    public byte[] createBody() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username.getText().toString());
            jsonObject.put("password", password.getText().toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject.toString().getBytes();
    }

    private boolean allFieldsAreFilled() {
        if (username.getText().toString().isEmpty()) {
            return false;
        } else return !username.getText().toString().isEmpty();
    }
}