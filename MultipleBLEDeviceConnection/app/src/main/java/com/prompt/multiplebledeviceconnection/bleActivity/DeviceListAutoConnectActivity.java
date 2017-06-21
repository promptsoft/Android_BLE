/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.prompt.multiplebledeviceconnection.bleActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.prompt.multiplebledeviceconnection.R;
import com.prompt.multiplebledeviceconnection.base.BaseActivity;
import com.prompt.multiplebledeviceconnection.bleUtils.BluetoothLeService;
import com.prompt.multiplebledeviceconnection.model.BLEDevice;
import com.prompt.multiplebledeviceconnection.utils.Const;
import com.prompt.multiplebledeviceconnection.utils.Logger;
import com.prompt.multiplebledeviceconnection.utils.Prefs;
import com.prompt.multiplebledeviceconnection.utils.Utils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class DeviceListAutoConnectActivity extends BaseActivity {

    // Stops scanning after 2 seconds.
    private static final long SCAN_PERIOD_TIMEOUT = 2000;
    private Timer mScanTimer;
    private boolean mScanning;

    private ViewHolder viewHolder;

    // Connection time out after 10 seconds.
    private static final long CONNECTION_TIMEOUT = 10000;
    private Timer mConnectTimer;
    private boolean mConnectTimerON = false;

    // device details
    public static String mDeviceName = "name";
    public static String mDeviceAddress = "address";

    //Pair status button and variables
    public static Button mPairButton;

    //Bluetooth adapter
    private static BluetoothAdapter mBluetoothAdapter;

    ArrayList<CheckBox> checkBoxes;

    // Devices list variables
    private LeDeviceListAdapter mLeDeviceListAdapter;

    // Activity request constant
    private static final int REQUEST_ENABLE_BT = 1;
    //GUI elements
    private ListView mProfileListView;

    private ProgressDialog mProgressdialog;

    //  Flags
    private boolean mSearchEnabled = false;


    //Delay Time out milisecons
    private static final long DELAY_PERIOD = 500;

    private ProgressDialog mpdia;
    private AlertDialog mAlert;
    /**
     * Service Discovery
     */
    private Timer mTimer;
    //milli seconds
    private static final long SERVICE_DISCOVERY_TIMEOUT = 10000;

    private TextView txtHexValue;
    private TextView txtAsciivalue, txtNoDeviceFound;
    private TextView txtTimevalue;
    private Button btnSendData;


    private int deviceCount = 0;
    private int currentDevice = 0;

    /**
     * Used to manage connections of the Blue tooth LE Device
     */
    private static BluetoothLeService mBluetoothLeService;
    private Runnable connectRunnable = null;
    private Handler connectionHandler = null;
    private Handler activityFinishHandler = null;
    private Runnable activityFinishRunnable = null;

    private ArrayList<BLEDevice> bleDeviceArrayList = new ArrayList<>();
    private ArrayList<BluetoothDevice> bluetoothDeviceArrayList = new ArrayList<>();
    BLEDevice currentBLEdevice = null;



    /**
     * BroadcastReceiver for receiving the GATT communication status
     */
    private final BroadcastReceiver mGattConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // Status received when connected to GATT Server

            switch (action){
                case BluetoothLeService.ACTION_GATT_CONNECTED :
                    mProgressdialog.setMessage(getString(R.string.alert_message_bluetooth_connect));
                    if (mScanning) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);

                        scanLeDevice(false);
                        mScanning = false;
                    }
                    if (mProgressdialog != null && mProgressdialog.isShowing())
                        mProgressdialog.dismiss();
