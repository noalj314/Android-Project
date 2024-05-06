package com.noakev.frontend;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.noakev.frontend.databinding.FragmentProfileBinding;

/**
 */
public class ProfileFragment extends Fragment implements ClickListener {
    private static final int REQUEST_CODE = 22;
    private String currentUser;
    private String amountOfFollowers;
    private String amountOfFollowing;
    private TextView usernameTv;
    private TextView followersTv;
    private TextView followingTv;
    private FragmentProfileBinding binding;
    private RecyclerView followerRv;
    private RecyclerView followingRv;
    private Groups followerGroups;
    private Groups followingGroups;
    private Adapter followersAdapter;
    private Adapter followingAdapter;
    private ImageView selfieHolder;
    private int a = 0;
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

        followerRv = binding.groups;
        followerRv.setHasFixedSize(true);
        followerRv.setLayoutManager(new LinearLayoutManager(getContext()));
        followersAdapter = new Adapter();
        followersAdapter.setListener(ProfileFragment.this);
        followerRv.setAdapter(followersAdapter);

        followingRv = binding.followinggroups;
        followingRv.setHasFixedSize(true);
        followingRv.setLayoutManager(new LinearLayoutManager(getContext()));
        followingAdapter = new Adapter();
        followingAdapter.setListener(ProfileFragment.this);
        followingRv.setAdapter(followingAdapter);

        getDataVolley("https://brave-mud-8154b800471f41b1bbae6eea8237e22e.azurewebsites.net/grupper", (groups) -> {
            followerGroups = groups;
            followersAdapter.setData(followerGroups.getUsers());
            followersTv = binding.numberoffollowers;
            followersTv.setText("Followers: " + String.valueOf(followersAdapter.getItemCount()));
        });

        getDataVolley("https://brave-mud-8154b800471f41b1bbae6eea8237e22e.azurewebsites.net/grupper", (groups) -> {
            followingGroups = groups;
            followingAdapter.setData(followingGroups.getUsers());
            followingTv = binding.numberoffollowing;
            followingTv.setText("Following: " + String.valueOf(followingAdapter.getItemCount()));
        });

        selfieHolder = binding.selfieholder;
        Button selfieBtn = binding.selfiebutton;
        selfieBtn.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_CODE);
        });

        return binding.getRoot();
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
        // Logik för att kolla om currentProfile följer profileName
        // Om ja -> byt profil
        // Om nej "You're not following $"User"
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            selfieHolder.setImageBitmap(photo);
        } else {
            Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}