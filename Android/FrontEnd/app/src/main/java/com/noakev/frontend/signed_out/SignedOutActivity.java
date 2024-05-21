package com.noakev.frontend.signed_out;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.noakev.frontend.R;
import com.noakev.frontend.signed_in.HomeActivity;

/**
 * The activity for being signed out. Contains 2 different fragments.
 */
public class SignedOutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_out);
    }

    public void navigateHome() {
        Intent intent = new Intent(SignedOutActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    public void navigateToRegistration() {
        NavDirections action = SignInFragmentDirections.actionSignInFragment2ToCreateUserFragment2();
        Navigation.findNavController(this, R.id.fragment_container).navigate(action);
    }

    public void navigateToSignIn() {
        NavDirections action = CreateUserFragmentDirections.actionCreateUserFragment2ToSignInFragment2();
        Navigation.findNavController(this, R.id.fragment_container).navigate(action);
    }
}