//                bleDeviceArrayList.clear();
                    if (mConnectTimer != null)
                        mConnectTimer.cancel();
                    mConnectTimerON = false;
                    Toast.makeText(DeviceListAutoConnectActivity.this,
                            R.string.successfully_connected_to_the_device,
                            Toast.LENGTH_SHORT).show();


                    mTimer = showServiceDiscoveryAlert(false);

                    stopActivityFinishHandler();
                      /*
                / Changes the MTU size to 512 in case LOLLIPOP and above devices
                */
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        BluetoothLeService.exchangeGattMtu(512, mDeviceAddress);
                    }

                    break;

                case BluetoothLeService.ACTION_GATT_DISCONNECTED :
                    /**
                     * Disconnect event.When the connect timer is ON,Reconnect the device
                     * else show disconnect message
                     */
                    if (mConnectTimerON) {
                        BluetoothLeService.reconnect(mDeviceAddress);
                    } else {
                        startActivityFinishHandler();
                        Toast.makeText(DeviceListAutoConnectActivity.this,
                                R.string.profile_cannot_connect_message,
                                Toast.LENGTH_SHORT).show();

                    }

                    break;
                case BluetoothLeService.ACTION_MTU_EXCHANGE :
                    Handler delayHandler = new Handler();
                    delayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Logger.e("Discover service called");
                            if (mDeviceAddress.equals(Prefs.getvalue(DeviceListAutoConnectActivity.this, Const.BLE_MAC1, ""))) {
                                if (BluetoothLeService.getConnectionStateBle1() == BluetoothLeService.STATE_CONNECTED)
                                    BluetoothLeService.discoverServicesforBle1();
                            } else if (mDeviceAddress.equals(Prefs.getvalue(DeviceListAutoConnectActivity.this, Const.BLE_MAC2, ""))) {
                                if (BluetoothLeService.getConnectionStateBle2() == BluetoothLeService.STATE_CONNECTED)
                                    BluetoothLeService.discoverServicesforBle2();
                            } else if (mDeviceAddress.equals(Prefs.getvalue(DeviceListAutoConnectActivity.this, Const.BLE_MAC3, ""))) {
                                if (BluetoothLeService.getConnectionStateBle3() == BluetoothLeService.STATE_CONNECTED)
                                    BluetoothLeService.discoverServicesforBle3();
                            } else if (mDeviceAddress.equals(Prefs.getvalue(DeviceListAutoConnectActivity.this, Const.BLE_MAC4, ""))) {
                                if (BluetoothLeService.getConnectionStateBle4() == BluetoothLeService.STATE_CONNECTED)
                                    BluetoothLeService.discoverServicesforBle4();
                            }else if (mDeviceAddress.equals(Prefs.getvalue(DeviceListAutoConnectActivity.this, Const.HW_BLE_MAC, ""))) {
                                if (BluetoothLeService.getmConnectionStateConfiguration() == BluetoothLeService.STATE_CONNECTED)
                                    BluetoothLeService.discoverServicesforConfiguration();
                            }
                        }
                    }, DELAY_PERIOD);
                    break;
            }
