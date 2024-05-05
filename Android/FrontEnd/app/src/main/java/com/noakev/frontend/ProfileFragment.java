package com.noakev.frontend;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
    private static final int REQUEST_CODE = 22;
    private FragmentProfileBinding binding;
    private RecyclerView recyclerView;
    private Groups groups;
    private Adapter adapter;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ImageView selfieHolder;
    private String currentAddress;
    public interface DataFetchedCallback {
        void onDataFetched();
    }

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

        selfieHolder = binding.selfieholder;
        Button selfieBtn = binding.selfiebutton;
        selfieBtn.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_CODE);
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
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 22){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 0, 0,
                        locationListener);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            selfieHolder.setImageBitmap(photo);
           // binding.text.setText(currentAddress);
        } else {
            Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}