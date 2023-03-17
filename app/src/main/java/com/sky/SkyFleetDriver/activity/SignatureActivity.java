package com.sky.SkyFleetDriver.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sky.SkyFleetDriver.retrofit.APIClient;
import com.sky.SkyFleetDriver.retrofit.GetResult;
import com.sky.skyfleettech.databinding.ActivitySignatureBinding;
import com.sky.SkyFleetDriver.model.RestResponse;
import com.sky.SkyFleetDriver.utils.CustPrograssbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;

public class SignatureActivity extends AppCompatActivity implements GetResult.MyListener {

    String jid;
    String rid;
    CustPrograssbar custPrograssbar;
    private ActivitySignatureBinding binding;

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
        binding=ActivitySignatureBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        custPrograssbar = new CustPrograssbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Signature");
        rid = getIntent().getStringExtra("rid");
        jid = getIntent().getStringExtra("jid");
        binding.signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
            }

            @Override
            public void onSigned() {
                //Event triggered when the pad is signed
            }

            @Override
            public void onClear() {
                //Event triggered when the pad is cleared
            }
        });

        btnClicked();
    }

    private void btnClicked(){
        binding.txtClear.setOnClickListener(v->  binding.signaturePad.clear());
        binding.txtDone.setOnClickListener(v ->{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            binding.signaturePad.getSignatureBitmap().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            orderCumplet(encoded);
        });
    }



    /*
        @OnClick({R.id.txt_done, R.id.txt_clear})
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.txt_done:
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    signaturePad.getSignatureBitmap().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    orderCumplet(encoded);
                    break;
                case R.id.txt_clear:
                    break;
                default:
                    break;
            }
        }
    */
    private void orderCumplet(String sing) {
        custPrograssbar.PrograssCreate(this);
        JSONObject jsonObject = new JSONObject();
        try {
//            Log.e("tagsign", sing );

            jsonObject.put("rid", rid);
            jsonObject.put("vid", "8");
            jsonObject.put("device_id", "1233");
            jsonObject.put("jid", jid);
            jsonObject.put("status", "complete");
            jsonObject.put("sign", sing);
            JsonParser jsonParser = new JsonParser();

            Call<JsonObject> call = APIClient.getInterface().getOstatus((JsonObject) jsonParser.parse(jsonObject.toString()));
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
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result.toString(), RestResponse.class);
                Toast.makeText(SignatureActivity.this, response.getResponseMsg(), Toast.LENGTH_SHORT).show();
                if (response.getResult().equalsIgnoreCase("true")) {
                    Intent intent = new Intent(SignatureActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
