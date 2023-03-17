package com.sky.SkyFleetDriver.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.provider.Settings;


import com.sky.SkyFleetDriver.MyApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utiles {

    public static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getNextDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        return dateFormat.format(tomorrow);
    }

    public static String getIMEI(Context context) {
//        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        String device_id = tm.getDeviceId();

        String unique_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//        Log.e("unique_id", "-->" + unique_id);
        return unique_id;

    }

    //    public static boolean internetChack() {
//        ConnectivityManager ConnectionManager = (ConnectivityManager) MyApplication.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
//        if (networkInfo != null && networkInfo.isConnected() == true) {
//            return true;
//        } else {
//            return false;
//        }
//    }
    public static boolean internetChack() {
        ConnectivityManager cm = (ConnectivityManager)MyApplication.mContext. getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}
