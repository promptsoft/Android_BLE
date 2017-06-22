/*
 * Copyright Cypress Semiconductor Corporation, 2014-2014-2015 All rights reserved.
 *
 * This software, associated documentation and materials ("Software") is
 * owned by Cypress Semiconductor Corporation ("Cypress") and is
 * protected by and subject to worldwide patent protection (UnitedStates and foreign), United States copyright laws and international
 * treaty provisions. Therefore, unless otherwise specified in a separate license agreement between you and Cypress, this Software
 * must be treated like any other copyrighted material. Reproduction,
 * modification, translation, compilation, or representation of this
 * Software in any other form (e.g., paper, magnetic, optical, silicon)
 * is prohibited without Cypress's express written permission.
 *
 * Disclaimer: THIS SOFTWARE IS PROVIDED AS-IS, WITH NO WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO,
 * NONINFRINGEMENT, IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE. Cypress reserves the right to make changes
 * to the Software without notice. Cypress does not assume any liability
 * arising out of the application or use of Software or any product or
 * circuit described in the Software. Cypress does not authorize its
 * products for use as critical components in any products where a
 * malfunction or failure may reasonably be expected to result in
 * significant injury or death ("High Risk Product"). By including
 * Cypress's product in a High Risk Product, the manufacturer of such
 * system or application assumes all risk of such use and in doing so
 * indemnifies Cypress against all liability.
 *
 * Use of this Software may be limited by and subject to the applicable
 * Cypress software license agreement.
 *
 *
 */

package com.prompt.multiplebleconnection.bleUtils;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;


import com.prompt.multiplebleconnection.R;
import com.prompt.multiplebleconnection.utils.Const;
import com.prompt.multiplebleconnection.utils.Logger;
import com.prompt.multiplebleconnection.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;


/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given BlueTooth LE device.
 */
public class BluetoothLeService extends Service {

    /**
     * GATT Status constants
     */
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    private final static String ACTION_GATT_CONNECTING =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTING";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL =
            "com.example.bluetooth.le.ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL";
    public final static String ACTION_MTU_EXCHANGE=
            "android.bluetooth.device.action.mtuExchange";

    /**
     * Connection status Constants
     */
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_DISCONNECTING = 4;
    private final static String ACTION_GATT_DISCONNECTING =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTING";
    /**
     * BluetoothAdapter for handling connections
     */
    private static BluetoothAdapter mBluetoothAdapter;

    private static BluetoothGatt mBluetoothGattBLE2;
    private static BluetoothGatt mBluetoothGattBLE1;

    private static BluetoothGattCharacteristic characteristicTxBle2;
    private static BluetoothGattCharacteristic characteristicRxBle2;

    private static BluetoothGattCharacteristic characteristicTxBLE1;
    private static BluetoothGattCharacteristic characteristicRxBLE2;

    private static final String LIST_NAME = "NAME";
    private static final String LIST_UUID = "UUID";


    private static int mConnectionStateBLE2 = STATE_DISCONNECTED;
    private static int mConnectionStateBLE1 = STATE_DISCONNECTED;
    /**
     * Device address
     */
    private static Context mContext;

    public static boolean isServiceStarted ;


    public BluetoothLeService(){
        //Just to ignore Manifest Error Don't use it
    }

