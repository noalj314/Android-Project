package com.noakev.frontend.signed_in.comment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.noakev.frontend.R;
import com.noakev.frontend.backend.APIObject;
import com.noakev.frontend.backend.BackEndCommunicator;
import com.noakev.frontend.backend.ResponseListener;
import com.noakev.frontend.signed_in.HomeActivity;
import com.noakev.frontend.signed_in.profile.ClickListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private ArrayList<HashMap> localData;
    private Context context;
    private Activity activity;

    public CommentAdapter() {
        this.localData = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashMap comment = localData.get(position);
        holder.publisher.setText(comment.get("username").toString());
        holder.comment.setText(comment.get("text").toString());

        holder.publisher.setOnClickListener(v -> {
            HomeActivity homeActivity = (HomeActivity)(activity);
            homeActivity.navigateToProfileFromComment(holder.publisher.getText().toString());
        });
    }
    public void setLocalData(ArrayList<HashMap> data, Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.localData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.localData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView publisher, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            comment = itemView.findViewById(R.id.comment);
            publisher = itemView.findViewById(R.id.publisher);
        }
    }
}
