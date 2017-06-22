package com.prompt.multiplebleconnection.utils;

import android.Manifest;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Const {

    // Storage Permissions
    public static final int REQUEST_EXTERNAL_STORAGE = 1;

    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //Preferences name
    public static final String PREF_FILE = "multiBLE_prefs";

    public static final String EXTRA_BYTE_VALUE = "EXTRA_BYTE_VALUE";

    public static final String EXTRA_BLE_DEVICE_ADDRESS = "BLE DEVICE ADDRESS";
    public static final String EXTRA_BLE_DEVICE_NAME = "BLE DEVICE NAME";

}
