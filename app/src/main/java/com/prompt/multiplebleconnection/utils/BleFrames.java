package com.prompt.multiplebleconnection.utils;

import android.content.Context;

import com.prompt.multiplebleconnection.bleUtils.OnDataReceiveInterface;

/**
 * Created by root on 19/9/16.
 */

public class BleFrames {

    public static void getData(Context context, String deviceAddress,String data){
            OnDataReceiveInterface lisInterface =  (OnDataReceiveInterface) context;
            lisInterface.onDataReceived(deviceAddress,data);
    }


}
