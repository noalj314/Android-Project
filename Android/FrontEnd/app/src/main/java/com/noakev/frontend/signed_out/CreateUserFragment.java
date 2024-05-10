package com.noakev.frontend.signed_out;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.noakev.frontend.R;
import com.noakev.frontend.databinding.FragmentCreateUserBinding;
import com.noakev.frontend.databinding.FragmentSignInBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateUserFragment extends Fragment {
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
            createUser();
            saveUser();
            SignedOutActivity mainActivity = (SignedOutActivity)(getActivity());
            mainActivity.navigateToSignIn();
        });

        return binding.getRoot();
    }

    private void createUser() {
        newUser = new JSONObject();
        try {
            newUser.put("username", String.valueOf(username.getText()));
            newUser.put("password", String.valueOf(password.getText()));
            newUser.put("description", "something");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveUser() {
        //.POST user
    }
}