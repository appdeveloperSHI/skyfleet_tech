package com.sky.SkyFleetDriver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.sky.SkyFleetDriver.fregment.DeliveryFragment;
import com.sky.SkyFleetDriver.fregment.HomeFragment;
import com.sky.SkyFleetDriver.fregment.NotificationFragment;
import com.sky.SkyFleetDriver.fregment.PendingFragment;
import com.sky.SkyFleetDriver.fregment.ProfileFragment;
import com.sky.skyfleettech.R;
import com.sky.skyfleettech.databinding.ActivityHomeBinding;
import com.sky.SkyFleetDriver.utils.CustPrograssbar;



public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener
        , OSSubscriptionObserver  {

    private ActivityHomeBinding binding;

    FragmentManager fragmentManager;
    Fragment currentFragment;
    int n=1;
    CustPrograssbar custPrograssbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(getLayoutInflater().inflate(R.layout.toolbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );



        binding.bottomNavigation.setOnNavigationItemSelectedListener(this);
        binding.bottomNavigation.setSelectedItemId(R.id.navigation_home);
        custPrograssbar = new CustPrograssbar();

    }



    public boolean callFragment(Fragment fragmentClass) {
        fragmentManager = getSupportFragmentManager();
        currentFragment = fragmentManager.findFragmentById(R.id.fragment_frame);
        if (n ==1) {
            if (fragmentClass != null) {
                n=2;
                fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragmentClass).commit();//.addToBackStack(null)
                return true;
            }
        }else if(!fragmentClass.getClass().toString().equals(currentFragment.getClass().toString())) {
            fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragmentClass).commit();
            return true;

        }
        return false;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {

            case R.id.navigation_home:
                fragment = new HomeFragment();
                break;

            case R.id.navigation_pendding:
                fragment = new PendingFragment();
                break;

            case R.id.navigation_delivery:
                fragment = new DeliveryFragment();
                break;

            case R.id.navigation_notifications:
                fragment = new NotificationFragment();
                break;

            case R.id.navigation_profile:
                fragment = new ProfileFragment();
                break;
            default:
                break;
        }
        return callFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }




    public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
//        if (!stateChanges.getFrom().isSubscribed() &&
//                stateChanges.getTo().isSubscribed()) {
//            new AlertDialog.Builder(this)
//                    .setMessage("You've successfully subscribed to push notifications!")
//                    .show();
//            // get player ID
//            stateChanges.getTo().getUserId();
//        }
//        Log.i("Debug", "onOSPermissionChanged: " + stateChanges);
    }

    public void walletclick(View view) {
        startActivity(new Intent(this,mywalletActivity.class));
    }


}
