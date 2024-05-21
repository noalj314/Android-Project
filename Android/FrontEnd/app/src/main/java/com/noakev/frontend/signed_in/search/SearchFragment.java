package com.noakev.frontend.signed_in.search;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.noakev.frontend.backend.APIObject;
import com.noakev.frontend.backend.BackEndCommunicator;
import com.noakev.frontend.backend.ResponseListener;
import com.noakev.frontend.databinding.FragmentSearchBinding;
import com.noakev.frontend.signed_in.HomeActivity;

/**
 * A simple fragment for searching for users.
 */
public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private EditText searchTxt;

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