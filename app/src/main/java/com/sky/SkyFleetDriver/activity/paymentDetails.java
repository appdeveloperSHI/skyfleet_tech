package com.sky.SkyFleetDriver.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sky.SkyFleetDriver.model.paymentModel;
import com.sky.SkyFleetDriver.retrofit.APIClient;
import com.sky.SkyFleetDriver.retrofit.GetResult;
import com.sky.skyfleettech.R;
import com.sky.skyfleettech.databinding.ActivityPaymentDetailsBinding;
import com.sky.SkyFleetDriver.model.RestResponse;
import com.sky.SkyFleetDriver.utils.CustPrograssbar;
import com.sky.SkyFleetDriver.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

public class paymentDetails extends AppCompatActivity implements GetResult.MyListener {
    private ActivityPaymentDetailsBinding binding;
    String jid;

    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;
    paymentModel response;
    String remark="Good";
    String amountReceived="";
//    bottomsheet bSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Payment Details");
        getSupportActionBar().setElevation(0f);

        sessionManager = new SessionManager(this);
        custPrograssbar = new CustPrograssbar();
        Intent intent = getIntent();
        jid = intent.getStringExtra("jid");
        payments();
        binding.report.setOnClickListener(v ->{

            View view = getLayoutInflater().inflate(R.layout.ed_amount_popup, null);
            BottomSheetDialog dialog = new BottomSheetDialog(this);
            dialog.setContentView(view);
            dialog.show();

            ConstraintLayout verify=  view.findViewById(R.id.bs_amount_received);
            EditText amount=view.findViewById(R.id.ed_amount);
            EditText remarks=view.findViewById(R.id.ed_remark);
            verify.setOnClickListener(v1 ->
            {
                if (verified(amount,remarks)) {
                    dialog.dismiss();
                    orderStatus("paid");

                }
            });

            /**

             bSheet =
             bottomsheet.newInstance();
             bSheet.show(getSupportFragmentManager(),
             "fragment");


             ConstraintLayout verify=  bSheet.getView().findViewById(R.id.bs_amount_received);
             verify.setOnClickListener(v1 ->
             {
             if (bSheet.verified()){
             //                    EditText ed_amount=bSheet.getView().findViewById(R.id.ed_amount);
             //                    EditText ed_remark=bSheet.getView().findViewById(R.id.ed_remark);
             //                    remark=ed_remark.getText().toString();
             //                    amountReceived=ed_amount.getText().toString();
             //                    orderStatus("paid");
             Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
             }
             */
//            });

        });
        binding.amountReceived.setOnClickListener(v ->
        {
            orderStatus("completed");
        });


        binding.scrol.postDelayed(new Runnable() {
            @Override
            public void run() {
                View lastChild = binding.scrol.getChildAt(binding.scrol.getChildCount() - 1);
                int bottom = lastChild.getBottom() + binding.scrol.getPaddingBottom();
                int sy = binding.scrol.getScrollY();
                int sh = binding.scrol.getHeight();
                int delta = bottom - (sy + sh);

                binding.scrol.smoothScrollBy(0, delta);
            }
        },2000);
    }


    public boolean verified(EditText amount,EditText remarks){
        if (!amount.getText().toString().isEmpty() && !remarks.getText().toString().isEmpty()) {
            /**
             amountReceived=amount.getText().toString();
             */
            remark=remarks.getText().toString();
            return true;
        }else {
            amount.setError("Enter Amount");
            remarks.setError("Enter Remark");
            return false;

        }
    }


    private void payments() {
        custPrograssbar.PrograssCreate(this);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("rid", sessionManager.getRiderId(""));
            jsonObject.put("jid", jid);
            jsonObject.put("device_id", sessionManager.getDeviceId("imei"));
            jsonObject.put("status", "completed");
            JsonParser jsonParser = new JsonParser();

            Call<JsonObject> call = APIClient.getInterface().getPaymentDetails((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.ClosePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {


                Gson gson = new Gson();
                response = gson.fromJson(result, paymentModel.class);
                if (response.getResult().equalsIgnoreCase("true")) {

                    binding.vehicle.setText(response.getPayment().getVehicle());
                    binding.totalDistance.setText(response.getPayment().getTotal_distance());
                    binding.totalTime.setText(response.getPayment().getTotal_time());

                    binding.pickupAddress.setText(response.getPayment().getOrigin_location());
                    binding.dropAddress.setText(response.getPayment().getDestination_location());
                    binding.basefairRs.setText("\u20B9" + response.getPayment().getBase_fair());
                    binding.extraDistanceRs.setText("\u20B9" +response.getPayment().getExtra_distance_crg());
                    binding.extraTimeRs.setText("\u20B9" +response.getPayment().getWaiting_charge());
                    binding.orderId.setText("#" +response.getPayment().getBooking_id());
                    amountReceived=response.getPayment().getTotal_fare();

                    binding.totalRs.setText("\u20B9" +amountReceived);
                    binding.amount.setText("\u20B9" +amountReceived);


                    binding.extraDistance.setText("Extra distance Charge For"
                            +"("+ response.getPayment().getExtra_distance_travelled()+ ")");
                    binding.extraTim.setText("Extra Time Charge For"+"("+ response.getPayment().getWaiting_time()+")");

                    binding.baseFair.setText("Base Fair (" +response.getPayment().getMinimum_fare()+")");
                    binding.date.setText(response.getPayment().getDate());


                    /**
                     *   response.getPayment().getExtra_distance_travelled();
                     *                   response.getPayment().getWaiting_charge();
                     *
                     */

                }
            }else if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result, RestResponse.class);
                if (response.getResult().equalsIgnoreCase("true")) {
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void orderStatus(String status) {
        custPrograssbar.PrograssCreate(this);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rid", sessionManager.getRiderId(""));
            jsonObject.put("device_id", sessionManager.getDeviceId("imei"));
            jsonObject.put("jid", jid);
            jsonObject.put("status", status);
            jsonObject.put("remark", remark);
            jsonObject.put("amount", amountReceived);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getOstatus((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "2");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

