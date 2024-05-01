package com.noakev.frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private MenuItem homeItem;
    private MenuItem aboutItem;
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

        if (savedInstanceState == null) {
            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.nav_home) {
            Navigation.findNavController(this, R.id.fragment_container).navigate(ProfileFragmentDirections.actionProfileFragmentToHomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
            //NavDirections action2 = ProfileFragmentDirections.actionProfileFragmentToHomeFragment();
            //Navigation.findNavController(this, R.id.fragment_container);//navigate(action2);
        }
        else if (id == R.id.nav_profile) {
            Navigation.findNavController(this, R.id.fragment_container).navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment());
            navigationView.setCheckedItem(R.id.nav_profile);
            //NavDirections action = HomeFragmentDirections.actionHomeFragmentToProfileFragment();
            //Navigation.findNavController(this, R.id.fragment_container).navigate(action);
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


    public void navigateHome() {
        Navigation.findNavController(this, R.id.fragment_container).navigate(ProfileFragmentDirections.actionProfileFragmentToHomeFragment());
        //NavDirections action = ProfileFragmentDirections.actionProfileFragmentToHomeFragment();
        //Navigation.findNavController(this, R.id.container).navigate(action);
        //Navigation.findNavController(v).navigate(action);
    }


    public void navigateToProfile(View v) {
        Navigation.findNavController(v).navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment());
        //NavDirections action = HomeFragmentDirections.actionHomeFragmentToProfileFragment();
        //Navigation.findNavController(this, R.id.fragment_container).navigate(action);
    }

}