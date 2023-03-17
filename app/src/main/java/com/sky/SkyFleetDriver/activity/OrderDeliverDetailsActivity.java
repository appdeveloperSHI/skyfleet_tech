package com.sky.SkyFleetDriver.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sky.SkyFleetDriver.model.completedItem;
import com.sky.skyfleettech.databinding.ActivityOrderDeliverDetailsBinding;
import com.sky.SkyFleetDriver.utils.CustPrograssbar;
import com.sky.SkyFleetDriver.utils.SessionManager;
import static com.sky.SkyFleetDriver.utils.SessionManager.currncy;

public class OrderDeliverDetailsActivity extends AppCompatActivity {
    private ActivityOrderDeliverDetailsBinding binding;


    completedItem order;
    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDeliverDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Job Completed Details");
        getSupportActionBar().setElevation(0f);
        sessionManager = new SessionManager(this);
        custPrograssbar = new CustPrograssbar();
        order = getIntent().getParcelableExtra("MyClass");

        binding.txtOrderid.setText("" + order.getBooking_id());
        binding.txtTotalprice.setText(sessionManager.getStringData(currncy) + " " + order.getTotal());

        binding.txtDistance.setText(order.getExact_trip_distance());

        binding.txtPaymode.setText("" + order.getPaymenttype());
        binding.txtName.setText("" + order.getName());
        binding.PickupAddress.setText(" " + order.getOrigin_location());
        binding.dropAddress.setText(" " + order.getDestination_location());
        binding.viaAddress.setText(" "+ order.getViapoint());
        binding.txtStatus.setText(order.getStatus());
        binding.paymentType.setText(order.getPaymenttype());
        binding.txtDate.setText(order.getPickup_date());

        byte[] decodedString;

        if (order.getSign() != null) {
            decodedString = Base64.decode(order.getSign(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            binding.imgSing.setImageBitmap(decodedByte);

        }
    }

}
