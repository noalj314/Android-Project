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

public class CreateUserFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentCreateUserBinding binding = FragmentCreateUserBinding.inflate(getLayoutInflater(), container, false);

        TextView username = (TextView) binding.usernametext;
        TextView password = (TextView) binding.passwordtext;

        Button register = binding.registerbtn;
        register.setOnClickListener(v -> {
            SignedOutActivity mainActivity = (SignedOutActivity)(getActivity());
            mainActivity.navigateToSignIn();
        });

        return binding.getRoot();
    }
}