package com.noakev.frontend.signed_in.post;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.noakev.frontend.signed_in.profile.ClickListener;
import com.noakev.frontend.R;

import java.util.ArrayList;
import java.util.HashMap;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private ArrayList<HashMap> localData;
    private ClickListener listener;

    //public Context mContext;
    //ublic List<Post> mPost;
    private String username;

    public PostAdapter() {
        this.localData = new ArrayList<>();
/*        Context mContext, List<Post> mPost
        this.mContext = mContext;
        this.mPost = mPost;*/
    }

    public void setListener(ClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashMap data = this.localData.get(position);
            Log.v("asd","asd"+data);

        //holder.username.setText(localData);
        //holder.username.setText("asd");
        //holder.description.setText("asd");
        //holder.post_text.setText("asd");
        //holder.publisher.setText("asd");
    }

    public void setLocalData(ArrayList<HashMap> data) {
        this.localData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.localData.size();
        //return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_profile, like, comment;
        public TextView post_text, username, publisher, description, comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            username = itemView.findViewById(R.id.username);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.decription);
            comments = itemView.findViewById(R.id.comments);
        }
    }

    private void publisherInfo(ImageView image_profile, TextView username, TextView publisher, String userid) {
        // Back end call -> getReference("Users").child(userid);
    }
}
