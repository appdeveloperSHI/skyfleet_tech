package com.sky.SkyFleetDriver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sky.SkyFleetDriver.MyApplication;
import com.sky.skyfleettech.databinding.ActivityInfoBinding;
import com.sky.SkyFleetDriver.utils.SessionManager;


public class InfoActivity extends AppCompatActivity {
    private ActivityInfoBinding binding;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().hide();
        initFirebase();
        sessionManager = new SessionManager(this);

        if (sessionManager.getBooleanData("rlogin")) {
            startActivity(new Intent(InfoActivity.this, HomeActivity.class));
            finish();
        }
        binding.btnLogin.setOnClickListener(v ->{
            startActivity(new Intent(InfoActivity.this, LoginActivity.class));
            finish();
        });
    }


    public void initFirebase(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    ((MyApplication)getApplication()).setFirebaseAppId(token);
                });
    }
}