    /**
     * Gatt Callback for BLE Device 1
     */
    private final static BluetoothGattCallback mGattCallbackBLE1 = new BluetoothGattCallback() {


        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {

            Logger.i("onConnectionStateChange");
            String intentAction;
            // GATT Server connected
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                synchronized (mGattCallbackBLE1) {
                    mConnectionStateBLE1 = STATE_CONNECTED;
                }
                broadcastConnectionUpdate(intentAction,gatt);
                String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                        + "[" + gatt.getDevice().getName() + "|" + gatt.getDevice().getAddress() + "] " +
                        mContext.getResources().getString(R.string.dl_connection_established);
                Logger.d(dataLog);
            }
            // GATT Server disconnected
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                synchronized (mGattCallbackBLE1) {
                    mConnectionStateBLE1 = STATE_DISCONNECTED;
                }
                broadcastConnectionUpdate(intentAction,gatt);
                String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                        + "[" + gatt.getDevice().getName() + "|" + gatt.getDevice().getAddress() + "] " +
                        mContext.getResources().getString(R.string.dl_connection_disconnected);
                Logger.d(dataLog);
            }
            // GATT Server Connecting
            if (newState == BluetoothProfile.STATE_CONNECTING) {
                intentAction = ACTION_GATT_CONNECTING;
                synchronized (mGattCallbackBLE1) {
                    mConnectionStateBLE1 = STATE_CONNECTING;
                }
                broadcastConnectionUpdate(intentAction,gatt);
                String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                        + "[" + gatt.getDevice().getName() + "|" + gatt.getDevice().getAddress() + "] " +
                        mContext.getResources().getString(R.string.dl_connection_establishing);
                Logger.d(dataLog);
            }
            // GATT Server disconnected
            else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                intentAction = ACTION_GATT_DISCONNECTING;
                synchronized (mGattCallbackBLE1) {
                    mConnectionStateBLE1 = STATE_DISCONNECTING;
                }
                broadcastConnectionUpdate(intentAction,gatt);
            }
        }

        //Discover Services the BLE Device Supported
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            mBluetoothGattBLE1 = gatt;

            List<BluetoothGattService> services = gatt.getServices();

            String uuid;
            String unknownServiceString = "Unknown String";
//            ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

            for (BluetoothGattService gattService : services) {
                HashMap<String, String> currentServiceData = new HashMap<>();
                uuid = gattService.getUuid().toString();
                currentServiceData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));

                // If the service exists for HM 10 Serial, say so.
                if (SampleGattAttributes.lookup(uuid, unknownServiceString).equals("BLE DEVICE")) {
                    if (characteristicTxBLE1 == null)
                        characteristicTxBLE1 = gattService.getCharacteristic(UUID.fromString(SampleGattAttributes.BLE_TX));

                    if (characteristicRxBLE2 == null)
                        characteristicRxBLE2 = gattService.getCharacteristic(UUID.fromString(SampleGattAttributes.BLE_RX));
                }
                currentServiceData.put(LIST_UUID, uuid);
//                gattServiceData.add(currentServiceData);
            }


            Log.i("onServicesDiscovered", services.toString());
            BluetoothGattCharacteristic therm_char = services.get(2).getCharacteristics().get(0);

            for (BluetoothGattDescriptor descriptor : therm_char.getDescriptors()) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGattBLE1.writeDescriptor(descriptor);
            }

            mBluetoothGattBLE1.readCharacteristic(therm_char);
            mBluetoothGattBLE1.setCharacteristicNotification(therm_char, true);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                String dataLog2 = mContext.getResources().getString(R.string.dl_commaseparator)
                        + "[" + gatt.getDevice().getName() + "|" + gatt.getDevice().getAddress() + "] " +
                        mContext.getResources().getString(R.string.dl_service_discovery_status) +
                        mContext.getResources().getString(R.string.dl_status_success);
                Logger.d(dataLog2);
                broadcastConnectionUpdate(ACTION_GATT_SERVICES_DISCOVERED,gatt);

