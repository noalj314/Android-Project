package com.noakev.frontend.signed_in.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.noakev.frontend.backend.VolleyData;
import com.noakev.frontend.databinding.FragmentOtherProfilesBinding;

import org.json.JSONObject;

/**
 */
public class OtherProfilesFragment extends Fragment implements ClickListener {
    private static final int REQUEST_CODE = 22;
    private String currentUser;
    private TextView followersTv;
    private TextView followingTv;
    private FragmentOtherProfilesBinding binding;
    private Groups followerGroups;
    private Groups followingGroups;
    private Adapter followersAdapter;
    private Adapter followingAdapter;
    private ImageView selfieHolder;
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
        binding = FragmentOtherProfilesBinding.inflate(getLayoutInflater(), container, false);

        RecyclerView followerRv = binding.groups;
        followerRv.setHasFixedSize(true);
        followerRv.setLayoutManager(new LinearLayoutManager(getContext()));
        followersAdapter = new Adapter();
        followersAdapter.setListener(OtherProfilesFragment.this);
        followerRv.setAdapter(followersAdapter);

        RecyclerView followingRv = binding.followinggroups;
        followingRv.setHasFixedSize(true);
        followingRv.setLayoutManager(new LinearLayoutManager(getContext()));
        followingAdapter = new Adapter();
        followingAdapter.setListener(OtherProfilesFragment.this);
        followingRv.setAdapter(followingAdapter);


        // Lok för att hämta nuvarande användare
        // Get current user
        TextView usernameTv = binding.username;
        usernameTv.setText(currentUser);


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

        VolleyData volleyData = new VolleyData();
        JSONObject obj = new JSONObject();

        selfieHolder = binding.selfieholder;

        return binding.getRoot();
    }
    public void getDataVolley(String url, ProfileFragment.DataFetchedCallback callback) {
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
        // Logik för att kolla om currentProfile följer profileName
        // Om ja -> byt profil
        // Om nej "You're not following $"User"
    }
}