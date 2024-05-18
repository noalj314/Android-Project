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
import com.noakev.frontend.backend.APIObject;
import com.noakev.frontend.backend.BackEndCommunicator;
import com.noakev.frontend.backend.ResponseListener;
import com.noakev.frontend.signed_in.event.Event;
import com.noakev.frontend.signed_in.event.Adapter;
import com.noakev.frontend.signed_in.event.Posts;
import com.noakev.frontend.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment {
    private ArrayList<HashMap> data;
    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private Posts events;
    private Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);

        RecyclerView rv = binding.posts;
        data = new ArrayList<>();
        Adapter adapter = new Adapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        //getPostsFromBackEnd();

        return binding.getRoot();
    }

    public void getPostsFromBackEnd() {
        BackEndCommunicator communicator = new BackEndCommunicator();
        communicator.sendRequest(0, "/user/get_following/get_events", null, getContext(), new ResponseListener() {
            @Override
            public void onSucces(APIObject apiObject) {
                /*
                int i = 0;
                for (Event event : events.getEvents()) {
                    HashMap<String, String> deats = new HashMap<>();
                    deats.put("id", "dW"+i);
                    data.add(deats);
                    i++;
                }
                adapter.setLocalData(data);
                 */
            }
            @Override
            public void onError(APIObject apiObject) {

            }
        });
    }
}


