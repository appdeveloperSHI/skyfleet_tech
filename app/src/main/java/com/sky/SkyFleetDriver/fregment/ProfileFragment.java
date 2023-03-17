package com.sky.SkyFleetDriver.fregment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sky.SkyFleetDriver.model.onlineStatus;
import com.sky.SkyFleetDriver.model.profileData;
import com.sky.SkyFleetDriver.model.totalCompletedStatus;
import com.sky.SkyFleetDriver.retrofit.APIClient;
import com.sky.SkyFleetDriver.retrofit.GetResult;
import com.sky.skyfleettech.R;
import com.sky.SkyFleetDriver.activity.InfoActivity;
import com.sky.skyfleettech.databinding.FragmentProfileBinding;
import com.sky.SkyFleetDriver.utils.CustPrograssbar;
import com.sky.SkyFleetDriver.utils.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;

public class ProfileFragment extends Fragment implements GetResult.MyListener {

    private FragmentProfileBinding binding;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        sessionManager = new SessionManager(getActivity());
        custPrograssbar = new CustPrograssbar();


        if (sessionManager.getBooleanData("status")) {
            binding.txtStatus.setText("Avaliable");
            binding.switch1.setChecked(true);
        } else {
            binding.switch1.setChecked(false);
            binding.txtStatus.setText("Not Avaliable");
        }
        binding.switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    status("1");
                    binding.txtStatus.setText("Avaliable");
                } else {
                    status("0");
                    binding.txtStatus.setText("Not Avaliable");

                }
            }
        });
        orderStatus();
        profiledeatils();
        binding.imgLogout.setOnClickListener(v-> logoutclick());
        return view;
    }

    private void profiledeatils(){
        custPrograssbar.PrograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("rid", sessionManager.getRiderId(""));
            jsonObject.put("device_id", sessionManager.getDeviceId("imei"));

            JsonParser jsonParser = new JsonParser();

            Call<JsonObject> call = APIClient.getInterface().getProfile((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "3");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void status(String key) {
        custPrograssbar.PrograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("rid", sessionManager.getRiderId(""));
            jsonObject.put("device_id", sessionManager.getDeviceId("imei"));

            jsonObject.put("status", key);
            JsonParser jsonParser = new JsonParser();

            Call<JsonObject> call = APIClient.getInterface().getStatus((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void orderStatus() {
        custPrograssbar.PrograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rid", sessionManager.getRiderId(""));
            jsonObject.put("device_id", sessionManager.getDeviceId("imei"));

            JsonParser jsonParser = new JsonParser();

            Call<JsonObject> call = APIClient.getInterface().orderStatus((JsonObject) jsonParser.parse(jsonObject.toString()));
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
                onlineStatus response = gson.fromJson(result.toString(), onlineStatus.class);
                if (response.getResult().equalsIgnoreCase("true")) {
                    sessionManager.setBooleanData("status", binding.switch1.isChecked());
                }
            } else if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();
                totalCompletedStatus ostatus = gson.fromJson(result.toString(), totalCompletedStatus.class);
                binding.txtComplet.setText("" + ostatus.getOrder_data().getTotal_complete_order());
                binding.txtSale.setText( ostatus.getOrder_data().getTotal_sale());
                binding.txtCencel.setText("" + ostatus.getOrder_data().getTotal_reject_order());


                if (ostatus.getOrder_data().getRider_status().equalsIgnoreCase("1")) {
                    binding.switch1.setChecked(true);
                    sessionManager.setBooleanData("status", true);
                } else {
                    binding.switch1.setChecked(false);
                    sessionManager.setBooleanData("status", false);
                }

            } else if (callNo.equalsIgnoreCase("3")) {
                Gson gson = new Gson();
                profileData profiledata = gson.fromJson(result.toString(), profileData.class);
                profiledata(profiledata);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void profiledata(profileData profi_data) {
        if (profi_data != null && sessionManager != null) {
            binding.edUsername.setText("" + profi_data.getRider().getName());
            binding.edEmail.setText("" + profi_data.getRider().getEmail());
            binding.edPhone.setText("" + profi_data.getRider().getMobile());
            binding.txtVehicle.setText("" + profi_data.getRider().getVehicle());
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public void logoutclick(){
        AlertDialog myDelete = new AlertDialog.Builder(getActivity())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout")
                .setIcon(R.drawable.ic_logout)

                .setPositiveButton("Logout", (dialog, whichButton) -> {
                    //your deleting code
                    dialog.dismiss();
                    sessionManager.logoutUser();
                    status("0");
                    binding.txtStatus.setText("Not Avaliable");
                    startActivity(new Intent(getActivity(), InfoActivity.class));
                    getActivity().finish();
                })
                .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                .create();
        myDelete.show();
    }

  /*  @OnClick({ R.id.img_logout})
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.img_edit:
//                startActivity(new Intent(getActivity(), ProfileActivity.class));
//                break;
            case R.id.img_logout:


                AlertDialog myDelete = new AlertDialog.Builder(getActivity())
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout")
                        .setIcon(R.drawable.ic_logout)

                        .setPositiveButton("Logout", (dialog, whichButton) -> {
                            //your deleting code
                            dialog.dismiss();
                            sessionManager.logoutUser();
                            status("0");
                            binding.txtStatus.setText("Not Avaliable");
                            startActivity(new Intent(getActivity(), InfoActivity.class));
                            getActivity().finish();
                        })
                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                        .create();
                myDelete.show();


                break;
            default:
                break;
        }
    }

   */

}
