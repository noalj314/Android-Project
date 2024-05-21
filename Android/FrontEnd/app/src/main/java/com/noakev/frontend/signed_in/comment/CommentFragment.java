package com.noakev.frontend.signed_in.comment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.noakev.frontend.backend.APIObject;
import com.noakev.frontend.backend.BackEndCommunicator;
import com.noakev.frontend.backend.ResponseListener;
import com.noakev.frontend.databinding.FragmentCommentBinding;
import com.noakev.frontend.signed_in.HomeActivity;
import com.noakev.frontend.signed_in.event.Adapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentFragment extends Fragment {
    private ArrayList<HashMap> data;
    private ArrayList<Comment> comments;
    private CommentAdapter commentAdapter;
    private FragmentCommentBinding binding;
    private EditText newComment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCommentBinding.inflate(getLayoutInflater(), container, false);

        newComment = binding.comment;
        Button send = binding.send;

        RecyclerView rv = binding.comments;
        data = new ArrayList<>();
        commentAdapter = new CommentAdapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(commentAdapter);

        loadComments(getArguments().getString("eventID"));

        send.setOnClickListener(v -> {
            if (newComment.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Write a text", Toast.LENGTH_SHORT).show();
            } else {
                saveComment(getArguments().getString("eventID"));
                HomeActivity homeActivity = (HomeActivity)(getActivity());
                homeActivity.navigateHomeFromComment();
            }
        });

        return binding.getRoot();
    }

    private void saveComment(String eventID) {
        BackEndCommunicator communicator = new BackEndCommunicator();
        communicator.sendRequest(1, "/event/comment/"+eventID, createBody(), getContext(), new ResponseListener() {
            @Override
            public void onSucces(APIObject apiObject) {
                Log.v("RESPONSE", apiObject.getMessage());
            }
            @Override
            public void onError(APIObject apiObject) {}
        });
    }

    public byte[] createBody() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("text", newComment.getText().toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject.toString().getBytes();
    }

    private void loadComments(String eventID) {
        BackEndCommunicator communicator = new BackEndCommunicator();
        communicator.sendRequest(0, "/event/get_comments/"+eventID, null, getContext(), new ResponseListener() {
            @Override
            public void onSucces(APIObject apiObject) {
                comments = apiObject.getComments();
                for (Comment comment : comments) {
                    HashMap<String, String> deats = new HashMap<>();
                    deats.put("id", comment.getId());
                    deats.put("username", comment.getUsername());
                    deats.put("event_id", comment.getEvent_id());
                    deats.put("text", comment.getText());
                    data.add(deats);
                }
                commentAdapter.setLocalData(data, getContext(), getActivity());
            }

            @Override
            public void onError(APIObject apiObject) {

            }
        });
    }
}