//            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
//
//
//
////                updateWithNewFragment();
//            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
//
//            }
        }
    };


    private final BroadcastReceiver mServiceDiscoveryListner = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            switch (action){
                case BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED :
                    Logger.e("Service discovered");
                    if (mTimer != null)
                        mTimer.cancel();

                    if (mProgressdialog != null && mProgressdialog.isShowing())
                        mProgressdialog.dismiss();

                    if(currentBLEdevice != null)
                        currentBLEdevice.setConnected(true);

                    mLeDeviceListAdapter.updateConnectionState(currentBLEdevice);
                    mLeDeviceListAdapter.notifyDataSetChanged();

                    stopActivityFinishHandler();

                    connectionHandler.postDelayed(connectRunnable, 1000);

                    //TODO Check here
//                scanLeDevice(true);
//                sendResult();
                    break;


                case BluetoothLeService.ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL :
                    if (mProgressdialog != null && mProgressdialog.isShowing())
                        mProgressdialog.dismiss();
                    if (mTimer != null)
                        mTimer.cancel();
                    showNoServiceDiscoverAlert();

                    //TODO Check here
//                scanLeDevice(true);
                    break;

            }
        }
    };

    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Getting the intent action and extras
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();

            switch (action){
                case BluetoothLeService.ACTION_DATA_AVAILABLE :
                    if (extras.containsKey(Const.EXTRA_BYTE_VALUE)
                            && extras.containsKey(Const.EXTRA_BLE_DEVICE_ADDRESS)) {

                        byte[] array = intent
                                .getByteArrayExtra(Const.
                                        EXTRA_BYTE_VALUE);
                        String deviceAddress = intent.getStringExtra(Const.
                                EXTRA_BLE_DEVICE_ADDRESS);

                        if (deviceAddress.equals(Prefs.getvalue(DeviceListAutoConnectActivity.this, Const.BLE_MAC1, ""))) {
                            displayASCIIValue2(byteToString(array));
                        } else {
                            displayASCIIValue4(byteToString(array));
                        }
                        displayTimeandDate();
                        displayTimeandDate();

                    }
                    break;
            }


        }

    };


    /**
     * Call back for BLE Scan
     * This call back is called when a BLE device is found near by.
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {
            Activity mActivity = DeviceListAutoConnectActivity.this;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                                    if (device.getAddress().equals(Prefs.getvalue(DeviceListAutoConnectActivity.this, Const.HW_BLE_MAC, ""))) {

                                        BLEDevice bleDevice = new BLEDevice();

                                        bleDevice.setBluetoothDevice(device);
                                        bleDevice.setConnected(false);

                                        mLeDeviceListAdapter.addDevice(bleDevice);
                                        mLeDeviceListAdapter.notifyDataSetChanged();
                                    }

                }
            });

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        setContentView(R.layout.activity_device_list);
        this.setFinishOnTouchOutside(false);
        getWindow().setTitle("");
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mProfileListView = (ListView) findViewById(android.R.id.list);
        txtNoDeviceFound = (TextView) findViewById(android.R.id.empty);
        txtHexValue = (TextView) findViewById(R.id.txtHex);
        txtAsciivalue = (TextView) findViewById(R.id.txtAscci);
        txtTimevalue = (TextView) findViewById(R.id.txtTime);
        btnSendData = (Button) findViewById(R.id.btnSendData);

        btnSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BluetoothLeService.writeCharacteristicGattDb1("Sample Data For 1".getBytes());
                BluetoothLeService.writeCharacteristicGattDb2("Sample Data For 2".getBytes());
                BluetoothLeService.writeCharacteristicGattDb3("Sample Data For 3".getBytes());
                BluetoothLeService.writeCharacteristicGattDb4("Sample Data For 4".getBytes());
            }
        });

        mLeDeviceListAdapter = new LeDeviceListAdapter();
        mProfileListView.setAdapter(mLeDeviceListAdapter);
        mProfileListView.setTextFilterEnabled(true);
        mProgressdialog = new ProgressDialog(this);
        mProgressdialog.setCancelable(false);

        mpdia = new ProgressDialog(this);
        mpdia.setCancelable(false);
        mAlert = new AlertDialog.Builder(this).create();
        mAlert.setMessage(getResources().getString(
                R.string.alert_message_bluetooth_reconnect));
        mAlert.setCancelable(false);
        mAlert.setTitle(getResources().getString(R.string.app_name));
        mAlert.setButton(Dialog.BUTTON_POSITIVE, getResources().getString(
                R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intentActivity = getIntent();
                finish();
                overridePendingTransition(
                        R.anim.slide_left, R.anim.push_left);
                startActivity(intentActivity);
                overridePendingTransition(
                        R.anim.slide_right, R.anim.push_right);
            }
        });
        mAlert.setCanceledOnTouchOutside(false);

        checkBleSupportAndInitialize();

        /**
         * Creating the dataLogger file and
         * updating the datalogger history
         */
        Logger.createDataLoggerFile(this);
        mProfileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mLeDeviceListAdapter.getCount() > 0) {


                    final BluetoothDevice device = mLeDeviceListAdapter
                            .getBLEDevice(position).getBluetoothDevice();
                    if (device != null) {
                        stopActivityFinishHandler();
                        scanLeDevice(false);
                        //TODO Device Coonect
                        connectDevice(device, true);
                    }
                }
            }
        });

        checkBoxes = new ArrayList<>();

        connectionHandler = new Handler();
        connectRunnable = new Runnable() {
            @Override
            public void run() {

                if (mLeDeviceListAdapter.getCount() > 0) {
                    if (deviceCount == 0) {
                        scanLeDevice(false);
                        deviceCount = mLeDeviceListAdapter.getCount();
                    }

                    if (currentDevice < deviceCount) {
                        currentBLEdevice = mLeDeviceListAdapter.getBLEDevice(currentDevice);
                        connectDevice(currentBLEdevice.getBluetoothDevice(), true);
                        currentDevice++;
                        connectionHandler.removeCallbacks(connectRunnable);
                    } else {
                        connectionHandler.removeCallbacks(connectRunnable);
                        Intent intent = new Intent();
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                } else {
                    finish();
                }
            }
        };


        mProfileListView.setEmptyView(txtNoDeviceFound);

        activityFinishHandler = new Handler();
        activityFinishRunnable = new Runnable() {
            @Override
            public void run() {
                DeviceListAutoConnectActivity.this.finish();
            }
        };

    }

    @Override
    protected void initializeWidget() {

    }

    @Override
    protected void bindEvents() {

    }

    @Override
    protected Context getContext() {
        return this;
    }

    @Override
    protected void dialogResponse(int id, boolean res) {

    }

