package com.sky.SkyFleetDriver;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;
import com.sky.SkyFleetDriver.activity.InfoActivity;
import com.sky.SkyFleetDriver.retrofit.APIClient;
import com.sky.SkyFleetDriver.retrofit.GetResult;
import com.sky.SkyFleetDriver.utils.SessionManager;
import com.sky.skyfleettech.R;

import org.json.JSONObject;

import retrofit2.Call;
import android.content.BroadcastReceiver;


public class MyApplication extends Application implements GetResult.MyListener, OneSignal.OSNotificationWillShowInForegroundHandler {
    public static Context mContext;
    String userid;
    private String firebaseAppId;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final String ONESIGNAL_APP_ID = "49b1c872-c99f-4087-b5f5-572f3d503545";
    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
//         OneSignal Initialization
//        OneSignal.startInit(this)
//                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                .unsubscribeWhenNotificationsAreDisabled(true)
//                .init();


        // OneSignal Initialization
        OneSignal.initWithContext(this);
//        OneSignal.setNotificationWillShowInForegroundHandler((OneSignal.OSNotificationWillShowInForegroundHandler) this);

        OneSignal.setNotificationWillShowInForegroundHandler(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);


//            .idsAvailable(new OneSignal.IdsAvailableHandler() {
//            @Override
//            public void idsAvailable(String userId, String registrationId) {
//                Log.d("debug", "User:" + userId);
//                if (registrationId != null)
//                    Log.d("debug", "registrationId:" + registrationId);
//
//            }
//        });
        sendFirebaseIdToServer( );


    }

    public void setFirebaseAppId(String firebaseAppId) {
        this.firebaseAppId = firebaseAppId;
        sendFirebaseIdToServer();
    }

    public void sendFirebaseIdToServer() {

        try {
//            String token=  OneSignal.getDeviceState().getPushToken();
            String uuid=  OneSignal.getDeviceState().getUserId();


            SessionManager sessionManager;
//            User user;
            sessionManager = new SessionManager(MyApplication.this);
            userid = sessionManager.getRiderId("");
            if (userid != null && !userid.equals("0")) {
                JSONObject jsonObject = new JSONObject();
                JsonParser jsonParser = new JsonParser();
                jsonObject.put("rid", userid);
                jsonObject.put("fid", firebaseAppId);
                jsonObject.put("uuid", uuid);
                Call<JsonObject> call = APIClient.getInterface().sendFirebaseId((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void callback(JsonObject result, String callNo) {

    }

    @Override
    public void notificationWillShowInForeground(OSNotificationReceivedEvent osNotificationReceivedEvent) {

        String title=osNotificationReceivedEvent.getNotification().getTitle();
        String msg=osNotificationReceivedEvent.getNotification().getBody();
        Intent intent
                = new Intent(this, InfoActivity.class);
        // Assign channel ID
        String channel_id = "notification_channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Pass the intent to PendingIntent to start the
        // next Activity
        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
                .setSmallIcon(R.drawable.red_circle_logo)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000,
                        1000, 1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.JELLY_BEAN) {
            builder = builder.setContent(
                    getCustomDesign(title, msg));
        } // If Android Version is lower than Jelly Beans,
        // customized layout cannot be used and thus the
        // layout is set as follows
        else {
            builder = builder.setContentTitle(title)
                    .setContentText(msg)
                    .setSmallIcon(R.drawable.red_circle_logo);
        }
        // Create an object of NotificationManager class to
        // notify the
        // user of events that happen in the background.
        NotificationManager notificationManager
                = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, "web_app",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }

        notificationManager.notify(0, builder.build());
    }

    private RemoteViews getCustomDesign(String title,
                                        String message) {
        @SuppressLint("RemoteViewLayout") RemoteViews remoteViews = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setImageViewResource(R.id.icon,
                R.drawable.red_circle_logo);
        return remoteViews;
    }


}