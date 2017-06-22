package com.prompt.multiplebledeviceconnection.utils;

import android.content.Context;


import com.prompt.multiplebledeviceconnection.R;
import com.prompt.multiplebledeviceconnection.base.BaseActivity;
import com.prompt.multiplebledeviceconnection.bleUtils.OnDataReceiveInterface;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

/**
 * Created by root on 19/9/16.
 */

public class BleFrames {


    private static void noDataFound(Context context){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put(context.getString(R.string.something_went_wrong),context.getString(R.string.something_went_wrong));

        OnDataReceiveInterface lisInterface =  (OnDataReceiveInterface) context;
        lisInterface.onDataReceived(hashMap);
    }

    public static void getData(Context context, String data){
        HashMap<String,String> hashMap = new HashMap<>();

            OnDataReceiveInterface lisInterface =  (OnDataReceiveInterface) context;
            lisInterface.onDataReceived(hashMap);
    }


}
