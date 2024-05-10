package com.noakev.frontend.signed_in.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.noakev.frontend.signed_in.post.Post;
import com.noakev.frontend.signed_in.post.PostAdapter;
import com.noakev.frontend.signed_in.post.Posts;
import com.noakev.frontend.databinding.FragmentHomeBinding;
import com.noakev.frontend.signed_in.profile.Groups;
import com.noakev.frontend.signed_in.profile.ProfileFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private Posts events;
    private PostAdapter adapter;
    public interface DataFetchedCallbackHome {
        void onDataFetched(Posts events);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);

        RecyclerView rv = binding.posts;
        ArrayList<HashMap> data = new ArrayList<>();
        PostAdapter postAdapter = new PostAdapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(postAdapter);

        getDataVolley("http://localhost:5000/event/get_events/", (events) -> {
            int i = 0;
            for (Post post : events.getEvents()) {
                HashMap<String, String> deats = new HashMap<>();
                deats.put("id", "dW"+i);
                data.add(deats);
                i++;
            }
        });
        postAdapter.setLocalData(data);

        return binding.getRoot();
    }

    public void getDataVolley(String url, DataFetchedCallbackHome callback) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Gson gson = new Gson();
                        events = gson.fromJson(response, Posts.class);
                        callback.onDataFetched(events);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Network", error.getMessage());
            }
        });
        //  d the request to the RequestQueue.
        queue.add(stringRequest);
    }
}

