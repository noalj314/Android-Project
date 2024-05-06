package com.noakev.frontend.logged_in.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noakev.frontend.logged_in.post.PostAdapter;
import com.noakev.frontend.logged_in.profile.Groups;
import com.noakev.frontend.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private Groups groups;
    private PostAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);

        RecyclerView rv = binding.posts;
        ArrayList<HashMap> data = new ArrayList<>();
        PostAdapter Newadapter = new PostAdapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(Newadapter);

        for (int x = 0; x<= 10; x++){
            HashMap<String, String> deats = new HashMap<>();
            deats.put("id", "dW"+x);
            data.add(deats);
        }
        Newadapter.setLocalData(data);


//        ArrayList<Integer> data = new ArrayList<>();
//        recyclerView = binding.posts;
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        adapter = new PostAdapter();
//        recyclerView.setAdapter(adapter);


        /*getDataVolley("https://brave-mud-8154b800471f41b1bbae6eea8237e22e.azurewebsites.net/grupper", () -> {
            adapter.setData(groups.getUsers());
        });*/

        return binding.getRoot();
    }
/*    public void getDataVolley(String url, ProfileFragment.DataFetchedCallback callback) {
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
    }*/
}