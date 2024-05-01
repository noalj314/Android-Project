package com.noakev.frontend;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.noakev.frontend.databinding.FragmentProfileBinding;

/**
 */
public class ProfileFragment extends Fragment implements ClickListener {
    public interface DataFetchedCallback {
        void onDataFetched();
    }
    FragmentProfileBinding binding;
    private RecyclerView recyclerView;
    private Groups groups;
    private Adapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);

        recyclerView = binding.groups;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new Adapter();
        adapter.setListener(ProfileFragment.this);

        recyclerView.setAdapter(adapter);

        getDataVolley("https://brave-mud-8154b800471f41b1bbae6eea8237e22e.azurewebsites.net/grupper", () -> {
            adapter.setData(groups.getUsers());
        });

        return binding.getRoot();
    }
    public void getDataVolley(String url, DataFetchedCallback callback) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Gson gson = new Gson();
                        groups = gson.fromJson(response, Groups.class);
                        callback.onDataFetched();
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

    public void textClicked() {
        HomeActivity homeActivity = (HomeActivity)(getActivity());
        homeActivity.navigateHome();
    }
}