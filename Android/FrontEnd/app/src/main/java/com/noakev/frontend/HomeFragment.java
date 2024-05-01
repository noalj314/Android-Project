package com.noakev.frontend;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.noakev.frontend.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    Button profileBtn;
    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentHomeBinding binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);

        profileBtn = binding.profilebutton;

        profileBtn.setOnClickListener(v -> textClicked(v));

        return binding.getRoot();
    }


    public void textClicked(View v) {
        HomeActivity homeActivity = (HomeActivity) (getActivity());
        homeActivity.navigateToProfile(v);
    }

}