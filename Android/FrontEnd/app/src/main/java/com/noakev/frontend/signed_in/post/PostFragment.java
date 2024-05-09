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
import android.widget.Toast;

import com.noakev.frontend.databinding.FragmentPostBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 */
public class PostFragment extends Fragment {
    private static final int REQUEST_CODE = 22;
    private FragmentPostBinding binding;
    private Button postBtn;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String currentAddress;
    private Bitmap photo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPostBinding.inflate(getLayoutInflater(), container, false);

        Button selfieBtn = binding.selfiebutton;
        Button locationBtn = binding.locationbtn;
        Button postBtn = binding.postbtn;

        // Logik för att hämta användare
        // binding.publisher.setText(getCurrentUser);
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
            //Post post = new Post(String.valueOf(binding.description.getText()), currentAddress, getImageAsString());

            Toast.makeText(getContext(), "Creating post...", Toast.LENGTH_SHORT).show();
            // Save to database
        } else {
            Toast.makeText(getContext(), "Insufficient information!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean allColumnsAreFilled() {
        if (String.valueOf(binding.description.getText()).isEmpty()) {
            Log.v("a", "a");
            return false;
        } else if (String.valueOf(binding.location.getText()).isEmpty()) {
            Log.v("b", "b");
            return false;
        } else if (getImageAsString().isEmpty()) {
            Log.v("c", "c");
            return false;
        }
        return true;
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