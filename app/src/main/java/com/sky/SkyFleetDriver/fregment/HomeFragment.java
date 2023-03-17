package com.sky.SkyFleetDriver.fregment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kofigyan.stateprogressbar.BuildConfig;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.sky.SkyFleetDriver.adopter.viaPtsAdapter;
import com.sky.SkyFleetDriver.adopter.viaPtsDialogAdapter;
import com.sky.SkyFleetDriver.model.Job;
import com.sky.SkyFleetDriver.model.homescreenModel;
import com.sky.SkyFleetDriver.model.otp;
import com.sky.SkyFleetDriver.retrofit.APIClient;
import com.sky.SkyFleetDriver.retrofit.GetResult;
import com.sky.skyfleettech.R;
import com.sky.SkyFleetDriver.activity.LoginActivity;
import com.sky.SkyFleetDriver.activity.OrderPendingDetailsActivity;
import com.sky.SkyFleetDriver.activity.paymentDetails;
import com.sky.skyfleettech.databinding.FragmentHomeBinding;
import com.sky.SkyFleetDriver.model.RestResponse;
import com.sky.SkyFleetDriver.utils.CustPrograssbar;
import com.sky.SkyFleetDriver.utils.SessionManager;
import com.sky.SkyFleetDriver.utils.Utiles;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;


public class HomeFragment extends Fragment implements GetResult.MyListener {
    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    String[] descriptionData = { "Accepted", "Dispatched","Completed"};
    List<Job> currentjob=new ArrayList<>();
    String phNum,coordinates;
    String rid,device_id;

    Job job;
    Dialog dialog;


    private FragmentHomeBinding binding;
    public  HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentHomeBinding.inflate(getLayoutInflater());
        /**
         notifyAll();
         */
        sessionManager = new SessionManager(getActivity());

        dialog=new Dialog(getActivity());

        rid=sessionManager.getRiderId("");
        device_id= Utiles.getIMEI(getActivity());
        custPrograssbar = new CustPrograssbar();

        getcurrentJob();

        binding.timeView.checkStateCompleted(true);
        binding.timeView.setStateDescriptionData(descriptionData);

        clickActions();


        return binding.getRoot();
    }

