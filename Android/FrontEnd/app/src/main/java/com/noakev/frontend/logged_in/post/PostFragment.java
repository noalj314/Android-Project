package com.noakev.frontend.logged_in.post;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.noakev.frontend.databinding.FragmentPostBinding;

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
        postBtn = binding.postbtn;

        //binding.publisher.setText(getCurrentUser);
        selfieBtn.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_CODE);

        });
        locationBtn.setOnClickListener(v -> askForLocation());
        //postBtn.setOnClickListener(v -> createNewPost());

        return binding.getRoot();
    }

    private void createNewPost() {
        //Post post = new Post(title.getText(), password.getText(), currentAddress);
    }


    private void askForLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addressList != null && !addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        currentAddress = address.getAddressLine(0);
                        binding.location.setText(currentAddress);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
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


}