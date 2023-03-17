package com.sky.SkyFleetDriver.retrofit;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST(APIClient.APPEND_URL + "login.php")
    Call<JsonObject> getLogin(@Body JsonObject object);


    //for online or offline status
    @POST(APIClient.APPEND_URL + "status.php")
    Call<JsonObject> getStatus(@Body JsonObject object);

    //for order data like total sale, total complete, total cancelled and online offline status
    @POST(APIClient.APPEND_URL + "order_status.php")
    Call<JsonObject> orderStatus(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "olist.php")
    Call<JsonObject> getOlist(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "complete.php")
    Call<JsonObject> getComplete(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "noti.php")
    Call<JsonObject> getNoti(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "ostatus.php")
    Call<JsonObject> getOstatus(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "area.php")
    Call<JsonObject> getArea(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "profile.php")
    Call<JsonObject> getProfile(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "firebaseid.php")
    Call<JsonObject> sendFirebaseId(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "wallet.php")
    Call<JsonObject> getWallet(@Body JsonObject object);


    /////////////////////////////////////
    @POST(APIClient.APPEND_URL + "home.php")
    Call<JsonObject> gethome(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "otp.php")
    Call<JsonObject> getotpVerification(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "app_version.php")
    Call<JsonObject> checkAppversion(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "payment.php")
    Call<JsonObject> getPaymentDetails(@Body JsonObject object);


}
