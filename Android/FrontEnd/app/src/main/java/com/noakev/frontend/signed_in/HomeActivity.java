package com.noakev.frontend.signed_in;

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

import com.google.android.material.navigation.NavigationView;
import com.noakev.frontend.R;
import com.noakev.frontend.signed_in.comment.CommentFragmentDirections;
import com.noakev.frontend.signed_in.event.Adapter;
import com.noakev.frontend.signed_in.event.EventFragmentDirections;
import com.noakev.frontend.signed_in.home.HomeFragment;
import com.noakev.frontend.signed_in.home.HomeFragmentDirections;
import com.noakev.frontend.signed_in.profile.ProfileFragmentDirections;
import com.noakev.frontend.signed_in.search.SearchFragmentDirections;

/**
 * The activity for being signed in.
 */
public class HomeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.fragment_container);
        NavigationUI.setupWithNavController(navigationView, navController);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setCheckedItem(R.id.homeFragment);
    }

    /**
     * When the drawer is open and the user wants to close it.
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Different navigations in HomeActivity. These are used outside of the drawer,
     * I.e. when a button is pressed.
     */

    public void navigateHome() {
        NavDirections action = EventFragmentDirections.actionEventFragmentToHomeFragment();
        Navigation.findNavController(this, R.id.fragment_container).navigate(action);
    }

    public void navigateHomeFromComment() {
        NavDirections action = CommentFragmentDirections.actionCommentFragmentToHomeFragment();
        Navigation.findNavController(this, R.id.fragment_container).navigate(action);
    }

    public void navigateToProfile(String username) {
        NavDirections action = SearchFragmentDirections.actionSearchFragmentToProfileFragment(username);
        Navigation.findNavController(this, R.id.fragment_container).navigate(action);
    }

   public void navigateToProfileFromComment(String username) {
        NavDirections action = CommentFragmentDirections.actionCommentFragmentToProfileFragment(username);
        Navigation.findNavController(this, R.id.fragment_container).navigate(action);
    }

    public void navigateToSelf(String username) {
        NavDirections action = ProfileFragmentDirections.actionProfileFragmentSelf(username);
        Navigation.findNavController(this, R.id.fragment_container).navigate(action);
    }

    public void navigateToComment(String eventID) {
        NavDirections action = HomeFragmentDirections.actionHomeFragmentToCommentFragment(eventID);
        Navigation.findNavController(this, R.id.fragment_container).navigate(action);
    }
}