//                changeBaudRate(gatt.getDevice(),115200);
            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION ||
                    status == BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION) {
//                bondDevice();
                broadcastConnectionUpdate(ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL,gatt);
            } else {
                String dataLog2 = mContext.getResources().getString(R.string.dl_commaseparator)
                        + "[" + gatt.getDevice().getName() + "|" + gatt.getDevice().getAddress() + "] " +
                        mContext.getResources().getString(R.string.dl_service_discovery_status) +
                        mContext.getResources().getString(R.string.dl_status_failure) + status;
                Logger.d(dataLog2);
                broadcastConnectionUpdate(ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL,gatt);
            }


        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            Log.i("onCharacteristicRead", characteristic.toString());
            byte[] value = characteristic.getValue();
            String v = new String(value);
            Log.i("onCharacteristicRead", "Value: " + v);

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic
                                                    characteristic) {
            String str_value = new String(characteristic.getValue());
            Log.i("onCharacteristicChanged", "append:-" + str_value);

//            String characteristicValue = Utils.ByteArraytoHex(characteristic.getValue());

            String characteristicValue = new String(characteristic.getValue());
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator) +
                    "[ " + characteristicValue + " ]";
            Logger.d(dataLog);
            broadcastNotifyUpdate(characteristic, gatt);

        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            Resources res = mContext.getResources();
            String dataLog = String.format(
                    res.getString(R.string.exchange_mtu_rsp),
                    gatt.getDevice().getName(),
                    gatt.getDevice().getAddress(),
                    res.getString(R.string.exchange_mtu),
                    mtu,
                    status);

            Logger.d(dataLog);
            broadcastConnectionUpdate(ACTION_MTU_EXCHANGE, gatt);
        }

    };

    private final static BluetoothGattCallback mGattCallbackBLE2 = new BluetoothGattCallback() {


        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {

            Logger.i("onConnectionStateChange");
            String intentAction;
            // GATT Server connected
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                synchronized (mGattCallbackBLE2) {
                    mConnectionStateBLE2 = STATE_CONNECTED;
                }
                broadcastConnectionUpdate(intentAction,gatt);
                String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                        + "[" + gatt.getDevice().getName() + "|" + gatt.getDevice().getAddress() + "] " +
                        mContext.getResources().getString(R.string.dl_connection_established);
                Logger.d(dataLog);
            }
            // GATT Server disconnected
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                synchronized (mGattCallbackBLE2) {
                    mConnectionStateBLE2 = STATE_DISCONNECTED;
                }
                broadcastConnectionUpdate(intentAction,gatt);
                String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                        + "[" + gatt.getDevice().getName() + "|" + gatt.getDevice().getAddress() + "] " +
                        mContext.getResources().getString(R.string.dl_connection_disconnected);
                Logger.d(dataLog);

            }
            // GATT Server Connecting
            if (newState == BluetoothProfile.STATE_CONNECTING) {
                intentAction = ACTION_GATT_CONNECTING;
                synchronized (mGattCallbackBLE2) {
                    mConnectionStateBLE2 = STATE_CONNECTING;
                }
                broadcastConnectionUpdate(intentAction,gatt);
                String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                        + "[" + gatt.getDevice().getName() + "|" + gatt.getDevice().getAddress() + "] " +
                        mContext.getResources().getString(R.string.dl_connection_establishing);
                Logger.d(dataLog);
            }
            // GATT Server disconnected
            else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                intentAction = ACTION_GATT_DISCONNECTING;
                synchronized (mGattCallbackBLE2) {
                    mConnectionStateBLE2 = STATE_DISCONNECTING;
                }
                broadcastConnectionUpdate(intentAction,gatt);
            }
        }

        //Discover Services the BLE Device Supported
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            mBluetoothGattBLE2 = gatt;

            List<BluetoothGattService> services = gatt.getServices();

            String uuid;
            String unknownServiceString = "Unknown String";
//            ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

            for (BluetoothGattService gattService : services) {
                HashMap<String, String> currentServiceData = new HashMap<>();
                uuid = gattService.getUuid().toString();
                currentServiceData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));

                // If the service exists for HM 10 Serial, say so.
                if (SampleGattAttributes.lookup(uuid, unknownServiceString).equals("BLE DEVICE")) {
                    if (characteristicTxBle2 == null)
                        characteristicTxBle2 = gattService.getCharacteristic(UUID.fromString(SampleGattAttributes.BLE_TX));

                    if (characteristicRxBle2 == null)
                        characteristicRxBle2 = gattService.getCharacteristic(UUID.fromString(SampleGattAttributes.BLE_RX));
                }
                currentServiceData.put(LIST_UUID, uuid);