//    @Override
//    public void onBackPressed() {
//
//        if (pressedTime + 2000 > System.currentTimeMillis()) {
//            super.onBackPressed();
//            finish();
//        } else {
//            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
//        }
//        pressedTime = System.currentTimeMillis();
//    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.refresh.setOnClickListener(v-> getcurrentJob());
        checkVersion();


        binding.pickupbtn.setOnClickListener(view1 -> {
            showDialog();
            onCompleteTxtClick();

        });
    }

    private void onCompleteTxtClick() {

//        String txt = binding.pickupbtn.getText().toString();
//todo
//        if (txt.equalsIgnoreCase("Completed")) {
//            startActivity(new Intent(getActivity(), paymentDetails.class)
//                    .putExtra("jid", job.getJob_id()));
//        }

    }
    private void showDialog() {

//        String txt=binding.pickupbtn.getText().toString();
        if (job.getStatus().equals("Processing")) { //txt.equalsIgnoreCase("Pick Up"


            dialog.setContentView(R.layout.fragment_otp);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            PinView pinview = dialog.findViewById(R.id.otpPinView);
            dialog.findViewById(R.id.txt_verify).setOnClickListener(v -> {
                String pin = pinview.getText().toString();

                if (pin != null ){
                    custPrograssbar.PrograssCreate(getActivity());
                    JSONObject jsonObject = new JSONObject();
                    try {

                        jsonObject.put("rid", sessionManager.getRiderId(""));
                        jsonObject.put("device_id", sessionManager.getDeviceId("imei"));
                        jsonObject.put("jid",job.getJob_id());
                        jsonObject.put("otp", pinview.getText().toString());

                        JsonParser jsonParser = new JsonParser();
//                        Log.e("tag",  pinview.getText().toString());
                        Call<JsonObject> call = APIClient.getInterface().getotpVerification((JsonObject) jsonParser.parse(jsonObject.toString()));
                        GetResult getResult = new GetResult();
                        getResult.setMyListener(this);
                        getResult.callForLogin(call, "4");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void getcurrentJob(){
        custPrograssbar.PrograssCreate(getActivity());

        JSONObject jsonObject = new JSONObject();
        try {
            if (rid != null & device_id != null) {
                jsonObject.put("rid", rid);
                jsonObject.put("device_id", device_id);
                JsonParser jsonParser = new JsonParser();
                Call<JsonObject> call = APIClient.getInterface().gethome((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "1");
            }
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
                binding.timeView.setVisibility(View.VISIBLE);
                binding.lines.setVisibility(View.VISIBLE);
                homescreenModel modelclass = gson.fromJson(result.toString(), homescreenModel.class);

//                Log.e("tagdatasss", String.valueOf(modelclass.getResult()));
/**                binding.txtDateandstatus("Processed On" + job.getDate);*/
                if (modelclass.getResult().equalsIgnoreCase("true")) {

                    if (modelclass.getTotal_request() >= 1) {
                        binding.txtNodata.setVisibility(View.GONE);

                        currentjob = modelclass.getJobs();


//            Log.e("tagstatus", currentjob.get(0).getStatus() );
                        if(currentjob.get(0).getStatus().equalsIgnoreCase("Assigned")) {
                            binding.linearJob.setVisibility(View.VISIBLE);
                            binding.acceptRejectbtns.setVisibility(View.VISIBLE);
                            binding.btns.setVisibility(View.GONE);


                        }
//            else{
//                binding.btns.setVisibility(View.VISIBLE);
//                binding.acceptRejectbtns.setVisibility(View.GONE);
//                binding.txtNodata.setVisibility(View.VISIBLE);
//
//            }
                        showCurrentJob(currentjob);
                        binding.linearJob.setVisibility(View.VISIBLE);
                        binding.acceptRejectbtns.setVisibility(View.VISIBLE);
                        job=currentjob.get(0);
                        if (job.getStatus().equalsIgnoreCase("processing")) {
                            binding.acceptRejectbtns.setVisibility(View.GONE);
//                binding.lvlDilevry.setVisibility(View.VISIBLE);
                            binding.btns.setVisibility(View.VISIBLE);
                            binding.txtNodata.setVisibility(View.GONE);
                            binding.pickupbtn.setVisibility(View.VISIBLE);
                        }
                        if ((job.getStatus().equalsIgnoreCase("Dispatched")) || (job.getStatus().equalsIgnoreCase("Moving"))){
                            binding.acceptRejectbtns.setVisibility(View.GONE);
//                binding.lvlDilevry.setVisibility(View.VISIBLE);
                            binding.btns.setVisibility(View.VISIBLE);
//                binding.pickupbtn.setVisibility(View.GONE);


                            if (job.getWayptcurrent().equals("0")){
                                binding.positionChange1.setVisibility(View.VISIBLE);
                                binding.positionChange1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

//                           binding.positionChange1.setText("Completed");
                                        binding.positionChange1.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_round_green));


                                        startActivity(new Intent(getContext(),paymentDetails.class)
                                                .putExtra("jid",job.getJob_id()));
                                        binding.positionChange1.setVisibility(View.GONE);
                                    }
                                });
//                    binding.positionChange1.setVisibility(View.GONE);

                            }
                            else if (job.getWayptcurrent().equals("1")){

                                binding.positionChange2.setVisibility(View.VISIBLE);

                                binding.positionChange2.setOnClickListener(view -> {

                                    orderStatus("partcompleted_1");
//                         binding.positionChange2.setText("Point1 Completed");
                                    binding.positionChange2.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_round_green));

                                    binding.positionChange2.setVisibility(View.GONE);
                                });

//                    binding.positionChange2.setVisibility(View.GONE);

                            }

                            else if (job.getWayptcurrent().equals("2")){
                                binding.positionChange3.setVisibility(View.VISIBLE);

                                binding.positionChange3.setOnClickListener(view -> {

                                    orderStatus("partcompleted_2");
//                           binding.positionChange3.setText("Point2 Completed");
                                    binding.positionChange3.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_round_green));

                                    binding.positionChange3.setVisibility(View.GONE);

                                });
//                    binding.positionChange3.setVisibility(View.GONE);

                            }
                            else if (job.getWayptcurrent().equals("3")){

                                binding.positionChange4.setVisibility(View.VISIBLE);
                                binding.positionChange4.setOnClickListener(view -> {

                                    orderStatus("partcompleted_3");
//                               binding.positionChange4.setText("Point3 Completed");
                                    binding.positionChange4.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_round_green));
                                    binding.positionChange4.setVisibility(View.GONE);

                                });
//                           binding.positionChange4.setVisibility(View.GONE);

                            }

                            else if (job.getWayptcurrent().equals("4")){

                                binding.positionChange5.setVisibility(View.VISIBLE);

                                binding.positionChange5.setOnClickListener(view -> {

                                    orderStatus("partcompleted_4");
//                        binding.positionChange5.setText("Completed");
                                    binding.positionChange5.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_round_green));
                                    binding.positionChange5.setVisibility(View.GONE);


                                });
