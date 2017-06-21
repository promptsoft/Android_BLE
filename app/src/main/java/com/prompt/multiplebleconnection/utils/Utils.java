package com.prompt.multiplebleconnection.utils;

import android.app.ProgressDialog;
import android.content.IntentFilter;

import com.prompt.multiplebleconnection.bleUtils.BluetoothLeService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class Utils {


    /**
     * Adding the necessary INtent filters for Broadcast receivers
     * @return {@link IntentFilter}
     */

    public static IntentFilter makeGattConnectIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_MTU_EXCHANGE);
        return intentFilter;
    }

    public static IntentFilter makeGattServiceDiscoveryIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL);
        return intentFilter;
    }


    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public static String ByteArraytoHex(byte[] bytes) {
        if (bytes != null) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02X ", b));
            }
            return sb.toString();
        }
        return "";
    }

    // Shared preference constant
    private static final String SHARED_PREF_NAME = "CySmart Shared Preference";
    private static ProgressDialog mProgressDialog;
    private static Timer mTimer;


    /**
     * Get the date
     *
     * @return {@link String}
     */
    public static String GetDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());

    }


    /**
     * Get the seven days before date
     *
     * @return {@link String}
     */

    public static String GetDateSevenDaysBack() {
        DateFormat formatter = new SimpleDateFormat("dd_MMM_yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        return formatter.format(calendar.getTime());

    }


    /**
     * Get time and date
     *
     * @return {@link String}
     */

    public static String GetTimeandDate() {
        DateFormat formatter = new SimpleDateFormat("[dd-MMM-yyyy|HH:mm:ss]");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());

    }



    public static String byteToString(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (byte byteChar : array) {
            sb.append(String.format("%02x", byteChar));
        }

        return sb.toString();
    }
    /**
     * @createdBy : Alpesh Makwana
     * @return String.
     * @param: give the format
     * @purpose: It will return the date in parameter format
     */
    public static String convertHexToAscci(String hexValue) {
        StringBuilder output = new StringBuilder("");
        try {
            for (int i = 0; i < hexValue.length(); i += 2) {
                String str = hexValue.substring(i, i + 2);
                output.append((char) Integer.parseInt(str, 16));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }

    public static String getLogFileName() {
        String path = "AMCS_" + Utils.getDate("ddMMyyHHmmss") + ".txt";
        // Log.e("Utils Backup Path",path) ;
        return path;
    }

    /**
     * @return void.
     * @purpose: It will return the date in yyyy-MM-dd format
     */
    public static String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    /**
     * @return void.
     * @param: give the format
     * @purpose: It will return the date in parameter format
     */
    public static String getDate(String dateformat) {
        try {
            return new SimpleDateFormat(dateformat).format(new Date());
        } catch (Exception e) {
            return "date not found";
        }
    }


}
