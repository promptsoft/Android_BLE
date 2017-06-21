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

package com.prompt.multiplebledeviceconnection.bleUtils;

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


import com.prompt.multiplebledeviceconnection.R;
import com.prompt.multiplebledeviceconnection.utils.Const;
import com.prompt.multiplebledeviceconnection.utils.Logger;
import com.prompt.multiplebledeviceconnection.utils.Prefs;
import com.prompt.multiplebledeviceconnection.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.prompt.multiplebledeviceconnection.utils.Const.BLE_MAC1;
import static com.prompt.multiplebledeviceconnection.utils.Const.BLE_MAC2;
import static com.prompt.multiplebledeviceconnection.utils.Const.BLE_MAC3;
import static com.prompt.multiplebledeviceconnection.utils.Const.BLE_MAC4;


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

    private static BluetoothGatt mBluetoothGattBle1;
    private static BluetoothGatt mBluetoothGattBle2;
    private static BluetoothGatt mBluetoothGattBle3;
    private static BluetoothGatt mBluetoothGattBle4;
    private static BluetoothGatt mBluetoothGattConfiguration;

    private static BluetoothGattCharacteristic characteristicTxBle1;
    private static BluetoothGattCharacteristic characteristicRXBle1;

    private static BluetoothGattCharacteristic characteristicTxBle2;
    private static BluetoothGattCharacteristic characteristicRxBle2;

    private static BluetoothGattCharacteristic characteristicTxBle3;
    private static BluetoothGattCharacteristic characteristicRxBle3;

    private static BluetoothGattCharacteristic characteristicTxBle4;
    private static BluetoothGattCharacteristic characteristicRxBle4;

    private static BluetoothGattCharacteristic characteristicTxConfiguration;
    private static BluetoothGattCharacteristic characteristicRxConfiguration;
    private static final String LIST_NAME = "NAME";
    private static final String LIST_UUID = "UUID";


    private static int mConnectionStateBle1 = STATE_DISCONNECTED;
    private static int mConnectionStateBle2 = STATE_DISCONNECTED;
    private static int mConnectionStateBle3 = STATE_DISCONNECTED;
    private static int mConnectionStateBle4 = STATE_DISCONNECTED;
    private static int mConnectionStateConfiguration = STATE_DISCONNECTED;
    /**
     * Device address
     */
    private static Context mContext;

    public static boolean isServiceStarted ;


    public BluetoothLeService(){
        //Just to ignore Manifest Error Don't use it
    }


    private final static BluetoothGattCallback mGattCallbackBle1 = new BluetoothGattCallback() {


        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {

            Logger.i("onConnectionStateChange");
            String intentAction;
            // GATT Server connected
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                synchronized (mGattCallbackBle1) {
                    mConnectionStateBle1 = STATE_CONNECTED;
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
                synchronized (mGattCallbackBle1) {
                    mConnectionStateBle1 = STATE_DISCONNECTED;
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
                synchronized (mGattCallbackBle1) {
                    mConnectionStateBle1 = STATE_CONNECTING;
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
                synchronized (mGattCallbackBle1) {
                    mConnectionStateBle1 = STATE_DISCONNECTING;
                }
                broadcastConnectionUpdate(intentAction,gatt);
            }
        }

        //Discover Services the BLE Device Supported
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            mBluetoothGattBle1 = gatt;

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
                    if (characteristicTxBle1 == null)
                        characteristicTxBle1 = gattService.getCharacteristic(UUID.fromString(SampleGattAttributes.BLE_TX));

                    if (characteristicRXBle1 == null)
                        characteristicRXBle1 = gattService.getCharacteristic(UUID.fromString(SampleGattAttributes.BLE_RX));
                }
                currentServiceData.put(LIST_UUID, uuid);
//                gattServiceData.add(currentServiceData);
            }


            Log.i("onServicesDiscovered", services.toString());
            BluetoothGattCharacteristic therm_char = services.get(2).getCharacteristics().get(0);

            for (BluetoothGattDescriptor descriptor : therm_char.getDescriptors()) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGattBle1.writeDescriptor(descriptor);
            }

            mBluetoothGattBle1.readCharacteristic(therm_char);
            mBluetoothGattBle1.setCharacteristicNotification(therm_char, true);

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


    private final static BluetoothGattCallback mGattCallbackBle2 = new BluetoothGattCallback() {


        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {

            Logger.i("onConnectionStateChange");
            String intentAction;
            // GATT Server connected
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                synchronized (mGattCallbackBle2) {
                    mConnectionStateBle2 = STATE_CONNECTED;
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
                synchronized (mGattCallbackBle2) {
                    mConnectionStateBle2 = STATE_DISCONNECTED;
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
                synchronized (mGattCallbackBle2) {
                    mConnectionStateBle2 = STATE_CONNECTING;
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
                synchronized (mGattCallbackBle2) {
                    mConnectionStateBle2 = STATE_DISCONNECTING;
                }
                broadcastConnectionUpdate(intentAction,gatt);
            }
        }

        //Discover Services the BLE Device Supported
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            mBluetoothGattBle2 = gatt;

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
                mBluetoothGattBle2.writeDescriptor(descriptor);
            }

            mBluetoothGattBle2.readCharacteristic(therm_char);
            mBluetoothGattBle2.setCharacteristicNotification(therm_char, true);

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

    private final static BluetoothGattCallback mGattCallbackBle3 = new BluetoothGattCallback() {


        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {

            Logger.i("onConnectionStateChange");
            String intentAction;
            // GATT Server connected
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                synchronized (mGattCallbackBle3) {
                    mConnectionStateBle3 = STATE_CONNECTED;
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
                synchronized (mGattCallbackBle3) {
                    mConnectionStateBle3 = STATE_DISCONNECTED;
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
                synchronized (mGattCallbackBle3) {
                    mConnectionStateBle3 = STATE_CONNECTING;
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
                synchronized (mGattCallbackBle3) {
                    mConnectionStateBle3 = STATE_DISCONNECTING;
                }
                broadcastConnectionUpdate(intentAction,gatt);
            }
        }

        //Discover Services the BLE Device Supported
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            mBluetoothGattBle3 = gatt;

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
                    if (characteristicTxBle3 == null)
                        characteristicTxBle3 = gattService.getCharacteristic(UUID.fromString(SampleGattAttributes.BLE_TX));

                    if (characteristicRxBle3 == null)
                        characteristicRxBle3 = gattService.getCharacteristic(UUID.fromString(SampleGattAttributes.BLE_RX));
                }
                currentServiceData.put(LIST_UUID, uuid);
//                gattServiceData.add(currentServiceData);
            }


            Log.i("onServicesDiscovered", services.toString());
            BluetoothGattCharacteristic therm_char = services.get(2).getCharacteristics().get(0);

            for (BluetoothGattDescriptor descriptor : therm_char.getDescriptors()) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGattBle3.writeDescriptor(descriptor);
            }

            mBluetoothGattBle3.readCharacteristic(therm_char);
            mBluetoothGattBle3.setCharacteristicNotification(therm_char, true);

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


    private final static BluetoothGattCallback mGattCallbackBle4 = new BluetoothGattCallback() {


        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {

            Logger.i("onConnectionStateChange");
            String intentAction;
            // GATT Server connected
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                synchronized (mGattCallbackBle4) {
                    mConnectionStateBle4 = STATE_CONNECTED;
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
                synchronized (mGattCallbackBle4) {
                    mConnectionStateBle4 = STATE_DISCONNECTED;
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
                synchronized (mGattCallbackBle4) {
                    mConnectionStateBle4 = STATE_CONNECTING;
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
                synchronized (mGattCallbackBle4) {
                    mConnectionStateBle4 = STATE_DISCONNECTING;
                }
                broadcastConnectionUpdate(intentAction,gatt);
            }
        }

        //Discover Services the BLE Device Supported
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            mBluetoothGattBle4 = gatt;

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
                    if (characteristicTxBle4 == null)
                        characteristicTxBle4 = gattService.getCharacteristic(UUID.fromString(SampleGattAttributes.BLE_TX));

                    if (characteristicRxBle4 == null)
                        characteristicRxBle4 = gattService.getCharacteristic(UUID.fromString(SampleGattAttributes.BLE_RX));
                }
                currentServiceData.put(LIST_UUID, uuid);
//                gattServiceData.add(currentServiceData);
            }


            Log.i("onServicesDiscovered", services.toString());
            BluetoothGattCharacteristic therm_char = services.get(2).getCharacteristics().get(0);

            for (BluetoothGattDescriptor descriptor : therm_char.getDescriptors()) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGattBle4.writeDescriptor(descriptor);
            }

            mBluetoothGattBle4.readCharacteristic(therm_char);
            mBluetoothGattBle4.setCharacteristicNotification(therm_char, true);

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

    private final static BluetoothGattCallback mGattCallbackConfiguration = new BluetoothGattCallback() {


        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {

            Logger.i("onConnectionStateChange");
            String intentAction;
            // GATT Server connected
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                synchronized (mGattCallbackConfiguration) {
                    mConnectionStateConfiguration = STATE_CONNECTED;
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
                synchronized (mGattCallbackConfiguration) {
                    mConnectionStateConfiguration = STATE_DISCONNECTED;
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
                synchronized (mGattCallbackConfiguration) {
                    mConnectionStateConfiguration = STATE_CONNECTING;
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
                synchronized (mGattCallbackConfiguration) {
                    mConnectionStateConfiguration = STATE_DISCONNECTING;
                }
                broadcastConnectionUpdate(intentAction,gatt);
            }
        }

        //Discover Services the BLE Device Supported
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            mBluetoothGattConfiguration = gatt;

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
                    if (characteristicTxConfiguration == null)
                        characteristicTxConfiguration = gattService.getCharacteristic(UUID.fromString(SampleGattAttributes.BLE_TX));

                    if (characteristicRxConfiguration == null)
                        characteristicRxConfiguration = gattService.getCharacteristic(UUID.fromString(SampleGattAttributes.BLE_RX));
                }
                currentServiceData.put(LIST_UUID, uuid);
//                gattServiceData.add(currentServiceData);
            }


            Log.i("onServicesDiscovered", services.toString());
            BluetoothGattCharacteristic therm_char = services.get(2).getCharacteristics().get(0);

            for (BluetoothGattDescriptor descriptor : therm_char.getDescriptors()) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGattConfiguration.writeDescriptor(descriptor);
            }

            mBluetoothGattConfiguration.readCharacteristic(therm_char);
            mBluetoothGattConfiguration.setCharacteristicNotification(therm_char, true);

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

    public static void exchangeGattMtu(int mtu, String deviceAddress) {

        int retry = 5;
        boolean status = false;
        while (!status && retry > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (deviceAddress.equals(Prefs.getvalue(mContext,BLE_MAC1,"")))
                    status = mBluetoothGattBle1.requestMtu(mtu);
                else if (deviceAddress.equals(Prefs.getvalue(mContext,BLE_MAC2,"")) && mBluetoothGattBle2 != null)
                    status = mBluetoothGattBle2.requestMtu(mtu);
                else if (deviceAddress.equals(Prefs.getvalue(mContext,BLE_MAC3,"")) && mBluetoothGattBle3 != null)
                    status = mBluetoothGattBle3.requestMtu(mtu);
                else if (deviceAddress.equals(Prefs.getvalue(mContext,BLE_MAC4,"")) && mBluetoothGattBle4 != null)
                    status = mBluetoothGattBle4.requestMtu(mtu);
                else
                    status = mBluetoothGattConfiguration.requestMtu(mtu);
            }
            retry--;
        }
    }

    /**
     * Request a write on a given {@code BluetoothGattCharacteristic}.
     */

    public static void writeCharacteristicGattDb1(byte[] byteArray) {

        String characteristicValue = Utils.ByteArraytoHex(byteArray);

        if (mBluetoothAdapter == null || mBluetoothGattBle1 == null || characteristicTxBle1 == null) {
            return;
        } else {
            characteristicTxBle1.setValue(byteArray);
            mBluetoothGattBle1.writeCharacteristic(characteristicTxBle1);
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator) +
                    mContext.getResources().getString(R.string.dl_characteristic_write_request) +
                    mContext.getResources().getString(R.string.dl_commaseparator) +
                    "[ " + characteristicValue + " ]";
            Logger.d(dataLog);
        }

    }

    public static void writeCharacteristicGattDb2(byte[] byteArray) {
        String characteristicValue = Utils.ByteArraytoHex(byteArray);
        if (mBluetoothAdapter == null || mBluetoothGattBle2 == null || characteristicTxBle2 == null) {
            return;
        } else {
            characteristicTxBle2.setValue(byteArray);
            mBluetoothGattBle2.writeCharacteristic(characteristicTxBle2);
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator) +
                    mContext.getResources().getString(R.string.dl_characteristic_write_request) +
                    mContext.getResources().getString(R.string.dl_commaseparator) +
                    "[ " + characteristicValue + " ]";
            Logger.d(dataLog);
        }

    }

    public static void writeCharacteristicGattDb3(byte[] byteArray) {
        String characteristicValue = Utils.ByteArraytoHex(byteArray);
        if (mBluetoothAdapter == null || mBluetoothGattBle3 == null || characteristicTxBle3 == null) {
            return;
        } else {
            characteristicTxBle3.setValue(byteArray);
            mBluetoothGattBle3.writeCharacteristic(characteristicTxBle3);
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator) +
                    mContext.getResources().getString(R.string.dl_characteristic_write_request) +
                    mContext.getResources().getString(R.string.dl_commaseparator) +
                    "[ " + characteristicValue + " ]";
            Logger.d(dataLog);
        }

    }


    public static void writeCharacteristicGattDb4(byte[] byteArray) {
        String characteristicValue = Utils.ByteArraytoHex(byteArray);
        if (mBluetoothAdapter == null || mBluetoothGattBle4 == null || characteristicTxBle4 == null) {
            return;
        } else {
            characteristicTxBle4.setValue(byteArray);
            mBluetoothGattBle4.writeCharacteristic(characteristicTxBle4);
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator) +
                    mContext.getResources().getString(R.string.dl_characteristic_write_request) +
                    mContext.getResources().getString(R.string.dl_commaseparator) +
                    "[ " + characteristicValue + " ]";
            Logger.d(dataLog);
        }

    }

    public static void writeCharacteristicGattDb(String deviceAddress , byte[] byteArray) {
        if(deviceAddress == null){
            return;
        }
        if(mBluetoothGattBle1 != null && deviceAddress.equals(mBluetoothGattBle1.getDevice().getAddress()))
            writeCharacteristicGattDb1(byteArray);
        else if(mBluetoothGattBle2 != null &&deviceAddress.equals(mBluetoothGattBle2.getDevice().getAddress()))
            writeCharacteristicGattDb2(byteArray);
        else if(mBluetoothGattBle3 != null && deviceAddress.equals(mBluetoothGattBle3.getDevice().getAddress()))
            writeCharacteristicGattDb3(byteArray);
        else if(mBluetoothGattBle4 != null && deviceAddress.equals(mBluetoothGattBle4.getDevice().getAddress()))
            writeCharacteristicGattDb4(byteArray);
        else
            return;
    }

    public static void writeCharacteristicGattConfiguration(byte[] byteArray) {

        String characteristicValue = Utils.ByteArraytoHex(byteArray);

        if (mBluetoothAdapter == null || mBluetoothGattConfiguration == null || characteristicTxConfiguration == null) {
            return;
        } else {
            characteristicTxConfiguration.setValue(byteArray);
            mBluetoothGattConfiguration.writeCharacteristic(characteristicTxConfiguration);
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
    public static void connect(Context context, final String address, final String deviceName) {
        mContext = context;
        if (mBluetoothAdapter == null || address == null) {
            return;
        }

        BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(address);
        if (device == null) {
            return;
        }
        // We want to directly connect to the device, so we are setting the
        // autoConnect
        // parameter to false.
        if (device.getAddress().equals(Prefs.getvalue(mContext, BLE_MAC1, ""))) {
            mBluetoothGattBle1 = null;//Creating a new instance of GATT before connect
            mBluetoothGattBle1 = device.connectGatt(mContext, false, mGattCallbackBle1);
        } else if (device.getAddress().equals(Prefs.getvalue(mContext, BLE_MAC2, ""))) {
            mBluetoothGattBle2 = null;//Creating a new instance of GATT before connect
            mBluetoothGattBle2 = device.connectGatt(mContext, false, mGattCallbackBle2);
        } else if (device.getAddress().equals(Prefs.getvalue(mContext, BLE_MAC3, ""))) {
            mBluetoothGattBle3 = null;//Creating a new instance of GATT before connect
            mBluetoothGattBle3 = device.connectGatt(mContext, false, mGattCallbackBle3);
        } else if (device.getAddress().equals(Prefs.getvalue(mContext, BLE_MAC4, ""))) {
            mBluetoothGattBle4 = null;//Creating a new instance of GATT before connect
            mBluetoothGattBle4 = device.connectGatt(mContext, false, mGattCallbackBle4);
        } else {
            mBluetoothGattConfiguration = null;//Creating a new instance of GATT before connect
            mBluetoothGattConfiguration = device.connectGatt(mContext, false, mGattCallbackConfiguration);
        }
        /**
         * Adding data to the data logger
         */
        String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                + "[" + deviceName + "|" + address + "] " +
                mContext.getResources().getString(R.string.dl_connection_request);
        Logger.d(dataLog);
    }


    public static void connectForConfiguration(final String address, final String deviceName, Context context) {
        mContext = context;
        if (mBluetoothAdapter == null || address == null) {
            return;
        }
        BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(address);
        if (device == null) {
            return;
        }
        // We want to directly connect to the device, so we are setting the
        // autoConnect
        // parameter to false.

        mBluetoothGattConfiguration = null;//Creating a new instance of GATT before connect
        characteristicTxConfiguration = null ;
        characteristicRxConfiguration = null ;
        mBluetoothGattConfiguration = device.connectGatt(mContext, false, mGattCallbackConfiguration);
        /**
         * Adding data to the data logger
         */
        String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                + "[" + deviceName + "|" + address + "] " +
                mContext.getResources().getString(R.string.dl_connection_request);
        Logger.d(dataLog);
    }

    /**
     * Reconnect method to connect to already connected device
     */
    public static void reconnect(String address) {
        Logger.e("<--Reconnecting device-->");
        BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(address);
        if (device == null) {
            return;
        }

        if (device.getAddress().equals(Prefs.getvalue(mContext, Const.BLE_MAC1, ""))) {
            mBluetoothGattBle1 = null;//Creating a new instance of GATT before connect
            mBluetoothGattBle1 = device.connectGatt(mContext, false, mGattCallbackBle1);
        } else if (device.getAddress().equals(Prefs.getvalue(mContext, Const.BLE_MAC2, ""))) {
            mBluetoothGattBle2 = null;//Creating a new instance of GATT before connect
            mBluetoothGattBle2 = device.connectGatt(mContext, false, mGattCallbackBle2);
        } else if (device.getAddress().equals(Prefs.getvalue(mContext, Const.BLE_MAC3, ""))) {
            mBluetoothGattBle3 = null;//Creating a new instance of GATT before connect
            mBluetoothGattBle3 = device.connectGatt(mContext, false, mGattCallbackBle3);
        } else if (device.getAddress().equals(Prefs.getvalue(mContext, Const.BLE_MAC4, ""))) {
            mBluetoothGattBle4 = null;//Creating a new instance of GATT before connect
            mBluetoothGattBle4 = device.connectGatt(mContext, false, mGattCallbackBle4);
        } else {
            mBluetoothGattConfiguration = null;//Creating a new instance of GATT before connect
            mBluetoothGattConfiguration = device.connectGatt(mContext, false, mGattCallbackConfiguration);
        }

        /**
         * Adding data to the data logger
         */
        String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                + "[" + device.getName() + "|" + device.getAddress() + "] " +
                mContext.getResources().getString(R.string.dl_connection_request);
        Logger.d(dataLog);

    }


    public static void reconnectForConfiguration(String address) {
        Logger.e("<--Reconnecting device-->");
        BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(address);
        if (device == null) {
            return;
        }

        mBluetoothGattConfiguration = null;//Creating a new instance of GATT before connect
        mBluetoothGattConfiguration = device.connectGatt(mContext, false, mGattCallbackConfiguration);

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
    private static void disconnectBle1() {
        Logger.i("disconnectBle1 called");
        if (mBluetoothAdapter == null || mBluetoothGattBle1 == null) {
            return;
        } else {

            mBluetoothGattBle1.disconnect();
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                    + "[" + mBluetoothGattBle1.getDevice().getName() + "|" + mBluetoothGattBle1.getDevice().getAddress() + "] " +
                    mContext.getResources().getString(R.string.dl_disconnection_request);
            Logger.d(dataLog);
            close1();
        }


    }

    private static void disconnectBle2() {
        Logger.i("disconnectBle2 called");

        if (mBluetoothAdapter == null || mBluetoothGattBle2 == null) {
            return;
        } else {

            mBluetoothGattBle2.disconnect();
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                    + "[" + mBluetoothGattBle2.getDevice().getName() + "|" + mBluetoothGattBle2.getDevice().getAddress() + "] " +
                    mContext.getResources().getString(R.string.dl_disconnection_request);
            Logger.d(dataLog);
            close2();
        }

    }

    private static void disconnectBle3() {
        Logger.i("disconnectBle3 called");

        if (mBluetoothAdapter == null || mBluetoothGattBle3 == null) {
            return;
        } else {

            mBluetoothGattBle3.disconnect();
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                    + "[" + mBluetoothGattBle3.getDevice().getName() + "|" + mBluetoothGattBle3.getDevice().getAddress() + "] " +
                    mContext.getResources().getString(R.string.dl_disconnection_request);
            Logger.d(dataLog);
            close3();
        }

    }

    private static void disconnectBle4() {
        Logger.i("disconnectBle4 called");

        if (mBluetoothAdapter == null || mBluetoothGattBle4 == null) {
            return;
        } else {

            mBluetoothGattBle4.disconnect();
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                    + "[" + mBluetoothGattBle4.getDevice().getName() + "|" + mBluetoothGattBle4.getDevice().getAddress() + "] " +
                    mContext.getResources().getString(R.string.dl_disconnection_request);
            Logger.d(dataLog);
            close4();
        }

    }

    public static void disconnectConfiguration() {
        Logger.i("disconnectBle4 called");

        if (mBluetoothAdapter == null || mBluetoothGattConfiguration == null) {
            return;
        } else {
            mBluetoothGattConfiguration.disconnect();
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                    + "[" + mBluetoothGattConfiguration.getDevice().getName() + "|" + mBluetoothGattConfiguration.getDevice().getAddress() + "] " +
                    mContext.getResources().getString(R.string.dl_disconnection_request);
            Logger.d(dataLog);
            close();
        }

    }

    public static void disconnect(String address) {

        if (address.equals(Prefs.getvalue(mContext, Const.BLE_MAC1, "")))
            disconnectBle1();
        else if (address.equals(Prefs.getvalue(mContext, Const.BLE_MAC2, "")))
            disconnectBle2();
        else if (address.equals(Prefs.getvalue(mContext, Const.BLE_MAC3, "")))
            disconnectBle3();
        else if (address.equals(Prefs.getvalue(mContext, Const.BLE_MAC4, "")))
            disconnectBle4();

    }

    public static void discoverServicesforBle1() {
        // Logger.d(mContext.getResources().getString(R.string.dl_service_discover_request));
        if (mBluetoothAdapter == null || mBluetoothGattBle1 == null) {
            return;
        } else {
            mBluetoothGattBle1.discoverServices();
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                    + "[" + mBluetoothGattBle1.getDevice().getName() + "|" + mBluetoothGattBle1.getDevice().getAddress() + "] " +
                    mContext.getResources().getString(R.string.dl_service_discovery_request);
            Logger.d(dataLog);
        }

    }

    public static void discoverServicesforBle2() {
        // Logger.d(mContext.getResources().getString(R.string.dl_service_discover_request));
        if (mBluetoothAdapter == null || mBluetoothGattBle2 == null) {
            return;
        } else {
            mBluetoothGattBle2.discoverServices();
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                    + "[" + mBluetoothGattBle2.getDevice().getName() + "|" + mBluetoothGattBle2.getDevice().getAddress() + "] " +
                    mContext.getResources().getString(R.string.dl_service_discovery_request);
            Logger.d(dataLog);
        }

    }

    public static void discoverServicesforBle3() {
        // Logger.d(mContext.getResources().getString(R.string.dl_service_discover_request));
        if (mBluetoothAdapter == null || mBluetoothGattBle3 == null) {
            return;
        } else {
            mBluetoothGattBle3.discoverServices();
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                    + "[" + mBluetoothGattBle3.getDevice().getName() + "|" + mBluetoothGattBle3.getDevice().getAddress() + "] " +
                    mContext.getResources().getString(R.string.dl_service_discovery_request);
            Logger.d(dataLog);
        }

    }

    public static void discoverServicesforBle4() {
        // Logger.d(mContext.getResources().getString(R.string.dl_service_discover_request));
        if (mBluetoothAdapter == null || mBluetoothGattBle4 == null) {
            return;
        } else {
            mBluetoothGattBle4.discoverServices();
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                    + "[" + mBluetoothGattBle4.getDevice().getName() + "|" + mBluetoothGattBle4.getDevice().getAddress() + "] " +
                    mContext.getResources().getString(R.string.dl_service_discovery_request);
            Logger.d(dataLog);
        }

    }

    public static void discoverServicesforConfiguration() {
        // Logger.d(mContext.getResources().getString(R.string.dl_service_discover_request));
        if (mBluetoothAdapter == null || mBluetoothGattConfiguration == null) {
            return;
        } else {
            mBluetoothGattConfiguration.discoverServices();
            String dataLog = mContext.getResources().getString(R.string.dl_commaseparator)
                    + "[" + mBluetoothGattConfiguration.getDevice().getName() + "|" + mBluetoothGattConfiguration.getDevice().getAddress() + "] " +
                    mContext.getResources().getString(R.string.dl_service_discovery_request);
            Logger.d(dataLog);
        }

    }


    public static int getConnectionState(Context context , String address) {
        mContext = context ;
        if (address.equals(Prefs.getvalue(mContext, Const.BLE_MAC1, "")))
            return getConnectionStateBle1();
        else if (address.equals(Prefs.getvalue(mContext, Const.BLE_MAC2, "")))
            return getConnectionStateBle2();
        else if (address.equals(Prefs.getvalue(mContext, Const.BLE_MAC3, "")))
            return getConnectionStateBle3();
        else if (address.equals(Prefs.getvalue(mContext, Const.BLE_MAC4, "")))
            return getConnectionStateBle4();
        else
            return 0;
    }

    public static int getConnectionStateBle1() {
        synchronized (mGattCallbackBle1) {
            return mConnectionStateBle1;
        }
    }

    public static int getConnectionStateBle2() {
        synchronized (mGattCallbackBle2) {
            return mConnectionStateBle2;
        }
    }

    public static int getConnectionStateBle3() {
        synchronized (mGattCallbackBle3) {
            return mConnectionStateBle3;
        }
    }

    public static int getConnectionStateBle4() {
        synchronized (mGattCallbackBle4) {
            return mConnectionStateBle4;
        }
    }

    public static int getmConnectionStateConfiguration() {
        synchronized (mGattCallbackConfiguration) {
            return mConnectionStateConfiguration;
        }
    }


    /**
     * After using a given BLE device, the app must call this method to ensure
     * resources are released properly.
     */
    private static void close() {

        if (mBluetoothGattBle1 != null) {
            mBluetoothGattBle1.close();
            mBluetoothGattBle1 = null;
        }

        if (mBluetoothGattBle2 != null) {
            mBluetoothGattBle2.close();
            mBluetoothGattBle2 = null;
        }

        if (mBluetoothGattBle3 != null) {
            mBluetoothGattBle3.close();
            mBluetoothGattBle3 = null;
        }

        if (mBluetoothGattBle4 != null) {
            mBluetoothGattBle4.close();
            mBluetoothGattBle4 = null;
        }

        if (mBluetoothGattConfiguration != null) {
            mBluetoothGattConfiguration.close();
            mBluetoothGattConfiguration = null;
        }

    }
    private static void close1() {

        if (mBluetoothGattBle1 != null) {
            mBluetoothGattBle1.close();
            mBluetoothGattBle1 = null;
        }

    }
    private static void close2() {

        if (mBluetoothGattBle2 != null) {
            mBluetoothGattBle2.close();
            mBluetoothGattBle2 = null;
        }

    }
    private static void close3() {

        if (mBluetoothGattBle3 != null) {
            mBluetoothGattBle3.close();
            mBluetoothGattBle3 = null;
        }

    }
    private static void close4() {

        if (mBluetoothGattBle4 != null) {
            mBluetoothGattBle4.close();
            mBluetoothGattBle4 = null;
        }

    }


    private static void disableBluetooth(){
        if(mBluetoothAdapter != null)
            mBluetoothAdapter.disable() ;
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


    public static void changeHwBleSetting() {

        BluetoothDevice device = null;

        String baudRate = "115200",parity = "N",dataBits = "8",stopBit = "1",debugStatus = "1",continuousData = "1";

        if (mBluetoothGattConfiguration == null)
            return;

        device = mBluetoothGattConfiguration.getDevice();
//        String chanegBaudRatestr = "@\"" + Utils.getBleAddress(device) + "\",\"" + device.getName() +
//                "\",\"" + baudRate + "," + parity+","+dataBits+","+
//                stopBit+"\",setting\r\n";
        String chanegBaudRatestr = "@\"" + device.getAddress() + "\",\"" + device.getName() +"\",\"" + debugStatus +
                "\",\"" + continuousData +
                "\",\"" + baudRate + "," + parity+","+dataBits+","+
                stopBit+"\",setting\r\n";
        writeCharacteristicGattConfiguration(chanegBaudRatestr.getBytes());
        Logger.d("BaudRate Setting :: " + chanegBaudRatestr);

    }


    public static void checkSetting(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothDevice device = mBluetoothGattConfiguration.getDevice() ;
                String checkSetting = "@\""+ Utils.getBleAddress(device)+"\",setting?\r\n" ;

                writeCharacteristicGattConfiguration(checkSetting.getBytes());
                Logger.d("Check Setting :: "+checkSetting);

            }
        },1000);

    }

    public static void changeName(String name){
                BluetoothDevice device = mBluetoothGattConfiguration.getDevice() ;
                String checkSetting = "@\""+ Utils.getBleAddress(device)+"\",setting?\r\n" ;

                writeCharacteristicGattConfiguration(checkSetting.getBytes());
                Logger.d("Check Setting :: "+checkSetting);

    }

}