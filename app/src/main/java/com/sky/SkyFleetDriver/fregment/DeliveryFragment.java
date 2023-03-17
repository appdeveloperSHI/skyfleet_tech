package com.sky.SkyFleetDriver.fregment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sky.SkyFleetDriver.model.completedItem;
import com.sky.SkyFleetDriver.model.completedOrders;
import com.sky.SkyFleetDriver.retrofit.APIClient;
import com.sky.SkyFleetDriver.retrofit.GetResult;
import com.sky.skyfleettech.R;
import com.sky.SkyFleetDriver.activity.OrderDeliverDetailsActivity;
import com.sky.skyfleettech.databinding.DeliveriItemBinding;
import com.sky.skyfleettech.databinding.FragmentDeliveryBinding;
import com.sky.SkyFleetDriver.utils.CustPrograssbar;
import com.sky.SkyFleetDriver.utils.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import static com.sky.SkyFleetDriver.utils.SessionManager.currncy;


public class    DeliveryFragment extends Fragment implements GetResult.MyListener, AdapterView.OnItemSelectedListener {
    private FragmentDeliveryBinding binding;

    public DeliveryFragment() {
        // Required empty public constructor
    }

    public static DeliveryFragment newInstance(String param1, String param2) {
        DeliveryFragment fragment = new DeliveryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    List<completedItem> pendinglistMain = new ArrayList<>();
    PendingAdepter myOrderAdepter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding=FragmentDeliveryBinding.inflate(getLayoutInflater());
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getActivity());
        binding.recycleDelivery.setLayoutManager(recyclerLayoutManager);
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(getActivity());

        getCumpletOrder("Completed");
        spinner();
        return binding.getRoot();
    }
    private void spinner(){
        Spinner spinner = binding.jobSpinner;
        ArrayAdapter adaptr = ArrayAdapter.createFromResource(getActivity(),
                R.array.job_array, R.layout.spinner_item);

        adaptr.setDropDownViewResource(R.layout.spinner_item_dropdown);
        spinner.setAdapter(adaptr);

        spinner.setOnItemSelectedListener(this);
    }


    public void getCumpletOrder(String jobType) {
        custPrograssbar.PrograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("rid", sessionManager.getRiderId(""));
            jsonObject.put("device_id", sessionManager.getDeviceId("imei"));
            jsonObject.put("status",jobType);

            JsonParser jsonParser = new JsonParser();

            Call<JsonObject> call = APIClient.getInterface().getComplete((JsonObject) jsonParser.parse(jsonObject.toString()));
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
                completedOrders completedOrder = gson.fromJson(result.toString(), completedOrders.class);
                if (completedOrder.getResult().equalsIgnoreCase("true")) {
                    binding.txtItmecount.setText(completedOrder.getOrder_data().size() + " Orders");
                    if (completedOrder.getOrder_data().size()  == 0) {
                        binding.txtNodata.setVisibility(View.VISIBLE);
                        binding.recycleDelivery.notifyAll();



                    } else {
                        binding.txtNodata.setVisibility(View.INVISIBLE);
                        pendinglistMain = completedOrder.getOrder_data();
                        myOrderAdepter = new PendingAdepter(pendinglistMain);
                        binding.recycleDelivery.setAdapter(myOrderAdepter);
                        binding.recycleDelivery.notifyAll();

                    }
                } else {

                    binding.txtItmecount.setText( "0 Orders");
                    pendinglistMain.clear();
                    myOrderAdepter = new PendingAdepter(pendinglistMain);
                    binding.recycleDelivery.setAdapter(myOrderAdepter);
                    binding.recycleDelivery.notifyAll();
                    binding.txtNodata.setVisibility(View.VISIBLE);

                }

            }

        } catch (Exception e) {
            e.toString();
        }
    }

    //for spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String titlechange= parent.getItemAtPosition(position).toString();
        String[] words=titlechange.split("\\s");
        getCumpletOrder(words[0]);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class PendingAdepter extends RecyclerView.Adapter<PendingAdepter.ViewHolder> {
        private List<completedItem> pendinglist;

        public PendingAdepter(List<completedItem> pendinglist) {
            this.pendinglist = pendinglist;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {

            return new ViewHolder( DeliveriItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                    parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder,
                                     int position) {

            completedItem order = pendinglist.get(position);
            holder.deliveriItemBinding.txtOderid.setText("BookingID #" + order.getBooking_id());
            holder.deliveriItemBinding.PickupAddress.setText(order.getOrigin_location());
            holder.deliveriItemBinding.dropAddress.setText(order.getDestination_location());
            holder.deliveriItemBinding.txtDateandstatus.setText("Schedule On " + order.getPickup_date());
            holder.deliveriItemBinding.txtType.setText("" + order.getPaymenttype());
            holder.deliveriItemBinding.txtPricetotla.setText(sessionManager.getStringData(currncy) + " " + order.getTotal());
            holder.deliveriItemBinding.correctTime.setText("Time:"+order.getExact_trip_time());
            holder.deliveriItemBinding.kms.setText("Km:"+order.getExact_trip_distance());

            holder.deliveriItemBinding.deliveryItem.setOnClickListener(v ->
                    startActivity(new Intent(getActivity(), OrderDeliverDetailsActivity.class).putExtra("MyClass", order)));
        }



        @Override
        public int getItemCount() {
            return pendinglist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private DeliveriItemBinding deliveriItemBinding;
            public ViewHolder(DeliveriItemBinding deliveriItemBinding) {
                super(deliveriItemBinding.getRoot());
                this.deliveriItemBinding=deliveriItemBinding;
            }
        }
    }
}