//                    binding.positionChange5.setVisibility(View.GONE);
                            }






                        }
                        binding.linearJob.setOnClickListener(v ->{
                            /**             if(job.getStatus().equalsIgnoreCase("completed")){
                             startActivity(new Intent(getActivity(), paymentDetails.class).putExtra("jid",job.getJob_id()));

                             }else {
                             }
                             startActivity(new Intent(getActivity(), OrderPendingDetailsActivity.class).putExtra("MyClass", currentjob.get(0)));
                             }

                             */
//                            changeActivity();
                        } );
//
//                        binding.viaRv.setOnClickListener(view -> {
//                            changeActivity();
//                            Intent intent=new Intent(getContext(),OrderPendingDetailsActivity.class)
//                                    .putExtra("MyClass", currentjob.get(0));
//
//                            //.putExtra("status",job.getWayptcurrent())
//
//                            startActivity(intent);
//
//                        });

                    }
                    else {
                        binding.linearJob.setVisibility(View.GONE);
                        binding.btns.setVisibility(View.GONE);
                        binding.acceptRejectbtns.setVisibility(View.GONE);
//                        binding.timeView.setVisibility(View.GONE);
                        binding.txtNodata.setVisibility(View.VISIBLE);

                    }

                    binding.currentLocatn.setText(modelclass.getLocation());
                    binding.txtVehicl.setText(modelclass.getVehicle());
                    if (modelclass.getDriver_status().equalsIgnoreCase("1")) {
                        binding.status.setText("Available");
                        binding.status.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
                    } else {
                        binding.status.setText("Offline");
                        binding.status.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                    }
                }else if(modelclass.getResult().equalsIgnoreCase("login again")) {

                    sessionManager.logoutUser();
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finish();

                }
            }
//===========================================================================================
            if (callNo.equalsIgnoreCase("5")) {
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result, RestResponse.class);
                if (response.getResult().equalsIgnoreCase("false")) {

                    dialogbox(response.getResponseMsg());
                }
            }
//=============================================================================================
            if (callNo.equalsIgnoreCase("6")) {
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result, RestResponse.class);
                Toast.makeText(getActivity(), response.getResponseMsg(), Toast.LENGTH_SHORT).show();
                if (response.getResult().equalsIgnoreCase("true")) {

                    binding.acceptRejectbtns.setVisibility(View.GONE);
                    binding.btns.setVisibility(View.VISIBLE);
                    getcurrentJob();

                } else{
                    binding.acceptRejectbtns.setVisibility(View.VISIBLE);
                    binding.btns.setVisibility(View.GONE);
                    getcurrentJob();
                }
            }
