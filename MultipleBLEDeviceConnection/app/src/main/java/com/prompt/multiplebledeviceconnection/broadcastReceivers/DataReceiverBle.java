package com.prompt.multiplebledeviceconnection.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.prompt.multiplebledeviceconnection.bleUtils.BluetoothLeService;
import com.prompt.multiplebledeviceconnection.utils.BleFrames;
import com.prompt.multiplebledeviceconnection.utils.Const;
import com.prompt.multiplebledeviceconnection.utils.Utils;

import static com.prompt.multiplebledeviceconnection.utils.Utils.byteToString;


public class DataReceiverBle extends BroadcastReceiver {
    private static DataReceiverBle dataReceiverBle ;
    private long mLastDataArrivalTime = 0;
    private Context context;

    public DataReceiverBle(){
        //Just to ignore Manifest Error Don't use it
    }

    private DataReceiverBle(Context context) {
        this.context = context;
    }

    public static DataReceiverBle getInstance(Context context){
        if(dataReceiverBle == null)
            dataReceiverBle = new DataReceiverBle(context);
        return dataReceiverBle ;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Bundle extras = intent.getExtras();
        if (SystemClock.elapsedRealtime() - mLastDataArrivalTime < 25) {
            return;
        }

        mLastDataArrivalTime = SystemClock.elapsedRealtime();

        if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            // Data Received
            if (extras.containsKey(Const.EXTRA_BYTE_VALUE)
                    && extras.containsKey(Const.EXTRA_BLE_DEVICE_ADDRESS)) {

                byte[] array = intent.getByteArrayExtra(Const.EXTRA_BYTE_VALUE);
                String deviceAddress = intent.getStringExtra(Const.EXTRA_BLE_DEVICE_ADDRESS);
//                String deviceName = intent.getStringExtra(Const.EXTRA_BLE_DEVICE_NAME);

                BleFrames.getData(context, Utils.convertHexToAscci(byteToString(array)));

            }
        }
    }

}