//    private void sendResult(){
//        Intent intent1 = new Intent();
//        intent1.putExtra("ADDRESS",mDeviceAddress);
//        setResult(1,intent1);
//        finish();
//    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.e("Scanning onResume");
        if (checkBluetoothStatus()) {
            prepareList();
        }
        Logger.e("Registering receiver in Profile scannng");
        registerReceiver(mGattConnectReceiver,
                Utils.makeGattConnectIntentFilter());
        registerReceiver(mServiceDiscoveryListner, Utils.makeGattServiceDiscoveryIntentFilter());
        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return super.onKeyUp(keyCode, event);
        else
            return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scanLeDevice(false);
        if (mLeDeviceListAdapter != null)
            mLeDeviceListAdapter.clear();
        if (mLeDeviceListAdapter != null) {
            try {
                mLeDeviceListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        unregisterReceiver(mBondStateReceiver);
        unregisterReceiver(mGattConnectReceiver);
        unregisterReceiver(mServiceDiscoveryListner);
        unregisterReceiver(mGattUpdateReceiver);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable BlueTooth.
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == Activity.RESULT_CANCELED) {
            finish();
        } else {
            // Check which request we're responding to
            if (requestCode == REQUEST_ENABLE_BT) {

                // Make sure the request was successful
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(
                            DeviceListAutoConnectActivity.this,
                            getResources().getString(
                                    R.string.device_bluetooth_on),
                            Toast.LENGTH_SHORT).show();
                    mLeDeviceListAdapter = new LeDeviceListAdapter();
                    mProfileListView.setAdapter(mLeDeviceListAdapter);
//                    scanLeDevice(true);
                    prepareList();
                } else {
                    finish();
                }
            }
        }
    }


    private void checkBleSupportAndInitialize() {
        // Use this check to determine whether BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(DeviceListAutoConnectActivity.this, R.string.device_ble_not_supported,
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        // Initializes a Blue tooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager)
                getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Blue tooth
            Toast.makeText(DeviceListAutoConnectActivity.this,
                    R.string.device_bluetooth_not_supported, Toast.LENGTH_SHORT)
                    .show();
            finish();
        }
    }

    public boolean checkBluetoothStatus() {
        /**
         * Ensures Blue tooth is enabled on the device. If Blue tooth is not
         * currently enabled, fire an intent to display a dialog asking the user
         * to grant permission to enable it.
         */
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }
        return true;
    }

    /**
     * Method to scan BLE Devices. The status of the scan will be detected in
     * the BluetoothAdapter.LeScanCallback
     *
     * @param enable
     */
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            if (!mScanning) {
                startScanTimer();
                mScanning = true;

                mBluetoothAdapter.startLeScan(mLeScanCallback);

            }
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }


    /**
     * Preparing the BLE Devicelist
     */
    public void prepareList() {
        // Initializes ActionBar as required
//        setUpActionBar();
        // Prepare list view and initiate scanning
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        mProfileListView.setAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
        mSearchEnabled = false;
    }

    /**
     * Swipe refresh timer
     */
    public void startScanTimer() {
        mScanTimer = new Timer();
        mScanTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mScanning = false;

                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                scanLeDevice(false);

                if (mLeDeviceListAdapter.getCount() == 0) {
                DeviceListAutoConnectActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },2000) ;

                    }
                });

                } else {
                    connectionHandler.post(connectRunnable);
                }
            }
        }, SCAN_PERIOD_TIMEOUT);
    }


    /**
     * Method to connect to the device selected. The time allotted for having a
     * connection is 8 seconds. After 8 seconds it will disconnect if not
     * connected and initiate scan once more
     *
     * @param device
     */

    private void connectDevice(BluetoothDevice device, boolean isFirstConnect) {
        Logger.v("connectDevice Called...");

        mDeviceAddress = device.getAddress();
        mDeviceName = device.getName();
        // Get the connection status of the device
        if (BluetoothLeService.getConnectionState(this, mDeviceAddress) == BluetoothLeService.STATE_DISCONNECTED) {
            Logger.v("BLE DISCONNECTED STATE");
            // Disconnected,so connect
            BluetoothLeService.connect(DeviceListAutoConnectActivity.this, mDeviceAddress, mDeviceName);
            showConnectAlertMessage(mDeviceName, mDeviceAddress);
        } else {
            Logger.v("BLE OTHER STATE-->" + BluetoothLeService.getConnectionState(this, mDeviceAddress));
            // Connecting to some devices,so disconnect and then connect
            BluetoothLeService.disconnect(mDeviceAddress);

            Handler delayHandler = new Handler();
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    BluetoothLeService.connect(DeviceListAutoConnectActivity.this, mDeviceAddress, mDeviceName);
                    showConnectAlertMessage(mDeviceName, mDeviceAddress);
                }
            }, DELAY_PERIOD);

        }
        if (isFirstConnect) {
            startConnectTimer();
            mConnectTimerON = true;
        }

    }

    private void showConnectAlertMessage(String devicename, String deviceaddress) {
        mProgressdialog.setTitle(getResources().getString(
                R.string.alert_message_connect_title));
        mProgressdialog.setMessage(getResources().getString(
                R.string.alert_message_connect)
                + "\n"
                + devicename
                + "\n"
                + deviceaddress
                + "\n"
                + getResources().getString(R.string.alert_message_wait));

        if (!DeviceListAutoConnectActivity.this.isDestroyed() && mProgressdialog != null) {
            mProgressdialog.show();
        }
    }

    /**
     * Connect Timer
     */
    private void startConnectTimer() {
        mConnectTimer = new Timer();
        mConnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mProgressdialog != null && mProgressdialog.isShowing()
                        && !isActivityFinish())
                    mProgressdialog.dismiss();
                Logger.v("CONNECTION TIME OUT");
                mConnectTimerON = false;