//===========================================================================================================
            if (callNo.equalsIgnoreCase("4")) {
                Gson gson = new Gson();
//                Log.e("tagotp", result.toString());
                otp response = gson.fromJson(result, otp.class);
                if (response.getVerification()) {

                    dialog.dismiss();
                    Toast.makeText(getContext(),"Otp successfully",Toast.LENGTH_SHORT).show();
                    showCurrentJob(currentjob);
                    binding.pickupbtn.setVisibility(View.GONE);
                    getcurrentJob();
//                    binding.positionChange.setVisibility(View.VISIBLE);

                    if (job.getWayptcurrent()=="0"){
                        binding.positionChange1.setVisibility(View.VISIBLE);
                        binding.positionChange1.setOnClickListener(view -> {

//                            binding.positionChange1.setText("Completed");
                            binding.positionChange1.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_round_green));

                            startActivity(new Intent(getContext(),paymentDetails.class)
                                    .putExtra("jid",job.getJob_id()));
                            binding.positionChange1.setVisibility(View.GONE);

                        });
//                        binding.positionChange1.setVisibility(View.GONE);
                    }
                    else if (job.getWayptcurrent()=="1"){
                        binding.positionChange2.setVisibility(View.VISIBLE);

                        binding.positionChange2.setOnClickListener(view -> {

                            orderStatus("partcompleted_1");
//                            binding.positionChange2.setText("Point1 Completed");
                            binding.positionChange2.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_round_green));
                            binding.positionChange2.setVisibility(View.GONE);

                        });
//                        binding.positionChange2.setVisibility(View.GONE);

                    }


                } else
                    Toast.makeText(getActivity(), "Please Enter Correct Number", Toast.LENGTH_SHORT).show();

            }
