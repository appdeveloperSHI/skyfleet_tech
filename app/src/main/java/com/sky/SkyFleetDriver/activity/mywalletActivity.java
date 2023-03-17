package com.sky.SkyFleetDriver.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sky.SkyFleetDriver.model.Tran;
import com.sky.SkyFleetDriver.model.walletModel;
import com.sky.SkyFleetDriver.retrofit.APIClient;
import com.sky.SkyFleetDriver.retrofit.GetResult;
import com.sky.skyfleettech.databinding.ActivityWalletBinding;
import com.sky.skyfleettech.databinding.WalletItemBinding;
import com.sky.SkyFleetDriver.utils.CustPrograssbar;
import com.sky.SkyFleetDriver.utils.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import retrofit2.Call;

public class mywalletActivity extends AppCompatActivity implements GetResult.MyListener {
    private ActivityWalletBinding binding;
    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("My Wallet");


        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
        binding.recycleWallet.setLayoutManager(recyclerLayoutManager);

        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(this);
        getwalletHistry();
    }

    private void getwalletHistry() {
        custPrograssbar.PrograssCreate(this);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("rid", sessionManager.getRiderId(""));
            jsonObject.put("device_id", sessionManager.getDeviceId("imei"));

            JsonParser jsonParser = new JsonParser();

            Call<JsonObject> call = APIClient.getInterface().getWallet((JsonObject) jsonParser.parse(jsonObject.toString()));
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
            walletModel wallet = gson.fromJson(result.toString(), walletModel.class);
            if (wallet.getResult().equalsIgnoreCase("true")) {
                binding.cashAmount.setText("Rs " +wallet.getBalance());
                if (wallet.getTrans().size() == 0) {
//                    binding.txtNodata.setVisibility(View.VISIBLE);
                } else {
                    WalletAdepter myOrderAdepter = new WalletAdepter(wallet.getTrans());
//                    Log.e("walletTag", String.valueOf(wallet.getTrans()));
                    binding.recycleWallet.setAdapter(myOrderAdepter);
                }
            } else {
//                binding.txtNodata.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class WalletAdepter extends RecyclerView.Adapter<WalletAdepter.ViewHolder> {

        private List<Tran> pendinglist;

        public WalletAdepter(List<Tran> pendinglist) {
            this.pendinglist = pendinglist;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {

            return new ViewHolder(WalletItemBinding.inflate(LayoutInflater.from(parent.getContext()),  parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder,
                                     int position) {
            Tran order = pendinglist.get(position);
            holder.walletItemBinding.date.setText("" + order.getMonth() + "\n" + order.getDate()+ "\n" + order.getDay());
            holder.walletItemBinding.name.setText(order.getName());
            holder.walletItemBinding.address.setText(order.getOrigin() + " TO"+"\n" + order.getDestination());
            holder.walletItemBinding.rs.setText(order.getAmount());

        }

        @Override
        public int getItemCount() {
            return pendinglist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private WalletItemBinding walletItemBinding;

            public ViewHolder(WalletItemBinding walletItemBinding) {
                super(walletItemBinding.getRoot());
                this.walletItemBinding=walletItemBinding;

            }
        }
    }
}