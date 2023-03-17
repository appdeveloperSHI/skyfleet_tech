package com.sky.SkyFleetDriver.fregment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.sky.SkyFleetDriver.adopter.viaPtsAdapter;
import com.sky.SkyFleetDriver.model.Job;
import com.sky.SkyFleetDriver.model.pendingItem;
import com.sky.SkyFleetDriver.retrofit.APIClient;
import com.sky.SkyFleetDriver.retrofit.GetResult;
import com.sky.SkyFleetDriver.activity.OrderPendingDetailsActivity;
import com.sky.SkyFleetDriver.activity.paymentDetails;
import com.sky.skyfleettech.databinding.FragmentPenddingBinding;
import com.sky.skyfleettech.databinding.PendingItemBinding;
import com.sky.SkyFleetDriver.utils.CustPrograssbar;
import com.sky.SkyFleetDriver.utils.SessionManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import static com.sky.SkyFleetDriver.utils.SessionManager.currncy;


public class PendingFragment extends Fragment implements GetResult.MyListener, OrderPendingDetailsActivity.PenddingFragment {

    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    private FragmentPenddingBinding binding;

    List<Job> pendinglistMain = new ArrayList<>();
    PendingAdepter myOrderAdepter;

    public PendingFragment() {
        // Required empty public constructor
    }

    public static PendingFragment newInstance(String param1, String param2) {
        PendingFragment fragment = new PendingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentPenddingBinding.inflate(getLayoutInflater());
        OrderPendingDetailsActivity.listener = this;
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(getActivity());
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclePending.setLayoutManager(recyclerLayoutManager);
        getPendingOrder();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.refresh.setOnClickListener(v->{
            getPendingOrder();
        });
    }

    private void getPendingOrder() {
        custPrograssbar.PrograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("rid", sessionManager.getRiderId(""));
            jsonObject.put("device_id", sessionManager.getDeviceId("imei"));



            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getOlist((JsonObject) jsonParser.parse(jsonObject.toString()));
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
                pendingItem pendingOrder = gson.fromJson(result.toString(), pendingItem.class);

                binding.txtVehicle.setText(pendingOrder.getVehicle());

                if (pendingOrder.getResult().equalsIgnoreCase("true")) {
                    binding.txtItmecount.setText(pendingOrder.getJobs().size() + " Orders");
                    if (pendingOrder.getJobs().size() == 0) {
                        binding.txtNodata.setVisibility(View.VISIBLE);
                        binding.recyclePending.setVisibility(View.GONE);
                    } else {
                        pendinglistMain = pendingOrder.getJobs();
                        myOrderAdepter = new PendingAdepter(pendinglistMain);
                        binding.recyclePending.setAdapter(myOrderAdepter);



                    }
                } else {
                    binding.txtNodata.setVisibility(View.VISIBLE);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }





    @Override
    public void onClickItem(String s, Job orderItem) {

        for (int i = 0; i < pendinglistMain.size(); i++) {
            if (pendinglistMain.get(i).getJob_id().equalsIgnoreCase(orderItem.getJob_id())) {
                if (s.equalsIgnoreCase("reject")) {
                    pendinglistMain.remove(i);
                    myOrderAdepter.notifyDataSetChanged();
                } else {

                    orderItem.setStatus("processing");
                    pendinglistMain.set(i, orderItem);
                    myOrderAdepter.notifyDataSetChanged();
                }
                break;  // uncomment to get the first instance
            }
        }

    }

    public class PendingAdepter extends RecyclerView.Adapter<PendingAdepter.ViewHolder> {
        private List<Job> pendinglist;

        public PendingAdepter(List<Job> pendinglist) {
            this.pendinglist = pendinglist;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            return new ViewHolder( PendingItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                    parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder,
                                     int position) {
            String[] descriptionData = {"Pending", "Accepted", "Dispatched"};
            Job order = pendinglist.get(position);

            holder.pendingItemBinding.txtOderid.setText("BookingID #" + order.getBooking_id());
//     holder.pendingItemBinding.txtOderid.setText("BookingID #" + order.getJob_id());
            holder.pendingItemBinding.PickupAddress.setText(order.getOrigin_location());
            holder.pendingItemBinding.dropAddress.setText(order.getDestination_location());
            holder.pendingItemBinding.txtDateandstatus.setText("Schedule On "+ order.getSchedule_date());


            holder.pendingItemBinding.txtPricetotla.setText(sessionManager.getStringData(currncy) + " " + order.getTotal_price());

            if(order.getStatus().equalsIgnoreCase("Assigned")){
                holder.pendingItemBinding.timeView.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
            }else if(order.getStatus().equalsIgnoreCase("prcessing")){
                holder.pendingItemBinding.timeView.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
            }else  if(order.getStatus().equalsIgnoreCase("dispatched"))
            {
                holder.pendingItemBinding.timeView.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);

            }
            holder.pendingItemBinding.timeView.checkStateCompleted(true);
            holder.pendingItemBinding.timeView.setStateDescriptionData(descriptionData);

            holder.pendingItemBinding.pendingCard.setOnClickListener(v ->
            {
                if (order.getStatus().equalsIgnoreCase("completed")) {
                    startActivity(new Intent(getActivity(), paymentDetails.class).putExtra("jid", order.getJob_id()));

                } else {
                    startActivity(new Intent(getActivity(), OrderPendingDetailsActivity.class).putExtra("MyClass", order));

                }
            });

            if(!order.getWaypts().isEmpty()){//!= null
                viaPtsAdapter adapter=new viaPtsAdapter(order.getWaypts(),getContext(),order,false);
                holder.pendingItemBinding.viaRv.setAdapter(adapter);
            }



        }

        @Override
        public int getItemCount() {
            return pendinglist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private PendingItemBinding pendingItemBinding;
            public ViewHolder(PendingItemBinding pendingItemBinding) {
                super(pendingItemBinding.getRoot());
                this.pendingItemBinding=pendingItemBinding;
            }
        }
    }


}
