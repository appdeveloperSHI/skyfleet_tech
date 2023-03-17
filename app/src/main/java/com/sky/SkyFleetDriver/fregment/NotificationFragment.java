package com.sky.SkyFleetDriver.fregment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sky.SkyFleetDriver.model.notificatn;
import com.sky.SkyFleetDriver.model.notificatnItem;
import com.sky.SkyFleetDriver.retrofit.APIClient;
import com.sky.SkyFleetDriver.retrofit.GetResult;
import com.sky.skyfleettech.databinding.FragmentNotificationBinding;
import com.sky.skyfleettech.databinding.NotificationItemBinding;
import com.sky.SkyFleetDriver.utils.CustPrograssbar;
import com.sky.SkyFleetDriver.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;


public class NotificationFragment extends Fragment implements GetResult.MyListener {

    private FragmentNotificationBinding binding;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getActivity());
        binding.recycleDelivery.setLayoutManager(recyclerLayoutManager);

        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(getActivity());
        getNotification();
        return view;
    }

    private void getNotification() {
        custPrograssbar.PrograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("rid", sessionManager.getRiderId(""));
            jsonObject.put("device_id", sessionManager.getDeviceId("imei"));

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getNoti((JsonObject) jsonParser.parse(jsonObject.toString()));
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
            Gson gson = new Gson();
            notificatn noti = gson.fromJson(result.toString(), notificatn.class);
            if (noti.getResult().equalsIgnoreCase("true")) {
                if (noti.getData().size() == 0) {
                    binding.txtNodata.setVisibility(View.VISIBLE);
                } else {
                    NotificationAdepter myOrderAdepter = new NotificationAdepter(noti.getData());
                    binding.recycleDelivery.setAdapter(myOrderAdepter);
                }
            } else {
                binding.txtNodata.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class NotificationAdepter extends RecyclerView.Adapter<NotificationAdepter.ViewHolder> {

        private List<notificatnItem> pendinglist;

        public NotificationAdepter(List<notificatnItem> pendinglist) {
            this.pendinglist = pendinglist;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            return new ViewHolder( NotificationItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                    parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder,
                                     int position) {
//            Log.e("position", "" + position);
            notificatnItem order = pendinglist.get(position);
            holder.notificationItemBinding.txtTitel.setText("" + order.getMsg());
            holder.notificationItemBinding.txtDate.setText(order.getDate());

        }

        @Override
        public int getItemCount() {
            return pendinglist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private NotificationItemBinding notificationItemBinding;

            public ViewHolder(NotificationItemBinding notificationItemBinding) {
                super(notificationItemBinding.getRoot());
                this.notificationItemBinding = notificationItemBinding;
            }
        }
    }
}
