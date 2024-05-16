package com.noakev.frontend.signed_in.post;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.noakev.frontend.GlobalUser;
import com.noakev.frontend.backend.APIObject;
import com.noakev.frontend.databinding.FragmentPostBinding;
import com.noakev.frontend.signed_in.HomeActivity;
import com.noakev.frontend.signed_in.profile.Groups;
import com.noakev.frontend.signed_in.profile.ProfileFragment;
import com.noakev.frontend.signed_out.SignInFragment;
import com.noakev.frontend.signed_out.SignedOutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

/**
 */
public class PostFragment extends Fragment {
    private String currentUser = GlobalUser.getUsername();
    private static final int REQUEST_CODE = 22;
    private JSONObject newPost;
    private FragmentPostBinding binding;
    private Button postBtn;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String currentAddress;
    private Bitmap photo;
    private TextView username;
    public interface DataFetchedCallbackPost {
        void onDataFetched();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPostBinding.inflate(getLayoutInflater(), container, false);

        username = binding.publisher;
        Button selfieBtn = binding.selfiebutton;
        Button locationBtn = binding.locationbtn;
        Button postBtn = binding.postbtn;
        binding.location.setText("Linköping");

        username.setText(GlobalUser.getUsername());

        selfieBtn.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_CODE);

        });
        locationBtn.setOnClickListener(v -> askForLocation());
        postBtn.setOnClickListener(v -> createNewPost());

        return binding.getRoot();
    }

    private void createNewPost() {
        if (allColumnsAreFilled()) {
            Toast.makeText(getContext(), "Creating post...", Toast.LENGTH_SHORT).show();
            getDataVolley("http://10.0.2.2:5000/event/create/" + currentUser, () -> {
                Toast.makeText(getContext(), "Successfully created post.", Toast.LENGTH_SHORT).show();
                HomeActivity homeActivity = (HomeActivity)(getActivity());
                homeActivity.navigateHome();
            });
            // Save to database
        } else {
            Toast.makeText(getContext(), "Insufficient information!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean allColumnsAreFilled() {
        if (String.valueOf(binding.description.getText()).isEmpty()) {
            return false;
        } else if (String.valueOf(binding.location.getText()).isEmpty()) {
            return false;
        } else if (getImageAsString().isEmpty()) {
            return false;
        }
        return true;
    }

    public void getDataVolley(String url, SignInFragment.DataFetchedCallbackSign callback) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    callback.onDataFetched();
                },
                error -> {
                    if (error.getClass() == com.android.volley.ClientError.class) {
                        // Get byte array from response data
                        byte[] byteArray = error.networkResponse.data;

                        // Convert byte array to APIObject using Gson
                        Gson gson = new Gson();
                        String jsonStringFromByteArray = new String(byteArray, StandardCharsets.UTF_8);
                        APIObject errorResponse = gson.fromJson(jsonStringFromByteArray, APIObject.class);
                    } else {
                        Log.e("NETWORK", "Network error.");
                        error.printStackTrace();

                    }
                }) {
            @Override
            public byte[] getBody() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("title", "A test title");
                    jsonObject.put("description", binding.description.getText().toString());
                    jsonObject.put("location", binding.location.getText().toString());
                    jsonObject.put("date", "A test date");
                    jsonObject.put("photo", getImageAsString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Log.v("JSON", jsonObject.toString());
                return jsonObject.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        queue.add(stringRequest);
    }

    private void askForLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = location -> {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                if (addressList != null && !addressList.isEmpty()) {
                    Address address = addressList.get(0);
                    currentAddress = address.getAddressLine(0);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        };
        if (ContextCompat.checkSelfPermission(
                getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},22);
        }
        else {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0,
                    locationListener);
        }
        binding.location.setText(currentAddress);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            binding.selfieholder.setImageBitmap(photo);
        } else {
            Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
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

    private String getImageAsString() {
        // give your image file url in mCurrentPhotoPath
        byte[] byteArray = new byte[0];
        try {
            Bitmap bitmap = photo;

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // In case you want to compress your image, here it's at 40%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
            byteArray = byteArrayOutputStream.toByteArray();
        } catch (NullPointerException e) {
            Log.e("There is no image", "Take a selfie");
        }

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}