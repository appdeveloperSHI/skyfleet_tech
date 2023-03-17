package com.sky.SkyFleetDriver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.onesignal.OneSignal;
import com.sky.SkyFleetDriver.model.LoginUser;
import com.sky.SkyFleetDriver.retrofit.APIClient;
import com.sky.SkyFleetDriver.retrofit.GetResult;
import com.sky.SkyFleetDriver.MyApplication;
import com.sky.skyfleettech.databinding.ActivityLoginBinding;
import com.sky.SkyFleetDriver.utils.CustPrograssbar;
import com.sky.SkyFleetDriver.utils.SessionManager;
import com.sky.SkyFleetDriver.utils.Utiles;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;


public class LoginActivity extends AppCompatActivity implements GetResult.MyListener {

    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;

    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().hide();
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(this);

        binding.txtLogin.setOnClickListener(v ->
        {
            if (validation()) {
                loginUser();
            }
        });
    }



    private void loginUser() {
        custPrograssbar.PrograssCreate(LoginActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", binding.edUsername.getText().toString());
            jsonObject.put("device_id", Utiles.getIMEI(LoginActivity.this));
            jsonObject.put("password", binding.edPassword.getText().toString());
            JsonParser jsonParser = new JsonParser();


//          Log.e("taglogin",  Utiles.getIMEI(LoginActivity.this));
            sessionManager.setDeviceId("imei", Utiles.getIMEI(LoginActivity.this));
//          Log.e("taglogiid",sessionManager.getDeviceId("imei"));


            Call<JsonObject> call = APIClient.getInterface().getLogin((JsonObject) jsonParser.parse(jsonObject.toString()));
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

            LoginUser response = gson.fromJson(result.toString(), LoginUser.class);

            Toast.makeText(LoginActivity.this, "" + response.getResponseMsg(), Toast.LENGTH_LONG).show();


            if (response.getResult().equalsIgnoreCase("true")) {
//                sessionManager.setDeviceId("imei",response.getDevice_id());
                sessionManager.setRiderId("", response.getId());
                OneSignal.sendTag("rider_id", response.getId());


                /**     if (response.getUser().getStatus().equalsIgnoreCase("1")) {
                 sessionManager.setBooleanData("status", true);

                 } else {
                 sessionManager.setBooleanData("status", false);
                 }
                 */


                if (binding.chkRemember.isChecked()) {
                    sessionManager.setBooleanData("rlogin", true);
                }
                ((MyApplication)getApplication()).sendFirebaseIdToServer();
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            }
        } catch (Exception e) {
            Log.e("error", " --> " + e.toString());
        }
    }

    public boolean validation() {
        if (binding.edUsername.getText().toString().isEmpty()) {
            binding.edUsername.setError("Enter Mobile No");
            return false;
        }
        if (binding.edPassword.getText().toString().isEmpty()) {
            binding.edUsername.setError("Enter Password");
            return false;
        }

        return true;
    }
}
