package com.sky.SkyFleetDriver.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaos.view.PinView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sky.SkyFleetDriver.adopter.viaPtsAdapter;
import com.sky.SkyFleetDriver.adopter.viaPtsDialogAdapter;
import com.sky.SkyFleetDriver.fregment.EstimateMapsFragment;
import com.sky.SkyFleetDriver.model.Job;
import com.sky.SkyFleetDriver.model.otp;
import com.sky.SkyFleetDriver.retrofit.APIClient;
import com.sky.SkyFleetDriver.retrofit.GetResult;
import com.sky.skyfleettech.R;
import com.sky.skyfleettech.databinding.ActivityOrderDetailsBinding;
import com.sky.SkyFleetDriver.model.RestResponse;
import com.sky.SkyFleetDriver.utils.CustPrograssbar;
import com.sky.SkyFleetDriver.utils.SessionManager;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import static com.sky.SkyFleetDriver.utils.SessionManager.currncy;


public class OrderPendingDetailsActivity extends AppCompatActivity implements
        RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener, GetResult.MyListener {

    private ActivityOrderDetailsBinding binding;

    private RapidFloatingActionHelper rfabHelper;

    String jobStatus="";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            case R.id.navigation_call:
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + order.getCustomermobile()));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);

                    return true;
                }
                startActivity(intent);
                return true;
            case R.id.navigation_map:
                if (jobStatus.equalsIgnoreCase("processing") ||jobStatus.equalsIgnoreCase("pending"))
                    getLocationFromAddress(order.getOrigin_location());
                else
                    showDialogForVia();
//                Single.fromCallable(() -> order.getOrigin_location())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        this::getLocationFromAddress);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    SessionManager sessionManager;
    private Dialog dialog ;

    CustPrograssbar custPrograssbar;

    Job order;

    ArrayList<String> arrayMidpts=new ArrayList();