//================================================================================================================
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void changeActivity(){
        Job job=currentjob.get(0);

        if(job.getStatus().equalsIgnoreCase("completed")){

            startActivity(new Intent(getActivity(), paymentDetails.class)
                    .putExtra("jid",job.getJob_id()));


            Toast.makeText(getContext(),"ugiugficicviuhfvyig",Toast.LENGTH_SHORT).show();

        }else {
            startActivity(new Intent(getActivity(), OrderPendingDetailsActivity.class).putExtra("MyClass", currentjob.get(0)));
        }
    }


    private void showCurrentJob(List<Job> currentJob){

        Job job=currentJob.get(0);
        binding.txtOderid.setText("BookingID # " + job.getBooking_id());
//        binding.txtOderid.setText("BookingID # " + job.getJob_id());
        binding.PickupAddress.setText(job.getOrigin_location());
        binding.dropAddress.setText(job.getDestination_location());
        binding.txtPricetotla.setText(job.getTotal_price());
        binding.txtType.setText(job.getPaymenttype());
        binding.txtDateandstatus.setText("Schedule On "+ job.getSchedule_date());
        binding.kms.setText(job.getTotal_distance()+"Km");
        phNum=job.getCustomermobile();
        binding.pick.setText("PickUp:");
        binding.drop.setText("Drop   :");
        if(job.getStatus().equalsIgnoreCase("processing")){
            binding.timeView.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
            coordinates=job.getOrigin_location();


        }else if(job.getStatus().equalsIgnoreCase("dispatched")){
            binding.timeView.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
            coordinates=job.getDestination_location();

        }else if(job.getStatus().equalsIgnoreCase("completed")){
            binding.timeView.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
            coordinates=job.getDestination_location();//todo have to check this line


        }else{
            binding.timeView.setCurrentStateDescriptionColor(R.color.colorPrimary);

        }


        if(Objects.requireNonNull(job.getWaypts()).size() >1){//!= null
//                for (int i=0; i< job.getWaypts().size() ;i++){
//
//                }
//
//                List<WayptX> waypts=job.getWaypts().remove(job.getWaypts().size());
            viaPtsAdapter adapter=new viaPtsAdapter(job.getWaypts(),getContext(),job,true);
            binding.viaRv.setAdapter(adapter);
        }

    }


    private void clickActions(){

        binding.call.setOnClickListener(v-> {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phNum));
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {


                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);

            }
            startActivity(intent);
        });

        binding.navigate.setOnClickListener(v->{
            if(job.getStatus().equalsIgnoreCase("processing")) {
                getLocationFromAddress(currentjob.get(0).getOrigin_location());
            }else
                showDialogForVia();

//            getLocationFromAddress(currentjob.get(0).getDestination_location());

//        getLocationFromAddress(); //todo
        });

        binding.accept.setOnClickListener(v ->     {
            orderStatus("accept");
            binding.acceptRejectbtns.setVisibility(View.GONE);
            binding.btns.setVisibility(View.VISIBLE);
//            binding.lvlDilevry.setVisibility(View.VISIBLE);
            binding.pickupbtn.setVisibility(View.VISIBLE);
        });
        binding.reject.setOnClickListener(v ->    {
            orderStatus("reject");
            binding.txtNodata.setVisibility(View.VISIBLE);

            binding.acceptRejectbtns.setVisibility(View.GONE);
            binding.btns.setVisibility(View.GONE);
        });


    }



    public void getLocationFromAddress(String coordinates) {
//        if(job.getStatus().equalsIgnoreCase("processing")) {
        Geocoder coder = new Geocoder(getContext());
        List<Address> address;

        try {
            address = coder.getFromLocationName(coordinates, 5);
            /**       if (address == null) {
             return false;
             }

             */
            Address location = address.get(0);
//            Log.e("TAGlocation", String.valueOf(location));
            location.getLatitude();
            location.getLongitude();
//            String url = "http://maps.google.com/maps?daddr="+address;
            String url = "http://maps.google.com/maps?daddr=" + location.getLatitude() + "," + location.getLongitude();
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);

//            String geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + mTitle + ")";
            /**
             String uri = String.format(Locale.ENGLISH, "geo:%f,%f", location.getLatitude(), location.getLongitude());
             Log.e("taglatlong",  location.getLatitude() + " // " + location.getLongitude() );
             Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
             startActivity(intent);
             */
//                return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
//        }else{
//            showDialogForVia();
//        }
//        return true;
    }

    private void showDialogForVia(){

        dialog=new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_via_pts);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager. LayoutParams.WRAP_CONTENT);

        dialog.show();
        TextView destintn=dialog.findViewById(R.id.destination);
        destintn.setText("Drop Point : "+currentjob.get(0).getDestination_location());
        destintn.setOnClickListener(v-> {
            coordinates=currentjob.get(0).getDestination_location();
            getLocationFromAddress(coordinates);
        });

        RecyclerView rv=dialog.findViewById(R.id.via_rv);
        viaPtsDialogAdapter adapter=new viaPtsDialogAdapter(Objects.requireNonNull(currentjob.get(0).getWaypts()),getContext());
        rv.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        getcurrentJob();
//      Log.e("tagresume", "onResume: " );
    }


    private void checkVersion() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("version", BuildConfig.VERSION_NAME);


            JsonParser jsonParser = new JsonParser();

            Call<JsonObject> call = APIClient.getInterface().checkAppversion((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "5");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void dialogbox(String url){

        final androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Skyfleet");
        builder.setIcon(R.drawable.red_circle_logo);
        builder.setMessage("You are using old version please update");

        builder.setPositiveButton("Update",
                (dialog, which) ->
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))));

        builder.setCancelable(false);
        builder.show();
    }


    private void orderStatus(String status) {
        custPrograssbar.PrograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rid", sessionManager.getRiderId(""));
//            jsonObject.put("vid", "8");
            jsonObject.put("device_id", sessionManager.getDeviceId("imei"));
            jsonObject.put("jid", currentjob.get(0).getJob_id());
            jsonObject.put("status", status);

            JsonParser jsonParser = new JsonParser();

            Call<JsonObject> call = APIClient.getInterface().getOstatus((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "6");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}

