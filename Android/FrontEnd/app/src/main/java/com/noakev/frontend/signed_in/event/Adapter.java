package com.noakev.frontend.signed_in.event;

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

import com.noakev.frontend.backend.APIObject;
import com.noakev.frontend.backend.BackEndCommunicator;
import com.noakev.frontend.backend.ResponseListener;
import com.noakev.frontend.signed_in.HomeActivity;
import com.noakev.frontend.signed_in.profile.ClickListener;
import com.noakev.frontend.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private ArrayList<HashMap> localData;
    private Context context;
    private Activity activity;

    public Adapter() {
        this.localData = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashMap event = localData.get(position);
        holder.publisher.setText(event.get("username").toString());
        holder.description.setText(event.get("description").toString());
        holder.eventImage.setImageBitmap(getStringAsImage(event.get("photo").toString()));
        holder.location.setText(event.get("location").toString());
        holder.like.setTag(0);

        holder.like.setOnClickListener(v -> {
            saveLikeToBackEnd(event.get("id").toString(), holder);
        });

        holder.comment.setOnClickListener(v -> {
            HomeActivity homeActivity = (HomeActivity)(activity);
            homeActivity.navigateToComment(event.get("id").toString());
        });
    }

    private void saveLikeToBackEnd(String eventID, ViewHolder holder) {
        BackEndCommunicator communicator = new BackEndCommunicator();
        String input = "";

        if (holder.like.getTag().toString().equals("0")) {
            input = "follow";
            holder.like.setImageResource(R.drawable.heartliked);
            holder.like.setTag("1");
        } else if (holder.like.getTag().toString().equals("1")) {
            input = "unfollow";
            holder.like.setImageResource(R.drawable.heartunliked);
            holder.like.setTag("0");
        }

        communicator.sendRequest(1, "/event/"+input+"/"+eventID, null, context, new ResponseListener() {
            @Override
            public void onSucces(APIObject apiObject) {
                Log.v("RESPONSE", apiObject.getMessage());
            }

            @Override
            public void onError(APIObject apiObject) {}
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
        public ImageView eventImage, like, comment;
        public TextView publisher, description, comments, location;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            publisher = itemView.findViewById(R.id.publisher);
            location = itemView.findViewById(R.id.location);
            eventImage = itemView.findViewById(R.id.event_image);
            description = itemView.findViewById(R.id.decription);
            comments = itemView.findViewById(R.id.comments);
        }
    }

    public Bitmap getStringAsImage(String encodedImage) {
        try {
            // Decode the Base64 string
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            // Convert the byte array to a Bitmap
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (IllegalArgumentException e) {
            // Handle the exception if the encodedImage is not properly Base64 encoded
            e.printStackTrace();
            return null;
        }
    }
}