//    User user;

    //TODO problem in getting order ->
    // from hoem page t0 pending job details 391

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        dialog = new Dialog(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Job Details");
        getSupportActionBar().setElevation(0f);
        requestPermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        sessionManager = new SessionManager(this);
//      user = sessionManager.getUserDetails("");
        custPrograssbar = new CustPrograssbar();
        order= getIntent().getParcelableExtra("MyClass");
//  order=getIntent().getParcelableExtra("status");
        jobStatus=order.getStatus();
//  waypointstatus=order.getWayptcurrent();

        mapfrag();

//        Single.fromCallable(new Callable<Object>() {
//            @Override
//            public Object call() throws Exception {
//                return loadInBackground();
//            }
//        });
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Object>() {
//                    @Override
//                    public void accept(Object resultObject) throws Exception {
//                        updateUi(resultObject);
//                    }
//                });

        binding.txtOrderid.setText("" + order.getBooking_id());
        binding.txtTotal.setText(sessionManager.getStringData(currncy) + " " + order.getTotal_price());
        binding.txtStatus.setText("" + order.getStatus());
//        binding.currency.setText(R.string.currency); //todo
        binding.txtName.setText("" + order.getCustomername());
        binding.txtPaymode.setText("" + order.getPaymenttype());
        binding.txtDistance.setText(" "+ order.getTotal_distance());
        binding.dropAddress.setText(" " + order.getDestination_location());
        binding.PickupAddress.setText(" " + order.getOrigin_location());
        binding.txtTime.setText(order.getSchedule_time());




        if(!order.getWaypts().isEmpty()){//todo

            viaPtsAdapter adapter=new viaPtsAdapter(order.getWaypts(),getApplicationContext(),order,false);
            binding.viaRv.setAdapter(adapter);
        }

        if (order.getStatus().equalsIgnoreCase("processing")) {
            binding.lvlAcceptReject.setVisibility(View.GONE);
            binding.activityMainRfal.setVisibility(View.VISIBLE);
            binding.lvlDilevry.setVisibility(View.VISIBLE);
            showDialog();
            binding.txtStatus.setBackground(getResources().getDrawable(R.drawable.pending_round_yell));
        }
        setFloting();

        if (order.getStatus().equalsIgnoreCase("Dispatched")){
            binding.lvlAcceptReject.setVisibility(View.GONE);
            binding.activityMainRfal.setVisibility(View.VISIBLE);
            binding.lvlDilevry.setVisibility(View.VISIBLE);


            if (order.getWayptcurrent().equals("0")){
                binding.positionChange1.setVisibility(View.VISIBLE);
                binding.positionChange1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                           binding.positionChange1.setText("Completed");
//                        binding.positionChange1.setBackground(ContextCompat.getDrawable(this, R.drawable.button_round_green));


                        startActivity(new Intent(OrderPendingDetailsActivity.this,paymentDetails.class)
                                .putExtra("jid",order.getJob_id()));
                        binding.positionChange1.setVisibility(View.GONE);
                    }
                });


            }
            else if (order.getWayptcurrent().equals("1")){

                binding.positionChange2.setVisibility(View.VISIBLE);

                binding.positionChange2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderStatus("partcompleted_1");
//                         binding.positionChange2.setText("Point1 Completed");
                        binding.positionChange2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_round_green));
                        binding.positionChange2.setVisibility(View.GONE);


                    }
                });


            }

            else if (order.getWayptcurrent().equals("2")){
                binding.positionChange3.setVisibility(View.VISIBLE);

                binding.positionChange3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderStatus("partcompleted_2");
//                           binding.positionChange3.setText("Point2 Completed");
                        binding.positionChange3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_round_green));

                        binding.positionChange3.setVisibility(View.GONE);


                    }
                });

            }
            else if (order.getWayptcurrent().equals("3")){

                binding.positionChange4.setVisibility(View.VISIBLE);
                binding.positionChange4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderStatus("partcompleted_3");
//                               binding.positionChange4.setText("Point3 Completed");
                        binding.positionChange4.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_round_green));
                        binding.positionChange4.setVisibility(View.GONE);

                    }
                });
            }

            else if (order.getWayptcurrent().equals("4")){

                binding.positionChange5.setVisibility(View.VISIBLE);

                binding.positionChange5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderStatus("partcompleted_4");
//                        binding.positionChange5.setText("Completed");
                        binding.positionChange5.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_round_green));
                        binding.positionChange5.setVisibility(View.GONE);

                    }
                });

            }
        }
        binding.txtStarted.setOnClickListener(v ->
        {
            showDialog();
//       onCompleteTxtClick();
        });

        binding.txtAccept.setOnClickListener(v ->      orderStatus("accept"));
        binding.txtReject.setOnClickListener(v ->      orderStatus("reject"));



    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setFloting() {

        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(this);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Issue with an ongoing order")
                .setResId(R.drawable.ic_clear_white)
                .setIconNormalColor(Color.parseColor("#C81507"))
                .setIconPressedColor(R.color.colorred)
                .setLabelColor(Color.WHITE)
                .setLabelBackgroundDrawable(getDrawable(R.drawable.button_round))
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Payment issue with my order")
                .setResId(R.drawable.ic_clear_white)
                .setIconNormalColor(Color.parseColor("#C81507"))
                .setIconPressedColor(R.color.colorred)
                .setLabelColor(Color.WHITE)
                .setLabelBackgroundDrawable(getDrawable(R.drawable.button_round))
                .setWrapper(1)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Address wrong ")
                .setResId(R.drawable.ic_clear_white)
                .setIconNormalColor(Color.parseColor("#C81507"))
                .setIconPressedColor(R.color.colorred)
                .setLabelColor(Color.WHITE)
                .setLabelBackgroundDrawable(getDrawable(R.drawable.button_round))
                .setWrapper(2)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Other")
                .setResId(R.drawable.ic_clear_white)
                .setIconNormalColor(Color.parseColor("#C81507"))
                .setIconPressedColor(R.color.colorred)
                .setLabelColor(Color.WHITE)
                .setLabelBackgroundDrawable(getDrawable(R.drawable.button_round))
                .setWrapper(3)
        );
        rfaContent
                .setItems(items)
                .setIconShadowRadius(RFABTextUtil.dip2px(this, 2))
                .setIconShadowColor(R.color.colorred)
                .setIconShadowDy(RFABTextUtil.dip2px(this, 1));
        rfabHelper = new RapidFloatingActionHelper(
                this,
                binding.activityMainRfal,
                binding.activityMainRfab,
                rfaContent
        ).build();
    }

    private Object mapfrag(){

        FragmentManager  fragmentManager = getSupportFragmentManager();

        for(int i=0;i<order.getWaypts().size();i++){
            arrayMidpts.add(order.getWaypts().get(i).getAddress());

        }



        Bundle bundle = new Bundle();
        String pickup = order.getOrigin_location();
        String dropoff = order.getDestination_location();
        bundle.putString("pickup", pickup );
        bundle.putString("dropoff", dropoff );
        bundle.putStringArrayList("waypts",arrayMidpts);
        EstimateMapsFragment mapfrag= new EstimateMapsFragment();
        mapfrag.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.rel_lay, mapfrag).addToBackStack(null).commit();
        return null;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.order, menu);
        return true;
    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {

        orderCencel(item.getLabel());
        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        orderCencel(item.getLabel());
        rfabHelper.toggleContent();
    }

    private boolean openMap(String coordinates){
        if(coordinates == null)
            return false;

        String[] latLng = coordinates.trim().split(",");
        if ((latLng.length != 2))
            return false;
        try {
//            Log.e("TAGopenmap", coordinates );
            String label = "Customer Location";
            String uriBegin = "geo:" + latLng[0] + "," + latLng[1];
            String query = latLng[0] + "," + latLng[1] + "(" + label + ")";
            String encodedQuery = Uri.encode(query);
            String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
            Uri uri = Uri.parse(uriString);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private void showDialogForVia(){

        dialog=new Dialog(this);
        dialog.setContentView(R.layout.dialog_via_pts);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager. LayoutParams.WRAP_CONTENT);

        dialog.show();
        TextView destintn=dialog.findViewById(R.id.destination);
        destintn.setText("Drop point :" +order.getDestination_location());
        destintn.setOnClickListener(v-> {
//            coordinates=currentjob.get(0).getDestination_location();
            getLocationFromAddress(order.getDestination_location());
        });

        RecyclerView rv=dialog.findViewById(R.id.via_rv);
        viaPtsDialogAdapter adapter=new viaPtsDialogAdapter(Objects.requireNonNull(order.getWaypts()),this);
        rv.setAdapter(adapter);

    }

    public boolean getLocationFromAddress(String coordinates) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(coordinates, 5);
            if (address == null) {
                return false;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            String url = "http://maps.google.com/maps?daddr="+location.getLatitude()+ "," +location.getLongitude();
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,  Uri.parse(url));
            startActivity(intent);


            /**
             String uri = String.format(Locale.ENGLISH, "geo:%f,%f", location.getLatitude(), location.getLongitude());
             Log.e("taglatlong",  location.getLatitude() + " // " + location.getLongitude() );
             Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
             startActivity(intent);
             */
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void orderStatus(String status) {
        custPrograssbar.PrograssCreate(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rid", sessionManager.getRiderId(""));
//            jsonObject.put("vid", "8");
            jsonObject.put("device_id", sessionManager.getDeviceId("imei"));
            jsonObject.put("jid", order.getJob_id());
            jsonObject.put("status", status);

            JsonParser jsonParser = new JsonParser();

            Call<JsonObject> call = APIClient.getInterface().getOstatus((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void orderCencel(String comment) {
        custPrograssbar.PrograssCreate(this);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("rid", sessionManager.getRiderId(""));
//            jsonObject.put("vid", "8");
            jsonObject.put("device_id", sessionManager.getDeviceId("imei"));
            jsonObject.put("jid", order.getJob_id());
            jsonObject.put("status", "cancle");
            jsonObject.put("comment", comment);
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getOstatus((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "2");

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
                RestResponse response = gson.fromJson(result, RestResponse.class);
                Toast.makeText(this, response.getResponseMsg(), Toast.LENGTH_SHORT).show();
                if (response.getResult().equalsIgnoreCase("true")) {

                    binding.txtStatus.setText("processing");
                    jobStatus="processing";
//                    waypointstatus="processing";
//                  binding.txtStatus.setText(order.getStatus());
                    binding.lvlAcceptReject.setVisibility(View.GONE);
                    binding.activityMainRfal.setVisibility(View.VISIBLE);
                    binding.lvlDilevry.setVisibility(View.VISIBLE);
                    binding.txtStatus.setBackground(getResources().getDrawable(R.drawable.pending_round_yell));

                    listener.onClickItem("processing", order);
                } else {
                    finish();
                    listener.onClickItem("reject", order);
                    //TODO check listener cause sometime reject problem
                }

            } else if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result, RestResponse.class);
                Toast.makeText(this, response.getResponseMsg(), Toast.LENGTH_SHORT).show();
                if (response.getResult().equalsIgnoreCase("true")) {
                    listener.onClickItem("reject", order);
                    finish();
                }

            }else if (callNo.equalsIgnoreCase("3")){
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result, RestResponse.class);
                Toast.makeText(this, response.getResponseMsg(), Toast.LENGTH_SHORT).show();
                if (response.getResult().equalsIgnoreCase("true")) {
                    startActivity(new Intent(this,paymentDetails.class));
                    finish();
                }

            } else if (callNo.equalsIgnoreCase("4")){
                Gson gson = new Gson();
//                Log.e("tagotp", result.toString() );
                otp response = gson.fromJson(result, otp.class);
                if (response.getVerification()) {
//                    orderStatus("dispatched");
                    dialog.dismiss();

                    Toast.makeText(getApplicationContext(),"Otp successfully",Toast.LENGTH_SHORT).show();
                    if (order.getWayptcurrent()=="0"){

                        binding.txtStarted.setText("Completed");
                        binding.txtStarted.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_round_green));

                    }else {

                        binding.txtStarted.setText("WaypointCompleted1");
                        binding.txtStarted.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_round_green));
                    }

                    binding.txtStatus.setText("Dispatched");
                    jobStatus="Dispatched";
//                    waypointstatus="Dispatched";
                    binding.txtStatus.setBackground(getResources().getDrawable(R.drawable.pending_round));

//            binding.txtStarted.setText("Completed");
//            binding.txtStarted.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_round_green));

                }else
                    Toast.makeText(this, "Please Enter Correct Number", Toast.LENGTH_SHORT).show();

            }else if (callNo.equalsIgnoreCase("6")) {
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result, RestResponse.class);
                Toast.makeText(this, response.getResponseMsg(), Toast.LENGTH_SHORT).show();
                if (response.getResult().equalsIgnoreCase("true")) {
                    startActivity(new Intent(this, paymentDetails.class));
                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showDialog() {
        String txt=binding.txtStarted.getText().toString();
        if (order.getStatus().equals("Processing")) { //txt.equalsIgnoreCase("Pick Up"

/**
 bottomsheet bSheet =
 bottomsheet.newInstance();
 bSheet.show(getSupportFragmentManager(),
 "fragment");

 //            TextView verify=  findViewById(R.id.txt_verify);
 //            verify.setOnClickListener(v -> {
 //                Toast.makeText(this, "clicked" , Toast.LENGTH_SHORT).show();
 //            });
 if (bSheet.verified()){
 Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();
 }
 */


            dialog.setContentView(R.layout.fragment_otp);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            PinView pinview = dialog.findViewById(R.id.otpPinView);
            dialog.findViewById(R.id.txt_verify).setOnClickListener(v -> {
                String pin = pinview.getText().toString();

                if (pin != null ){
                    custPrograssbar.PrograssCreate(this);
                    JSONObject jsonObject = new JSONObject();
                    try {

                        jsonObject.put("rid", sessionManager.getRiderId(""));
                        jsonObject.put("device_id", sessionManager.getDeviceId("imei"));
                        jsonObject.put("jid",order.getJob_id());
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


//    private void onCompleteTxtClick(){
//        String txt=binding.txtStarted.getText().toString();
//        if (txt.equalsIgnoreCase("Completed")){
//            startActivity(new Intent(this,paymentDetails.class).putExtra("jid",order.getJob_id()));
//  /*          Log.e("tagcomplete", "running" );
//            custPrograssbar.PrograssCreate(this);
//            JSONObject jsonObject = new JSONObject();
//            try {
//
//
//                jsonObject.put("rid", sessionManager.getRiderId(""));
//                jsonObject.put("vid", "8");
//                jsonObject.put("device_id", sessionManager.getDeviceId("imei"));
//                jsonObject.put("jid", order.getJob_id());
//                jsonObject.put("status", "complete");
//                jsonObject.put("sign", "");
//                JsonParser jsonParser = new JsonParser();
//
//                Call<JsonObject> call = APIClient.getInterface().getOstatus((JsonObject) jsonParser.parse(jsonObject.toString()));
//                GetResult getResult = new GetResult();
//                getResult.setMyListener(this);
//                getResult.callForLogin(call, "6");
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//*/
//        }
//
//    }

    public static PenddingFragment listener;

    public interface PenddingFragment {
        void onClickItem(String s, Job orderItem);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
