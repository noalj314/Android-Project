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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.noakev.frontend.signed_in.event.Event;
import com.noakev.frontend.signed_in.event.Adapter;
import com.noakev.frontend.signed_in.event.Posts;
import com.noakev.frontend.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private Posts events;
    private Adapter adapter;
    public interface DataFetchedCallbackHome {
        void onDataFetched(Posts events);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);

        RecyclerView rv = binding.posts;
        ArrayList<HashMap> data = new ArrayList<>();
        Adapter adapter = new Adapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        getDataVolley("http://localhost:5000/event/get_events/", (events) -> {
            int i = 0;
            for (Event event : events.getEvents()) {
                HashMap<String, String> deats = new HashMap<>();
                deats.put("id", "dW"+i);
                data.add(deats);
                i++;
            }
        });
        adapter.setLocalData(data);

        return binding.getRoot();
    }

    public void getDataVolley(String url, DataFetchedCallbackHome callback) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Display the response string.
                    Gson gson = new Gson();
                    events = gson.fromJson(response, Posts.class);
                    callback.onDataFetched(events);
                }, error -> Log.e("Network", error.getMessage())
        );
        queue.add(stringRequest);
    }
}


