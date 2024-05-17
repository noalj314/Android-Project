package com.noakev.frontend.signed_in.search;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.noakev.frontend.R;
import com.noakev.frontend.backend.APIObject;
import com.noakev.frontend.backend.BackEndCommunicator;
import com.noakev.frontend.backend.ResponseListener;
import com.noakev.frontend.databinding.FragmentSearchBinding;
import com.noakev.frontend.signed_in.HomeActivity;
import com.noakev.frontend.signed_in.profile.Groups;
import com.noakev.frontend.signed_in.profile.ProfileFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private EditText searchTxt;
    private String username;
    private APIObject apiObject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(getLayoutInflater(), container, false);

        searchTxt = binding.searchtext;
        TextView user = binding.relevantuser;

        Button searchBtn = binding.searchbtn;
        searchBtn.setOnClickListener(v -> {
            final String userToFind = searchTxt.getText().toString();
            BackEndCommunicator communicator = new BackEndCommunicator();
            communicator.sendRequest(0, "/user/find_user/"+userToFind, null, getContext(), new ResponseListener() {
                @Override
                public void onSucces(APIObject apiObject) {
                    user.setText(apiObject.getUsername());
                }

                @Override
                public void onError(APIObject apiObject) {
                    Log.v("Error", apiObject.getMessage());
                }
            });
        });

        user.setOnClickListener(v -> {
            HomeActivity homeActivity = (HomeActivity)(getActivity());
            homeActivity.navigateToProfile(user.getText().toString());
        });

        return binding.getRoot();
    }
}