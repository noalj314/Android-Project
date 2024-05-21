package com.noakev.frontend.signed_in.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noakev.frontend.backend.APIObject;
import com.noakev.frontend.backend.BackEndCommunicator;
import com.noakev.frontend.backend.ResponseListener;
import com.noakev.frontend.signed_in.event.Event;
import com.noakev.frontend.signed_in.event.Adapter;
import com.noakev.frontend.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The fragment for managing the main feed.
 */
public class HomeFragment extends Fragment {
    private ArrayList<HashMap> data;
    private FragmentHomeBinding binding;
    private ArrayList<Event> events;
    private Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);

        RecyclerView rv = binding.posts;
        data = new ArrayList<>();
        adapter = new Adapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        getPostsFromBackEnd();

        return binding.getRoot();
    }

    /**
     * Send a request to backend to retrieve all posts by the current user or the people it's following.
     */
    public void getPostsFromBackEnd() {
        BackEndCommunicator communicator = new BackEndCommunicator();
        communicator.sendRequest(0, "/user/get_following/get_events", null, getContext(), new ResponseListener() {
            @Override
            public void onSucces(APIObject apiObject) {
                events = apiObject.getEvents();
                for (Event event : events) {
                    HashMap<String, String> deats = new HashMap<>();
                    deats.put("id", event.getEvent_id());
                    deats.put("username", event.getUsername());
                    deats.put("description", event.getDescription());
                    deats.put("location", event.getLocation());
                    deats.put("photo", event.getPhoto());
                    data.add(deats);
                }
                adapter.setLocalData(data, getContext(), getActivity());
            }
            @Override
            public void onError(APIObject apiObject) {

                Log.v("RESPONSE", apiObject.getEvents().toString());
            }
        });
    }
}


