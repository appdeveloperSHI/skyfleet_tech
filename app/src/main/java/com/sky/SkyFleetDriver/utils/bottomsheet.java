package com.sky.SkyFleetDriver.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sky.skyfleettech.R;

public class bottomsheet extends BottomSheetDialogFragment {

    public static bottomsheet newInstance() {
        return new bottomsheet();
    }
    ConstraintLayout amountReceived;
    EditText remark,amount;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ed_amount_popup, container,
                false);

        amount=view.findViewById(R.id.ed_amount);
        remark=view.findViewById(R.id.ed_remark);
        // get the views and attach the listener
//        amountReceived=view.findViewById(R.id.bs_amount_received);
//        amountReceived.setOnClickListener(v ->
//        {
//            if (verified()){
//                paymentDetails payment_activity=new paymentDetails();
//              if ( payment_activity.fromBottomSheet(amount.getText().toString(),remark.getText().toString())){
//               payment_activity.orderStatus("paid");
//              }
//            }
//        });

        return view;

    }
//    public boolean verified(){
//        if (!amount.getText().toString().isEmpty() && !remark.getText().toString().isEmpty()) {
//            return true;
//        }else {
//            amount.setError("Enter Amount");
//            remark.setError("Enter Remark");
//            return false;
//
//        }
//    }
}