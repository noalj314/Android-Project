package com.noakev.frontend.signed_in.event;

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
import com.noakev.frontend.backend.BackEndCommunicator;
import com.noakev.frontend.backend.ResponseListener;
import com.noakev.frontend.databinding.FragmentEventBinding;
import com.noakev.frontend.signed_in.HomeActivity;
import com.noakev.frontend.signed_out.SignInFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

/**
 * The fragment for creating new events.
 */
public class EventFragment extends Fragment {
    private final String currentUser = GlobalUser.getUsername();
    private static final int REQUEST_CODE = 22;
    private FragmentEventBinding binding;
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
        binding = FragmentEventBinding.inflate(getLayoutInflater(), container, false);

        TextView username = binding.publisher;
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
            saveEventToBackend();
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

    private void saveEventToBackend() {
        BackEndCommunicator communicator = new BackEndCommunicator();
        communicator.sendRequest(1, "/event/create", createBody(), getContext(), new ResponseListener() {
            @Override
            public void onSucces(APIObject apiObject) {
                Toast.makeText(getContext(), "Successfully created post.", Toast.LENGTH_SHORT).show();
                HomeActivity homeActivity = (HomeActivity)(getActivity());
                homeActivity.navigateHome();
            }

            @Override
            public void onError(APIObject apiObject) {}
        });
    }

    private byte[] createBody() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", currentUser);
            jsonObject.put("location", binding.location.getText().toString());
            jsonObject.put("description", binding.description.getText().toString());
            jsonObject.put("photo", getImageAsString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Log.v("JSON", jsonObject.toString());
        return jsonObject.toString().getBytes();
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

    /**
     * Check if current user took an image.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
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

    /**
     * Check if the current user accepted gps tracking.
     *
     * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
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