package com.sky.SkyFleetDriver.Notification;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sky.SkyFleetDriver.model.notificatn;
import com.sky.skyfleettech.R;
import com.sky.SkyFleetDriver.activity.InfoActivity;
import com.sky.SkyFleetDriver.retrofit.GetResult;
import com.sky.SkyFleetDriver.utils.SessionManager;

import org.json.JSONObject;

public class FirebaseMessageReceiver
        extends FirebaseMessagingService  implements GetResult.MyListener{

    //   User user;
    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {
//        Log.e("msgreceived", "onMessageReceived: ");

        SessionManager sessionManager=new SessionManager(getApplicationContext());
//        user = sessionManager.getUserDetails("");

		/*if(remoteMessage.getData().size()>0){
			showNotification(remoteMessage.getData().get("title"),
						remoteMessage.getData().get("message"));
		}*/


        if (remoteMessage.getNotification() != null) {

            showNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody());
        }
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

    public void showNotification(String title,
                                 String message) {
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
                    getCustomDesign(title, message));
        } // If Android Version is lower than Jelly Beans,
        // customized layout cannot be used and thus the
        // layout is set as follows
        else {
            builder = builder.setContentTitle(title)
                    .setContentText(message)
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

    private void getNotification() {
        JSONObject jsonObject = new JSONObject();
    /*
        try {
            jsonObject.put("uid", user.getId());
            JsonParser jsonParser = new JsonParser();

            Call<JsonObject> call = APIClient.getInterface().getNoti((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    */
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
//            Log.e("notify", "callback:  TRUE" );
            Gson gson = new Gson();
            /**          Noti noti = gson.fromJson(result.toString(), Noti.class);
             if (noti.getResult().equalsIgnoreCase("true")) {
             */
            notificatn noti = gson.fromJson(result.toString(), notificatn.class);
            if (noti.getResult().equalsIgnoreCase("true")) {


            } else {
//                txtNodata.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
