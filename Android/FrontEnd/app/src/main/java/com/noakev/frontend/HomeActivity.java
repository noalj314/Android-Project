package com.noakev.frontend;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;

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
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.nav_home) {
            Navigation.findNavController(this, R.id.fragment_container).navigate(ProfileFragmentDirections.actionProfileFragmentToHomeFragment());
            //Navigation.findNavController(this, R.id.fragment_container).navigate(PostFragmentDirections.actionPostFragmentToHomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        } else if (id == R.id.nav_profile) {
            Navigation.findNavController(this, R.id.fragment_container).navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment());
            //Navigation.findNavController(this, R.id.fragment_container).navigate(PostFragmentDirections.actionPostFragmentToProfileFragment());
            navigationView.setCheckedItem(R.id.nav_profile);
        } else if (id == R.id.nav_post) {
            Navigation.findNavController(this, R.id.fragment_container).navigate(HomeFragmentDirections.actionHomeFragmentToPostFragment());
            //Navigation.findNavController(this, R.id.fragment_container).navigate(ProfileFragmentDirections.actionProfileFragmentToPostFragment());
            navigationView.setCheckedItem(R.id.nav_post);
        } else if (id == R.id.nav_logout) {
            Toast.makeText(HomeActivity.this, "LOGGING OUT", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this, LogInActivity.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}