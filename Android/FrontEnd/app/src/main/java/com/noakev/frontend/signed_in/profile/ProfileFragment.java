package com.noakev.frontend.signed_in.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.noakev.frontend.GlobalUser;
import com.noakev.frontend.backend.APIObject;
import com.noakev.frontend.backend.BackEndCommunicator;
import com.noakev.frontend.backend.ResponseListener;
import com.noakev.frontend.databinding.FragmentProfileBinding;
import com.noakev.frontend.signed_in.HomeActivity;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The fragment for managing the different profiles.
 */
public class ProfileFragment extends Fragment implements ClickListener {
    private String currentUser = GlobalUser.getUsername();
    private String currentAccount;
    private TextView followersTv;
    private TextView followingTv;
    private FragmentProfileBinding binding;
    private ArrayList<String> followerGroups;
    private ArrayList<String> followingGroups;
    private Adapter followersAdapter;
    private Adapter followingAdapter;
    private Button followBtn;
    private TextView usernameTv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);

        usernameTv = binding.username;
        followBtn = binding.followbtn;

        if (getArguments() != null) {
            currentAccount = getArguments().getString("username");
            if (!Objects.equals(currentAccount, currentUser)) {
                usernameTv.setText(currentAccount);
                userIsFollowed();
            } else { sameUser(); }
            setArguments(null);
        } else { sameUser(); }

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

        getFollowers();
        getFollowing();

        followBtn.setOnClickListener(v -> {
            String input = "";
            BackEndCommunicator communicator = new BackEndCommunicator();

            if (followBtn.getText().toString().equals("follow")) {
                input = "follow";
                followBtn.setText("unfollow");
            } else if (followBtn.getText().toString().equals("unfollow")) {
                input = "unfollow";
                followBtn.setText("follow");
            }

            communicator.sendRequest(1, "/user/"+input+"/" + currentAccount, null, getContext(), new ResponseListener() {
                @Override
                public void onSucces(APIObject apiObject) {
                    Log.v("abcd", apiObject.getMessage());
                }
                @Override
                public void onError(APIObject apiObject) {
                    Log.v("abcd", apiObject.getMessage());
                }
            });
        });

        return binding.getRoot();
    }

    private void sameUser() {
        currentAccount = currentUser;
        usernameTv.setText(currentAccount);
        binding.followbtn.setVisibility(View.INVISIBLE);
    }

    /**
     * Send a request to backend to retrieve all followers.
     */
    private void getFollowers() {
        BackEndCommunicator communicator = new BackEndCommunicator();
        communicator.sendRequest(0, "/user/get_followers/" + currentAccount, null, getContext(), new ResponseListener() {
            @Override
            public void onSucces(APIObject apiObject) {
                followerGroups = apiObject.getFollowersList();
                followersAdapter.setData(followerGroups);
                followersTv = binding.numberoffollowers;
                followersTv.setText("Followers: " + followersAdapter.getItemCount());
            }
            @Override
            public void onError(APIObject apiObject) {
                Log.v("Error", apiObject.getMessage());
            }
        });
    }


    /**
     * Send a request to backend to retrieve all following.
     */
    private void getFollowing() {
        BackEndCommunicator communicator = new BackEndCommunicator();
        communicator.sendRequest(0, "/user/get_following/" + currentAccount, null, getContext(), new ResponseListener() {
            @Override
            public void onSucces(APIObject apiObject) {
                followingGroups = apiObject.getFollowingList();
                followingAdapter.setData(followingGroups);
                followingTv = binding.numberoffollowing;
                followingTv.setText("Following: " + followingAdapter.getItemCount());            }
            @Override
            public void onError(APIObject apiObject) {
                Log.v("Error", apiObject.getMessage());
            }
        });
    }

    /**
     * Check if the current account is followed by the signed in user.
     */
    private void userIsFollowed() {
        BackEndCommunicator communicator = new BackEndCommunicator();
        communicator.sendRequest(0, "/user/check_following/"+currentAccount, null, getContext(), new ResponseListener() {
            @Override
            public void onSucces(APIObject apiObject) {
                Log.v("Response", apiObject.getMessage());
                if (Objects.equals(apiObject.getMessage(), "true")) {
                    binding.followbtn.setText("unfollow");
                } else if (Objects.equals(apiObject.getMessage(), "false")) {
                    binding.followbtn.setText("follow");
                }
            }
            @Override
            public void onError(APIObject apiObject) {
                Log.e("Error", apiObject.getMessage());
            }
        });
    }

    public void textClicked(String profileName) {
        HomeActivity homeActivity = (HomeActivity)(getActivity());
        homeActivity.navigateToSelf(profileName);
    }
}