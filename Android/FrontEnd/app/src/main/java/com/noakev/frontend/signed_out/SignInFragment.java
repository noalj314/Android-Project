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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.noakev.frontend.GlobalUser;
import com.noakev.frontend.backend.APIObject;
import com.noakev.frontend.backend.BackEndCommunicator;
import com.noakev.frontend.backend.ResponseListener;
import com.noakev.frontend.databinding.FragmentSignInBinding;

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

    public void getDataVolley(String route) {
        BackEndCommunicator communicator = new BackEndCommunicator();
        communicator.sendRequest(1, route, getBody(), getContext(), new ResponseListener() {
            public void onSucces(APIObject apiObject) {
                Log.e("RESPONSE", apiObject.getToken()+" : "+username.getText().toString());
                GlobalUser.setToken(apiObject.getToken());
                GlobalUser.setUsername(username.getText().toString());
                SignedOutActivity mainActivity = (SignedOutActivity)(getActivity());
                mainActivity.navigateHome();
                Toast.makeText(getContext(), "LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show();
            }

            public void onError(APIObject apiObject) {
                if (apiObject.getToken().isEmpty()) {
                    Toast.makeText(getContext(), "no such user!!!", Toast.LENGTH_SHORT).show();
                }
                Log.e("Network", "fail");
            }
        });
    }

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

    private boolean allFieldsAreFilled() {
        if (username.getText().toString().isEmpty()) {
            return false;
        } else return !username.getText().toString().isEmpty();
    }
}