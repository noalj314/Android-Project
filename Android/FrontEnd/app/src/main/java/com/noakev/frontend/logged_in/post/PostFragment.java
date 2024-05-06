package com.noakev.frontend.logged_in.post;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.noakev.frontend.databinding.FragmentPostBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 */
public class PostFragment extends Fragment {
    private FragmentPostBinding binding;
    private Button locationBtn;
    private Button postBtn;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView title;
    private TextView password;
    private String currentAddress = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPostBinding.inflate(getLayoutInflater(), container, false);

        title = binding.usernametext;
        password = binding.passwordtext;
        locationBtn = binding.locationbtn;
        postBtn = binding.postbtn;

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