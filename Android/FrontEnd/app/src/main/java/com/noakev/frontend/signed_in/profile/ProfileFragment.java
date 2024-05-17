package com.noakev.frontend.signed_in.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.noakev.frontend.GlobalUser;
import com.noakev.frontend.databinding.FragmentProfileBinding;
import com.noakev.frontend.signed_in.HomeActivity;

/**
 */
public class ProfileFragment extends Fragment implements ClickListener {
    private static final int REQUEST_CODE = 22;
    private String currentUser = GlobalUser.getUsername();
    private TextView followersTv;
    private TextView followingTv;
    private FragmentProfileBinding binding;
    private Groups followerGroups;
    private Groups followingGroups;
    private Adapter followersAdapter;
    private Adapter followingAdapter;
    private TextView usernameTv;
    public interface DataFetchedCallback {
        void onDataFetched(Groups groups);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);

        usernameTv = binding.username;

        if (getArguments() != null) {
            String user = getArguments().getString("username");
            usernameTv.setText(user);
            if (userIsFollowerd()) {
                Log.v("username",getArguments().toString());
                binding.followbtn.setText("unfollow");
            }
            setArguments(null);
            //Check following
        } else {
            usernameTv.setText(currentUser);
            binding.followbtn.setVisibility(View.INVISIBLE);
        }

        RecyclerView followerRv = binding.groups;
        followerRv.setHasFixedSize(true);
        followerRv.setLayoutManager(new LinearLayoutManager(getContext()));
        followersAdapter = new Adapter();
        followersAdapter.setListener(ProfileFragment.this);
        followerRv.setAdapter(followersAdapter);

        RecyclerView followingRv = binding.followinggroups;
        followingRv.setHasFixedSize(true);
        followingRv.setLayoutManager(new LinearLayoutManager(getContext()));
        followingAdapter = new Adapter();
        followingAdapter.setListener(ProfileFragment.this);
        followingRv.setAdapter(followingAdapter);

        getDataVolley("https://brave-mud-8154b800471f41b1bbae6eea8237e22e.azurewebsites.net/grupper", (groups) -> {
            followerGroups = groups;
            followersAdapter.setData(followerGroups.getUsers());
            followersTv = binding.numberoffollowers;
            followersTv.setText("Followers: " + followersAdapter.getItemCount());
        });

        getDataVolley("https://brave-mud-8154b800471f41b1bbae6eea8237e22e.azurewebsites.net/grupper", (groups) -> {
            followingGroups = groups;
            followingAdapter.setData(followingGroups.getUsers());
            followingTv = binding.numberoffollowing;
            followingTv.setText("Following: " + followingAdapter.getItemCount());
        });

        return binding.getRoot();
    }

    private boolean userIsFollowerd() {
        return false;
    }

    public void getDataVolley(String url, DataFetchedCallback callback) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Display the response string.
                    Gson gson = new Gson();
                    Groups groups = gson.fromJson(response, Groups.class);
                    callback.onDataFetched(groups);
                },
                error -> Log.e("Network", error.getMessage())
        );
        queue.add(stringRequest);
    }

    public void textClicked(String profileName) {
        HomeActivity homeActivity = (HomeActivity)(getActivity());
        homeActivity.navigateToSelf(profileName);
    }
}