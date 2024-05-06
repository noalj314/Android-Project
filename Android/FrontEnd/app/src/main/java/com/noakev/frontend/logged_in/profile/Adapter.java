package com.noakev.frontend.logged_in.profile;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.noakev.frontend.databinding.RowBinding;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.RecyclerViewHolder> {

    private ArrayList<String> data;
    private ClickListener listener;

    public Adapter() {
        data = new ArrayList<>();
    }
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Skapa en ViewHolder och returner
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowBinding binding = RowBinding.inflate(inflater, parent, false);
        return new RecyclerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        String profileName = data.get(position);
        holder.getView().setText(profileName);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.textClicked(profileName);
        });
    }

    public void setListener(ClickListener listener) {
        this.listener = listener;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
        this.notifyDataSetChanged();

    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView view;
        public RecyclerViewHolder(RowBinding binding) {
            super(binding.getRoot());
            view = binding.textRow;
        }

        public TextView getView(){
            return view;
        }

    }
}

