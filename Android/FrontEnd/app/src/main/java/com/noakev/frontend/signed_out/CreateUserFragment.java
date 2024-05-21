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

import com.noakev.frontend.backend.APIObject;
import com.noakev.frontend.backend.BackEndCommunicator;
import com.noakev.frontend.backend.ResponseListener;
import com.noakev.frontend.databinding.FragmentCreateUserBinding;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The fragment for managing user creation and registration.
 */
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
            } else {
                Toast.makeText(getContext(), "Fill out all fields.", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    /**
     * Save the new user to the database.
     */
    private void saveUser() {
        BackEndCommunicator communicator = new BackEndCommunicator();
        communicator.sendRequest(1, "/user/create", createBody(), getContext(), new ResponseListener() {
            @Override
            public void onSucces(APIObject apiObject) {
                if (apiObject.statusFail()) {
                    Toast.makeText(getContext(), "Username taken", Toast.LENGTH_SHORT).show();
                } else {
                    SignedOutActivity mainActivity = (SignedOutActivity) (getActivity());
                    mainActivity.navigateToSignIn();
                }
            }
            @Override
            public void onError(APIObject apiObject) {}
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