//                if(mDeviceAddress.equals(BluetoothLeService.BLE_MAC2))
//                    BluetoothLeService.disconnectBle2();
//                else
//                    BluetoothLeService.disconnectBle4();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DeviceListAutoConnectActivity.this,
                                R.string.profile_cannot_connect_message,
                                Toast.LENGTH_SHORT).show();
                        if (mLeDeviceListAdapter != null)
//                                mLeDeviceListAdapter.clear();
                            if (mLeDeviceListAdapter != null) {
                                try {
                                    mLeDeviceListAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        //TODO Check here
//                            scanLeDevice(true);
                        mScanning = true;
                    }
                });

            }
        }, CONNECTION_TIMEOUT);
    }

    private Timer showServiceDiscoveryAlert(boolean isReconnect) {
        mProgressdialog.setTitle(getString(R.string.progress_tile_service_discovering));
        if (!isReconnect) {
            mProgressdialog.setMessage(getString(R.string.progress_message_service_discovering));
        } else {
            mProgressdialog.setMessage(getString(R.string.progress_message_reconnect));
        }
        mProgressdialog.setIndeterminate(true);
        mProgressdialog.setCancelable(false);
        mProgressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressdialog.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mProgressdialog != null && mProgressdialog.isShowing()
                        && !isActivityFinish()) {
                        mProgressdialog.dismiss();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showNoServiceDiscoverAlert();
                            startActivityFinishHandler();
                        }
                    });
                }

            }
        }, SERVICE_DISCOVERY_TIMEOUT);
        return timer;
    }

    private String byteToString(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (byte byteChar : array) {
            sb.append(String.format("%02x", byteChar));
        }

        return sb.toString();
    }


    /**
     * Method to convert the hexvalue to ascii value and displaying to the user
     *
     * @param hexValue
     */
    void displayASCIIValue2(String hexValue) {
        txtAsciivalue.setText("");
        StringBuilder output = new StringBuilder("");
        try {
            for (int i = 0; i < hexValue.length(); i += 2) {
                String str = hexValue.substring(i, i + 2);
                output.append((char) Integer.parseInt(str, 16));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        txtAsciivalue.setText(output.toString());
    }

    void displayASCIIValue4(String hexValue) {
        txtHexValue.setText("");
        StringBuilder output = new StringBuilder("");
        try {
            for (int i = 0; i < hexValue.length(); i += 2) {
                String str = hexValue.substring(i, i + 2);
                output.append((char) Integer.parseInt(str, 16));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        txtHexValue.setText(output.toString());
    }

    /**
     * Method to display time and date
     */
    private void displayTimeandDate() {
        txtTimevalue.setText(Utils.GetTimeFromMilliseconds());
    }


    private void showNoServiceDiscoverAlert() {
        Toast.makeText(this, "No Service Discovered", Toast.LENGTH_SHORT).show();
    }

    /**
     * Holder class for the list view view widgets
     */
    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        CheckBox chkStatus;
    }

    /**
     * List Adapter for holding devices found through scanning.
     */
    private class LeDeviceListAdapter extends BaseAdapter implements Filterable {

        ArrayList<BLEDevice> mFilteredDevices = new ArrayList<>();
        private LayoutInflater mInflator;
        private int rssiValue;
        private ItemFilter mFilter = new ItemFilter();

        public LeDeviceListAdapter() {
            super();
            mInflator = getLayoutInflater();
        }

        private void addDevice(BLEDevice device) {

            if (!bluetoothDeviceArrayList.contains(device.getBluetoothDevice())) {
                bluetoothDeviceArrayList.add(device.getBluetoothDevice());
                bleDeviceArrayList.add(device);
            }
        }


        private void updateConnectionState(BLEDevice device) {
            for (int i = 0; i < bleDeviceArrayList.size(); i++) {
                BLEDevice bleDevice = bleDeviceArrayList.get(i);
                if (bleDevice.getBluetoothDevice().getAddress().equals(device.getBluetoothDevice().getAddress())) {
                    bleDeviceArrayList.set(i, device);
                }
            }
        }

        /**
         * Getter method to get the blue tooth device
         *
         * @param position
         * @return BluetoothDevice
         */
        public BLEDevice getBLEDevice(int position) {
            return bleDeviceArrayList.get(position);
        }

        /**
         * Clearing all values in the device array list
         */
        public void clear() {
            bleDeviceArrayList.clear();
        }

        @Override
        public int getCount() {
            return bleDeviceArrayList.size();
        }


        @Override
        public Object getItem(int i) {
            return bleDeviceArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {

            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_autoconnect_device, viewGroup,
                        false);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view
                        .findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view
                        .findViewById(R.id.device_name);
                viewHolder.chkStatus = (CheckBox) view.
                        findViewById(R.id.chk_connectindicator);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();

                checkBoxes.add((CheckBox) viewHolder.deviceAddress.getTag());
            }

            /**
             * Setting the name and the RSSI of the BluetoothDevice. provided it
             * is a valid one
             */
            final BLEDevice device = bleDeviceArrayList.get(position);
            final String deviceName = device.getBluetoothDevice().getName();
            if (deviceName != null && deviceName.length() > 0) {
                try {
                    viewHolder.deviceName.setText(deviceName);
                    viewHolder.deviceAddress.setText(device.getBluetoothDevice().getAddress());

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                viewHolder.deviceName.setText(R.string.device_unknown);
                viewHolder.deviceName.setSelected(true);
                viewHolder.deviceAddress.setText(device.getBluetoothDevice().getAddress());
            }

            if (bleDeviceArrayList.get(position).isConnected())
                viewHolder.chkStatus.setChecked(true);
            else
                viewHolder.chkStatus.setChecked(false);


            return view;
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String mFilterString = constraint.toString().toLowerCase();

                FilterResults mResults = new FilterResults();

                final ArrayList<BLEDevice> list = bleDeviceArrayList;

                int count = list.size();
                final ArrayList<BluetoothDevice> nlist = new ArrayList<BluetoothDevice>(count);

                for (int i = 0; i < count; i++) {
                    if (list.get(i).getBluetoothDevice().getName() != null && list.get(i).getBluetoothDevice().getName().toLowerCase().contains(mFilterString)) {
                        nlist.add(list.get(i).getBluetoothDevice());
                    }
                }

                mResults.values = nlist;
                mResults.count = nlist.size();
                return mResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredDevices = (ArrayList<BLEDevice>) results.values;
                clear();
                int count = mFilteredDevices.size();
                for (int i = 0; i < count; i++) {
                    BLEDevice mDevice = mFilteredDevices.get(i);
                    mLeDeviceListAdapter.addDevice(mFilteredDevices.get(i));
                    notifyDataSetChanged(); // notifies the data with new filtered values
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

//        StringBuilder sb = new StringBuilder();
//        sb.append("");
//        if (BluetoothLeService.getConnectionStateBle1() == BluetoothLeService.STATE_CONNECTED ||
//                BluetoothLeService.getConnectionStateBle1() == BluetoothLeService.STATE_CONNECTING ||
//                BluetoothLeService.getConnectionStateBle1() == BluetoothLeService.STATE_DISCONNECTING) {
//            BluetoothLeService.disconnectBle1();
//            sb.append("1");
//        }
//
//        if (BluetoothLeService.getConnectionStateBle2() == BluetoothLeService.STATE_CONNECTED ||
//                BluetoothLeService.getConnectionStateBle2() == BluetoothLeService.STATE_CONNECTING ||
//                BluetoothLeService.getConnectionStateBle2() == BluetoothLeService.STATE_DISCONNECTING) {
//            BluetoothLeService.disconnectBle2();
//            sb.append("2");
//
//        }
//        if (BluetoothLeService.getConnectionStateBle3() == BluetoothLeService.STATE_CONNECTED ||
//                BluetoothLeService.getConnectionStateBle3() == BluetoothLeService.STATE_CONNECTING ||
//                BluetoothLeService.getConnectionStateBle3() == BluetoothLeService.STATE_DISCONNECTING) {
//            BluetoothLeService.disconnectBle2();
//            sb.append("3");
//
//        }
//
//        if (BluetoothLeService.getConnectionStateBle4() == BluetoothLeService.STATE_CONNECTED ||
//                BluetoothLeService.getConnectionStateBle4() == BluetoothLeService.STATE_CONNECTING ||
//                BluetoothLeService.getConnectionStateBle4() == BluetoothLeService.STATE_DISCONNECTING) {
//            BluetoothLeService.disconnectBle4();
//            sb.append("4");
//
//        }

//        if(!sb.toString().equals(""))
//            Toast.makeText(this, getResources().getString(R.string.alert_message_bluetooth_disconnect)+ sb.toString(), Toast.LENGTH_SHORT).show();

//        super.onBackPressed();
    }

    private void startActivityFinishHandler(){
        if(activityFinishHandler != null && activityFinishRunnable != null)
            activityFinishHandler.postDelayed(activityFinishRunnable,5000);
    }
    private void stopActivityFinishHandler(){
        if(activityFinishHandler != null && activityFinishRunnable != null)
            activityFinishHandler.removeCallbacks(activityFinishRunnable);
    }

}