//                gattServiceData.add(currentServiceData);
            }


            Log.i("onServicesDiscovered", services.toString());
            BluetoothGattCharacteristic therm_char = services.get(2).getCharacteristics().get(0);

            for (BluetoothGattDescriptor descriptor : therm_char.getDescriptors()) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGattBLE2.writeDescriptor(descriptor);
            }

            mBluetoothGattBLE2.readCharacteristic(therm_char);
            mBluetoothGattBLE2.setCharacteristicNotification(therm_char, true);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                String dataLog2 = mContext.getResources().getString(R.string.dl_commaseparator)
                        + "[" + gatt.getDevice().getName() + "|" + gatt.getDevice().getAddress() + "] " +
                        mContext.getResources().getString(R.string.dl_service_discovery_status) +
                        mContext.getResources().getString(R.string.dl_status_success);
                Logger.d(dataLog2);
                broadcastConnectionUpdate(ACTION_GATT_SERVICES_DISCOVERED,gatt);
            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION ||
                    status == BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION) {
//                bondDevice();
                broadcastConnectionUpdate(ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL,gatt);
            } else {
                String dataLog2 = mContext.getResources().getString(R.string.dl_commaseparator)
                        + "[" + gatt.getDevice().getName() + "|" + gatt.getDevice().getAddress() + "] " +
                        mContext.getResources().getString(R.string.dl_service_discovery_status) +
                        mContext.getResources().getString(R.string.dl_status_failure) + status;
                Logger.d(dataLog2);
                broadcastConnectionUpdate(ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL,gatt);
            }


        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            Log.i("onCharacteristicRead", characteristic.toString());
            byte[] value = characteristic.getValue();
            String v = new String(value);
            Log.i("onCharacteristicRead", "Value: " + v);

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic
                                                    characteristic) {
            String str_value = new String(characteristic.getValue());
            Log.i("onCharacteristicChanged", "append:-" + str_value);

//            String characteristicValue = Utils.ByteArraytoHex(characteristic.getValue());

            String characteristicValue = new String(characteristic.getValue());
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator) +
                    "[ " + characteristicValue + " ]";
            Logger.d(dataLog);
            broadcastNotifyUpdate(characteristic, gatt);

        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            Resources res = mContext.getResources();
            String dataLog = String.format(
                    res.getString(R.string.exchange_mtu_rsp),
                    gatt.getDevice().getName(),
                    gatt.getDevice().getAddress(),
                    res.getString(R.string.exchange_mtu),
                    mtu,
                    status);

            Logger.d(dataLog);
            broadcastConnectionUpdate(ACTION_MTU_EXCHANGE, gatt);
        }

    };

    public static void exchangeGattMtu1(int mtu, String deviceAddress) {

        int retry = 5;
        boolean status = false;
        while (!status && retry > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    status = mBluetoothGattBLE1.requestMtu(mtu);
            }
            retry--;
        }
    }

    public static void exchangeGattMtu2(int mtu, String deviceAddress) {

        int retry = 5;
        boolean status = false;
        while (!status && retry > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                status = mBluetoothGattBLE2.requestMtu(mtu);
            }
            retry--;
        }
    }

    /**
     * Request a write on a given {@code BluetoothGattCharacteristic}.
     */


    public static void writeCharacteristicGattBLE2(byte[] byteArray) {
        String characteristicValue = Utils.ByteArraytoHex(byteArray);
        if (mBluetoothAdapter == null || mBluetoothGattBLE2 == null || characteristicTxBle2 == null) {
            return;
        } else {
            characteristicTxBle2.setValue(byteArray);
            mBluetoothGattBLE2.writeCharacteristic(characteristicTxBle2);
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator) +
                    mContext.getResources().getString(R.string.dl_characteristic_write_request) +
                    mContext.getResources().getString(R.string.dl_commaseparator) +
                    "[ " + characteristicValue + " ]";
            Logger.d(dataLog);
        }

    }


    public static void writeCharacteristicGattBLE1(byte[] byteArray) {

        String characteristicValue = Utils.ByteArraytoHex(byteArray);

        if (mBluetoothAdapter == null || mBluetoothGattBLE1 == null || characteristicTxBLE1 == null) {
            return;
        } else {
            characteristicTxBLE1.setValue(byteArray);
            mBluetoothGattBLE1.writeCharacteristic(characteristicTxBLE1);
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator) +
                    mContext.getResources().getString(R.string.dl_characteristic_write_request) +
                    mContext.getResources().getString(R.string.dl_commaseparator) +
                    "[ " + characteristicValue + " ]";
            Logger.d(dataLog);
        }

    }



    private final IBinder mBinder = new LocalBinder();
    /**
     * BlueTooth manager for handling connections
     */
    private BluetoothManager mBluetoothManager;


    private static void broadcastConnectionUpdate(final String action, BluetoothGatt gatt) {
        Logger.i("action :" + action);
        final Intent intent = new Intent(action);
        Bundle mBundle = new Bundle();

        mBundle.putString(Const.EXTRA_BLE_DEVICE_ADDRESS,
                gatt.getDevice().getAddress());
        mBundle.putString(Const.EXTRA_BLE_DEVICE_NAME,
                gatt.getDevice().getName());
        intent.putExtras(mBundle);
        mContext.sendBroadcast(intent);
    }



    private static void broadcastNotifyUpdate(final BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
        final Intent intent = new Intent(BluetoothLeService.ACTION_DATA_AVAILABLE);
        Bundle mBundle = new Bundle();
        // Putting the byte value read for GATT Db
        mBundle.putByteArray(Const.EXTRA_BYTE_VALUE,
                characteristic.getValue());

        mBundle.putString(Const.EXTRA_BLE_DEVICE_ADDRESS,
                gatt.getDevice().getAddress());
        mBundle.putString(Const.EXTRA_BLE_DEVICE_NAME,
                gatt.getDevice().getName());
        intent.putExtras(mBundle);

        /**
         * Sending the broad cast so that it can be received on registered
         * receivers
         */

        mContext.sendBroadcast(intent);
    }


    /**
     * Connects to the GATT server hosted on the BlueTooth LE device.
     */
    public static void connectBLE1(Context context, final String address, final String deviceName) {
        mContext = context;
        if (mBluetoothAdapter == null || address == null) {
            return;
        }

        BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(address);
        if (device == null) {
            return;
        }
        // We want to directly connectBLE1 to the device, so we are setting the
        // autoConnect
        // parameter to false.
        mBluetoothGattBLE1 = null;//Creating a new instance of GATT before connectBLE1
        mBluetoothGattBLE1 = device.connectGatt(mContext, false, mGattCallbackBLE1);
        /**
         * Adding data to the data logger
         */
        String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                + "[" + deviceName + "|" + address + "] " +
                mContext.getResources().getString(R.string.dl_connection_request);
        Logger.d(dataLog);
    }

    public static void connectBLE2(Context context, final String address, final String deviceName) {
        mContext = context;
        if (mBluetoothAdapter == null || address == null) {
            return;
        }

        BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(address);
        if (device == null) {
            return;
        }
        // We want to directly connectBLE1 to the device, so we are setting the
        // autoConnect
        // parameter to false.
            mBluetoothGattBLE2 = null;//Creating a new instance of GATT before connectBLE1
            mBluetoothGattBLE2 = device.connectGatt(mContext, false, mGattCallbackBLE2);

        /**
         * Adding data to the data logger
         */
        String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                + "[" + deviceName + "|" + address + "] " +
                mContext.getResources().getString(R.string.dl_connection_request);
        Logger.d(dataLog);
    }


    public static void reconnectBLE1(String address) {
        Logger.e("<--Reconnecting device-->");
        BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(address);
        if (device == null) {
            return;
        }

        mBluetoothGattBLE1 = null;//Creating a new instance of GATT before connectBLE1
        mBluetoothGattBLE1 = device.connectGatt(mContext, false, mGattCallbackBLE1);

        /**
         * Adding data to the data logger
         */
        String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                + "[" + device.getName() + "|" + device.getAddress() + "] " +
                mContext.getResources().getString(R.string.dl_connection_request);
        Logger.d(dataLog);
    }

    public static void reconnectBLE2(String address) {
        Logger.e("<--Reconnecting device-->");
        BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(address);
        if (device == null) {
            return;
        }

        mBluetoothGattBLE2 = null;//Creating a new instance of GATT before connectBLE1
        mBluetoothGattBLE2 = device.connectGatt(mContext, false, mGattCallbackBLE2);

        /**
         * Adding data to the data logger
         */
        String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                + "[" + device.getName() + "|" + device.getAddress() + "] " +
                mContext.getResources().getString(R.string.dl_connection_request);
        Logger.d(dataLog);
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The
     * disconnection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public static void disconnectBLE1() {
        Logger.i("disconnectBLE2 called");

        if (mBluetoothAdapter == null || mBluetoothGattBLE1 == null) {
            return;
        } else {
            mBluetoothGattBLE1.disconnect();
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                    + "[" + mBluetoothGattBLE1.getDevice().getName() + "|" + mBluetoothGattBLE1.getDevice().getAddress() + "] " +
                    mContext.getResources().getString(R.string.dl_disconnection_request);
            Logger.d(dataLog);
            close();
        }

    }

    public static void disconnectBLE2() {
        Logger.i("disconnectBLE2 called");

        if (mBluetoothAdapter == null || mBluetoothGattBLE2 == null) {
            return;
        } else {

            mBluetoothGattBLE2.disconnect();
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                    + "[" + mBluetoothGattBLE2.getDevice().getName() + "|" + mBluetoothGattBLE2.getDevice().getAddress() + "] " +
                    mContext.getResources().getString(R.string.dl_disconnection_request);
            Logger.d(dataLog);
            close2();
        }

    }




    public static void discoverServicesforBLE1() {
        // Logger.d(mContext.getResources().getString(R.string.dl_service_discover_request));
        if (mBluetoothAdapter == null || mBluetoothGattBLE2 == null) {
            return;
        } else {
            mBluetoothGattBLE2.discoverServices();
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                    + "[" + mBluetoothGattBLE2.getDevice().getName() + "|" + mBluetoothGattBLE2.getDevice().getAddress() + "] " +
                    mContext.getResources().getString(R.string.dl_service_discovery_request);
            Logger.d(dataLog);
        }

    }

    public static void discoverServicesforBLE2() {
        // Logger.d(mContext.getResources().getString(R.string.dl_service_discover_request));
        if (mBluetoothAdapter == null || mBluetoothGattBLE1 == null) {
            return;
        } else {
            mBluetoothGattBLE1.discoverServices();
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                    + "[" + mBluetoothGattBLE1.getDevice().getName() + "|" + mBluetoothGattBLE1.getDevice().getAddress() + "] " +
                    mContext.getResources().getString(R.string.dl_service_discovery_request);
            Logger.d(dataLog);
        }

    }


    public static int getmConnectionStateBLE1() {
        synchronized (mGattCallbackBLE1) {
            return mConnectionStateBLE1;
        }
    }


    /**
     * After using a given BLE device, the app must call this method to ensure
     * resources are released properly.
     */
    private static void close() {



        if (mBluetoothGattBLE2 != null) {
            mBluetoothGattBLE2.close();
            mBluetoothGattBLE2 = null;
        }

        if (mBluetoothGattBLE1 != null) {
            mBluetoothGattBLE1.close();
            mBluetoothGattBLE1 = null;
        }

    }

    private static void close2() {

        if (mBluetoothGattBLE2 != null) {
            mBluetoothGattBLE2.close();
            mBluetoothGattBLE2 = null;
        }

    }



    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    /**
     * Initializes a reference to the local BlueTooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    private boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();

        return mBluetoothAdapter != null;

    }

    @Override
    public void onCreate() {
        // Initializing the service
        stopSelf();
        if (!initialize()) {
            Logger.d("Service not initialized");
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Logger.d("Service on Start");
        isServiceStarted = true ;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d("Service onStartCommand");
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public boolean stopService(Intent name) {
        Logger.d("Service onStop");
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        isServiceStarted = false ;
        close();
//        disableBluetooth();
        super.onDestroy();
    }

    /**
     * Local binder class
     */
    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }
}