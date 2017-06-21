package com.prompt.multiplebledeviceconnection.bleUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.prompt.multiplebledeviceconnection.R;
import com.prompt.multiplebledeviceconnection.uicomponents.MyAlertDialog;
import com.prompt.multiplebledeviceconnection.utils.Const;
import com.prompt.multiplebledeviceconnection.utils.Utils;


public class GattConnectReceiver extends BroadcastReceiver {
    private static GattConnectReceiver gattConnectReceiver ;
    private Context mContext ;
    private MyAlertDialog alertDialog ;
    private Handler handler = new Handler();


    public GattConnectReceiver(){
        //Just to ignore Manifest Error Don't use it
    }
    private GattConnectReceiver(Context context) {
        this.mContext = context ;
    }

    public static GattConnectReceiver getInstance(Context context){
        if(gattConnectReceiver == null)
            gattConnectReceiver = new GattConnectReceiver(context);
        return gattConnectReceiver ;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        String deviceAddress = "";
        Bundle extras = intent.getExtras() ;
        if(extras != null && extras.containsKey(Const.EXTRA_BLE_DEVICE_ADDRESS)){
            deviceAddress = extras.get(Const.EXTRA_BLE_DEVICE_ADDRESS).toString();
        }
        if(deviceAddress == null){
            deviceAddress = "" ;
        }
        // Status received when connected to GATT Server
        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {

            //Connected Do nothing
        }else if(BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {

                showAlert(0, mContext.getString(R.string.alert_message_bluetooth_disconnect),deviceAddress);
            }
        }

    protected void showAlert(int id, String message, String address) {
        alertDialog =  new MyAlertDialog(mContext);
        alertDialog.setIcon(R.drawable.ic_alert);
        alertDialog.setMessage(message);
        alertDialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!Utils.isActivityFinish(mContext))
                    alertDialog.dismiss();
            }
        }, Const.DIALOG_DISPLAY_TIME);

//        BluetoothLeService.connect(mContext, address,"");
    }
}
